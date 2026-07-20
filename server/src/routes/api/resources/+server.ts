import { json, type RequestHandler } from '@sveltejs/kit';
import { getDb } from '$lib/server/db';
import { resource } from '$lib/server/db/schema';
import { createStorage } from '$lib/server/storage';
import { eq } from 'drizzle-orm';

export const GET: RequestHandler = async ({ url, platform }) => {
	try {
		const typeParam = url.searchParams.get('type');

		if (platform?.env?.DB) {
			const db = getDb(platform.env.DB);
			
			const list = typeParam
				? await db.select().from(resource).where(eq(resource.type, typeParam))
				: await db.select().from(resource);

			const formatted = list.map((res) => ({
				...res,
				downloadUrl: `/api/resources/${res.id}/download`
			}));

			return json({ resources: formatted });
		}

		if (platform?.env?.BUCKET) {
			const storage = createStorage(platform.env.BUCKET);
			const objects = await storage.list({ prefix: 'resources/' });
			const formatted = objects.objects.map((obj) => ({
				id: obj.key.replace('resources/', ''),
				name: obj.key,
				type: 'file',
				r2Key: obj.key,
				downloadUrl: `/api/resources/${obj.key}/download`
			}));
			return json({ resources: formatted });
		}

		return json({ resources: [] });
	} catch (err) {
		const errorMessage = err instanceof Error ? err.message : 'Failed to list resources';
		return json({ error: errorMessage }, { status: 500 });
	}
};
