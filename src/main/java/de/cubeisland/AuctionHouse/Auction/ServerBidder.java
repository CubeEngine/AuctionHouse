package de.cubeisland.AuctionHouse.Auction;

/**
 *
 * @author Faithcaio
 */
public class ServerBidder extends Bidder
{
    private static ServerBidder instance = null;

    public ServerBidder()
    {
        super(null);
    }

    public static Bidder getInstance()
    {
        if (instance == null)
        {
            instance = new ServerBidder();
        }
        return instance;
    }
    
    public ServerBidder(int id)
    {
        super(id,"*Server");
    }

    public static Bidder getInstance(int id)
    {
        if (instance == null)
        {
            instance = new ServerBidder(id);
        }
        return instance;
    }
}
