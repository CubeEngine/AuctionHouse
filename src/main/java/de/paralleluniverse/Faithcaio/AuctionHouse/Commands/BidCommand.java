package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import de.paralleluniverse.Faithcaio.AuctionHouse.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Faithcaio
 */
public class BidCommand extends AbstractCommand
{
    public BidCommand(BaseCommand base)
    {
        super("bid", base);
    }

    public boolean execute(CommandSender sender, String[] args)
    {
        if (args.length < 2)
        {
            sender.sendMessage("/ah bid <AuctionID> <BidAmount>");
            return false;
        }
        Arguments arguments = new Arguments(args);
        
        int id = arguments.getInt("1");
        if (id != -1)
        {
           double bidAmount = arguments.getDouble("2");
           if (bidAmount != -1)
           {
               Auction auction = AuctionManager.getInstance().getAuction(id);
               Bidder.getInstance((Player)sender).addAuction(auction);
               auction.bid((Player)sender, bidAmount);
               sender.sendMessage("You just bid "+String.valueOf(bidAmount)+
                                  " on "+auction.item.toString());
               if (auction.owner.isOnline())
               auction.owner.sendMessage("Somone bid on your auction #"+String.valueOf(auction.id)+"!");
               return true;
           }
        }
        return false;
    }

    @Override
    public String getDescription()
    {
        return "Bids on an auction.";
    }
}    

