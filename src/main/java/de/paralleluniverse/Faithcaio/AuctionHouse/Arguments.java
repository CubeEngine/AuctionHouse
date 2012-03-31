package de.paralleluniverse.Faithcaio.AuctionHouse;

import java.util.HashMap;
import java.util.Map;
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
    
    public int getInt(String name)
    {
        int intArg = -1;
        try {intArg = Integer.parseInt(this.getString(name)); }
        catch (NumberFormatException ex) {return -1; }
        return intArg;
    }
   
    public Player getPlayer(String name)
    {
        
        Player player =  AuctionHouse.getInstance().server.getPlayer(arguments.get(name));
        return player;
    }
    
    public double getDouble(String name)
    {
        double dubArg = -1;
        try {dubArg = Double.parseDouble(this.getString(name)); }
        catch (NumberFormatException ex) {return -1; }
        return dubArg;
    }
    
}
