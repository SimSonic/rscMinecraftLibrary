package ru.simsonic.rscMinecraftLibrary.Bukkit;

import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class LocationReachDetector extends MovingPlayersCatcher implements Runnable
{
	public static class NamedLocation extends Location
	{
		final String caption;
		final double minDistanceSq;
		public NamedLocation(Location target, double minDistance, String caption)
		{
			super(target.getWorld(), target.getX(), target.getY(), target.getZ());
			this.minDistanceSq = minDistance * minDistance;
			this.caption = caption;
		}
		public String getCaption()
		{
			return caption;
		}
	}
	public static class PlayerReachedPointEvent extends PlayerEvent
	{
		private static final HandlerList handlers = new HandlerList();
		private final NamedLocation target;
		public PlayerReachedPointEvent(Player player, NamedLocation target)
		{
			super(player);
			this.target = target;
		}
		public NamedLocation getNamedLocation()
		{
			return this.target;
		}
		@Override
		public HandlerList getHandlers()
		{
			return handlers;
		}
		public static HandlerList getHandlerList()
		{
			return handlers;
		}
	}
	private final JavaPlugin plugin;
	private final HashSet<NamedLocation> targets = new HashSet<>();
	private BukkitTask scheduledTask = null;
	public LocationReachDetector(JavaPlugin plugin)
	{
		this.plugin = plugin;
	}
	public void onEnable()
	{
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		scheduledTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this, 0, 1);
	}
	public void onDisable()
	{
		plugin.getServer().getServicesManager().unregister(this);
		if(scheduledTask != null)
		{
			scheduledTask.cancel();
			scheduledTask = null;
		}
	}
	public synchronized boolean add(NamedLocation namedLocation)
	{
		return targets.add(namedLocation);
	}
	public synchronized boolean contains(NamedLocation namedLocation)
	{
		return targets.contains(namedLocation);
	}
	public synchronized boolean remove(NamedLocation namedLocation)
	{
		return targets.remove(namedLocation);
	}
	@Override
	public void run()
	{
		for(Player player : super.getMovedPlayersAsync())
		{
			final Location playerLoc = player.getLocation();
			for(NamedLocation nl : targets)
				if(nl.getWorld().equals(playerLoc.getWorld()) && nl.distanceSquared(playerLoc) < nl.minDistanceSq)
					plugin.getServer().getPluginManager().callEvent(new PlayerReachedPointEvent(player, nl));
		}
	}
}
