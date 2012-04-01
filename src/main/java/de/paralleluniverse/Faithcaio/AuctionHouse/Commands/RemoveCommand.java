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
            sender.sendMessage("/ah remove p:<Player>");//TODO /ah confirm
            sender.sendMessage("/ah remove all");
            return true;
        }
        Arguments arguments = new Arguments(args);
        
        if (arguments.getString("1")!=null)
        {
            if (arguments.getString("1").equalsIgnoreCase("all"))
            {
                //TODO permission
                int max = AuctionManager.getInstance().auctions.size();
                if (max == 0) sender.sendMessage("Info: No Auctions detected!");
                for (int i=max-1;i>=0;--i)
                {
                    AuctionManager.getInstance().cancelAuction(AuctionManager.getInstance().auctions.get(i));
                }
                sender.sendMessage("Info: All Auctions deleted!");
                return true;
            }
            else
            {
                Integer id = arguments.getInt("1");
                if (id != null)
                {
                    if (AuctionManager.getInstance().getAuction(id)==null)
                    {
                        sender.sendMessage("Error: Auction"+id+"does not exist!");
                        return true;    
                    }
                    AuctionHouse.debug("Remove per Id");
                        AuctionManager.getInstance().cancelAuction(AuctionManager.getInstance().getAuction(id));      
                    sender.sendMessage("Info:Removed auction #"+id);
                    return true;
                }
                sender.sendMessage("Error: Invalid Command!");
                return true;
            }
        }
        else
        {
            Bidder player = arguments.getBidder("p");
            if (player == null)
            {
                sender.sendMessage("Info: Player \""+arguments.getString("p")+"\" does not exist or has no Auction!");
                return true;
            }
            else
            {
                AuctionHouse.debug("Remove per Player");

                if(!(player.activeBids.isEmpty()))
                {    
                    int bids = player.activeBids.size();    
                    while (player.activeBids.size()>0)
                    {
                        AuctionManager.getInstance().cancelAuction(player.getAuctions(player.player).get(0));
                    }
                    sender.sendMessage("Info:Removed "+bids+" auctions of "+player.player.toString());
                    return true;
                }
                sender.sendMessage("Info: Player \""+arguments.getString("p")+"\" has no Auctions!");
                return true;
            }
        }
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