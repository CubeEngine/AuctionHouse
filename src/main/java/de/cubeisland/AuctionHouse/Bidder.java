package de.cubeisland.AuctionHouse;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Faithcaio
 */
public class Bidder
{
    private final ArrayList<Auction> activeBids;
    private final ArrayList<Auction> subscriptions;
    private final ArrayList<ItemStack> materialSub;
    private final OfflinePlayer player;
    private final ItemContainer itemContainer;
    public boolean playerNotification = false;
    public boolean notify = false;
    public boolean notifyCancel = false;
    public boolean notifyContainer = false;
    public int id;
    private static final Map<OfflinePlayer, Bidder> bidderInstances = new HashMap<OfflinePlayer, Bidder>();

    public Bidder(OfflinePlayer player)
    {
        this.player = player;
        this.activeBids = new ArrayList<Auction>();
        this.itemContainer = new ItemContainer(this);
        this.subscriptions = new ArrayList<Auction>();
        this.materialSub = new ArrayList<ItemStack>();
        this.id = -1;
        String bidder;
        Database data = AuctionHouse.getInstance().database;
        try
        {
            
            if (player==null) 
            {
                bidder="*Server";
                data.exec(
                        "INSERT INTO `bidder` ("+
                        "`name` ,"+
                        "`type` ,"+
                        "`notify` "+
                        ") "+
                        "VALUES ( ?, ?, ? );"
                      ,bidder,1,0);
            }
            else
            {
                bidder=player.getName();
                data.exec(
                        "INSERT INTO `bidder` ("+
                        "`name` ,"+
                        "`type` ,"+
                        "`notify` "+
                        ") "+
                        "VALUES ( ?, ?, ? );"
                      ,bidder,0,0);
            }
            ResultSet set =
                    data.query("SELECT * FROM `bidder` WHERE `name`=? LIMIT 1",bidder);
            if (set.next())
                this.id = set.getInt("id");                
        }
        catch (SQLException ex)
        {
            
        }
    }
    
    public Bidder(int id,String name)
    {
        if (name.equalsIgnoreCase("*Server"))
            this.player = null;//ServerBidder
        else
            this.player = AuctionHouse.getInstance().server.getOfflinePlayer(name);
        this.activeBids = new ArrayList<Auction>();
        this.itemContainer = new ItemContainer(this);
        this.subscriptions = new ArrayList<Auction>();
        this.materialSub = new ArrayList<ItemStack>();
        this.id = id;
    }
    
    

    public static Bidder getInstance(Player player)
    {
        Bidder instance;
        if (bidderInstances.isEmpty())
        {
            instance = null;
        }
        else
        {
            instance = bidderInstances.get(player);
        }
        if (instance == null)
        {
            bidderInstances.put(player, new Bidder(player));
        }
        instance = bidderInstances.get(player);
        return instance;
    }
    
    public static Bidder getInstance(int id,String player)
    {
        Bidder instance;
        if (bidderInstances.isEmpty())
        {
            instance = null;
        }
        else
        {
            if (player.equalsIgnoreCase("*Server"))
                return ServerBidder.getInstance(id);
            else
                instance = bidderInstances.get(AuctionHouse.getInstance().server.getOfflinePlayer(player));
        }
        if (instance == null)
        {
            bidderInstances.put(AuctionHouse.getInstance().server.getOfflinePlayer(player), new Bidder(id ,player));
        }
        instance = bidderInstances.get(AuctionHouse.getInstance().server.getOfflinePlayer(player));
        return instance;
    }

    public static Map<OfflinePlayer, Bidder> getInstances()
    {
        return bidderInstances;
    }

    public static Bidder getInstance(OfflinePlayer player)
    {
        Bidder instance;
        if (bidderInstances.isEmpty())
        {
            instance = null;
        }
        else
        {
            Player onlinePlayer = AuctionHouse.getInstance().getServer().getPlayer(player.getName());
            instance = bidderInstances.get(onlinePlayer);
        }
        return instance;
    }
    
    public static Bidder getInstance(CommandSender player)
    {
        Bidder instance;
        if (bidderInstances.isEmpty())
        {
            instance = null;
        }
        else
        {
            if (player instanceof ConsoleCommandSender)
                return ServerBidder.getInstance();
            instance = getInstance((Player)player);
        }
        return instance;
    }

    public ItemContainer getContainer()
    {
        return itemContainer;
    }

    public ArrayList<Auction> getActiveBids()
    {
        return activeBids;
    }

    public ArrayList<Auction> getSubs()
    {
        return subscriptions;
    }

    public ArrayList<ItemStack> getMatSub()
    {
        return materialSub;
    }

    public Player getPlayer()
    {
        if (this.player != null)
        if (this.player.isOnline())
        {
            return this.player.getPlayer();
        }
        return null;
    }

    public OfflinePlayer getOffPlayer()
    {
        return player;
    }

