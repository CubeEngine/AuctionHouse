package de.cubeisland.AuctionHouse;
import java.sql.*;
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
                        "`bidderid` int(11) NOT NULL,"+
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
                        "`bidderid` int(11) NOT NULL,"+
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
    
    public void loadDatabase()
    {  //TODO serverstart... prüfen
        try{
        Database data = AuctionHouse.getInstance().database; 
        

        ResultSet bidderset =
              data.query("SELECT * from `bidder`");
        while (bidderset.next())
        {
            //load in Bidder
            Bidder.getInstance(bidderset.getInt("id"), bidderset.getString("bidder"));
        }
        
        
        int max = AuctionHouse.getInstance().config.auction_maxAuctions_overall;
        for (int i=0; i>max; i++)
        {
            ResultSet set =
              data.query("SELECT * from `auctions` where `id`=? LIMIT 1;",i);  
            
            if (set.next())
            {
                int id = set.getInt("id");
                ItemStack item = MyUtil.get().convertItem(set.getString("item"));
                //load in Bidder:
                Bidder owner = Bidder.getInstance(set.getInt("bidderid"),this.getBidderString(set.getInt("bidderid")));
                long auctionEnd = set.getTimestamp("timestamp").getTime();
                Auction newauction = new Auction (id,item,owner,auctionEnd);
                //load in auction
                Manager.getInstance().addAuction(newauction);
                ResultSet bidset =
                  data.query("SELECT * from `bids` where `auctionid`=? ;",i);
                while (bidset.next())
                { 
                    //sort bids by time & fill auction with bids
                    //TODO prüfen ob richtig sortiert
                    data.query("SELECT * from `bids` ORDER BY `timestamp` ;");
                    newauction.bids.push(
                            new Bid( bidset.getInt("id"),
                                     bidset.getInt("bidderid"),
                                     this.getBidderString(bidset.getInt("bidder")),
                                     bidset.getDouble("price"), newauction));
                }
            }
        }
        //load in ID-Subs
        //TODO prüfen ob richtig
        ResultSet subset =
              data.query("SELECT * from `subscriptions` ;");
        while (subset.next())
        {
            Bidder bidder = Bidder.getInstance(subset.getInt("bidderid"),this.getBidderString(subset.getInt("bidderid")));
            if (subset.getInt("type")==1)
            {//IDSub
                
                bidder.addDataBaseSub(subset.getInt("auctionid"));
            }
            else
            {//MatSub
                bidder.addDataBaseSub(MyUtil.get().convertItem(subset.getString("item")));
            }
        }
        //TODO
        //load in ItemContainer
        //TODO prüfen ob richtig sortiert
        data.query("SELECT * from `itemcontainer` ORDER BY `timestamp` ;");
        ResultSet itemset =
              data.query("SELECT * from `subscriptions` ;");
        while (itemset.next())
        {
            Bidder bidder = Bidder.getInstance(itemset.getInt("bidderid"), this.getBidderString(itemset.getInt("bidderid")));
            //TODO ItemContainer DataBaseFiller
        }
        }   
        catch (SQLException ex){}
        AuctionHouse.log("Database loaded succesfully");
    }
    
    private String getBidderString(int id)
    {
        try{
            Database data = AuctionHouse.getInstance().database;
            ResultSet set =
              data.query("SELECT * from `bidder` where `id`=? LIMIT 1;",id);  
            if (set.next())
              return set.getString("bidder");
        }   
        catch (SQLException ex){}
        return null;
    }
}
