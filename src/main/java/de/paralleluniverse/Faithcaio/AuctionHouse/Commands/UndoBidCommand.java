package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import de.paralleluniverse.Faithcaio.AuctionHouse.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Faithcaio
 */
public class UndoBidCommand extends AbstractCommand
{
    public UndoBidCommand(BaseCommand base)
    {
        super("undoBid", base);
    }
 
    public boolean execute(CommandSender sender, String[] args)
    {
        if (!(sender.hasPermission("auctionhouse.use.undobid")))
        {
            sender.sendMessage("You are not allowed to undo Bids!");
            return true;
        }
        if (args.length < 1)
        {
            sender.sendMessage("/ah undoBid last");
            sender.sendMessage("/ah undoBid <AuctionID>");
            return true;
        }
        Arguments arguments = new Arguments(args);
        Player psender = (Player)sender;
        if (arguments.getString("1").equals("last"))
        {
            if (Bidder.getInstance(psender).getlastAuction(Bidder.getInstance(psender))==null)
            {
                sender.sendMessage("ProTip: You have to bid to undo it!");
                return true;
            }
            if (Bidder.getInstance(psender).getlastAuction(Bidder.getInstance(psender)).undobid(Bidder.getInstance(psender)))
            {
                sender.sendMessage("Info: Bid on last Auction redeemed!");
                return true;
            }
            else
            {
                sender.sendMessage("ProTip: You have to bid to undo it!");
                return true;
            }
        }
        if (arguments.getInt("1")!=null)
        {
            if (AuctionManager.getInstance().getAuction(arguments.getInt("1"))==null)
            {
                sender.sendMessage("Info: Auction #"+arguments.getInt("1")+" does not exist!");
                return true;
            }
            if (AuctionManager.getInstance().getAuction(arguments.getInt("1")).undobid(Bidder.getInstance(psender)))
            {    
                sender.sendMessage("Info: Bid on Auction #"+arguments.getInt("1")+" redeemed!");
                return true;
            }
            else
            {
                sender.sendMessage("Info: You are not the highest Bidder!");
                return true;
            }
        }
        sender.sendMessage("Info: Couldn't undo Bid!");
        return true;
    }

    @Override
    public String getUsage()
    {
        return "/ah undoBid <last|<AuctionId>>";
    }
    public String getDescription()
    {
        return "Removes an auction.";
    }
}
