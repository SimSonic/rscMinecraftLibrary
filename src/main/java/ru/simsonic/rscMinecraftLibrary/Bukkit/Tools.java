package ru.simsonic.rscMinecraftLibrary.Bukkit;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
}
