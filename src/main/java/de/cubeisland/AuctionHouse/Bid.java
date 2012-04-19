package de.cubeisland.AuctionHouse;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

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
            data.exec(
                "INSERT INTO `bids` (`auctionid` ,`bidderid` ,`amount` ,`timestamp`) VALUES ( ?, ?, ?, ?);",
                auction.getId(),
                bidder.getId(),
                amount,
                new Timestamp(System.currentTimeMillis())
            );
            ResultSet set = data.query("SELECT * FROM `bids` WHERE `timestamp`=? && `bidderid`=? LIMIT 1",timestamp,bidder.getId());
            if (set.next())
                this.id = set.getInt("id");
                
        }
        catch (SQLException ex)
        {}
        
    }
    
    //Override: load in Bid from DataBase
    public Bid(int id,int bidderid ,String bidder, double amount, Timestamp timestamp)
    {
        this.amount = amount;
        if (bidder.equalsIgnoreCase("*Server"))
            this.bidder = ServerBidder.getInstance(bidderid);
        else
            this.bidder = Bidder.getInstance(bidderid,bidder);
        this.timestamp = timestamp.getTime();
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
