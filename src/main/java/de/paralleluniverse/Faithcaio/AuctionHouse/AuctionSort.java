package de.paralleluniverse.Faithcaio.AuctionHouse;

import de.paralleluniverse.Faithcaio.AuctionHouse.Auction;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Faithcaio
 */
public class AuctionSort {

    Comparator compareId;
    Comparator comparePrice;
    Comparator compareDate;
    
    public AuctionSort()
    {
        compareId = new Comparator()
            {
                public int compare(Object a1,Object a2)
                {
                if (((Auction)a2).id <= ((Auction)a1).id) return 1;
                //else
                return -1;
                }
            };
        comparePrice = new Comparator()
            {
                public int compare(Object a1,Object a2)
                {
                if (((Auction)a2).bids.peek().getAmount() <= ((Auction)a1).bids.peek().getAmount()) return 1;
                //else
                return -1;
                }
            };
        compareDate = new Comparator()
            {
                public int compare(Object a1,Object a2)
                {
                if (((Auction)a2).auctionEnd <= ((Auction)a1).auctionEnd) return 1;
                //else
                return -1;
                }
            };
    }
    
    public List<Auction> SortAuction (List<Auction> auctionlist,String type)
    {
        if (type.equalsIgnoreCase("id"))   
            Collections.sort(auctionlist,compareId);
        if (type.equalsIgnoreCase("price"))   
            Collections.sort(auctionlist,comparePrice);
        if (type.equalsIgnoreCase("date"))   
            Collections.sort(auctionlist,compareDate);
        return auctionlist;    
    }
}
