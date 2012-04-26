package de.cubeisland.AuctionHouse.Commands;

import de.cubeisland.AuctionHouse.AbstractCommand;
import de.cubeisland.AuctionHouse.Auction.Bidder;
import de.cubeisland.AuctionHouse.AuctionHouse;
import static de.cubeisland.AuctionHouse.AuctionHouse.t;
import de.cubeisland.AuctionHouse.BaseCommand;
import de.cubeisland.AuctionHouse.CommandArgs;
import de.cubeisland.AuctionHouse.Perm;
import de.cubeisland.AuctionHouse.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Changes notification
 * 
 * @author Faithcaio
 */
public class NotifyCommand extends AbstractCommand
{
    public NotifyCommand(BaseCommand base)
    {
        super(base, "notify", "n");
    }

    public boolean execute(CommandSender sender, CommandArgs args)
    {
        if (args.isEmpty())
        {
            sender.sendMessage(t("note_title1"));
            sender.sendMessage(t("note_title2"));
            sender.sendMessage(t("note_title3"));
            sender.sendMessage(t("note_title4"));
            sender.sendMessage("");
            return true;
        }
        if (!Perm.command_notify.check(sender)) return true;
        if (args.getString(0) == null)
        {
            return true;
        }
        if (sender instanceof ConsoleCommandSender)
        {
            AuctionHouse.log("Console can not use notification!");
            return true;
        }
        Bidder bidder = Bidder.getInstance((Player) sender);
        if (args.getString(0).equalsIgnoreCase("true") || args.getString(0).equalsIgnoreCase("on"))
        {
            bidder.setNotifyState(Bidder.NOTIFY_STATUS);
        }
        if (args.getString(0).equalsIgnoreCase("false") || args.getString(0).equalsIgnoreCase("off"))
        {
            bidder.unsetNotifyState(Bidder.NOTIFY_STATUS);
        }
        if (args.getString(0).equalsIgnoreCase("toggle") || args.getString(0).equalsIgnoreCase("t"))
        {
            bidder.toggleNotifyState(Bidder.NOTIFY_STATUS);
        }
        if (bidder.hasNotifyState(Bidder.NOTIFY_STATUS))
            sender.sendMessage(t("i")+" "+t("note_on"));
        else
            sender.sendMessage(t("i")+" "+t("note_off"));
        Util.updateNotifyData(bidder);
        return true;
    }

    @Override
    public String getUsage()
    {
        return super.getUsage() + " toggle";
    }

    public String getDescription()
    {
        return t("command_note");
    }
}
