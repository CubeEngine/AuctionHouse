package de.cubeisland.AuctionHouse;

import de.cubeisland.AuctionHouse.Auction.Auction;
import de.cubeisland.AuctionHouse.Auction.Bidder;
import de.cubeisland.AuctionHouse.Auction.ServerBidder;
import static de.cubeisland.AuctionHouse.AuctionHouse.t;
import de.cubeisland.AuctionHouse.Database.Database;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;

/**
 *
 * @author Faithcaio
 */
public class AuctionTimer
{
    private final TimerTask timerTask;
    private final TimerTask notifyTask;
    private Timer timer;
    private Timer notifyTimer;
    private static AuctionTimer instance = null;
    private static final AuctionHouse plugin = AuctionHouse.getInstance();
    private static final AuctionHouseConfiguration config = plugin.getConfiguration();
    private final Database db;

    public AuctionTimer()
    {
        this.db = AuctionHouse.getInstance().getDB();
        timerTask = new TimerTask()
        {
            public void run()
            {
                Manager manager = Manager.getInstance();
                if (!(manager.getAuctions().isEmpty()))
                {
                    Economy econ = AuctionHouse.getInstance().getEconomy();
                    List<Auction> auctionlist = manager.getEndingAuctions();
                    int size = auctionlist.size();
                    for (int i = 0; i < size; ++i)
                    {
                        Auction auction = auctionlist.get(i);
                        if ((System.currentTimeMillis() + 600 > auction.getAuctionEnd())
                                && (System.currentTimeMillis() - 600 < auction.getAuctionEnd()))
                        {
                            List<Bidder> rPlayer = new ArrayList<Bidder>();
                            while (auction.getOwner() != auction.getBids().peek().getBidder())
                            {
                                Bidder winner = auction.getBids().peek().getBidder();
                                if (rPlayer.contains(winner))//remove punished Player
                                {
                                    auction.getBids().pop();
                                    continue;
                                }

                                if (econ.getBalance(winner.getName()) > auction.getBids().peek().getAmount())
                                {
                                    double topbid = auction.getBids().peek().getAmount();
                                    econ.withdrawPlayer(winner.getName(), topbid);
                                    if (!(auction.getOwner() instanceof ServerBidder))
                                    {

                                        econ.depositPlayer(auction.getOwner().getName(), topbid);
                                        econ.withdrawPlayer(auction.getOwner().getName(), topbid * config.auction_comission / 100);
                                        if (auction.getOwner().isOnline())
                                        {
                                            auction.getOwner().getPlayer().sendMessage(t("time_sold",
                                                                    auction.getItem().getType().toString()+" x"+auction.getItem().getAmount(),
                                                                    econ.format(topbid - topbid * config.auction_comission / 100),
                                                                    econ.format(topbid * config.auction_comission / 100)));
                                        }
                                    }
                                    winner.getContainer().addItem(auction);
                                    Manager.getInstance().adjustPrice(auction.getItem(), topbid);
                                    if (winner.isOnline())
                                    {
                                        winner.getPlayer().sendMessage(t("time_won",auction.getItem().getType().toString()+" x"+auction.getItem().getAmount()
                                                                         ,econ.format(topbid)));
                                    }
                                    else
                                    {
                                        winner.setNotifyState(Bidder.NOTIFY_WIN);
                                        Util.updateNotifyData(winner);
                                    }
                                    manager.cancelAuction(auction, true);
                                    break; //NPE Prevention
                                }
                                else
                                {
                                    if (winner.isOnline())
                                    {
                                        winner.getPlayer().sendMessage(t("time_pun1"));
                                        winner.getPlayer().sendMessage(t("time_pun2",config.auction_punish));
                                        winner.getPlayer().sendMessage(t("time_pun3"));
                                    }
                                    rPlayer.add(winner);
                                    econ.withdrawPlayer(winner.getName(), auction.getBids().peek().getAmount() * config.auction_punish / 100);
                                    winner.removeAuction(auction);
                                    auction.getBids().pop();
                                }
                            }
                            if (auction.getBids().isEmpty()) return;
                            if (auction.getBids().peek().getBidder().equals(auction.getOwner()))
                            {
                                auction.getOwner().setNotifyState(Bidder.NOTIFY_CANCEL);
                                Util.updateNotifyData(auction.getOwner());
                                if (!(auction.getOwner() instanceof ServerBidder))
                                {
                                    econ.withdrawPlayer(auction.getOwner().getName(), auction.getBids().peek().getAmount() * config.auction_comission / 100);
                                    if (auction.getOwner().isOnline())
                                    {
                                        auction.getOwner().getPlayer().sendMessage(t("time_stop"));
                                        if (auction.getBids().peek().getAmount() != 0)
                                        {
                                            auction.getOwner().getPlayer().sendMessage(t("time_pun4",config.auction_comission));
                                        }
                                    }
                                }
                                manager.cancelAuction(auction, false);
                            }
                        }
                        else
                        {
                            break; //No Auctions in Timeframe
                        }
                    }
                }
            }
        };
        notifyTask = new TimerTask()
        {
            public void run()
            {
                Manager manager = Manager.getInstance();
                if (!(manager.getAuctions().isEmpty()))
                {
                    List<OfflinePlayer> playerlist = new ArrayList<OfflinePlayer>();
                    for (Bidder bidder : Bidder.getInstances().values())
                    {
                        if (bidder.isOnline() && bidder.hasNotifyState(Bidder.NOTIFY_STATUS))
                        {
                            playerlist.add(bidder.getPlayer());
                        }
                    }
                    if (playerlist.isEmpty())
                    {
                        return; //No Player online to notify
                    }
                    List<Auction> auctionlist = manager.getEndingAuctions();
                    int size = auctionlist.size();
                    int note = config.auction_notifyTime.size();
                    long nextAuction = auctionlist.get(0).getAuctionEnd() - System.currentTimeMillis();
                    if (config.auction_notifyTime.get(0) + 600 < nextAuction)
                    {
                        return; //No Notifications now
                    }
                    for (int i = 0; i < size; ++i)
                    {

                        Auction auction = auctionlist.get(i);
                        nextAuction = auction.getAuctionEnd() - System.currentTimeMillis();
                        for (int j = 0; j < note; ++j)
                        {
                            if ((config.auction_notifyTime.get(j) + 600 > nextAuction)
                                    && (config.auction_notifyTime.get(j) - 600 < nextAuction))
                            {
                                note = j + 1;
                                int max = playerlist.size();
                                for (int k = 0; k < max; ++k)
                                {
                                    if (Bidder.getInstance(playerlist.get(k)).getSubs().contains(auction))
                                    {
                                        if (playerlist.get(k).equals(auction.getOwner().getPlayer()))
                                        {
                                            playerlist.get(k).getPlayer().sendMessage(t("time_end1",auction.getId(),Util.convertTime(auction.getAuctionEnd() - System.currentTimeMillis())));
                                        }
                                        else
                                        {
                                            String out = "";
                                            out += t("time_end2",auction.getId(),Util.convertTime(auction.getAuctionEnd() - System.currentTimeMillis()));
                                            
                                            if (playerlist.get(k).equals(auction.getBids().peek().getBidder().getPlayer()))
                                            {
                                                out += " "+t("time_high");
                                            }
                                            else
                                            {
                                                out += " "+t("time_low");
                                            }

                                            playerlist.get(k).getPlayer().sendMessage(out);
                                        }
                                    }
                                }
                                continue; // out of j-loop
                            }
                        }
                    }
                }
            }
        };
        timer = new Timer();
        notifyTimer = new Timer();
    }

    public static AuctionTimer getInstance()
    {
        if (instance == null)
        {
            instance = new AuctionTimer();
        }
        return instance;
    }

    public void firstschedule(Manager auctions)
    {
        timer.schedule(timerTask, 1000, 1000);
        notifyTimer.schedule(notifyTask, 1000, 1000);
    }
}
