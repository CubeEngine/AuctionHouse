package de.paralleluniverse.Faithcaio.AuctionHouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
/**
 *
 * @author Faithcaio
 */
public class Bidder {
   private final ArrayList<Auction> activeBids;
   private final ArrayList<Auction> subscriptions;
   private final ArrayList<ItemStack> materialSub;
   private final OfflinePlayer player;
   
   private final ItemContainer itemContainer;
   public boolean playerNotification = false;
   
   private static final Map<Player, Bidder> bidderInstances = new HashMap<Player, Bidder>();
   
   public Bidder(Player player)
   {
       this.player = player;  
       this.activeBids = new ArrayList<Auction>();
       this.itemContainer = new ItemContainer(this);
       this.subscriptions = new ArrayList<Auction>();
       this.materialSub = new ArrayList<ItemStack>();    
   }
    
    public static Bidder getInstance(Player player)
    {
        Bidder instance;
        if (bidderInstances.isEmpty()) instance = null;
        else instance = bidderInstances.get(player);
        if (instance == null)
          bidderInstances.put(player, new Bidder(player));
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
        if (bidderInstances.isEmpty()) instance = null;
        else 
        {
            Player onlinePlayer=AuctionHouse.getInstance().getServer().getPlayer(player.getName());
            instance = bidderInstances.get(onlinePlayer);
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
            return player.getPlayer();
        else
            return null;
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
            for (int i=0;i<auctionlist.size();++i)
            total += auctionlist.get(i).bids.peek().getAmount();
        }
        return total;
    }
    
    public boolean removeAuction(Auction auction)
    {
        subscriptions.remove(auction);
        return activeBids.remove(auction);
    }
    
    public boolean removeSubscription(Auction auction)
    {
        return subscriptions.remove(auction);
    }
    
    public boolean removeSubscription(ItemStack mat)
    {
        return materialSub.remove(mat);
    }
    
    public List<Auction> getLeadingAuctions(Bidder player)
    {
        List<Auction> auctionlist = new ArrayList<Auction>();
        final int length = this.activeBids.size();
        for (int i = 0; i < length; ++i)
        {
            if (this.activeBids.get(i).bids.peek().getBidder() == player)
            {
                auctionlist.add(this.activeBids.get(i));
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
        ArrayList<Auction> auctionlist = new ArrayList<Auction>() {};  
        final int length = this.activeBids.size();
        for (int i = 0;i < length;i++)
        {
            if (this.activeBids.get(i).owner == player)
            { auctionlist.add( this.activeBids.get(i) ); }     
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
        for (int i = 0;i < length;i++)
        {
            
            if (this.activeBids.get(i).bids.peek().getBidder() == player)
            {
                if (auctionIndex == -1)
                    auctionIndex = i;                    
                if (this.activeBids.get(i).bids.peek().getTimestamp()
                   >this.activeBids.get(auctionIndex).bids.peek().getTimestamp())
                    if (this.activeBids.get(i).owner != player)
                        auctionIndex = i;
            }     
        }
        if (auctionIndex == -1) return null;
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
        this.subscriptions.add(auction);
        return this;
    }
    
    public Bidder addSubscription(ItemStack material)
    {
        this.materialSub.add(material);
        return this;
    }
}