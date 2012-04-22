AuctionHouse
==========

Description:
------------
Implements an AuctionHouse for your Minecraft-Server

Features:
---------
Create auctions bid on them and get your lovely items!

planned features:

-  auctionbox as chest with separate inventory
-  average price for Items
-  flatfile for starting Serverauctions

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

Signs:
------------

- 1st Line: [AuctionHouse]
- AuctionBox Sign:
    - 2nd Line: AuctionBox
- StartAuction Sign:
    - 2nd Line: Start
    - 3rd Line: <auctionlength\>
    - 3th Line: [startbid]
- ListAuctions Sign:
    - 2nd Line: List or AuctionSearch
    - 3rd Line: [ItemFilter]

Permissions:
------------
OPs have the auctionhouse.* permission
- auctionhouse.*: Can do everything
- auctionhouse.admin: Same
- auctionhouse.user: General use of the plugin. Can not start auctions / receive items with commands
- auctionhouse.user.command: All normal commands
- auctionhouse.mod: delete auctions from other player / create AuctionHouse signs
- auctionhouse.use 

- auctionhouse.command.* All Commands
    - auctionhouse.command.add
    - auctionhouse.command.add.server
    - auctionhouse.command.add.cheatItems
    - auctionhouse.command.add.multi
    - auctionhouse.add.nolomit
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