package de.paralleluniverse.Faithcaio.AuctionHouse;

import org.bukkit.configuration.Configuration;

public class AuctionHouseConfiguration
{
    public final int player_maxBet;
    public final boolean OPignoreMaxBet;
   
    public AuctionHouseConfiguration(Configuration config)
    {
        this.player_maxBet = config.getInt("price.player.maxBet");
        this.OPignoreMaxBet = config.getBoolean("price.OPignoreMaxBet");  
    }
}
