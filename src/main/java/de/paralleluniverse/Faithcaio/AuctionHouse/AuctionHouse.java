package de.paralleluniverse.Faithcaio.AuctionHouse;

import de.paralleluniverse.Faithcaio.AuctionHouse.Commands.AddCommand;
import de.paralleluniverse.Faithcaio.AuctionHouse.Commands.HelpCommand;
import de.paralleluniverse.Faithcaio.AuctionHouse.Commands.RemoveCommand;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AuctionHouse extends JavaPlugin
{
    private static AuctionHouse instance = null;
    protected static Logger logger = null;
    public static boolean debugMode = false;
    
    protected Server server;
    protected PluginManager pm;
    protected AuctionHouseConfiguration config;
    protected File dataFolder;

    public AuctionHouse()
    {
        instance = this;
    }
    public static AuctionHouse getInstance()
    {
        return instance;
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
        
        BaseCommand baseCommand = new BaseCommand(this);
        baseCommand
            .registerSubCommand(new   HelpCommand(baseCommand))
            .registerSubCommand(new    AddCommand(baseCommand))
            .registerSubCommand(new RemoveCommand(baseCommand))
            .setDefaultCommand("help");
        this.getCommand("auctionhouse").setExecutor(baseCommand);

        log("Version " + this.getDescription().getVersion() + " enabled");
    }
    
    public void onDisable()
    {
        log("Version " + this.getDescription().getVersion() + " disabled");
    }
    
    public AuctionHouseConfiguration getConfigurations()
    {
        return this.config;
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
