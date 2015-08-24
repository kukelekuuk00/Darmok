package com.radthorne.fancychat.commands.FancyChatSubCommands;

import com.radthorne.fancychat.CaselessArrayList;
import com.radthorne.fancychat.FancyChat;
import com.radthorne.fancychat.commandlibs.SubCommand;
import com.radthorne.fancychat.exceptions.NoConsoleException;
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
public class reload implements SubCommand
{
    @Override
    public void console( ConsoleCommandSender sender, String label, String[] args ) throws NoConsoleException
    {
        handle( sender );
    }

    @Override
    public void player( Player sender, String label, String[] args )
    {
        if ( sender.hasPermission( permission() ) )
        {
            handle( sender );
        }
    }

    public void handle( CommandSender sender )
    {
        FancyChat.getInstance().unloadChannels();
        FancyChat.getInstance().reloadConfig();
        FancyChat.getInstance().loadConfig();
        FancyChat.getInstance().loadChannelsForAllPlayers();
        sender.sendMessage( FancyChat.messenger.playerHeaderMsg( "Configuration reloaded successfully." ) );
    }

    @Override
    public String parent()
    {
        return "fancychat";
    }

    @Override
    public CaselessArrayList aliases()
    {
        return new CaselessArrayList();
    }

    @Override
    public String name()
    {
        return "reload";
    }

    @Override
    public String permission()
    {
        return "fancychat.commands.fancychat.reload";
    }

    @Override
    public String help()
    {
        return "";
    }
}
