package de.paralleluniverse.Faithcaio.AuctionHouse;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Faithcaio
 */
public class Events implements Listener{
    
    private static Events instance;
    private static final AuctionHouse plugin = AuctionHouse.getInstance();
    private static final AuctionHouseConfiguration config = plugin.getConfigurations();
    
    public Events ()
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
                    event.getPlayer().sendMessage("Info: You have purchased Items by auction. Don't forget to check your ItemContainer with \"/ah getItems\"");
                    Bidder.getInstance(event.getPlayer()).notify = false;
                }
                if (bidder.notifyCancel)
                {
                    event.getPlayer().sendMessage("Info: Your auction(s) failed. Don't forget to check your ItemContainer with \"/ah getItems\"");
                    Bidder.getInstance(event.getPlayer()).notifyCancel = false;
                }
                if (bidder.notifyContainer)
                {
                    event.getPlayer().sendMessage("Info: You still have Items left in your ItemContainer!"+
                                                    " Be aware Items get deleted after "+config.auction_itemContainerLength+" days!");
                    Bidder.getInstance(event.getPlayer()).notifyCancel = false;
                }    
            };   
        });
    }
    
    @EventHandler
    public void goesOffline(PlayerQuitEvent event)
    {
        ItemContainer items = Bidder.getInstance(event.getPlayer()).getContainer();
        if (!(items.itemList.isEmpty()))
            for (AuctionItem item : items.itemList)
                if (System.currentTimeMillis()-item.date > config.auction_itemContainerLength*24*60*60*1000)
                    items.itemList.remove(item);
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
        if (block == null) return;
        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
        if (block.getType().equals(Material.WALL_SIGN))
        {
            if (((Sign)block.getState()).getLine(0).equals("[AuctionHouse]"))
            {
                if (((Sign)block.getState()).getLine(1).equals("AuctionBox"))
                {
                    if (!(player.hasPermission("auctionhouse.getItems.sign")))
                    {
                        player.sendMessage("AuctionHouse: You do not have Permission to use this Sign!");
                        return;
                    }
                    AuctionHouse.debug(player.getName()+": Request Items");
                    if (!(Bidder.getInstance(player).getContainer().giveNextItem()))
                        player.sendMessage("Your ItemContainer is empty!");   
                }
            }  
        }
    }
}
    
