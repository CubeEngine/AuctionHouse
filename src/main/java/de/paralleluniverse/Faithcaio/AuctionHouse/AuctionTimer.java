package de.paralleluniverse.Faithcaio.AuctionHouse;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 *
 * @author Faithcaio
 */
public class AuctionTimer 
{
    private TimerTask timerTask;
    private TimerTask notifyTask;
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
                timer.cancel();
                
                AuctionHouse.debug("Auction beendet!");
                AuctionTimer.getInstance().scheduleTimer(AuctionManager.getInstance());  
            }
        };
        notifyTask = new TimerTask()
        {
            public void run()
            {
                notifyTimer.cancel();
                AuctionManager manager = AuctionManager.getInstance();
                if (manager.getAuctions().isEmpty()) return;
                Auction auction = manager.getEndingAuctions().get(0);
                List<Player> playerlist  = Arrays.asList(plugin.server.getOnlinePlayers());
                int max=playerlist.size();
                for (int i = 0;i<max;++i)
                {
                    Bidder player = Bidder.getInstance(playerlist.get(i));
                    if (!player.playerNotification)
                    {
                        playerlist.remove(i);
                        continue;
                    }
                    
                    if (!player.getAuctions().contains(auction))
                    {
                        playerlist.remove(i);
                        continue;
                    }
                    if (player==auction.owner)
                        playerlist.get(i).sendMessage("Your auction #"+auction.id+" is ending soon!");
                    else
                    {
                        if (player==auction.bids.peek().getBidder())
                            playerlist.get(i).sendMessage("Auction #"+auction.id+" is ending soon! You are the highest Bidder now!");
                        else
                            playerlist.get(i).sendMessage("Auction #"+auction.id+" is ending soon! You are not the highest Bidder!");
                    }
                }
                
                
                
                AuctionTimer.getInstance().scheduleNotify(AuctionManager.getInstance()); 
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
    public void scheduleNotify(AuctionManager auctions)
    {
        notifyTimer.cancel();
        Long nextAuction = auctions.getEndingAuctions().get(0).auctionEnd - System.currentTimeMillis();
        int size=config.auction_notifyTime.size();
        for (int i=0;i<size;++i)
        {
            if (nextAuction-config.auction_notifyTime.get(i)<0)
                continue;
            nextAuction -= config.auction_notifyTime.get(i);
        }
        notifyTimer.schedule(notifyTask, nextAuction);
    }
    public void scheduleTimer(AuctionManager auctions)
    {
        timer.cancel();
        timer.schedule(timerTask, auctions.getEndingAuctions().get(0).auctionEnd - System.currentTimeMillis());
    }
    
    public void schedule(AuctionManager auctions)
    {
        this.scheduleNotify(auctions);
        this.scheduleTimer(auctions);
    }
    
}
