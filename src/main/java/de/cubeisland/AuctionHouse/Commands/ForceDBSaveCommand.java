package de.cubeisland.AuctionHouse.Commands;

import de.cubeisland.AuctionHouse.AbstractCommand;
import de.cubeisland.AuctionHouse.BaseCommand;
import de.cubeisland.AuctionHouse.CommandArgs;
import org.bukkit.command.CommandSender;
import static de.cubeisland.AuctionHouse.AuctionHouse.t;

/**
 * flush DataBase and resave all 
 * 
 * @author Faithcaio
 */
public class ForceDBSaveCommand extends AbstractCommand
{
   
    public ForceDBSaveCommand(BaseCommand base)
    {
        super(base, "force save");
    }
    
    public boolean execute(CommandSender sender, CommandArgs args)
    {
        //TODO forceSave 
        //TODO do not show in help perm
        /*
         *       auctionhouse.admun.reload: true
      auctionhouse.admin.save: true
         */
        return true;
    }
    
        @Override
    public String getUsage()
    {
        return super.getUsage();
    }

    public String getDescription()
    {
        return t("command_save");
    }
}
