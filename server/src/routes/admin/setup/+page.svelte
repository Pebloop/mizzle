<script lang="ts">
	import { enhance } from '$app/forms';
	let { form } = $props();
</script>

<div class="min-h-screen bg-slate-900 text-white flex items-center justify-center p-4">
	<div class="w-full max-w-md bg-slate-800 rounded-xl shadow-2xl p-8 border border-slate-700">
		<div class="text-center mb-8">
			<div class="inline-flex items-center justify-center w-16 h-16 rounded-full bg-blue-600/20 text-blue-400 mb-4 border border-blue-500/30">
				<svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
					<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path>
				</svg>
			</div>
			<h1 class="text-2xl font-bold tracking-tight text-slate-100">First-Time Setup</h1>
			<p class="text-slate-400 text-sm mt-2">
				No administrator exists yet. Create the initial admin account below.
			</p>
		</div>

		{#if form?.error}
			<div class="mb-6 p-4 rounded-lg bg-red-500/10 border border-red-500/30 text-red-400 text-sm">
				{form.error}
			</div>
		{/if}

		<form method="POST" use:enhance class="space-y-5">
			<div>
				<label for="name" class="block text-sm font-medium text-slate-300 mb-2">Admin Name</label>
				<input
					type="text"
					id="name"
					name="name"
					required
					placeholder="Administrator"
					class="w-full px-4 py-2.5 rounded-lg bg-slate-900 border border-slate-700 text-slate-100 placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
				/>
			</div>

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
				<p class="text-xs text-slate-500 mt-1">Must be at least 8 characters long.</p>
			</div>

			<button
				type="submit"
				class="w-full py-3 px-4 rounded-lg bg-blue-600 hover:bg-blue-500 text-white font-medium shadow-lg shadow-blue-600/20 transition duration-150 ease-in-out focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 focus:ring-offset-slate-800"
			>
				Create Administrator Account
			</button>
		</form>
	</div>
</div>
