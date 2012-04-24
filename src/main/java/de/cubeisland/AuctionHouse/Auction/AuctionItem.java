package de.cubeisland.AuctionHouse.Auction;

import de.cubeisland.AuctionHouse.AuctionHouse;
import de.cubeisland.AuctionHouse.Database.Database;
import de.cubeisland.AuctionHouse.Database.DatabaseEntity;
import de.cubeisland.AuctionHouse.Database.EntityIdentifier;
import de.cubeisland.AuctionHouse.Database.EntityProperty;
import de.cubeisland.AuctionHouse.Util;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an Item in the AuctionBox
 * 
 * @author Faithcaio
 */
public class AuctionItem implements DatabaseEntity
{
    @EntityIdentifier
    private int id;
    @EntityProperty
    private Bidder bidder;
    @EntityProperty
    private ItemStack item;
    @EntityProperty
    private long date;
    @EntityProperty
    private String owner;
    @EntityProperty
    private Double price;
    
    private final Database db;
    
/**
 * Creates a new AuctionItem when won auction + Add it to DataBase
 */
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
                    "INSERT INTO `auctionbox` ("+
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
                    db.query("SELECT * FROM `auctionbox` ORDER BY `id` DESC LIMIT 1");
             if (set.next())
                this.id = set.getInt("id");
                
        }
        catch (SQLException ex)
        {
            
        }
    }

/**
 * Loads in an AuctionItem from DataBase
 */
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
    
/**
 * Creates a new AuctionItem when aborted + Add it to DataBase
 */ 
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
                    "INSERT INTO `auctionbox` ("+
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
                    db.query("SELECT * FROM `auctionbox` ORDER BY `id` DESC LIMIT 1");
            if (set.next())
                this.id = set.getInt("id");
        }
        catch (SQLException ex)
        {
            
        }
    }
    
/**
 * Creates a Fake auctionItem
 */
    private AuctionItem(Bidder bidder, ItemStack item, long date, String owner, Double price)
    {
        this.db = AuctionHouse.getInstance().getDB();
        this.bidder = bidder;
        this.item = item;
        this.date = date;
        this.owner = owner;
        this.price = price;
    }
    
/**
 *  @return TableName in Database
 */ 
    public String getTable()
    {
        return "auctionbox";
    }

/**
 * @return A clone of this auctionItem
 */
    public AuctionItem cloneItem()
    {
        return new AuctionItem(bidder, item, date, owner, price);
    }

/**
 * @return owner of this auctionItem
 */
    public Bidder getBidder()
    {
        return this.bidder;
    }
    
/**
 * @return item as Itemstack
 */ 
    public ItemStack getItem()
    {
        return this.item;
    }
    
/**
 * @return date when added to Box
 */ 
    public long getDate()
    {
        return this.date;
    }
    
/**
 * @return original owner
 */ 
    public String getOwner()
    {
        return this.owner;
    }
    
/**
 * @return price item was bought
 */ 
    public Double getPrice()
    {
        return this.price;
    }
    
/**
 * @return Id in DataBase
 */ 
    public int getId()
    {
        return this.id;
    }   
}