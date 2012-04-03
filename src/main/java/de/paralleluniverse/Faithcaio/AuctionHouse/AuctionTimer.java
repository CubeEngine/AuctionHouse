package de.paralleluniverse.Faithcaio.AuctionHouse;

import java.util.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
                //AuctionHouse.debug("Timer Run");
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
                            AuctionHouse.debug("Auction ended!");
                            while (auction.owner != auction.bids.peek().getBidder())
                            {
                                Bidder winner = auction.bids.peek().getBidder();
                                if (econ.getBalance(winner.player.getName())>auction.bids.peek().getAmount())
                                {
                                    econ.withdrawPlayer(winner.player.getName(),auction.bids.peek().getAmount());
                                    if (!(auction.owner instanceof ServerBidder))
                                        econ.depositPlayer(auction.owner.player.getName(), auction.bids.peek().getAmount());
                                    winner.itemContainer.addItem(auction);
                                    if (winner.player.isOnline())
                                        winner.player.getPlayer().sendMessage("Congratulations! You just bought: "+auction.item.toString()+
                                                                            " for "+econ.format(auction.bids.peek().getAmount()));
                                    //TODO Meldung beim Login für offline Player!
                                    manager.finishAuction(auction);
                                    break; //NPE Prevention
                                }
                                if (winner.player.isOnline())
                                    winner.player.getPlayer().sendMessage("Not enough money to pay what you bid for!");
                                //TODO Strafe für zuwenig Geld haben aber voll viel bieten wollen
                                //TODO wenn Strafe erhalten darf nicht Höchstbietender werden!!!!
                                winner.removeAuction(auction);
                                auction.bids.pop();
                            }
                            if (auction.bids.peek().getBidder()==auction.owner)
                            {
                                //TODO offline Meldung an Owner Auktion Failed
                                if (auction.owner instanceof ServerBidder)
                                    AuctionHouse.log("No Bids | Auction failed!");
                                else
                                    if (auction.owner.player.isOnline())
                                        auction.owner.player.getPlayer().sendMessage("Nobody bid on your auction and it got canceled.");
                                manager.cancelAuction(auction);
                            }
                        }
                        else
                            break; //No Auctions in Timeframe
                    }
                }
                
               // AuctionTimer.getInstance().timer.cancel();
               // AuctionTimer.getInstance().scheduleTimer(AuctionManager.getInstance());  
            }
        };
        notifyTask = new TimerTask()
        {
            public void run()
            {
                //AuctionHouse.debug("Notify Timer Run");
                AuctionManager manager = AuctionManager.getInstance();
                if (!(manager.getAuctions().isEmpty()))
                {
                    List<Player> playerlist = new ArrayList<Player>();
                    for (Bidder bidder : Bidder.getInstances().values())
                    {
                        if (bidder.player.isOnline() && bidder.playerNotification)
                            playerlist.add(bidder.player.getPlayer());
                    }
                    if (playerlist.size()==0) return; //No Player online to notify
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
                                    if (Bidder.getInstance(playerlist.get(k)).subscriptions.contains(auction))
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
                                            
                                            if (playerlist.get(k) ==auction.bids.peek().getBidder().player)
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
                //AuctionTimer.getInstance().notifyTimer.cancel();
                //AuctionTimer.getInstance().scheduleNotify(AuctionManager.getInstance()); 
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
    
    /* 
    public void scheduleNotify(AuctionManager auctions)
    {
        notifyTimer.purge();
        notifyTimer = new Timer();
        long nextAuction;
        if (!auctions.getAuctions().isEmpty())
        {
            nextAuction = auctions.getEndingAuctions().get(0).auctionEnd - System.currentTimeMillis();
            int size=config.auction_notifyTime.size();
            for (int i=0;i<size;++i)
            {
                if (nextAuction-config.auction_notifyTime.get(i)<0)
                    continue;
                nextAuction -= config.auction_notifyTime.get(i);
            }
        }
        else
        {
            AuctionHouse.debug("Notify Timer Start");
            nextAuction = 1000;
        }
        
        notifyTimer.schedule(notifyTask, nextAuction);
    }
    public void scheduleTimer(AuctionManager auctions)
    {
        timer.purge();
        timer = new Timer();
        long nextAuction;
        if (!auctions.getAuctions().isEmpty())
        {
            nextAuction = auctions.getEndingAuctions().get(0).auctionEnd - System.currentTimeMillis();
        }
        else
        {
            AuctionHouse.debug("Timer Start");
            nextAuction = 1000;
        }    
        timer.schedule(timerTask, nextAuction);    
    }
   
    public void schedule(AuctionManager auctions)
    {
        //notifyTimer.cancel();
        //timer.cancel();
        AuctionHouse.debug("Timer ReStart"); 
        this.scheduleNotify(auctions);
        this.scheduleTimer(auctions);
    }
    */
    public void firstschedule(AuctionManager auctions)
    {
        AuctionHouse.debug("First Timer Start");             
   //     this.scheduleNotify(auctions);
   //     this.scheduleTimer(auctions);
        timer.schedule(timerTask, 1000, 1000);
        notifyTimer.schedule(notifyTask, 1000, 1000);
    }
    
}
