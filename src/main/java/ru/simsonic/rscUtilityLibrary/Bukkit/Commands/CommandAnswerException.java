package ru.simsonic.rscUtilityLibrary.Bukkit.Commands;

import java.util.List;

public class CommandAnswerException extends Exception
{
	private final String[] lines;
	public CommandAnswerException(String message)
	{
		this.lines = new String[] { message };
	}
	public CommandAnswerException(String[] messages)
	{
		this.lines = messages;
	}
	public CommandAnswerException(List<String> messages)
	{
		this.lines = messages.toArray(new String[messages.size()]);
	}
	public String[] getMessageArray()
	{
		return lines;
	}
}
