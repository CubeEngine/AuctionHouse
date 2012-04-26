package de.cubeisland.AuctionHouse.Commands;

import de.cubeisland.AuctionHouse.AbstractCommand;
import de.cubeisland.AuctionHouse.Auction.Bidder;
import de.cubeisland.AuctionHouse.AuctionHouse;
import static de.cubeisland.AuctionHouse.AuctionHouse.t;
import de.cubeisland.AuctionHouse.BaseCommand;
import de.cubeisland.AuctionHouse.CommandArgs;
import de.cubeisland.AuctionHouse.Perm;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Gives you the next Item from your auctionbox
 * 
 * @author Faithcaio
 */
public class GetItemsCommand extends AbstractCommand
{
    public GetItemsCommand(BaseCommand base)
    {
        super(base, "getitems", "get");
    }

    public boolean execute(CommandSender sender, CommandArgs args)
    {
        if (!Perm.command_getItems.check(sender)) return true;
        if (sender instanceof ConsoleCommandSender)
        {
            AuctionHouse.log("Console can not receive Items");
            return true;
        }
                
        if (!(Bidder.getInstance((Player) sender).getBox().giveNextItem()))
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
