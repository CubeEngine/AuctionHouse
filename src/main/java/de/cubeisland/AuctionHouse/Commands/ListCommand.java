package de.cubeisland.AuctionHouse.Commands;

import de.cubeisland.AuctionHouse.AbstractCommand;
import de.cubeisland.AuctionHouse.AuctionHouse;
import static de.cubeisland.AuctionHouse.AuctionHouse.t;
import de.cubeisland.AuctionHouse.AuctionHouseConfiguration;
import de.cubeisland.AuctionHouse.BaseCommand;
import de.cubeisland.AuctionHouse.CommandArgs;
import de.cubeisland.AuctionHouse.Manager;
import de.cubeisland.AuctionHouse.Util;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;
/**
 
 * @author Faithcaio
 */
public class ListCommand extends AbstractCommand
{
    
    private static final AuctionHouse plugin = AuctionHouse.getInstance();
    private static final AuctionHouseConfiguration config = plugin.getConfiguration();
    Economy econ = AuctionHouse.getInstance().getEconomy();
    
    public ListCommand(BaseCommand base)
    {
        super(base, "list");
    }

    public boolean execute(CommandSender sender, CommandArgs args)
    {
        Util.sendInfo(sender, Manager.getInstance().getAuctions());
        return true;
    }

    public String getDescription()
    {
        return t("command_list");
    }
}