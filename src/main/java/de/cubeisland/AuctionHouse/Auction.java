package de.cubeisland.AuctionHouse;

import static de.cubeisland.AuctionHouse.Translation.Translator.t;
import java.util.Stack;
import org.bukkit.enchantments.Enchantment;
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
        this.id = Manager.getInstance().freeIds.peek();
        Manager.getInstance().freeIds.pop();
        this.item = item;
        this.owner = owner;
        this.auctionEnd = auctionEnd;
        this.bids = new Stack<Bid>();
        this.bids.push(new Bid(owner, startBid, this));
        
        Database data = AuctionHouse.getInstance().database;
        data.query(  
            "INSERT INTO `auctions` ("+
            "`id` ,"+
            "`ownerid` ,"+
            "`item` ,"+
            "`amount`"+
            "`timestamp`"+
            ")"+
            "VALUES ("+
            "?, ?, ?, ?, ?"
        ,this.id,owner.id,MyUtil.get().convertItem(item),item.getAmount(),auctionEnd);
        
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
        
        Database data = AuctionHouse.getInstance().database;
        //Single Bid delete
        data.query("DELETE FROM `bids` WHERE `bidderid`=? && `auctionid`=? && `timestamp`=?"
                      ,bidder.id,this.id,this.bids.peek().getTimestamp());
        this.bids.pop();
        return true;
    }
}