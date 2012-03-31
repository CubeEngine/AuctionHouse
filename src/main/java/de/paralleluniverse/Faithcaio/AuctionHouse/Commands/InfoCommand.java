package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import de.paralleluniverse.Faithcaio.AuctionHouse.*;
import java.util.List;
import org.apache.commons.lang.time.DateFormatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Faithcaio
 */
public class InfoCommand extends AbstractCommand
{    
    public InfoCommand(BaseCommand base)
    {
        super("info", base);
    }

    public boolean execute(CommandSender sender, String[] args)
    {
        if (args.length < 1)
        {
            sender.sendMessage("/ah info <AuctionID>");
            sender.sendMessage("/ah info <Player>");
            sender.sendMessage("/ah info Bids");
            sender.sendMessage("/ah info Leading");
            sender.sendMessage("/ah info Auctions");
            return false;
        }
        Arguments arguments = new Arguments(args);
        
        if (arguments.getString("1").equalsIgnoreCase("Bids"))//bidding
        {
            List<Auction> auctions = Bidder.getInstance((Player)sender).getAuctions();
            int max = auctions.size();
            for (int i=0;i>max;++i)
            {
                Auction auction = auctions.get(i);
                if (auction.owner != (Player)sender)
                   this.TextOut(sender, auction);    
            }
        }
        
        if (arguments.getString("1").equalsIgnoreCase("Auctions"))//own auctions
        {
            List<Auction> auctions = Bidder.getInstance((Player)sender).getAuctions((Player)sender);
            int max = auctions.size();
            for (int i=0;i>max;++i)
            {
                Auction auction = auctions.get(i);
                this.TextOut(sender, auction);
            }
        }
        
        if (arguments.getString("1").equalsIgnoreCase("Leading"))
        {
            List<Auction> auctions = Bidder.getInstance((Player)sender).getLeadingAuctions((Player)sender);
            int max = auctions.size();
            for (int i=0;i>max;++i)
            {
                Auction auction = auctions.get(i);
                this.TextOut(sender, auction);
            }
        }
        
        int id = arguments.getInt("1");
        if (id != -1)
        {
           this.TextOut(sender, AuctionManager.getInstance().getAuction(id));        
        }
        
        Player player = arguments.getPlayer("1");
        if (player != null)
        {
            List<Auction> auctions = Bidder.getInstance(player).getAuctions(player);
            int max = auctions.size();
            for (int i=0;i>max;++i)
            {
                Auction auction = auctions.get(i);
                this.TextOut(sender, auction);
            }       
        }

        return true;
    }
    
    public void TextOut(CommandSender sender,Auction auction)
    {
     sender.sendMessage("#"+auction.id+": "+auction.item.toString()+
                                   " Leading Bidder: "+auction.bids.peek().getBidder().toString()+
                                   "with "+String.valueOf(auction.bids.peek().getAmount())+
                                   "Auction ends: "+DateFormatUtils.format(auction.auctionEnd, "dd/MM/yy HH:mm")
                                  );       
    }

    @Override
    public String getDescription()
    {
        return "Removes an auction.";
    }
}    
