import { json, type RequestHandler } from '@sveltejs/kit';
import { createStorage } from '$lib/server/storage';
import { getDb } from '$lib/server/db';
import { droplet } from '$lib/server/db/schema';
import { eq } from 'drizzle-orm';

export const POST: RequestHandler = async ({ request, platform, locals }) => {
	if (!platform?.env?.BUCKET) {
		return json({ error: 'R2 Storage binding BUCKET not found' }, { status: 500 });
	}

	try {
		const body = await request.json();
		const { id, name, data } = body as { id?: string; name?: string; data?: unknown };

		if (!id || !name) {
			return json({ error: 'Missing required droplet fields (id, name)' }, { status: 400 });
		}

		const storage = createStorage(platform.env.BUCKET);
		const r2Key = `droplets/${id}.json`;

		const dropletPayload = {
			id,
			name,
			data: data ?? body,
			uploadedAt: new Date().toISOString(),
			uploadedBy: locals.user?.id ?? 'anonymous'
		};

		// 1. Save droplet content to Cloudflare R2
		await storage.putJson(r2Key, dropletPayload);

		// 2. Save/Update droplet record in D1 Database (if DB binding exists)
		if (platform.env.DB) {
			const db = getDb(platform.env.DB);
			const existing = await db.select().from(droplet).where(eq(droplet.id, id)).limit(1);

			if (existing.length > 0) {
				await db.update(droplet).set({
					name,
					userId: locals.user?.id ?? null,
					r2Key
				}).where(eq(droplet.id, id));
			} else {
				await db.insert(droplet).values({
					id,
					name,
					userId: locals.user?.id ?? null,
					r2Key
				});
			}
		}

		return json({
			success: true,
			id,
			name,
			r2Key,
			message: `Droplet '${name}' successfully stored on server`
		});
	} catch (err) {
		const errorMessage = err instanceof Error ? err.message : 'Upload failed';
		return json({ error: errorMessage }, { status: 500 });
	}
};
