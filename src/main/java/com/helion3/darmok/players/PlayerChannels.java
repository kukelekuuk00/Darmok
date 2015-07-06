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
package com.helion3.darmok.players;

import java.util.ArrayList;

import org.spongepowered.api.entity.player.Player;

import com.helion3.darmok.Darmok;
import com.helion3.darmok.channels.Channel;
import com.helion3.darmok.channels.ChannelPermissions;
import com.helion3.darmok.exceptions.ChannelPermissionException;
import com.helion3.darmok.exceptions.JoinChannelException;
import com.helion3.darmok.exceptions.LeaveChannelException;

public class PlayerChannels {

    /**
     *
     */
    private Player player;

    /**
     *
     */
    private ArrayList<String> channels = new ArrayList<String>();

    /**
     *
     */
    private String defaultChannel;

    /**
     *
     * @param player
     */
    public PlayerChannels(Player player) {
        this.player = player;
    }

    /**
     *
     * @return
     */
    public ArrayList<Channel> getChannels() {
        ArrayList<Channel> playerChannels = new ArrayList<Channel>();
        if (!channels.isEmpty()) {
            for (String alias : channels) {
                Channel c = Darmok.getChannelRegistry().getChannel(alias);
                if (c != null) {
                    playerChannels.add(c);
                }
            }
        }
        return playerChannels;
    }

    /**
     * Does not register the channel in the db, use this only when the user
     * already has joined the channel. Primarily used when re/loading channel
     * settings for a player.
     * 
     * @param c
     */
    public boolean addChannel(Channel c) {
        channels.add(c.getCommand());
        return true;
    }

    /**
     *
     * @return
     */
    public Channel getDefault() {
        if (defaultChannel != null) {
            return Darmok.getChannelRegistry().getChannel(defaultChannel);
        }
        return null;
    }

    /**
     *
     * @param channel
     * @return
     */
    public boolean setDefault(Channel channel) {
        defaultChannel = channel.getCommand();
        return true;
    }

    /**
     *
     * @param channel
     * @return
     */
    public boolean inChannel(Channel channel) {
        return channels.contains(channel.getCommand());
    }

    /**
     * Subscribes a player to a channel.
     * 
     * @param c
     * @throws JoinChannelException
     */
    public void joinChannel(Channel c) throws JoinChannelException {

        try {
            ChannelPermissions.playerCanJoin(player, c);
        } catch (ChannelPermissionException e) {
            throw new JoinChannelException(e.getMessage());
        }

        addChannel(c);

    }

    /**
     * Removes a channel from the player's subscriptions.
     * 
     * @param player
     * @return
     * @throws LeaveChannelException
     */
    public boolean leaveChannel(Channel channel) throws LeaveChannelException {
        if (channel != null) {

            try {
                ChannelPermissions.playerCanLeave(player, channel);
            } catch (ChannelPermissionException e) {
                throw new LeaveChannelException(e.getMessage());
            }

            if (channel.getCommand().equals(defaultChannel)) {
                if (channels.size() > 1) {
                    // Find the first channel that isn't this one
                    for (String alias : channels) {
                        if (!alias.equals(channel.getCommand())) {
                            defaultChannel = alias;
                            break;
                        }
                    }
                } else {
                    throw new LeaveChannelException("May not leave only subscribed channel.");
                }
            }
            removeChannel(channel);

        }
        return false;
    }

    /**
     * Removes a channel from a player without unsubscribing
     * 
     * @param player
     * @return
     * @throws LeaveChannelException
     */
    public void removeChannel(Channel channel) throws LeaveChannelException {
        if (channel != null) {
            channels.remove(channel.getCommand());
            if (channels.size() > 0) {
                setDefault(Darmok.getChannelRegistry().getChannel(channels.get(0)));
            }
            return;
        }
        throw new LeaveChannelException("Removing channel player is not subscribed to.");
    }
}