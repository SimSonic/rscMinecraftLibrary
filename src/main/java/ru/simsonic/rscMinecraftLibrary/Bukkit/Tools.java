package ru.simsonic.rscMinecraftLibrary.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public final class Tools
{
	public static Set<Player> getOnlinePlayers()
	{
		try
		{
			final Method method = Bukkit.class.getMethod("getOnlinePlayers", new Class[0]);
			if(method.getReturnType() == Collection.class)
			{
				final Collection<Player> players = (Collection<Player>)method.invoke(null, new Object[0]);
				return new HashSet<>(players);
			} else {
				final Player[] players = (Player[])method.invoke(null, new Object[0]);
				return new HashSet<>(Arrays.asList(players));
			}
		} catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
		}
		return Collections.emptySet();
	}
	public static ArrayList<String> getPluginWelcome(Plugin plugin, String extraString)
	{
		final ArrayList<String> result = new ArrayList<>();
		if(plugin != null)
		{
			final PluginDescriptionFile desc = plugin.getDescription();
			result.add("{_WH}" + desc.getName() + " v" + desc.getVersion() + " © " + desc.getAuthors().get(0));
			if(extraString != null)
				result.add(extraString);
			result.add("{_LB}{_U}" + desc.getWebsite());
		}
		return result;
	}
}
