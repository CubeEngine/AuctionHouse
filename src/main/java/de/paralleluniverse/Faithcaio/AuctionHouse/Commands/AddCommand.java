package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import de.paralleluniverse.Faithcaio.AuctionHouse.*;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
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
        //TODO run commmand as console too
        if (!(sender instanceof Player))
        {
            if (!(sender instanceof ConsoleCommandSender))
              return false; //Invalid Sender
            //else | Console-Command Cheat Items...
            sender.sendMessage("Info: Console creates Auction...");
        }        
        //TODO Take Items from Inventory //correct?
        if ((!((Player)sender).isOp())||(((Player)sender).hasPermission("CheatedItems")))//TODO permission
        {  if(!(((Player)sender).getInventory().contains(newItem))) 
           {
               return false;//Player has not enough Items and is not OP or CheatPermission
           }
           else
               ((Player)sender).getInventory().remove(newItem); //Player has Item -> removeIt
        }
        else
        {   
            if(!(((Player)sender).getInventory().contains(newItem))) 
           {
               sender.sendMessage("Info: Not enough Items! Items were cheated!");
               //OP/CheatPerm has not Item -> CheatIt
           }
           else
               ((Player)sender).getInventory().remove(newItem); //OP has Item -> removeIt
            
        }
        Auction newAuction;
        if (sender instanceof ConsoleCommandSender)
            newAuction = new Auction(newItem,(Player)sender.getServer().getPlayer("Server"),auctionEnd,startBid); //Created Auction as FakePlayer: "Server"
           //TODO anders Server als Player übergeben? vlt Rasselbande als ServerBank (einstellbar in Config)
        else
            newAuction = new Auction(newItem,(Player)sender,auctionEnd,startBid);//Created Auction
        AuctionManager.getInstance().addAuction(newAuction);        //Give Auction to Manager
        Bidder.getInstance((Player)sender).addAuction(newAuction);  //Give Auction to Bidder
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
