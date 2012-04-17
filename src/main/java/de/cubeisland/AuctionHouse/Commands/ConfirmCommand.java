package de.cubeisland.AuctionHouse.Commands;

import static de.cubeisland.AuctionHouse.Translation.Translator.t;
import de.cubeisland.AuctionHouse.AbstractCommand;
import de.cubeisland.AuctionHouse.Auction;
import de.cubeisland.AuctionHouse.AuctionHouse;
import de.cubeisland.AuctionHouse.Manager;
import de.cubeisland.AuctionHouse.BaseCommand;
import de.cubeisland.AuctionHouse.Bidder;
import de.cubeisland.AuctionHouse.ServerBidder;
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
        if (manager.remAllConfirm.contains(Bidder.getInstance(sender)))
        {
            manager.remAllConfirm.remove(Bidder.getInstance(sender));
            int max = manager.size();
            if (max == 0)
            {
                sender.sendMessage(t("i")+" "+t("no_detect"));
                return true;
            }
            for (int i = max - 1; i >= 0; --i)
            {
                manager.cancelAuction(manager.getIndexAuction(i));
            }
            sender.sendMessage(t("i")+" "+t("confirm_del"));
            return true;
        }
        if (manager.remBidderConfirm.containsKey(Bidder.getInstance(sender)))
        {
            if (manager.remBidderConfirm.get(Bidder.getInstance(sender)) instanceof ServerBidder)
            {
                int max = ServerBidder.getInstance().getAuctions().size();
                if (max == 0)
                {
                    sender.sendMessage(t("i")+" "+t("confirm_no_serv"));
                    manager.remBidderConfirm.remove(Bidder.getInstance(sender));
                    return true;
                }
                for (int i = max - 1; i >= 0; --i)
                {
                    manager.cancelAuction(ServerBidder.getInstance().getAuctions().get(i));
                }
                sender.sendMessage(t("i")+" "+t("confirm_del_serv"));
                manager.remBidderConfirm.remove(Bidder.getInstance(sender));
                return true;
            }
            else
            {
                Bidder player = manager.remBidderConfirm.get(Bidder.getInstance(sender));
                int bids = player.getActiveBids().size();
                List<Auction> auctions = player.getActiveBids();
                for (int i = 0; i < bids; ++i)
                {
                    if (auctions.get(i).owner == player)
                    {
                        manager.cancelAuction(auctions.get(i));
                    }
                }
                sender.sendMessage(t("i")+" "+t("confirm_rem",bids,player.getName()));
                manager.remBidderConfirm.remove(Bidder.getInstance(sender));
                return true;
            }
        }
        if (manager.remSingleConfirm.containsKey(Bidder.getInstance(sender)))
        {
            ItemStack item = Manager.getInstance().getAuction(manager.remSingleConfirm.get(Bidder.getInstance(sender))).item;
            Manager.getInstance().cancelAuction(Manager.getInstance().getAuction(manager.remSingleConfirm.get(Bidder.getInstance(sender))));
            sender.sendMessage(t("i")+" "+t("rem_id",manager.remSingleConfirm.get(Bidder.getInstance(sender)),item.getType().toString()+"x"+item.getAmount()));
            manager.remBidderConfirm.remove(Bidder.getInstance(sender));
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