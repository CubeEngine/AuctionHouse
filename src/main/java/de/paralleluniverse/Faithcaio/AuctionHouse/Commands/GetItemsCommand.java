package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import de.paralleluniverse.Faithcaio.AuctionHouse.AbstractCommand;
import de.paralleluniverse.Faithcaio.AuctionHouse.BaseCommand;
import de.paralleluniverse.Faithcaio.AuctionHouse.Bidder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Faithcaio
 */
public class GetItemsCommand extends AbstractCommand{

    public GetItemsCommand (BaseCommand base)
    {
        super("getItems",base);
    }
    public boolean execute(CommandSender sender, String[] args)
    {
        if (!(sender.hasPermission("auctionhouse.getItems.command")))
        {
            sender.sendMessage("You do not have Permission to use the GetItems Command! Use the sign instead!");
            //TODO Use sign
            return true;
        }
        Bidder.getInstance((Player)sender).itemContainer.giveNextItem();
        return true;
    }
    @Override
    public String getUsage()
    {
        return "/ah getItems";
    }
    public String getDescription()
    {
        return "Gives you the next Item from your ItemContainer.";
    }
}
