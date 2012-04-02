package de.paralleluniverse.Faithcaio.AuctionHouse;

import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Faithcaio
 */
public class AuctionItem {

    Bidder bidder;
    ItemStack item;
    long date;
    String owner;
    Double price;
    
    public AuctionItem (Auction auction)
    {
        this.bidder = auction.bids.peek().getBidder();
        this.item = auction.item;
        this.date = System.currentTimeMillis();
        this.owner = auction.owner.player.getName();
        this.price = auction.bids.peek().getAmount();
    }
    public AuctionItem (ItemStack item, Bidder bidder)
    {
        this.bidder = bidder;
        this.item = item;
        this.date = System.currentTimeMillis();
        this.owner = bidder.player.getName();
        this.price = 0.0;
    }
}
