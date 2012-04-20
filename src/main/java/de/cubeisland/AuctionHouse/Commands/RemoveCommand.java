package de.cubeisland.AuctionHouse.Commands;

import de.cubeisland.AuctionHouse.AbstractCommand;
import de.cubeisland.AuctionHouse.Arguments;
import de.cubeisland.AuctionHouse.Auction.Auction;
import de.cubeisland.AuctionHouse.Auction.Bidder;
import de.cubeisland.AuctionHouse.Auction.ServerBidder;
import de.cubeisland.AuctionHouse.AuctionHouse;
import static de.cubeisland.AuctionHouse.AuctionHouse.t;
import de.cubeisland.AuctionHouse.AuctionHouseConfiguration;
import de.cubeisland.AuctionHouse.BaseCommand;
import de.cubeisland.AuctionHouse.Manager;
import de.cubeisland.AuctionHouse.Perm;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
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

    public boolean execute(CommandSender sender, String[] args)
    {
        if (args.length < 1)
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
        Arguments arguments = new Arguments(args);

        if (arguments.getString("1") != null)
        {
            if (arguments.getString("1").equalsIgnoreCase("all"))
            {
                if (!Perm.get().check(sender,"auctionhouse.command.delete.all")) return true;
                Manager.getInstance().getAllConfirm().add(Bidder.getInstance(sender));
                sender.sendMessage(t("rem_all"));
                sender.sendMessage(t("rem_confirm"));
                final CommandSender sender2= sender;
                AuctionHouse.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(AuctionHouse.getInstance(), new Runnable() 
                    {
                        public void run() 
                        {
                            if (Manager.getInstance().getAllConfirm().contains(Bidder.getInstance(sender2)))
                                sender2.sendMessage(t("rem_abort"));
                            Manager.getInstance().getAllConfirm().remove(Bidder.getInstance(sender2));  
                        }
                    }, 200L);

                return true;
            }
            else
            {
                if (arguments.getString("1").equalsIgnoreCase("Server"))
                {
                    if (!Perm.get().check(sender,"auctionhouse.command.delete.server")) return true;
                    Manager.getInstance().getBidderConfirm().put(Bidder.getInstance(sender), ServerBidder.getInstance());
                    sender.sendMessage(t("rem_allserv"));
                    sender.sendMessage(t("rem_confirm"));
                    final CommandSender sender2= sender;
                    
                    AuctionHouse.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(AuctionHouse.getInstance(), new Runnable() 
                        {
                            public void run() 
                            {
                                if (Manager.getInstance().getBidderConfirm().containsKey(Bidder.getInstance(sender2)))
                                    sender2.sendMessage(t("rem_abort"));
                                Manager.getInstance().getBidderConfirm().remove(Bidder.getInstance(sender2));  
                            }
                        }, 200L);

                    return true;
                }
            }
            {
                Integer id = arguments.getInt("1");
                if (id != null)
                {
                    if (!Perm.get().check(sender,"auctionhouse.command.delete.id")) return true;
                    if (Manager.getInstance().getAuction(id) == null)
                    {
                        sender.sendMessage(t("e")+" "+t("auction_no_exist",id));
                        return true;
                    }
                    
                    Auction auction = Manager.getInstance().getAuction(id);
                    if (auction.getOwner() instanceof ServerBidder)
                    {
                        if (!Perm.get().check(sender,"auctionhouse.command.delete.server")) return true;
                    }
                    if (!auction.getOwner().equals(Bidder.getInstance(sender)))
                        if (!Perm.get().check(sender, "auctionhouse.command.delete.player.other"))

                    if (config.auction_removeTime < System.currentTimeMillis() - auction.getBids().firstElement().getTimestamp())
                    {
                        sender.sendMessage(t("i")+" "+t("rem_time"));
                        return true;
                    }
                            
                    if (config.auction_confirmID)
                    {
                        Manager.getInstance().getSingleConfirm().put(Bidder.getInstance(sender), id);
                        sender.sendMessage(t("rem_single"));
                        sender.sendMessage(t("rem_confirm"));
                        final CommandSender sender2= sender;

                        AuctionHouse.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(AuctionHouse.getInstance(), new Runnable() 
                            {
                                public void run() 
                                {
                                    if (Manager.getInstance().getSingleConfirm().containsKey(Bidder.getInstance(sender2)))
                                        sender2.sendMessage(t("rem_abort"));
                                    Manager.getInstance().getSingleConfirm().remove(Bidder.getInstance(sender2));  
                                }
                            }, 200L);

                        return true;
                    }    
                    ItemStack item = auction.getItem();
                    Manager.getInstance().cancelAuction(auction, false);
                    sender.sendMessage(t("i")+" "+t("rem_id",id,item.getType().toString()+"x"+item.getAmount()));
                    return true;
                }
                sender.sendMessage(t("e")+" "+t("invalid_com"));
                return true;
            }
        }
        else
        {
            Bidder player = arguments.getBidder("p");
            if (player == null)
            {
                sender.sendMessage(t("i")+" "+t("info_p_no_auction",arguments.getString("p")));
                return true;
            }
            else
            {
                if (player.getPlayer().equals((Player) sender))
                {
                    if (!Perm.get().check(sender,"auctionhouse.command.delete.player")) return true;
                }
                else
                {
                    if (!Perm.get().check(sender,"auctionhouse.command.delete.player.other")) return true;
                }

                if (!(player.getAuctions().isEmpty()))
                {
                    Manager.getInstance().getBidderConfirm().put(Bidder.getInstance(sender), player);
                    sender.sendMessage(t("rem_play",player.getName()));
                    sender.sendMessage(t("rem_confirm"));
                    final CommandSender sender2= sender;
                    AuctionHouse.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(AuctionHouse.getInstance(), new Runnable() 
                        {
                            public void run() 
                            {
                                if (Manager.getInstance().getBidderConfirm().containsKey(Bidder.getInstance(sender2)))
                                    sender2.sendMessage(t("rem_abort"));
                                Manager.getInstance().getBidderConfirm().remove(Bidder.getInstance(sender2));  
                            }
                        }, 200L);
                    return true;
                }
                sender.sendMessage(t("i")+" "+t("rem_no_auc",arguments.getString("p")));
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