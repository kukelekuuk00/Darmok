#Darmok

Darmok is an *extremely* light-weight chat channel management and filtering plugin. Designed to address the needs of both large and small servers; it offers fully configurable channels, a better profanity filtering system (optional), additional permissions to better control channel access/use/management, and a lot more.

Darmok was designed to feel very similar to existing chat plugins, but it packs a lot more "bang for the buck".

## Features

- No database required
- Define any number of channels - with configurable colors, message formats, etc
- Per-channel permissions for read and speak let you control who sees and who can talk to a channel.
- Permissions to allow players to auto-join, or have channels automatically set as default
- Abides by the Essentials /mute command
- Towny integration (optional). Duplicates the town chat or nation chat commands
- Players may join/leave a channel if the channel permissions allow it
- Moderators may force players into a specific channel
- Moderators may kick players from a channel
- Moderators may ban/unban players from a specific channel
- Smart profanity filter (optional) - catches most "leet-speak", ignores characters meant to bypass, etc
- Caps-lock filter (optional) - Limit capital letters in a sentence to a percentage, with a minimum string length
- Permission node to use color/text codes in chat
- List available channels, or channels you're currently in


## Install

Move the darmok.jar file to your plugins folder.

**Vault necessary if you wish to have permissions prefix/suffixes in chat messages.**

Darmok comes with default channels you're likely familiar with.

Modify the configuration if you wish, but it comes with sensible defaults.

## Permissions

- `darmok.channel.CHANNEL_NAME_HERE.read` - Messages to this channel can be seen by the player
- `darmok.channel.CHANNEL_NAME_HERE.speak` - Player may send messages to this channel
- `darmok.channel.CHANNEL_NAME_HERE.autojoin` - Players will auto-join this channel unless they have settings otherwise.
- `darmok.list` - List all available channels, or list channels you're subscribed to
- `darmok.chatcolor` - Use chat color codes or text formatting codes in messages
- `darmok.channel.CHANNEL_NAME_HERE.kick` - Permission to kick players from any channel
- `darmok.channel.CHANNEL_NAME_HERE.ban` - Permission to ban players from any channel
- `darmok.mod` - Gives force/kick/ban capabilities for every channel


## Commands

- `/(channel alias)` - Join channel and make it the default. Example: `/g` to set global as the default.
- `/(channel alias) (msg)` - Send a message to the channel no matter which channel you're in. Example: `/g hi there`
- `/ch leave (channel)` - Leave a channel - you will no longer receive messages for this channel
- `/ch join (channel)` - Join a channel
- `/ch force (player) (channel)` - Force a player to switch to another channel. For those noobs who don't get what local is for ;)
- `/ch kick (player) (channel)` - Kicks a player from a channel, but they may return.
- `/ch ban (player) (channel)` - Bans a player from a channel. They may not return


## Links

[File-A-Bug](https://snowy-evening.com/botsko/darmok )

[Source](https://github.com/prism/Darmok )



## Get Help

IRC: irc.esper.net #prism


## License 

Attribution-NonCommercial-ShareAlike 3.0 United States

http://creativecommons.org/licenses/by-nc-sa/3.0/us/

## Credits

Designed to solve specific needs for our Minecraft server, DHMC.

- viveleroi (*Creator, Lead Dev*)
- nasonfish, Ollie2000, YeaItsMe (*Alpha Testers*)
- Everyone on DHMC in March 2013 and after
- [Metrics](http://mcstats.org) class Copyright 2013 Tyler Blair. All rights reserved.


## Donate to Vive


[![alt text][2]][1]

  [1]: https://www.paypal.com/cgi-bin/webscr?return=http%3A%2F%2Fdev.bukkit.org%2Fserver-mods%2Fprism%2F&cn=Add+special+instructions+to+the+addon+author%28s%29&business=botsko%40gmail.com&bn=PP-DonationsBF%3Abtn_donateCC_LG.gif%3ANonHosted&cancel_return=http%3A%2F%2Fdev.bukkit.org%2Fserver-mods%2Fprism%2F&lc=US&item_name=Prism+%28from+Bukkit.org%29&cmd=_donations&rm=1&no_shipping=1&currency_code=USD
  [2]: http://botsko.s3.amazonaws.com/paypal_donate.gif

I'm viveleroi, author of Darmok and other plugins like Prism, Craftys, DarkMythos, InventoryToolkit, and more. There's no pay in making plugins but it's rewarding knowing you all use them - so please help bridge the gap and donate to cover my own time and money investment.

So please, *make a donation and make it easier for me to continue with these amazing plugins*.