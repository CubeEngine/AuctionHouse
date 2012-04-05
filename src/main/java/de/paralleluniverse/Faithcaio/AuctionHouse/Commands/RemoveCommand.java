package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import static de.paralleluniverse.Faithcaio.AuctionHouse.Translation.Translator.t;
import de.paralleluniverse.Faithcaio.AuctionHouse.AbstractCommand;
import de.paralleluniverse.Faithcaio.AuctionHouse.Arguments;
import de.paralleluniverse.Faithcaio.AuctionHouse.AuctionHouse;
import de.paralleluniverse.Faithcaio.AuctionHouse.AuctionManager;
import de.paralleluniverse.Faithcaio.AuctionHouse.BaseCommand;
import de.paralleluniverse.Faithcaio.AuctionHouse.Bidder;
import de.paralleluniverse.Faithcaio.AuctionHouse.ServerBidder;
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
        super(base, "remove", "cancel", "delete");
    }

    public boolean execute(CommandSender sender, String[] args)
    {
        if (args.length < 1)
        {
            sender.sendMessage("/ah remove <AuctionID>");
            sender.sendMessage("/ah remove p:<Player>");
            sender.sendMessage("/ah remove Server");
            sender.sendMessage("/ah remove all");
            return true;
        }
        Arguments arguments = new Arguments(args);

        if (arguments.getString("1") != null)
        {
            if (arguments.getString("1").equalsIgnoreCase("all"))
            {
                if (sender.hasPermission("auctionhouse.delete.all"))
                {
                    AuctionManager.getInstance().remAllConfirm.add(sender);
                    sender.sendMessage(t("rem_all"));
                    sender.sendMessage(t("rem_confirm"));
                    return true;
                }
                sender.sendMessage(t("perm")+" "+t("rem_all_perm"));
                return true;
            }
            else
            {
                if (arguments.getString("1").equalsIgnoreCase("Server"))
                {
                    if (sender.hasPermission("auctionhouse.delete.server"))
                    {
                        AuctionManager.getInstance().remBidderConfirm.put(sender, ServerBidder.getInstance());
                        sender.sendMessage(t("rem_allserv"));
                        sender.sendMessage(t("rem_confirm"));
                        return true;
                    }
                    sender.sendMessage(t("perm")+" "+t("rem_allserv_perm"));
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
                    if (!(sender.hasPermission("auctionhouse.delete.id")))
                    {
                        sender.sendMessage(t("perm")+" "+t("rem_id_perm"));
                        return true;
                    }
                    if (AuctionManager.getInstance().getAuction(id).owner instanceof ServerBidder)
                    {
                        if (!(sender.hasPermission("auctionhouse.delete.server")))
                        {
                            sender.sendMessage(t("perm")+" "+t("rem_serv_perm"));
                            return true;
                        }
                    }
                    ItemStack item = AuctionManager.getInstance().getAuction(id).item;
                    AuctionManager.getInstance().cancelAuction(AuctionManager.getInstance().getAuction(id));
                    sender.sendMessage(t("i")+" "+t("rem_id",id) + item.toString());//TODO
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
                    if (!(sender.hasPermission("auctionhouse.delete.player")))
                    {
                        sender.sendMessage(t("perm")+" "+t("rem_own_perm"));
                        return true;
                    }
                }
                else
                {
                    if (!(sender.hasPermission("auctionhouse.delete.player.other")))
                    {
                        sender.sendMessage(t("perm")+" "+t("rem_other_perm"));
                        return true;
                    }
                }

                if (!(player.getAuctions().isEmpty()))
                {
                    AuctionManager.getInstance().remBidderConfirm.put(sender, player);
                    sender.sendMessage(t("rem_play",player.getName()));
                    sender.sendMessage(t("rem_confirm"));
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
        return super.getUsage() + " <<AuctionId>|all <Player>>";
    }

    public String getDescription()
    {
        return t("command_rem");
    }
}