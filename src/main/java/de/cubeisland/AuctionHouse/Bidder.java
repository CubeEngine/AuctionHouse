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
    private static final Map<Player, Bidder> bidderInstances = new HashMap<Player, Bidder>();

    public Bidder(Player player)
    {
        this.player = player;
        this.activeBids = new ArrayList<Auction>();
        this.itemContainer = new ItemContainer(this);
        this.subscriptions = new ArrayList<Auction>();
        this.materialSub = new ArrayList<ItemStack>();
        this.id = -1;

        Database data = AuctionHouse.getInstance().database;
        try
        {
            String bidder;
            if (player==null) 
                bidder="*Server";
            else
                bidder=player.getName();
                
            ResultSet set = 
            data.query(
                        "INSERT INTO `bidder` ("+
                        "`name` ,"+
                        "`type` ,"+
                        "`notify` ,"+
                        ")"+
                        "VALUES ("+
                        " ?, ?, ?"+
                        ");"
                      ,bidder,false,0);
            if (set.next())
                this.id = set.getInt("id");
                
        }
        catch (SQLException ex)
        {
            
        }
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

    public static Map<Player, Bidder> getInstances()
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
        if (player.isOnline())
        {
            return player.getPlayer();
        }
        else
        {
            return null;
        }
    }

    public OfflinePlayer getOffPlayer()
    {
        return player;
    }

    public String getName()
    {
        return player.getName();
    }

    public boolean isOnline()
    {
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
        data.query("DELETE FROM `bids` WHERE `bidderid`=? && `auctionid`=?"
                      ,this.id,auction.id);
        
        subscriptions.remove(auction);
        return activeBids.remove(auction);
    }

    public boolean removeSubscription(Auction auction)
    {
        Database data = AuctionHouse.getInstance().database;
        //IdSub delete
        data.query("DELETE FROM `subscription` WHERE `playerid`=? && `auctionid`=?"
                      ,this.id,auction.id);
        return subscriptions.remove(auction);
    }

    public boolean removeSubscription(ItemStack item)
    {
        Database data = AuctionHouse.getInstance().database;
        //MAtSub delete
        data.query("DELETE FROM `subscription` WHERE `playerid`=? && `item`=?"
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
        this.subscriptions.add(auction);
        return this;
    }

    public Bidder addSubscription(Auction auction)
    {
        Database data = AuctionHouse.getInstance().database;        
        data.query(
                    "INSERT INTO `subscription` ("+
                    "`id` ,"+
                    "`playerid` ,"+
                    "`auctionid` ,"+
                    "`type` ,"+
                    "`item`"+
                    ")"+
                    "VALUES ("+
                    "NULL , '?', '?', '0', 'NULL'"+
                    ");"
                  ,this.id,auction.id);
        
        this.subscriptions.add(auction);
        return this;
    }

    public Bidder addSubscription(ItemStack item)
    {
        Database data = AuctionHouse.getInstance().database;        
        data.query(
                    "INSERT INTO `subscription` ("+
                    "`id` ,"+
                    "`playerid` ,"+
                    "`auctionid` ,"+
                    "`type` ,"+
                    "`item`"+
                    ")"+
                    "VALUES ("+
                    "NULL , '?', 'NULL', '1', '?'"+
                    ");"
                  ,this.id,MyUtil.get().convertItem(item)); 
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