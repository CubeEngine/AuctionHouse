package de.paralleluniverse.Faithcaio.AuctionHouse;

import org.bukkit.entity.Player;

/**
 * Represents a bid by a player
 *
 * @author Faithcaio
 */
public class Bid
{
    private final double amount;
    private final Player bidder;
    private final long timestamp;

    public Bid(Player bidder, double amount)
    {
       this.amount = amount;
       this.bidder = bidder;
       this.timestamp = System.currentTimeMillis();
    }
    
    public double getAmount()
    {
        return this.amount;
    }
    
    public Player getBidder()
    {
        return this.bidder;
    }
    
    public long getTimestamp()
    {
        return this.timestamp;
    }
}

