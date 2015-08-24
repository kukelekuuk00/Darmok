package com.radthorne.fancychat.commandlibs;

import com.radthorne.fancychat.CaselessArrayList;
import com.radthorne.fancychat.FancyChat;
import com.radthorne.fancychat.exceptions.NoConsoleException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Copyright 2015 Luuk Jacobs
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class CommandHandler implements CommandExecutor
{
    private Map<CommonsCommand, List<SubCommand>> commandMap = new HashMap<>();

    private boolean permissionsHelp = false;

    public CommandHandler()
    {
    }

    public CommandHandler( Map<CommonsCommand, List<SubCommand>> commandMap )
    {
        this.commandMap = commandMap;
    }

    public void RegisterCommand( CommonsCommand command, List<SubCommand> subCommands )
    {
        commandMap.put( command, subCommands );
    }

    @Override
    public boolean onCommand( CommandSender sender, Command cmd, String label, String[] args )
    {
        for ( CommonsCommand command : commandMap.keySet() )
        {
            if ( command.name().equalsIgnoreCase( cmd.getName() ) )
            {
                if ( args.length == 0 )
                {
                    if ( sender instanceof Player )
                    {
                        Player player = (Player) sender;
                        if ( hasPermission( player, command.permission() ) )
                        {
                            command.player( player, label );
                        }
                        else
                        {
                            player.sendMessage( "no permission" );
                        }
                    }
                    else if ( sender instanceof ConsoleCommandSender )
                    {
                        try
                        {
                            command.console( (ConsoleCommandSender) sender, label );
                        }
                        catch ( NoConsoleException e )
                        {
                            sender.sendMessage( e.getMessage() );
                        }
                    }
                }
                else if ( args.length > 0 && args[0].equalsIgnoreCase( "help" ) )
                {
                    if ( command.help() != null && !command.help().equals( "" ) && ( permissionsHelp && hasPermission( sender, command.permission() ) ) )
                    {
                        if ( permissionsHelp )
                        {
                            if ( hasPermission( sender, command.permission() ) )
                            {
                                sender.sendMessage( command.help() );
                            }
                        }
                        else
                        {
                            sender.sendMessage( command.help() );
                        }
                    }
                    List<SubCommand> subCommands = commandMap.get( command );
                    for ( SubCommand sub : subCommands )
                    {
                        if ( sub.help() != null && !sub.help().equals( "" ) )
                        {
                            if ( permissionsHelp )
                            {
                                if ( hasPermission( sender, sub.permission() ) )
                                {
                                    sender.sendMessage( sub.help() );
                                }
                            }
                            else
                            {
                                sender.sendMessage( sub.help() );
                            }
                        }
                    }
                }
                else if ( args.length > 0 )
                {
                    String subLabel = args[0];
                    FancyChat.debug( "label: " + subLabel );
                    List<SubCommand> subCommands = commandMap.get( command );
                    for ( SubCommand sub : subCommands )
                    {
                        FancyChat.debug( "sub: " + sub.name() );
                        CaselessArrayList aliases = sub.aliases();
                        if ( sub.name().equalsIgnoreCase( subLabel ) || ( !aliases.isEmpty() && aliases.contains( subLabel ) ) )
                        {
                            String[] subArgs = new String[0];
                            if ( args.length > 1 )
                            {
                                subArgs = Arrays.copyOfRange( args, 1, args.length );
                            }
                            if ( sender instanceof Player )
                            {
                                Player player = (Player) sender;
                                if ( hasPermission( player, command.permission() ) )
                                {
                                    FancyChat.debug( "sub.player:" + sub.name() );
                                    sub.player( player, subLabel, subArgs );
                                }
                                else
                                {
                                    player.sendMessage( "no permission" );
                                }
                            }
                            else if ( sender instanceof ConsoleCommandSender )
                            {
                                try
                                {
                                    sub.console( (ConsoleCommandSender) sender, subLabel, subArgs );
                                }
                                catch ( NoConsoleException e )
                                {
                                    sender.sendMessage( e.getMessage() );
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean hasPermission( CommandSender player, String permission )
    {
        return permission == null || permission.equals( "" ) || player.hasPermission( permission );
    }

    public void setPermissionsHelp( boolean permissionsHelp )
    {
        this.permissionsHelp = permissionsHelp;
    }

    public boolean isPermissionsHelp()
    {
        return permissionsHelp;
    }
}
