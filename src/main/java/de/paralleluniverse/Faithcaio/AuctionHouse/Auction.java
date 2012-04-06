package de.paralleluniverse.Faithcaio.AuctionHouse;

import static de.paralleluniverse.Faithcaio.AuctionHouse.Translation.Translator.t;
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

    public boolean abortAuction()
    {
        while (!(this.bids.isEmpty()))
        {
            this.bids.pop();
        }
        return true;
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
                this.bids.push(new Bid(bidder, amount));
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
        this.bids.pop();
        return true;
    }
}