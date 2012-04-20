package de.cubeisland.AuctionHouse.Auction;

import de.cubeisland.AuctionHouse.AuctionHouse;
import de.cubeisland.AuctionHouse.Database.Database;
import de.cubeisland.AuctionHouse.Util;
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
    private Bidder bidder;
    private ItemStack item;
    private long date;
    private String owner;
    private Double price;
    private int id;
    private final Database db;
    
    public AuctionItem(Auction auction)
    {
        this.db = AuctionHouse.getInstance().getDB();
        if (auction.getBids().isEmpty())
        {
            this.bidder = auction.getOwner();
            this.price = 0.0;
        }
        else
        {
            this.bidder = auction.getBids().peek().getBidder();
            this.price = auction.getBids().peek().getAmount();
        }
        this.item = auction.getItem().clone();// = new ItemStack(auction.item.getType(),auction.item.getAmount());
        this.date = System.currentTimeMillis();
        if (auction.getOwner() instanceof ServerBidder)
        {
            this.owner = "Server";
        }
        else
        {
            this.owner = auction.getOwner().getName();
        }
        this.id = -1;
        try
        {
            db.exec(
                    "INSERT INTO `itemcontainer` ("+
                    "`bidderid` ,"+
                    "`item` ,"+
                    "`amount` ,"+
                    "`price` ,"+
                    "`timestamp` ,"+
                    "`ownerid`"+
                    ")"+
                    "VALUES ( ?, ?, ?, ?, ?, ? );"
                  ,this.bidder.getId(),Util.convertItem(this.item),
                  this.item.getAmount(),this.price,new Timestamp(this.date),auction.getOwner().getId());
            ResultSet set =
                    db.query("SELECT * FROM `itemcontainer` ORDER BY `id` DESC LIMIT 1");
             if (set.next())
                this.id = set.getInt("id");
                
        }
        catch (SQLException ex)
        {
            
        }
    }

    public AuctionItem(Bidder bidder, ItemStack item, Timestamp time,String owner, double price, int id)
    {
        this.db = AuctionHouse.getInstance().getDB();
        this.bidder = bidder;
        this.item = item;
        this.date = time.getTime();
        this.owner = owner;
        this.price = price;
        this.id = id; 
    }
    
    public AuctionItem(ItemStack item, Bidder bidder)
    {
        this.db = AuctionHouse.getInstance().getDB();
        this.bidder = bidder;
        this.item = item;
        this.date = System.currentTimeMillis();
        this.owner = bidder.getName();
        this.price = 0.0;
        this.id = -1; 
        
        try
        {
            db.exec(
                    "INSERT INTO `itemcontainer` ("+
                    "`playerid` ,"+
                    "`item` ,"+
                    "`amount` ,"+
                    "`price` ,"+
                    "`timestamp` ,"+
                    "`ownerid`"+
                    ")"+
                    "VALUES ( ?, ?, ?, ?, ?, ?);"
                  ,bidder.getId(),Util.convertItem(item),
                  item.getAmount(),this.price,this.date,bidder.getId());
            ResultSet set =
                    db.query("SELECT * FROM `itemcontainer` ORDER BY `id` DESC LIMIT 1");
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
        this.db = AuctionHouse.getInstance().getDB();
        this.bidder = bidder;
        this.item = item;
        this.date = date;
        this.owner = owner;
        this.price = price;
    }

    public AuctionItem cloneItem()
    {
        return new AuctionItem(bidder, item, date, owner, price);
    }
    
    public Bidder getBidder()
    {
        return this.bidder;
    }
    
    public ItemStack getItem()
    {
        return this.item;
    }
    
    public long getDate()
    {
        return this.date;
    }
    
    public String getOwner()
    {
        return this.owner;
    }
    
    public Double getPrice()
    {
        return this.price;
    }
    
    public int getId()
    {
        return this.id;
    }   
}