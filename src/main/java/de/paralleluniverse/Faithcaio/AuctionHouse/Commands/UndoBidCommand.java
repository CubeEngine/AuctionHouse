package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import de.paralleluniverse.Faithcaio.AuctionHouse.AbstractCommand;
import de.paralleluniverse.Faithcaio.AuctionHouse.Arguments;
import de.paralleluniverse.Faithcaio.AuctionHouse.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Faithcaio
 */
public class UndoBidCommand extends AbstractCommand
{
    public UndoBidCommand(BaseCommand base)
    {
        super("undoBid", base);
    }
 
    public boolean execute(CommandSender sender, String[] args)
    {
        if (args.length < 1)
        {
            sender.sendMessage("/ah undoBid last");
            sender.sendMessage("/ah undoBid <AuctionID>");
            return false;
        }
        Arguments arguments = new Arguments(args);
        Player psender = (Player)sender;
        if (arguments.getString("1").equals("last"))
        {
            if (Bidder.getInstance(psender).getlastAuction(psender).undobid(psender))
            {
                sender.sendMessage("Info: Bid on last Auction redeemed");
                return true;
            }
        }
        if (arguments.getInt("1")!=null)
        {
            if (AuctionManager.getInstance().getAuction(arguments.getInt("1")).undobid(psender))
            {    
                sender.sendMessage("Info: Bid on Auction redeemed");
                return true;
            }
        }
        sender.sendMessage("Info: Couldn't undo Bid!");
        return false;
    }

    @Override
    public String getUsage()
    {
        return "/ah undoBid <last|<AuctionId>>";
    }
    public String getDescription()
    {
        return "Removes an auction.";
    }
}