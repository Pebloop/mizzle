<script lang="ts">
	import { enhance } from '$app/forms';
	let { form } = $props();
</script>

<div class="min-h-screen bg-slate-900 text-white flex items-center justify-center p-4">
	<div class="w-full max-w-md bg-slate-800 rounded-xl shadow-2xl p-8 border border-slate-700">
		<div class="text-center mb-8">
			<div class="inline-flex items-center justify-center w-16 h-16 rounded-full bg-slate-700 text-blue-400 mb-4 border border-slate-600">
				<svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
					<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"></path>
				</svg>
			</div>
			<h1 class="text-2xl font-bold tracking-tight text-slate-100">Admin Console</h1>
			<p class="text-slate-400 text-sm mt-2">Sign in to your administrator account</p>
		</div>

		{#if form?.error}
			<div class="mb-6 p-4 rounded-lg bg-red-500/10 border border-red-500/30 text-red-400 text-sm">
				{form.error}
			</div>
		{/if}

		<form method="POST" use:enhance class="space-y-5">
			<div>
				<label for="email" class="block text-sm font-medium text-slate-300 mb-2">Email Address</label>
				<input
					type="email"
					id="email"
					name="email"
					required
					placeholder="admin@example.com"
					class="w-full px-4 py-2.5 rounded-lg bg-slate-900 border border-slate-700 text-slate-100 placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
				/>
			</div>

			<div>
				<label for="password" class="block text-sm font-medium text-slate-300 mb-2">Password</label>
				<input
					type="password"
					id="password"
					name="password"
					required
					placeholder="••••••••••••"
					class="w-full px-4 py-2.5 rounded-lg bg-slate-900 border border-slate-700 text-slate-100 placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
				/>
			</div>

			<button
				type="submit"
				class="w-full py-3 px-4 rounded-lg bg-blue-600 hover:bg-blue-500 text-white font-medium shadow-lg shadow-blue-600/20 transition duration-150 ease-in-out focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 focus:ring-offset-slate-800"
			>
				Sign In as Admin
			</button>
		</form>
	</div>
</div>
