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
import java.util.HashMap;
import java.util.Map.Entry;

import org.spongepowered.api.entity.player.Player;

import com.helion3.darmok.channels.Channel;
import com.helion3.darmok.channels.ChannelPermissions;
import com.helion3.darmok.exceptions.ChannelPermissionException;
import com.helion3.darmok.exceptions.LeaveChannelException;

public class PlayerRegistry {
    private HashMap<Player,PlayerChannels> players = new HashMap<Player,PlayerChannels>();
    private HashMap<Player,ArrayList<String>> channelBans = new HashMap<Player,ArrayList<String>>();

    /**
     *
     * @return
     */
    public HashMap<Player,PlayerChannels> getPlayers(){
        return players;
    }

    /**
     *
     * @param player
     * @return
     */
    public PlayerChannels getPlayerChannels(Player player){
        if( ! players.containsKey( player ) ){
            players.put( player, new PlayerChannels( player ) );
        }
        return players.get( player );
    }

    /**
     *
     * @param player
     * @return
     */
    public ArrayList<Player> getPlayersInChannel( Channel channel ){
        ArrayList<Player> inChannel = new  ArrayList<Player>();
        for (Entry<Player,PlayerChannels> entry : players.entrySet()){
            if( entry.getValue().inChannel(channel) ){

                try {
                    ChannelPermissions.playerCanRead( entry.getKey(), channel );
                } catch (ChannelPermissionException e1) {
                    try {
                        players.get( entry.getKey() ).leaveChannel(channel);
                    } catch (LeaveChannelException e) {
                    }
                    continue;
                }

                inChannel.add( entry.getKey() );

            }
        }
        return inChannel;
    }

    /**
     *
     * @param player
     * @param channel
     */
    public void banFromChannel( Player player, Channel channel ){
        try {
            getPlayerChannels( player ).removeChannel(channel);
        } catch (LeaveChannelException e) {
            // not really possible
        }
        setChannelBanForPlayer( player, channel.getCommand() );
    }

    /**
     *
     * @param player
     * @param channel
     */
    public void unbanFromChannel( Player player, Channel channel ){
        if( channelBans.containsKey( player ) ){
            ArrayList<String> bannedin = channelBans.get( player );
            bannedin.remove( channel.getCommand() );
            channelBans.put( player, bannedin );
        }
    }

    /**
     *
     * @param player
     * @param alias
     */
    public void setChannelBanForPlayer( Player player, String alias ){
        ArrayList<String> bannedin;
        if( channelBans.containsKey( player ) ){
            bannedin = channelBans.get( player );
        } else {
            bannedin = new ArrayList<String>();
        }
        bannedin.add( alias );
        channelBans.put( player, bannedin );
    }

    /**
     *
     * @param player
     * @param channel
     * @return
     */
    public boolean isPlayerBannedFromChannel( Player player, Channel channel ){
        if( channelBans.containsKey( player ) ){
            ArrayList<String> bannedin = channelBans.get( player );
            if( bannedin.contains( channel.getCommand() ) ){
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param player
     * @param channel
     * @return
     */
    public ArrayList<String> getChannelBansForPlayer( Player player ){
        if( channelBans.containsKey( player ) ){
            return channelBans.get( player );
        }
        return null;
    }

    /**
     *
     * @param player
     */
    public void removePlayer( Player player ){
        if( players.containsKey( player ) ){
            players.remove( player );
        }
    }
}