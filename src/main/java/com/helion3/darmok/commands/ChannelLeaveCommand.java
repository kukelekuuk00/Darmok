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
import com.helion3.darmok.exceptions.LeaveChannelException;
import com.helion3.darmok.utils.Format;

public class ChannelLeaveCommand implements CommandCallable {
    @Override
    public Optional<CommandResult> process(CommandSource source, String arguments) throws CommandException {
        String[] args = arguments.split(" ");

        // @todo fix this!
        Player player = (Player) source;

        if( args.length != 2 ){
            source.sendMessage( Format.error( "You must provide a channel to leave, like /ch leave g" ) );
            return Optional.absent();
        }

        // Get the channel
        Channel channel = Darmok.getChannelRegistry().getChannel(args[1]);
        if( channel == null ){
            source.sendMessage( Format.error( "Channel '" + args[1] + "' does not exist." ) );
            return Optional.absent();
        }

        try {
            Darmok.getPlayerRegistry().getPlayerChannels(player).leaveChannel(channel);
        } catch (LeaveChannelException e) {
            source.sendMessage( Format.error( e.getMessage() ) );
            return Optional.absent();
        }

        source.sendMessage( Format.heading( "You have left "+channel.getName()+" channel." ) );

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