import { json, type RequestHandler } from '@sveltejs/kit';
import { createStorage } from '$lib/server/storage';
import { getDb } from '$lib/server/db';
import { resource } from '$lib/server/db/schema';
import { eq } from 'drizzle-orm';

export const POST: RequestHandler = async ({ request, platform, locals }) => {
	if (!platform?.env?.BUCKET) {
		return json({ error: 'R2 Storage binding BUCKET not found' }, { status: 500 });
	}

	try {
		const contentType = request.headers.get('content-type') || '';
		let id = crypto.randomUUID();
		let name = '';
		let type = 'texture';
		let mimeType = 'image/png';
		let regionX = 0;
		let regionY = 0;
		let regionWidth = -1;
		let regionHeight = -1;
		let fileBody: ArrayBuffer | Uint8Array | Blob | null = null;
		let fileName = 'resource.bin';

		if (contentType.includes('multipart/form-data')) {
			const formData = await request.formData();
			name = (formData.get('name') as string) || 'Untitled Resource';
			type = (formData.get('type') as string) || 'texture';
			regionX = parseInt((formData.get('regionX') as string) || '0', 10);
			regionY = parseInt((formData.get('regionY') as string) || '0', 10);
			regionWidth = parseInt((formData.get('regionWidth') as string) || '-1', 10);
			regionHeight = parseInt((formData.get('regionHeight') as string) || '-1', 10);

			const file = formData.get('file');
			if (file && file instanceof File) {
				fileName = file.name;
				mimeType = file.type || (type === 'audio' ? 'audio/mpeg' : 'image/png');
				fileBody = await file.arrayBuffer();
			} else {
				return json({ error: 'Missing file in form data' }, { status: 400 });
			}
		} else if (contentType.includes('application/json')) {
			const body = (await request.json()) as {
				name?: string;
				type?: string;
				mimeType?: string;
				regionX?: number;
				regionY?: number;
				regionWidth?: number;
				regionHeight?: number;
				base64Data?: string;
			};
			name = body.name || 'Untitled Resource';
			type = body.type || 'texture';
			mimeType = body.mimeType || (type === 'audio' ? 'audio/mpeg' : 'image/png');
			regionX = body.regionX ?? 0;
			regionY = body.regionY ?? 0;
			regionWidth = body.regionWidth ?? -1;
			regionHeight = body.regionHeight ?? -1;

			if (body.base64Data) {
				const binaryStr = atob(body.base64Data);
				const bytes = new Uint8Array(binaryStr.length);
				for (let i = 0; i < binaryStr.length; i++) {
					bytes[i] = binaryStr.charCodeAt(i);
				}
				fileBody = bytes;
			} else {
				return json({ error: 'Missing base64Data in json body' }, { status: 400 });
			}
		} else {
			return json({ error: 'Unsupported Content-Type. Use multipart/form-data or application/json' }, { status: 400 });
		}

		const r2Key = `resources/${id}-${fileName}`;
		const storage = createStorage(platform.env.BUCKET);

		await storage.put(r2Key, fileBody, {
			contentType: mimeType
		});

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

		return json({
			success: true,
			resource: {
				id,
				name,
				type,
				r2Key,
				mimeType,
				regionX,
				regionY,
				regionWidth,
				regionHeight,
				downloadUrl: `/api/resources/${id}/download`
			}
		});
	} catch (err) {
		const errorMessage = err instanceof Error ? err.message : 'Upload failed';
		return json({ error: errorMessage }, { status: 500 });
	}
};
