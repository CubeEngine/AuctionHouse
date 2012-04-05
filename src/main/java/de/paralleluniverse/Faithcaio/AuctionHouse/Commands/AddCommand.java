package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;


import static de.paralleluniverse.Faithcaio.AuctionHouse.Translation.Translator.t;
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
            sender.sendMessage(t("perm")+" "+t("add_Auc_perm"));
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
                sender.sendMessage(t("i")+" "+t("add_multi_number"));
                return true;
            }
            AuctionHouse.debug("MultiAuction: " + multiAuction);
            if (!(sender.hasPermission("auctionhouse.use.add.multi")))
            {
                sender.sendMessage(t("perm")+" "+t("add_multi_perm"));
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
                    sender.sendMessage(t("pro")+" "+t("add_sell_hand"));
                    return true;
                }
                if (arguments.getString("2") != null)
                {
                    startBid = arguments.getDouble("2");
                    if (startBid == null)
                    {
                        sender.sendMessage(t("i")+" "+t("no_invalid_time"));
                        return true;
                    }
                }
                else
                {
                    startBid = 0.0;
                }

                if (arguments.getString("3") != null)
                {
                    Integer length = this.convert(arguments.getString("3"));
                    if (length == null)
                    {
                        sender.sendMessage(t("e")+" "+t("add_invalid_length"));
                        return true;
                    }
                    if (length <= config.auction_maxLength)
                    {
                        auctionEnd = (System.currentTimeMillis() + length);
                    }
                    else
                    {
                        sender.sendMessage(t("i")+" "+t("add_max_length",
                                DateFormatUtils.format(config.auction_maxLength, "dd:hh:mm:ss")));
                        return true;
                    }
                }
                else
                {
                    auctionEnd = (System.currentTimeMillis() + config.auction_standardLength);
                }
            }
        }
        else
        {
            newMaterial = Material.matchMaterial(arguments.getString("1"));
            if (newMaterial == null)
            {
                sender.sendMessage(t("i") + " " + arguments.getString("1") + t("add_valid_item"));
                return true;
            }
            if (newMaterial.equals(Material.AIR))
            {
                sender.sendMessage(t("i") + " AIR" + t("add_valid_item"));
                return true;
            }
            amount = arguments.getInt("2");
            if (amount == null)
            {
                sender.sendMessage(t("1") + " " + t("add_no_amount"));
                return true;
            }
            newItem = new ItemStack(newMaterial, amount);
            if (arguments.getString("3") != null)
            {
                startBid = arguments.getDouble("3");
                if (startBid == null)
                {
                    sender.sendMessage(t("i") + " "+t("add_invalid_startbid"));
                    return true;
                }
            }
            else
            {
                startBid = 0.0;
            }

            if (arguments.getString("4") != null)
            {
                Integer length = this.convert(arguments.getString("4"));
                if (length == null)
                {
                    sender.sendMessage(t("e") + " " + t("add_valid_item"));
                    return true;
                }
                if (length <= config.auction_maxLength)
                {
                    auctionEnd = (System.currentTimeMillis() + length);
                }
                else
                {
                    sender.sendMessage(t("i")+" "+t("add_max_length")+" "+
                              DateFormatUtils.format(config.auction_maxLength, "dd:hh:mm:ss"));
                    return true;
                }
            }
            else
            {
                auctionEnd = (System.currentTimeMillis() + config.auction_standardLength);
            }
        }

        if (sender instanceof ConsoleCommandSender)
        {
            sender.sendMessage(t("i")+" "+t("add_server_create"));
        }

        if (newItem == null)
        {
            sender.sendMessage(t("pro")+" "+t("add_server_nohand"));
            return true;
        }
        ItemStack removeItem = newItem.clone();
        removeItem.setAmount(removeItem.getAmount() * multiAuction);

        if (!(sender instanceof ConsoleCommandSender))
        {
            if (!((Player) sender).getInventory().contains(removeItem.getType(), removeItem.getAmount()))
            {
                if (sender.hasPermission("auctionhouse.cheatItems"))
                {
                    sender.sendMessage(t("i")+" "+t("add_enough_item")+" "+t("add_cheat"));
                }
                else
                {
                    sender.sendMessage(t("i")+" "+t("add_enough_item"));
                    return true;
                }
            }
        }

        for (ItemStack item : config.auction_blacklist)
        {
            if (item.getType().equals(newItem.getType()))
            {
                sender.sendMessage(t("e")+" "+t("add_blacklist"));
                return true;
            }
        }
        Auction newAuction;
        for (int i = 0; i < multiAuction; ++i)
        {
            if (sender instanceof ConsoleCommandSender)
            {
                newAuction = new Auction(newItem, ServerBidder.getInstance(), auctionEnd, startBid);
            }
            else
            {
                newAuction = new Auction(newItem, Bidder.getInstance((Player) sender), auctionEnd, startBid);//Created Auction
            }

            if (!(this.RegisterAuction(newAuction, sender)))
            {
                sender.sendMessage(t("i")+" "+t("add_all_stop"));
                sender.sendMessage(t("i")+" "+t("add_max_auction",config.auction_maxAuctions_overall));
                return true;
            }
        }

        if (!(sender instanceof ConsoleCommandSender))
        {

            ((Player) sender).getInventory().removeItem(removeItem);
        }
        else
        {
            AuctionHouse.log("ServerAuction(s) added succesfully!");
        }

        sender.sendMessage(t("i")+" "+t("add_start",multiAuction,newItem.toString(),startBid,
                                DateFormatUtils.format(auctionEnd, config.auction_timeFormat)));                     
        return true;
    }

    private boolean RegisterAuction(Auction auction, CommandSender sender)
    {
        if (AuctionManager.getInstance().isEmpty())
        {
            return false;
        }
        AuctionManager.getInstance().addAuction(auction);

        if (sender instanceof ConsoleCommandSender)
        {
            ServerBidder.getInstance().addAuction(auction);
        }
        else
        {
            Bidder.getInstance((Player) sender).addAuction(auction);
        }

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
        return t("command_add");
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