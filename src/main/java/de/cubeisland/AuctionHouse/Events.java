package de.cubeisland.AuctionHouse;

import static de.cubeisland.AuctionHouse.Translation.Translator.t;
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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
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
            };
        });
        Bidder bidder = Bidder.getInstance(event.getPlayer());
        Database data = AuctionHouse.getInstance().database;
        //Update BidderNotification
        data.query("UPDATE `bidder` SET `notify`=? WHERE `id`=?"
                      ,bidder.notifyBitMask(),bidder.id); 
    }
    
    @EventHandler
    public void goesOffline(PlayerQuitEvent event)
    {
        Bidder bidder = Bidder.getInstance(event.getPlayer());
        ItemContainer items = bidder.getContainer();
        
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
        Database data = AuctionHouse.getInstance().database;
        //Update BidderNotification
        data.query("UPDATE `bidder` SET `notify`=? WHERE `id`=?"
                      ,bidder.notifyBitMask(),bidder.id);  
    }
   

    @EventHandler
    public void onSignChange(SignChangeEvent event)
    {
        if(event.getLine(0).equalsIgnoreCase("[AuctionHouse]"))
        {
            if (event.getLine(1).equalsIgnoreCase("AuctionBox"))
            {
                if (!Perm.get().check(event.getPlayer(), "auctionhouse.create.boxsign"))
                {
                    event.setCancelled(true);
                    return;
                }
                event.setLine(1, "AuctionBox");
            }
            else
            {
                if (event.getLine(1).equalsIgnoreCase("Start"))
                {
                    if (!Perm.get().check(event.getPlayer(), "auctionhouse.create.addsign"))
                    {
                        event.setCancelled(true);
                        return;
                    }
                    if (MyUtil.get().convert(event.getLine(2))==null)
                    {
                        event.getPlayer().sendMessage(t("event_sign_fail"));
                        event.setCancelled(true);
                        return;
                    }
                    event.setLine(1, "Start");
                }
                else
                {
                    event.getPlayer().sendMessage(t("event_sign_fail"));
                    event.setCancelled(true);
                    return;
                }
            }
            event.getPlayer().sendMessage(t("event_sign_create"));            
            event.setLine(0, "[AuctionHouse]");
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
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            if (block.getType().equals(Material.WALL_SIGN))
            {
                if (((Sign)block.getState()).getLine(0).equals("[AuctionHouse]"))
                {
                    event.setCancelled(true);
                    if (((Sign)block.getState()).getLine(1).equals("AuctionBox"))
                    {
                        //AuktionBox GetItems
                        if (!Perm.get().check(player,"auctionhouse.getItems.sign")) return;
                        if (!(Bidder.getInstance(player).getContainer().giveNextItem()))
                        {
                            player.sendMessage(t("i")+" "+t("time_sign_empty"));
                        }
                    }
                    if (((Sign) block.getState()).getLine(1).equals("Start"))
                    {
                        if (player.getItemInHand().getType().equals(Material.AIR))
                        {
                            player.sendMessage(t("pro")+" "+t("add_sell_hand"));
                            return;
                        }
                        //AuktionBox Start Auktion
                        if (!Perm.get().check(player, "auctionhouse.use.addsign")) return;

                        Double startbid;
                        Integer length = MyUtil.get().convert(((Sign) block.getState()).getLine(2));
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

                        for (ItemStack item : config.auction_blacklist)
                        {
                            if (item.getType().equals(player.getItemInHand().getType()))
                            {
                                player.sendMessage(t("e")+" "+t("add_blacklist"));
                                return;
                            }
                        }

                        Auction newAuction = new Auction(player.getItemInHand(), 
                                                        Bidder.getInstance(player),
                                                        System.currentTimeMillis()+length,
                                                        startbid);
                        if (!(MyUtil.get().RegisterAuction(newAuction, player)))
                        {
                            player.sendMessage(t("i")+" "+t("add_max_auction",config.auction_maxAuctions_overall));
                        }
                        else
                        {
                            player.getInventory().removeItem(player.getItemInHand());
                            player.updateInventory();
                            player.sendMessage(t("i")+" "+t("add_start",1,newAuction.item.toString(),econ.format(startbid),
                                    DateFormatUtils.format(newAuction.auctionEnd, config.auction_timeFormat))); 
                        }    
                    }
                }
            }
        }
    }
}
