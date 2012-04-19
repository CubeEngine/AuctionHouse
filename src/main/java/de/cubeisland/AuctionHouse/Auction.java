package de.cubeisland.AuctionHouse;

import static de.cubeisland.AuctionHouse.AuctionHouse.t;
import java.sql.Timestamp;
import java.util.Stack;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an auction
 *
 * @author Faithcaio
 */
public class Auction
{
    private int id;
    private final ItemStack item;
    private final Bidder owner;
    private final long auctionEnd;
    private final Stack<Bid> bids;
    private static final AuctionHouse plugin = AuctionHouse.getInstance();
    private static final AuctionHouseConfiguration config = plugin.getConfigurations();
    
    private final Database db;

    public Auction(ItemStack item, Bidder owner, long auctionEnd, double startBid)
    {
        this.db = AuctionHouse.getInstance().getDB();
        this.id = Manager.getInstance().getFreeIds().pop();
        this.item = item;
        this.owner = owner;
        this.auctionEnd = auctionEnd;
        this.bids = new Stack<Bid>();
        this.bids.push(new Bid(owner, startBid, this));
        
        db.exec(  
            "INSERT INTO `auctions` ("+
            "`id` ,"+
            "`ownerid` ,"+
            "`item` ,"+
            "`amount` ,"+
            "`timestamp`"+
            ")"+
            "VALUES (?, ?, ?, ?, ?)"
        ,this.id, owner.getId(), MyUtil.convertItem(item), item.getAmount(), new Timestamp(auctionEnd));
    }

    //Override: load in Auction from DataBase
    public Auction(int id,ItemStack item, Bidder owner, long auctionEnd)
    {
        Manager.getInstance().getFreeIds().removeElement(id);
        this.db = AuctionHouse.getInstance().getDB();
        this.id = id;
        this.item = item;
        this.owner = owner;
        this.auctionEnd = auctionEnd;
        this.bids = new Stack<Bid>();
    }
    
    public boolean bid(final Bidder bidder, final double amount)//evtl nicht bool / bessere Unterscheidung
    {
        if (amount <= 0)
        {
            bidder.getPlayer().sendMessage(t("e")+" "+t("auc_bid_low1"));
            return false;
        }
        if (amount <= this.bids.peek().getAmount())
        {
            bidder.getPlayer().sendMessage(t("i")+" "+t("auc_bid_low2"));
            return false;
        }
        if ((AuctionHouse.getInstance().getEconomy().getBalance(bidder.getName()) >= amount)
                || Perm.get().check(bidder,"auctionhouse.use.bid.infinite"))
        {
            if (AuctionHouse.getInstance().getEconomy().getBalance(bidder.getName()) - bidder.getTotalBidAmount() >= amount
                    || Perm.get().check(bidder,"auctionhouse.use.bid.infinite"))
            {
                this.bids.push(new Bid(bidder, amount, this));
                return true;
            }
            bidder.getPlayer().sendMessage(t("e")+" "+t("auc_bid_money1"));
            return false;
        }
        bidder.getPlayer().sendMessage(t("e")+" "+t("auc_bid_money2"));
        return false;
    }

    public boolean undobid(final Bidder bidder)
    {
        if (bidder != this.bids.peek().getBidder())
        {
            return false;
        }
        if (bidder == this.owner)
        {
            return false;
        }
        long undoTime = config.auction_undoTime;
        if (undoTime < 0) //Infinite UndoTime
        {
            undoTime = this.auctionEnd - this.bids.peek().getTimestamp();
        }
        if ((System.currentTimeMillis() - this.bids.peek().getTimestamp()) < undoTime)
        {
            return false;
        }
        //else: Undo Last Bid
        
        //Single Bid delete
        db.exec("DELETE FROM `bids` WHERE `bidderid`=? && `auctionid`=? && `timestamp`=?"
                      ,bidder.getId(), this.id, this.bids.peek().getTimestamp());
        this.bids.pop();
        return true;
    }
    
    public int getId()
    {
       return this.id; 
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public ItemStack getItem()
    {
        return this.item;
    }    
    
    public Bidder getOwner()
    {
        return this.owner;
    }
    
    public long getAuctionEnd()
    {
        return this.auctionEnd;
    }
    
    public Stack<Bid> getBids()
    {
        return this.bids;
    }
}