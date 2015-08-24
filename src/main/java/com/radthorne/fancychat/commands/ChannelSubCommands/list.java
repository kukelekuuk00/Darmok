package com.radthorne.fancychat.commands.ChannelSubCommands;

import com.radthorne.fancychat.CaselessArrayList;
import com.radthorne.fancychat.FancyChat;
import com.radthorne.fancychat.channels.Channel;
import com.radthorne.fancychat.channels.ChannelPermissions;
import com.radthorne.fancychat.commandlibs.SubCommand;
import com.radthorne.fancychat.exceptions.ChannelPermissionException;
import com.radthorne.fancychat.exceptions.NoConsoleException;
import com.radthorne.fancychat.players.PlayerChannels;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

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
public class list implements SubCommand
{
    @Override
    public void console( ConsoleCommandSender sender, String label, String[] args ) throws NoConsoleException
    {
        throw new NoConsoleException();
    }

    @Override
    public void player( Player sender, String label, String[] args )
    {
        PlayerChannels playerChannels = FancyChat.getPlayerRegistry().getPlayerChannels( sender );
        if ( playerChannels == null )
        {
            sender.sendMessage( FancyChat.messenger.playerError( "This player has no active channel subscriptions." ) );
            return;
        }
        ArrayList<Channel> channels = playerChannels.getChannels();

        if ( channels.isEmpty() )
        {
            sender.sendMessage( FancyChat.messenger.playerError( "There are no channels." ) );
            return;
        }

        // List them
        sender.sendMessage( FancyChat.messenger.playerHeaderMsg( "-- All Channels --" ) );
        for ( Channel c : channels )
        {
            boolean youreBanned = c.isBanned( sender );

            boolean canRead = true;
            try
            {
                ChannelPermissions.playerCanRead( sender, c );
            }
            catch ( ChannelPermissionException e )
            {
                canRead = false;
            }

            boolean canSpeak = true;
            try
            {
                ChannelPermissions.playerCanSpeak( sender, c );
            }
            catch ( ChannelPermissionException e )
            {
                canSpeak = false;
            }

            String list = c.getColor() + c.getName() + " / " + c.getShortName() + " &f" + ( youreBanned ? "You're Banned" : "" );
            list += " &7Read: " + ( canRead ? "&aY" : "&cN" );
            list += " &7Speak: " + ( canSpeak ? "&aY" : "&cN" );

            sender.sendMessage( FancyChat.messenger.playerMsg( c.colorize( list ) ) );
        }
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
        return "list";
    }

    @Override
    public String permission()
    {
        return "fancychat.commands.channel.list";
    }

    @Override
    public String help()
    {
        return "";
    }
}
