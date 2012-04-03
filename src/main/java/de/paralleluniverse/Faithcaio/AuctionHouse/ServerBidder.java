package de.paralleluniverse.Faithcaio.AuctionHouse;

import java.util.ArrayList;
/**
 *
 * @author Faithcaio
 */
public class ServerBidder extends Bidder
{
    public final String server;
    public final ArrayList<Auction> activeBids;
    private static ServerBidder instance = null;
    
    public ServerBidder () 
    {
       super(null);
       this.server = "Server";  
       this.activeBids = new ArrayList<Auction>();
    }
    
    public static Bidder getInstance()
    {
        if (instance == null)
        {
            instance = new ServerBidder();
        }
        return instance;
    }
}
