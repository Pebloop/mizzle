import { integer, sqliteTable, text } from 'drizzle-orm/sqlite-core';
import { sql } from 'drizzle-orm';

export const task = sqliteTable('task', {
	id: text('id')
		.primaryKey()
		.$defaultFn(() => crypto.randomUUID()),
	title: text('title').notNull(),
	priority: integer('priority').notNull().default(1)
});

export const droplet = sqliteTable('droplet', {
	id: text('id').primaryKey(),
	name: text('name').notNull(),
	userId: text('user_id'),
	r2Key: text('r2_key').notNull(),
	createdAt: integer('created_at', { mode: 'timestamp_ms' })
		.default(sql`(cast(unixepoch('subsecond') * 1000 as integer))`)
		.notNull()
});

export const resource = sqliteTable('resource', {
	id: text('id')
		.primaryKey()
		.$defaultFn(() => crypto.randomUUID()),
	name: text('name').notNull(),
	type: text('type').notNull(), // 'texture' | 'audio'
	r2Key: text('r2_key').notNull(),
	mimeType: text('mime_type'),
	regionX: integer('region_x').notNull().default(0),
	regionY: integer('region_y').notNull().default(0),
	regionWidth: integer('region_width').notNull().default(-1),
	regionHeight: integer('region_height').notNull().default(-1),
	userId: text('user_id'),
	createdAt: integer('created_at', { mode: 'timestamp_ms' })
		.default(sql`(cast(unixepoch('subsecond') * 1000 as integer))`)
		.notNull()
});

export * from './auth.schema';

