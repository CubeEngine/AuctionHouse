package de.cubeisland.AuctionHouse.Commands;

import de.cubeisland.AuctionHouse.*;
import static de.cubeisland.AuctionHouse.AuctionHouse.t;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Faithcaio
 */
public class InfoCommand extends AbstractCommand
{
    
    private static final AuctionHouse plugin = AuctionHouse.getInstance();
    private static final AuctionHouseConfiguration config = plugin.getConfigurations();
    
    
    public InfoCommand(BaseCommand base)
    {
        super(base, "info");
    }

    public boolean execute(CommandSender sender, String[] args)
    {
        if (args.length < 1)
        {
            sender.sendMessage("/ah info <AuctionID>");
            sender.sendMessage("/ah info <Player>");
            sender.sendMessage("/ah info Bids");
            sender.sendMessage("/ah info lead");
            sender.sendMessage("/ah info own");
            sender.sendMessage("/ah info *Server");
            return true;
        }
        Arguments arguments = new Arguments(args);
        if (!Perm.get().check(sender,"auctionhouse.info"))
        {
            
            return true;
        }

        if (arguments.getString("1").equalsIgnoreCase("Bids"))//bidding
        {
            List<Auction> auctions = Bidder.getInstance((Player) sender).getAuctions();
            int max = auctions.size();
            if (max == 0)
            {
                sender.sendMessage(t("i")+" "+t("info_no_bid"));
            }
            for (int i = 0; i < max; ++i)
            {
                Auction auction = auctions.get(i);
                if (auction.getOwner() != (Player) sender)
                {
                    MyUtil.sendInfo(sender, auction);
                }
            }
        }
        else
        {
            if (arguments.getString("1").equalsIgnoreCase("own"))
            {
                List<Auction> auctions = Bidder.getInstance((Player) sender).getOwnAuctions();
                int max = auctions.size();
                if (max == 0)
                {
                    sender.sendMessage(t("i")+" "+t("info_no_start"));
                }
                for (int i = 0; i < max; ++i)
                {
                    Auction auction = auctions.get(i);
                    MyUtil.sendInfo(sender, auction);
                }
            }
            else
            {

                if (arguments.getString("1").equalsIgnoreCase("lead"))
                {
                    List<Auction> auctions = Bidder.getInstance((Player) sender).getLeadingAuctions();
                    int max = auctions.size();
                    AuctionHouse.debug("max: " + max);
                    if (max == 0)
                    {
                        sender.sendMessage(t("i")+" "+t("info_no_lead"));
                    }
                    for (int i = 0; i < max; ++i)
                    {
                        Auction auction = auctions.get(i);
                        MyUtil.sendInfo(sender, auction);
                    }
                }
                else
                {
                    if (arguments.getString("1").equalsIgnoreCase("*Server"))
                    {
                        List<Auction> auctions = ServerBidder.getInstance().getAuctions();
                        int max = auctions.size();
                        AuctionHouse.debug("max: " + max);
                        if (max == 0)
                        {
                            sender.sendMessage(t("i")+" "+t("info_no_serv"));
                        }
                        for (int i = 0; i < max; ++i)
                        {
                            Auction auction = auctions.get(i);
                            MyUtil.sendInfo(sender, auction);
                        }
                    }
                    else
                    {
                        Integer id = arguments.getInt("1");
                        if (id != null)
                        {
                            if (Manager.getInstance().getAuction(id) != null)
                            {
                                MyUtil.sendInfo(sender, Manager.getInstance().getAuction(id));
                            }
                            else
                            {
                                sender.sendMessage(t("i")+" "+t("auction_no_exist",id));
                            }
                        }
                        else
                        {
                            if (!Perm.get().check(sender,"auctionhouse.info.others")) return true;
                            Bidder player = arguments.getBidder("1");
                            if (player != null)
                            {
                                AuctionHouse.debug("Player Auction");
                                List<Auction> auctions = player.getAuctions(player);
                                int max = auctions.size();
                                AuctionHouse.debug("max: " + max);
                                if (max == 0)
                                {
                                    sender.sendMessage(t("e")+t("info_no_auction",player.getName()));
                                }
                                for (int i = 0; i < max; ++i)
                                {
                                    Auction auction = auctions.get(i);
                                    MyUtil.sendInfo(sender, auction);
                                }
                            }
                            else
                            {
                                sender.sendMessage(t("perm")+" "+t("info_p_no_auction",arguments.getString("1")));
                            }
                        }
                    }
                }
            }
        }
        return true;
    }



    @Override
    public String getUsage()
    {
        return super.getUsage() + " <<AuctionId>|<Player>> )";
    }

    public String getDescription()
    {
        return t("command_info");
    }
}
