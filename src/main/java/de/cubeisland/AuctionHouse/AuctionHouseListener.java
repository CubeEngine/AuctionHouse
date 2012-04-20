package de.cubeisland.AuctionHouse;

import de.cubeisland.AuctionHouse.Auction.Auction;
import de.cubeisland.AuctionHouse.Auction.AuctionItem;
import de.cubeisland.AuctionHouse.Auction.Bidder;
import de.cubeisland.AuctionHouse.Auction.ItemContainer;
import static de.cubeisland.AuctionHouse.AuctionHouse.t;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.time.DateFormatUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Faithcaio
 */
public class AuctionHouseListener implements Listener
{
    private final AuctionHouse plugin;
    private final AuctionHouseConfiguration config;
    private final Economy econ;
    
    public AuctionHouseListener(AuctionHouse plugin)
    {
        this.plugin = plugin;
        this.config = plugin.getConfiguration();
        this.econ = plugin.getEconomy();
    }

    @EventHandler
    public void goesOnline(final PlayerJoinEvent event)
    {
        Bidder bidder = Bidder.getInstance(event.getPlayer());
        Util.updateNotifyData(bidder);
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            public void run()
            {
                Bidder bidder = Bidder.getInstance(event.getPlayer());
                if (bidder.hasNotifyState(Bidder.NOTIFY_STATUS))
                {
                    event.getPlayer().sendMessage(t("i")+" "+t("event_new"));
                    bidder.unsetNotifyState(Bidder.NOTIFY_STATUS);
                }
                if (bidder.hasNotifyState(Bidder.NOTIFY_CANCEL))
                {
                    event.getPlayer().sendMessage(t("i")+" "+t("event_fail"));
                    bidder.unsetNotifyState(Bidder.NOTIFY_CANCEL);
                }
                if (bidder.hasNotifyState(Bidder.NOTIFY_ITEMS))
                {
                    event.getPlayer().sendMessage(t("i")+" "+t("event_old",config.auction_itemContainerLength));
                    bidder.unsetNotifyState(Bidder.NOTIFY_ITEMS);
                }
            };
        });
    }
    
    @EventHandler
    public void goesOffline(PlayerQuitEvent event)
    {
        Bidder bidder = Bidder.getInstance(event.getPlayer());
        ItemContainer items = bidder.getContainer();
        
        if (!(items.getItemList().isEmpty()))
        {
            for (AuctionItem item : items.getItemList())
            {
                if (System.currentTimeMillis() - item.getDate() > config.auction_itemContainerLength * 24 * 60 * 60 * 1000)
                {
                    items.getItemList().remove(item);
                }
            }
        }
        if (!(items.getItemList().isEmpty()))
        {
            Bidder.getInstance(event.getPlayer()).setNotifyState(Bidder.NOTIFY_ITEMS);
        }
        Util.updateNotifyData(bidder);
    }
   
    @EventHandler
    public void onSignChange(SignChangeEvent event)
    {
        if(event.getLine(0).equalsIgnoreCase("[AuctionHouse]"))
        {
            if (event.getLine(1).equalsIgnoreCase("AuctionBox"))
            {
                if (!Perm.get().check(event.getPlayer(), "auctionhouse.sign.create.boxsign"))
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
                    if (!Perm.get().check(event.getPlayer(), "auctionhouse.sign.create.addsign"))
                    {
                        event.setCancelled(true);
                        return;
                    }
                    if (Util.convertTimeToMillis(event.getLine(2)) < 0)
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
                        if (!Perm.get().check(player,"auctionhouse.sign.auctionbox")) return;
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
                        if (!Perm.get().check(player, "auctionhouse.sign.start")) return;

                        Double startbid;
                        Integer length = Util.convertTimeToMillis(((Sign) block.getState()).getLine(2));
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
                        if (!(Util.registerAuction(newAuction, player)))
                        {
                            player.sendMessage(t("i")+" "+t("add_max_auction",config.auction_maxAuctions_overall));
                        }
                        else
                        {
                            player.getInventory().removeItem(player.getItemInHand());
                            player.updateInventory();
                            player.sendMessage(t("i")+" "+t("add_start",1,
                                    newAuction.getItem().getType().toString()+"x"+newAuction.getItem().getAmount(),
                                    econ.format(startbid),
                                    DateFormatUtils.format(newAuction.getAuctionEnd(), config.auction_timeFormat))); 
                        }    
                    }
                }
            }
        }
    }
}
