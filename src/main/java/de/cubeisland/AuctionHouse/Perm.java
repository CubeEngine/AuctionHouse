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
        //TODO post-release: enum oder so
        if (!sender.hasPermission(perm))
        {
                 if (perm.equalsIgnoreCase("auctionhouse.command.add"))                 sender.sendMessage(t("perm")+" "+t("add_Auc_perm")); 
            else if (perm.equalsIgnoreCase("auctionhouse.command.add.multi"))           sender.sendMessage(t("perm")+" "+t("add_multi_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.command.bid"))                 sender.sendMessage(t("perm")+" "+t("bid_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.sign.auctionbox"))             sender.sendMessage(t("perm")+" "+t("event_sign_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.command.getItems"))            sender.sendMessage(t("perm")+" "+t("get_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.help"))                        sender.sendMessage(t("perm")+" "+t("help_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.command.info"))                sender.sendMessage(t("perm")+" "+t("info_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.command.info.others"))         sender.sendMessage(t("perm")+" "+t("info_perm_other"));
            else if (perm.equalsIgnoreCase("auctionhouse.command.notify"))              sender.sendMessage(t("perm")+" "+t("note_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.command.delete.all"))          sender.sendMessage(t("perm")+" "+t("rem_all_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.command.delete.id"))           sender.sendMessage(t("perm")+" "+t("rem_id_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.command.delete.server"))       sender.sendMessage(t("perm")+" "+t("rem_allserv_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.command.delete.server"))       sender.sendMessage(t("perm")+" "+t("rem_serv_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.command.delete.player"))       sender.sendMessage(t("perm")+" "+t("rem_own_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.command.delete.player.other")) sender.sendMessage(t("perm")+" "+t("rem_other_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.command.search"))              sender.sendMessage(t("perm")+" "+t("search_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.command.undobid"))             sender.sendMessage(t("perm")+" "+t("undo_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.sign.start"))                  sender.sendMessage(t("perm")+" "+t("event_sign_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.sign.create.box"))         sender.sendMessage(t("perm")+" "+t("event_signplacebox_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.sign.create.add"))         sender.sendMessage(t("perm")+" "+t("event_signplaceadd_perm"));
            else if (perm.equalsIgnoreCase("auctionhouse.command.sub"))                 sender.sendMessage(t("perm")+" "+t("sub_perm"));
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
