package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import de.paralleluniverse.Faithcaio.AuctionHouse.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Faithcaio
 */
public class SubscribeCommand extends AbstractCommand{

    public SubscribeCommand (BaseCommand base) 
    {
        super("Sub",base);//TODO auf subscription ändern mit aliases
    }
    public boolean execute(CommandSender sender, String[] args)
    {
        if (args.length<1)
        {
            sender.sendMessage("/ah sub <i:<AuctionID>");
            sender.sendMessage("/ah sub <m:<Material>");
            sender.sendMessage("/ah unsub <i:<AuctionID>");
            sender.sendMessage("/ah unsub <m:<Material>");
            return true;
        }
        Bidder bidder= Bidder.getInstance((Player)sender);
        Arguments arguments = new Arguments(args);
        if (arguments.getString("m")!=null)
        {
            if (arguments.getMaterial("m")!=null)
            {
                bidder.addSubscription(arguments.getMaterial("m"));
                sender.sendMessage("Info: Added "+arguments.getString("m")+" to your subscriptionlist. You will be notified if a new auction is started!");
                if (!bidder.playerNotification)
                    sender.sendMessage("Info: Do not forget to turn on notification!");
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
                    sender.sendMessage("Info: Added Auction #"+arguments.getString("i")+" to your subscriptionlist. You will be notified when the auction ends!");
                    if (!bidder.playerNotification)
                        sender.sendMessage("Info: Do not forget to turn on notification!");
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
        return "/ah sub <i:<AuctionID>]|m:<Material>>";
    }
    public String getDescription()
    {
        return "Manages your Subscriptions";
    }
}