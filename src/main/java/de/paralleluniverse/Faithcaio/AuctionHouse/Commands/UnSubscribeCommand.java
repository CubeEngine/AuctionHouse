package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import de.paralleluniverse.Faithcaio.AuctionHouse.AbstractCommand;
import de.paralleluniverse.Faithcaio.AuctionHouse.Arguments;
import de.paralleluniverse.Faithcaio.AuctionHouse.AuctionManager;
import de.paralleluniverse.Faithcaio.AuctionHouse.BaseCommand;
import de.paralleluniverse.Faithcaio.AuctionHouse.Bidder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Faithcaio
 */
public class UnSubscribeCommand extends AbstractCommand
{
    public UnSubscribeCommand(BaseCommand base)
    {
        super(base, "unsubscribe", "unsub");//TODO auf subscription ändern mit aliases
    }

    public boolean execute(CommandSender sender, String[] args)
    {
        if (args.length < 1)
        {
            sender.sendMessage(super.getUsage() + " <i:<AuctionID>");
            sender.sendMessage(super.getUsage() + " <m:<Material>");
            sender.sendMessage(super.getUsage() + " <i:<AuctionID>");
            sender.sendMessage(super.getUsage() + " <m:<Material>");
            return true;
        }
        Bidder bidder = Bidder.getInstance((Player) sender);
        Arguments arguments = new Arguments(args);
        if (arguments.getString("m") != null)
        {
            if (arguments.getMaterial("m") != null)
            {
                bidder.removeSubscription(arguments.getMaterial("m"));
                sender.sendMessage("Info: Removed " + arguments.getString("m") + " from your subscriptionlist.");
                return true;
            }
            sender.sendMessage("Error: Invalid Item!");
            return true;
        }
        if (arguments.getString("i") != null)
        {
            if (arguments.getInt("i") != null)
            {
                if (AuctionManager.getInstance().getAuction(arguments.getInt("i")) != null)
                {
                    if (bidder.removeSubscription(AuctionManager.getInstance().getAuction(arguments.getInt("i"))))
                    {
                        sender.sendMessage("Info: Removed Auction #" + arguments.getString("i") + " from your subscriptionlist.");
                        return true;
                    }
                    sender.sendMessage("Error: You were not subscribed to this auction!");
                    return true;
                }
                sender.sendMessage("Error: Auction #" + arguments.getString("i") + "does not exist!");
                return true;
            }
            sender.sendMessage("Error: Invalid AuctionID!");
            return true;
        }
        sender.sendMessage("Error: Invalid Command!");
        return true;
    }

    @Override
    public String getUsage()
    {
        return super.getUsage() + " <i:<AuctionID>]|m:<Material>>";
    }

    public String getDescription()
    {
        return "Manages your Subscriptions";
    }
}