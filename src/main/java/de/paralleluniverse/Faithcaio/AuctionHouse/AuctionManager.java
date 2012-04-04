package de.paralleluniverse.Faithcaio.AuctionHouse;

import java.util.*;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

/**
 * Manages all Auctions
 *
 * @author Faithcaio
 */
public class AuctionManager
{
    private static AuctionManager instance = null;
    private final List<Auction> auctions;
    private final Stack<Integer> freeIds;
    private static final AuctionHouse plugin = AuctionHouse.getInstance();
    private static final AuctionHouseConfiguration config = plugin.getConfigurations();
    public HashMap<CommandSender, Bidder> remBidderConfirm = new HashMap();
    public HashSet<CommandSender> remAllConfirm = new HashSet();

    private AuctionManager()
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

    public static AuctionManager getInstance()
    {
        if (instance == null)
        {
            instance = new AuctionManager();
        }
        return instance;
    }

    public Auction getAuction(int id) //Get Auction with ID
    {
        Auction auction = null;
        int size = this.auctions.size();
        for (int i = 0; i < size; i++)
        {
            if (this.auctions.get(i).id == id)
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
            if ((this.auctions.get(i).item.getType() == material.getType()
                    && (this.auctions.get(i).item.getDurability() == material.getDurability())))
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
        Collections.sort(auctionlist,
                new Comparator()
                {
                    public int compare(Object a1, Object a2)
                    {
                        if (((Auction) a1).auctionEnd >= ((Auction) a2).auctionEnd)
                        {
                            return 1;
                        }
                        //else
                        return -1;
                    }
                });
        return auctionlist;
    }

    public boolean cancelAuction(Auction auction)
    {
        this.freeIds.push(auction.id);
        if (!(auction.owner instanceof ServerBidder))
        {
            Bidder.getInstance(auction.owner.getPlayer()).removeAuction(auction);
            while (!(auction.bids.isEmpty()))
            {
                Bidder.getInstance(auction.bids.peek().getBidder().getPlayer()).removeAuction(auction);
                auction.bids.pop();
            }
            auction.owner.getContainer().addItem(auction);
        }
        else
        {
            ServerBidder.getInstance().removeAuction(auction);
        }
        this.auctions.remove(auction);
        auction.abortAuction();
        return true;
    }

    public boolean finishAuction(Auction auction)
    {
        this.freeIds.push(auction.id);
        if (!(auction.owner instanceof ServerBidder))
        {
            Bidder.getInstance(auction.owner.getPlayer()).removeAuction(auction);
            while (!(auction.bids.isEmpty()))
            {
                Bidder.getInstance(auction.bids.peek().getBidder().getPlayer()).removeAuction(auction);
                auction.bids.pop();
            }
        }
        else
        {
            ServerBidder.getInstance().removeAuction(auction);
        }
        this.auctions.remove(auction);
        return true;
    }

    public boolean addAuction(Auction auction)
    {
        auction.id = this.freeIds.peek();
        this.freeIds.pop();
        this.auctions.add(auction);
        return true;
    }
}