package de.cubeisland.AuctionHouse.Commands;

import static de.cubeisland.AuctionHouse.Translation.Translator.t;
import de.cubeisland.AuctionHouse.AbstractCommand;
import de.cubeisland.AuctionHouse.Auction;
import de.cubeisland.AuctionHouse.AuctionHouse;
import de.cubeisland.AuctionHouse.AuctionManager;
import de.cubeisland.AuctionHouse.BaseCommand;
import de.cubeisland.AuctionHouse.Bidder;
import de.cubeisland.AuctionHouse.ServerBidder;
import java.util.List;
import org.bukkit.command.CommandSender;

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
        AuctionManager manager = AuctionManager.getInstance();
        if (manager.remAllConfirm.contains(sender))
        {
            manager.remAllConfirm.remove(sender);
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
        if (manager.remBidderConfirm.containsKey(sender))
        {
            manager.remBidderConfirm.remove(sender);
            if (manager.remBidderConfirm.get(sender) instanceof ServerBidder)
            {
                int max = ServerBidder.getInstance().getAuctions().size();
                if (max == 0)
                {
                    sender.sendMessage(t("i")+" "+t("confirm_no_serv"));
                    return true;
                }
                for (int i = max - 1; i >= 0; --i)
                {
                    manager.cancelAuction(ServerBidder.getInstance().getAuctions().get(i));
                }
                sender.sendMessage(t("i")+" "+t("confirm_del_serv"));
                return true;
            }
            else
            {
                Bidder player = manager.remBidderConfirm.get(sender);
                int bids = player.getActiveBids().size();
                List<Auction> auctions = player.getActiveBids();
                for (int i = 0; i < bids; ++i)
                {
                    if (auctions.get(i).owner == player)
                    {
                        manager.cancelAuction(auctions.get(i));
                    }
                }
                sender.sendMessage(t("i")+" "+t("confirm_rem",(player.getActiveBids().size() - bids),player.getName()));
                return true;
            }
        }
        sender.sendMessage(t("e")+" "+t("confirm_no_req"));
        return true;
    }

    public String getDescription()
    {
        return t("command_confirm");
    }
}