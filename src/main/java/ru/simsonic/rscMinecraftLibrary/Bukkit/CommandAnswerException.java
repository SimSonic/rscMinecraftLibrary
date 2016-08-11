package ru.simsonic.rscMinecraftLibrary.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class CommandAnswerException extends Exception
{
	private final String[] lines;
	public CommandAnswerException()
	{
		this.lines = new String[] {};
	}
	public CommandAnswerException(String message)
	{
		this.lines = (message != null) ? new String[] { message } : new String[] {};
	}
	public CommandAnswerException(String[] messages)
	{
		final ArrayList<String> buffer = new ArrayList<>();
		if(messages != null)
			for(String message : messages)
				if(message != null)
					buffer.add(message);
		this.lines = buffer.toArray(new String[buffer.size()]);
	}
	public CommandAnswerException(List<String> messages)
	{
		final ArrayList<String> buffer = new ArrayList<>();
		if(messages != null)
			for(String message : messages)
				if(message != null)
					buffer.add(message);
		this.lines = buffer.toArray(new String[buffer.size()]);
	}
	public String[] getMessageArray()
	{
		return lines;
	}
}
