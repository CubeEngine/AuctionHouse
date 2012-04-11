package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import de.paralleluniverse.Faithcaio.AuctionHouse.*;
import static de.paralleluniverse.Faithcaio.AuctionHouse.Translation.Translator.t;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Faithcaio
 */
public class RemoveCommand extends AbstractCommand
{
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
                AuctionManager.getInstance().remAllConfirm.add(sender);
                sender.sendMessage(t("rem_all"));
                sender.sendMessage(t("rem_confirm"));
                final CommandSender sender2= sender;
                AuctionHouse.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(AuctionHouse.getInstance(), new Runnable() 
                    {
                        public void run() 
                        {
                            if (AuctionManager.getInstance().remAllConfirm.contains(sender2))
                                sender2.sendMessage(t("rem_abort"));
                            AuctionManager.getInstance().remAllConfirm.remove(sender2);  
                        }
                    }, 200L);

                return true;
            }
            else
            {
                if (arguments.getString("1").equalsIgnoreCase("Server"))
                {
                    if (!Perm.get().check(sender,"auctionhouse.delete.server")) return true;
                    AuctionManager.getInstance().remBidderConfirm.put(sender, ServerBidder.getInstance());
                    sender.sendMessage(t("rem_allserv"));
                    sender.sendMessage(t("rem_confirm"));
                    final CommandSender sender2= sender;
                    AuctionHouse.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(AuctionHouse.getInstance(), new Runnable() 
                        {
                            public void run() 
                            {
                                AuctionHouse.debug("running");
                                if (AuctionManager.getInstance().remBidderConfirm.containsKey(sender2))
                                    sender2.sendMessage(t("rem_abort"));
                                AuctionManager.getInstance().remBidderConfirm.remove(sender2);  
                            }
                        }, 200L);

                    return true;
                }
            }
            {
                Integer id = arguments.getInt("1");
                if (id != null)
                {
                    if (AuctionManager.getInstance().getAuction(id) == null)
                    {
                        sender.sendMessage(t("e")+" "+t("auction_no_exist",id));
                        return true;
                    }
                    if (!Perm.get().check(sender,"auctionhouse.delete.id"))
                    {
                        sender.sendMessage(t("perm")+" "+t("rem_id_perm"));
                        return true;
                    }
                    if (AuctionManager.getInstance().getAuction(id).owner instanceof ServerBidder)
                    {
                        if (!Perm.get().check(sender,"auctionhouse.delete.server")) return true;
                    }
                    ItemStack item = AuctionManager.getInstance().getAuction(id).item;
                    AuctionManager.getInstance().cancelAuction(AuctionManager.getInstance().getAuction(id));
                    sender.sendMessage(t("i")+" "+t("rem_id",id,item.toString()));
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
                    AuctionManager.getInstance().remBidderConfirm.put(sender, player);
                    sender.sendMessage(t("rem_play",player.getName()));
                    sender.sendMessage(t("rem_confirm"));
                    final CommandSender sender2= sender;
                    AuctionHouse.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(AuctionHouse.getInstance(), new Runnable() 
                        {
                            public void run() 
                            {
                                AuctionHouse.debug("running");
                                if (AuctionManager.getInstance().remBidderConfirm.containsKey(sender2))
                                    sender2.sendMessage(t("rem_abort"));
                                AuctionManager.getInstance().remBidderConfirm.remove(sender2);  
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