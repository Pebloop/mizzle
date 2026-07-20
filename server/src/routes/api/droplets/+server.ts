import { json, type RequestHandler } from '@sveltejs/kit';
import { getDb } from '$lib/server/db';
import { droplet } from '$lib/server/db/schema';
import { createStorage } from '$lib/server/storage';

export const GET: RequestHandler = async ({ platform }) => {
	try {
		if (platform?.env?.DB) {
			const db = getDb(platform.env.DB);
			const list = await db.select().from(droplet);
			return json({ droplets: list });
		}

		if (platform?.env?.BUCKET) {
			const storage = createStorage(platform.env.BUCKET);
			const objects = await storage.list({ prefix: 'droplets/' });
			return json({ droplets: objects.objects });
		}

		return json({ droplets: [] });
	} catch (err) {
		const errorMessage = err instanceof Error ? err.message : 'Failed to list droplets';
		return json({ error: errorMessage }, { status: 500 });
	}
};
