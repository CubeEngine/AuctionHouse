package de.cubeisland.AuctionHouse.Auction;

import de.cubeisland.AuctionHouse.AuctionHouse;
import de.cubeisland.AuctionHouse.Database.Database;
import de.cubeisland.AuctionHouse.Manager;
import de.cubeisland.AuctionHouse.Util;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a Bidder / Player using AuctionHouse
 * 
 * @author Faithcaio
 */
public class Bidder
{
    public static final byte NOTIFY_STATUS = 8;
    public static final byte NOTIFY_ITEMS = 4;
    public static final byte NOTIFY_CANCEL = 2;
    public static final byte NOTIFY_WIN = 1;
    private final ArrayList<Auction> activeBids;
    private final ArrayList<Auction> subscriptions;
    private final ArrayList<ItemStack> materialSub;
    private final OfflinePlayer player;
    private final AuctionBox itemContainer;
    private byte notifyState = 0;
    private int id;
    private static final Map<OfflinePlayer, Bidder> bidderInstances = new HashMap<OfflinePlayer, Bidder>();
    private final Database db;
/**
 * Creates a new Bidder + add him to DataBase
 */
    public Bidder(OfflinePlayer player)
    {
        this.db = AuctionHouse.getInstance().getDB();
        this.player = player;
        this.activeBids = new ArrayList<Auction>();
        this.itemContainer = new AuctionBox(this);
        this.subscriptions = new ArrayList<Auction>();
        this.materialSub = new ArrayList<ItemStack>();
        this.id = -1;
        String name;
        try
        {

            if (player == null)
            {
                name = "*Server";
                db.exec(
                    "INSERT INTO `bidder` ("
                    + "`name` ,"
                    + "`type` ,"
                    + "`notify` "
                    + ") "
                    + "VALUES ( ?, ?, ? );", name, 1, 0);
            }
            else
            {
                name = player.getName();
                db.exec(
                    "INSERT INTO `bidder` ("
                    + "`name` ,"
                    + "`type` ,"
                    + "`notify` "
                    + ") "
                    + "VALUES ( ?, ?, ? );", name, 0, 0);
            }
            ResultSet set =
                db.query("SELECT * FROM `bidder` WHERE `name`=? LIMIT 1", name);
            if (set.next())
            {
                this.id = set.getInt("id");
            }
        }
        catch (SQLException ex)
        {
        }
    }
/**
 * Creates a new Bidder from DataBase
 */
    public Bidder(int id, String name)
    {
        this.db = AuctionHouse.getInstance().getDB();
        if (name.equalsIgnoreCase("*Server"))
        {
            this.player = null;//ServerBidder
        }
        else
        {
            this.player = AuctionHouse.getInstance().getServer().getOfflinePlayer(name);
        }
        this.activeBids = new ArrayList<Auction>();
        this.itemContainer = new AuctionBox(this);
        this.subscriptions = new ArrayList<Auction>();
        this.materialSub = new ArrayList<ItemStack>();
        this.id = id;
    }

/**
 * @return New Bidder loaded in from DataBase
 */    
    public static Bidder getInstance(int id, String player)
    {
        Bidder instance;
        if (player.equalsIgnoreCase("*Server"))
        {
            return ServerBidder.getInstance(id);
        }
        
        instance = bidderInstances.get(AuctionHouse.getInstance().getServer().getOfflinePlayer(player));

        if (instance == null)
        {
            instance = new Bidder(id, player);
            bidderInstances.put(AuctionHouse.getInstance().getServer().getOfflinePlayer(player), instance);
        }
        return instance;
    }
    
/**
 * @return HashMap of all current Bidder
 */   
    public static Map<OfflinePlayer, Bidder> getInstances()
    {
        return bidderInstances;
    }
    
/**
 * if not given create a new Bidder
 * @return Bidder by Player
 */   
    public static Bidder getInstance(Player player)
    {
        return getInstance((OfflinePlayer)player);
    }
    
/**
 * if not given create a new Bidder
 * @return Bidder by OfflinePlayer
 */   
    public static Bidder getInstance(OfflinePlayer player)
    {
        Bidder instance = bidderInstances.get(player);
        if (instance == null)
        {
            instance = new Bidder(player);
            bidderInstances.put(player, instance);
        }
        return instance;
    }
    
/**
 * creates NEVER a new Bidder
 * @return Bidder by Player
 */   
    public static Bidder getInstanceNoCreate(OfflinePlayer player)
    {
        return bidderInstances.get(player);
    }
    
/**
 * if not given create a new Bidder
 * @return Bidder by CommandSender
 */   
    public static Bidder getInstance(CommandSender player)
    {
        if (player instanceof Player)
        {
            return getInstance((Player)player);
        }
        return ServerBidder.getInstance();
    }
    
/**
 * resets the notify bitmask
 */   
    public final void resetNotifyState()
    {
        this.resetNotifyState((byte)0);
    }

/**
 * sets the notify bitmask
 */   
    public void resetNotifyState(byte state)
    {
        this.notifyState = state;
    }

/**
 * sets the notify state
 * @param Bidder.NOTIFY_STATUS | NOTIFY_ITEMS | NOTIFY_CANCEL | NOTIFY_WIN
 */
    public void setNotifyState(byte state)
    {
        this.notifyState |= state;
    }

/**
 * @param Bidder.NOTIFY_STATUS | NOTIFY_ITEMS | NOTIFY_CANCEL | NOTIFY_WIN
 * @return notify State of this Bidder
 */ 
    public boolean hasNotifyState(byte state)
    {
        return ((this.notifyState & state) == state);
    }

/**
 * gets the notify bitmask
 */     
    public byte getNotifyState()
    {
        return this.notifyState;
    }

/**
 * toggles the notify state
 * @param Bidder.NOTIFY_STATUS | NOTIFY_ITEMS | NOTIFY_CANCEL | NOTIFY_WIN
 */ 
    public void toggleNotifyState(byte state)
    {
        this.notifyState ^= state;
    }
    
/**
 * unsets the notify state
 * @param Bidder.NOTIFY_STATUS | NOTIFY_ITEMS | NOTIFY_CANCEL | NOTIFY_WIN
 */ 
    public void unsetNotifyState(byte state)
    {
        this.notifyState &= ~state;
    }

/**
 * @return AuctionBox of this Bidder
 */ 
    public AuctionBox getBox()
    {
        return itemContainer;
    }

/**
 * @return All auctions started or bid on by this Bidder
 */ 
    public ArrayList<Auction> getActiveBids()
    {
        return activeBids;
    }

/**
 * @return All subscribed auctions
 */ 
    public ArrayList<Auction> getSubs()
    {
        return subscriptions;
    }
    
/**
 * @return All material subscriptions
 */ 
    public ArrayList<ItemStack> getMatSub()
    {
        return materialSub;
    }

/**
 * @return Player represented by this Bidder if online
 */ 
    public Player getPlayer()
    {
        if (this.player != null)
        {
            if (this.player.isOnline())
            {
                return this.player.getPlayer();
            }
        }
        return null;
    }
    
/**
 * @return BidderID in DataBase
 */ 
    public int getId()
    {
        return this.id;
    }
    
/**
 * @return BidderID in DataBase
 */ 
    public OfflinePlayer getOffPlayer()
    {
        return player;
    }

/**
 * @return Exact name of this Bidder
 */  
    public String getName()
    {
        if (player == null)
        {
            return "*Server";
        }
        else
        {
            return player.getName();
        }
    }
    
/**
 * @return true if Bidder is online
 */  
    public boolean isOnline()
    {
        if (player == null)
        {
            return false;
        }
        return player.isOnline();
    }
    
/**
 * @return Total amount of money spend in leading bids
 */  
    public double getTotalBidAmount()
    {
        double total = 0;
        List<Auction> auctionlist;
        if (!(this.getLeadingAuctions().isEmpty()))
        {
            auctionlist = this.getLeadingAuctions();
            for (int i = 0; i < auctionlist.size(); ++i)
            {
                total += auctionlist.get(i).getBids().peek().getAmount();
            }
        }
        return total;
    }

/**
 * removes auction + bids + subscription from this Bidder and out of DataBase
 * @return could remove?
 */  
    public boolean removeAuction(Auction auction)
    {
        db.execUpdate("DELETE FROM `bids` WHERE `bidderid`=? && `auctionid`=?", this.id, auction.getId());
        this.removeSubscription(auction);
        return activeBids.remove(auction);
    }
    
/**
 * removes Id Subscription from this Bidder and out of DataBase
 * @return could remove?
 */  
    public boolean removeSubscription(Auction auction)
    {
        db.execUpdate("DELETE FROM `subscription` WHERE `bidderid`=? && `auctionid`=?", this.id, auction.getId());
        return subscriptions.remove(auction);
    }

/**
 * removes Material Subscription from this Bidder and out of DataBase
 * @return could remove?
 */  
    public boolean removeSubscription(ItemStack item)
    {
        //MAtSub delete
        db.execUpdate("DELETE FROM `subscription` WHERE `bidderid`=? && `item`=?", this.id, Util.convertItem(item));
        return materialSub.remove(item);
    }

/**
 * @param Bidder to get auctions of
 * @return all auctions of player with leading Bid (excluding own auctions)
 */  
    public List<Auction> getLeadingAuctions(Bidder player)
    {
        List<Auction> auctionlist = new ArrayList<Auction>();
        for (Auction auction : this.activeBids)
        {
            if (auction.getBids().peek() == null)
            {
                return null;
            }
            if (auction.getBids().peek().getBidder() == player)
            {
                auctionlist.add(auction);
            }
        }
        return auctionlist;
    }

/**
 * @return all auctions of this Bidder with leading Bid (excluding own auctions)
 */  
    public List<Auction> getLeadingAuctions()
    {
        return this.getLeadingAuctions(this);
    }

/**
 * @return all auctions of this Bidder
 */  
    public List<Auction> getAuctions() //Get all Auctions with player involved
    {
        return activeBids;
    }
    
/**
 * @return all auctions started by player
 */  
    public List<Auction> getAuctions(Bidder player) //Get all Auctions started by player
    {
        ArrayList<Auction> auctionlist = new ArrayList<Auction>()
        {
        };
        final int length = this.activeBids.size();
        for (int i = 0; i < length; i++)
        {
            if (this.activeBids.get(i).getOwner() == player)
            {
                auctionlist.add(this.activeBids.get(i));
            }
        }
        return auctionlist;
    }
/**
 * @return all auctions started by this Bidder
 */  
    public List<Auction> getOwnAuctions() //Get all Auctions started yourself
    {
        return this.getAuctions(this);
    }

/**
 * @return last Auction player bid on
 */  
    public Auction getlastAuction(Bidder player) //Get last Auction Bid on
    {

        final int length = this.activeBids.size();
        int auctionIndex = -1;
        for (int i = 0; i < length; i++)
        {

            if (this.activeBids.get(i).getBids().peek().getBidder() == player)
            {
                if (auctionIndex == -1)
                {
                    auctionIndex = i;
                }
                if (this.activeBids.get(i).getBids().peek().getTimestamp()
                    > this.activeBids.get(auctionIndex).getBids().peek().getTimestamp())
                {
                    if (this.activeBids.get(i).getOwner() != player)
                    {
                        auctionIndex = i;
                    }
                }
            }
        }
        if (auctionIndex == -1)
        {
            return null;
        }
        return this.activeBids.get(auctionIndex);
    }
    
/**
 * Adds auction to this Bidder
 * @param auction to add to this Bidder
 * @return Bidder who added auction
 */  
    public Bidder addAuction(Auction auction)
    {
        this.activeBids.add(auction);
        this.addSubscription(auction);
        return this;
    }

/**
 * Adds auction to Subsriptionlist
 * @param auction to add to Subscriptions
 * @return could add subscription
 */  
    public boolean addSubscription(Auction auction)
    {
        if (this.subscriptions.contains(auction)) return false;
        db.exec(
            "INSERT INTO `subscription` ("
            + "`bidderid` ,"
            + "`auctionid` ,"
            + "`type` "
            + ")"
            + "VALUES ( ?, ?, ? );", this.id, auction.getId(), 1);

        this.subscriptions.add(auction);
        return true;
    }
    
/**
 * Loads auction to Subsriptionlist from DataBase
 * @param auctionID to add to Subscriptions
 * @return Bidder
 */  
    public Bidder addDataBaseSub(int id)
    {
        this.subscriptions.add(Manager.getInstance().getAuction(id));
        return this;
    }

/**
 * Loads item to Subsriptionlist from DataBase
 * @param ItemStack to add to Subscriptions
 * @return Bidder
 */  
    public Bidder addDataBaseSub(ItemStack item)
    {
        this.materialSub.add(item);
        return this;
    }
/**
 * Adds item to Subsriptionlist
 * @param ItemStack to add to Subscriptions
 * @return could add subscription
 */  
    public boolean addSubscription(ItemStack item)
    {
        if (this.materialSub.contains(item)) return false;
        db.exec(
            "INSERT INTO `subscription` (`bidderid` ,`type` ,`item` ) VALUES ( ?, ?, ? );",
            this.id,
            0,
            Util.convertItem(item)
        );
        this.materialSub.add(item);
        return true;
    }
}