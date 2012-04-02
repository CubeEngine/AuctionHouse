package de.paralleluniverse.Faithcaio.AuctionHouse.Commands;

import de.paralleluniverse.Faithcaio.AuctionHouse.AbstractCommand;
import de.paralleluniverse.Faithcaio.AuctionHouse.Arguments;
import de.paralleluniverse.Faithcaio.AuctionHouse.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Faithcaio
 */
public class RemoveCommand extends AbstractCommand
{    
    public RemoveCommand(BaseCommand base)
    {
        super("remove", base);
    }

    public boolean execute(CommandSender sender, String[] args)
    {
        if (args.length < 1)
        {
            sender.sendMessage("/ah remove <AuctionID>");
            sender.sendMessage("/ah remove p:<Player>");//TODO /ah confirm
            sender.sendMessage("/ah remove Server");
            sender.sendMessage("/ah remove all");
            return true;
        }
        Arguments arguments = new Arguments(args);
        
        if (arguments.getString("1")!=null)
        {
            if (arguments.getString("1").equalsIgnoreCase("all"))
            {
                if (sender.hasPermission("auctionhouse.delete.all"))
                {
                    int max = AuctionManager.getInstance().auctions.size();
                    if (max == 0)
                    {
                        sender.sendMessage("Info: No Auctions detected!");
                        return true;
                    }
                    for (int i=max-1;i>=0;--i)
                    {
                        AuctionManager.getInstance().cancelAuction(AuctionManager.getInstance().auctions.get(i));
                    }
                    sender.sendMessage("Info: All Auctions deleted!");
                    return true;
                }
                sender.sendMessage("You do not have Permission to delete all Auctions!");
                return true;
            }
            else
            {
            if (arguments.getString("1").equalsIgnoreCase("Server"))
            {
                if (sender.hasPermission("auctionhouse.delete.server"))
                {
                    int max = ServerBidder.getInstance().getAuctions().size();
                    if (max == 0)
                    {
                        sender.sendMessage("Info: No ServerAuctions detected!");
                        return true;
                    }
                    for (int i=max-1;i>=0;--i)
                    {
                        AuctionManager.getInstance().cancelAuction(ServerBidder.getInstance().getAuctions().get(i));
                    }
                    sender.sendMessage("Info: All ServerAuctions deleted!");
                    return true;
                }
                sender.sendMessage("You do not have Permission to delete all ServerAuctions!");
                return true;
            }    
            }
            {
                Integer id = arguments.getInt("1");
                if (id != null)
                {
                    if (AuctionManager.getInstance().getAuction(id)==null)
                    {
                        sender.sendMessage("Error: Auction"+id+"does not exist!");
                        return true;    
                    }
                    if (!(sender.hasPermission("auctionhouse.delete.id")))
                    {
                        sender.sendMessage("You do not have Permission to delete Auctions!");
                        return true;
                    }
                    if(AuctionManager.getInstance().getAuction(id).owner instanceof ServerBidder)
                        if (!(sender.hasPermission("auctionhouse.delete.server")))
                        {
                            sender.sendMessage("You do not have Permission to delete ServerAuctions!");
                            return true;
                        }
                    AuctionHouse.debug("Remove per Id");
                    ItemStack item=AuctionManager.getInstance().getAuction(id).item;
                    AuctionManager.getInstance().cancelAuction(AuctionManager.getInstance().getAuction(id));      
                    sender.sendMessage("Info: Removed auction #"+id+" "+item.toString());
                    return true;
                }
                sender.sendMessage("Error: Invalid Command!");
                return true;
            }
        }
        else
        {
            Bidder player = arguments.getBidder("p");
            if (player == null)
            {
                sender.sendMessage("Info: Player \""+arguments.getString("p")+"\" does not exist or has no Auction!");
                return true;
            }
            else
            {
                AuctionHouse.debug("Remove per Player");
                if (player.player.getPlayer().equals((Player)sender))
                {   if (!(sender.hasPermission("auctionhouse.delete.player")))
                    {
                        sender.sendMessage("You do not have Permission to delete all your Auctions at once!");
                        return true;
                    }}
                else
                {   if (!(sender.hasPermission("auctionhouse.delete.player.other")))
                    {
                        sender.sendMessage("You do not have Permission to delete all Auctions of a Player!");
                        return true;
                    }}
                
                if(!(player.getAuctions().isEmpty()))
                {    
                    int bids = player.activeBids.size();    
                    for (int i=bids;i >=0 ;--i)
                        {
                            if (player.activeBids.get(i).owner==player)
                            {
                                AuctionManager.getInstance().cancelAuction(player.getAuctions(player).get(0));
                                AuctionHouse.debug("Remove per Player");
                            }
                        }
                    sender.sendMessage("Info:Removed "+(player.activeBids.size()-bids)+
                                       " auctions of "+player.player.getName());
                    return true;
                }
                sender.sendMessage("Info: Player \""+arguments.getString("p")+"\" has no Auctions!");
                return true;
            }
        }
    }

    @Override
        public String getUsage()
    {
        return "/ah remove <<AuctionId>|all <Player>>";
    }
    public String getDescription()
    {
        return "Removes an auction.";
    }
}