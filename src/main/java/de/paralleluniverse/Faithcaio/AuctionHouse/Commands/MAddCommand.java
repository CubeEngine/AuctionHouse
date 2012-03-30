package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import de.paralleluniverse.Faithcaio.AuctionHouse.*;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Faithcaio
 */
public class MAddCommand extends AbstractCommand
{
    private static final AuctionHouse plugin = AuctionHouse.getInstance();
    private static final AuctionHouseConfiguration config = plugin.getConfigurations();
    
    public MAddCommand(BaseCommand base)
    {
        super("madd", base);
    }


    public boolean execute(CommandSender sender, String[] args)
    {
        int auctionAmount=1;
        String[] nargs=args;
        try { auctionAmount = Integer.parseInt(args[2]); }
        catch (NumberFormatException ex) { return false; }
        if (auctionAmount < 1 )
        {
            sender.sendMessage("Info: AuctionAmount must be greater than 0");
            return false;
        }
        sender.sendMessage("Debug: AuctionAmount OK");
        nargs[2] = nargs[3];
        nargs[3] = nargs[4];
        return AddCommand.getInstance().execute(sender, nargs, auctionAmount);
    }
    @Override
    public String getDescription()
    {
        return "Adds multiple acutions.";
    }
}