package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import de.paralleluniverse.Faithcaio.AuctionHouse.*;
import static de.paralleluniverse.Faithcaio.AuctionHouse.Translation.Translator.t;
import java.util.List;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Faithcaio
 */
public class BidCommand extends AbstractCommand
{
    Economy econ = AuctionHouse.getInstance().getEconomy();
    
    public BidCommand(BaseCommand base)
    {
        super(base, "bid");
    }

    public boolean execute(CommandSender sender, String[] args)
    {
        if (!Perm.get().check(sender,"auctionhouse.use.bid")) return true;
        if (sender instanceof ConsoleCommandSender)
        {
            sender.sendMessage(t("bid_console"));
            return true;
        }

        Double bidAmount;
        Auction auction;
        Integer quantity;
        if (args.length < 2)
        {
            sender.sendMessage("/ah bid <AuctionID> <BidAmount>");
            sender.sendMessage("/ah bid i:<Item> [q:<Quantity>] <BidAmount>");
            return true;
        }
        Arguments arguments = new Arguments(args);
        if (arguments.getString("i") != null)
        {
            if (arguments.getMaterial("i") != null)
            {
                if (arguments.getString("q") == null)
                {
                    quantity = arguments.getMaterial("i").getMaxStackSize();
                }
                else
                {
                    if (arguments.getInt("q") == null)
                    {
                        sender.sendMessage(t("e")+" "+t("bid_quantity_num"));
                        return true;
                    }
                    if (arguments.getInt("q") < 1)
                    {
                        sender.sendMessage(t("e")+" "+t("bid_quantity"));
                        return true;
                    }
                    else
                    {
                        quantity = arguments.getInt("q");
                    }
                }

                List<Auction> auctionlist = AuctionManager.getInstance().getAuctionItems(arguments.getMaterial("i"));
                if (auctionlist.isEmpty())
                {
                    sender.sendMessage(t("i")+" "+t("bid_no_auction",arguments.getMaterial("i").toString()));
                    return true;
                }
                AuctionSort sorter = new AuctionSort();
                sorter.SortAuction(auctionlist, "quantity", quantity);
                sorter.SortAuction(auctionlist, "price");
                for (Auction auction2 : auctionlist)
                {
                    if (auction2.owner == Bidder.getInstance((Player) sender))
                    {
                        auctionlist.remove(auction2);
                    }
                }
                if (auctionlist.isEmpty())
                {
                    sender.sendMessage(t("i")+" "+t("bid_no_auc_least",quantity,arguments.getMaterial("i").toString()));
                    return true;
                }
                auction = auctionlist.get(0);//First is Cheapest after Sort
                bidAmount = arguments.getDouble("1");
                if (bidAmount != null)
                {
                    if (auction.owner == Bidder.getInstance((Player) sender))
                    {
                        sender.sendMessage(t("pro")+" "+t("bid_own"));
                        return true;
                    }
                    if (auction.bid(Bidder.getInstance((Player) sender), bidAmount))
                    {
                        Bidder.getInstance((Player) sender).addAuction(auction);
                        this.SendInfo(auction, sender);
                        return true;
                    }
                    return true;
                }
            }
            else
            {
                sender.sendMessage(t("i")+t("no_valid_item",arguments.getString("i")));
                return true;
            }
        }
        Integer id = arguments.getInt("1");
        if (id != null)
        {
            bidAmount = arguments.getDouble("2");
            if (bidAmount != null)
            {
                if (AuctionManager.getInstance().getAuction(id) == null)
                {
                    sender.sendMessage(t("i")+" "+t("auction_no_exist",id));
                    return true;
                }
                auction = AuctionManager.getInstance().getAuction(id);
                if (auction.owner == Bidder.getInstance((Player) sender))
                {
                    sender.sendMessage(t("pro")+" "+t("bid_own"));
                    return true;
                }
                if (auction.bid(Bidder.getInstance((Player) sender), bidAmount))
                {
                    Bidder.getInstance((Player) sender).addAuction(auction);
                    this.SendInfo(auction, sender);
                    return true;
                }
                return true;
            }
        }
        sender.sendMessage(t("e")+" " + t("bid_valid_id",arguments.getString("1")));
        return true;
    }

    public void SendInfo(Auction auction, CommandSender sender)
    {
        sender.sendMessage(t("bid_out",econ.format(auction.bids.peek().getAmount()),auction.item.toString(),auction.id));
        if (!(auction.owner instanceof ServerBidder) && auction.owner.isOnline())
        {
            if (auction.owner.playerNotification)
            {
                auction.owner.getPlayer().sendMessage(t("bid_owner",auction.id));
            }
        }
    }

    @Override
    public String getUsage()
    {
        return super.getUsage() + " <AuctionID> <BidAmount>";
    }

    public String getDescription()
    {
        return t("command_bid");
    }
}
