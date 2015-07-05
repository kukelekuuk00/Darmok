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

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.TextMessageException;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

import com.google.common.base.Optional;
import com.helion3.darmok.Darmok;
import com.helion3.darmok.channels.Channel;
import com.helion3.darmok.channels.ChannelPermissions;
import com.helion3.darmok.exceptions.ChannelPermissionException;
import com.helion3.darmok.players.PlayerChannels;
import com.helion3.darmok.utils.Format;

public class ChannelListCommand implements CommandCallable {
    @Override
    public Optional<CommandResult> process(CommandSource source, String arguments) throws CommandException {
        Player limitTo = (Player) source;

        ArrayList<Channel> channels;

        // Load the channels
        if( limitTo != null ){
            PlayerChannels playerChannels = Darmok.getPlayerRegistry().getPlayerChannels( limitTo );
            if( playerChannels == null ){
                source.sendMessage( Format.error( "This player has no active channel subscriptions." ) );
                return Optional.absent();
            }
            channels = playerChannels.getChannels();
        } else {
            channels = Darmok.getChannelRegistry().getChannels();
        }

        if( channels.isEmpty() ){
            source.sendMessage( Format.error( "There are no channels." ) );
            return Optional.absent();
        }

        // List them
        source.sendMessage( Format.heading( "-- All Channels --" ) );
        for ( Channel c : channels ){

            boolean youreBanned = Darmok.getPlayerRegistry().isPlayerBannedFromChannel(limitTo, c);

            boolean canRead = true;
            try {
                ChannelPermissions.playerCanRead(limitTo, c);
            } catch (ChannelPermissionException e) {
                canRead = false;
            }

            boolean canSpeak = true;
            try {
                ChannelPermissions.playerCanSpeak(limitTo, c);
            } catch (ChannelPermissionException e) {
                canSpeak = false;
            }

            // @todo fix colors!
            Text color = Texts.of(TextColors.WHITE);
            try {
                color = Texts.legacy().from(c.getColor());
            } catch (TextMessageException e) {
                e.printStackTrace();
            }
            Text yes = Texts.of(TextColors.GREEN, "Y");
            Text no = Texts.of(TextColors.RED, "N");

            Text channelEntry = Texts.of(color, c.getName(), " /", c.getCommand(),
                TextColors.WHITE, (youreBanned ? "You're Banned" : ""),
                TextColors.GRAY, "Read: ", (canRead ? yes : no),
                TextColors.GRAY, "Speak: ", (canSpeak ? yes : no)
            );

            source.sendMessage(Format.message(channelEntry));
        }
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