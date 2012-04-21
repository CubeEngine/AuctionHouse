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
    private final ItemContainer itemContainer;
    private byte notifyState = 0;
    private int id;
    private static final Map<OfflinePlayer, Bidder> bidderInstances = new HashMap<OfflinePlayer, Bidder>();
    private final Database db;

    public Bidder(OfflinePlayer player)
    {
        this.db = AuctionHouse.getInstance().getDB();
        this.player = player;
        this.activeBids = new ArrayList<Auction>();
        this.itemContainer = new ItemContainer(this);
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
        this.itemContainer = new ItemContainer(this);
        this.subscriptions = new ArrayList<Auction>();
        this.materialSub = new ArrayList<ItemStack>();
        this.id = id;
    }

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

    public static Map<OfflinePlayer, Bidder> getInstances()
    {
        return bidderInstances;
    }

    public static Bidder getInstance(Player player)
    {
        return getInstance((OfflinePlayer)player);
    }

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
    
    public static Bidder getInstanceNoCreate(OfflinePlayer player)
    {
        return bidderInstances.get(player);
    }

    public static Bidder getInstance(CommandSender player)
    {
        if (player instanceof Player)
        {
            return getInstance((Player)player);
        }
        return ServerBidder.getInstance();
    }

    public final void resetNotifyState()
    {
        this.resetNotifyState((byte)0);
    }

    public void resetNotifyState(byte state)
    {
        this.notifyState = state;
    }

    public void setNotifyState(byte state)
    {
        this.notifyState |= state;
    }

    public boolean hasNotifyState(byte state)
    {
        return ((this.notifyState & state) == state);
    }

    public byte getNotifyState()
    {
        return this.notifyState;
    }

    public void toggleNotifyState(byte state)
    {
        this.notifyState ^= state;
    }

    public void unsetNotifyState(byte state)
    {
        this.notifyState &= ~state;
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
        {
            if (this.player.isOnline())
            {
                return this.player.getPlayer();
            }
        }
        return null;
    }

    public int getId()
    {
        return this.id;
    }

    public OfflinePlayer getOffPlayer()
    {
        return player;
    }

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

    public boolean isOnline()
    {
        if (player == null)
        {
            return false;
        }
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
                total += auctionlist.get(i).getBids().peek().getAmount();
            }
        }
        return total;
    }

    public boolean removeAuction(Auction auction)
    {
        //All Bid delete
        db.execUpdate("DELETE FROM `bids` WHERE `bidderid`=? && `auctionid`=?", this.id, auction.getId());
        this.removeSubscription(auction);
        return activeBids.remove(auction);
    }

    public boolean removeSubscription(Auction auction)
    {
        //IdSub delete
        db.execUpdate("DELETE FROM `subscription` WHERE `bidderid`=? && `auctionid`=?", this.id, auction.getId());
        return subscriptions.remove(auction);
    }

    public boolean removeSubscription(ItemStack item)
    {
        //MAtSub delete
        db.execUpdate("DELETE FROM `subscription` WHERE `bidderid`=? && `item`=?", this.id, Util.convertItem(item));
        return materialSub.remove(item);
    }

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
            if (this.activeBids.get(i).getOwner() == player)
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

    public Bidder addAuction(Auction auction)
    {
        this.activeBids.add(auction);
        this.addSubscription(auction);
        return this;
    }

    public Bidder addSubscription(Auction auction)
    {
        if (this.subscriptions.contains(auction)) return this;
        db.exec(
            "INSERT INTO `subscription` ("
            + "`bidderid` ,"
            + "`auctionid` ,"
            + "`type` "
            + ")"
            + "VALUES ( ?, ?, ? );", this.id, auction.getId(), 1);

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
        if (this.materialSub.contains(item)) return this;
        db.exec(
            "INSERT INTO `subscription` (`bidderid` ,`type` ,`item` ) VALUES ( ?, ?, ? );",
            this.id,
            0,
            Util.convertItem(item)
        );
        this.materialSub.add(item);
        return this;
    }
}