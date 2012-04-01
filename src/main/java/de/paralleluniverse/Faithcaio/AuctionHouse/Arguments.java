package de.paralleluniverse.Faithcaio.AuctionHouse;

import de.paralleluniverse.Faithcaio.AuctionHouse.AuctionHouse;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 *
 * @author Faithcaio
 */
public class Arguments {
    private Map<String,String> arguments;
    
    public Arguments(String[] args)
    {
       arguments = new HashMap<String,String>();
       int arg = args.length;
       int j=1;
       for (int i=0;i<arg;++i)
       {
           int parambreak = args[i].indexOf(":");
           if (parambreak == -1)
           {
               this.arguments.put(String.valueOf(j), args[i]);
               j++;
           }
           else
               this.arguments.put(args[i].substring(0, parambreak),args[i].substring(parambreak+1) );
       }
    }
    
    public String getString(String name)
    {
        return arguments.get(name);
    }
    
    public Integer getInt(String name)
    {
        try
        {
            return Integer.parseInt(this.getString(name));    
        }
        catch (NumberFormatException ex)
        {
            return null;
        }
    }
    
    public Double getDouble(String name)
    {
        try
        {
            return Double.parseDouble(this.getString(name));    
        }
        catch (NumberFormatException ex)
        {
            return null;
        }
    }
   
    public Player getPlayer(String name)
    {
        return AuctionHouse.getInstance().getServer().getPlayer(this.getString(name));
    }
    
    public Bidder getBidder(String name)
    {
        return Bidder.getInstance(AuctionHouse.getInstance().getServer().getOfflinePlayer(this.getString(name)));
    }
    
    public Material getMaterial(String name)
    {
        return Material.matchMaterial(this.getString(name));
    }


    
}
