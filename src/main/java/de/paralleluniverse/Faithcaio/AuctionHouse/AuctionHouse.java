package de.paralleluniverse.Faithcaio.AuctionHouse;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.Configuration;
import org.bukkit.Server;

public class AuctionHouse extends JavaPlugin
{
    protected static Logger logger = null;
    public static boolean debugMode = false;
    
    protected Server server;
    protected PluginManager pm;
    protected AuctionHouseConfiguration config;
    protected File dataFolder;

    public AuctionHouse()
    {
    }

    public void onEnable()
    {
        logger = this.getLogger();
        this.server = this.getServer();
        this.pm = this.server.getPluginManager();
        this.dataFolder = this.getDataFolder();

        this.dataFolder.mkdirs();
        
        Configuration configuration = this.getConfig();
        configuration.options().copyDefaults(true);
        debugMode = configuration.getBoolean("debug");
        this.config = new AuctionHouseConfiguration(configuration);
        
        this.saveConfig();

        log("Version " + this.getDescription().getVersion() + " enabled");
    }

    public void onDisable()
    {
        log("Version " + this.getDescription().getVersion() + " disabled");
    }

    public static void log(String msg)
    {
        logger.log(Level.INFO, msg);
    }

    public static void error(String msg)
    {
        logger.log(Level.SEVERE, msg);
    }

    public static void error(String msg, Throwable t)
    {
        logger.log(Level.SEVERE, msg, t);
    }

    public static void debug(String msg)
    {
        if (debugMode)
        {
            log("[debug] " + msg);
        }
    }
}
