package de.cubeisland.AuctionHouse.Commands;

import de.cubeisland.AuctionHouse.Auction.Auction;
import de.cubeisland.AuctionHouse.*;
import static de.cubeisland.AuctionHouse.AuctionHouse.t;
import java.util.Collections;
import java.util.List;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Faithcaio
 */
public class SearchCommand extends AbstractCommand
{
    
    private static final AuctionHouse plugin = AuctionHouse.getInstance();
    private static final AuctionHouseConfiguration config = plugin.getConfiguration();
    Economy econ = AuctionHouse.getInstance().getEconomy();
    
    public SearchCommand(BaseCommand base)
    {
        super(base, "search");
    }

    public boolean execute(CommandSender sender, String[] args)
    {
        if (!Perm.get().check(sender,"auctionhouse.search")) return true;
        if (args.length < 1)
        {
            sender.sendMessage("/ah search <Item> [s:<date|id|price>]");
            return true;
        }
        Arguments arguments = new Arguments(args);
        List<Auction> auctionlist;

        if (arguments.getString("1") == null)
        {
            if (arguments.getString("s")!=null)
               sender.sendMessage(t("pro")+" "+t("search_pro")); 
            sender.sendMessage(t("e")+" "+t("invalid_com"));
            return true;
        }
        if (arguments.getMaterial("1") == null)
        {
            sender.sendMessage(t("e")+" "+t("item_no_exist",arguments.getString("1")));
            return true;
        }
        auctionlist = Manager.getInstance().getAuctionItem(arguments.getMaterial("1"));
        if (arguments.getString("s") != null)
        {
            if (arguments.getString("s").equalsIgnoreCase("date"))
            {
                AuctionSort.sortAuction(auctionlist, "date");
                Collections.reverse(auctionlist);
            }
            if (arguments.getString("s").equalsIgnoreCase("id"))
            {
                AuctionSort.sortAuction(auctionlist, "id");
                Collections.reverse(auctionlist);
            }
            if (arguments.getString("s").equalsIgnoreCase("price"))
            {
                AuctionSort.sortAuction(auctionlist, "date");
                AuctionSort.sortAuction(auctionlist, "price");
                Collections.reverse(auctionlist);
            }
        }
        if (auctionlist.isEmpty())
        {
            sender.sendMessage(t("i")+" "+t("search_found"));
        }
        Util.sendInfo(sender, auctionlist);
        return true;
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
