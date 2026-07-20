import { fail, redirect } from '@sveltejs/kit';
import { hasAnyAdmin } from '$lib/server/admin';
import { getDb } from '$lib/server/db';
import { user, droplet, resource } from '$lib/server/db/schema';
import { createStorage } from '$lib/server/storage';
import { eq } from 'drizzle-orm';
import type { PageServerLoad, Actions } from './$types';

export const load: PageServerLoad = async ({ platform, locals }) => {
	if (!platform?.env?.DB) {
		throw new Error('Database binding not found');
	}

	const adminExists = await hasAnyAdmin(platform.env.DB);

	if (!adminExists) {
		throw redirect(303, '/admin/setup');
	}

	const userRole = (locals.user as { role?: string } | undefined)?.role;
	if (!locals.user || userRole !== 'admin') {
		throw redirect(303, '/admin/login');
	}

	const db = getDb(platform.env.DB);
	let usersList: Array<{ id: string; name: string; email: string; role: string | null; createdAt: Date }> = [];
	try {
		usersList = await db
			.select({
				id: user.id,
				name: user.name,
				email: user.email,
				role: user.role,
				createdAt: user.createdAt
			})
			.from(user);
	} catch (err) {
		console.error('Failed to fetch users:', err);
	}

	let dropletsList: Array<{ id: string; name: string; userId: string | null; userName: string | null; r2Key: string; createdAt: Date }> = [];
	try {
		const rawDroplets = await db
			.select({
				id: droplet.id,
				name: droplet.name,
				userId: droplet.userId,
				userName: user.name,
				r2Key: droplet.r2Key,
				createdAt: droplet.createdAt
			})
			.from(droplet)
			.leftJoin(user, eq(droplet.userId, user.id));

		dropletsList = rawDroplets;
	} catch (err) {
		console.error('Failed to fetch droplets:', err);
	}

	let resourcesList: Array<{
		id: string;
		name: string;
		type: string;
		r2Key: string;
		mimeType: string | null;
		regionX: number;
		regionY: number;
		regionWidth: number;
		regionHeight: number;
		userId: string | null;
		userName: string | null;
		createdAt: Date;
	}> = [];
	try {
		const rawResources = await db
			.select({
				id: resource.id,
				name: resource.name,
				type: resource.type,
				r2Key: resource.r2Key,
				mimeType: resource.mimeType,
				regionX: resource.regionX,
				regionY: resource.regionY,
				regionWidth: resource.regionWidth,
				regionHeight: resource.regionHeight,
				userId: resource.userId,
				userName: user.name,
				createdAt: resource.createdAt
			})
			.from(resource)
			.leftJoin(user, eq(resource.userId, user.id));

		resourcesList = rawResources;
	} catch (err) {
		console.error('Failed to fetch resources:', err);
	}

	return {
		user: locals.user,
		users: usersList,
		droplets: dropletsList,
		resources: resourcesList
	};
};

export const actions: Actions = {
	logout: async ({ request, locals }) => {
		if (locals.auth) {
			await locals.auth.api.signOut({
				headers: request.headers
			});
		}
		throw redirect(303, '/admin/login');
	},

	uploadResource: async ({ request, platform, locals }) => {
		if (!platform?.env?.BUCKET) {
			return fail(500, { error: 'R2 Storage binding not found' });
		}

		try {
			const formData = await request.formData();
			const name = (formData.get('name') as string)?.trim();
			const type = (formData.get('type') as string) || 'texture';
			const regionX = parseInt((formData.get('regionX') as string) || '0', 10);
			const regionY = parseInt((formData.get('regionY') as string) || '0', 10);
			const regionWidth = parseInt((formData.get('regionWidth') as string) || '-1', 10);
			const regionHeight = parseInt((formData.get('regionHeight') as string) || '-1', 10);

			const file = formData.get('file');
			if (!name) {
				return fail(400, { error: 'Resource name is required' });
			}

			if (!file || !(file instanceof File) || file.size === 0) {
				return fail(400, { error: 'A valid file must be provided' });
			}

			const id = crypto.randomUUID();
			const fileName = file.name || 'resource.bin';
			const mimeType = file.type || (type === 'audio' ? 'audio/mpeg' : 'image/png');
			const r2Key = `resources/${id}-${fileName}`;

			const storage = createStorage(platform.env.BUCKET);
			const arrayBuffer = await file.arrayBuffer();

			await storage.put(r2Key, arrayBuffer, { contentType: mimeType });

			if (platform.env.DB) {
				const db = getDb(platform.env.DB);
				await db.insert(resource).values({
					id,
					name,
					type,
					r2Key,
					mimeType,
					regionX,
					regionY,
					regionWidth,
					regionHeight,
					userId: locals.user?.id ?? null
				});
			}

			return { success: true, message: `Resource '${name}' uploaded successfully.` };
		} catch (err) {
			const errorMsg = err instanceof Error ? err.message : 'Resource upload failed';
			return fail(500, { error: errorMsg });
		}
	},

	deleteResource: async ({ request, platform }) => {
		try {
			const formData = await request.formData();
			const id = formData.get('id') as string;

			if (!id) {
				return fail(400, { error: 'Resource ID is required' });
			}

			if (platform?.env?.DB) {
				const db = getDb(platform.env.DB);
				const [found] = await db.select().from(resource).where(eq(resource.id, id)).limit(1);

				if (found) {
					if (platform.env.BUCKET) {
						const storage = createStorage(platform.env.BUCKET);
						await storage.delete(found.r2Key);
					}
					await db.delete(resource).where(eq(resource.id, id));
				}
			}

			return { success: true, message: 'Resource deleted successfully.' };
		} catch (err) {
			const errorMsg = err instanceof Error ? err.message : 'Delete resource failed';
			return fail(500, { error: errorMsg });
		}
	}
};
