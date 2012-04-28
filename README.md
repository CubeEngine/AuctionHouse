AuctionHouse
==========

Description:
------------
Create auctions bid on them and get your lovely items!

This plugin provides an AuctionHouse for your economy server. (Vault needed!)

AuctionHouse is simple:
You only need one command to bid on auctions! Everything else an be done by simply clicking on the AuctionHouse signs!
But if you want you can enable the other chat commands such as: 
Adding auctions, removing auctions, undo bids, search for specified items, get infos about your auctions and much more!
As Admin you can also start auctions as server so you can cheat in items if you want.
AuctionHouse also provides a system to punish people who bid more money than they have. By default 20% of their bid.
When an auction ends the item gets into the AuctionBox which is a virtual container for all your Items won.
Keep in mind items get deleted after 2 days (by default) in the Box.
If you don't want some items to be sold you can simply add them to the blacklist.
AuctionHouse also have language support in english and german. (I could do french too if requested)

You can enable or disable every single command with the permissions.
For example you could give only the "auctionhouse.use", "auctionhouse.command.bid" and "auctionhouse.sign.use"
to a user who is only allowed to bid on auction and use signs to start/search auctions and receive Items.
"auctionhouse.user.command" allows general usage of all normal commands and signs!
"auctionhouse.mod" grants permission to remove auction of other users and to create new AuctionHouse signs.
Finally "auctionhouse.admin" gives all permissions for every command such as removing all active auctions on your server.

AuctionHouse is my first plugin. If you have found any bugs, or have got any suggestions, please feel free to create a ticket or contact me on IRC!

Features:
---------

Create auctions with different length and startbid.
Add your item in hand or specify the item!
Enchantments do work too!
Add multiple auctions at once.
Add auctions as server.
Bid on auctions.
Undo your bid after configurable time.
Abort your own auctions or even all auctions.
Subscribe to auctions and materials to get notified.
Get your items from everywhere or use signs.
And much more...

planned features:

-  auctionbox as chest with separate inventory
-  flatfile for starting planned serverauctions

Commands:
------------

- /ah help : Shows available commands
- /ah add : Adds an auction
- /ah remove : Removes an auction
- /ah bid : Bids on an auction
- /ah confirm : Confirms a request
- /ah list : Lists all auctions
- /ah search : Search for auctions
- /ah info : Information about auctions
- /ah getItems : Receive Items from won or aborted auctions
- /ah notify : Changes notification
- /ah subscribe : Subscribes to auctions or material
- /ah unsubscribe : Unsubscribes auctions or material
- /ah undoBid : Undo bids
- /ah reload : reload the plugin

Signs:
------------

- 1st Line: [AuctionHouse] or [ah]
- AuctionBox Sign:
    - 2nd Line: AuctionBox
- StartAuction Sign:
    - 2nd Line: Start
    - 3rd Line: <auctionlength\>
    - 4th Line: [startbid]
- ListAuctions Sign:
    - 2nd Line: List or AuctionSearch
    - 3rd Line: [ItemFilter] or All

Creating AuctionHouse signs is NOT case sensitive!
The sign will adjust itself!
To use the sign simply rightclick on it.
The Start sign will start an auction with your item in hand
To destroy AuctionHouse signs you have to sneak!

Permissions:
------------
Grouped permissions:
- auctionhouse.*: Can do everything (OPs have this by default)
- auctionhouse.admin: Same
- auctionhouse.user: General use of the plugin. Can not start auctions / receive items with commands
- auctionhouse.user.command: All normal commands
- auctionhouse.mod: delete auctions from other player / create AuctionHouse signs
- auctionhouse.use 

All permissions:
- auctionhouse.command.* All Commands
    - auctionhouse.command.add
    - auctionhouse.command.add.server
    - auctionhouse.command.add.cheatItems
    - auctionhouse.command.add.multi
    - auctionhouse.command.add.nolomit
    - auctionhouse.command.sub
    - auctionhouse.command.bid
    - auctionhouse.command.bid.infinite
    - auctionhouse.command.undobid
    - auctionhouse.command.delete.all Can delete ALL auctions
        - auctionhouse.command.delete.player
        - auctionhouse.command.delete.id
        - auctionhouse.command.delete.player.other
        - auctionhouse.command.delete.server
    - auctionhouse.command.info.*
        - auctionhouse.command.info
        - auctionhouse.command.info.others
    - auctionhouse.command.notify
    - auctionhouse.command.search
    - auctionhouse.command.getItems
- auctionhouse.sign.* Use & Create Signs
    - auctionhouse.sign.use Use signs
        - auctionhouse.sign.auctionbox
        - auctionhouse.sign.start
        - auctionhouse.sign.list
    - auctionhouse.sign.create.* Create signs
        - auctionhouse.sign.create.box
        - auctionhouse.sign.create.add
        - auctionhouse.sign.create.list

***README***
============

Plugin developed by Faithcaio - [Cube Island](http://cubeisland.de)


***[Talk to the developers](http://webchat.esper.net/?channels=cubeisland-dev&nick=)*** (#cubeisland-dev on EsperNet)

***[Source on Github](https://github.com/CubeIsland/AuctionHouse)***