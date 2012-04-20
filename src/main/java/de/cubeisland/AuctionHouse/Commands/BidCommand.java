package de.cubeisland.AuctionHouse.Commands;

import de.cubeisland.AuctionHouse.AbstractCommand;
import de.cubeisland.AuctionHouse.Arguments;
import de.cubeisland.AuctionHouse.Auction.Auction;
import de.cubeisland.AuctionHouse.Auction.Bidder;
import de.cubeisland.AuctionHouse.Auction.ServerBidder;
import de.cubeisland.AuctionHouse.AuctionHouse;
import static de.cubeisland.AuctionHouse.AuctionHouse.t;
import de.cubeisland.AuctionHouse.AuctionSort;
import de.cubeisland.AuctionHouse.BaseCommand;
import de.cubeisland.AuctionHouse.Manager;
import de.cubeisland.AuctionHouse.Perm;
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
        Manager manager = Manager.getInstance();
        if (!Perm.get().check(sender,"auctionhouse.command.bid")) return true;
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
            sender.sendMessage(t("bid_title1"));
            sender.sendMessage(t("bid_title2"));
            sender.sendMessage(t("bid_title3"));
            sender.sendMessage(t("bid_use"));
            sender.sendMessage("");
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

                List<Auction> auctions = manager.getAuctionItem(arguments.getMaterial("i"),Bidder.getInstance(sender));
                
                if (auctions.isEmpty())
                {
                    sender.sendMessage(t("i")+" "+t("bid_no_auction",arguments.getMaterial("i").toString()));
                    return true;
                }
                AuctionSort.sortAuction(auctions, "quantity", quantity);
                AuctionSort.sortAuction(auctions, "price");
                if (auctions.isEmpty())
                {
                    sender.sendMessage(t("i")+" "+t("bid_no_auc_least",quantity,arguments.getMaterial("i").toString()));
                    return true;
                }
                auction = auctions.get(0);//First is Cheapest after Sort
                bidAmount = arguments.getDouble("1");
                if (bidAmount != null)
                {
                    if (auction.getOwner() == Bidder.getInstance((Player) sender))
                    {
                        sender.sendMessage(t("pro")+" "+t("bid_own"));
                        return true;
                    }
                    if (auction.bid(Bidder.getInstance((Player) sender), bidAmount))
                    {
                        Bidder.getInstance((Player) sender).addAuction(auction);
                        this.SendBidInfo(auction, sender);
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
                if (manager.getAuction(id) == null)
                {
                    sender.sendMessage(t("i")+" "+t("auction_no_exist",id));
                    return true;
                }
                auction = manager.getAuction(id);
                if (auction.getOwner() == Bidder.getInstance((Player) sender))
                {
                    sender.sendMessage(t("pro")+" "+t("bid_own"));
                    return true;
                }
                if (auction.bid(Bidder.getInstance((Player) sender), bidAmount))
                {
                    Bidder.getInstance((Player) sender).addAuction(auction);
                    this.SendBidInfo(auction, sender);
                    return true;
                }
                return true;
            }
        }
        sender.sendMessage(t("e")+" " + t("bid_valid_id",arguments.getString("1")));
        return true;
    }

    public void SendBidInfo(Auction auction, CommandSender sender)
    {
        sender.sendMessage(t("bid_out",econ.format(auction.getBids().peek().getAmount()),auction.getItem().toString(),auction.getId()));
        if (!(auction.getOwner() instanceof ServerBidder) && auction.getOwner().isOnline())
        {
            if (auction.getOwner().hasNotifyState(Bidder.NOTIFY_STATUS))
            {
                auction.getOwner().getPlayer().sendMessage(t("bid_owner",auction.getId()));
            }
        }
    }

    @Override
    public String getUsage()
    {
        return super.getUsage() + " <#ID> <amount>";
    }

    public String getDescription()
    {
        return t("command_bid");
    }
}
