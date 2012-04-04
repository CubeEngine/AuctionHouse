package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import de.paralleluniverse.Faithcaio.AuctionHouse.AbstractCommand;
import de.paralleluniverse.Faithcaio.AuctionHouse.Arguments;
import de.paralleluniverse.Faithcaio.AuctionHouse.Auction;
import de.paralleluniverse.Faithcaio.AuctionHouse.AuctionHouse;
import de.paralleluniverse.Faithcaio.AuctionHouse.AuctionHouseConfiguration;
import de.paralleluniverse.Faithcaio.AuctionHouse.AuctionManager;
import de.paralleluniverse.Faithcaio.AuctionHouse.BaseCommand;
import de.paralleluniverse.Faithcaio.AuctionHouse.Bidder;
import de.paralleluniverse.Faithcaio.AuctionHouse.ServerBidder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        super(base, "add");
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
        Integer amount;
        Double startBid = 0.0;
        long auctionEnd = 1;
        Integer multiAuction = 1;

        if (!sender.hasPermission("auctionhouse.use.add"))
        {
            sender.sendMessage("You are not allowed to add an Auction!");
            return true;
        }

        if (args.length < 1)
        {
            sender.sendMessage("/ah add hand [StartBid] [Length] [m:<quantity>]");
            sender.sendMessage("/ah add <Item> <Amount> [StartBid] [Length]");
            sender.sendMessage("/ah add <Item> <Amount> [StartBid] [Length] [m:<quantity>]");
            sender.sendMessage("Length is in d|h|m|s");
            return true;
        }
        Arguments arguments = new Arguments(args);

        if (arguments.getString("m") != null)
        {
            multiAuction = arguments.getInt("m");
            if (multiAuction == null)
            {
                sender.sendMessage("Info: MultiAuction m: must be an Number!");
                return true;
            }
            AuctionHouse.debug("MultiAuction: " + multiAuction);
            if (!(sender.hasPermission("auctionhouse.use.add.multi")))
            {
                sender.sendMessage("You are not allowed to add multiple Auctions!");
                return true;
            }
        }

        if (arguments.getString("1").equalsIgnoreCase("hand"))
        {
            if (!(sender instanceof ConsoleCommandSender))
            {
                newItem = ((Player) sender).getItemInHand();
                if (newItem.getType() == Material.AIR)
                {
                    sender.sendMessage("ProTip: You can NOT sell your hands!");
                    return true;
                }
                AuctionHouse.debug("Hand ItemDetection OK: " + newItem.toString());
                if (arguments.getString("2") != null)
                {
                    startBid = arguments.getDouble("2");
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

                if (arguments.getString("3") != null)
                {
                    Integer length = this.convert(arguments.getString("3"));
                    if (length == null)
                    {
                        sender.sendMessage("Error: Invalid Length Format");
                        return true;
                    }
                    if (length <= config.auction_maxLength)
                    {
                        auctionEnd = (System.currentTimeMillis() + length);
                        AuctionHouse.debug("AuctionLentgh OK");
                    }
                    else
                    {
                        sender.sendMessage("Info: AuctionLength too high! Max: "
                                + DateFormatUtils.format(config.auction_maxLength, "dd:hh:mm:ss"));
                        return true;
                    }
                }
                else
                {
                    auctionEnd = (System.currentTimeMillis() + config.auction_standardLength);
                    AuctionHouse.debug("No Auction Length Set to default");
                }
            }
        }
        else
        {
            newMaterial = Material.matchMaterial(arguments.getString("1"));
            if (newMaterial == null)
            {
                sender.sendMessage("Info: " + arguments.getString("1") + " is not a valid Item");
                return true;
            }
            AuctionHouse.debug("Item MaterialDetection OK: " + newMaterial.toString());
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
            AuctionHouse.debug("Quantity MaterialDetection OK: " + amount);

            newItem = new ItemStack(newMaterial, amount);
            AuctionHouse.debug("Separate ItemDetection OK: " + newItem.toString());

            if (arguments.getString("3") != null)
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

            if (arguments.getString("4") != null)
            {
                Integer length = this.convert(arguments.getString("4"));
                if (length == null)
                {
                    sender.sendMessage("Error: Invalid Length Format");
                    return true;
                }
                if (length <= config.auction_maxLength)
                {
                    auctionEnd = (System.currentTimeMillis() + length);
                    AuctionHouse.debug("AuctionLentgh OK");
                }
                else
                {
                    sender.sendMessage("Info: AuctionLength too high! Max: "
                            + DateFormatUtils.format(config.auction_maxLength, "dd:hh:mm:ss"));
                    return true;
                }
            }
            else
            {
                auctionEnd = (System.currentTimeMillis() + config.auction_standardLength);
                AuctionHouse.debug("No Auction Length Set to default");
            }
        }

        if (sender instanceof ConsoleCommandSender)
        {
            sender.sendMessage("Info: Creating Auction as Server...");
        }

        if (newItem == null)
        {
            sender.sendMessage("ProTip: You are a Server. You have no hands!");
            return true;
        }
        ItemStack removeItem = newItem.clone();
        removeItem.setAmount(removeItem.getAmount() * multiAuction);

        if (!(sender instanceof ConsoleCommandSender))
        {
            if (((Player) sender).getInventory().contains(removeItem.getType(), removeItem.getAmount()))
            {
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

        for (ItemStack item : config.auction_blacklist)
        {
            if (item.getType().equals(newItem.getType()))
            {
                sender.sendMessage("Error: This Item is blacklisted!");
                return true;
            }
        }
        Auction newAuction;
        for (int i = 0; i < multiAuction; ++i)
        {
            if (sender instanceof ConsoleCommandSender)
            {
                newAuction = new Auction(newItem, ServerBidder.getInstance(), auctionEnd, startBid);
                AuctionHouse.log("Console adds Auction");
            }
            else
            {
                newAuction = new Auction(newItem, Bidder.getInstance((Player) sender), auctionEnd, startBid);//Created Auction
            }
            AuctionHouse.debug("Auction #" + (i + 1) + " init complete");

            if (!(this.RegisterAuction(newAuction, sender)))
            {
                sender.sendMessage("Info: Couldn't add all Auctions!");
                sender.sendMessage("Info: Max Auctions reached! (" + config.auction_maxAuctions_overall + ")");
                return true;
            }
        }

        if (!(sender instanceof ConsoleCommandSender))
        {

            ((Player) sender).getInventory().removeItem(removeItem);
            AuctionHouse.debug("UserAuction(s) added succesfully!");
        }
        else
        {
            AuctionHouse.debug("ServerAuction(s) added succesfully!");
        }

        sender.sendMessage(
                "AuctionHouse: Started " + multiAuction
                + " Auction(s) with " + newItem.toString()
                + ". StartBid: " + startBid
                + ". Auction ends: " + DateFormatUtils.format(auctionEnd, config.auction_timeFormat));
        return true;
    }

    private boolean RegisterAuction(Auction auction, CommandSender sender)
    {
        if (AuctionManager.getInstance().isEmpty())
        {
            return false;
        }
        AuctionManager.getInstance().addAuction(auction);
        AuctionHouse.debug("Manager OK");

        if (sender instanceof ConsoleCommandSender)
        {
            ServerBidder.getInstance().addAuction(auction);
        }
        else
        {
            Bidder.getInstance((Player) sender).addAuction(auction);
        }
        AuctionHouse.debug("Bidder OK");

        for (Bidder bidder : Bidder.getInstances().values())
        {
            if (bidder.getMatSub().contains(new ItemStack(auction.item.getType(), 1, auction.item.getDurability())))
            {
                bidder.addSubscription(auction);
            }
        }
        return true;
    }

    public boolean execute(CommandSender sender, String[] args, int quantity)
    {
        for (int i = 0; i < quantity; ++i)
        {
            this.execute(sender, args);
        }
        return true;
    }

    @Override
    public String getUsage()
    {
        return super.getUsage() + " <hand|<<Item><Amount>>> [StartBid] [Length] [m:<quantity>]";
    }

    public String getDescription()
    {
        return "Adds an auction";
    }

    public Integer convert(String str) //ty quick_wango
    {
        Pattern pattern = Pattern.compile("^(\\d+)([smhd])?$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        matcher.find();
        int tmp;
        try
        {
            tmp = Integer.valueOf(String.valueOf(matcher.group(1)));
        }
        catch (NumberFormatException e)
        {
            return null;
        }
        catch (IllegalStateException ex)
        {
            return null;
        }
        if (tmp == -1)
        {
            return -1;
        }
        String unitSuffix = matcher.group(2);
        if (unitSuffix == null)
        {
            unitSuffix = "m";
        }
        switch (unitSuffix.toLowerCase().charAt(0))
        {
            case 'd':
                tmp *= 24;
            case 'h':
                tmp *= 60;
            case 'm':
                tmp *= 60;
            case 's':
                tmp *= 1000;
        }
        return tmp;
    }
}