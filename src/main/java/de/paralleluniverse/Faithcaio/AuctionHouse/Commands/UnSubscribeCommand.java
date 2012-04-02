package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import de.paralleluniverse.Faithcaio.AuctionHouse.*;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Faithcaio
 */
public class UnSubscribeCommand extends AbstractCommand{

    public UnSubscribeCommand (BaseCommand base) 
    {
        super("UnSub",base);//TODO auf subscription ändern mit aliases
    }
    public boolean execute(CommandSender sender, String[] args)
    {
        if (args.length<1)
        {
            sender.sendMessage("/ah unsub <i:<AuctionID>");
            sender.sendMessage("/ah unsub <m:<Material>");
            sender.sendMessage("/ah sub <i:<AuctionID>");
            sender.sendMessage("/ah sub <m:<Material>");
            return true;
        }
        Bidder bidder= Bidder.getInstance((Player)sender);
        Arguments arguments = new Arguments(args);
        if (arguments.getString("m")!=null)
        {
            if (arguments.getMaterial("m")!=null)
            {
                bidder.removeSubscription(arguments.getMaterial("m"));
                sender.sendMessage("Info: Removed "+arguments.getString("m")+" from your subscriptionlist.");
                return true;
            }
            sender.sendMessage("Error: Invalid Item!");
            return true;
        }
        if (arguments.getString("i")!=null)
        {
            if (arguments.getInt("i")!=null)
            {
                if (AuctionManager.getInstance().getAuction(arguments.getInt("i"))!=null)
                {
                    if (bidder.removeSubscription(AuctionManager.getInstance().getAuction(arguments.getInt("i"))))
                    {
                        sender.sendMessage("Info: Removed Auction #"+arguments.getString("i")+" from your subscriptionlist.");
                        return true;
                    }
                    sender.sendMessage("Error: You were not subscribed to this auction!");
                    return true;
                }
                sender.sendMessage("Error: Auction #"+arguments.getString("i")+"does not exist!");
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
        return "/ah unsub <i:<AuctionID>]|m:<Material>>";
    }
    public String getDescription()
    {
        return "Manages your Subscriptions";
    }
}