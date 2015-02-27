package ru.simsonic.rscUtilityLibrary.Bukkit.Commands;
import org.bukkit.command.CommandSender;

public abstract interface IFairyCommand
{
	public abstract void execute(CommandSender sender, FairyArgument[] args) throws CommandAnswerException;
}
