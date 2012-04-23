package de.cubeisland.AuctionHouse.Auction;

import de.cubeisland.AuctionHouse.AuctionHouse;
import static de.cubeisland.AuctionHouse.AuctionHouse.t;
import de.cubeisland.AuctionHouse.Database.Database;
import java.util.LinkedList;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.time.DateFormatUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Faithcaio
 */
public class AuctionBox
{
    private LinkedList<AuctionItem> itemList;
    private final Bidder bidder;
    private Economy econ = AuctionHouse.getInstance().getEconomy();
    private final Database db;

    public AuctionBox(Bidder bidder)
    {
        this.db = AuctionHouse.getInstance().getDB();
        this.bidder = bidder;
        this.itemList = new LinkedList<AuctionItem>();
    }

    public void addItem(Auction auction)
    {
        this.itemList.add(new AuctionItem(auction));
    }

    public boolean giveNextItem()
    {
        Player player = this.bidder.getPlayer();

        if (this.itemList.isEmpty())
        {
            return false;
        }

        AuctionItem auctionItem = this.itemList.getFirst();

        ItemStack tmp = player.getInventory().addItem(this.itemList.getFirst().cloneItem().getItem()).get(0);


        if (auctionItem.getOwner().equals(this.bidder.getName()))
        {
            player.sendMessage(t("i") + " " + t("cont_rec_ab", auctionItem.getItem().getType().toString() + "x" + auctionItem.getItem().getAmount()));
        }
        else
        {
            player.sendMessage(t("i") + " " + t("cont_rec", auctionItem.getItem().getType().toString() + "x" + auctionItem.getItem().getAmount(),
                econ.format(auctionItem.getPrice()), auctionItem.getOwner(),
                DateFormatUtils.formatUTC(auctionItem.getDate(), "MMM dd"))
            );
        }

        if (tmp == null)
        {
            player.updateInventory();
            db.execUpdate("DELETE FROM `itemcontainer` WHERE `id`=?", this.itemList.getFirst().getId());
            this.itemList.removeFirst();
            return true;
        }
        else
        {
            player.sendMessage(t("i") + " " + t("cont_rec_remain"));

            db.execUpdate("UPDATE `itemcontainer` SET `amount`=? WHERE `id`=?", tmp.getAmount(), this.itemList.getFirst().getId());
            itemList.getFirst().getItem().setAmount(tmp.getAmount());
            player.updateInventory();
            return true;
        }
    }
    
    public LinkedList<AuctionItem> getItemList()
    {
        return this.itemList;
    }
}
