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
package com.helion3.darmok.listeners;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerChatEvent;

import com.helion3.darmok.Darmok;
import com.helion3.darmok.channels.Channel;

public class PlayerChatListener {
    /**
     * Load channels for player upon join.
     * 
     * @param event
     */
    @Subscribe
    public void onPlayerChat(final PlayerChatEvent event) {
        Player player = event.getEntity();

        // Get the current default channel
        Channel channel = Darmok.getPlayerRegistry().getPlayerChannels(player).getDefault();

        // Reset their default
        if (channel == null) {
            channel = Darmok.resetDefaultChannelForPlayer(player);
        }

        if (channel != null) {
            Darmok.getLogger().debug("Found default channel " + channel.getName() + " for " + player.getName());
            Darmok.getChatter().send(player, channel, event.getUnformattedMessage());
            event.setCancelled(true);
        }
    }
}
