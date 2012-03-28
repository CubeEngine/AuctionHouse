package de.paralleluniverse.Faithcaio.AuctionHouse;

import org.bukkit.entity.Player;

/**
 * Represents a bid by a player
 *
 * @author Anselm
 */
public class Bid
{
    public final double amount;
    public final Player bidder;
    public final long timestamp;

    public Bid(Player bidder, double amount)
    {
       this.amount = amount;
       this.bidder = bidder;
       this.timestamp = System.currentTimeMillis();
    }
}

