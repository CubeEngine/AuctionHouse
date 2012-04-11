package de.cubeisland.AuctionHouse.Commands;

import static de.cubeisland.AuctionHouse.Translation.Translator.t;
import de.cubeisland.AuctionHouse.AbstractCommand;
import de.cubeisland.AuctionHouse.Auction;
import de.cubeisland.AuctionHouse.AuctionHouse;
import de.cubeisland.AuctionHouse.AuctionHouseConfiguration;
import de.cubeisland.AuctionHouse.AuctionManager;
import de.cubeisland.AuctionHouse.BaseCommand;
import de.cubeisland.AuctionHouse.ServerBidder;
import java.util.List;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.time.DateFormatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
/**
 *
 * @author Faithcaio
 */
public class ListCommand extends AbstractCommand
{
    
    private static final AuctionHouse plugin = AuctionHouse.getInstance();
    private static final AuctionHouseConfiguration config = plugin.getConfigurations();
    Economy econ = AuctionHouse.getInstance().getEconomy();
    
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
        String output;
        if (max == 0)
        {
            sender.sendMessage(t("no_detect"));
        }
        for (int i = 0; i < max; ++i)
        {
            Auction auction = auctionlist.get(i);
            output = "";
            output += "#" + auction.id + ": ";
            output += auction.item.toString();
            if (auction.item.getEnchantments().size() > 0)
            {
                output += " "+t("info_out_ench")+" ";
                for (Enchantment enchantment : auction.item.getEnchantments().keySet())
                {
                    output += enchantment.toString() + ":";
                    output += auction.item.getEnchantments().get(enchantment).toString() + " ";
                }
            }
            if (auction.bids.peek().getBidder().equals(auction.owner))
            {
                output += " "+t("info_out_bid",econ.format(auction.bids.peek().getAmount()));
            }
            else
            {
                if (auction.bids.peek().getBidder() instanceof ServerBidder)
                {
                    output += t("info_out_leadserv");
                }
                else
                {
                    output += t("info_out_lead",auction.bids.peek().getBidder().getName());
                }
                output += " "+t("info_out_with",auction.bids.peek().getAmount());
            }
            output += " "+t("info_out_end",
                    DateFormatUtils.format(auction.auctionEnd, config.auction_timeFormat));
        sender.sendMessage(output);
        }
    }

    public String getDescription()
    {
        return t("command_list");
    }
}