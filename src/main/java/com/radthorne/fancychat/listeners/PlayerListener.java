package com.radthorne.fancychat.listeners;

import com.radthorne.fancychat.FancyChat;
import com.radthorne.fancychat.channels.Channel;
import com.radthorne.fancychat.exceptions.JoinChannelException;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener
{

    protected FancyChat plugin;


    /**
     * @param plugin
     */
    public PlayerListener( FancyChat plugin )
    {
        this.plugin = plugin;
    }


    /**
     * @param event
     */
    @EventHandler( priority = EventPriority.LOW )
    public void onPlayerJoin( final PlayerJoinEvent event )
    {
        Player player = event.getPlayer();
        plugin.loadChannelSettingsForPlayer( player );
    }


    /**
     * @param event
     */
    @EventHandler( priority = EventPriority.LOW )
    public void onPlayerQuit( final PlayerQuitEvent event )
    {
        plugin.saveChannelSettingsForPlayer( event.getPlayer() );
        FancyChat.unloadChannelSettingsForPlayer( event.getPlayer() );
    }


    /**
     * @param event
     */
    @EventHandler( priority = EventPriority.LOWEST )
    public void onPlayerCommandPreprocess( PlayerCommandPreprocessEvent event )
    {

        Player player = event.getPlayer();

        String cmdArgs[] = event.getMessage().split( "\\ " );
        String primaryCmd = cmdArgs[0].trim().toLowerCase().replace( "/", "" );

        // Does channel exist?
        Channel channel = FancyChat.getChannelRegistry().getChannel( primaryCmd );
        if ( channel != null )
        {

            // Are they in the channel?
            if ( !FancyChat.getPlayerRegistry().getPlayerChannels( player ).inChannel( channel ) )
            {
                plugin.debug( "Trying to auto-join player to " + channel.getName() );

                try
                {
                    FancyChat.getPlayerRegistry().getPlayerChannels( player ).joinChannel( channel );
                }
                catch ( JoinChannelException e )
                {
                    player.sendMessage( FancyChat.messenger.playerError( e.getMessage() ) );
                    event.setCancelled( true );
                    return;
                }

                player.sendMessage( FancyChat.messenger.playerSubduedHeaderMsg( "Auto-joining channel " + channel.getName() + "..." ) );
            }

            // if message actually sent
            if ( cmdArgs.length > 1 )
            {

                String[] messageArgs = new String[( cmdArgs.length - 1 )];
                for ( int i = 1; i < cmdArgs.length; i++ )
                {
                    messageArgs[( i - 1 )] = cmdArgs[i];
                }

                String message = StringUtils.join( messageArgs, " " );

                // Chat!
                FancyChat.getChatter().send( player, channel, message );
            }
            else
            {
                plugin.debug( "Setting " + player.getName() + "'s default channel to " + channel.getName() );
                if ( FancyChat.getPlayerRegistry().getPlayerChannels( player ).setFocused( channel ) )
                {
                    player.sendMessage( FancyChat.messenger.playerHeaderMsg( "Default channel switched to " + channel.getName() ) );
                }
                else
                {
                    player.sendMessage( FancyChat.messenger.playerError( "Failed setting channel as default. Are you allowed?" ) );
                }
            }

            event.setCancelled( true );
            return;
        }
        // it's not our command, ignore it
    }


    /**
     * @param event
     */
    @EventHandler( priority = EventPriority.LOW )
    public void onPlayerChat( AsyncPlayerChatEvent event )
    {

        Player player = event.getPlayer();

        // Get the current default channel
        Channel channel = FancyChat.getPlayerRegistry().getPlayerChannels( player ).getFocused();

        // Reset their default
        if ( channel == null )
        {
            channel = plugin.resetDefaultChannelForPlayer( player );
        }

        if ( channel != null )
        {
            plugin.debug( "Found default channel " + channel.getName() + " for " + player.getName() );
            FancyChat.getChatter().send( player, channel, event.getMessage() );
            event.setCancelled( true );
        }
    }
}
