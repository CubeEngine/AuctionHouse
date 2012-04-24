package de.cubeisland.AuctionHouse.Commands;

import de.cubeisland.AuctionHouse.AbstractCommand;
import de.cubeisland.AuctionHouse.AuctionHouse;
import static de.cubeisland.AuctionHouse.AuctionHouse.t;
import de.cubeisland.AuctionHouse.BaseCommand;
import de.cubeisland.AuctionHouse.CommandArgs;
import org.bukkit.command.CommandSender;

/**
 * reload the plugin
 * 
 * @author Faithcaio
 */
public class ReloadCommand extends AbstractCommand
{
 
    public ReloadCommand(BaseCommand base)
    {
        super(base, "reload");
    }
    
    public boolean execute(CommandSender sender, CommandArgs args)
    {
        AuctionHouse.getInstance().onDisable();
        AuctionHouse.getInstance().onEnable();
        AuctionHouse.log("reload complete");
        return true;
    }
    
        @Override
    public String getUsage()
    {
        return super.getUsage();
    }

    public String getDescription()
    {
        return t("command_reload");
    }
}
