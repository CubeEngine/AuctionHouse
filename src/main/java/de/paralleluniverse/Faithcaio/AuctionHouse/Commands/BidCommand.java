package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import de.paralleluniverse.Faithcaio.AuctionHouse.AbstractCommand;
import de.paralleluniverse.Faithcaio.AuctionHouse.Arguments;
import de.paralleluniverse.Faithcaio.AuctionHouse.Auction;
import de.paralleluniverse.Faithcaio.AuctionHouse.AuctionHouse;
import de.paralleluniverse.Faithcaio.AuctionHouse.AuctionManager;
import de.paralleluniverse.Faithcaio.AuctionHouse.AuctionSort;
import de.paralleluniverse.Faithcaio.AuctionHouse.BaseCommand;
import de.paralleluniverse.Faithcaio.AuctionHouse.Bidder;
import de.paralleluniverse.Faithcaio.AuctionHouse.ServerBidder;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Faithcaio
 */
public class BidCommand extends AbstractCommand
{
    public BidCommand(BaseCommand base)
    {
        super(base, "bid");
    }

    public boolean execute(CommandSender sender, String[] args)
    {
        if (!(sender.hasPermission("auctionhouse.use.bid")))
        {
            sender.sendMessage("You are not allowed to bid on Auctions!");
            return true;
        }
        if (sender instanceof ConsoleCommandSender)
        {
            sender.sendMessage("Console can not bid on Auctions!");
            return true;
        }

        Double bidAmount;
        Auction auction;
        Integer quantity;
        if (args.length < 2)
        {
            sender.sendMessage("/ah bid <AuctionID> <BidAmount>");
            sender.sendMessage("/ah bid i:<Item> [q:<Quantity>]<BidAmount>");//bid on the cheapest Item found
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
                    AuctionHouse.debug("No Quantity | Set to " + quantity);
                }
                else
                {
                    if (arguments.getInt("q") == null)
                    {
                        sender.sendMessage("Error: Quantity q: must be a number.");
                        return true;
                    }
                    if (arguments.getInt("q") < 1)
                    {
                        sender.sendMessage("Error: Quantity q: must be greater than 0");
                        return true;
                    }
                    else
                    {
                        quantity = arguments.getInt("q");
                        AuctionHouse.debug("Quantity Set: " + quantity);
                    }
                }
                AuctionHouse.debug("Bid on Material");

                List<Auction> auctionlist = AuctionManager.getInstance().getAuctionItems(arguments.getMaterial("i"));
                if (auctionlist.isEmpty())
                {
                    sender.sendMessage("Info: No Auctions with " + arguments.getMaterial("i").toString());
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
                    sender.sendMessage("Info: No Auctions with at least " + quantity + " " + arguments.getMaterial("i").toString());
                    return true;
                }
                auction = auctionlist.get(0);//First is Cheapest after Sort
                bidAmount = arguments.getDouble("1");
                if (bidAmount != null)
                {
                    AuctionHouse.debug("BidAmount Set");
                    if (auction.owner == Bidder.getInstance((Player) sender))
                    {
                        sender.sendMessage("ProTip: To Bid on your own auction is unfair!");
                        return true;
                    }
                    if (auction.bid(Bidder.getInstance((Player) sender), bidAmount))
                    {
                        AuctionHouse.debug("Item Bid OK");
                        Bidder.getInstance((Player) sender).addAuction(auction);
                        this.SendInfo(auction, sender);
                        return true;
                    }
                    return true;
                }
            }
            else
            {
                sender.sendMessage("Info: " + arguments.getString("i") + "is not a valid item!");
                return true;
            }
        }
        Integer id = arguments.getInt("1");
        if (id != null)
        {
            AuctionHouse.debug("Bid on ID");
            bidAmount = arguments.getDouble("2");
            if (bidAmount != null)
            {
                AuctionHouse.debug("BidAmount Set");

                if (AuctionManager.getInstance().getAuction(id) == null)
                {
                    sender.sendMessage("Info: Auction #" + id + " does not exist!");
                    return true;
                }
                auction = AuctionManager.getInstance().getAuction(id);
                if (auction.owner == Bidder.getInstance((Player) sender))
                {
                    sender.sendMessage("ProTip: To Bid on your own auction is unfair!");
                    return true;
                }
                if (auction.bid(Bidder.getInstance((Player) sender), bidAmount))
                {
                    AuctionHouse.debug("Id Bid OK");
                    Bidder.getInstance((Player) sender).addAuction(auction);
                    this.SendInfo(auction, sender);
                    return true;
                }
                return true;
            }
        }
        sender.sendMessage("Info: " + arguments.getString("1") + "is not a valid AuctionID!");
        return true;
    }

    public void SendInfo(Auction auction, CommandSender sender)
    {
        sender.sendMessage(
                "You just bid " + auction.bids.peek().getAmount()
                + " on " + auction.item.toString()
                + " | Auction ID:" + auction.id);
        if (!(auction.owner instanceof ServerBidder) && auction.owner.isOnline())
        {
            if (auction.owner.playerNotification)
            {
                auction.owner.getPlayer().sendMessage("Someone bid on your auction #" + auction.id + "!");
            }
        }
    }

    @Override
    public String getUsage()
    {
        return super.getUsage() + " <AuctionID|<i:<Item>[q:<quantity>]>> <BidAmount>";
    }

    public String getDescription()
    {
        return "Bids on an auction.";
    }
}
