package de.paralleluniverse.Faithcaio.AuctionHouse;

import org.bukkit.configuration.Configuration;

public class AuctionHouseConfiguration
{
    public final int player_maxBet;
    public final boolean opIgnoreMaxBet;
    public final int auction_undoTimer; //In Seconds (-1 for Infinite)
    public final int auction_maxAuctions;
   
    public AuctionHouseConfiguration(Configuration config)
    {
        this.player_maxBet = config.getInt("player.maxBet");
        this.opIgnoreMaxBet = config.getBoolean("opIgnoreMaxBet");
        this.auction_undoTimer = config.getInt("auction.UndoTimer");
        this.auction_maxAuctions = config.getInt("auction.maxAuctions");
    }
}
