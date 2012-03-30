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
public class MAddCommand extends AbstractCommand
{
    private static final AuctionHouse plugin = AuctionHouse.getInstance();
    private static final AuctionHouseConfiguration config = plugin.getConfigurations();
    
    public MAddCommand(BaseCommand base)
    {
        super("madd", base);
    }


    public boolean execute(CommandSender sender, String[] args)
    {
        sender.sendMessage("Debug: Added nothing yet");
        int amount = 1;
        double startBid = 0;
        long auctionEnd = 1;
        ItemStack newItem;
        int auctionAmount;

        if (args.length < 3){
            sender.sendMessage("/ah madd <Item> <Amount> <Auctions> (<StartBid> <Length>)");
            return false;
        }
            Material newMaterial = Material.getMaterial(args[0]);
            if (newMaterial == null) return false;
            sender.sendMessage("Debug: MaterialDetection OK");
        
            try {amount = Integer.parseInt(args[1]); }
            catch (NumberFormatException ex) { return false; }
            sender.sendMessage("Debug: AmountDetection OK");
        newItem = new ItemStack(Material.getMaterial(args[0]),amount);                    
        sender.sendMessage("Debug: "+newItem.toString());
            try { auctionAmount = Integer.parseInt(args[2]); }
            catch (NumberFormatException ex) { return false; }
            if (auctionAmount < 1 )
            {
                sender.sendMessage("Info: AuctionAmount muss be greater than 1");
                return false;
            }
            sender.sendMessage("Debug: AuctionAmount OK");
        if (args.length >= 4)
        {
            try { startBid = Double.parseDouble(args[3]); }
            catch (NumberFormatException ex) { return false; }
            sender.sendMessage("Debug: StartBid OK");
        }
        else
        {
            sender.sendMessage("Debug: No StartBid Set to 0");
        }
        if (args.length >= 5)
        {
            try { auctionEnd = (System.currentTimeMillis()+Integer.parseInt(args[4])*60*60*1000); }
            catch (NumberFormatException ex)  { return false; }
            sender.sendMessage("Debug: AuctionLentgh OK");
        }
        else
        {
            sender.sendMessage("Debug: No Auction Length Set to 1h");
        }  
        if (!(sender instanceof Player))
        {
            if (!(sender instanceof ConsoleCommandSender))
              return false; //Invalid Sender
            //else | Console-Command Cheat Items...
            sender.sendMessage("Debug: Console creates Auction...");
        }        
        //TODO Take Items from Inventory ERROR takes all Items
        if ((!((Player)sender).isOp())||(((Player)sender).hasPermission("CheatedItems")))//TODO permission Check OP does not work
        {  if(!(((Player)sender).getInventory().contains(newItem))) 
           {
               sender.sendMessage("Info: Not enough Items");
               return false;//Player has not enough Items and is not OP or CheatPermission
           }
           else
           {
               sender.sendMessage("Debug: Items were added to Auction");
               ((Player)sender).getInventory().remove(newItem); //Player has Item -> removeIt
           }//TODO not remove ALL newItem
        //TODO Check if greater Stacks are availible
        }
        else
        {   
            if(!(((Player)sender).getInventory().contains(newItem))) 
           {
               sender.sendMessage("Info: Not enough Items! Items were cheated!");
               //OP/CheatPerm has not Item -> CheatIt
           }
           else
           {    
               sender.sendMessage("Info: OP but Items were added to Auction");
               ((Player)sender).getInventory().remove(newItem); //OP has Item -> removeIt
           } 
        }
        //TODO multiple Auctions so läufts nicht denk ich
        for (int i=0;i<auctionAmount;++i)
        {
        Auction newAuction;
        if (sender instanceof ConsoleCommandSender)
            newAuction = new Auction(newItem,(Player)sender.getServer().getPlayer("Server"),auctionEnd,startBid); //Created Auction as FakePlayer: "Server"
           //TODO anders Server als Player übergeben? vlt Rasselbande als ServerBank (einstellbar in Config)
        else
            newAuction = new Auction(newItem,(Player)sender,auctionEnd,startBid);//Created Auction
        sender.sendMessage("Debug: Auction init complete");
        if (AuctionManager.getInstance().freeIds.isEmpty())
        {
            sender.sendMessage("Info: Max Auctions reached! ("+config.auction_maxAuctions+")");
            return false;
        }
        AuctionManager.getInstance().addAuction(newAuction);        //Give Auction to Manager
        sender.sendMessage("Debug: Manager OK");
        Bidder.getInstance((Player)sender).addAuction(newAuction);  //Give Auction to Bidder
        sender.sendMessage("Debug: Bidder OK");
        sender.sendMessage("Info: Auction added succesfully!"+String.valueOf(i+1));        }
        return true;
        
    }

    @Override
    public String getDescription()
    {
        return "Adds multiple acutions.";
    }
}