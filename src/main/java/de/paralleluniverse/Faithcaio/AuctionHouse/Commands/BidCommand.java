package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import de.paralleluniverse.Faithcaio.AuctionHouse.AbstractCommand;
import de.paralleluniverse.Faithcaio.AuctionHouse.Arguments;
import de.paralleluniverse.Faithcaio.AuctionHouse.*;
import java.util.List;
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
        
        Double bidAmount;
        Auction auction;        
        if (args.length < 2)
        {
            sender.sendMessage("/ah bid <AuctionID> <BidAmount>");
            sender.sendMessage("/ah bid i:<Item> [q:<Quantity>]<BidAmount>");//bid on the cheapest Item found
            //TODO mit quantity 
            return false;
        }
        Arguments arguments = new Arguments(args);
        if (arguments.getString("i") == null) return false;
        if (arguments.getMaterial("i") != null)
        {
            if (AuctionHouse.debugMode) sender.sendMessage("Debug: Bid on Material");
            List<Auction> auctionlist = AuctionManager.getInstance().getAuctionItems(arguments.getMaterial("i"));
            AuctionSort sorter = new AuctionSort();
            sorter.SortAuction(auctionlist, "price");   
            if (auctionlist.size()==0)
            {
                sender.sendMessage("Info: No Auctions with "+arguments.getMaterial("i").toString());
                return true;
            }    
            auction = auctionlist.get(0);//First is Cheapest after Sort
            bidAmount = arguments.getDouble("1");    
            if (bidAmount != null)
            {
                if (AuctionHouse.debugMode) sender.sendMessage("Debug: BidAmount Set");
                if (auction.bid((Player)sender, bidAmount))
                {
                    if (AuctionHouse.debugMode) sender.sendMessage("Debug: Item Bid OK");
                    Bidder.getInstance((Player)sender).addAuction(auction);
                    this.SendInfo(auction, sender);
                    return true;
                }
                sender.sendMessage("Info: Bid is too low!");
                return true;
            }
        }
        else if (arguments.getString("i")!=null) return false;
        Integer id = arguments.getInt("1");
        if (id != null)
        {
            if (AuctionHouse.debugMode) sender.sendMessage("Debug: Bid on ID");
            bidAmount = arguments.getDouble("2");    
            if (bidAmount != null)
            {
                if (AuctionHouse.debugMode) sender.sendMessage("Debug: BidAmount Set");
                    
                if (AuctionManager.getInstance().getAuction(id)==null)
                {
                    sender.sendMessage("Info: Auction"+String.valueOf(id)+"does not exist!");
                    return true;
                }
                auction = AuctionManager.getInstance().getAuction(id);
                
                if (auction.bid((Player)sender, bidAmount))
                {
                    if (AuctionHouse.debugMode) sender.sendMessage("Debug: Id Bid OK");
                    Bidder.getInstance((Player)sender).addAuction(auction);
                    this.SendInfo(auction, sender);
                    return true;
                }
                sender.sendMessage("Info: Bid is too low!");
                return true;
            }
        }
        return false;
    }
    
    public void SendInfo (Auction auction,CommandSender sender)
    {
    sender.sendMessage("You just bid "+String.valueOf(auction.bids.peek().getAmount())+
                       " on "+auction.item.toString());
    if (auction.owner.isOnline())
        auction.owner.sendMessage("Somone bid on your auction #"+String.valueOf(auction.id)+"!");    
    }

    @Override
    public String getUsage()
    {
        return "/ah bid <AuctionID> <BidAmount>";
        //TODO Bid on cheapest Item...
    }
    public String getDescription()
    {
        return "Bids on an auction.";
    }
}    

