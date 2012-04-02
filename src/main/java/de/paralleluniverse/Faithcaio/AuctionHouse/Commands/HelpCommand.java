package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import de.paralleluniverse.Faithcaio.AuctionHouse.AbstractCommand;
import de.paralleluniverse.Faithcaio.AuctionHouse.BaseCommand;
import org.bukkit.command.CommandSender;

/**
 * This command prints a help message
 *
 * @author Phillip Schichtel
 */
public class HelpCommand extends AbstractCommand
{

    public HelpCommand(BaseCommand base)
    {
        super("help", base);
    }


    public boolean execute(CommandSender sender, String[] args)
    {
        if (!(sender.hasPermission("auctionhouse.help")))
        {
            sender.sendMessage("You are not allowed to show AuctionHouse Help!");
            return true;
        }
        
        sender.sendMessage("AuctionHouse Commandlist:");
        sender.sendMessage("");

        for (AbstractCommand command : getBase().getRegisteredCommands())
        {
            sender.sendMessage(command.getUsage());
            sender.sendMessage("    " + command.getDescription());
            sender.sendMessage("");
        }

        return true;
    }

    @Override
    public String getDescription()
    {
        return "Prints this message.";
    }
}
