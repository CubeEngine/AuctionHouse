package de.paralleluniverse.Faithcaio.AuctionHouse;

import java.util.Stack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an auction
 *
 * @author Anselm
 */
public class Auction
{
    public final int id;
    public final ItemStack item;
    public final Player owner;
    public final long auctionEnd;
    public final Stack<Bid> bids;
    private static final AuctionHouse plugin = AuctionHouse.getInstance();
    private static final AuctionHouseConfiguration config = plugin.getConfigurations();
    
    public Auction(int id, ItemStack item, Player owner, long auctionEnd)
    {
        this.id = id;
        this.item = item;
        this.owner = owner;
        this.auctionEnd = auctionEnd;
        this.bids = new Stack<Bid>();
    }
    
    public boolean bid(final Player bidder, final double amount)
    {
        if (amount < this.bids.peek().getAmount())
        {
            return false;
        }
        this.bids.push(new Bid(bidder, amount));
        return true;
    }
    public boolean undobid(final Player bidder)
    {
        //is last bidder?
        if (bidder != this.bids.peek().getBidder()) 
          { return false; }
        
        //calculate UndoTime from config
        long undoTime = config.auction_UndoTimer / 1000;
        if (config.auction_UndoTimer < 0) //Infinite UndoTime
          { undoTime = this.auctionEnd - this.bids.peek().getTimestamp(); }
        
        //undoTime ok?
        if ((System.currentTimeMillis() - this.bids.peek().getTimestamp()) < undoTime)
          { return false; }
        //else: Undo Last Bid
        this.bids.pop();
        return true;
    }
}
