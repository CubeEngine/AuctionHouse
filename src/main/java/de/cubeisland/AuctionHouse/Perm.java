package de.cubeisland.AuctionHouse;

import static de.cubeisland.AuctionHouse.AuctionHouse.t;
import java.util.EnumMap;
import java.util.Map;
import org.bukkit.command.CommandSender;

/**
 * Check permissions and send message to ths User
 * 
 * @author Faithcaio
 */
public enum Perm 
{
    command_add("add_Auc_perm"),
    command_add_multi("add_multi_perm"),
    command_bid("bid_perm"),
    sign_auctionbox("event_sign_perm"),
    command_getItems("get_perm"),
    use("help_perm"),
    command_info("info_perm"),
    command_info_others("info_perm_other"),
    command_notify("note_perm"),
    command_delete_all("rem_all_perm"),
    command_delete_id("rem_id_perm"),
    command_delete_server("rem_serv_perm"),
    command_delete_player("rem_own_perm"),
    command_delete_player_other("rem_other_perm"),
    command_search("search_perm"),
    command_undobid("undo_perm"),
    sign_start("event_sign_perm"),
    sign_list("event_sign_perm"),
    sign_create_box("event_signplacebox_perm"),
    sign_create_add("event_signplaceadd_perm"),
    sign_create_list("event_signplacelist_perm"),
    command_sub("sub_perm"),
    command_bid_infinite(null),
    command_add_cheatItems(null);
  
    private final String text;

    private Perm(final String text)
    {
        this.text = text;
    }
    
    /**
     * Transform perm to String and check for auctionhouse permission
     * @param sender
     * @param perm
     * @return true if sender has the perm permission
     */
    private boolean checkPerm (CommandSender sender, Perm perm)
    {
        return sender.hasPermission("auctionhouse."+perm.name().replace("_", "."));
    }
 
    /**
     * Check for permission
     * @param sender
     * @param perm
     * @return true if sender has the perm permission
     */
    public boolean check (CommandSender sender, Perm perm)
    {
        if (this.checkPerm(sender, perm))
        {
            this.send(sender, perm);
            return false;
        }
        else
            return true;
    }
    
    /**
     * Send permission message if needed
     * 
     * @param sender
     * to check permission from
     * @param perm
     * permission in the enum
     */
    private void send (CommandSender sender, Perm perm)
    {
        if (this.text!=null)
        {
            sender.sendMessage(t("perm")+" "+t(this.text));
        }
    }
}