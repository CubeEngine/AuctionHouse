package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import de.paralleluniverse.Faithcaio.AuctionHouse.AbstractCommand;
import de.paralleluniverse.Faithcaio.AuctionHouse.Arguments;
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
            return true;
        }
        Arguments arguments = new Arguments(args);
        if (!(sender.hasPermission("auctionhouse.info")))
        {
            sender.sendMessage("You do not have Permission to get Info about Auctions!");
            return true;
        }
        
        if (arguments.getString("1").equalsIgnoreCase("Bids"))//bidding
        {
            if (AuctionHouse.debugMode) sender.sendMessage("Debug: Bids");
            List<Auction> auctions = Bidder.getInstance((Player)sender).getAuctions();
            int max = auctions.size();
            if (AuctionHouse.debugMode) sender.sendMessage("Debug: max: "+max);
            if (max == 0) sender.sendMessage("Info: No Bids yet!"); 
            for (int i=0;i<max;++i)
            {
                Auction auction = auctions.get(i);
                if (auction.owner != (Player)sender)
                   this.sendInfo(sender, auction);    
            }
        }
        else
        {
            if (arguments.getString("1").equalsIgnoreCase("Auctions"))//own auctions
            {
                if (AuctionHouse.debugMode) sender.sendMessage("Debug: own Auctions");
                List<Auction> auctions = Bidder.getInstance((Player)sender).getAuctions(Bidder.getInstance((Player)sender));
                int max = auctions.size();
                if (AuctionHouse.debugMode) sender.sendMessage("Debug: max: "+max);
                if (max == 0) sender.sendMessage("Info: No own Auctions started!"); 
                for (int i=0;i<max;++i)
                {
                    Auction auction = auctions.get(i);
                    this.sendInfo(sender, auction);
                }
            }
            else
            {

                if (arguments.getString("1").equalsIgnoreCase("Leading"))
                {
                    if (AuctionHouse.debugMode) sender.sendMessage("Debug: Leading Auctions");
                    List<Auction> auctions = Bidder.getInstance((Player)sender).getLeadingAuctions(Bidder.getInstance((Player)sender));
                    int max = auctions.size();
                    if (AuctionHouse.debugMode) sender.sendMessage("Debug: max: "+max);
                    if (max == 0) sender.sendMessage("Info: No Leading Auctions!"); 
                    for (int i=0;i<max;++i)
                    {
                        Auction auction = auctions.get(i);
                        this.sendInfo(sender, auction);
                    }
                }
                else
                {
                    Integer id = arguments.getInt("1");
                    if (id != null)
                    {
                        if (AuctionHouse.debugMode) sender.sendMessage("Debug: Id Auction");
                        if (AuctionManager.getInstance().getAuction(id) !=null)
                            this.sendInfo(sender, AuctionManager.getInstance().getAuction(id));
                        else
                            sender.sendMessage("Info: Auction #"+id+" does not exist!");
                    }
                    else
                    {
                        if (!(sender.hasPermission("auctionhouse.info.others")))
                        {
                            sender.sendMessage("You do not have Permission to get Info about other Players Auctions!");
                            return true;
                        }
                        Bidder player = arguments.getBidder("1");
                        if (player != null)
                        {
                            if (AuctionHouse.debugMode) sender.sendMessage("Debug: Player Auction");
                            List<Auction> auctions = player.getAuctions(player);
                            int max = auctions.size();
                            if (AuctionHouse.debugMode) sender.sendMessage("Debug: max: "+max);
                            if (max == 0) sender.sendMessage("Info: "+player.player.getName()+" has no Auctions!"); 
                            for (int i=0;i<max;++i)
                            {
                                Auction auction = auctions.get(i);
                                this.sendInfo(sender, auction);
                            }
                        }
                        else
                        {
                            sender.sendMessage("Info: Player \""+arguments.getString("1")+"\" does not exist or has no Auction!");
                        }
                    }
                }
            }
        }
        return true;
    }
    
    public void sendInfo(CommandSender sender,Auction auction)
    {
     sender.sendMessage("#"+auction.id+": "+auction.item.toString()+
                                   " Leading Bidder: "+auction.bids.peek().getBidder().player.getName()+
                                   "with "+auction.bids.peek().getAmount()+
                                   "Auction ends: "+
                                   DateFormatUtils.format(auction.auctionEnd, AuctionHouse.getInstance().getConfigurations().auction_timeFormat)
                                  );       
    }
    @Override
    public String getUsage()
    {
        return "/ah info <<AuctionId>|<Player>|<Bids>|<Leading>|<Auctions>>";
    }
    public String getDescription()
    {
        return "Provides Info for Auctions.";
    }
}    
