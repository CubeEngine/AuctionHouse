package de.paralleluniverse.Faithcaio.AuctionHouse;

import static de.paralleluniverse.Faithcaio.AuctionHouse.Translation.Translator.t;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.time.DateFormatUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Faithcaio
 */
public class Events implements Listener
{
    private static Events instance;
    private static final AuctionHouse plugin = AuctionHouse.getInstance();
    private static final AuctionHouseConfiguration config = plugin.getConfigurations();
    Economy econ = AuctionHouse.getInstance().getEconomy();

    public Events()
    {
        instance = this;
    }

    @EventHandler
    public void goesOnline(final PlayerJoinEvent event)
    {
        plugin.server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            public void run()
            {
                Bidder bidder = Bidder.getInstance(event.getPlayer());
                if (bidder.notify)
                {
                    event.getPlayer().sendMessage(t("i")+" "+t("event_new"));
                    Bidder.getInstance(event.getPlayer()).notify = false;
                }
                if (bidder.notifyCancel)
                {
                    event.getPlayer().sendMessage(t("i")+" "+t("event_fail"));
                    Bidder.getInstance(event.getPlayer()).notifyCancel = false;
                }
                if (bidder.notifyContainer)
                {
                    event.getPlayer().sendMessage(t("i")+" "+t("event_old",config.auction_itemContainerLength));
                    Bidder.getInstance(event.getPlayer()).notifyCancel = false;
                }
            }
        ;
    }

    );
    }
    
    @EventHandler
    public void goesOffline(PlayerQuitEvent event)
    {
        ItemContainer items = Bidder.getInstance(event.getPlayer()).getContainer();
        if (!(items.itemList.isEmpty()))
        {
            for (AuctionItem item : items.itemList)
            {
                if (System.currentTimeMillis() - item.date > config.auction_itemContainerLength * 24 * 60 * 60 * 1000)
                {
                    items.itemList.remove(item);
                }
            }
        }
        if (!(items.itemList.isEmpty()))
        {
            Bidder.getInstance(event.getPlayer()).notifyContainer = true;
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        final Player player = event.getPlayer();
        final Block block = event.getClickedBlock();
        if (block == null)
        {
            return;
        }
        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK))
        {
            return;
        }
        if (block.getType().equals(Material.WALL_SIGN))
        {
            if (((Sign) block.getState()).getLine(0).equals("[AuctionHouse]"))
            {
                if (((Sign) block.getState()).getLine(1).equals("AuctionBox"))
                {
                    //AuktionBox GetItems
                    if (!(player.hasPermission("auctionhouse.getItems.sign")))
                    {
                        player.sendMessage(t("event_sign_perm"));
                        return;
                    }
                    if (!(Bidder.getInstance(player).getContainer().giveNextItem()))
                    {
                        player.sendMessage(t("i")+" "+t("time_sign_empty"));
                    }
                }
                if (((Sign) block.getState()).getLine(1).equals("Start"))
                {
                    //AuktionBox Start Auktion
                    //TODO Schwert verschwindet nicht im Inventar
                    //TODO Schild wird gesetzt UND eingestellt
                    Double startbid;
                    Integer length = this.convert(((Sign) block.getState()).getLine(2));
                    if (length == null)
                       return;
                    try
                    {
                         startbid = Double.parseDouble(((Sign) block.getState()).getLine(3));
                    }
                    catch (NumberFormatException ex)
                    {
                        startbid = 0.0;
                    }
                    if (startbid == null) startbid = 0.0;
                    
                    Auction newAuction = new Auction(player.getItemInHand(), 
                                                    Bidder.getInstance(player),
                                                    System.currentTimeMillis()+length,
                                                    startbid);
                    if (!(this.RegisterAuction(newAuction, player)))
                    {
                        player.sendMessage(t("i")+" "+t("add_max_auction",config.auction_maxAuctions_overall));
                        return;
                    }
                    else
                    {
                        player.getInventory().removeItem(player.getItemInHand());
                        player.sendMessage(t("i")+" "+t("add_start",1,newAuction.item.toString(),econ.format(startbid),
                                DateFormatUtils.format(newAuction.auctionEnd, config.auction_timeFormat))); 
                    }    
                }
            }
        }
    }
    public Integer convert(String str) //ty quick_wango
    {//TODO auslagern ist auch in config / add command
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
    
    private boolean RegisterAuction(Auction auction, CommandSender sender)
    {//TODO aulagern ist auch in add
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
    
}
