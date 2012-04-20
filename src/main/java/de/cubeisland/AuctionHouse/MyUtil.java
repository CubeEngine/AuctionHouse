package de.cubeisland.AuctionHouse;


import static de.cubeisland.AuctionHouse.AuctionHouse.t;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.time.DateFormatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Faithcaio
 */
public class MyUtil
{
    private static final AuctionHouse plugin = AuctionHouse.getInstance();
    private static final AuctionHouseConfiguration config = plugin.getConfigurations();
    
    public static Integer convert(String str) //ty quick_wango
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
    
    
    public static boolean RegisterAuction(Auction auction, CommandSender sender)
    {
        if (Manager.getInstance().isEmpty())
        {
            return false;
        }
        Manager.getInstance().addAuction(auction);

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
            if (bidder.getMatSub().contains(new ItemStack(auction.getItem().getType(), 1, auction.getItem().getDurability())))
            {
                bidder.addSubscription(auction);
            }
        }
        return true;
    }
    
    public static void sendInfo(CommandSender sender, Auction auction)
    {
        Economy econ = plugin.getEconomy();
        String output = "";
        output += t("info_out_1",auction.getId(),auction.getItem().getType().toString(),auction.getItem().getAmount());
        if (auction.getItem().getEnchantments().size() > 0)
        {
            output += " "+t("info_out_ench");
            for (Enchantment enchantment : auction.getItem().getEnchantments().keySet())
            {
                output += " "+enchantment.getName() + ":";
                output += auction.getItem().getEnchantmentLevel(enchantment);
            }
        }
        if (auction.getBids().peek().getBidder().equals(auction.getOwner()))
        {
            output += " "+t("info_out_bid",econ.format(auction.getBids().peek().getAmount()));
        }
        else
        {
            if (auction.getBids().peek().getBidder() instanceof ServerBidder)
            {
                output += " "+t("info_out_leadserv");
            }
            else
            {
                if (auction.getBids().peek().getBidder().getName().equals(sender.getName()))
                    output += " "+t("info_out_lead",auction.getBids().peek().getBidder().getName());
                else
                    output += " "+t("info_out_lead2",auction.getBids().peek().getBidder().getName());
            }
            output +=" "+t("info_out_with",econ.format(auction.getBids().peek().getAmount()));
        }
        if (auction.getAuctionEnd()-System.currentTimeMillis()>1000*60*60*24)
            output += " "+t("info_out_end",DateFormatUtils.format(auction.getAuctionEnd(), 
                    AuctionHouse.getInstance().getConfigurations().auction_timeFormat));
        else
            output += " " + t("info_out_end2", convertTime(auction.getAuctionEnd() - System.currentTimeMillis()));
        sender.sendMessage(output);
    }
    
    public static void sendInfo(CommandSender sender, List<Auction> auctionlist)
    {
        int max = auctionlist.size();
        if (max == 0)
        {
            sender.sendMessage(t("no_detect"));
        }
        for (int i = 0; i < max; ++i)
        {
            sendInfo(sender, auctionlist.get(i));
        }
    }
    
    public static String convertTime(long time)
    {
        if (TimeUnit.MILLISECONDS.toMinutes(time)==0)
            return t("less_time");
        return String.format("%dh %dm", 
            TimeUnit.MILLISECONDS.toHours(time),
            TimeUnit.MILLISECONDS.toMinutes(time) - 
            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time))
            );
    }
    
    public static String convertItem(ItemStack item)
    {
        String out = item.getTypeId()+":"+item.getDurability();
        if (!item.getEnchantments().isEmpty())
        {
            for (Enchantment ench : item.getEnchantments().keySet())
            {
                out += " "+ench.getId()+":"+item.getEnchantmentLevel(ench);
            }
        }
        return out;
    }
    
    public static ItemStack convertItem (String in, int amount)
    {
        ItemStack out = convertItem(in);
        out.setAmount(amount);
        return out;
    }
    
    public static ItemStack convertItem(String in)
    {
        //id:data
        int id = Integer.valueOf(in.substring(0,in.indexOf(":")));
        short data;
        if (in.indexOf(" ")==-1)
        {
            data = Short.valueOf(in.substring(in.indexOf(":")+1));
            in = "";
        }
            
        else
        {
            data = Short.valueOf(in.substring(in.indexOf(":")+1,in.indexOf(" ")));
            in.replace(in.substring(0, in.indexOf(" ")+1), "");
        }
        
        
        ItemStack out = new ItemStack(id,data);
        //ench1:val1 ench2:val2 ...
        while (in.length()>1)
        {
            int enchid = Integer.valueOf(in.substring(0,in.indexOf(":")));
            int enchval; 
            if (in.indexOf(" ")==-1)
            {
                enchval = Short.valueOf(in.substring(in.indexOf(":")+1));
                in = "";
            }
            else
            {
                enchval = Integer.valueOf(in.substring(in.indexOf(":")+1,in.indexOf(" ")));
                in.replace(in.substring(0, in.indexOf(" ")+1), "");
            }
            if (Enchantment.getById(id)!=null)
                out.addEnchantment(Enchantment.getById(enchid), enchval);
        }  
        return out;
    }
    
    public static void updateNotifyData(Bidder bidder)
    {
        //TODO db ist hier manchmal null warum?
        Database db = AuctionHouse.getInstance().getDB();
        db.exec(
                    "UPDATE `bidder` SET `notify`=? WHERE `id`=?"
                    ,bidder.getNotifyState(),bidder.getId());     
    }
    
}
