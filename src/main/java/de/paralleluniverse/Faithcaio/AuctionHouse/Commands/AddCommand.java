package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import de.paralleluniverse.Faithcaio.AuctionHouse.AbstractCommand;
import de.paralleluniverse.Faithcaio.AuctionHouse.Arguments;
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
        //TODO /ah confirm
        ItemStack newItem = null;
        Material newMaterial;
        Integer amount = 1;
        Double startBid = 0.0;
        long auctionEnd = 1;
        Integer multiAuction = 1;
        
        if (sender instanceof Player)
        {
            if (args.length < 1)
            {
                sender.sendMessage("/ah add hand [StartBid] [Length]");
                sender.sendMessage("/ah add <Item> <Amount> [StartBid] [Length]");
                sender.sendMessage("/ah add [m:<quantity>] <Item> <Amount> [StartBid] [Length]");
                //TODO Length mit d h m s
                return true;
            }
            Arguments arguments = new Arguments(args);

            if (arguments.getString("1").equalsIgnoreCase("hand"))
            {
                newItem = ((Player)sender).getItemInHand();
                if (newItem.getType()==Material.AIR)
                {
                    sender.sendMessage("ProTip: You can NOT sell your hands!");
                    return true;
                }
                AuctionHouse.debug("Hand ItemDetection OK: "+newItem.toString());
            }
            else 
            {    
                newMaterial = Material.matchMaterial(arguments.getString("1"));
                if (newMaterial == null)
                {
                    sender.sendMessage("Info: "+arguments.getString("1")+" is not a valid Item");
                    return true;
                }
                AuctionHouse.debug("Item MaterialDetection OK: "+newMaterial.toString());
                if (newMaterial.equals(Material.AIR))
                {
                    sender.sendMessage("Info: AIR ist not a valid Item!");
                    return true;
                }
                amount = arguments.getInt("2");
                if (amount == null)
                {    
                    sender.sendMessage("Info: No Amount given");
                    return true;
                }
                AuctionHouse.debug("Quantity MaterialDetection OK: "+amount);   

                newItem = new ItemStack(newMaterial,amount);
                AuctionHouse.debug("Separate ItemDetection OK: "+newItem.toString()); 
            }

            if (arguments.getString("3")!=null) 
            {
                startBid = arguments.getDouble("3");
                if (startBid == null)
                {
                    sender.sendMessage("Info: Invalid Start Bid Format!");
                    return true;
                }
                AuctionHouse.debug("StartBid OK");
            }
            else 
            {
                startBid = 0.0;
                AuctionHouse.debug("No StartBid Set to 0");
            }

            if (arguments.getString("4")!=null)
            {
                if (arguments.getInt("4") == null)
                {
                    sender.sendMessage("Info: Invalid TimeFormat!");
                    return true;
                }
                auctionEnd = (System.currentTimeMillis()+arguments.getInt("4")*60*60*1000);
                AuctionHouse.debug("AuctionLentgh OK");
            }
            else 
                if (AuctionHouse.debugMode) 
                {
                    auctionEnd = (System.currentTimeMillis()+1*60*60*1000);
                    //TODO Auction Standardlänge in config
                    sender.sendMessage("Debug: No Auction Length Set to 1h");
                }

            if (arguments.getString("m")!=null)
            {
                multiAuction = arguments.getInt("m");
                if (multiAuction == null)
                {
                    sender.sendMessage("Info: MultiAuction m: must be an Number!");
                    return true;
                }
                AuctionHouse.debug("MultiAuction: "+multiAuction);
            }
        }
        else 
        {
            if (!(sender instanceof ConsoleCommandSender))
                return false; //Invalid Sender
            //else | is Console-Command
            sender.sendMessage("Info: Creating Auction as Server...");   
            //TODO ServerBefehle
            
        }
        //Command segmented .. 
        //Start Checking Permissions etc.
        if (sender.hasPermission("auctionhouse.use.add"))
        {
            if (!(sender instanceof ConsoleCommandSender))
            {
                if (((Player)sender).getInventory().contains(newItem.getType(),newItem.getAmount()))
                {
                    //TODO funktioniert nicht richtig!
                    AuctionHouse.debug("Item Amount OK");    
                }
                else
                {
                    if (sender.hasPermission("auctionhouse.cheatItems"))
                    {
                        sender.sendMessage("Info: Not enough Items! Cheat Items...");
                    }
                    else
                    {
                        sender.sendMessage("Info: Not enough Items");
                        return true;
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
            {
                AuctionHouse.log("Console cannot add Auctions (yet)");
                return true;
            
                //    newAuction = new Auction(newItem,sender.getServer().getOfflinePlayer("Server"),auctionEnd,startBid); 
                //TODO Create Auction as FakePlayer: "Server"
            }
            else
                newAuction = new Auction(newItem,(Player)sender,auctionEnd,startBid);//Created Auction
            AuctionHouse.debug("Auction #"+(i+1)+" init complete");
            
            if (!(this.RegisterAuction(newAuction, sender)))
            {
                sender.sendMessage("Info: Couldn't add all Auctions!"); 
                sender.sendMessage("Info: Max Auctions reached! ("+config.auction_maxAuctions_overall+")"); 
                return true;
            }
            else //TODO Auktion Start... remove Items...
            {
                ((Player)sender).getInventory().removeItem(newItem);
            }
        }
                sender.sendMessage("Info: Auction(s) added succesfully!");
                sender.sendMessage(
                  "AuctionHouse: Started "+multiAuction+
                  " Auction(s) with "+newItem.toString()+
                  ". StartBid: "+startBid+
                  ". Auction ends: "+DateFormatUtils.format(auctionEnd, AuctionHouse.getInstance().getConfigurations().auction_timeFormat)
                  );
        return true;
    }
    
    private boolean RegisterAuction(Auction auction,CommandSender sender)
    {
        if (AuctionManager.getInstance().freeIds.isEmpty())
            return false;
        AuctionManager.getInstance().addAuction(auction);        //Give Auction to Manager
        AuctionHouse.debug("Manager OK");
        Bidder.getInstance((Player)sender).addAuction(auction);  //Give Auction to Bidder
        AuctionHouse.debug("Bidder OK");
        return true;
    }
    
    public boolean execute(CommandSender sender, String[] args,int quantity)
    {
        for (int i=0;i<quantity;++i)
          this.execute(sender, args);
        return true;
    }

    @Override
    public String getUsage()
    {
        return "/ah add <hand|<<Item><Amount>>> [StartBid] [Length] [m:<quantity>]";
    }
    public String getDescription()
    {
        return "Adds an auction";
    }
}
