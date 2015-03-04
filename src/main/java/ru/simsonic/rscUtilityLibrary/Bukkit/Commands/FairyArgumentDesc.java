package ru.simsonic.rscUtilityLibrary.Bukkit.Commands;
import java.util.HashMap;
import org.bukkit.command.TabCompleter;

public class FairyArgumentDesc
{
	protected FairyArgumentType type;
	protected String            caption;
	protected boolean           required;
	protected String            hint;
	protected TabCompleter      completer;
	protected String            replacement;
	protected final HashMap<String, FairyCommandWrapper> subcommands = new HashMap<>();
	public FairyArgumentDesc(IFairyCommand[] subcommands)
	{
		type = FairyArgumentType.SUBCOMMAND;
		// this.subcommands = subcommands;
	}
	public FairyArgumentDesc(FairyArgumentType type)
	{
	}
	public FairyArgumentDesc(String label, IFairyCommand command, FairyArgumentDesc[] args)
	{
		final FairyCommandWrapper wrapper = new FairyCommandWrapper(label, command, args, null);
		subcommands.put(label, wrapper);
		type = FairyArgumentType.SUBCOMMAND;
	}
	public FairyArgumentDesc(FairyArgumentType type, boolean required)
	{
		this.type = type;
		this.required = required;
	}
	public FairyArgumentDesc setCaption(String caption)
	{
		this.caption = caption;
		return this;
	}
	public FairyArgumentDesc setRequired(boolean required)
	{
		this.required = required;
		return this;
	}
	public FairyArgumentDesc setTabCompleter(TabCompleter completer)
	{
		this.completer = completer;
		return this;
	}
	public FairyArgumentDesc setHint(String hint)
	{
		this.hint = hint;
		return this;
	}
}
