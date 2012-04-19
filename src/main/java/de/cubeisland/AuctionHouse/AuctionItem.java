package de.cubeisland.AuctionHouse;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Faithcaio
 */
public class AuctionItem
{
    protected Bidder bidder;
    protected ItemStack item;
    protected long date;
    protected String owner;
    protected Double price;
    public int id;

    public AuctionItem(Auction auction)
    {
        if (auction.bids.isEmpty())
        {
            this.bidder = auction.owner;
            this.price = 0.0;
        }
        else
        {
            this.bidder = auction.bids.peek().getBidder();
            this.price = auction.bids.peek().getAmount();
        }
        this.item = auction.item.clone();// = new ItemStack(auction.item.getType(),auction.item.getAmount());
        this.date = System.currentTimeMillis();
        if (auction.owner instanceof ServerBidder)
        {
            this.owner = "Server";
        }
        else
        {
            this.owner = auction.owner.getName();
        }
        this.id = -1;
        try
        {
            Database data = AuctionHouse.getInstance().database;
            data.exec(
                    "INSERT INTO `itemcontainer` ("+
                    "`bidderid` ,"+
                    "`item` ,"+
                    "`amount` ,"+
                    "`price` ,"+
                    "`timestamp` ,"+
                    "`ownerid`"+
                    ")"+
                    "VALUES ( ?, ?, ?, ?, ?, ? );"
                  ,this.bidder.id,MyUtil.get().convertItem(this.item),
                  this.item.getAmount(),this.price,new Timestamp(this.date),auction.owner.id);
            ResultSet set =
                    data.query("SELECT * FROM `itemcontainer` ORDER BY `id` DESC LIMIT 1");
             if (set.next())
                this.id = set.getInt("id");
                
        }
        catch (SQLException ex)
        {
            
        }
    }

    
    public AuctionItem(ItemStack item, Bidder bidder)
    {
        this.bidder = bidder;
        this.item = item;
        this.date = System.currentTimeMillis();
        this.owner = bidder.getName();
        this.price = 0.0;
        this.id = -1; 
        
        try
        {
            Database data = AuctionHouse.getInstance().database;
            data.exec(
                    "INSERT INTO `itemcontainer` ("+
                    "`playerid` ,"+
                    "`item` ,"+
                    "`amount` ,"+
                    "`price` ,"+
                    "`timestamp` ,"+
                    "`ownerid`"+
                    ")"+
                    "VALUES ("+
                    " ?, ?, ?, ?, ?, ?"+
                    ");"
                  ,bidder.id,MyUtil.get().convertItem(item),
                  item.getAmount(),this.price,this.date,bidder.id);
            ResultSet set =
                    data.query("SELECT * FROM `itemcontainer` ORDER BY `id` DESC LIMIT 1");
            if (set.next())
                this.id = set.getInt("id");
        }
        catch (SQLException ex)
        {
            
        }
    }
    
    //Fake Auktion
    private AuctionItem(Bidder bidder, ItemStack item, long date, String owner, Double price)
    {
        this.bidder = bidder;
        this.item = item;
        this.date = date;
        this.owner = owner;
        this.price = price;
    }

    public AuctionItem clone()
    {
        return new AuctionItem(bidder, item, date, owner, price);
    }
}