package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import de.paralleluniverse.Faithcaio.AuctionHouse.*;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.time.DateFormatUtils;
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

            if (arguments.getString("1").equalsIgnoreCase("hand"))
            {
                newItem = ((Player)sender).getItemInHand();
                if (AuctionHouse.debugMode) sender.sendMessage("Debug: Hand ItemDetection OK: "+newItem.toString());
            }
            else 
            {    
                newMaterial = Material.matchMaterial(arguments.getString("1"));
                if (newMaterial == null) return false;
                if (AuctionHouse.debugMode) sender.sendMessage("Debug: Item MaterialDetection OK: "+newMaterial.toString());

                amount = arguments.getInt("2");
                if (amount == -1) return false;
                if (AuctionHouse.debugMode) sender.sendMessage("Debug: Quantity MaterialDetection OK: "+String.valueOf(amount));   

                newItem = new ItemStack(newMaterial,amount);
                if (AuctionHouse.debugMode) sender.sendMessage("Debug: Separate ItemDetection OK: "+newItem.toString()); 
            }

            if (arguments.getString("3")!=null) 
            {
                startBid = arguments.getInt("3");
                if (startBid == -1) return false;
                if (AuctionHouse.debugMode) sender.sendMessage("Debug: StartBid OK");
            }
            else 
            {
                startBid = 0;
                if (AuctionHouse.debugMode) sender.sendMessage("Debug: No StartBid Set to 0");
            }

            if (arguments.getString("4")!=null)
            {
                if (arguments.getInt("4") == -1) return false;
                auctionEnd = (System.currentTimeMillis()+arguments.getInt("4")*60*60*1000);
                if (AuctionHouse.debugMode) sender.sendMessage("Debug: AuctionLentgh OK");
            }
            else 
                if (AuctionHouse.debugMode) sender.sendMessage("Debug: No Auction Length Set to 1h");

            if (arguments.getString("m")!=null)
            {
                multiAuction = arguments.getInt("m");
                if (multiAuction == -1) return false;
                if (AuctionHouse.debugMode) sender.sendMessage("Debug: MultiAuction: "+String.valueOf(multiAuction));
            }
        }
        else 
        {
            if (!(sender instanceof ConsoleCommandSender))
                return false; //Invalid Sender
            //else | is Console-Command
            sender.sendMessage("Info: Creating Auction as Server...");   
        }
        //Command segmented .. 
        //Start Checking Permissions etc.
        if (sender.hasPermission("auctionhouse.use.add"))
        {
            if (!(sender instanceof ConsoleCommandSender))
            {
                if (((Player)sender).getInventory().contains(newItem.getType(),newItem.getAmount()))
                {
                    ((Player)sender).getInventory().removeItem(newItem);
                    //TODO funktioniert nicht richtig!
                    if (AuctionHouse.debugMode) sender.sendMessage("Debug: Items were added to Auction");    
                }
                else
                {
                    if (sender.hasPermission("auctionhouse.cheatItems"))
                    {
                        sender.sendMessage("Info: Not enough Items! Items were cheated!");
                    }
                    else
                    {
                        sender.sendMessage("Info: Not enough Items");
                        return false;
                    }
                }
            }
        }
        //Permission passed ..
        //Start Creating / Register Auction
        Auction newAuction;
        for (int i=0; i<multiAuction; i++)
        {
            if (sender instanceof ConsoleCommandSender)
                newAuction = new Auction(newItem,sender.getServer().getPlayer("Server"),auctionEnd,startBid); 
                //Created Auction as FakePlayer: "Server" //TODO
            else
                newAuction = new Auction(newItem,(Player)sender,auctionEnd,startBid);//Created Auction
            if (AuctionHouse.debugMode) sender.sendMessage("Debug: Auction #"+String.valueOf(i+1)+" init complete");
            
            if (!(this.RegisterAuction(newAuction, sender)))
            {
                sender.sendMessage("Info: Couldn't add all Auctions!"); 
                sender.sendMessage("Info: Max Auctions reached! ("+config.auction_maxAuctions_overall+")"); 
                return false;
            }
        }
                sender.sendMessage("Info: Auction(s) added succesfully!");
                sender.sendMessage(
                  "AuctionHouse: Started "+String.valueOf(multiAuction)+
                  " Auction(s) with "+newItem.toString()+
                  ". StartBid: "+String.valueOf(startBid)+
                  ". Auction ends: "+DateFormatUtils.format(auctionEnd, "dd/MM/yy HH:mm")
                  );
        return true;
    }
    
    private boolean RegisterAuction(Auction auction,CommandSender sender)
    {
        if (AuctionManager.getInstance().freeIds.isEmpty())
            return false;
        AuctionManager.getInstance().addAuction(auction);        //Give Auction to Manager
        if (AuctionHouse.debugMode) sender.sendMessage("Debug: Manager OK");
        Bidder.getInstance((Player)sender).addAuction(auction);  //Give Auction to Bidder
        if (AuctionHouse.debugMode) sender.sendMessage("Debug: Bidder OK");
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
