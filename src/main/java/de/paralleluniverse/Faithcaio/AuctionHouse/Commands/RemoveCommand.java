package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

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
            return false;
        }
        Arguments arguments = new Arguments(args);
        
        if (arguments.getString("1").equalsIgnoreCase("all"))
        {
            Player player = arguments.getPlayer("2");
            if (player == null) return false;
            if (AuctionHouse.debugMode) sender.sendMessage("Debug:delete per Player");
            
            if(!(Bidder.getInstance(player).activeBids.isEmpty()))
            {    
                int bids = Bidder.getInstance(player).activeBids.size();
                if (bids == 0) return false;
                for (int i=0; i<bids; ++i)
                    AuctionManager.getInstance().cancelAuction(Bidder.getInstance(player).getAuctions(player).get(i));
                sender.sendMessage("Info:Deleted "+String.valueOf(bids)+" auctions of "+player.toString());
                return true;
            }
        }
        
        int id =  arguments.getInt("1");
        if (id == -1) return false;
        if (AuctionHouse.debugMode) sender.sendMessage("Debug:delete per Id");
        AuctionManager.getInstance().cancelAuction(AuctionManager.getInstance().getAuction(id));      
        sender.sendMessage("Info:Deleted auction #"+String.valueOf(id));
        return true;
    }

    @Override
    public String getDescription()
    {
        return "Removes an auction.";
    }
}