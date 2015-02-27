package ru.simsonic.rscUtilityLibrary.Bukkit.Commands;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class FairyArgument
{
	protected FairyArgumentDesc desc;
	protected FairyArgument(FairyArgumentDesc desc)
	{
		this.desc = desc;
	}
	protected FairyArgument[] parentArgs;
	protected boolean         present;
	protected String          keyword;
	protected Player          player;
	protected OfflinePlayer   offline;
	protected long            timeInterval;
	public FairyArgumentDesc getDescription()
	{
		return desc;
	}
	public FairyArgumentType getType()
	{
		return desc.type;
	}
	public boolean isPresent()
	{
		return present;
	}
	public FairyArgument[] getParentArgs()
	{
		return parentArgs;
	}
	public String getKeyword()
	{
		return keyword;
	}
	public Player getPlayer()
	{
		return player;
	}
	public OfflinePlayer getOfflinePlayer()
	{
		return offline;
	}
	public long getTimeInterval()
	{
		return timeInterval;
	}
}
