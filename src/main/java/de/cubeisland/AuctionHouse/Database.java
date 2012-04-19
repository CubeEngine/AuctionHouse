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
                        "`amount` int(11) NOT NULL,"+
                        "`timestamp` timestamp NOT NULL,"+
                        "PRIMARY KEY (`id`)"+
                        ") ENGINE=MyISAM DEFAULT CHARSET=latin1;"
                 );
        this.exec(      "CREATE TABLE IF NOT EXISTS `bidder` ("+
                        "`id` int(11) NOT NULL AUTO_INCREMENT,"+
                        "`name` varchar(16) NOT NULL,"+
                        "`type` tinyint(1) NOT NULL COMMENT 'is ServerBidder?',"+
                        "`notify` int(1) NOT NULL,"+
                        "PRIMARY KEY (`id`)"+
                        ") ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;"
                 );
        this.exec(      "CREATE TABLE IF NOT EXISTS `bids` ("+
                        "`id` int(11) NOT NULL AUTO_INCREMENT,"+
                        "`auctionid` int(11) NOT NULL,"+
                        "`bidderid` int(11) NOT NULL,"+
                        "`amount` int(11) NOT NULL,"+
                        "`timestamp` timestamp NOT NULL,"+
                        "PRIMARY KEY (`id`)"+
                        ") ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;"
                 );
        this.exec(       "CREATE TABLE IF NOT EXISTS `itemcontainer` ("+
                        "`id` int(11) NOT NULL AUTO_INCREMENT,"+
                        "`bidderid` int(11) NOT NULL,"+
                        "`item` varchar(42) NOT NULL COMMENT 'ID:DATA Ench1:Val Ench2:Val ...',"+
                        "`amount` int(11) NOT NULL,"+
                        "`price` decimal(11,2) NOT NULL,"+
                        "`timestamp` timestamp NOT NULL,"+
                        "`ownerid` int(11) NOT NULL COMMENT 'Bidder who started auction',"+
                        "PRIMARY KEY (`id`)"+
                        ") ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;"
                 );
        this.exec(      "CREATE TABLE IF NOT EXISTS `subscription` ("+
                        "`id` int(11) NOT NULL AUTO_INCREMENT,"+
                        "`bidderid` int(11) NOT NULL,"+
                        "`auctionid` int(11) DEFAULT NULL,"+
                        "`type` tinyint(1) NOT NULL,"+
                        "`item` varchar(42) DEFAULT NULL COMMENT 'ID:DATA Ench1:Val Ench2:Val ...',"+
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

    public int getPort()
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
            for (int i = 0; i < params.length; ++i)
            {
                statement.setObject(i+1, params[i]);
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
                statement.setObject(i+1, params[i]);
            }
            return statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new IllegalStateException("Failed to execute a query!", e);
        }
    }
    
    public void loadDatabase()
    {
        try{
        Database data = AuctionHouse.getInstance().database; 
        AuctionHouse.debug("Load DataBase...");

        ResultSet bidderset =
              data.query("SELECT * FROM `bidder`");
        while (bidderset.next())
        {
            //load in Bidder
            Bidder bidderer = Bidder.getInstance(bidderset.getInt("id"), bidderset.getString("name"));
            AuctionHouse.debug("Bidder loaded: "+bidderer.getId()+"|"+bidderer.getName());
            //bitmask
            //TODO notify in Database zu byte ändern
            bidderer.resetNotifyState(bidderset.getByte("notify"));
           
        }
        int max = AuctionHouse.getInstance().config.auction_maxAuctions_overall;
        for (int i=0; i<max; i++)
        {
            ResultSet set =
              data.query("SELECT * FROM `auctions` WHERE `id`=? LIMIT 1;",i);  
            if (set.next())
            {
                int id = set.getInt("id");
                ItemStack item = MyUtil.convertItem(set.getString("item"),set.getInt("amount"));
                Bidder owner = Bidder.getInstance(set.getInt("ownerid"),this.getBidderString(set.getInt("ownerid")));
                long auctionEnd = set.getTimestamp("timestamp").getTime();
                Auction newauction = new Auction (id,item,owner,auctionEnd);
                //load in auction
                AuctionHouse.debug("Auction loaded: "+newauction.getId()+"|O:"+newauction.getOwner().getName());
                Manager.getInstance().addAuction(newauction);
                ResultSet bidset =
                  data.query("SELECT * FROM `bids` WHERE `auctionid`=? ;",i);
                while (bidset.next())
                { 
                    //sort bids by time & fill auction with bids
                    data.query("SELECT * FROM `bids` ORDER BY `timestamp` ;");
                    AuctionHouse.debug("Bid loaded: "+newauction.getId()+"|C:"+bidset.getDouble("amount")+"|P:"+this.getBidderString(bidset.getInt("bidderid")));
                    //load in Bids
                    Bid bid = new Bid( bidset.getInt("id"),
                                     bidset.getInt("bidderid"),
                                     this.getBidderString(bidset.getInt("bidderid")),
                                     bidset.getDouble("amount"),
                                     bidset.getTimestamp("timestamp"));
                    Manager.getInstance().getAuction(newauction.getId()).getBids().push(bid);
                   
                }
            }
        }
        //load in Subs
        ResultSet subset =
              data.query("SELECT * FROM `subscription`;");
        while (subset.next())
        {
            Bidder bidder = Bidder.getInstance(subset.getInt("bidderid"),this.getBidderString(subset.getInt("bidderid")));
            if (subset.getInt("type")==1)
            {//IDSub
                
                bidder.addDataBaseSub(subset.getInt("auctionid"));
            }
            else
            {//MatSub
                bidder.addDataBaseSub(MyUtil.convertItem(subset.getString("item")));
            }
        }
        //load in ItemContainer
        ResultSet itemset =
              data.query("SELECT * from `itemcontainer` ORDER BY `timestamp`;");
        while (itemset.next())
        {
            Bidder bidder = Bidder.getInstance(itemset.getInt("bidderid"), this.getBidderString(itemset.getInt("bidderid")));
            bidder.getContainer().itemList.add(
                    new AuctionItem( bidder,
                    MyUtil.convertItem(itemset.getString("item"),itemset.getInt("amount")),
                    itemset.getTimestamp("timestamp"),
                    this.getBidderString(itemset.getInt("ownerid")),
                    itemset.getDouble("price"),
                    itemset.getInt("id")
                    ));
        }
        }   
        catch (SQLException ex){
        AuctionHouse.log("Error while loading DataBase!");
        }
        AuctionHouse.log("Database loaded succesfully");
    }
    
    private String getBidderString(int id)
    {
        try{
            Database data = AuctionHouse.getInstance().database;
            ResultSet set =
              data.query("SELECT * from `bidder` where `id`=? LIMIT 1;",id);  
            if (set.next())
              return set.getString("name");
        }   
        catch (SQLException ex){}
        return null;
    }
}
