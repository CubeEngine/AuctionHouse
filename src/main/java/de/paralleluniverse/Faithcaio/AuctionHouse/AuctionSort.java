package de.paralleluniverse.Faithcaio.AuctionHouse;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Faithcaio
 */
public class AuctionSort
{
    Comparator compareId;
    Comparator comparePrice;
    Comparator compareDate;
    Comparator compareQuantity;

    public AuctionSort()
    {
        compareId = new Comparator()
        {
            public int compare(Object a1, Object a2)
            {
                if (((Auction) a2).id <= ((Auction) a1).id)
                {
                    return 1;
                }
                //else
                return -1;
            }
        };
        comparePrice = new Comparator()
        {
            public int compare(Object a1, Object a2)
            {
                if (((Auction) a2).bids.peek().getAmount() <= ((Auction) a1).bids.peek().getAmount())
                {
                    return 1;
                }
                //else
                return -1;
            }
        };
        compareDate = new Comparator()
        {
            public int compare(Object a1, Object a2)
            {
                if (((Auction) a2).auctionEnd <= ((Auction) a1).auctionEnd)
                {
                    return 1;
                }
                //else
                return -1;
            }
        };
        compareQuantity = new Comparator()
        {
            public int compare(Object a1, Object a2)
            {
                if (((Auction) a1).item.getAmount() <= ((Auction) a2).item.getAmount())
                {
                    return 1;
                }
                //else
                return -1;
            }
        };
    }

    public List<Auction> SortAuction(List<Auction> auctionlist, String type)
    {
        if (type.equalsIgnoreCase("id"))
        {
            Collections.sort(auctionlist, compareId);
        }
        if (type.equalsIgnoreCase("price"))
        {
            Collections.sort(auctionlist, comparePrice);
        }
        if (type.equalsIgnoreCase("date"))
        {
            Collections.sort(auctionlist, compareDate);
        }
        if (type.equalsIgnoreCase("quantity"))
        {
            Collections.sort(auctionlist, compareQuantity);
        }
        return auctionlist;
    }

    public List<Auction> SortAuction(List<Auction> auctionlist, String type, int quantity)
    {
        this.SortAuction(auctionlist, type);

        if (type.equalsIgnoreCase("quantity"))
        {
            if (auctionlist.isEmpty())
            {
                return null;
            }
            while (auctionlist.get(auctionlist.size() - 1).item.getAmount() < quantity)
            {
                AuctionHouse.debug("removing low quantity remain:" + auctionlist.size());
                auctionlist.remove(auctionlist.size() - 1);
                if (auctionlist.isEmpty())
                {
                    return null;
                }
            }
        }
        return auctionlist;
    }
}