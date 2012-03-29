package de.paralleluniverse.Faithcaio.AuctionHouse;

import java.util.ArrayList;
import org.bukkit.entity.Player;

/**
 *
 * @author Anselm
 */
public class Bidder {
   public final Player player;
   public final ArrayList<Auction> activeBids;
   
   public Bidder(Player player)
   {
       this.player = player;  
       this.activeBids = new ArrayList<Auction>();
   }
}
