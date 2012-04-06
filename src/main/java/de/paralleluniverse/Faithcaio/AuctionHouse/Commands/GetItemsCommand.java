package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import de.paralleluniverse.Faithcaio.AuctionHouse.*;
import static de.paralleluniverse.Faithcaio.AuctionHouse.Translation.Translator.t;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Faithcaio
 */
public class GetItemsCommand extends AbstractCommand
{
    public GetItemsCommand(BaseCommand base)
    {
        super(base, "getitems", "get");
    }

    public boolean execute(CommandSender sender, String[] args)
    {
        if (!(sender.hasPermission("auctionhouse.getItems.command")))
        {
            sender.sendMessage(t("perm")+" "+t("get_perm"));
            return true;
        }
        if (sender instanceof ConsoleCommandSender)
        {
            AuctionHouse.log("Console can not receive Items");
            return true;
        }
                
        
        if (!(Bidder.getInstance((Player) sender).getContainer().giveNextItem()))
        {
            sender.sendMessage(t("get_empty"));
        }
        return true;
    }

    public String getDescription()
    {
        return t("command_get");
    }
}
