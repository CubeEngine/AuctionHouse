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
            command_add,
            command_add_multi,
            command_bid,
            sign_auctionbox,
            command_getItems,
            use,
            command_info,
            command_info_others,
            command_notify,
            command_delete_all,
            command_delete_id,
            command_delete_server,
            command_delete_player,
            command_delete_player_other,
            command_search,
            command_undobid,
            sign_start,
            sign_list,
            sign_create_box,
            sign_create_add,
            sign_create_list,
            command_sub,
            command_bid_infinite,
            command_add_cheatItems;
  
    private Map<Perm,String> perms;

    /**
     * Initialize Permission Messages
     */
    public void Init()
    {
        this.perms = new EnumMap<Perm,String>(Perm.class);
        this.perms.put(command_add,"add_Auc_perm"); 
        this.perms.put(command_add_multi,"add_multi_perm");
        this.perms.put(command_bid,"bid_perm");
        this.perms.put(sign_auctionbox,"event_sign_perm");
        this.perms.put(command_getItems,"get_perm");
        this.perms.put(use,"help_perm");
        this.perms.put(command_info,"info_perm");
        this.perms.put(command_info_others,"info_perm_other");
        this.perms.put(command_notify,"note_perm");
        this.perms.put(command_delete_all,"rem_all_perm");
        this.perms.put(command_delete_id,"rem_id_perm");
        this.perms.put(command_delete_server,"rem_serv_perm");
        this.perms.put(command_delete_player,"rem_own_perm");
        this.perms.put(command_delete_player_other,"rem_other_perm");
        this.perms.put(command_search,"search_perm");
        this.perms.put(command_undobid,"undo_perm");
        this.perms.put(sign_start,"event_sign_perm");
        this.perms.put(sign_list,"event_sign_perm");
        this.perms.put(sign_create_box,"event_signplacebox_perm");
        this.perms.put(sign_create_add,"event_signplaceadd_perm");
        this.perms.put(sign_create_list,"event_signplacelist_perm");
        this.perms.put(command_sub,"sub_perm");  
    }

/*
 * 
 */
    /**
     * Transform perm to String and check for auctionhouse permission
     * @param sender
     * @param perm
     * @return true if sender has the perm permission
     */
    private boolean checkPerm (CommandSender sender, Perm perm)
    {
        return sender.hasPermission("auctionhouse."+perm.toString().replace("_", "."));
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
        if (this.perms.get(perm)!=null)
        {
            sender.sendMessage(t("perm")+" "+t(this.perms.get(perm)));
        }
    }
}