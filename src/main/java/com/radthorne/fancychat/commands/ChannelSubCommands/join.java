package com.radthorne.fancychat.commands.ChannelSubCommands;

import com.radthorne.fancychat.CaselessArrayList;
import com.radthorne.fancychat.FancyChat;
import com.radthorne.fancychat.channels.Channel;
import com.radthorne.fancychat.commandlibs.SubCommand;
import com.radthorne.fancychat.exceptions.JoinChannelException;
import com.radthorne.fancychat.exceptions.NoConsoleException;
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
public class join implements SubCommand
{
    @Override
    public void console( ConsoleCommandSender sender, String label, String[] args ) throws NoConsoleException
    {
        throw new NoConsoleException();
    }

    @Override
    public void player( Player sender, String label, String[] args )
    {

        String ch;
        if ( aliases().contains( label ) )
        {
            ch = label;
        }
        else if ( args.length > 0 )
        {
            ch = args[0];
        }
        else
        {
            sender.sendMessage( FancyChat.messenger.playerError( "You must provide a channel to join, like /ch join g" ) );
            return;
        }
        // Get the channel
        Channel channel = FancyChat.getChannelRegistry().getChannel( ch );
        if ( channel == null )
        {
            sender.sendMessage( FancyChat.messenger.playerError( "Channel '" + ch + "' does not exist." ) );
            return;
        }

        try
        {
            FancyChat.getPlayerRegistry().getPlayerChannels( sender ).joinChannel( channel );
            FancyChat.getPlayerRegistry().getPlayerChannels( sender ).setFocused( channel );
        }
        catch ( JoinChannelException e )
        {
            sender.sendMessage( FancyChat.messenger.playerError( e.getMessage() ) );
            return;
        }

        sender.sendMessage( FancyChat.messenger.playerHeaderMsg( "Joined " + channel.getName() + " channel." ) );
        return;

        // @todo alert other players in this channel that they joined

    }

    @Override
    public String parent()
    {
        return "channel";
    }

    @Override
    public CaselessArrayList aliases()
    {
        return FancyChat.getInstance().getChannelAliases();
    }

    @Override
    public String name()
    {
        return "join";
    }

    @Override
    public String permission()
    {
        return "fancychat.commands.channel.join";
    }

    @Override
    public String help()
    {
        return null;
    }
}
