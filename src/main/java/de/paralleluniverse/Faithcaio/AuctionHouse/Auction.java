package de.paralleluniverse.Faithcaio.AuctionHouse;

import java.util.Stack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an auction
 *
 * @author Faithcaio
 */
public class Auction
{
    public int id;
    public final ItemStack item;
    public final Player owner;
    public final long auctionEnd;
    public final Stack<Bid> bids;
    private static final AuctionHouse plugin = AuctionHouse.getInstance();
    private static final AuctionHouseConfiguration config = plugin.getConfigurations();
    
    public Auction(ItemStack item, Player owner, long auctionEnd, double startBid)
    {
        this.id = 0;
        this.item = item;
        this.owner = owner;
        this.auctionEnd = auctionEnd;
        this.bids = new Stack<Bid>();
        this.bids.push(new Bid(owner, startBid));
    }
    
    //Abort Auction
    public boolean abortAuction()
    {
        //Rechte zum Abbrechen ?
        //return false;
        //
        while (!(this.bids.isEmpty()))
        {
            //TODO Geld zurückgeben & Meldung wenn online sonst per mail (Essentials)?
            this.bids.pop();
        }
        return true;
    }
    
    public boolean bid(final Player bidder, final double amount)//evtl nicht bool / bessere Unterscheidung
    {
        if (amount <= 0) { return false;}         //Bid cannot be 0 or less !
        if (amount < this.bids.peek().getAmount())//Bid is too low !
        {
            return false;
        }
        //Ueberbotener bekommt Geld zurück?
        this.bids.push(new Bid(bidder, amount));
        return true;
    }
    public boolean undobid(final Player bidder)//evtl nicht bool / bessere Unterscheidung
    {
        //is last bidder?
        if (bidder != this.bids.peek().getBidder()) 
          { return false; }
        
        //calculate UndoTime from config
        long undoTime = config.auction_undoTime / 1000;
        if (config.auction_undoTime < 0) //Infinite UndoTime
          { undoTime = this.auctionEnd - this.bids.peek().getTimestamp(); }
        
        //undoTime ok?
        if ((System.currentTimeMillis() - this.bids.peek().getTimestamp()) < undoTime)
          { return false; }
        //else: Undo Last Bid
        this.bids.pop();
        return true;
    }
}
