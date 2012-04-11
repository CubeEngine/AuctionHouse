package de.cubeisland.AuctionHouse.Commands;

import de.cubeisland.AuctionHouse.*;
import static de.cubeisland.AuctionHouse.Translation.Translator.t;
import java.util.List;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.time.DateFormatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
/**
 *
 * @author Faithcaio
 */
public class ListCommand extends AbstractCommand
{
    
    private static final AuctionHouse plugin = AuctionHouse.getInstance();
    private static final AuctionHouseConfiguration config = plugin.getConfigurations();
    Economy econ = AuctionHouse.getInstance().getEconomy();
    
    public ListCommand(BaseCommand base)
    {
        super(base, "List");
    }

    public boolean execute(CommandSender sender, String[] args)
    {
        MyUtil.get().sendInfo(sender, AuctionManager.getInstance().getAuctions());
        return true;
    }

    public String getDescription()
    {
        return t("command_list");
    }
}