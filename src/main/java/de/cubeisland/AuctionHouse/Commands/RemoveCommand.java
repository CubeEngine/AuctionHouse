package de.cubeisland.AuctionHouse.Commands;

import de.cubeisland.AuctionHouse.AbstractCommand;
import de.cubeisland.AuctionHouse.Auction.Auction;
import de.cubeisland.AuctionHouse.Auction.Bidder;
import de.cubeisland.AuctionHouse.Auction.ServerBidder;
import de.cubeisland.AuctionHouse.AuctionHouse;
import static de.cubeisland.AuctionHouse.AuctionHouse.t;
import de.cubeisland.AuctionHouse.AuctionHouseConfiguration;
import de.cubeisland.AuctionHouse.BaseCommand;
import de.cubeisland.AuctionHouse.CommandArgs;
import de.cubeisland.AuctionHouse.Manager;
import de.cubeisland.AuctionHouse.Perm;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Removes an auction
 * 
 * @author Faithcaio
 */
public class RemoveCommand extends AbstractCommand
{
    private static final AuctionHouse plugin = AuctionHouse.getInstance();
    private static final AuctionHouseConfiguration config = plugin.getConfiguration();
    
    public RemoveCommand(BaseCommand base)
    {
        super(base, "remove", "cancel", "delete", "rem");
    }

    public boolean execute(CommandSender sender, CommandArgs args)
    {
        if (args.isEmpty())
        {
            sender.sendMessage(t("rem_title1"));
            sender.sendMessage(t("rem_title2"));
            sender.sendMessage(t("rem_title3"));
            sender.sendMessage(t("rem_title4"));
            sender.sendMessage(t("rem_title5"));
            sender.sendMessage(t("rem_title6"));
            sender.sendMessage("");
            return true;
        }
        Manager manager = Manager.getInstance();
        BukkitScheduler timer = plugin.getServer().getScheduler();
        final CommandSender sender2= sender;
        
        if (args.getString(0) != null)
        {
            if (args.getString(0).equalsIgnoreCase("all"))
            {
                if (!Perm.command_delete_all.check(sender)) return true;
                manager.getAllConfirm().add(Bidder.getInstance(sender));
                sender.sendMessage(t("rem_all"));
                sender.sendMessage(t("rem_confirm"));
                timer.scheduleSyncDelayedTask(AuctionHouse.getInstance(), new Runnable() 
                    {
                        public void run() 
                        {
                            Manager manager = Manager.getInstance();
                            Bidder bidder = Bidder.getInstance(sender2);
                            if (manager.getAllConfirm().contains(bidder))
                                sender2.sendMessage(t("rem_abort"));
                            manager.getAllConfirm().remove(bidder);  
                        }
                    }, 200L);

                return true;
            }
            else
            {
                if (args.getString(0).equalsIgnoreCase("Server"))
                {
                    if (!Perm.command_delete_server.check(sender)) return true;
                    manager.getBidderConfirm().put(Bidder.getInstance(sender), ServerBidder.getInstance());
                    sender.sendMessage(t("rem_allserv"));
                    sender.sendMessage(t("rem_confirm"));                    
                    timer.scheduleSyncDelayedTask(AuctionHouse.getInstance(), new Runnable() 
                        {
                            public void run() 
                            {
                                Manager manager = Manager.getInstance();
                                if (manager.getBidderConfirm().containsKey(Bidder.getInstance(sender2)))
                                    sender2.sendMessage(t("rem_abort"));
                                manager.getBidderConfirm().remove(Bidder.getInstance(sender2));  
                            }
                        }, 200L);

                    return true;
                }
            }
            {
                Integer id = args.getInt(0);
                if (id != null)
                {
                    if (!Perm.command_delete_id.check(sender)) return true;
                    if (manager.getAuction(id) == null)
                    {
                        sender.sendMessage(t("e")+" "+t("auction_no_exist",id));
                        return true;
                    }
                    
                    Auction auction = manager.getAuction(id);
                    if (auction.getOwner() instanceof ServerBidder)
                        if (!Perm.command_delete_server.check(sender)) return true;                    
                    if (config.auction_removeTime < System.currentTimeMillis() - auction.getBids().firstElement().getTimestamp())
                    {
                        if (!sender.hasPermission("aucionhouse.delete.player.all"))
                        {
                            sender.sendMessage(t("i")+" "+t("rem_time"));
                            return true;
                        }
                    }
                    if (!auction.getOwner().equals(Bidder.getInstance(sender)))
                        if (!Perm.command_delete_player_other.check(sender)) return true;
                    if (config.auction_confirmID)
                    {
                        manager.getSingleConfirm().put(Bidder.getInstance(sender), id);
                        sender.sendMessage(t("rem_single"));
                        sender.sendMessage(t("rem_confirm"));
                        timer.scheduleSyncDelayedTask(AuctionHouse.getInstance(), new Runnable() 
                            {
                                public void run() 
                                {
                                    Manager manager = Manager.getInstance();
                                    if (manager.getSingleConfirm().containsKey(Bidder.getInstance(sender2)))
                                        sender2.sendMessage(t("rem_abort"));
                                    manager.getSingleConfirm().remove(Bidder.getInstance(sender2));  
                                }
                            }, 200L);

                        return true;
                    }    
                    ItemStack item = auction.getItem();
                    manager.cancelAuction(auction, false);
                    sender.sendMessage(t("i")+" "+t("rem_id",id,item.getType().toString()+"x"+item.getAmount()));
                    return true;
                }
                sender.sendMessage(t("e")+" "+t("invalid_com"));
                return true;
            }
        }
        else
        {
            Bidder player = args.getBidder("p");
            if (player == null)
            {
                sender.sendMessage(t("i")+" "+t("info_p_no_auction",args.getString("p")));
                return true;
            }
            else
            {
                if (player.getPlayer().equals((Player) sender))
                    if (!Perm.command_delete_player.check(sender)) return true;
                else
                    if (!Perm.command_delete_player_other.check(sender)) return true;
                if (!(player.getAuctions().isEmpty()))
                {
                    manager.getBidderConfirm().put(Bidder.getInstance(sender), player);
                    sender.sendMessage(t("rem_play",player.getName()));
                    sender.sendMessage(t("rem_confirm"));
                    timer.scheduleSyncDelayedTask(AuctionHouse.getInstance(), new Runnable() 
                        {
                            public void run() 
                            {
                                Manager manager = Manager.getInstance();
                                if (manager.getBidderConfirm().containsKey(Bidder.getInstance(sender2)))
                                    sender2.sendMessage(t("rem_abort"));
                                manager.getBidderConfirm().remove(Bidder.getInstance(sender2));  
                            }
                        }, 200L);
                    return true;
                }
                sender.sendMessage(t("i")+" "+t("rem_no_auc",args.getString("p")));
                return true;
            }
        }
    }

    @Override
    public String getUsage()
    {
        return super.getUsage() + " <AuctionId>";
    }

    public String getDescription()
    {
        return t("command_rem");
    }
}