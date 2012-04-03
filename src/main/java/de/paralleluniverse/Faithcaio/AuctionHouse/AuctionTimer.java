package de.paralleluniverse.Faithcaio.AuctionHouse;

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
    
    public AuctionTimer ()
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
                    for (int i=0;i<size;++i)
                    {
                        Auction auction = auctionlist.get(i);
                        if ((System.currentTimeMillis()+600>auction.auctionEnd)
                          &&(System.currentTimeMillis()-600<auction.auctionEnd)) 
                        {
                            List<Bidder> rPlayer = new ArrayList<Bidder>();
                            AuctionHouse.debug("Auction ended!");
                            while (auction.owner != auction.bids.peek().getBidder())
                            {
                                Bidder winner = auction.bids.peek().getBidder();
                                if (rPlayer.contains(winner)) //Player was Highest Bidder but not enough money
                                {
                                    auction.bids.pop();
                                    continue;
                                }
                                
                                if (econ.getBalance(winner.getName())>auction.bids.peek().getAmount())
                                {
                                    econ.withdrawPlayer(winner.getName(),auction.bids.peek().getAmount());
                                    if (!(auction.owner instanceof ServerBidder))
                                        econ.depositPlayer(auction.owner.getName(), auction.bids.peek().getAmount());
                                    winner.getContainer().addItem(auction);
                                    if (winner.isOnline())
                                        winner.getPlayer().sendMessage("Congratulations! You just bought: "+auction.item.toString()+
                                                                            " for "+econ.format(auction.bids.peek().getAmount()));
                                    //TODO Meldung beim Login für offline Player!
                                    manager.finishAuction(auction);
                                    break; //NPE Prevention
                                }
                                else
                                {
                                    if (winner.isOnline())
                                        winner.getPlayer().sendMessage("Not enough money to pay what you bid for!");
                                    rPlayer.add(winner);
                                    econ.withdrawPlayer(winner.getName(), auction.bids.peek().getAmount()*config.auction_punish / 100);
                                    //TODO Strafe für zuwenig Geld haben aber voll viel bieten wollen
                                    //TODO Strafe = 1/5 des Preises config!!!
                                    //TODO wenn Strafe erhalten darf nicht Höchstbietender werden!!!! ok s.o.
                                    winner.removeAuction(auction);
                                    auction.bids.pop();
                                }
                            }
                            if (auction.bids.peek().getBidder()==auction.owner)
                            {
                                //TODO offline Meldung an Owner Auktion Failed
                                if (auction.owner instanceof ServerBidder)
                                    AuctionHouse.log("No Bids | Auction failed!");
                                else
                                    if (auction.owner.isOnline())
                                        auction.owner.getPlayer().sendMessage("Nobody bid on your auction and it got canceled.");
                                manager.cancelAuction(auction);
                            }
                        }
                        else
                            break; //No Auctions in Timeframe
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
                            playerlist.add(bidder.getPlayer());
                    }
                    if (playerlist.isEmpty()) return; //No Player online to notify
                    List<Auction> auctionlist = manager.getEndingAuctions();
                    int size = auctionlist.size();  
                    int note = config.auction_notifyTime.size();
                    long nextAuction = auctionlist.get(0).auctionEnd - System.currentTimeMillis();
                    if (config.auction_notifyTime.get(0)+600<nextAuction) return; //No Notifications now
                    for (int i=0;i<size;++i)
                    {
                        
                        Auction auction = auctionlist.get(i);
                        nextAuction = auction.auctionEnd - System.currentTimeMillis();
                        for (int j=0;j<note;++j)
                        {
                            if((config.auction_notifyTime.get(j)+600>nextAuction)
                            &&(config.auction_notifyTime.get(j)-600<nextAuction))
                            {
                                note=j+1;
                                AuctionHouse.debug("Notify Time!");
                                int max=playerlist.size();
                                for (int k = 0;k<max;++k)
                                {
                                    if (Bidder.getInstance(playerlist.get(k)).getSubs().contains(auction))
                                    {
                                        int last = config.auction_notifyTime.size()-j;
                                        if (playerlist.get(k)==auction.owner)
                                        {
                                            if (last > 3)
                                                playerlist.get(k).sendMessage("Your auction #"+auction.id+" is ending soon!");
                                            else if (last == 3)
                                                playerlist.get(k).sendMessage("Your auction #"+auction.id+" ends in 3...");
                                            else if (last == 2)
                                                playerlist.get(k).sendMessage("Your auction #"+auction.id+" ends in 2..");
                                            else if (last == 1)
                                                playerlist.get(k).sendMessage("Your auction #"+auction.id+" ends in 1.");
                                        }
                                        else
                                        {
                                            String out = "Auction #"+auction.id;
                                            if (last > 3)
                                                out += " is ending soon!";
                                            else if (last == 3)          
                                                out += " ends in 3...";
                                            else if (last == 2)          
                                                out += " ends in 2..";
                                            else if (last == 1)          
                                                out += " ends in 1.";
                                            
                                            if (playerlist.get(k) ==auction.bids.peek().getBidder().getOffPlayer())
                                                out += " You are the highest Bidder now!";
                                            else
                                                out += " You are not the highest Bidder!";
                                            
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
        AuctionHouse.debug("First Timer Start");             
        timer.schedule(timerTask, 1000, 1000);
        notifyTimer.schedule(notifyTask, 1000, 1000);
    }
    
}
