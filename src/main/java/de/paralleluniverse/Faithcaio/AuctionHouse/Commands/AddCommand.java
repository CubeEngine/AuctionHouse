package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import de.paralleluniverse.Faithcaio.AuctionHouse.*;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Faithcaio
 */
public class AddCommand extends AbstractCommand
{    
    private static AddCommand instance = null;
    private static final AuctionHouse plugin = AuctionHouse.getInstance();
    private static final AuctionHouseConfiguration config = plugin.getConfigurations();
    
    
    public AddCommand(BaseCommand base)
    {
        super("add", base);
    }

    public static AddCommand getInstance()
    {
        if (instance == null)
        {
            BaseCommand base = new BaseCommand(plugin);
            instance = new AddCommand(base);
        }
        return instance;
    }
    
    public boolean execute(CommandSender sender, String[] args)
    {
        ItemStack newItem = null;
        Material newMaterial;
        int amount = 1;
        double startBid = 0;
        long auctionEnd = 1;
        int multiAuction = 1;
        
        if (sender instanceof Player)
        {
            if (args.length < 1)
            {
                sender.sendMessage("/ah add hand [StartBid] [Length]");
                sender.sendMessage("/ah add <Item> <Amount> [StartBid] [Length]");
                sender.sendMessage("/ah add [m:<quantity>] <Item> <Amount> [StartBid] [Length]");
                return false;
            }
            Arguments arguments = new Arguments(args);

            if (arguments.getParam("1").equalsIgnoreCase("hand"))
            {
                newItem = ((Player)sender).getItemInHand();
                sender.sendMessage("Debug: Hand ItemDetection OK");
            }
            else 
            {    
                newMaterial = Material.matchMaterial(arguments.getParam("1"));
                if (newMaterial == null) return false;
                sender.sendMessage("Debug: Item MaterialDetection OK: "+newMaterial.toString());

                try {amount = Integer.parseInt(arguments.getParam("2")); }
                catch (NumberFormatException ex) {return false; }
                sender.sendMessage("Debug: Quantity MaterialDetection OK");   

                newItem = new ItemStack(newMaterial,amount);
                sender.sendMessage("Debug: Separate ItemDetection OK"); 
            }

            if (arguments.getParam("3")!=null) 
            {
                try {startBid = Integer.parseInt(arguments.getParam("3")); }
                catch (NumberFormatException ex) {return false; }
                sender.sendMessage("Debug: StartBid OK");
            }
            else sender.sendMessage("Debug: No StartBid Set to 0");

            if (arguments.getParam("4")!=null)
            {
                try 
                { 
                    auctionEnd = (System.currentTimeMillis()+Integer.parseInt(arguments.getParam("4"))*60*60*1000);
                }
                catch (NumberFormatException ex)  { return false; }
                sender.sendMessage("Debug: AuctionLentgh OK");
            }
            else sender.sendMessage("Debug: No Auction Length Set to 1h");

            if (arguments.getParam("m")!=null)
            {
                try {multiAuction = Integer.parseInt(arguments.getParam("m")); }
                catch (NumberFormatException ex) {return false; }
                sender.sendMessage("Debug: MultiAuction: "+String.valueOf(multiAuction));
            }
        }
        else 
        {
            if (!(sender instanceof ConsoleCommandSender))
                return false; //Invalid Sender
            //else | is Console-Command
            sender.sendMessage("Info: Creating Auction as Server...");   
        }

                
        //TODO Take Items from Inventory ERROR takes all Items
        if ((!((Player)sender).isOp())||(((Player)sender).hasPermission("CheatedItems")))//TODO permission Check / OP does not work
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
        Auction newAuction;
        if (sender instanceof ConsoleCommandSender)
            newAuction = new Auction(newItem,(Player)sender.getServer().getPlayer("Server"),auctionEnd,startBid); //Created Auction as FakePlayer: "Server"
           //TODO anders Server als Player übergeben? vlt Rasselbande als ServerBank (einstellbar in Config)
        else
            newAuction = new Auction(newItem,(Player)sender,auctionEnd,startBid);//Created Auction
        sender.sendMessage("Debug: Auction init complete");
        if (AuctionManager.getInstance().freeIds.isEmpty())
        {
            sender.sendMessage("Info: Max Auctions reached! ("+config.auction_maxAuctions_overall+")");
            return false;
        }
        AuctionManager.getInstance().addAuction(newAuction);        //Give Auction to Manager
        sender.sendMessage("Debug: Manager OK");
        Bidder.getInstance((Player)sender).addAuction(newAuction);  //Give Auction to Bidder
        sender.sendMessage("Debug: Bidder OK");

        sender.sendMessage("Info: Auction added succesfully!");
        return true;
    }
    
    public boolean execute(CommandSender sender, String[] args,int quantity)
    {
        for (int i=0;i<quantity;++i)
          this.execute(sender, args);
        return true;
    }

    @Override
    public String getDescription()
    {
        return "Adds an auction";
    }
}
