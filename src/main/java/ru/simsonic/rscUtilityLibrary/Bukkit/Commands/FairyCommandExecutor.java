package ru.simsonic.rscUtilityLibrary.Bukkit.Commands;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map.Entry;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class FairyCommandExecutor extends FairyCommandWrapper implements CommandExecutor
{
	private final JavaPlugin plugin;
	public FairyCommandExecutor(JavaPlugin plugin)
	{
		this.plugin = plugin;
	}
	private final HashMap<String, FairyCommandWrapper> topLevelCommands = new HashMap<>();
	protected FairyCommandWrapper wrapCommand(String label, IFairyCommand command, FairyArgumentDesc[] args)
	{
		final FairyCommandWrapper result = new FairyCommandWrapper(label, command, args, this);
		return result;
	}
	public IFairyCommand register(String label, IFairyCommand command, FairyArgumentDesc[] args)
	{
		final FairyCommandWrapper wrapper = wrapCommand(label, command, args);
  		topLevelCommands.put(label, wrapper);
		// TO DO HERE
		plugin.getCommand(label).setExecutor(this);
		return command;
	}
	/*
	КУРИТЬ ЭТИ ТЕМЫ И ИСКАТЬ ДРУГИЕ :)
	* https://bukkit.org/threads/register-command-without-plugin-yml.112932/
	* https://github.com/Goblom/Bukkit-Libraries/blob/master/src/main/java/command/AbstractCommand.java
	* https://bukkit.org/threads/how-to-get-commandmap-with-default-commands.122746/
	* http://bukkit.org/threads/resource-abstractcommand-add-commands-without-accessing-your-plugin-yml.195990/
	*/
	public void onEnable()
	{
		// REGISTER ALL COMMANDS
		final CommandMap commandMap = getCommandMap();
		if(commandMap != null)
			for(FairyCommandWrapper wrapper : topLevelCommands.values())
				wrapper.register(commandMap);
	}
	public void onDisable()
	{
		// UNREGISTER ALL COMMANDS
		final CommandMap commandMap = getCommandMap();
		if(commandMap != null)
			for(FairyCommandWrapper wrapper : topLevelCommands.values())
				wrapper.unregister(commandMap);
	}
	private CommandMap getCommandMap()
	{
		try
		{
			final Server server = plugin.getServer();
			final Field field = server.getClass().getDeclaredField("commandMap");
			final boolean accessible = field.isAccessible();
			field.setAccessible(true);
			final CommandMap result = (CommandMap)field.get(server);
			field.setAccessible(accessible);
			return result;
		} catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
		}
		return null;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(command instanceof FairyCommandWrapper)
		{
			final FairyCommandWrapper wrapper = (FairyCommandWrapper)command;
			return wrapper.execute(sender, label, args);
		}
		for(Entry<String, FairyCommandWrapper> entry : topLevelCommands.entrySet())
			if(entry.getKey().equals(label))
				return entry.getValue().execute(sender, label, args);
		return false;
	}
}
