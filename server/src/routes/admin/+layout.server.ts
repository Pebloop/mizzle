import { hasAnyAdmin } from '$lib/server/admin';
import type { LayoutServerLoad } from './$types';

export const load: LayoutServerLoad = async ({ platform, locals }) => {
	if (!platform?.env?.DB) {
		throw new Error('Database binding not found');
	}

	const adminExists = await hasAnyAdmin(platform.env.DB);

	return {
		hasAdmin: adminExists,
		user: locals.user,
		session: locals.session
	};
};
