package de.paralleluniverse.Faithcaio.AuctionHouse;

import java.util.List;
import org.bukkit.configuration.Configuration;

public class AuctionHouseConfiguration
{


    public final int      auction_undoTime;            //In seconds (-1 = Infinite)
    public final int      auction_maxAuctions_overall;          //Overall
    public final int      auction_maxAuctions_player;   //per Player
    public final boolean  auction_maxAuctions_opIgnore; //Op ignore perPlayer limit NOT Overall Limit!
    public final int      auction_maxLength;            //in hours
    public final boolean  auction_opCanCheat;           //Op can Cheat Items for Auction
    public final List<String> auction_blacklist;        //Blacklist Materials
    public final String   auction_timeFormat;           //Time Format Output
    //TODO blacklist einbauen
    public AuctionHouseConfiguration(Configuration config)
    {
        this.auction_maxAuctions_player = config.getInt("auction.maxAuctions.player");
        this.auction_maxAuctions_opIgnore = config.getBoolean("auction.maxAuctions.opIgnore");
        this.auction_undoTime = config.getInt("auction.undoTime");
        this.auction_maxAuctions_overall = config.getInt("auction.maxAuctions.overall");
        this.auction_maxLength = config.getInt("auction.maxLength");
        this.auction_opCanCheat = config.getBoolean("auction.opCanCheat");
        this.auction_blacklist = config.getStringList("auction.blacklist");
        this.auction_timeFormat = config.getString("auction.timeFormat");
        //TODO Preis fuer AuktionsErstellung (Formel mit Startgebot?)
        
    }
}
