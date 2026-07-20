import { json, type RequestHandler } from '@sveltejs/kit';
import { getDb } from '$lib/server/db';
import { resource } from '$lib/server/db/schema';
import { createStorage } from '$lib/server/storage';
import { eq } from 'drizzle-orm';

// Resource binary download endpoint


export const GET: RequestHandler = async ({ params, platform }) => {
	if (!platform?.env?.BUCKET) {
		return json({ error: 'Storage bucket not configured' }, { status: 500 });
	}

	const resourceId = params.id;
	if (!resourceId) {
		return json({ error: 'Resource ID is required' }, { status: 400 });
	}

	let r2Key: string | null = null;
	let mimeType = 'application/octet-stream';

	if (platform.env.DB) {
		const db = getDb(platform.env.DB);
		const [found] = await db.select().from(resource).where(eq(resource.id, resourceId)).limit(1);
		if (found) {
			r2Key = found.r2Key;
			if (found.mimeType) {
				mimeType = found.mimeType;
			}
		}
	}

	// Fallback to using resourceId directly as key if not found in DB or DB not bound
	if (!r2Key) {
		r2Key = resourceId.startsWith('resources/') ? resourceId : `resources/${resourceId}`;
	}

	const storage = createStorage(platform.env.BUCKET);
	const object = await storage.get(r2Key);

	if (!object || !object.body) {
		return new Response('Resource file not found', { status: 404 });
	}

	const headers = new Headers();
	if (object.httpEtag) {
		headers.set('etag', object.httpEtag);
	}
	if (object.httpMetadata?.contentType) {
		headers.set('content-type', object.httpMetadata.contentType);
	}
	if (object.httpMetadata?.contentDisposition) {
		headers.set('content-disposition', object.httpMetadata.contentDisposition);
	}
	if (object.httpMetadata?.cacheControl) {
		headers.set('cache-control', object.httpMetadata.cacheControl);
	}
	if (mimeType && !headers.get('content-type')) {
		headers.set('content-type', mimeType);
	}

	return new Response(object.body as ReadableStream, {
		headers
	});
};
