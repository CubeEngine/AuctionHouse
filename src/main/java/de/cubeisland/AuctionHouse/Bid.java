package de.cubeisland.AuctionHouse;

/**
 * Represents a bid by a player
 *
 * @author Faithcaio
 */
public class Bid
{
    private final double amount;
    private final Bidder bidder;
    private final long timestamp;

    public Bid(Bidder bidder, double amount)
    {
        this.amount = amount;
        this.bidder = bidder;
        this.timestamp = System.currentTimeMillis();
    }

    public double getAmount()
    {
        return this.amount;
    }

    public Bidder getBidder()
    {
        return this.bidder;
    }

    public long getTimestamp()
    {
        return this.timestamp;
    }
}
