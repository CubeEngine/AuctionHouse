package de.cubeisland.AuctionHouse;

import de.cubeisland.AuctionHouse.Auction.Auction;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Faithcaio
 */
public class AuctionSort
{
    private static final Comparator compareId;
    private static final Comparator comparePrice;
    private static final Comparator compareDate;
    private static final Comparator compareQuantity;

    static
    {
        compareId = new Comparator()
        {
            public int compare(Object a1, Object a2)
            {
                if (((Auction) a2).getId() <= ((Auction) a1).getId())
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
                if (((Auction) a2).getBids().peek().getAmount() <= ((Auction) a1).getBids().peek().getAmount())
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
                if (((Auction) a2).getAuctionEnd() <= ((Auction) a1).getAuctionEnd())
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
                if (((Auction) a1).getItem().getAmount() <= ((Auction) a2).getItem().getAmount())
                {
                    return 1;
                }
                //else
                return -1;
            }
        };
    }

    public static void sortAuction(List<Auction> auctionlist, String type)
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
    }

    public static List<Auction> sortAuction(List<Auction> auctionlist, String type, int quantity)
    {
        sortAuction(auctionlist, type);

        if (type.equalsIgnoreCase("quantity"))
        {
            if (auctionlist.isEmpty())
            {
                return null;
            }
            while (auctionlist.get(auctionlist.size() - 1).getItem().getAmount() < quantity)
            {
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