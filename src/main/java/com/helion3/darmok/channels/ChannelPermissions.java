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
package com.helion3.darmok.channels;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.util.command.CommandSource;

import com.helion3.darmok.Darmok;
import com.helion3.darmok.exceptions.ChannelPermissionException;

public class ChannelPermissions {
    private ChannelPermissions() {}

    /**
     *
     * @param player
     * @param channel
     * @return
     * @throws ChannelPermissionException
     */
    public static boolean playerCanAutoJoin( Player player, Channel channel ) throws ChannelPermissionException{
        String permPrefix = "darmok.channel." + channel.getName().toLowerCase() + ".";
        if( player.hasPermission( permPrefix + "autojoin" ) && player.hasPermission( permPrefix + "read" ) ){
            return true;
        }
        throw new ChannelPermissionException("Insufficient permission to auto-join this channel.");
    }

    /**
     *
     * @param player
     * @param channel
     * @return
     * @throws ChannelPermissionException
     */
    public static boolean sourceCanBan( CommandSource source, Channel channel ) throws ChannelPermissionException {
        if (!(source instanceof Player)) {
            return true;
        }

        Player player = (Player) source;
        String permPrefix = "darmok.channel." + channel.getName().toLowerCase() + ".";
        if( player.hasPermission( permPrefix + "ban" ) || player.hasPermission( "darmok.mod" ) ){
            return true;
        }

        throw new ChannelPermissionException("Insufficient permission to ban a player from this channel.");
    }

    /**
     *
     * @param player
     * @param channel
     * @return
     * @throws ChannelPermissionException
     */
    public static boolean playerCanDefaultTo( Player player, Channel channel ) throws ChannelPermissionException{

        // Perms?
        String permPrefix = "darmok.channel." + channel.getName().toLowerCase() + ".";
        if( !player.hasPermission( permPrefix + "read" ) && !player.hasPermission( permPrefix + "speak" ) ){
            throw new ChannelPermissionException("Insufficient permission to read or speak in this channel.");
        }

        // Banned?
        if( Darmok.getPlayerRegistry().isPlayerBannedFromChannel(player, channel) ){
            throw new ChannelPermissionException("Player has been banned from this channel.");
        }

        return true;

    }

    /**
     * Can the player force another user to change channels
     * @param player
     * @param channel
     * @return
     * @throws ChannelPermissionException
     */
    public static boolean sourceCanForce(CommandSource source, Channel channel ) throws ChannelPermissionException {
        if (!(source instanceof Player)) {
            return true;
        }

        Player player = (Player) source;
        if( !player.hasPermission( "darmok.mod" ) ){
            throw new ChannelPermissionException("Insufficient permission to force a player into this channel.");
        }
        return false;
    }

    /**
     *
     * @param player
     * @param channel
     * @return
     * @throws ChannelPermissionException
     */
    public static boolean playerCanJoin( Player player, Channel channel ) throws ChannelPermissionException{
        // Perms?
        String permPrefix = "darmok.channel." + channel.getName().toLowerCase() + ".";
        if( !player.hasPermission( permPrefix + "read" ) && !player.hasPermission( permPrefix + "speak" ) ){
            throw new ChannelPermissionException("Insufficient permission to read or speak in this channel.");
        }

        // Banned?
        if( Darmok.getPlayerRegistry().isPlayerBannedFromChannel(player, channel) ){
            throw new ChannelPermissionException("Player has been banned from this channel.");
        }

        return true;
    }

    /**
     * They have to be allowed to join if they have read or speak perms.
     * @param player
     * @param channel
     * @return
     * @throws ChannelPermissionException
     */
    public static boolean sourceCanKick( CommandSource source, Channel channel ) throws ChannelPermissionException {
        if (!(source instanceof Player)) {
            return true;
        }

        Player player = (Player) source;
        String permPrefix = "darmok.channel." + channel.getName().toLowerCase() + ".";
        if( player.hasPermission( permPrefix + "kick" ) || player.hasPermission( permPrefix + "ban" ) || player.hasPermission( "darmok.mod" ) ){
            return true;
        }
        throw new ChannelPermissionException("Insufficient permission to kick a player from this channel.");
    }

    /**
     *
     * @param player
     * @param channel
     * @return
     * @throws ChannelPermissionException
     */
    public static boolean playerCanLeave( Player player, Channel channel ) throws ChannelPermissionException{
        String permPrefix = "darmok.channel." + channel.getName().toLowerCase() + ".";
        if( player.hasPermission( permPrefix + "leave" ) ){
            return true;
        }
        throw new ChannelPermissionException("Insufficient permission to leave this channel.");
    }

    /**
     * Can't speak without reading, so speak grants read perms.
     * @param player
     * @param channel
     * @return
     * @throws ChannelPermissionException
     */
    public static boolean playerCanRead(Player player, Channel channel) throws ChannelPermissionException{
        // Perms?
        String permPrefix = "darmok.channel." + channel.getName().toLowerCase() + ".";
        if( !player.hasPermission( permPrefix + "read" ) && !player.hasPermission( permPrefix + "speak" ) ){
            throw new ChannelPermissionException("Insufficient permission to read this channel.");
        }

        // Banned?
        if( Darmok.getPlayerRegistry().isPlayerBannedFromChannel(player, channel) ){
            throw new ChannelPermissionException("Player has been banned from this channel.");
        }

        return true;
    }

    /**
     *
     * @param player
     * @param channel
     * @return
     * @throws ChannelPermissionException
     */
    public static boolean playerCanSpeak(Player player, Channel channel) throws ChannelPermissionException{
        // Perms?
        String permPrefix = "darmok.channel." + channel.getName().toLowerCase() + ".";
        if( !player.hasPermission( permPrefix + "speak" ) ){
            throw new ChannelPermissionException("Insufficient permission to speak this channel.");
        }

        // Banned?
        if( Darmok.getPlayerRegistry().isPlayerBannedFromChannel(player, channel) ){
            throw new ChannelPermissionException("Player has been banned from this channel.");
        }

        return true;
    }
}
