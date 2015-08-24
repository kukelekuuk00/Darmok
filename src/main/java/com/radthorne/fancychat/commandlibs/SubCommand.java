package com.radthorne.fancychat.commandlibs;

import com.radthorne.fancychat.CaselessArrayList;
import com.radthorne.fancychat.exceptions.NoConsoleException;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

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

public interface SubCommand
{
    /**
     * @param sender The ConsoleCommandSender
     * @param label  the command label.
     * @param args   the arguments.
     * @throws NoConsoleException exception when the console can't execute this command;
     */
    void console( ConsoleCommandSender sender, String label, String[] args ) throws NoConsoleException;

    /**
     * @param sender The Player
     * @param label  the command label.
     * @param args   the arguments.
     */
    void player( Player sender, String label, String[] args );

    /**
     * @return the name of the parent command
     */
    String parent();

    /**
     * @return the aliases of the subcommand
     */
    CaselessArrayList aliases();

    /**
     * @return the name of the subcommand
     */
    String name();

    /**
     * @return the permission required to use the subcommand
     */
    String permission();

    /**
     * @return The help message for the subcommand
     */
    String help();
}
