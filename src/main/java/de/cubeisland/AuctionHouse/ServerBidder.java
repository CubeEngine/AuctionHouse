package de.cubeisland.AuctionHouse;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
}
