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
        AuctionItem auctionItem = this.itemList.pollFirst();
        if (auctionItem == null)
            return false;
        if (auctionItem.owner.equals(this.bidder.getName()))
            player.sendMessage("Info: Receiving aborted Auction with "+auctionItem.item.toString());
        else   
            player.sendMessage("Info: Receiving "+auctionItem.item.toString()+
                            " for "+auctionItem.price+
                            " from "+auctionItem.owner+
                            " at "+DateFormatUtils.formatUTC(auctionItem.date, "MMM dd"));
        
        ItemStack remain = player.getInventory().addItem(auctionItem.item).get(0);
        if (remain==null)
        {
            AuctionHouse.debug("Player: "+player.getName()+": all Items received");
            return true;
        }
        else
        {
            player.sendMessage("Info: Could not retrieve all Items. Remains are stored again!");
            itemList.addFirst(new AuctionItem(remain,bidder));
            return true;
        }
    }
}
