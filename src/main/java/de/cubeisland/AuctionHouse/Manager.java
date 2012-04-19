package de.cubeisland.AuctionHouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import org.bukkit.inventory.ItemStack;

/**
 * Manages all Auctions
 *
 * @author Faithcaio
 */
public class Manager
{
    private static Manager instance = null;
    private final List<Auction> auctions;
    public final Stack<Integer> freeIds;
    private static final AuctionHouse plugin = AuctionHouse.getInstance();
    private static final AuctionHouseConfiguration config = plugin.getConfigurations();
    public HashMap<Bidder, Bidder> remBidderConfirm = new HashMap();
    public HashSet<Bidder> remAllConfirm = new HashSet();
    public HashMap<Bidder, Integer> remSingleConfirm = new HashMap();

    private Manager()
    {
        int maxAuctions = config.auction_maxAuctions_overall;
        if (!(maxAuctions > 0))
        {
            maxAuctions = 1;
        }
        this.auctions = new ArrayList<Auction>();
        this.freeIds = new Stack<Integer>();
        for (int i = maxAuctions; i > 0; --i)
        {
            this.freeIds.push(i);
        }
    }

    public static Manager getInstance()
    {
        if (instance == null)
        {
            instance = new Manager();
        }
        return instance;
    }

    public Auction getAuction(int id) //Get Auction with ID
    {
        Auction auction = null;
        int size = this.auctions.size();
        for (int i = 0; i < size; i++)
        {
            if (this.auctions.get(i).getId() == id)
            {
                auction = this.auctions.get(i);
            }
        }
        return auction;
    }

    public List<Auction> getAuctions()
    {
        return auctions;
    }

    public Auction getIndexAuction(int index)
    {
        return auctions.get(index);
    }

    public boolean isEmpty()
    {
        return freeIds.isEmpty();
    }

    public int size()
    {
        return auctions.size();
    }

    public List<Auction> getAuctionItems(ItemStack material) //Get all Auctions with material
    {
        ArrayList<Auction> auctionlist = new ArrayList<Auction>();
        int size = this.auctions.size();
        for (int i = 0; i < size; i++)
        {
            if (this.auctions.get(i) == null)
            {
                return null;
            }
            if ((this.auctions.get(i).getItem().getType() == material.getType()
                && (this.auctions.get(i).getItem().getDurability() == material.getDurability())))
            {
                auctionlist.add(this.auctions.get(i));
            }
        }
        return auctionlist;
    }

    public List<Auction> getEndingAuctions() //Get soon Ending Auctions
    {
        ArrayList<Auction> auctionlist = new ArrayList<Auction>()
        {
        };
        int size = this.auctions.size();
        for (int i = 0; i < size; i++)
        {
            auctionlist.add(this.auctions.get(i));
        }
        return AuctionSort.sortAuction(auctionlist, "date");
    }

    public boolean cancelAuction(Auction auction)
    {
        this.freeIds.push(auction.getId());
        if (!(auction.getOwner() instanceof ServerBidder))
        {
            Bidder.getInstance(auction.getOwner().getPlayer()).removeAuction(auction);
            while (!(auction.getBids().isEmpty()))
            {
                Bidder.getInstance(auction.getBids().peek().getBidder().getPlayer()).removeAuction(auction);
                auction.getBids().pop();
            }
            auction.getOwner().getContainer().addItem(auction);
        }
        else
        {
            ServerBidder.getInstance().removeAuction(auction);
        }
        Database data = AuctionHouse.getInstance().database;
        data.exec("DELETE FROM `auctions` WHERE `id`=?", auction.getId());

        this.auctions.remove(auction);
        return true;
    }

    public boolean finishAuction(Auction auction)
    {
        this.freeIds.push(auction.getId());
        if (!(auction.getOwner() instanceof ServerBidder))
        {
            Bidder.getInstance(auction.getOwner().getPlayer()).removeAuction(auction);
            while (!(auction.getBids().isEmpty()))
            {
                Bidder.getInstance(auction.getBids().peek().getBidder().getPlayer()).removeAuction(auction);
                auction.getBids().pop();
            }
        }
        else
        {
            ServerBidder.getInstance().removeAuction(auction);
        }

        Database data = AuctionHouse.getInstance().database;
        data.exec("DELETE FROM `auctions` WHERE `id`=?", auction.getId());

        this.auctions.remove(auction);
        return true;
    }

    public boolean addAuction(Auction auction)
    {
        this.auctions.add(auction);
        return true;
    }
}