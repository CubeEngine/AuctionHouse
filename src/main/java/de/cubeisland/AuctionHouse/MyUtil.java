package de.cubeisland.AuctionHouse;


import static de.cubeisland.AuctionHouse.Translation.Translator.t;
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
public class MyUtil {
    
    private static MyUtil instance = null;
    private static final AuctionHouse plugin = AuctionHouse.getInstance();
    private static final AuctionHouseConfiguration config = plugin.getConfigurations();
    
    public static MyUtil get()
    {
        if (instance == null)
        {
            instance = new MyUtil();
        }
        return instance; 
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
    
    
    public boolean RegisterAuction(Auction auction, CommandSender sender)
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
            if (bidder.getMatSub().contains(new ItemStack(auction.item.getType(), 1, auction.item.getDurability())))
            {
                bidder.addSubscription(auction);
            }
        }
        return true;
    }
    
    public void sendInfo(CommandSender sender, Auction auction)
    {
        Economy econ = plugin.getEconomy();
        String output = "";
        output += "&e#" + auction.id + ":&f ";
        output += auction.item.getType().toString()+" x"+auction.item.getAmount()+" ";
        if (auction.item.getEnchantments().size() > 0)
        {
            output += " "+t("info_out_ench")+" ";
            for (Enchantment enchantment : auction.item.getEnchantments().keySet())
            {
                output += enchantment.toString() + ":";
                output += auction.item.getEnchantments().get(enchantment).toString() + " ";
            }
        }
        if (auction.bids.peek().getBidder().equals(auction.owner))
        {
            output += "&e"+t("info_out_bid",econ.format(auction.bids.peek().getAmount()))+"&f";
        }
        else
        {
            if (auction.bids.peek().getBidder() instanceof ServerBidder)
            {
                output += "&c"+t("info_out_leadserv")+"&f";
            }
            else
            {
                if (auction.bids.peek().getBidder().getName().equals(sender.getName()))
                    output += "&a";
                else
                    output += "&c";
                output += t("info_out_lead",auction.bids.peek().getBidder().getName())+"&f";
            }
            output +=" "+t("info_out_with",econ.format(auction.bids.peek().getAmount()));
        }
        if (auction.auctionEnd-System.currentTimeMillis()>1000*60*60*24)
            output += " "+t("info_out_end",
                            DateFormatUtils.format(auction.auctionEnd, config.auction_timeFormat));
        else
            output += " "+t("info_out_end2",this.convertTime(auction.auctionEnd - System.currentTimeMillis()));
        sender.sendMessage(output);
    }
    
    public void sendInfo(CommandSender sender, List<Auction> auctionlist)
    {
        int max = auctionlist.size();
        if (max == 0)
        {
            sender.sendMessage(t("no_detect"));
        }
        for (int i = 0; i < max; ++i)
        {
            this.sendInfo(sender, auctionlist.get(i));
        }
    }
    
    public String convertTime(long time)
    {
        if (TimeUnit.MILLISECONDS.toMinutes(time)==0)
            return t("less_time");
        return String.format("%dh %dm", 
            TimeUnit.MILLISECONDS.toHours(time),
            TimeUnit.MILLISECONDS.toMinutes(time) - 
            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time))
            );
    }
    
    public String convertItem(ItemStack item)
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
    
}
