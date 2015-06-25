package ru.simsonic.rscUtilityLibrary.TextProcessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public final class GenericChatCodes
{
	private final static HashMap<String, String> minecraftChatCodes = new HashMap<>();
	static
	{
		// Char colors
		minecraftChatCodes.put("{BLACK}",       "§0");
		minecraftChatCodes.put("{_BL}",         "§0");
		minecraftChatCodes.put("{DARKBLUE}",    "§1");
		minecraftChatCodes.put("{_DB}",         "§1");
		minecraftChatCodes.put("{DARKGREEN}",   "§2");
		minecraftChatCodes.put("{_DG}",         "§2");
		minecraftChatCodes.put("{DARKCYAN}",    "§3");
		minecraftChatCodes.put("{DARKAQUA}",    "§3");
		minecraftChatCodes.put("{_DC}",         "§3");
		minecraftChatCodes.put("{DARKRED}",     "§4");
		minecraftChatCodes.put("{_DR}",         "§4");
		minecraftChatCodes.put("{PURPLE}",      "§5");
		minecraftChatCodes.put("{_DP}",         "§5");
		minecraftChatCodes.put("{BROWN}",       "§6");
		minecraftChatCodes.put("{GOLD}",        "§6");
		minecraftChatCodes.put("{_BR}",         "§6");
		minecraftChatCodes.put("{GRAY}",        "§7");
		minecraftChatCodes.put("{LIGHTGRAY}",   "§7");
		minecraftChatCodes.put("{SILVER}",      "§7");
		minecraftChatCodes.put("{_LS}",         "§7");
		minecraftChatCodes.put("{DARKGRAY}",    "§8");
		minecraftChatCodes.put("{DARKSILVER}",  "§8");
		minecraftChatCodes.put("{_DS}",         "§8");
		minecraftChatCodes.put("{BLUE}",        "§9");
		minecraftChatCodes.put("{LIGHTBLUE}",   "§9");
		minecraftChatCodes.put("{_LB}",         "§9");
		minecraftChatCodes.put("{GREEN}",       "§a");
		minecraftChatCodes.put("{LIGHTGREEN}",  "§a");
		minecraftChatCodes.put("{_LG}",         "§a");
		minecraftChatCodes.put("{CYAN}",        "§b");
		minecraftChatCodes.put("{LIGHTCYAN}",   "§b");
		minecraftChatCodes.put("{AQUA}",        "§b");
		minecraftChatCodes.put("{LIGHTAQUA}",   "§b");
		minecraftChatCodes.put("{_LC}",         "§b");
		minecraftChatCodes.put("{RED}",         "§c");
		minecraftChatCodes.put("{LIGHTRED}",    "§c");
		minecraftChatCodes.put("{_LR}",         "§c");
		minecraftChatCodes.put("{PURPLE}",      "§d");
		minecraftChatCodes.put("{LIGHTPURPLE}", "§d");
		minecraftChatCodes.put("{PINK}",        "§d");
		minecraftChatCodes.put("{MAGENTA}",     "§d");
		minecraftChatCodes.put("{_LP}",         "§d");
		minecraftChatCodes.put("{YELLOW}",      "§e");
		minecraftChatCodes.put("{_YL}",         "§e");
		minecraftChatCodes.put("{WHITE}",       "§f");
		minecraftChatCodes.put("{_WH}",         "§f");
		// Char styles
		minecraftChatCodes.put("{BOLD}",        "§l");
		minecraftChatCodes.put("{_B}",          "§l");
		minecraftChatCodes.put("{STRIKED}",     "§m");
		minecraftChatCodes.put("{_S}",          "§m");
		minecraftChatCodes.put("{UNDERLINED}",  "§n");
		minecraftChatCodes.put("{_U}",          "§n");
		minecraftChatCodes.put("{ITALIC}",      "§o");
		minecraftChatCodes.put("{_I}",          "§o");
		minecraftChatCodes.put("{RESET}",       "§r");
		minecraftChatCodes.put("{_R}",          "§r");
		// Special characters
		minecraftChatCodes.put("{_NL}",         "\n");
	}
	public static String processStringStatic(String input)
	{
		if(input == null)
			return "";
		for(Entry<String, String> entry : minecraftChatCodes.entrySet())
			input = input.replace(entry.getKey(), entry.getValue());
		return input;
	}
	private final HashMap<String, String> templatesPurpose = new HashMap<>();
	public String processString(String input)
	{
		input = processStringStatic(input);
		for(Entry<String, String> entry : templatesPurpose.entrySet())
			input = input.replace(entry.getKey(), entry.getValue());
		return input;
	}
	public static String glue(String[] strings, String glue)
	{
		if(glue == null)
			glue = "";
		if(strings.length == 0)
			return "";
		final StringBuilder builder = new StringBuilder(strings[0] == null || "".equals(strings[0]) ? "" : strings[0]);
		for(int nStr = 1; nStr < strings.length; nStr += 1)
			if(strings[nStr] != null && !"".equals(strings[nStr]))
				builder.append(glue).append(strings[nStr]);
		return builder.toString();
	}
	public static boolean wildcardMatch(String text, String pattern)
	{
		if(pattern == null || text == null)
			return false;
		for(String card : pattern.split("\\*"))
		{
			int index = text.indexOf(card);
			if(index == -1)
				return false;
			text = text.substring(index + card.length());
		}
		return true;
	}
	public static boolean wildcardMatchIgnoreCase(String text, String pattern)
	{
		return (pattern != null && text != null) ? wildcardMatch(text.toLowerCase(), pattern.toLowerCase()) : false;
	}
	public static List<String> wrapWords(String text, int lineLength)
	{
		String[] intendedLines = text.split("\\n+", 0);
		ArrayList<String> lines = new ArrayList<>();
		for(String intendedLine : intendedLines)
		{
			String[] words = intendedLine.split(" ");
			StringBuilder buffer = new StringBuilder();
			for(String word : words)
			{
				if(word.length() >= lineLength)
				{
					if(buffer.length() != 0)
						lines.add(buffer.toString());
					lines.add(word);
					buffer = new StringBuilder();
					continue;
				}
				if(buffer.length() + word.length() >= lineLength)
				{
					lines.add(buffer.toString());
					buffer = new StringBuilder();
				}
				if(buffer.length() != 0)
					buffer.append(' ');
				buffer.append(word);
			}
			lines.add(buffer.toString());
		}
		return lines;
	}
}
