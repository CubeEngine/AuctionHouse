package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import de.paralleluniverse.Faithcaio.AuctionHouse.AbstractCommand;
import de.paralleluniverse.Faithcaio.AuctionHouse.Auction;
import de.paralleluniverse.Faithcaio.AuctionHouse.AuctionHouse;
import de.paralleluniverse.Faithcaio.AuctionHouse.AuctionManager;
import de.paralleluniverse.Faithcaio.AuctionHouse.BaseCommand;
import de.paralleluniverse.Faithcaio.AuctionHouse.Bidder;
import de.paralleluniverse.Faithcaio.AuctionHouse.ServerBidder;
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
        if (AuctionManager.getInstance().remAllConfirm.contains(sender))
        {
            int max = AuctionManager.getInstance().size();
            if (max == 0)
            {
                sender.sendMessage("Info: No Auctions detected!");
                return true;
            }
            for (int i = max - 1; i >= 0; --i)
            {
                AuctionManager.getInstance().cancelAuction(AuctionManager.getInstance().getIndexAuction(i));
            }
            sender.sendMessage("Info: All Auctions deleted!");
            return true;
        }
        if (AuctionManager.getInstance().remBidderConfirm.containsKey(sender))
        {
            if (AuctionManager.getInstance().remBidderConfirm.get(sender) instanceof ServerBidder)
            {
                int max = ServerBidder.getInstance().getAuctions().size();
                if (max == 0)
                {
                    sender.sendMessage("Info: No ServerAuctions detected!");
                    return true;
                }
                for (int i = max - 1; i >= 0; --i)
                {
                    AuctionManager.getInstance().cancelAuction(ServerBidder.getInstance().getAuctions().get(i));
                }
                sender.sendMessage("Info: All ServerAuctions deleted!");
                return true;
            }
            else
            {
                Bidder player = AuctionManager.getInstance().remBidderConfirm.get(sender);
                int bids = player.getActiveBids().size();
                List<Auction> auctions = player.getActiveBids();
                for (int i = 0; i < bids; ++i)
                {
                    if (auctions.get(i).owner == player)
                    {
                        AuctionHouse.debug("Remove per Player");
                        AuctionManager.getInstance().cancelAuction(auctions.get(i));
                    }
                }
                sender.sendMessage(
                        "Info:Removed " + (player.getActiveBids().size() - bids)
                        + " auctions of " + player.getName());
                return true;
            }
        }
        sender.sendMessage("Error: Nothing to confirm!");
        return true;
    }

    public String getDescription()
    {
        return "Confirms a requested action";
    }
}