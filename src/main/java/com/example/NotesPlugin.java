package com.example;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(
	name = "Better Notes",
	description = "A notepad with per-line checkboxes and paste support",
	tags = {"notes", "checklist", "todo"}
)
public class NotesPlugin extends Plugin
{
	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ConfigManager configManager;

	private NotesPanel panel;
	private NavigationButton navButton;

	@Override
	protected void startUp()
	{
		panel = new NotesPanel(configManager);

		navButton = NavigationButton.builder()
			.tooltip("Better Notes")
			.icon(buildIcon())
			.priority(7)
			.panel(panel)
			.build();

		clientToolbar.addNavigation(navButton);
		log.info("Better Notes started");
	}

	@Override
	protected void shutDown()
	{
		clientToolbar.removeNavigation(navButton);
		panel = null;
		navButton = null;
		log.info("Better Notes stopped");
	}

	@Provides
	NotesConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NotesConfig.class);
	}

	/** Draws a simple notepad icon programmatically (16x16). */
	private static BufferedImage buildIcon()
	{
		BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Notepad body
		g.setColor(new Color(220, 200, 140));
		g.fillRoundRect(1, 0, 13, 15, 2, 2);

		// Ruled lines
		g.setColor(new Color(150, 130, 90));
		g.drawLine(3, 4, 12, 4);
		g.drawLine(3, 7, 12, 7);
		g.drawLine(3, 10, 12, 10);
		g.drawLine(3, 13, 12, 13);

		// Checkbox squares on the left
		g.setColor(new Color(100, 100, 100));
		g.drawRect(2, 2, 3, 3);   // row 1 checkbox
		g.drawRect(2, 5, 3, 3);   // row 2 checkbox

		// Tick in first checkbox
		g.setColor(new Color(60, 170, 60));
		g.drawLine(3, 4, 4, 5);
		g.drawLine(4, 5, 5, 3);

		// Pencil in bottom-right corner
		g.setColor(new Color(60, 120, 220));
		g.drawLine(10, 13, 14, 9);
		g.drawLine(11, 13, 15, 9);
		g.setColor(new Color(230, 180, 80));
		g.drawLine(14, 9, 15, 9);
		g.setColor(Color.DARK_GRAY);
		g.drawLine(10, 14, 11, 15);

		g.dispose();

		// Scale up to keep it sharp at the size RuneLite uses (~16px nav icon)
		return img;
	}
}
