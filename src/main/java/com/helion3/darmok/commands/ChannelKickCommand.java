/**
 * This file is part of Darmok, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 Helion3 http://helion3.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.helion3.darmok.commands;

import java.util.List;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

import com.google.common.base.Optional;
import com.helion3.darmok.Darmok;
import com.helion3.darmok.channels.Channel;
import com.helion3.darmok.channels.ChannelPermissions;
import com.helion3.darmok.exceptions.ChannelPermissionException;
import com.helion3.darmok.exceptions.LeaveChannelException;
import com.helion3.darmok.utils.Format;

public class ChannelKickCommand implements CommandCallable {
    @Override
    public Optional<CommandResult> process(CommandSource source, String arguments) throws CommandException {
        String[] args = arguments.split(" ");

        if( args.length != 3 ){
            source.sendMessage( Format.error( "You must provide a player name and channel, like /ch kick viveleroi g" ) );
            return Optional.absent();
        }

        // Get the player
        Optional<Player> player = Darmok.getGame().getServer().getPlayer(args[1]);
        if (!player.isPresent()) {
            source.sendMessage(Format.error("Can't find a player with that name."));
            return Optional.absent();
        }

        // Get the channel
        Channel channel = Darmok.getChannelRegistry().getChannel(args[2]);
        if( channel == null ){
            source.sendMessage( Format.error( "Channel '" + args[2] + "' does not exist." ) );
            return Optional.absent();
        }

        try {
            ChannelPermissions.sourceCanKick( source, channel );
        } catch (ChannelPermissionException e1) {
            source.sendMessage( Format.error( e1.getMessage() ) );
            return Optional.absent();
        }

        try {
            Darmok.getPlayerRegistry().getPlayerChannels(player.get()).removeChannel( channel );
        } catch (LeaveChannelException e) {
            // not really possible
        }

        player.get().sendMessage( Format.error( "You have been kicked from the "+channel.getName()+" channel." ) );
        source.sendMessage( Format.heading( "You have kicked "+player.get().getName()+" from the "+channel.getName()+" channel." ) );

        // @todo alert other players in this channel that they left

        return Optional.of(CommandResult.success());
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return true;
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return null;
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return null;
    }

    @Override
    public Text getUsage(CommandSource source) {
        return null;
    }
}