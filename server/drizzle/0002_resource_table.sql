CREATE TABLE `resource` (
	`id` text PRIMARY KEY NOT NULL,
	`name` text NOT NULL,
	`type` text NOT NULL,
	`r2_key` text NOT NULL,
	`mime_type` text,
	`region_x` integer DEFAULT 0 NOT NULL,
	`region_y` integer DEFAULT 0 NOT NULL,
	`region_width` integer DEFAULT -1 NOT NULL,
	`region_height` integer DEFAULT -1 NOT NULL,
	`user_id` text,
	`created_at` integer DEFAULT (cast(unixepoch('subsecond') * 1000 as integer)) NOT NULL
);
