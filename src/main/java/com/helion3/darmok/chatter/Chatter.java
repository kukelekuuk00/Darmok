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
package com.helion3.darmok.chatter;

import java.util.List;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.TextMessageException;

import com.helion3.darmok.Darmok;
import com.helion3.darmok.channels.Channel;
import com.helion3.darmok.channels.ChannelPermissions;
import com.helion3.darmok.exceptions.ChannelPermissionException;
import com.helion3.darmok.utils.Format;

public class Chatter {
    /**
     *
     * @param player
     * @param channel
     * @param msg
     */
    public void send(Player player, Channel channel, Text msg) {
        try {
            ChannelPermissions.playerCanSpeak(player, channel);
        } catch (ChannelPermissionException e1) {
            player.sendMessage(Format.error(e1.getMessage()));
            return;
        }

        // Muted?
        if (isPlayerMuted(player)) {
            player.sendMessage(Format.error("You've been muted in this channel."));
            return;
        }

        /**
         * Apply censors
         */
        // Caps limits
        if (Darmok.getConfig().getNode("censors", "caps", "enabled").getBoolean()) {
            int minLength = Darmok.getConfig().getNode("censors", "caps", "min-length").getInt();
            int minPercent = Darmok.getConfig().getNode("censors", "caps", "min-percentage").getInt();
            msg = Darmok.getCensor().filterCaps(msg, minLength, minPercent);
        }

        // Profanity
        if (Darmok.getConfig().getNode("censors", "profanity", "enabled").getBoolean()) {
            if (Darmok.getCensor().containsSuspectedProfanity(msg)) {
                player.sendMessage(Format
                        .error("Profanity or trying to bypass the censor is not allowed. Sorry if this is a false catch."));
                return;
            } else {
                // scan for words we censor
                msg = Darmok.getCensor().replaceCensoredWords(msg);
            }
        }

        // Format the final message
        try {
            msg = channel.formatMessage(player, msg);
        } catch (TextMessageException e1) {
            e1.printStackTrace();
        }

        /**
         * Build a list of all players we think we should be messaging.
         */
        List<Player> playersToMessage = Darmok.getPlayerRegistry().getPlayersInChannel(channel);

        // Message players if in range
        for (Player pl : playersToMessage) {
            int range = channel.getRange();

            // Does range matter?
            if (range > -1) {
                // if 0, check worlds match
                if (range == 0 && !player.getWorld().equals(pl.getWorld())) {
                    continue;
                }
                // otherwise, it's a distance
                else if (!player.getWorld().equals(pl.getWorld())
                        || player.getLocation().getPosition().distance(pl.getLocation().getPosition()) > range) {
                    continue;
                }
            }

            // Player is in range.

            // Ensure they have permission to READ
            try {
                ChannelPermissions.playerCanRead(player, channel);
            } catch (ChannelPermissionException e) {
                return;
            }

            // All checks are GO for launch
            pl.sendMessage(msg);
        }

        // log to console
        Darmok.getGame().getServer().getConsole().sendMessage(msg);
    }

    /**
     *
     * @param player
     * @return
     */
    private boolean isPlayerMuted(Player player) {
        // @todo until essentials works, this won't
        return false;
    }
}