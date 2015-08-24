package com.radthorne.fancychat.players;

import com.radthorne.fancychat.FancyChat;
import com.radthorne.fancychat.channels.Channel;
import com.radthorne.fancychat.channels.ChannelPermissions;
import com.radthorne.fancychat.exceptions.ChannelPermissionException;
import com.radthorne.fancychat.exceptions.JoinChannelException;
import com.radthorne.fancychat.exceptions.LeaveChannelException;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PlayerChannels
{

    /**
     *
     */
    private Player player;


    /**
     *
     */
    private ArrayList<String> channels = new ArrayList<>();


    /**
     *
     */
    private String focusedChannel;


    /**
     * @param player
     */
    public PlayerChannels( Player player )
    {
        this.player = player;
    }


    /**
     * @return
     */
    public ArrayList<Channel> getChannels()
    {
        ArrayList<Channel> playerChannels = new ArrayList<>();
        if ( !channels.isEmpty() )
        {
            for ( String alias : channels )
            {
                Channel c = FancyChat.getChannelRegistry().getChannel( alias );
                if ( c != null )
                {
                    playerChannels.add( c );
                }
            }
        }
        return playerChannels;
    }


    /**
     * Does not register the channel in the db, use this only when the user
     * already has joined the channel. Primarily used when re/loading
     * channel settings for a player.
     *
     * @param c
     */
    public boolean addChannel( Channel c )
    {
        if ( !channels.contains( c.getShortName().toLowerCase() ) )
        {
            channels.add( c.getShortName().toLowerCase() );
        }
        return true;
    }


    /**
     * @return
     */
    public Channel getFocused()
    {
        if ( focusedChannel != null )
        {
            return FancyChat.getChannelRegistry().getChannel( focusedChannel );
        }
        return null;
    }


    /**
     * @param channel
     * @return
     */
    public boolean setFocused( Channel channel )
    {
        focusedChannel = channel.getShortName();
        return true;
    }


    /**
     * @param channel
     * @return
     */
    public boolean inChannel( Channel channel )
    {
        return channels.contains( channel.getShortName().toLowerCase() );
    }


    /**
     * Subscribes a player to a channel.
     *
     * @param c
     * @throws JoinChannelException
     */
    public void joinChannel( Channel c ) throws JoinChannelException
    {

        try
        {
            ChannelPermissions.playerCanJoin( player, c );
        }
        catch ( ChannelPermissionException e )
        {
            throw new JoinChannelException( e.getMessage() );
        }

        addChannel( c );
    }


    /**
     * Removes a channel from the player's subscriptions.
     *
     * @return
     * @throws LeaveChannelException
     */
    public boolean leaveChannel( Channel channel ) throws LeaveChannelException
    {
        if ( channel != null )
        {

            try
            {
                ChannelPermissions.playerCanLeave( player, channel );
            }
            catch ( ChannelPermissionException e )
            {
                throw new LeaveChannelException( e.getMessage() );
            }

            if ( channel.getShortName().equals( focusedChannel ) )
            {
                if ( channels.size() > 1 )
                {
                    // Find the first channel that isn't this one
                    for ( String alias : channels )
                    {
                        if ( !alias.equals( channel.getShortName() ) )
                        {
                            focusedChannel = alias;
                            break;
                        }
                    }
                }
                else
                {
                    throw new LeaveChannelException( "May not leave only subscribed channel." );
                }
            }
            removeChannel( channel );
        }
        return false;
    }


    /**
     * Removes a channel from a player without unsubscribing
     *
     * @return
     * @throws LeaveChannelException
     */
    public void removeChannel( Channel channel ) throws LeaveChannelException
    {
        if ( channel != null )
        {
            channels.remove( channel.getShortName().toLowerCase() );
            if ( channels.size() > 0 )
            {
                setFocused( FancyChat.getChannelRegistry().getChannel( channels.get( 0 ) ) );
            }
            return;
        }
        throw new LeaveChannelException( "Removing channel player is not subscribed to." );
    }
}