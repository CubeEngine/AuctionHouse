package de.paralleluniverse.Faithcaio.AuctionHouse;

import static de.paralleluniverse.Faithcaio.AuctionHouse.Translation.Translator.t;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

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
    private static final AuctionHouseConfiguration config = plugin.getConfigurations();

    public AuctionTimer()
    {
        timerTask = new TimerTask()
        {
            public void run()
            {
                AuctionManager manager = AuctionManager.getInstance();
                if (!(manager.getAuctions().isEmpty()))
                {
                    Economy econ = AuctionHouse.getInstance().getEconomy();
                    List<Auction> auctionlist = manager.getEndingAuctions();
                    int size = auctionlist.size();
                    for (int i = 0; i < size; ++i)
                    {
                        Auction auction = auctionlist.get(i);
                        if ((System.currentTimeMillis() + 600 > auction.auctionEnd)
                                && (System.currentTimeMillis() - 600 < auction.auctionEnd))
                        {
                            List<Bidder> rPlayer = new ArrayList<Bidder>();
                            AuctionHouse.debug("Auction #"+auction.id+" ended!");
                            while (auction.owner != auction.bids.peek().getBidder())
                            {
                                Bidder winner = auction.bids.peek().getBidder();
                                if (rPlayer.contains(winner))//remove punished Player
                                {
                                    auction.bids.pop();
                                    continue;
                                }

                                if (econ.getBalance(winner.getName()) > auction.bids.peek().getAmount())
                                {
                                    double money = auction.bids.peek().getAmount();
                                    econ.withdrawPlayer(winner.getName(), money);
                                    if (!(auction.owner instanceof ServerBidder))
                                    {

                                        econ.depositPlayer(auction.owner.getName(), money);
                                        econ.withdrawPlayer(auction.owner.getName(), money * config.auction_comission / 100);
                                        if (auction.owner.isOnline())
                                        {
                                            auction.owner.getPlayer().sendMessage(t("time_sold",auction.item.toString(),
                                                                    econ.format(money - money * config.auction_comission / 100),
                                                                    econ.format(money * config.auction_comission / 100)));
                                        }
                                    }
                                    winner.getContainer().addItem(auction);
                                    if (winner.isOnline())
                                    {
                                        winner.getPlayer().sendMessage(t("time_won",auction.item.toString(),econ.format(money)));
                                    }
                                    else
                                    {
                                        winner.notify = true;
                                    }
                                    manager.finishAuction(auction);
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
                                    econ.withdrawPlayer(winner.getName(), auction.bids.peek().getAmount() * config.auction_punish / 100);
                                    winner.removeAuction(auction);
                                    auction.bids.pop();
                                }
                            }
                            if (auction.bids.isEmpty()) return;
                            if (auction.bids.peek().getBidder().equals(auction.owner))
                            {
                                auction.owner.notifyCancel = true;
                                if (!(auction.owner instanceof ServerBidder))
                                {
                                    econ.withdrawPlayer(auction.owner.getName(), auction.bids.peek().getAmount() * config.auction_comission / 100);
                                    if (auction.owner.isOnline())
                                    {
                                        auction.owner.getPlayer().sendMessage(t("time_stop"));
                                        if (auction.bids.peek().getAmount() != 0)
                                        {
                                            auction.owner.getPlayer().sendMessage(t("time_pun4",config.auction_comission));
                                        }
                                    }
                                }
                                manager.cancelAuction(auction);
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
                AuctionManager manager = AuctionManager.getInstance();
                if (!(manager.getAuctions().isEmpty()))
                {
                    List<Player> playerlist = new ArrayList<Player>();
                    for (Bidder bidder : Bidder.getInstances().values())
                    {
                        if (bidder.isOnline() && bidder.playerNotification)
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
                    long nextAuction = auctionlist.get(0).auctionEnd - System.currentTimeMillis();
                    if (config.auction_notifyTime.get(0) + 600 < nextAuction)
                    {
                        return; //No Notifications now
                    }
                    for (int i = 0; i < size; ++i)
                    {

                        Auction auction = auctionlist.get(i);
                        nextAuction = auction.auctionEnd - System.currentTimeMillis();
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
                                        int last = config.auction_notifyTime.size() - j;
                                        if (playerlist.get(k).equals(auction.owner.getPlayer()))
                                        {
                                            if (last > 3)
                                            {
                                                playerlist.get(k).sendMessage(t("time_end4",auction.id));
                                            }
                                            else if (last == 3)
                                            {
                                                playerlist.get(k).sendMessage(t("time_end3",auction.id));
                                            }
                                            else if (last == 2)
                                            {
                                                playerlist.get(k).sendMessage(t("time_end2",auction.id));
                                            }
                                            else if (last == 1)
                                            {
                                                playerlist.get(k).sendMessage(t("time_end1",auction.id));
                                            }
                                        }
                                        else
                                        {
                                            String out = "";
                                            if (last > 3)
                                            {
                                                out += t("time_end42",auction.id);
                                            }
                                            else if (last == 3)
                                            {
                                                out += t("time_end32",auction.id);
                                            }
                                            else if (last == 2)
                                            {
                                                out += t("time_end22",auction.id);
                                            }
                                            else if (last == 1)
                                            {
                                                out += t("time_end12",auction.id);
                                            }

                                            if (playerlist.get(k) == auction.bids.peek().getBidder().getOffPlayer())
                                            {
                                                out += t("time_high");
                                            }
                                            else
                                            {
                                                out += t("time_low");
                                            }

                                            playerlist.get(k).sendMessage(out);
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

    public void firstschedule(AuctionManager auctions)
    {
        timer.schedule(timerTask, 1000, 1000);
        notifyTimer.schedule(notifyTask, 1000, 1000);
    }
}
