package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import static de.paralleluniverse.Faithcaio.AuctionHouse.Translation.Translator.t;
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
        super(base, "help");
    }

    public boolean execute(CommandSender sender, String[] args)
    {
        if (!(sender.hasPermission("auctionhouse.help")))
        {
            sender.sendMessage(t("perm")+" "+t("help_perm"));
            return true;
        }
        sender.sendMessage(t("help_list"));
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
        return t("command_help");
    }
}
