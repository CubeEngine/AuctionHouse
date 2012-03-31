package de.paralleluniverse.Faithcaio.AuctionHouse;

import java.util.HashMap;
import java.util.Map;

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
               this.arguments.put(args[i].substring(0, parambreak - 1),args[i].substring(parambreak+1) );
       }
    }
    
    public String getParam(String name)
    {
        return arguments.get(name);
    }

    public boolean addParam(String name,String param)
    {
        arguments.put(name,param);
        return true;
    }
}
