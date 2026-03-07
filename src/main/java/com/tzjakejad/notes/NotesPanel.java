package com.tzjakejad.notes;

import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotesPanel extends PluginPanel
{
	private static final String CONFIG_GROUP = "betternotes";
	private static final String CONFIG_KEY = "notes";

	private final ConfigManager configManager;
	private final JPanel notesContainer;
	private final JTextArea inputArea;
	private final List<NoteItem> notes = new ArrayList<>();

	public NotesPanel(ConfigManager configManager)
	{
		super(false);
		this.configManager = configManager;

		setLayout(new BorderLayout(0, 5));
		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setBorder(new EmptyBorder(10, 10, 10, 10));

		JPanel topPanel = new JPanel(new BorderLayout(5, 0));
		topPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
		topPanel.setBorder(new EmptyBorder(0, 0, 6, 0));

		JLabel title = new JLabel("Notes");
		title.setForeground(Color.WHITE);
		title.setFont(FontManager.getRunescapeBoldFont());

		JButton clearDoneBtn = new JButton("Clear Done");
		clearDoneBtn.setFont(FontManager.getRunescapeSmallFont());
		clearDoneBtn.setFocusPainted(false);
		clearDoneBtn.setToolTipText("Remove all checked notes");
		clearDoneBtn.addActionListener(e -> clearCompleted());

		topPanel.add(title, BorderLayout.WEST);
		topPanel.add(clearDoneBtn, BorderLayout.EAST);

		notesContainer = new JPanel();
		notesContainer.setLayout(new BoxLayout(notesContainer, BoxLayout.Y_AXIS));
		notesContainer.setBackground(ColorScheme.DARK_GRAY_COLOR);

		JScrollPane scrollPane = new JScrollPane(notesContainer);
		scrollPane.setBackground(ColorScheme.DARK_GRAY_COLOR);
		scrollPane.setBorder(null);
		scrollPane.getViewport().setBackground(ColorScheme.DARK_GRAY_COLOR);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		JPanel inputPanel = new JPanel(new BorderLayout(0, 4));
		inputPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
		inputPanel.setBorder(new EmptyBorder(4, 0, 0, 0));

		JLabel inputLabel = new JLabel("Add notes (one per line, paste supported):");
		inputLabel.setFont(FontManager.getRunescapeSmallFont());
		inputLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);

		inputArea = new JTextArea(4, 20);
		inputArea.setFont(FontManager.getRunescapeSmallFont());
		inputArea.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		inputArea.setForeground(Color.WHITE);
		inputArea.setCaretColor(Color.WHITE);
		inputArea.setLineWrap(true);
		inputArea.setWrapStyleWord(true);
		inputArea.setBorder(new EmptyBorder(5, 5, 5, 5));
		inputArea.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown())
				{
					addFromInput();
				}
			}
		});

		JScrollPane inputScroll = new JScrollPane(inputArea);
		inputScroll.setBorder(BorderFactory.createLineBorder(ColorScheme.MEDIUM_GRAY_COLOR));

		JButton addBtn = new JButton("Add  (Ctrl+Enter)");
		addBtn.setFocusPainted(false);
		addBtn.addActionListener(e -> addFromInput());

		inputPanel.add(inputLabel, BorderLayout.NORTH);
		inputPanel.add(inputScroll, BorderLayout.CENTER);
		inputPanel.add(addBtn, BorderLayout.SOUTH);

		add(topPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(inputPanel, BorderLayout.SOUTH);

		loadNotes();
	}

	private void addFromInput()
	{
		String text = inputArea.getText();
		if (text.trim().isEmpty())
		{
			return;
		}

		for (String line : text.split("\n"))
		{
			String trimmed = line.trim();
			if (!trimmed.isEmpty())
			{
				notes.add(new NoteItem(trimmed, false));
			}
		}

		inputArea.setText("");
		rebuildNoteRows();
		saveNotes();
	}

	private void clearCompleted()
	{
		notes.removeIf(n -> n.checked);
		rebuildNoteRows();
		saveNotes();
	}

	private void rebuildNoteRows()
	{
		notesContainer.removeAll();

		for (NoteItem note : notes)
		{
			JPanel row = new JPanel(new BorderLayout(4, 0));
			row.setBackground(ColorScheme.DARKER_GRAY_COLOR);
			row.setBorder(new EmptyBorder(4, 6, 4, 4));
			row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

			Font baseFont = FontManager.getRunescapeSmallFont();
			Map<TextAttribute, Object> attrs = new HashMap<>();
			attrs.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
			Font strikeFont = baseFont.deriveFont(attrs);
			Color dimColor = new Color(75, 75, 75);

			JCheckBox checkbox = new JCheckBox(note.text);
			checkbox.setSelected(note.checked);
			checkbox.setBackground(ColorScheme.DARKER_GRAY_COLOR);
			checkbox.setForeground(note.checked ? dimColor : Color.WHITE);
			checkbox.setFont(note.checked ? strikeFont : baseFont);
			checkbox.addItemListener(e ->
			{
				note.checked = checkbox.isSelected();
				checkbox.setForeground(note.checked ? dimColor : Color.WHITE);
				checkbox.setFont(note.checked ? strikeFont : baseFont);
				saveNotes();
			});

			JButton deleteBtn = new JButton("✕");
			deleteBtn.setFont(FontManager.getRunescapeSmallFont());
			deleteBtn.setForeground(new Color(200, 60, 60));
			deleteBtn.setBackground(ColorScheme.DARKER_GRAY_COLOR);
			deleteBtn.setBorderPainted(false);
			deleteBtn.setFocusPainted(false);
			deleteBtn.setPreferredSize(new Dimension(22, 22));
			deleteBtn.setToolTipText("Delete this note");
			deleteBtn.addActionListener(e ->
			{
				notes.remove(note);
				rebuildNoteRows();
				saveNotes();
			});

			row.add(checkbox, BorderLayout.CENTER);
			row.add(deleteBtn, BorderLayout.EAST);

			notesContainer.add(row);
			notesContainer.add(Box.createRigidArea(new Dimension(0, 2)));
		}

		notesContainer.revalidate();
		notesContainer.repaint();
	}

	private void saveNotes()
	{
		StringBuilder sb = new StringBuilder();
		for (NoteItem note : notes)
		{
			sb.append(note.checked ? '1' : '0')
				.append('|')
				.append(note.text.replace("\n", " "))
				.append('\n');
		}
		configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY, sb.toString());
	}

	private void loadNotes()
	{
		String data = configManager.getConfiguration(CONFIG_GROUP, CONFIG_KEY);
		notes.clear();

		if (data != null && !data.isEmpty())
		{
			for (String line : data.split("\n"))
			{
				if (line.length() < 3)
				{
					continue;
				}
				boolean checked = line.charAt(0) == '1';
				String text = line.substring(2);
				if (!text.isEmpty())
				{
					notes.add(new NoteItem(text, checked));
				}
			}
		}

		rebuildNoteRows();
	}

	private static class NoteItem
	{
		String text;
		boolean checked;

		NoteItem(String text, boolean checked)
		{
			this.text = text;
			this.checked = checked;
		}
	}
}
