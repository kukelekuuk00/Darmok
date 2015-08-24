package com.radthorne.fancychat.players;

import com.radthorne.fancychat.FancyChat;
import com.radthorne.fancychat.channels.Channel;
import com.radthorne.fancychat.channels.ChannelPermissions;
import com.radthorne.fancychat.exceptions.ChannelPermissionException;
import com.radthorne.fancychat.exceptions.LeaveChannelException;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class PlayerRegistry
{

    /**
     *
     */
    private HashMap<Player, PlayerChannels> players = new HashMap<Player, PlayerChannels>();


    /**
     * @return
     */
    public HashMap<Player, PlayerChannels> getPlayers()
    {
        return players;
    }


    /**
     * @param player
     * @return
     */
    public PlayerChannels getPlayerChannels( Player player )
    {
        if ( !players.containsKey( player ) )
        {
            players.put( player, new PlayerChannels( player ) );
        }
        return players.get( player );
    }


    /**
     * @param channel
     * @return
     */
    public ArrayList<Player> getPlayersInChannel( Channel channel )
    {
        ArrayList<Player> inChannel = new ArrayList<Player>();
        for ( Entry<Player, PlayerChannels> entry : players.entrySet() )
        {
            FancyChat.debug( "player: " + entry.getKey().getName() );
            if ( entry.getValue().inChannel( channel ) )
            {

                try
                {
                    ChannelPermissions.playerCanRead( entry.getKey(), channel );
                }
                catch ( ChannelPermissionException e1 )
                {
                    try
                    {
                        players.get( entry.getKey() ).leaveChannel( channel );
                    }
                    catch ( LeaveChannelException e )
                    {
                    }
                    continue;
                }
                inChannel.add( entry.getKey() );
            }
        }
        return inChannel;
    }


    /**
     * @param player
     * @param player
     * @return
     */
    public ArrayList<String> getChannelBansForPlayer( Player player )
    {
        ArrayList<String> list = new ArrayList<>();
        for ( Channel channel : FancyChat.getChannelRegistry().getChannels() )
        {
            if ( channel.isBanned( player ) )
            {
                list.add( channel.getName() );
            }
        }
        return list;
    }


    /**
     * @param player
     */
    public void removePlayer( Player player )
    {
        if ( players.containsKey( player ) )
        {
            players.remove( player );
        }
    }
}