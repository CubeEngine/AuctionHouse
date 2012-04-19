package de.cubeisland.AuctionHouse.Commands;

import de.cubeisland.AuctionHouse.AbstractCommand;
import de.cubeisland.AuctionHouse.Arguments;
import de.cubeisland.AuctionHouse.Auction;
import de.cubeisland.AuctionHouse.AuctionHouse;
import de.cubeisland.AuctionHouse.AuctionHouseConfiguration;
import de.cubeisland.AuctionHouse.BaseCommand;
import de.cubeisland.AuctionHouse.Bidder;
import de.cubeisland.AuctionHouse.Manager;
import de.cubeisland.AuctionHouse.Perm;
import de.cubeisland.AuctionHouse.ServerBidder;
import static de.cubeisland.AuctionHouse.AuctionHouse.t;
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
    private static final AuctionHouseConfiguration config = plugin.getConfigurations();
    
    public RemoveCommand(BaseCommand base)
    {
        super(base, "remove", "cancel", "delete", "rem");
    }

    public boolean execute(CommandSender sender, String[] args)
    {
        if (args.length < 1)
        {
            sender.sendMessage("/ah remove <AuctionID>");
            sender.sendMessage("/ah remove p:<Player>");
            sender.sendMessage("/ah remove Server");
            sender.sendMessage("/ah remove all");
            sender.sendMessage("Aliases: remove|cancel|delete|rem");
            return true;
        }
        Arguments arguments = new Arguments(args);

        if (arguments.getString("1") != null)
        {
            if (arguments.getString("1").equalsIgnoreCase("all"))
            {
                if (!Perm.get().check(sender,"auctionhouse.delete.all")) return true;
                Manager.getInstance().remAllConfirm.add(Bidder.getInstance(sender));
                sender.sendMessage(t("rem_all"));
                sender.sendMessage(t("rem_confirm"));
                final CommandSender sender2= sender;
                AuctionHouse.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(AuctionHouse.getInstance(), new Runnable() 
                    {
                        public void run() 
                        {
                            if (Manager.getInstance().remAllConfirm.contains(Bidder.getInstance(sender2)))
                                sender2.sendMessage(t("rem_abort"));
                            Manager.getInstance().remAllConfirm.remove(Bidder.getInstance(sender2));  
                        }
                    }, 200L);

                return true;
            }
            else
            {
                if (arguments.getString("1").equalsIgnoreCase("Server"))
                {
                    if (!Perm.get().check(sender,"auctionhouse.delete.server")) return true;
                    Manager.getInstance().remBidderConfirm.put(Bidder.getInstance(sender), ServerBidder.getInstance());
                    sender.sendMessage(t("rem_allserv"));
                    sender.sendMessage(t("rem_confirm"));
                    final CommandSender sender2= sender;
                    
                    AuctionHouse.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(AuctionHouse.getInstance(), new Runnable() 
                        {
                            public void run() 
                            {
                                if (Manager.getInstance().remBidderConfirm.containsKey(Bidder.getInstance(sender2)))
                                    sender2.sendMessage(t("rem_abort"));
                                Manager.getInstance().remBidderConfirm.remove(Bidder.getInstance(sender2));  
                            }
                        }, 200L);

                    return true;
                }
            }
            {
                Integer id = arguments.getInt("1");
                if (id != null)
                {
                    if (Manager.getInstance().getAuction(id) == null)
                    {
                        sender.sendMessage(t("e")+" "+t("auction_no_exist",id));
                        return true;
                    }
                    if (!Perm.get().check(sender,"auctionhouse.delete.id")) return true;
                    Auction auction = Manager.getInstance().getAuction(id);
                    if (auction.getOwner() instanceof ServerBidder)
                    {
                        if (!Perm.get().check(sender,"auctionhouse.delete.server")) return true;
                    }
                    if (!auction.getOwner().equals(Bidder.getInstance(sender)))
                        if (!Perm.get().check(sender, "auctionhouse.delete.player.other"))

                    if (config.auction_confirmID)
                    {
                        Manager.getInstance().remSingleConfirm.put(Bidder.getInstance(sender), id);
                        sender.sendMessage(t("rem_single"));
                        sender.sendMessage(t("rem_confirm"));
                        final CommandSender sender2= sender;

                        AuctionHouse.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(AuctionHouse.getInstance(), new Runnable() 
                            {
                                public void run() 
                                {
                                    if (Manager.getInstance().remSingleConfirm.containsKey(Bidder.getInstance(sender2)))
                                        sender2.sendMessage(t("rem_abort"));
                                    Manager.getInstance().remSingleConfirm.remove(Bidder.getInstance(sender2));  
                                }
                            }, 200L);

                        return true;
                    }    
                    ItemStack item = Manager.getInstance().getAuction(id).getItem();
                    Manager.getInstance().cancelAuction(Manager.getInstance().getAuction(id));
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
                    if (!Perm.get().check(sender,"auctionhouse.delete.player")) return true;
                }
                else
                {
                    if (!Perm.get().check(sender,"auctionhouse.delete.player.other")) return true;
                }

                if (!(player.getAuctions().isEmpty()))
                {
                    Manager.getInstance().remBidderConfirm.put(Bidder.getInstance(sender), player);
                    sender.sendMessage(t("rem_play",player.getName()));
                    sender.sendMessage(t("rem_confirm"));
                    final CommandSender sender2= sender;
                    AuctionHouse.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(AuctionHouse.getInstance(), new Runnable() 
                        {
                            public void run() 
                            {
                                if (Manager.getInstance().remBidderConfirm.containsKey(Bidder.getInstance(sender2)))
                                    sender2.sendMessage(t("rem_abort"));
                                Manager.getInstance().remBidderConfirm.remove(Bidder.getInstance(sender2));  
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
        return super.getUsage() + " <<AuctionId>|p:<Player>|all>";
    }

    public String getDescription()
    {
        return t("command_rem");
    }
}