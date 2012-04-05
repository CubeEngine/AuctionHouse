package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import static de.paralleluniverse.Faithcaio.AuctionHouse.Translation.Translator.t;
import de.paralleluniverse.Faithcaio.AuctionHouse.*;
import java.util.List;
import org.apache.commons.lang.time.DateFormatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;

/**
 *
 * @author Faithcaio
 */
public class SearchCommand extends AbstractCommand
{
    
    private static final AuctionHouse plugin = AuctionHouse.getInstance();
    private static final AuctionHouseConfiguration config = plugin.getConfigurations();
    
    public SearchCommand(BaseCommand base)
    {
        super(base, "search");
    }

    public boolean execute(CommandSender sender, String[] args)
    {
        if (!(sender.hasPermission("auctionhouse.search")))
        {
            sender.sendMessage(t("perm")+" "+t("search_perm"));
            return true;
        }
        if (args.length < 1)
        {
            sender.sendMessage("/ah search <Item> [s:<date|id|price>]");
            return true;
        }
        Arguments arguments = new Arguments(args);
        List<Auction> auctionlist;

        if (arguments.getString("1") == null)
        {
            sender.sendMessage(t("pro")+" "+t("search_pro"));
            return true;
        }
        if (arguments.getMaterial("1") != null)
        {
            auctionlist = AuctionManager.getInstance().getAuctionItems(arguments.getMaterial("1"));
        }
        else
        {
            sender.sendMessage(t("e")+" "+t("item")+" "+t("no_exist"));
            return true;
        }
        if (arguments.getString("s") != null)
        {
            AuctionSort sorter = new AuctionSort();
            if (arguments.getString("s").equalsIgnoreCase("date"))
            {
                sorter.SortAuction(auctionlist, "date");
            }
            if (arguments.getString("s").equalsIgnoreCase("id"))
            {
                sorter.SortAuction(auctionlist, "id");
            }
            if (arguments.getString("s").equalsIgnoreCase("price"))
            {
                sorter.SortAuction(auctionlist, "price");
            }
        }
        if (auctionlist.isEmpty())
        {
            sender.sendMessage(t("i")+" "+t("search_found"));
        }
        this.sendInfo(sender, auctionlist);
        return true;
    }

    public void sendInfo(CommandSender sender, List<Auction> auctionlist)
    {
        int max = auctionlist.size();
        for (int i = 0; i < max; ++i)
        {
            Auction auction = auctionlist.get(i);
            String output = "";
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
                output += " "+t("info_out_bid",auction.bids.peek().getAmount());
            }
            else //TODO change it to %s etc
            {
                if (auction.bids.peek().getBidder() instanceof ServerBidder)
                {
                    output += t("info_out_leadserv");
                }
                else
                {
                    output += t("info_out_lead",auction.bids.peek().getBidder().getName());
                }
                output += " "+t("with",auction.bids.peek().getAmount());
            }
            output += " "+t("info_out_end",
                    DateFormatUtils.format(auction.auctionEnd, config.auction_timeFormat));

            sender.sendMessage(output);
        }
    }

    @Override
    public String getDescription()
    {
        return t("command_search");
    }

    @Override
    public String getUsage()
    {
        return super.getUsage() + " <Item> [s:<date|id|price>]";
    }
}
