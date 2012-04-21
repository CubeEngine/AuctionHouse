package de.cubeisland.AuctionHouse.Commands;

import de.cubeisland.AuctionHouse.*;
import de.cubeisland.AuctionHouse.Auction.Auction;
import de.cubeisland.AuctionHouse.Auction.Bidder;
import de.cubeisland.AuctionHouse.Auction.ServerBidder;
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
    private static final AuctionHouseConfiguration config = plugin.getConfiguration();
    
    
    public InfoCommand(BaseCommand base)
    {
        super(base, "info");
    }

    public boolean execute(CommandSender sender, CommandArgs args)
    {
        if (args.isEmpty())
        {
            sender.sendMessage(t("info_title1"));
            sender.sendMessage(t("info_title2"));
            sender.sendMessage(t("info_title3"));
            sender.sendMessage(t("info_title4"));
            sender.sendMessage(t("info_title5"));
            sender.sendMessage(t("info_title6"));
            sender.sendMessage(t("info_title7"));
            sender.sendMessage(t("info_title8"));
            sender.sendMessage("");
            return true;
        }
        if (!Perm.get().check(sender,"auctionhouse.command.info"))
        {
            
            return true;
        }

        if (args.getString(0).equalsIgnoreCase("Bids"))//bidding
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
                    Util.sendInfo(sender, auction);
                }
            }
        }
        else
        {
            if (args.getString(0).equalsIgnoreCase("own"))
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
                    Util.sendInfo(sender, auction);
                }
            }
            else
            {
                if (args.getString(0).equalsIgnoreCase("sub"))
                {
                    List<Auction> auctions = Bidder.getInstance((Player) sender).getSubs();
                    auctions.removeAll(Bidder.getInstance(sender).getOwnAuctions());
                    
                    int max = auctions.size();
                    if (max == 0)
                    {
                        sender.sendMessage(t("i")+" "+t("info_no_sub"));
                    }
                    for (int i = 0; i < max; ++i)
                    {
                        Auction auction = auctions.get(i);
                        Util.sendInfo(sender, auction);
                    }
                }
                else    
                {

                    if (args.getString(0).equalsIgnoreCase("lead"))
                    {
                        List<Auction> auctions = Bidder.getInstance((Player) sender).getLeadingAuctions();
                        auctions.removeAll(Bidder.getInstance(sender).getOwnAuctions());
                        int max = auctions.size();
                        AuctionHouse.debug("max: " + max);
                        if (max == 0)
                        {
                            sender.sendMessage(t("i")+" "+t("info_no_lead"));
                        }
                        for (int i = 0; i < max; ++i)
                        {
                            Auction auction = auctions.get(i);
                            Util.sendInfo(sender, auction);
                        }
                    }
                    else
                    {
                        if (args.getString(0).equalsIgnoreCase("*Server"))
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
                                Util.sendInfo(sender, auction);
                            }
                        }
                        else
                        {
                            Integer id = args.getInt(0);
                            if (id != null)
                            {
                                if (Manager.getInstance().getAuction(id) != null)
                                {
                                    Util.sendInfo(sender, Manager.getInstance().getAuction(id));
                                }
                                else
                                {
                                    sender.sendMessage(t("i")+" "+t("auction_no_exist",id));
                                }
                            }
                            else
                            {
                                if (!Perm.get().check(sender,"auctionhouse.command.info.others")) return true;
                                Bidder player = args.getBidder(0);
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
                                        Util.sendInfo(sender, auction);
                                    }
                                }
                                else
                                {
                                    sender.sendMessage(t("e")+" "+t("info_p_no_auction",args.getString(0)));
                                }
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
        return super.getUsage() + " <AuctionId>";
    }

    public String getDescription()
    {
        return t("command_info");
    }
}
