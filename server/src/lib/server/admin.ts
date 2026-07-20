import { eq } from 'drizzle-orm';
import { getDb } from '$lib/server/db';
import { user } from '$lib/server/db/schema';
import type { createAuth } from '$lib/server/auth';

/**
 * Checks whether any admin user is currently registered in the database.
 */
export async function hasAnyAdmin(d1: D1Database): Promise<boolean> {
	try {
		const db = getDb(d1);
		const [existingAdmin] = await db
			.select({ id: user.id })
			.from(user)
			.where(eq(user.role, 'admin'))
			.limit(1);

		return !!existingAdmin;
	} catch (err) {
		return false;
	}
}

/**
 * Registers the initial admin account for first-time setup.
 * Throws an error if an admin account already exists.
 */
export async function registerFirstAdmin(
	d1: D1Database,
	auth: ReturnType<typeof createAuth>,
	data: { name: string; email: string; password: string }
) {
	const adminExists = await hasAnyAdmin(d1);
	if (adminExists) {
		throw new Error('An admin account has already been registered.');
	}

	const res = await auth.api.signUpEmail({
		body: {
			name: data.name,
			email: data.email,
			password: data.password
		}
	});

	if (!res || !res.user) {
		throw new Error('Failed to create account.');
	}

	const db = getDb(d1);
	await db.update(user).set({ role: 'admin' }).where(eq(user.id, res.user.id));

	return res.user;
}
