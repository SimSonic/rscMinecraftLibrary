package ru.simsonic.rscUtilityLibrary.BukkitListeners;
import java.util.HashSet;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class MovingPlayersCatcher implements Listener
{
	private final HashSet<Player> movedPlayers = new HashSet<>();
	public synchronized HashSet<Player> getMovedPlayersAsync()
	{
		HashSet<Player> result = new HashSet<>(movedPlayers);
		movedPlayers.clear();
		return result;
	}
	@org.bukkit.event.EventHandler
	protected synchronized void onPlayerJoin(PlayerJoinEvent event)
	{
		movedPlayers.add(event.getPlayer());
	}
	@org.bukkit.event.EventHandler
	protected synchronized void onPlayerMove(PlayerMoveEvent event)
	{
		movedPlayers.add(event.getPlayer());
	}
	@org.bukkit.event.EventHandler
	protected synchronized void onPlayerTeleport(PlayerTeleportEvent event)
	{
		movedPlayers.add(event.getPlayer());
	}
	@org.bukkit.event.EventHandler
	protected synchronized void onPlayerChangedWorld(PlayerChangedWorldEvent event)
	{
		movedPlayers.add(event.getPlayer());
	}
}
