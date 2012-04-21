package de.cubeisland.AuctionHouse.Auction;

import de.cubeisland.AuctionHouse.AuctionHouse;
import de.cubeisland.AuctionHouse.Database.Database;
import de.cubeisland.AuctionHouse.Database.DatabaseEntity;
import de.cubeisland.AuctionHouse.Database.EntityIdentifier;
import de.cubeisland.AuctionHouse.Database.EntityProperty;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Represents a bid by a player
 *
 * @author Faithcaio
 */
public class Bid implements DatabaseEntity
{
    @EntityIdentifier
    private int id;

    @EntityProperty
    private final double amount;
    
    @EntityProperty
    private Bidder bidder;

    @EntityProperty
    private final long timestamp;

    
    private final Database db;

    public Bid(Bidder bidder, double amount, Auction auction)
    {
        this.db = AuctionHouse.getInstance().getDB();
        this.amount = amount;
        this.bidder = bidder;
        this.timestamp = System.currentTimeMillis();
        this.id = -1;
        try
        {
            db.exec(
                "INSERT INTO `bids` (`auctionid` ,`bidderid` ,`amount` ,`timestamp`) VALUES ( ?, ?, ?, ?);",
                auction.getId(),
                bidder.getId(),
                amount,
                new Timestamp(System.currentTimeMillis())
            );
            ResultSet set = db.query("SELECT * FROM `bids` WHERE `timestamp`=? && `bidderid`=? LIMIT 1",timestamp,bidder.getId());
            if (set.next())
                this.id = set.getInt("id");
                
        }
        catch (SQLException ex)
        {}
        
    }

    public String getTable()
    {
        return "bids";
    }
    
    //Override: load in Bid from DataBase
    public Bid(int id,int bidderid ,String bidder, double amount, Timestamp timestamp)
    {
        this.db = AuctionHouse.getInstance().getDB();
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
    
    public int getId()
    {
        return this.id;
    }
    
    public void giveServer()
    {
        this.bidder = ServerBidder.getInstance();
    }
}
