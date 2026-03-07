package com.example;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("betternotes")
public interface NotesConfig extends Config
{
	@ConfigItem(
		keyName = "notes",
		name = "Notes",
		description = "Stored notes data",
		hidden = true
	)
	default String notes()
	{
		return "";
	}
}
