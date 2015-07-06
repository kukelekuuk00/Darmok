#Darmok

Darmok is a light-weight chat channel management and filtering plugin for Sponge servers. Designed to address the needs of both large and small servers; it offers fully configurable channels, a profanity filtering system (optional), additional permissions to better control channel access/use/management, and more.

## Features

- No database required
- Define any number of channels - with configurable colors, message formats, etc
- Per-channel permissions for read and speak let you control who sees and who can talk to a channel.
- Permissions to allow players to auto-join, or have channels automatically set as default
- Players may join/leave a channel if the channel permissions allow it
- Range-based channels
- Moderators may force players into a specific channel
- Moderators may kick players from a channel
- Moderators may ban/unban players from a specific channel
- Smart profanity filter (optional) - catches "leet-speak", ignores characters meant to bypass, etc
- Caps-lock filter (optional) - Limit capital letters in a sentence to a percentage, with a minimum string length
- List available channels


## Install

Move the darmok.jar file to your mods folder.

Darmok comes with default channels you're likely familiar with.

Modify the configuration if you wish, but it comes with sensible defaults.

## Permissions

- `darmok.channel.CHANNEL_NAME_HERE.read` - Messages to this channel can be seen by the player
- `darmok.channel.CHANNEL_NAME_HERE.speak` - Player may send messages to this channel
- `darmok.channel.CHANNEL_NAME_HERE.leave` - Player may leave channel
- `darmok.channel.CHANNEL_NAME_HERE.autojoin` - Players will auto-join this channel unless they have settings otherwise.
- `darmok.list` - List all available channels, or list channels you're subscribed to
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

[Source](https://github.com/prism/Darmok )


## Get Help

IRC: irc.esper.net #helion3


## License 

See LICENSE.txt.

## Credits

Designed for DHMC.

- viveleroi (*Creator, Lead Dev*)
- nasonfish, Ollie2000, YeaItsMe (*Alpha Testers on 1.0*)
- Everyone on DHMC in March 2013 and after

## Donate to Vive

[![alt text][2]][1]

  [1]: https://www.paypal.com/cgi-bin/webscr?return=http%3A%2F%2Fdev.bukkit.org%2Fserver-mods%2Fprism%2F&cn=Add+special+instructions+to+the+addon+author%28s%29&business=botsko%40gmail.com&bn=PP-DonationsBF%3Abtn_donateCC_LG.gif%3ANonHosted&cancel_return=http%3A%2F%2Fdev.bukkit.org%2Fserver-mods%2Fprism%2F&lc=US&item_name=Prism+%28from+Bukkit.org%29&cmd=_donations&rm=1&no_shipping=1&currency_code=USD
  [2]: http://botsko.s3.amazonaws.com/paypal_donate.gif

I'm viveleroi, author of Darmok and other plugins like Prism, Craftys, DarkMythos, InventoryToolkit, and more. There's no pay in making plugins but it's rewarding knowing you all use them - so please help bridge the gap and donate to cover my own time and money investment.

So please, *make a donation and make it easier for me to continue with these amazing plugins*.