package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import de.paralleluniverse.Faithcaio.AuctionHouse.AbstractCommand;
import de.paralleluniverse.Faithcaio.AuctionHouse.BaseCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Anselm
 */
public class AddCommand extends AbstractCommand{
    
    public AddCommand(BaseCommand base)
    {
        super("add", base);
    }


    public boolean execute(CommandSender sender, String[] args)
    {
        sender.sendMessage("Debug: Added nothing yet");
        if (args.length <= 2) return false;
        if (Material.getMaterial(args[0])!=null)//ITEM
        {
        sender.sendMessage("Debug: MaterialDetection OK");
        }
        if (Integer.parseInt(args[1])!=null)//MENGE
        {
        sender.sendMessage("Debug: AmountDetection OK");
        }
        
        sender.sendMessage("");

        for (AbstractCommand command : getBase().getRegisteredCommands())
        {
            sender.sendMessage(command.getUsage());
            sender.sendMessage("    " + command.getDescription());
            sender.sendMessage("");
        }

        return true;
    }

    @Override
    public String getDescription()
    {
        return "Adds an auction";
    }
}
