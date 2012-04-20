package de.cubeisland.AuctionHouse;

import de.cubeisland.AuctionHouse.Auction.Bidder;
import static de.cubeisland.AuctionHouse.AuctionHouse.t;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Faithcaio
 */
public class Perm {
    
    private static Perm instance = null;
    
    
    public static Perm get()
    {
        if (instance == null)
        {
            instance = new Perm();
        }
        return instance; 
    }
    
    public boolean check(Player sender, String perm)
    {
        if (perm == null)
        {
            return false;
        }
        //TODO enum oder so später
        if (!sender.hasPermission(perm))
        {
                 if (perm.equalsIgnoreCase("auctionhouse.use.add"))              sender.sendMessage(t("perm")+" "+t("add_Auc_perm")); 
            else if (perm.equalsIgnoreCase("auctionhouse.use.add.multi"))        sender.sendMessage(t("perm")+" "+t("add_multi_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.use.bid"))              sender.sendMessage(t("perm")+" "+t("bid_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.getItems.sign"))        sender.sendMessage(t("perm")+" "+t("event_sign_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.getItems.command"))     sender.sendMessage(t("perm")+" "+t("get_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.help"))                 sender.sendMessage(t("perm")+" "+t("help_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.info"))                 sender.sendMessage(t("perm")+" "+t("info_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.info.others"))          sender.sendMessage(t("perm")+" "+t("info_perm_other"));
            else if (perm.equalsIgnoreCase("auctionhouse.notify.command"))       sender.sendMessage(t("perm")+" "+t("note_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.delete.all"))           sender.sendMessage(t("perm")+" "+t("rem_all_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.delete.id"))            sender.sendMessage(t("perm")+" "+t("rem_id_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.delete.server"))        sender.sendMessage(t("perm")+" "+t("rem_allserv_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.delete.server"))        sender.sendMessage(t("perm")+" "+t("rem_serv_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.delete.player"))        sender.sendMessage(t("perm")+" "+t("rem_own_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.delete.player.other"))  sender.sendMessage(t("perm")+" "+t("rem_other_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.search"))               sender.sendMessage(t("perm")+" "+t("search_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.use.undobid"))          sender.sendMessage(t("perm")+" "+t("undo_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.use.addsign"))          sender.sendMessage(t("perm")+" "+t("event_sign_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.create.boxsign"))       sender.sendMessage(t("perm")+" "+t("event_signplacebox_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.create.addsign"))       sender.sendMessage(t("perm")+" "+t("event_signplaceadd_perm"));
            return false;
        }
        return true;
    }
    
    public boolean check(Bidder sender, String perm)
    {
       return this.check(sender.getPlayer(), perm);  
    }
    
    public boolean check(CommandSender sender, String perm)
    {
        if (sender instanceof Player)
        {
            return this.check((Player)sender, perm);
        }
        return true;
    }
}
