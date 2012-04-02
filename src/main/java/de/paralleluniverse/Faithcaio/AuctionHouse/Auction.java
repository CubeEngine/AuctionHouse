package de.paralleluniverse.Faithcaio.AuctionHouse;

import java.util.Stack;
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
    public final Bidder owner;
    public final long auctionEnd;
    public final Stack<Bid> bids;
    private static final AuctionHouse plugin = AuctionHouse.getInstance();
    private static final AuctionHouseConfiguration config = plugin.getConfigurations();
    
    public Auction(ItemStack item, Bidder owner, long auctionEnd, double startBid)
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
    
    public boolean bid(final Bidder bidder, final double amount)//evtl nicht bool / bessere Unterscheidung
    {
        if (amount <= 0) 
        {
            bidder.player.getPlayer().sendMessage("Error: Bid must be greater than 0!");
            return false;
        }
        if (amount <= this.bids.peek().getAmount())
        {
            bidder.player.getPlayer().sendMessage("Info: Bid is too low!");
            return false;
        }
        if ((AuctionHouse.getInstance().getEconomy().getBalance(bidder.player.getName())>=amount)
           ||bidder.player.getPlayer().hasPermission("auctionhouse.use.bid.infinite"))
        {
            if (AuctionHouse.getInstance().getEconomy().getBalance(bidder.player.getName()) - bidder.getTotalBidAmount() >= amount
              ||bidder.player.getPlayer().hasPermission("auctionhouse.use.bid.infinite"))
            {
                this.bids.push(new Bid(bidder, amount));
                return true;
            }//TODO für später   getEconomy().withdrawPlayer(player.getName(), price).transactionSuccess()
            bidder.player.getPlayer().sendMessage("Error: You already bid too much. You would not have enough money to buy everything.");
            return false;
        }
        bidder.player.getPlayer().sendMessage("Error: Not enough money");
        return false;
        
    }
    
    public boolean undobid(final Bidder bidder)
    {
        AuctionHouse.debug("UndoBid Checking...");
        if (bidder != this.bids.peek().getBidder()) 
            return false;
        AuctionHouse.debug("LastBidder OK");
        if (bidder == this.owner)
            return false;
        AuctionHouse.debug("NoOwner OK");
        
        //calculate UndoTime from config
        long undoTime = config.auction_undoTime;
        if (undoTime < 0) //Infinite UndoTime
          { undoTime = this.auctionEnd - this.bids.peek().getTimestamp(); }
        if ((System.currentTimeMillis() - this.bids.peek().getTimestamp()) < undoTime)
            return false;
        //else: Undo Last Bid
        this.bids.pop();
        return true;
    }
}
