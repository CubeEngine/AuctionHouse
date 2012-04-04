package de.paralleluniverse.Faithcaio.AuctionHouse;

import java.util.LinkedList;
import org.apache.commons.lang.time.DateFormatUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
/**
 *
 * @author Faithcaio
 */
public class ItemContainer {

    LinkedList<AuctionItem> itemList;
    public final Bidder bidder;
    
    public ItemContainer (Bidder bidder)
    {
        this.bidder = bidder;
        this.itemList = new LinkedList<AuctionItem>();        
    }
    
    public void addItem (Auction auction)
    {
        this.itemList.add(new AuctionItem(auction));
    }

    public boolean giveNextItem ()
    {
        Player player = this.bidder.getPlayer();
        
        if (this.itemList.isEmpty())
            return false;
        
        AuctionItem auctionItem = this.itemList.getFirst();  
        AuctionHouse.debug("Player: "+player.getName()+": Give Items"+auctionItem.item.toString());

        
        ItemStack tmp = player.getInventory().addItem(this.itemList.getFirst().clone().item).get(0);
  

        if (auctionItem.owner.equals(this.bidder.getName()))
            player.sendMessage("Info: Receiving aborted Auction with "+auctionItem.item.toString());
        else   
            player.sendMessage("Info: Receiving "+auctionItem.item.toString()+
                            " for "+auctionItem.price+
                            " from "+auctionItem.owner+
                            " at "+DateFormatUtils.formatUTC(auctionItem.date, "MMM dd"));

        if (tmp == null)
        {
            AuctionHouse.debug("Player: "+player.getName()+": all Items received");
            player.updateInventory();
            this.itemList.removeFirst();
            return true;
        }
        else
        {
            AuctionHouse.debug("Player: "+player.getName()+": old Items"+auctionItem.item.toString());
            player.sendMessage("Info: Could not retrieve all Items. Remains are stored again!");
            itemList.getFirst().item.setAmount(tmp.getAmount());
            player.updateInventory();
            return true;
        }
    }
}
