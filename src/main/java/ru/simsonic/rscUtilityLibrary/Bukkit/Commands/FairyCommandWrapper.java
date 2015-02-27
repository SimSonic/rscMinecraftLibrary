package ru.simsonic.rscUtilityLibrary.Bukkit.Commands;
import java.util.ArrayList;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import ru.simsonic.rscUtilityLibrary.TextProcessing.GenericChatCodes;

class FairyCommandWrapper extends BukkitCommand
{
	protected FairyCommandWrapper  parent;
	protected IFairyCommand        target;
	protected FairyArgumentDesc[]  argumentList;
	protected FairyCommandExecutor executor;
	protected FairyCommandWrapper(String name, IFairyCommand target, FairyArgumentDesc[] argumentList, FairyCommandExecutor executor)
	{
		super(name);
		this.target = target;
		this.argumentList = argumentList;
		this.executor = executor;
	}
	@Override
	public boolean execute(CommandSender sender, String label, String[] args)
	{
		final ArrayList<FairyArgument> list = new ArrayList<>();
		try
		{
			target.execute(sender, list.toArray(new FairyArgument[list.size()]));
		} catch(CommandAnswerException ex) {
			for(String answer : ex.getMessageArray())
				sender.sendMessage(GenericChatCodes.processStringStatic(answer));
		}
		return true;
	}
}
