package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import de.paralleluniverse.Faithcaio.AuctionHouse.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Faithcaio
 */
public class UndoBidCommand extends AbstractCommand{

    public UndoBidCommand(BaseCommand base)
    {
        super("undoBid", base);
    }
 
    public boolean execute(CommandSender sender, String[] args)
    {
        if (args.length < 1)
        {
            sender.sendMessage("/ah undoBid last");
            sender.sendMessage("/ah undoBid <AuctionID>");
            return false;
        }
        Arguments arguments = new Arguments(args);
        
        if (arguments.getString("1").equals("last"))
        {
            //TODO getlatest Bid
            //Bidder.getInstance((Player)sender).activeBids;
        }
        if (arguments.getInt("1")!=null)
        {
            AuctionManager.getInstance().getAuction(arguments.getInt("1")).undobid((Player)sender);
        }
        
        return true;
    }

    @Override
    public String getDescription()
    {
        return "Removes an auction.";
    }
}
