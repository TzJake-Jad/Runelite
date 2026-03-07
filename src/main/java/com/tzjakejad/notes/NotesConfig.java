package com.tzjakejad.notes;

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
```

---

### Step 2 — Delete the old `com/example` files

Go to each of the 3 old files in `src/main/java/com/example/`, open each one, click the **trash icon** (top right), and commit the deletion.

---

### Step 3 — Update `runelite-plugin.properties`

Open that file at the root of your repo and make sure it looks like this:
```
displayName=Better Notes
author=TzJake-Jad
description=A notepad with per-line checkboxes and paste support
tags=notes,checklist,todo
plugins=com.tzjakejad.notes.NotesPlugin
```

---

### Step 4 — Check `build.gradle`

Open `build.gradle` and confirm this line exists:
```
runeLiteVersion = 'latest.release'
