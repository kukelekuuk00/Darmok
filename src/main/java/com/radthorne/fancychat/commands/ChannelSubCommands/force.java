package com.radthorne.fancychat.commands.ChannelSubCommands;

import com.radthorne.fancychat.CaselessArrayList;
import com.radthorne.fancychat.FancyChat;
import com.radthorne.fancychat.channels.Channel;
import com.radthorne.fancychat.channels.ChannelPermissions;
import com.radthorne.fancychat.commandlibs.SubCommand;
import com.radthorne.fancychat.exceptions.ChannelPermissionException;
import com.radthorne.fancychat.exceptions.NoConsoleException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/*
 * Copyright 2015 Luuk Jacobs

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class force implements SubCommand
{
    @Override
    public void console( ConsoleCommandSender sender, String label, String[] args ) throws NoConsoleException
    {
        throw new NoConsoleException();
    }

    @Override
    public void player( Player sender, String label, String[] args )
    {
        handle( sender, label, args );
    }

    public void handle( CommandSender sender, String label, String[] args )
    {

        if ( args.length < 2 )
        {
            sender.sendMessage( FancyChat.messenger.playerError( "You must provide a player name and channel, like /ch force g" ) );
            return;
        }

        // Get the player
        Player player = Bukkit.getServer().getPlayer( args[1] );
        if ( player == null )
        {
            sender.sendMessage( FancyChat.messenger.playerError( "Can't find a player with that name." ) );
            return;
        }

        // Get the channel
        Channel channel = FancyChat.getChannelRegistry().getChannel( args[0] );
        if ( channel == null )
        {
            sender.sendMessage( FancyChat.messenger.playerError( "Channel '" + args[0] + "' does not exist." ) );
            return;
        }
        if ( sender instanceof Player )
        {
            try
            {
                ChannelPermissions.playerCanForce( (Player) sender, channel );
            }
            catch ( ChannelPermissionException e1 )
            {
                sender.sendMessage( FancyChat.messenger.playerError( e1.getMessage() ) );
                return;
            }
        }

        FancyChat.getPlayerRegistry().getPlayerChannels( player ).setFocused( channel );

        player.sendMessage( FancyChat.messenger.playerError( "A moderator has forced you into the " + channel.getName() + " channel." ) );
        sender.sendMessage( FancyChat.messenger.playerHeaderMsg( "You have forced " + player.getName() + " into the " + channel.getName() + " channel." ) );
    }

    @Override
    public String parent()
    {
        return "channel";
    }

    @Override
    public CaselessArrayList aliases()
    {
        return new CaselessArrayList();
    }

    @Override
    public String name()
    {
        return "force";
    }

    @Override
    public String permission()
    {
        return "fancychat.commands.channel.force";
    }

    @Override
    public String help()
    {
        return "";
    }
}
