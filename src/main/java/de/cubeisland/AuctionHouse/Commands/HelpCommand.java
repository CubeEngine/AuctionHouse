package de.cubeisland.AuctionHouse.Commands;

import static de.cubeisland.AuctionHouse.Translation.Translator.t;
import de.cubeisland.AuctionHouse.AbstractCommand;
import de.cubeisland.AuctionHouse.BaseCommand;
import de.cubeisland.AuctionHouse.Perm;
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
        if (!Perm.get().check(sender,"auctionhouse.help")) return true;
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
