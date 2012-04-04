package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import de.paralleluniverse.Faithcaio.AuctionHouse.AbstractCommand;
import de.paralleluniverse.Faithcaio.AuctionHouse.Auction;
import de.paralleluniverse.Faithcaio.AuctionHouse.AuctionHouse;
import de.paralleluniverse.Faithcaio.AuctionHouse.AuctionManager;
import de.paralleluniverse.Faithcaio.AuctionHouse.BaseCommand;
import de.paralleluniverse.Faithcaio.AuctionHouse.ServerBidder;
import java.util.List;
import org.apache.commons.lang.time.DateFormatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;

/**
 *
 * @author Faithcaio
 */
public class ListCommand extends AbstractCommand
{
    public ListCommand(BaseCommand base)
    {
        super(base, "List");
    }

    public boolean execute(CommandSender sender, String[] args)
    {
        this.sendInfo(sender, AuctionManager.getInstance().getAuctions());
        return true;
    }

    public void sendInfo(CommandSender sender, List<Auction> auctionlist)
    {
        int max = auctionlist.size();
        String output = "";
        if (max == 0)
        {
            output += "No auctions found!";
        }
        for (int i = 0; i < max; ++i)
        {
            Auction auction = auctionlist.get(i);
            output = "";
            output += "#" + auction.id + ": ";
            output += auction.item.toString();
            if (auction.item.getEnchantments().size() > 0)
            {
                output += " Enchantments: ";
                for (Enchantment enchantment : auction.item.getEnchantments().keySet())
                {
                    output += enchantment.toString() + ":";
                    output += auction.item.getEnchantments().get(enchantment).toString() + " ";
                }
            }
            if (auction.bids.peek().getBidder().equals(auction.owner))
            {
                output += "StartBid is: " + auction.bids.peek().getAmount();
            }
            else
            {
                if (auction.bids.peek().getBidder() instanceof ServerBidder)
                {
                    output += "Leading Bidder: Server";
                }
                else
                {
                    output += "Leading Bidder: " + auction.bids.peek().getBidder().getName();
                }
                output += " with " + auction.bids.peek().getAmount();
            }
            output += " Auction ends: ";
            output += DateFormatUtils.format(auction.auctionEnd, AuctionHouse.getInstance().getConfigurations().auction_timeFormat);

            sender.sendMessage(output);
        }
    }

    public String getDescription()
    {
        return ""; // TODO description missing!
    }
}