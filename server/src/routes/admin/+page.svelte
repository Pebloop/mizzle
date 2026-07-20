<script lang="ts">
	import { enhance } from '$app/forms';
	let { data, form } = $props();

	let showUsers = $state(true);
	let showDroplets = $state(true);
	let showResources = $state(true);
	let showUploadModal = $state(false);

	let resourceType = $state<'texture' | 'audio'>('texture');

	let textureCount = $derived(
		data.resources?.filter((r: { type: string }) => r.type === 'texture').length ?? 0
	);
	let audioCount = $derived(
		data.resources?.filter((r: { type: string }) => r.type === 'audio').length ?? 0
	);
</script>

<div class="min-h-screen bg-slate-950 font-sans text-slate-100">
	<!-- Top Navbar -->
	<header class="border-b border-slate-800 bg-slate-900">
		<div class="mx-auto flex h-16 max-w-7xl items-center justify-between px-4 sm:px-6 lg:px-8">
			<div class="flex items-center space-x-3">
				<div
					class="flex h-9 w-9 items-center justify-center rounded-lg bg-blue-600 font-bold text-white shadow-md shadow-blue-600/30"
				>
					M
				</div>
				<span class="text-lg font-bold tracking-wide text-white">Mizzle Admin</span>
			</div>

			<div class="flex items-center space-x-4">
				<div class="text-right">
					<p class="text-sm font-medium text-slate-200">{data.user.name}</p>
					<p class="text-xs text-slate-400">{data.user.email}</p>
				</div>
				<span
					class="rounded-full border border-blue-500/30 bg-blue-500/20 px-2.5 py-0.5 text-xs font-semibold text-blue-400"
				>
					Admin
				</span>
				<form action="?/logout" method="POST" use:enhance>
					<button
						type="submit"
						class="rounded-lg border border-slate-700 bg-slate-800 px-3.5 py-1.5 text-sm font-medium text-slate-300 transition hover:bg-slate-700"
					>
						Sign Out
					</button>
				</form>
			</div>
		</div>
	</header>

	<!-- Main Content Area -->
	<main class="mx-auto max-w-7xl px-4 py-10 sm:px-6 lg:px-8">
		<!-- Action Feedback Banner -->
		{#if form?.error}
			<div class="mb-6 rounded-xl border border-red-500/30 bg-red-500/10 p-4 text-sm text-red-400">
				<span class="font-bold">Error:</span> {form.error}
			</div>
		{/if}
		{#if form?.message}
			<div class="mb-6 rounded-xl border border-emerald-500/30 bg-emerald-500/10 p-4 text-sm text-emerald-400">
				<span class="font-bold">Success:</span> {form.message}
			</div>
		{/if}

		<div class="mb-8 flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
			<div>
				<h1 class="text-3xl font-extrabold tracking-tight text-white">Dashboard Overview</h1>
				<p class="mt-1 text-slate-400">
					Welcome back, {data.user.name}. Manage users, droplets, and the mobile resource library.
				</p>
			</div>

			<!-- Buttons to toggle views and upload -->
			<div class="flex flex-wrap items-center gap-3">
				<button
					onclick={() => (showResources = !showResources)}
					class="inline-flex items-center justify-center rounded-xl border border-purple-500/30 bg-purple-600/20 px-4 py-2.5 text-sm font-semibold text-purple-300 shadow-md transition hover:bg-purple-600/30 active:scale-95"
				>
					{showResources ? 'Hide Resources' : 'View Resources'}
				</button>
				<button
					onclick={() => (showUsers = !showUsers)}
					class="inline-flex items-center justify-center rounded-xl border border-slate-700 bg-slate-800 px-4 py-2.5 text-sm font-semibold text-slate-200 shadow-md transition hover:bg-slate-700 active:scale-95"
				>
					{showUsers ? 'Hide Users' : 'View Users'}
				</button>
				<button
					onclick={() => (showDroplets = !showDroplets)}
					class="inline-flex items-center justify-center rounded-xl border border-slate-700 bg-slate-800 px-4 py-2.5 text-sm font-semibold text-slate-200 shadow-md transition hover:bg-slate-700 active:scale-95"
				>
					{showDroplets ? 'Hide Droplets' : 'View Droplets'}
				</button>
				<button
					onclick={() => (showUploadModal = true)}
					class="inline-flex items-center justify-center rounded-xl bg-purple-600 px-4 py-2.5 text-sm font-semibold text-white shadow-lg shadow-purple-600/30 transition hover:bg-purple-500 active:scale-95"
				>
					+ Add Resource
				</button>
			</div>
		</div>

		<!-- Overview Cards -->
		<div class="mb-10 grid grid-cols-1 gap-6 sm:grid-cols-4">
			<div class="rounded-xl border border-purple-500/30 bg-purple-950/20 p-6 shadow-lg backdrop-blur-sm">
				<div class="flex items-center justify-between">
					<span class="text-sm font-medium text-purple-300">Resource Library</span>
					<span class="rounded bg-purple-500/20 px-2 py-0.5 text-xs font-semibold text-purple-300">
						Textures & Audio
					</span>
				</div>
				<p class="mt-2 text-3xl font-bold text-white">{data.resources?.length ?? 0}</p>
				<p class="mt-1 text-xs text-purple-400">
					{textureCount} Textures &bull; {audioCount} Audios
				</p>
			</div>

			<div class="rounded-xl border border-slate-800 bg-slate-900 p-6 shadow-lg">
				<div class="flex items-center justify-between">
					<span class="text-sm font-medium text-slate-400">Total Users</span>
					<span class="rounded bg-blue-500/20 px-2 py-0.5 text-xs font-semibold text-blue-400">
						D1 DB
					</span>
				</div>
				<p class="mt-2 text-3xl font-bold text-white">{data.users?.length ?? 0}</p>
				<p class="mt-1 text-xs text-slate-500">Registered account records</p>
			</div>

			<div class="rounded-xl border border-slate-800 bg-slate-900 p-6 shadow-lg">
				<div class="flex items-center justify-between">
					<span class="text-sm font-medium text-slate-400">Total Droplets</span>
					<span class="rounded bg-cyan-500/20 px-2 py-0.5 text-xs font-semibold text-cyan-400">
						R2 / D1
					</span>
				</div>
				<p class="mt-2 text-3xl font-bold text-white">{data.droplets?.length ?? 0}</p>
				<p class="mt-1 text-xs text-slate-500">Uploaded droplet files</p>
			</div>

			<div class="rounded-xl border border-slate-800 bg-slate-900 p-6 shadow-lg">
				<div class="flex items-center justify-between">
					<span class="text-sm font-medium text-slate-400">Storage Services</span>
					<span class="rounded bg-emerald-500/20 px-2 py-0.5 text-xs font-semibold text-emerald-400">
						Online
					</span>
				</div>
				<p class="mt-2 text-2xl font-bold text-white">Cloudflare R2 & D1</p>
				<p class="mt-1 text-xs text-slate-500">Resource & DB storage active</p>
			</div>
		</div>

		<!-- Resource Library Table Section -->
		{#if showResources}
			<div class="mb-10 rounded-xl border border-purple-500/20 bg-slate-900 p-6 shadow-xl">
				<div class="mb-6 flex flex-wrap items-center justify-between gap-4 border-b border-slate-800 pb-4">
					<div>
						<h2 class="text-xl font-bold text-white">Mobile Resource Library</h2>
						<p class="text-xs text-slate-400">
							Global texture and audio assets available for mobile app users to download
						</p>
					</div>
					<div class="flex items-center space-x-3">
						<span class="rounded-full bg-purple-500/20 border border-purple-500/30 px-3 py-1 text-xs font-semibold text-purple-300">
							{data.resources?.length ?? 0} Assets Total
						</span>
						<button
							onclick={() => (showUploadModal = true)}
							class="rounded-lg bg-purple-600 px-3 py-1.5 text-xs font-semibold text-white transition hover:bg-purple-500"
						>
							+ Upload Resource
						</button>
					</div>
				</div>

				{#if !data.resources || data.resources.length === 0}
					<div class="py-12 text-center">
						<p class="text-sm text-slate-400">No resources added to the library yet.</p>
						<button
							onclick={() => (showUploadModal = true)}
							class="mt-4 rounded-xl bg-purple-600 px-4 py-2 text-xs font-semibold text-white transition hover:bg-purple-500"
						>
							Add First Resource
						</button>
					</div>
				{:else}
					<div class="overflow-x-auto">
						<table class="w-full text-left text-sm text-slate-300">
							<thead class="border-b border-slate-800 bg-slate-950/50 text-xs uppercase text-slate-400">
								<tr>
									<th class="px-4 py-3">Preview</th>
									<th class="px-4 py-3">Resource Name</th>
									<th class="px-4 py-3">Type</th>
									<th class="px-4 py-3">Texture Region (X, Y, W, H)</th>
									<th class="px-4 py-3">R2 Key</th>
									<th class="px-4 py-3">Uploaded</th>
									<th class="px-4 py-3 text-right">Actions</th>
								</tr>
							</thead>
							<tbody class="divide-y divide-slate-800">
								{#each data.resources as r}
									<tr class="hover:bg-slate-800/50 transition">
										<td class="px-4 py-3">
											{#if r.type === 'texture'}
												<img
													src="/api/resources/{r.id}/download"
													alt={r.name}
													class="h-10 w-10 rounded border border-slate-700 bg-slate-950 object-cover"
												/>
											{:else}
												<audio controls src="/api/resources/{r.id}/download" class="h-8 max-w-[160px]"></audio>
											{/if}
										</td>
										<td class="px-4 py-3 font-semibold text-white">{r.name}</td>
										<td class="px-4 py-3">
											{#if r.type === 'texture'}
												<span class="rounded-full border border-emerald-500/30 bg-emerald-500/20 px-2.5 py-0.5 text-xs font-semibold text-emerald-400">
													Texture
												</span>
											{:else}
												<span class="rounded-full border border-amber-500/30 bg-amber-500/20 px-2.5 py-0.5 text-xs font-semibold text-amber-400">
													Audio
												</span>
											{/if}
										</td>
										<td class="px-4 py-3 font-mono text-xs text-slate-400">
											{#if r.type === 'texture'}
												({r.regionX}, {r.regionY}, {r.regionWidth}, {r.regionHeight})
											{:else}
												<span class="text-slate-600">N/A</span>
											{/if}
										</td>
										<td class="px-4 py-3 font-mono text-xs text-purple-400">{r.r2Key}</td>
										<td class="px-4 py-3 text-xs text-slate-400">
											{new Date(r.createdAt).toLocaleString()}
										</td>
										<td class="px-4 py-3 text-right space-x-2">
											<a
												href="/api/resources/{r.id}/download"
												target="_blank"
												class="inline-block rounded-lg border border-slate-700 bg-slate-800 px-2.5 py-1 text-xs font-medium text-slate-200 transition hover:bg-slate-700"
											>
												Download
											</a>
											<form action="?/deleteResource" method="POST" use:enhance class="inline">
												<input type="hidden" name="id" value={r.id} />
												<button
													type="submit"
													onclick={(e) => !confirm(`Delete resource '${r.name}'?`) && e.preventDefault()}
													class="rounded-lg border border-red-500/30 bg-red-500/20 px-2.5 py-1 text-xs font-medium text-red-400 transition hover:bg-red-500/30"
												>
													Delete
												</button>
											</form>
										</td>
									</tr>
								{/each}
							</tbody>
						</table>
					</div>
				{/if}
			</div>
		{/if}

		<!-- Users List Table Section -->
		{#if showUsers}
			<div class="mb-10 rounded-xl border border-slate-800 bg-slate-900 p-6 shadow-xl">
				<div class="mb-6 flex items-center justify-between border-b border-slate-800 pb-4">
					<div>
						<h2 class="text-xl font-bold text-white">Registered Users</h2>
						<p class="text-xs text-slate-400">List of all users registered in the system</p>
					</div>
					<span class="rounded-full bg-slate-800 px-3 py-1 text-xs font-medium text-slate-300">
						{data.users?.length ?? 0} Users Total
					</span>
				</div>

				{#if !data.users || data.users.length === 0}
					<p class="py-8 text-center text-sm text-slate-500">No users registered yet.</p>
				{:else}
					<div class="overflow-x-auto">
						<table class="w-full text-left text-sm text-slate-300">
							<thead class="border-b border-slate-800 bg-slate-950/50 text-xs uppercase text-slate-400">
								<tr>
									<th class="px-4 py-3">User</th>
									<th class="px-4 py-3">Email</th>
									<th class="px-4 py-3">Role</th>
									<th class="px-4 py-3">User ID</th>
									<th class="px-4 py-3">Registered At</th>
								</tr>
							</thead>
							<tbody class="divide-y divide-slate-800">
								{#each data.users as u}
									<tr class="hover:bg-slate-800/50 transition">
										<td class="px-4 py-3 font-semibold text-white">{u.name}</td>
										<td class="px-4 py-3 text-slate-300">{u.email}</td>
										<td class="px-4 py-3">
											{#if u.role === 'admin'}
												<span class="rounded-full bg-blue-500/20 px-2.5 py-0.5 text-xs font-semibold text-blue-400 border border-blue-500/30">
													Admin
												</span>
											{:else}
												<span class="rounded-full bg-slate-800 px-2.5 py-0.5 text-xs font-semibold text-slate-300 border border-slate-700">
													User
												</span>
											{/if}
										</td>
										<td class="px-4 py-3 font-mono text-xs text-slate-400">{u.id}</td>
										<td class="px-4 py-3 text-xs text-slate-400">
											{new Date(u.createdAt).toLocaleString()}
										</td>
									</tr>
								{/each}
							</tbody>
						</table>
					</div>
				{/if}
			</div>
		{/if}

		<!-- Droplets List Table Section -->
		{#if showDroplets}
			<div class="rounded-xl border border-slate-800 bg-slate-900 p-6 shadow-xl">
				<div class="mb-6 flex items-center justify-between border-b border-slate-800 pb-4">
					<div>
						<h2 class="text-xl font-bold text-white">Stored Droplets</h2>
						<p class="text-xs text-slate-400">All droplets uploaded to server and R2 storage</p>
					</div>
					<span class="rounded-full bg-cyan-500/20 border border-cyan-500/30 px-3 py-1 text-xs font-semibold text-cyan-400">
						{data.droplets?.length ?? 0} Droplets Total
					</span>
				</div>

				{#if !data.droplets || data.droplets.length === 0}
					<p class="py-8 text-center text-sm text-slate-500">No droplets stored on server yet.</p>
				{:else}
					<div class="overflow-x-auto">
						<table class="w-full text-left text-sm text-slate-300">
							<thead class="border-b border-slate-800 bg-slate-950/50 text-xs uppercase text-slate-400">
								<tr>
									<th class="px-4 py-3">Droplet Name</th>
									<th class="px-4 py-3">Droplet ID</th>
									<th class="px-4 py-3">Creator / User</th>
									<th class="px-4 py-3">R2 Storage Key</th>
									<th class="px-4 py-3">Uploaded At</th>
								</tr>
							</thead>
							<tbody class="divide-y divide-slate-800">
								{#each data.droplets as d}
									<tr class="hover:bg-slate-800/50 transition">
										<td class="px-4 py-3 font-semibold text-white">
											<div class="flex items-center space-x-2">
												<span class="h-2 w-2 rounded-full bg-cyan-400 shadow-sm shadow-cyan-400/50"></span>
												<span>{d.name}</span>
											</div>
										</td>
										<td class="px-4 py-3 font-mono text-xs text-slate-400">{d.id}</td>
										<td class="px-4 py-3 text-slate-300">
											{#if d.userName}
												<span class="font-medium text-slate-200">{d.userName}</span>
											{:else if d.userId}
												<span class="font-mono text-xs text-slate-400">{d.userId}</span>
											{:else}
												<span class="text-slate-500 italic">Anonymous</span>
											{/if}
										</td>
										<td class="px-4 py-3 font-mono text-xs text-blue-400">{d.r2Key}</td>
										<td class="px-4 py-3 text-xs text-slate-400">
											{new Date(d.createdAt).toLocaleString()}
										</td>
									</tr>
								{/each}
							</tbody>
						</table>
					</div>
				{/if}
			</div>
		{/if}
	</main>
</div>

<!-- Upload Resource Modal -->
{#if showUploadModal}
	<div class="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/80 p-4 backdrop-blur-sm">
		<div class="w-full max-w-lg rounded-2xl border border-purple-500/30 bg-slate-900 p-6 shadow-2xl">
			<div class="mb-4 flex items-center justify-between border-b border-slate-800 pb-3">
				<h3 class="text-lg font-bold text-white">Upload New Resource</h3>
				<button
					onclick={() => (showUploadModal = false)}
					class="text-slate-400 hover:text-white"
				>
					✕
				</button>
			</div>

			<form
				action="?/uploadResource"
				method="POST"
				enctype="multipart/form-data"
				use:enhance={() => {
					return async ({ result, update }) => {
						await update();
						if (result.type === 'success') {
							showUploadModal = false;
						}
					};
				}}
				class="space-y-4"
			>
				<div>
					<label for="resource-name" class="block text-xs font-medium uppercase tracking-wider text-slate-400 mb-1">Resource Name</label>
					<input
						id="resource-name"
						type="text"
						name="name"
						required
						placeholder="e.g. Grass Texture 1, Explosion Sound"
						class="w-full rounded-xl border border-slate-700 bg-slate-950 px-4 py-2.5 text-sm text-white placeholder-slate-500 focus:border-purple-500 focus:outline-none"
					/>
				</div>

				<div>
					<span class="block text-xs font-medium uppercase tracking-wider text-slate-400 mb-1">Resource Type</span>
					<div class="flex items-center space-x-4">
						<label class="flex items-center space-x-2 text-sm text-slate-200 cursor-pointer">
							<input
								type="radio"
								name="type"
								value="texture"
								bind:group={resourceType}
								class="text-purple-600 focus:ring-purple-500"
							/>
							<span>Texture (Image)</span>
						</label>
						<label class="flex items-center space-x-2 text-sm text-slate-200 cursor-pointer">
							<input
								type="radio"
								name="type"
								value="audio"
								bind:group={resourceType}
								class="text-purple-600 focus:ring-purple-500"
							/>
							<span>Audio (Sound)</span>
						</label>
					</div>
				</div>

				<div>
					<label for="resource-file" class="block text-xs font-medium uppercase tracking-wider text-slate-400 mb-1">Resource File</label>
					<input
						id="resource-file"
						type="file"
						name="file"
						required
						accept={resourceType === 'texture' ? 'image/*' : 'audio/*'}
						class="w-full rounded-xl border border-slate-700 bg-slate-950 px-4 py-2.5 text-sm text-slate-300 file:mr-4 file:rounded-lg file:border-0 file:bg-purple-600 file:px-3 file:py-1 file:text-xs file:font-semibold file:text-white hover:file:bg-purple-500"
					/>
				</div>

				{#if resourceType === 'texture'}
					<div class="rounded-xl border border-slate-800 bg-slate-950/60 p-4">
						<p class="text-xs font-bold text-slate-300 mb-2">Texture Region Parameters</p>
						<div class="grid grid-cols-2 gap-3">
							<div>
								<label for="region-x" class="block text-[10px] text-slate-400 uppercase">Region X</label>
								<input
									id="region-x"
									type="number"
									name="regionX"
									value="0"
									class="w-full rounded-lg border border-slate-700 bg-slate-900 px-3 py-1.5 text-xs text-white"
								/>
							</div>
							<div>
								<label for="region-y" class="block text-[10px] text-slate-400 uppercase">Region Y</label>
								<input
									id="region-y"
									type="number"
									name="regionY"
									value="0"
									class="w-full rounded-lg border border-slate-700 bg-slate-900 px-3 py-1.5 text-xs text-white"
								/>
							</div>
							<div>
								<label for="region-width" class="block text-[10px] text-slate-400 uppercase">Region Width (-1 for full)</label>
								<input
									id="region-width"
									type="number"
									name="regionWidth"
									value="-1"
									class="w-full rounded-lg border border-slate-700 bg-slate-900 px-3 py-1.5 text-xs text-white"
								/>
							</div>
							<div>
								<label for="region-height" class="block text-[10px] text-slate-400 uppercase">Region Height (-1 for full)</label>
								<input
									id="region-height"
									type="number"
									name="regionHeight"
									value="-1"
									class="w-full rounded-lg border border-slate-700 bg-slate-900 px-3 py-1.5 text-xs text-white"
								/>
							</div>
						</div>
					</div>
				{/if}

				<div class="flex items-center justify-end space-x-3 pt-4 border-t border-slate-800">
					<button
						type="button"
						onclick={() => (showUploadModal = false)}
						class="rounded-xl border border-slate-700 bg-slate-800 px-4 py-2 text-xs font-semibold text-slate-300 transition hover:bg-slate-700"
					>
						Cancel
					</button>
					<button
						type="submit"
						class="rounded-xl bg-purple-600 px-5 py-2 text-xs font-semibold text-white shadow-lg shadow-purple-600/30 transition hover:bg-purple-500"
					>
						Upload Resource
					</button>
				</div>
			</form>
		</div>
	</div>
{/if}
