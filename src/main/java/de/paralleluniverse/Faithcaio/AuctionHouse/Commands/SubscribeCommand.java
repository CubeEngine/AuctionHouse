package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import static de.paralleluniverse.Faithcaio.AuctionHouse.Translation.Translator.t;
import de.paralleluniverse.Faithcaio.AuctionHouse.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Faithcaio
 */
public class SubscribeCommand extends AbstractCommand
{
    public SubscribeCommand(BaseCommand base)
    {
        super(base, "subscribe", "sub");
    }

    public boolean execute(CommandSender sender, String[] args)
    {
        if (args.length < 1)
        {
            sender.sendMessage("/ah sub <i:<AuctionID>");
            sender.sendMessage("/ah sub <m:<Material>");
            sender.sendMessage("/ah unsub <i:<AuctionID>");
            sender.sendMessage("/ah unsub <m:<Material>");
            return true;
        }
        Bidder bidder = Bidder.getInstance((Player) sender);
        Arguments arguments = new Arguments(args);
        if (arguments.getString("m") != null)
        {
            if (arguments.getMaterial("m") != null)
            {
                bidder.addSubscription(arguments.getMaterial("m"));
                sender.sendMessage(t("i")+" "+t("sub_add_mat",arguments.getString("m")));
                if (!bidder.playerNotification)
                {
                    sender.sendMessage(t("i")+" "+t("sub_note"));
                }
                return true;
            }
            sender.sendMessage(t("i")+" "+arguments.getString("m") + " "+t("no_valid_item"));
            return true;
        }
        if (arguments.getString("i") != null)
        {
            if (arguments.getInt("i") != null)
            {
                if (AuctionManager.getInstance().getAuction(arguments.getInt("i")) != null)
                {
                    sender.sendMessage(t("i")+" "+t("sub_add",arguments.getInt("i")));
                    if (!bidder.playerNotification)
                    {
                        sender.sendMessage(t("i")+" "+t("sub_note"));
                    }
                    return true;
                }
                sender.sendMessage(t("e")+" "+t("auction_no_exist",arguments.getString("i")));
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