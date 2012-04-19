package de.cubeisland.AuctionHouse.Commands;

import de.cubeisland.AuctionHouse.Arguments;
import de.cubeisland.AuctionHouse.Manager;
import de.cubeisland.AuctionHouse.BaseCommand;
import de.cubeisland.AuctionHouse.AbstractCommand;
import de.cubeisland.AuctionHouse.AuctionHouse;
import de.cubeisland.AuctionHouse.Bidder;
import static de.cubeisland.AuctionHouse.AuctionHouse.t;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Faithcaio
 */
public class UnSubscribeCommand extends AbstractCommand
{
    public UnSubscribeCommand(BaseCommand base)
    {
        super(base, "unsubscribe", "unsub");
    }

    public boolean execute(CommandSender sender, String[] args)
    {
        if (args.length < 1)
        {
            sender.sendMessage("/ah unsub <i:<AuctionID>");
            sender.sendMessage("/ah unsub <m:<Material>");
            sender.sendMessage("/ah sub <i:<AuctionID>");
            sender.sendMessage("/ah sub <m:<Material>");
            return true;
        }
        if (sender instanceof ConsoleCommandSender)
        {
            AuctionHouse.log("Console can not unsubcribe");
            return true;
        }
        Bidder bidder = Bidder.getInstance((Player) sender);
        Arguments arguments = new Arguments(args);
        if (arguments.getString("m") != null)
        {
            if (arguments.getMaterial("m") != null)
            {
                bidder.removeSubscription(arguments.getMaterial("m"));
                sender.sendMessage(t("i")+" "+t("sub_rem_mat",arguments.getMaterial("m").getType().toString()));
                return true;
            }
            sender.sendMessage(t("e")+" "+arguments.getString("m") + " "+t("no_valid_item"));
            return true;
        }
        if (arguments.getString("i") != null)
        {
            if (arguments.getInt("i") != null)
            {
                if (Manager.getInstance().getAuction(arguments.getInt("i")) != null)
                {
                    if (bidder.removeSubscription(Manager.getInstance().getAuction(arguments.getInt("i"))))
                    {
                        sender.sendMessage(t("i")+" "+t("sub_rem",arguments.getInt("i")));
                        return true;
                    }
                    sender.sendMessage(t("e")+" "+t("sub_rem_no"));
                    return true;
                }
                sender.sendMessage(t("e")+" "+t("auction_no_exist",arguments.getInt("i")));
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
        return super.getUsage() + " <i:<AuctionID>]|m:<Material>>";
    }

    public String getDescription()
    {
        return t("command_sub");
    }
}