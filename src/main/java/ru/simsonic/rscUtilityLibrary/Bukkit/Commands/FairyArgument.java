package ru.simsonic.rscUtilityLibrary.Bukkit.Commands;
import org.bukkit.entity.Player;

public class FairyArgument
{
	protected FairyArgumentDesc desc;
	protected FairyArgument(FairyArgumentDesc desc)
	{
		this.desc = desc;
	}
	protected boolean present;
	protected String  keyword;
	protected Player  player;
	protected long    timeInterval;
	protected FairyArgument[] parentArgs;
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
	public String getKeyword()
	{
		return keyword;
	}
	public Player getPlayer()
	{
		return player;
	}
	public long getTimeInterval()
	{
		return timeInterval;
	}
	public FairyArgument[] getParentArgs()
	{
		return parentArgs;
	}
}