    public String getName()
    {
        if (player==null)
            return "*Server";
        else
            return player.getName();
    }

    public boolean isOnline()
    {
        if (player==null)
            return false;
        return player.isOnline();
    }

    public double getTotalBidAmount()
    {
        double total = 0;
        List<Auction> auctionlist;
        if (!(this.getLeadingAuctions().isEmpty()))
        {
            auctionlist = this.getLeadingAuctions();
            for (int i = 0; i < auctionlist.size(); ++i)
            {
                total += auctionlist.get(i).bids.peek().getAmount();
            }
        }
        return total;
    }

    public boolean removeAuction(Auction auction)
    {
        Database data = AuctionHouse.getInstance().database;
        //All Bid delete
        data.exec("DELETE FROM `bids` WHERE `bidderid`=? && `auctionid`=?"
                      ,this.id,auction.id);
        
        subscriptions.remove(auction);
        return activeBids.remove(auction);
    }

    public boolean removeSubscription(Auction auction)
    {
        Database data = AuctionHouse.getInstance().database;
        //IdSub delete
        data.exec("DELETE FROM `subscription` WHERE `playerid`=? && `auctionid`=?"
                      ,this.id,auction.id);
        return subscriptions.remove(auction);
    }

    public boolean removeSubscription(ItemStack item)
    {
        Database data = AuctionHouse.getInstance().database;
        //MAtSub delete
        data.exec("DELETE FROM `subscription` WHERE `playerid`=? && `item`=?"
                      ,this.id,MyUtil.get().convertItem(item));
        return materialSub.remove(item);
    }

    public List<Auction> getLeadingAuctions(Bidder player)
    {
        List<Auction> auctionlist = new ArrayList<Auction>();
        for (Auction auction : this.activeBids)
        {
            if (auction.bids.peek()==null)
                return null;
            if (auction.bids.peek().getBidder() == player)
            {
                auctionlist.add(auction);
            }
        }
        return auctionlist;
    }

    public List<Auction> getLeadingAuctions()
    {
        return this.getLeadingAuctions(this);
    }

    public List<Auction> getAuctions() //Get all Auctions with player involved
    {
        return activeBids;
    }

    public List<Auction> getAuctions(Bidder player) //Get all Auctions started by player
    {
        ArrayList<Auction> auctionlist = new ArrayList<Auction>()
        {
        };
        final int length = this.activeBids.size();
        for (int i = 0; i < length; i++)
        {
            if (this.activeBids.get(i).owner == player)
            {
                auctionlist.add(this.activeBids.get(i));
            }
        }
        return auctionlist;
    }

    public List<Auction> getOwnAuctions() //Get all Auctions started yourself
    {
        return this.getAuctions(this);
    }

    public Auction getlastAuction(Bidder player) //Get last Auction Bid on
    {

        final int length = this.activeBids.size();
        int auctionIndex = -1;
        for (int i = 0; i < length; i++)
        {

            if (this.activeBids.get(i).bids.peek().getBidder() == player)
            {
                if (auctionIndex == -1)
                {
                    auctionIndex = i;
                }
                if (this.activeBids.get(i).bids.peek().getTimestamp()
                        > this.activeBids.get(auctionIndex).bids.peek().getTimestamp())
                {
                    if (this.activeBids.get(i).owner != player)
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

    public Bidder addAuction(Auction auction)
    {
        this.activeBids.add(auction);
        this.addSubscription(auction);
        return this;
    }

    public Bidder addSubscription(Auction auction)
    {
        Database data = AuctionHouse.getInstance().database;        
        data.exec(
                    "INSERT INTO `subscription` ("+
                    "`bidderid` ,"+
                    "`auctionid` ,"+
                    "`type` "+
                    ")"+
                    "VALUES ( ?, ?, ? );"
                  ,this.id,auction.id,1);
        
        this.subscriptions.add(auction);
        return this;
    }
    
    public Bidder addDataBaseSub(int id)
    {
        this.subscriptions.add(Manager.getInstance().getAuction(id));
        return this;
    }
    
    public Bidder addDataBaseSub(ItemStack item)
    {
        this.materialSub.add(item);
        return this;
    }

    public Bidder addSubscription(ItemStack item)
    {
        Database data = AuctionHouse.getInstance().database;        
        data.exec(
                    "INSERT INTO `subscription` ("+
                    "`bidderid` ,"+
                    "`type` ,"+
                    "`item` "+
                    ")"+
                    "VALUES ( ?, ?, ? );"
                  ,this.id,0,MyUtil.get().convertItem(item)); 
        this.materialSub.add(item);
        return this;
    }
    
    public int notifyBitMask()
    {
        int tmp = 0;
        if (this.notify) tmp |= 1;
        if (this.notifyCancel) tmp |= 2;
        if (this.notifyContainer) tmp |= 4;
        if (this.playerNotification) tmp |= 8;
        return tmp;
    }
    
}