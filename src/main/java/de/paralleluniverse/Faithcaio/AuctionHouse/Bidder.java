package de.paralleluniverse.Faithcaio.AuctionHouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author Faithcaio
 */
public class Bidder {
   public final OfflinePlayer player;
   public final ArrayList<Auction> activeBids;
   public final ItemContainer itemContainer;
   public final ArrayList<Auction> subscriptions;
   public final ArrayList<Material> materialSub;
   public boolean playerNotification = false;
   
   private static final Map<Player, Bidder> bidderInstances = new HashMap<Player, Bidder>();
   
   public Bidder(Player player)
   {
       this.player = player;  
       this.activeBids = new ArrayList<Auction>();
       this.itemContainer = new ItemContainer(this);
       this.subscriptions = new ArrayList<Auction>(); //TODO command
       this.materialSub = new ArrayList<Material>();  //TODO command      
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
        return instance; //instance ist null wenn offline Player AuctionHouse nicht genutzt hat
    }
    
    public double getTotalBidAmount()
    {
        double total = 0;
        List<Auction> auctionlist;
        if (!(this.getLeadingAuctions(this).isEmpty()))
        {   
            auctionlist = this.getLeadingAuctions(this);
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
    public boolean removeSubscription(Material mat)
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
    public Bidder addSubscription(Material material)
    {
        this.materialSub.add(material);
        return this;
    }
   
}
