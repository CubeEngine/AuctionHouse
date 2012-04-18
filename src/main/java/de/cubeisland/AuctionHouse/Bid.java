package de.cubeisland.AuctionHouse;

import java.sql.ResultSet;
import java.sql.SQLException;

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
    public int id;

    public Bid(Bidder bidder, double amount, Auction auction)
    {
        this.amount = amount;
        this.bidder = bidder;
        this.timestamp = System.currentTimeMillis();
        this.id = -1;
        Database data = AuctionHouse.getInstance().database;
        try
        {
            ResultSet set = 
            data.query(
                        "INSERT INTO `bids` ("+
                        "`auctionid` ,"+
                        "`bidderid` ,"+
                        "`amount` ,"+
                        "`timestamp` ,"+
                        ")"+
                        "VALUES ("+
                        " ?, ?, ?, ?"+
                        ");"
                      ,auction.id,bidder.id,amount,System.currentTimeMillis());
            if (set.next())
                this.id = set.getInt("id");
                
        }
        catch (SQLException ex)
        {
            
        }
        
    }
    
    //Override: load in Bid from DataBase
    public Bid(int id,int bidderid ,String bidder, double amount, Auction auction)
    {
        this.amount = amount;
        this.bidder = Bidder.getInstance(bidderid,bidder);
        this.timestamp = System.currentTimeMillis();
        this.id = id;
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
