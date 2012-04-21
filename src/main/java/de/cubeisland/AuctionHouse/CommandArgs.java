package de.cubeisland.AuctionHouse;

import de.cubeisland.AuctionHouse.Auction.Bidder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Phillip Schichtel
 */
public class CommandArgs
{
    private final String label;
    private final List<String> flags;
    private final Map<String, String> params;
    private final boolean empty;
    private final int size;

    public CommandArgs(String[] args)
    {
        this.flags = new ArrayList<String>();
        this.params = new HashMap<String, String>();

        if (args.length > 0)
        {
            this.label = args[0];
            String name;
            for (int i = 1; i < args.length; ++i)
            {
                if (args[i].charAt(0) == '-')
                {
                    name = args[i].substring(1);
                    if (i + 1 < args.length)
                    {
                        this.params.put(name, args[++i]);
                    }
                    else
                    {
                        this.flags.add(name);
                    }
                }
                else
                {
                    this.flags.add(args[i]);
                }
            }
        }
        else
        {
            throw new IllegalArgumentException("There need to be at least 1 argument!");
        }
        this.empty = (this.flags.isEmpty() && this.params.isEmpty());
        this.size = this.flags.size() + this.params.size();
    }

    public boolean isEmpty()
    {
        return this.empty;
    }

    public int size()
    {
        return this.size;
    }

    public String getLabel()
    {
        return this.label;
    }

    public boolean hasFlag(String flag)
    {
        return this.flags.contains(flag);
    }

    public boolean hasParam(String param)
    {
        return this.params.containsKey(param);
    }

    public String getString(int i)
    {
        try { return this.flags.get(i); }
        catch (IndexOutOfBoundsException ex) {return null;}
    }

    public String getString(String param)
    {
        return params.get(param);
    }

    public Integer getInt(int flag)
    {
        return this.getInt(flag, null);
    }

    public Integer getInt(int flag, Integer def)
    {
        try
        {
            return Integer.parseInt(this.getString(flag));
        }
        catch (NumberFormatException e)
        {
            return def;
        }
    }

    public Integer getInt(String param)
    {
        return this.getInt(param, null);
    }

    public Integer getInt(String param, Integer def)
    {
        try
        {
            return Integer.parseInt(this.getString(param));
        }
        catch (NumberFormatException e)
        {
            return def;
        }
    }

    public Double getDouble(int flag)
    {
        return this.getDouble(flag, null);
    }

    public Double getDouble(int flag, Double def)
    {
        try
        {
            return Double.parseDouble(this.getString(flag));
        }
        catch (NumberFormatException e)
        {
            return def;
        }
    }

    public Double getDouble(String param)
    {
        return this.getDouble(param, null);
    }

    public Double getDouble(String param, Double def)
    {
        try
        {
            return Double.parseDouble(this.getString(param));
        }
        catch (NumberFormatException e)
        {
            return def;
        }
    }

    public Player getPlayer(int flag)
    {
        return Bukkit.getPlayer(this.getString(flag));
    }

    public Player getPlayer(String param)
    {
        return Bukkit.getPlayer(this.getString(param));
    }

    public Bidder getBidder(int flag)
    {
        return Bidder.getInstanceNoCreate(Bukkit.getOfflinePlayer(this.getString(flag)));
    }

    public Bidder getBidder(String param)
    {
        return Bidder.getInstanceNoCreate(Bukkit.getOfflinePlayer(this.getString(param)));
    }

    public ItemStack getItem(int flag)
    {
        return this.getItem(flag, null);
    }

    public ItemStack getItem(int flag, ItemStack def)
    {
        ItemStack stack = convertToItemStack(this.getString(flag));
        if (stack != null)
        {
            return stack;
        }
        return def;
    }

    public ItemStack getItem(String param)
    {
        return this.getItem(param, null);
    }

    public ItemStack getItem(String param, ItemStack def)
    {
        ItemStack stack = convertToItemStack(this.getString(param));
        if (stack != null)
        {
            return stack;
        }
        return def;
    }

    private static ItemStack convertToItemStack(String name)
    {
        if (name == null)
        {
            return null;
        }

        Material material;
        short data = 0;
        int colonOffset = name.indexOf(":");

        try
        {
            if (colonOffset > 0)
            {
                data = Short.parseShort(name.substring(colonOffset + 1));
                name = name.substring(0, colonOffset);
            }

            material = Material.matchMaterial(name);
            if (material != null)
            {
                return new ItemStack(material, 1, data);
            }
        }
        catch (NumberFormatException e)
        {}

        return null;
    }
}
