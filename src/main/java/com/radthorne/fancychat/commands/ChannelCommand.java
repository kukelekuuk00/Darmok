package com.radthorne.fancychat.commands;

import com.radthorne.fancychat.commandlibs.CommonsCommand;
import com.radthorne.fancychat.exceptions.NoConsoleException;
import org.bukkit.Bukkit;
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
public class ChannelCommand implements CommonsCommand
{
    @Override
    public void console( ConsoleCommandSender sender, String label ) throws NoConsoleException
    {
        throw new NoConsoleException();
    }

    @Override
    public void player( Player sender, String label )
    {
        sender.sendMessage( "/" + label + " help" );
        Bukkit.dispatchCommand( sender, "/" + label + " help" );
    }

    @Override
    public String name()
    {
        return "channel";
    }

    @Override
    public String permission()
    {
        return "fancychat.commands.channel";
    }

    @Override
    public String help()
    {
        return "";
    }
}
