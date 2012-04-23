package de.cubeisland.AuctionHouse.Commands;

import de.cubeisland.AuctionHouse.AbstractCommand;
import de.cubeisland.AuctionHouse.Auction.Bidder;
import de.cubeisland.AuctionHouse.AuctionHouse;
import static de.cubeisland.AuctionHouse.AuctionHouse.t;
import de.cubeisland.AuctionHouse.BaseCommand;
import de.cubeisland.AuctionHouse.CommandArgs;
import de.cubeisland.AuctionHouse.Manager;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Manages your Subscriptions
 * 
 * @author Faithcaio
 */
public class UnSubscribeCommand extends AbstractCommand
{
    public UnSubscribeCommand(BaseCommand base)
    {
        super(base, "unsubscribe", "unsub");
    }

    public boolean execute(CommandSender sender, CommandArgs args)
    {
        if (args.isEmpty())
        {
            sender.sendMessage(t("unsub_title1"));
            sender.sendMessage(t("unsub_title2"));
            sender.sendMessage(t("unsub_title3"));
            sender.sendMessage(t("sub_title2"));
            sender.sendMessage(t("sub_title3"));
            sender.sendMessage("");
            return true;
        }
        if (sender instanceof ConsoleCommandSender)
        {
            AuctionHouse.log("Console can not unsubcribe");
            return true;
        }
        Bidder bidder = Bidder.getInstance((Player) sender);
        if (args.getString("m") != null)
        {
            if (args.getItem("m") != null)
            {
                if (bidder.removeSubscription(args.getItem("m")))
                {
                    sender.sendMessage(t("i")+" "+t("sub_rem_mat",args.getItem("m").getType().toString()));
                    return true;
                }
                sender.sendMessage(t("e")+" "+t("sub_rem_no_mat"));
                return true;
            }
            sender.sendMessage(t("e")+" "+args.getString("m") + " "+t("no_valid_item"));
            return true;
        }
        if (args.getString("i") != null)
        {
            if (args.getInt("i") != null)
            {
                if (Manager.getInstance().getAuction(args.getInt("i")) != null)
                {
                    if (bidder.removeSubscription(Manager.getInstance().getAuction(args.getInt("i"))))
                    {
                        sender.sendMessage(t("i")+" "+t("sub_rem",args.getInt("i")));
                        return true;
                    }
                    sender.sendMessage(t("e")+" "+t("sub_rem_no"));
                    return true;
                }
                sender.sendMessage(t("e")+" "+t("auction_no_exist",args.getInt("i")));
                return true;
            }
            sender.sendMessage(t("e")+" "+t("invalid_id"));
            return true;
        }
        sender.sendMessage(t("e")+" "+t("invalid_com"));
        return true;
    }

    @Override
    public String getUsage()
    {
        return super.getUsage() + " m:<Material>";
    }

    public String getDescription()
    {
        return t("command_sub");
    }
}