package de.paralleluniverse.Faithcaio.AuctionHouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author Faithcaio
 */
public class Bidder {
   public final Player player;
   public final ArrayList<Auction> activeBids;
   
   private static final Map<Player, Bidder> bidderInstances = new HashMap<Player, Bidder>();
   
   public Bidder(Player player)
   {
       this.player = player;  
       this.activeBids = new ArrayList<Auction>();
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
    
    public boolean removeAuction(Auction auction)
    {
        return activeBids.remove(auction);
    }
    
    public List<Auction> getLeadingAuctions(Player player)
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
    
    public List<Auction> getAuctions(Player player) //Get all Auctions started by player
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
    
    public Auction getlastAuction(Player player) //Get all Auctions started by player
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
                   >this.activeBids.get(auctionIndex).bids.peek().getTimestamp()     
                        )
                    auctionIndex = i;
            }     
        }
        if (auctionIndex == -1) return null;
        return this.activeBids.get(auctionIndex);
    }  
    
    public Bidder addAuction(Auction auction)
    {
        this.activeBids.add(auction);
        return this;
    }
   
}
