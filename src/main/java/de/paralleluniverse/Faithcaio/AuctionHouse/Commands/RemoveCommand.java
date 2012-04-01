package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import de.paralleluniverse.Faithcaio.AuctionHouse.AbstractCommand;
import de.paralleluniverse.Faithcaio.AuctionHouse.Arguments;
import de.paralleluniverse.Faithcaio.AuctionHouse.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Faithcaio
 */
public class RemoveCommand extends AbstractCommand
{    
    public RemoveCommand(BaseCommand base)
    {
        super("remove", base);
    }

    public boolean execute(CommandSender sender, String[] args)
    {
        if (args.length < 1)
        {
            sender.sendMessage("/ah remove <AuctionID>");
            sender.sendMessage("/ah remove all <Player>");//TODO /ah confirm
            //TODO remove all und all from player
            return false;
        }
        Arguments arguments = new Arguments(args);
        
        if (arguments.getString("1").equalsIgnoreCase("all"))
        {
            Bidder player = arguments.getBidder("2");
            if (player == null)
            {
                sender.sendMessage("Info: Player \""+arguments.getString("2")+"\" does not exist or has no Auction!");
                return false;
            }
            if (AuctionHouse.debugMode) sender.sendMessage("Debug:Remove per Player");
            
            if(!(player.activeBids.isEmpty()))
            {    
                int bids = player.activeBids.size();
                while (player.activeBids.size()>0)
                {
                     AuctionManager.getInstance().cancelAuction(player.getAuctions(player.player).get(0));
                }
                sender.sendMessage("Info:Removed "+String.valueOf(bids)+" auctions of "+player.player.toString());
                return true;
            }
            sender.sendMessage("Info: Player \""+arguments.getString("2")+"\" has no Auctions!");
            return true;
        }
        
        Integer id = arguments.getInt("1");
        if (id == null) return false;
        if (AuctionManager.getInstance().getAuction(id)==null)
        {
            sender.sendMessage("Info: Auction"+String.valueOf(id)+"does not exist!");
            return true;
        }
        if (AuctionHouse.debugMode) sender.sendMessage("Debug:Remove per Id");
        AuctionManager.getInstance().cancelAuction(AuctionManager.getInstance().getAuction(id));      
        sender.sendMessage("Info:Removed auction #"+String.valueOf(id));
        return true;
    }

    @Override
        public String getUsage()
    {
        return "/ah remove <<AuctionId>|all <Player>>";
    }
    public String getDescription()
    {
        return "Removes an auction.";
    }
}