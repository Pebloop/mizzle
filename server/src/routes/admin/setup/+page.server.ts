import { fail, redirect } from '@sveltejs/kit';
import { hasAnyAdmin, registerFirstAdmin } from '$lib/server/admin';
import type { PageServerLoad, Actions } from './$types';

export const load: PageServerLoad = async ({ platform }) => {
	if (!platform?.env?.DB) {
		throw new Error('Database binding not found');
	}

	const adminExists = await hasAnyAdmin(platform.env.DB);

	if (adminExists) {
		throw redirect(303, '/admin/login');
	}

	return {};
};

export const actions: Actions = {
	default: async ({ request, platform, locals }) => {
		if (!platform?.env?.DB) {
			return fail(500, { error: 'Database binding not found' });
		}

		const formData = await request.formData();
		const name = formData.get('name')?.toString().trim();
		const email = formData.get('email')?.toString().trim();
		const password = formData.get('password')?.toString();

		if (!name || !email || !password) {
			return fail(400, { error: 'All fields are required.' });
		}

		if (password.length < 8) {
			return fail(400, { error: 'Password must be at least 8 characters long.' });
		}

		try {
			await registerFirstAdmin(platform.env.DB, locals.auth, {
				name,
				email,
				password
			});
		} catch (err: any) {
			return fail(400, { error: err.message || 'Registration failed.' });
		}

		throw redirect(303, '/admin');
	}
};
