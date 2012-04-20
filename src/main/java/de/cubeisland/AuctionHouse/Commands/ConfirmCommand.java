package de.cubeisland.AuctionHouse.Commands;

import de.cubeisland.AuctionHouse.AbstractCommand;
import de.cubeisland.AuctionHouse.Auction.Auction;
import de.cubeisland.AuctionHouse.Auction.Bidder;
import de.cubeisland.AuctionHouse.Auction.ServerBidder;
import de.cubeisland.AuctionHouse.AuctionHouse;
import static de.cubeisland.AuctionHouse.AuctionHouse.t;
import de.cubeisland.AuctionHouse.BaseCommand;
import de.cubeisland.AuctionHouse.Manager;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Faithcaio
 */
public class ConfirmCommand extends AbstractCommand
{
    public ConfirmCommand(BaseCommand base)
    {
        super(base, "confirm");
    }

    public boolean execute(CommandSender sender, String[] args)
    {
        Manager manager = Manager.getInstance();
        if (manager.getAllConfirm().contains(Bidder.getInstance(sender)))
        {
            manager.getAllConfirm().remove(Bidder.getInstance(sender));
            int max = manager.size();
            if (max == 0)
            {
                sender.sendMessage(t("i")+" "+t("no_detect"));
                return true;
            }
            for (int i = max - 1; i >= 0; --i)
            {
                manager.cancelAuction(manager.getIndexAuction(i), false);
            }
            sender.sendMessage(t("i")+" "+t("confirm_del"));
            return true;
        }
        if (manager.getBidderConfirm().containsKey(Bidder.getInstance(sender)))
        {
            if (manager.getBidderConfirm().get(Bidder.getInstance(sender)) instanceof ServerBidder)
            {
                int max = ServerBidder.getInstance().getAuctions().size();
                if (max == 0)
                {
                    sender.sendMessage(t("i")+" "+t("confirm_no_serv"));
                    manager.getBidderConfirm().remove(Bidder.getInstance(sender));
                    return true;
                }
                for (int i = max - 1; i >= 0; --i)
                {
                    manager.cancelAuction(ServerBidder.getInstance().getAuctions().get(i), false);
                }
                sender.sendMessage(t("i")+" "+t("confirm_del_serv"));
                manager.getBidderConfirm().remove(Bidder.getInstance(sender));
                return true;
            }
            else
            {
                Bidder player = manager.getBidderConfirm().get(Bidder.getInstance(sender));
                ArrayList<Auction> auctions = (ArrayList<Auction>)player.getActiveBids().clone();
                int max = auctions.size();
                for (Auction auction : auctions)
                {
                    if (auction.getOwner() == player)
                    {
                        if (AuctionHouse.getInstance().getConfiguration().auction_removeTime <
                            System.currentTimeMillis() - auction.getBids().firstElement().getTimestamp())
                        {
                            if (!sender.hasPermission("aucionhouse.delete.player.other"))
                                {
                                     sender.sendMessage(t("i")+" "+t("rem_time"));
                                     continue;
                                }
                           
                        }
                        manager.cancelAuction(auction , false);
                    }
                }
                sender.sendMessage(t("i")+" "+t("confirm_rem",max,player.getName()));
                manager.getBidderConfirm().remove(Bidder.getInstance(sender));
                return true;
            }
        }
        if (manager.getSingleConfirm().containsKey(Bidder.getInstance(sender)))
        {
            ItemStack item = Manager.getInstance().getAuction(manager.getSingleConfirm().get(Bidder.getInstance(sender))).getItem();
            manager.cancelAuction(manager.getAuction(manager.getSingleConfirm().get(Bidder.getInstance(sender))), false);
            sender.sendMessage(t("i")+" "+t("rem_id",manager.getSingleConfirm().get(Bidder.getInstance(sender)),item.getType().toString()+"x"+item.getAmount()));
            manager.getBidderConfirm().remove(Bidder.getInstance(sender));
            return true;
        }
        sender.sendMessage(t("e")+" "+t("confirm_no_req"));
        return true;
    }

    public String getDescription()
    {
        return t("command_confirm");
    }
}