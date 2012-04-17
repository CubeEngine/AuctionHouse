package de.paralleluniverse.Faithcaio.AuctionHouse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    }

    private void setupStructure()
    {
        // TODO implement me :)
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
}
