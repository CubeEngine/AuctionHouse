package de.cubeisland.AuctionHouse.Commands;

import de.cubeisland.AuctionHouse.Perm;
import de.cubeisland.AuctionHouse.BaseCommand;
import de.cubeisland.AuctionHouse.AbstractCommand;
import de.cubeisland.AuctionHouse.AuctionHouse;
import de.cubeisland.AuctionHouse.Bidder;
import static de.cubeisland.AuctionHouse.Translation.Translator.t;
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
        if (!Perm.get().check(sender,"auctionhouse.getItems.command")) return true;
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
