/**
 * Cloudflare R2 Storage API Service Helper
 */

export interface PutOptions extends R2PutOptions {
	contentType?: string;
}

export class R2StorageService {
	constructor(private readonly bucket: R2Bucket) {}

	/**
	 * Retrieves an object from R2 with its body readable stream.
	 */
	async get(key: string, options?: R2GetOptions): Promise<R2ObjectBody | null> {
		const object = await this.bucket.get(key, options);
		if (!object || !('body' in object)) {
			return null;
		}
		return object as R2ObjectBody;
	}

	/**
	 * Retrieves metadata for an object without downloading its body.
	 */
	async head(key: string): Promise<R2Object | null> {
		return await this.bucket.head(key);
	}

	/**
	 * Checks if an object exists in R2.
	 */
	async exists(key: string): Promise<boolean> {
		const object = await this.head(key);
		return object !== null;
	}

	/**
	 * Reads an object from R2 as a UTF-8 text string.
	 */
	async getText(key: string, options?: R2GetOptions): Promise<string | null> {
		const object = await this.get(key, options);
		if (!object) return null;
		return await object.text();
	}

	/**
	 * Reads an object from R2 and parses it as JSON.
	 */
	async getJson<T = unknown>(key: string, options?: R2GetOptions): Promise<T | null> {
		const text = await this.getText(key, options);
		if (text === null) return null;
		return JSON.parse(text) as T;
	}

	/**
	 * Reads an object from R2 as an ArrayBuffer.
	 */
	async getBytes(key: string, options?: R2GetOptions): Promise<ArrayBuffer | null> {
		const object = await this.get(key, options);
		if (!object) return null;
		return await object.arrayBuffer();
	}

	/**
	 * Uploads or overwrites an object in R2.
	 */
	async put(
		key: string,
		value: ReadableStream | ArrayBuffer | ArrayBufferView | string | Blob,
		options?: PutOptions
	): Promise<R2Object> {
		const httpMetadata: R2HTTPMetadata = {
			...options?.httpMetadata,
			...(options?.contentType ? { contentType: options.contentType } : {})
		};

		return await this.bucket.put(key, value, {
			...options,
			httpMetadata
		});
	}

	/**
	 * Serializes a JavaScript object to JSON and uploads it to R2.
	 */
	async putJson(key: string, value: unknown, options?: PutOptions): Promise<R2Object> {
		const jsonString = JSON.stringify(value);
		return await this.put(key, jsonString, {
			contentType: 'application/json',
			...options
		});
	}

	/**
	 * Deletes one or multiple object keys from R2.
	 */
	async delete(keys: string | string[]): Promise<void> {
		await this.bucket.delete(keys);
	}

	/**
	 * Lists objects in the bucket according to prefix, limit, cursor, etc.
	 */
	async list(options?: R2ListOptions): Promise<R2Objects> {
		return await this.bucket.list(options);
	}
}

/**
 * Creates an instance of R2StorageService from a Cloudflare R2Bucket binding.
 */
export function createStorage(bucket: R2Bucket): R2StorageService {
	return new R2StorageService(bucket);
}
