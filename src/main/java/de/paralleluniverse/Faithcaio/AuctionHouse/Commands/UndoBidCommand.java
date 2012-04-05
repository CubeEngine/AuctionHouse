package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import static de.paralleluniverse.Faithcaio.AuctionHouse.Translation.Translator.t;
import de.paralleluniverse.Faithcaio.AuctionHouse.AbstractCommand;
import de.paralleluniverse.Faithcaio.AuctionHouse.Arguments;
import de.paralleluniverse.Faithcaio.AuctionHouse.AuctionManager;
import de.paralleluniverse.Faithcaio.AuctionHouse.BaseCommand;
import de.paralleluniverse.Faithcaio.AuctionHouse.Bidder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Faithcaio
 */
public class UndoBidCommand extends AbstractCommand
{
    public UndoBidCommand(BaseCommand base)
    {
        super(base, "undobid");
    }

    public boolean execute(CommandSender sender, String[] args)
    {
        if (!(sender.hasPermission("auctionhouse.use.undobid")))
        {
            sender.sendMessage(t("perm")+" "+t("undo_perm"));
            return true;
        }
        if (args.length < 1)
        {
            sender.sendMessage("/ah undoBid last");
            sender.sendMessage("/ah undoBid <AuctionID>");
            return true;
        }
        Arguments arguments = new Arguments(args);
        Player psender = (Player) sender;
        if (arguments.getString("1").equals("last"))
        {
            if (Bidder.getInstance(psender).getlastAuction(Bidder.getInstance(psender)) == null)
            {
                sender.sendMessage(t("pro")+" "+t("undo_pro"));
                return true;
            }
            if (Bidder.getInstance(psender).getlastAuction(Bidder.getInstance(psender)).undobid(Bidder.getInstance(psender)))
            {
                sender.sendMessage(t("i")+" "+t("undo_redeem"));
                return true;
            }
            else
            {
                sender.sendMessage(t("pro")+" "+t("undo_pro"));
                return true;
            }
        }
        if (arguments.getInt("1") != null)
        {
            if (AuctionManager.getInstance().getAuction(arguments.getInt("1")) == null)
            {
                sender.sendMessage(t("i")+" "+t("auction_no_exist",arguments.getInt("1")));
                return true;
            }
            if (AuctionManager.getInstance().getAuction(arguments.getInt("1")).undobid(Bidder.getInstance(psender)))
            {
                sender.sendMessage(t("i")+" "+t("undo_bid_n",arguments.getInt("1")));
                return true;
            }
            else
            {
                sender.sendMessage(t("i")+" "+t("undo_bidder"));
                return true;
            }
        }
        sender.sendMessage(t("i")+" "+t("undo_fail"));
        return true;
    }

    @Override
    public String getUsage()
    {
        return super.getUsage() + " <last|<AuctionId>>";
    }

    public String getDescription()
    {
        return t("command_undo");
    }
}
