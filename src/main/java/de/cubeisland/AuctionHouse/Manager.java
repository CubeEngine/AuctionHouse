package de.cubeisland.AuctionHouse;

import de.cubeisland.AuctionHouse.Auction.Auction;
import de.cubeisland.AuctionHouse.Auction.Bidder;
import de.cubeisland.AuctionHouse.Auction.ServerBidder;
import de.cubeisland.AuctionHouse.Database.Database;
import java.util.ArrayList;
import java.util.Collections;
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
    private final Stack<Integer> freeIds;
    private static final AuctionHouse plugin = AuctionHouse.getInstance();
    private static final AuctionHouseConfiguration config = plugin.getConfiguration();
    private HashMap<Bidder, Bidder> remBidderConfirm = new HashMap();
    private HashSet<Bidder> remAllConfirm = new HashSet();
    private HashMap<Bidder, Integer> remSingleConfirm = new HashMap();
    private final Database db;

    private Manager()
    {
        this.db = AuctionHouse.getInstance().getDB();
        int maxAuctions = config.auction_maxAuctions_overall;
        if (maxAuctions <= 0)
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

    public List<Auction> getAuctionItem(ItemStack material) //Get all Auctions with material
    {
        List<Auction> auctionlist = new ArrayList<Auction>();
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
    
    public List<Auction> getAuctionItem(ItemStack material, Bidder bidder) //Get all Auctions with material without bidder
    {
        List<Auction> auctionlist = this.getAuctionItem(material);
        for (Auction auction : bidder.getActiveBids())
        {
            if (auction.getOwner() == bidder)
            {
                auctionlist.remove(auction);
            }
        }
        return auctionlist;
    }

    public List<Auction> getEndingAuctions()
    {
        List<Auction> endingActions = new ArrayList<Auction>();
        int size = this.auctions.size();
        for (int i = 0; i < size; ++i)
        {
            endingActions.add(this.auctions.get(i));
        }
        AuctionSort.sortAuction(endingActions, "date");
        
        return endingActions;
    }

    public boolean cancelAuction(Auction auction, boolean win)
    {
        this.freeIds.push(auction.getId());
        Collections.sort(this.freeIds);
        Collections.reverse(this.freeIds);

        if (!(auction.getOwner() instanceof ServerBidder))
        {
            auction.getOwner().removeAuction(auction);
            while (!(auction.getBids().isEmpty()))
            {
                Bidder.getInstance(auction.getBids().peek().getBidder().getOffPlayer()).removeAuction(auction);
                auction.getBids().pop();
            }
            if (!win)
                auction.getOwner().getContainer().addItem(auction);
        }
        else
        {
            ServerBidder.getInstance().removeAuction(auction);
        }
        db.execUpdate("DELETE FROM `auctions` WHERE `id`=?", auction.getId());
        //clean up DataBase just in case
        db.execUpdate("DELETE FROM `subscription` WHERE `auctionid`=?", auction.getId());
        db.execUpdate("DELETE FROM `bids` WHERE `auctionid`=?", auction.getId());
        this.auctions.remove(auction);
        return true;
    }

    public boolean addAuction(Auction auction)
    {
        this.auctions.add(auction);
        return true;
    }
    
    public Stack<Integer> getFreeIds()
    {
        return this.freeIds;
    }
    
    public HashMap<Bidder, Bidder> getBidderConfirm()
    {
        return this.remBidderConfirm;
    }
    
    public HashSet<Bidder> getAllConfirm()
    {
        return this.remAllConfirm;
    }
    
    public HashMap<Bidder, Integer> getSingleConfirm()
    {
        return this.remSingleConfirm;
    }
    
    public void removeOldAuctions()
    {
        List<Auction> t_auctions = new ArrayList<Auction>(this.auctions);
        for (Auction auction : t_auctions)
        {
            if (auction.getAuctionEnd() < System.currentTimeMillis())
                this.cancelAuction(auction, false);
        }
    }
}