package de.cubeisland.AuctionHouse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author CodeInfection
 */
public class Database
{
    private final String host;
    private final short port;
    private final String user;
    private final String pass;
    private final String name;

    private final Connection connection;

    public Database(String host, short port, String user, String pass, String name)
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (Throwable t)
        {
            throw new IllegalStateException("Couldn't find the MySQL driver!", t);
        }

        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.name = name;

        try
        {
            this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + String.valueOf(this.port) + "/" + this.name, this.user, this.pass);
        }
        catch (SQLException e)
        {
            throw new IllegalStateException("Failed to connect to the database server!", e);
        }
        this.setupStructure();
    }

    private void setupStructure()
    {
        this.exec(      "CREATE TABLE IF NOT EXISTS `auctions` ("+
                        "`id` int(10) unsigned NOT NULL,"+
                        "`ownerid` int(11) NOT NULL,"+
                        "`item` varchar(42) NOT NULL,"+
                        "`amount` int(11) NOT NULL"+
                        "`timestamp` timestamp NOT NULL"+
                        "PRIMARY KEY (`id`)"+
                        ") ENGINE=MyISAM DEFAULT CHARSET=latin1;"
                 );
        this.exec(      "CREATE TABLE IF NOT EXISTS `bidder` ("+
                        "`id` int(11) NOT NULL,"+
                        "`name` varchar(16) NOT NULL,"+
                        "`type` tinyint(1) NOT NULL COMMENT 'is ServerBidder?',"+
                        "`notify` int(1) NOT NULL"+
                        "PRIMARY KEY (`id`)"+
                        ") ENGINE=MyISAM DEFAULT CHARSET=latin1;"
                 );
        this.exec(      "CREATE TABLE IF NOT EXISTS `bids` ("+
                        "`id` int(11) NOT NULL AUTO_INCREMENT,"+
                        "`auctionid` int(11) NOT NULL,"+
                        "`bidderid` int(11) NOT NULL,"+
                        "`amount` int(11) NOT NULL,"+
                        "`timestamp` timestamp NOT NULL"+
                        "PRIMARY KEY (`id`)"+
                        ") ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;"
                 );
        this.exec(       "CREATE TABLE IF NOT EXISTS `itemcontainer` ("+
                        "`id` int(11) NOT NULL AUTO_INCREMENT,"+
                        "`playerid` int(11) NOT NULL,"+
                        "`item` varchar(42) NOT NULL COMMENT 'ID:DATA Ench1:Val Ench2:Val ...',"+
                        "`amount` int(11) NOT NULL,"+
                        "`price` decimal(11,2) NOT NULL,"+
                        "`timestamp` timestamp NOT NULL"+
                        "`ownerid` int(11) NOT NULL COMMENT 'Bidder who started auction',"+
                        "PRIMARY KEY (`id`)"+
                        ") ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;"
                 );
        this.exec(      "CREATE TABLE IF NOT EXISTS `subscription` ("+
                        "`id` int(11) NOT NULL AUTO_INCREMENT,"+
                        "`playerid` int(11) NOT NULL,"+
                        "`auctionid` int(11) NOT NULL,"+
                        "`type` tinyint(1) NOT NULL,"+
                        "`item` varchar(42) NOT NULL 'ID:DATA Ench1:Val Ench2:Val ...',"+
                        "PRIMARY KEY (`id`)"+
                        ") ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;"
                 );
    }
    
    public Database(String user, String pass, String name)
    {
        this("localhost", (short)3306, user, pass, name);
    }

    public String getHost()
    {
        return this.host;
    }

    public short getPort()
    {
        return this.port;
    }

    public String getUser()
    {
        return this.user;
    }

    public String getPass()
    {
        return this.pass;
    }

    public String getName()
    {
        return this.name;
    }

    public ResultSet query(String query, Object... params)
    {
        try
        {
            PreparedStatement statement = this.connection.prepareStatement(query);
            for (int i = 1; i <= params.length; ++i)
            {
                statement.setObject(i, params[i]);
            }
            return statement.executeQuery();
        }
        catch (SQLException e)
        {
            throw new IllegalStateException("Failed to execute a query!", e);
        }
    }

    public int exec(String query, Object... params)
    {
        try
        {
            PreparedStatement statement = this.connection.prepareStatement(query);
            for (int i = 0; i < params.length; ++i)
            {
                statement.setObject(i, params[i]);
            }
            return statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new IllegalStateException("Failed to execute a query!", e);
        }
    }
    
    public void newAuction(Auction auction)
    {

        
        //register auction &| Bidder
        this.query(  
                    "INSERT INTO `%s`.`auctions` ("+
                    "`id` ,"+
                    "`ownerid` ,"+
                    "`item` ,"+
                    "`amount`"+
                    "`timestamp`"+
                    ")"+
                    "VALUES ("+
                    "'%d', '%d', '%s', '%d', '%d'"
                  ,"playground",auction.id,this.getBidderID(auction.owner),this.convertItem(auction.item),auction.item.getAmount(),auction.auctionEnd);
        //register startBid
        this.newBid(auction.bids.peek(),auction.id);
    }
    
    public void newBid(Bid bid, int id)
    {
        //register Bid
        this.query(
                    "INSERT INTO `%s`.`bids` ("+
                    "`id` ,"+
                    "`auctionid` ,"+
                    "`bidderid` ,"+
                    "`amount` ,"+
                    "`timestamp`"+
                    ")"+
                    "VALUES ("+
                    "NULL , '%d', '%d', '%d', '%d'"+
                    ");"
                  ,"playground",id,this.getBidderID(bid.getBidder()),bid.getAmount(),bid.getTimestamp()); 
    }
    
    public void remAuction(Auction auction)
    {
        //TODO search & destroy XD
    }
    
    public void remBid(int id)
    {
        //TODO search & destroy XD
    }
    
    
    public void addIdSub(Bidder bidder,int id)
    {
        this.query(
                    "INSERT INTO `%s`.`subscription` ("+
                    "`id` ,"+
                    "`playerid` ,"+
                    "`auctionid` ,"+
                    "`type` ,"+
                    "`item`"+
                    ")"+
                    "VALUES ("+
                    "NULL , '%d', '%d', '0', 'NULL'"+
                    ");"
                  ,"playground",this.getBidderID(bidder),id);
    }
    
    public void remIdSub()
    {
        //TODO search & destroy XD
    }

    public void addMatSub(Bidder bidder,ItemStack item)
    {
        
        this.query(
                    "INSERT INTO `%s`.`subscription` ("+
                    "`id` ,"+
                    "`playerid` ,"+
                    "`auctionid` ,"+
                    "`type` ,"+
                    "`item`"+
                    ")"+
                    "VALUES ("+
                    "NULL , '%d', 'NULL', '1', '%s'"+
                    ");"
                  ,"playground",this.getBidderID(bidder),this.convertItem(item)); 
    }
    
    public void remMatSub()
    {
        //TODO search & destroy XD
    }
    
    public void addItemContainer(Auction auction)
    {
        this.query(
                    "INSERT INTO `%s`.`itemcontainer` ("+
                    "`id` ,"+
                    "`playerid` ,"+
                    "`item` ,"+
                    "`amount` ,"+
                    "`price` ,"+
                    "`timestamp` ,"+
                    "`ownerid`"+
                    ")"+
                    "VALUES ("+
                    "NULL , '%d', '%s', '%d', '%f',"+
                    "'%d' , '%d'"+
                    ");"
                  ,"playground",this.getBidderID(auction.bids.peek().getBidder()),this.convertItem(auction.item),
                  auction.item.getAmount(),auction.bids.peek().getAmount(),System.currentTimeMillis(),this.getBidderID(auction.owner));
    }
    
    public void remItemContainer()
    {
        //TODO search & destroy XD
    }
    
    public void updateItemContainer()
    {
        
    }
            
    
    private int getBidderID(Bidder bidder)
    {
        //TODO getBidderID
        //TODO oder Bidder neu anlegen und zurückgeben
        return 0;
    }
    
    private String convertItem(ItemStack item)
    {
        String out = item.getTypeId()+":"+item.getDurability();
        if (!item.getEnchantments().isEmpty())
        {
            for (Enchantment ench : item.getEnchantments().keySet())
            {
                out += " "+ench.getId()+":"+item.getEnchantmentLevel(ench);
            }
        }
        return out;
    }        
    
    public void loadDatabase()
    {
        //TODO serverstart...
        AuctionHouse.log("Database loaded succesfully");
    }
}
