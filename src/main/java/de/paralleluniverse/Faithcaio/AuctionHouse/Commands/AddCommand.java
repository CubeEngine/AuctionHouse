package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import de.paralleluniverse.Faithcaio.AuctionHouse.*;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Faithcaio
 */
public class AddCommand extends AbstractCommand{
    
    public AddCommand(BaseCommand base)
    {
        super("add", base);
    }


    public boolean execute(CommandSender sender, String[] args)
    {
        sender.sendMessage("Debug: Added nothing yet");
        int quantity = 1;
        double startBid = 0;
        long auctionEnd = 1;

        if (args.length <= 2) return false;
        if (Material.getMaterial(args[0])!=null)
        {
            sender.sendMessage("Debug: MaterialDetection OK");
        }
            try {quantity = Integer.parseInt(args[1]); }
            catch (NumberFormatException ex) { return false; }
            sender.sendMessage("Debug: AmountDetection OK");
        if (args.length == 3)
        {
            try { startBid = Double.parseDouble(args[2]); }
            catch (NumberFormatException ex) { return false; }
            sender.sendMessage("Debug: StartBid OK");
        }
        else
        {
            sender.sendMessage("Debug: No StartBid Set to 0");
        }
        if (args.length == 4)
        {
            try { auctionEnd = (System.currentTimeMillis()+Integer.parseInt(args[3])*60*60*1000); }
            catch (NumberFormatException ex)  { return false; }
            sender.sendMessage("Debug: StartBid OK");
        }
        else
        {
            sender.sendMessage("Debug: No Auction Length Set to 1h");
        }   
        ItemStack newItem = new ItemStack(Material.getMaterial(args[0]),quantity);
        if (!(sender instanceof Player))return false;//TODO run commmand as console too
        Auction newAuction = new Auction(newItem,(Player)sender,auctionEnd,startBid);//Created Auction
        AuctionManager.getInstance().addAuction(newAuction);
        Bidder.getInstance((Player)sender).addAuction(newAuction);
        sender.sendMessage("");

        for (AbstractCommand command : getBase().getRegisteredCommands())
        {
            sender.sendMessage(command.getUsage());
            sender.sendMessage("    " + command.getDescription());
            sender.sendMessage("");
        }

        return true;
    }

    @Override
    public String getDescription()
    {
        return "Adds an auction";
    }
}
