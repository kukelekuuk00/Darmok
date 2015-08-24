package com.radthorne.fancychat.chatter;

import com.radthorne.fancychat.FancyChat;
import com.radthorne.fancychat.JChat;
import com.radthorne.fancychat.channels.Channel;
import com.radthorne.fancychat.channels.ChannelPermissions;
import com.radthorne.fancychat.exceptions.ChannelPermissionException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class Chatter
{

    /**
     *
     */
    protected FancyChat plugin;

    /**
     *
     */
    protected HashMap<Player, Long> messageTimestamps = new HashMap<Player, Long>();


    /**
     * @param plugin
     */
    public Chatter( FancyChat plugin )
    {
        this.plugin = plugin;
    }


    /**
     * @param player
     * @param channel
     * @param msg
     */
    public void send( Player player, Channel channel, String msg )
    {
        FancyChat.debug( "so it is sending.. right?" );

        // Not spamming are you?
        if ( isPlayerSpamming( player ) )
        {
            player.sendMessage( FancyChat.messenger.playerError( "Can the spam man!" ) );
            return;
        }

        try
        {
            ChannelPermissions.playerCanSpeak( player, channel );
        }
        catch ( ChannelPermissionException e1 )
        {
            player.sendMessage( FancyChat.messenger.playerError( e1.getMessage() ) );
            return;
        }

        // Muted?
        if ( isPlayerMuted( player, channel ) )
        {
            player.sendMessage( FancyChat.messenger.playerError( "You've been muted in this channel, sorry." ) );
            return;
        }

        /**
         * Apply censors
         */
        // Caps limits
        if ( plugin.getConfig().getBoolean( "fancychat.censors.caps.enabled" ) )
        {
            msg = FancyChat.getCensor().filterCaps( msg, plugin.getConfig().getInt( "fancychat.censors.caps.min-length" ), plugin.getConfig().getInt( "fancychat.censors.caps.min-percentage" ) );
        }
//		// Fake censor
//		if( plugin.getConfig().getBoolean("fancychat.censors.fakecensor.enabled") && FancyChat.getCensor().isFakeCensor( msg, plugin.getConfig().getString("fancychat.censors.fakecensor.string") ) ){
//			player.sendMessage( FancyChat.messenger.playerError("Sorry but we do not allow stars instead of curse words.") );
//			return;
//		}
        // Profanity
        if ( plugin.getConfig().getBoolean( "fancychat.censors.profanity.enabled" ) )
        {
            if ( FancyChat.getCensor().containsSuspectedProfanity( msg ) )
            {

                player.sendMessage( FancyChat.messenger.playerError( "Profanity or trying to bypass the censor is not allowed. Sorry if this is a false catch." ) );

//				String alert_msg = player.getName() + "'s message was blocked for profanity.";
//				plugin.alertPlayers(alert_msg);
//				plugin.log( player.getName()+"'s message was blocked for profanity. Original was: " + event.getMessage() );

                return;
            }
            else
            {
                // scan for words we censor
                msg = FancyChat.getCensor().replaceCensoredWords( msg );
            }
        }

        // Do they have permission to use colors?
        if ( !player.hasPermission( "fancychat.chatcolor" ) )
        {
            msg = channel.stripColor( msg );
        }

        // Format the final message
        JChat jChat = channel.formatMessage( player, msg );

        /**
         * Build a list of all players we think we should be
         * messaging.
         */
        List<Player> playersToMessage = channel.getPlayers();

        // Message players if in range
        for ( Player pl : playersToMessage )
        {
            FancyChat.debug( "pl: " + pl.getName() );
            int range = channel.getRange();

            // Does range matter?
            if ( range > -1 )
            {
                // if 0, check worlds match
                if ( range == 0 && !player.getWorld().equals( pl.getWorld() ) )
                {
                    continue;
                }
                // otherwise, it's a distance
                else if ( !player.getWorld().equals( pl.getWorld() ) || player.getLocation().distance( pl.getLocation() ) > range )
                {
                    continue;
                }
            }

            // Ensure they have permission to READ
            try
            {
                ChannelPermissions.playerCanRead( player, channel );
            }
            catch ( ChannelPermissionException e )
            {
                FancyChat.debug( "can't read: " + pl.getName() );
                return;
            }

            // All checks are GO for launch
            try
            {
                FancyChat.debug( "sending: " + pl.getName() );
                jChat.send( pl );
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }

        // log to console
        Bukkit.getServer().getConsoleSender().sendMessage( jChat.toOldMessageFormat() );
    }


    /**
     * @param player
     * @return
     */
    private boolean isPlayerSpamming( Player player )
    {

        if ( !plugin.getConfig().getBoolean( "fancychat.spam-prevention.enabled" ) )
        {
            return false;
        }

        int secondBetween = plugin.getConfig().getInt( "fancychat.spam-prevention.min-seconds-between-msg" );
        long currentTime = System.currentTimeMillis();
        long spam = currentTime;

        if ( messageTimestamps.containsKey( player ) )
        {
            spam = messageTimestamps.get( player );
            messageTimestamps.remove( player );
        }
        else
        {
            spam -= ( ( secondBetween + 1 ) * 1000 );
        }

        messageTimestamps.put( player, currentTime );

        if ( currentTime - spam < ( secondBetween * 1000 ) )
        {
            return true;
        }

        return false;
    }


    /**
     * @param player
     * @return
     */
    private boolean isPlayerMuted( Player player, Channel channel )
    {
        return channel.isMuted( player );
    }
}