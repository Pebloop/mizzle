import { fail, redirect } from '@sveltejs/kit';
import { hasAnyAdmin } from '$lib/server/admin';
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
	if (locals.user && userRole === 'admin') {
		throw redirect(303, '/admin');
	}

	return {};
};

export const actions: Actions = {
	default: async ({ request, locals }) => {
		const formData = await request.formData();
		const email = formData.get('email')?.toString().trim();
		const password = formData.get('password')?.toString();

		if (!email || !password) {
			return fail(400, { error: 'Email and password are required.' });
		}

		try {
			const res = await locals.auth.api.signInEmail({
				body: {
					email,
					password
				}
			});

			if (!res || !res.user) {
				return fail(400, { error: 'Invalid email or password.' });
			}

			const userRole = (res.user as { role?: string }).role;
			if (userRole !== 'admin') {
				// Sign out if non-admin user tries to log into admin console
				await locals.auth.api.signOut({
					headers: request.headers
				});
				return fail(403, { error: 'Access denied. Admin privileges required.' });
			}
		} catch (err: any) {
			return fail(400, { error: err.message || 'Login failed.' });
		}

		throw redirect(303, '/admin');
	}
};
