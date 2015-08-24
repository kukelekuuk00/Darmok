package com.radthorne.fancychat;

import com.radthorne.fancychat.channels.Channel;
import com.radthorne.fancychat.channels.ChannelPermissions;
import com.radthorne.fancychat.channels.ChannelRegistry;
import com.radthorne.fancychat.chatter.Censor;
import com.radthorne.fancychat.chatter.Chatter;
import com.radthorne.fancychat.commandlibs.CommandHandler;
import com.radthorne.fancychat.commandlibs.SubCommand;
import com.radthorne.fancychat.commands.ChannelCommand;
import com.radthorne.fancychat.commands.ChannelSubCommands.*;
import com.radthorne.fancychat.commands.FancyChatCommand;
import com.radthorne.fancychat.commands.FancyChatSubCommands.reload;
import com.radthorne.fancychat.exceptions.ChannelPermissionException;
import com.radthorne.fancychat.exceptions.JoinChannelException;
import com.radthorne.fancychat.listeners.PlayerListener;
import com.radthorne.fancychat.metrics.Metrics;
import com.radthorne.fancychat.players.PlayerChannels;
import com.radthorne.fancychat.players.PlayerRegistry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class FancyChat extends JavaPlugin
{

    //TODO add /ch who
    //TODO add /msg
    //TODO add /r
    //TODO twitter style messaging
    //TODO re-do messaging system

    /**
     * Protected/private
     */
    private String plugin_name;
    private String plugin_version;
    public static Logger log;
    private static ChannelRegistry channelRegistry;
    private static Chatter chatter;
    private static PlayerRegistry playerRegistry;
    private static Censor censor;
    private FileConfiguration profanity;
    private FileConfiguration channels;
    private static FancyChat instance;

    /**
     * Public
     */
    public static Messenger messenger;
    public static Config configHandler;

    public static FancyChat getInstance()
    {
        return instance;
    }


    /**
     * Enables the plugin and activates our player listeners
     */
    @Override
    public void onEnable()
    {
        log = getLogger();
        plugin_name = this.getDescription().getName();
        plugin_version = this.getDescription().getVersion();

        instance = this;
        saveDefaultConfig();
        log( "Initializing " + plugin_name + " " + plugin_version + "." );

        // Load configuration, or install if new
        loadConfig();

        try
        {
            Metrics metrics = new Metrics( this );
            metrics.start();
        }
        catch ( IOException e )
        {
            log( "MCStats submission failed." );
        }

        if ( isEnabled() )
        {

            channelRegistry = new ChannelRegistry();
            chatter = new Chatter( this );
            playerRegistry = new PlayerRegistry();
            messenger = new Messenger( plugin_name );
            // Register channels
            registerChannels();

            // Assign event listeners
            getServer().getPluginManager().registerEvents( new PlayerListener( this ), this );
            setupCommands();

            loadChannelsForAllPlayers();
        }
    }

    private void setupCommands()
    {
        CommandHandler handler = new CommandHandler();
        handler.RegisterCommand( new FancyChatCommand(), Collections.<SubCommand>singletonList( new reload() ) );
        handler.RegisterCommand( new ChannelCommand(), Arrays.asList(
                new ban(),
                new force(),
                new join(),
                new kick(),
                new leave(),
                new list(),
                new mute(),
                new unban()
        ) );
        getCommand( "fancychat" ).setExecutor( handler );
        getCommand( "channel" ).setExecutor( handler );
    }


    /**
     * Load configuration and language files
     */
    @SuppressWarnings( "unchecked" )
    public void loadConfig()
    {
        configHandler = new Config( this );
        profanity = configHandler.getProfanityConfig();
        channels = configHandler.getChannelConfig();
        censor = new Censor( (List<String>) profanity.getList( "reject-words" ), (List<String>) profanity.getList( "censor-words" ) );
    }

    /**
     *
     */
    private void registerChannels()
    {

        Set<String> channelKeys = channels.getKeys( false );
        for ( String channelName : channelKeys )
        {

            ConfigurationSection channel = channels.getConfigurationSection( channelName );
            if ( channel == null )
            {
                continue;
            }

            String format = getConfig().getString( "fancychat.channel.default-format" );

            channelRegistry.registerChannel(
                    new Channel(
                            channelName,
                            channel.getString( "command" ),
                            channel.getString( "color" ),
                            ( channel.getString( "format" ).isEmpty() ? format : channel.getString( "format" ) ),
                            channel.getInt( "range" ),
                            channel.getString( "context" )
                    ) );
        }
    }


    /**
     * @return
     */
    public static ChannelRegistry getChannelRegistry()
    {
        return channelRegistry;
    }


    /**
     *
     */
    public static Chatter getChatter()
    {
        return chatter;
    }


    /**
     * @return
     */
    public static PlayerRegistry getPlayerRegistry()
    {
        return playerRegistry;
    }


    /**
     * @return
     */
    public static Censor getCensor()
    {
        return censor;
    }


    /**
     * Load existing yaml channel settings for a player
     * when they join, or when the server reloads.
     *
     * @param player
     */
    public void loadChannelSettingsForPlayer( Player player )
    {

        FileConfiguration playerConfig = configHandler.loadPlayerConfig( player );

        // They have no config - set the default channels
        if ( playerConfig == null )
        {

            // Load all channels
            ArrayList<Channel> channels = getChannelRegistry().getChannels();
            for ( Channel channel : channels )
            {
                // Can the player join this?

                try
                {
                    ChannelPermissions.playerCanAutoJoin( player, channel );
                }
                catch ( ChannelPermissionException e )
                {
                    continue;
                }

                debug( "Creating first join in channel " + channel.getShortName() );

                // Register the channel for this player
                try
                {
                    getPlayerRegistry().getPlayerChannels( player ).joinChannel( channel );
                }
                catch ( JoinChannelException e )
                {
                    continue;
                }

                // Set as the default channel
                if ( getConfig().getString( "fancychat.channel.default-channel" ).equals( channel.getShortName() ) )
                {
                    debug( "Setting default channel to " + channel.getName() );
                    getPlayerRegistry().getPlayerChannels( player ).setFocused( channel );
                }
            }
        }
        else
        {

            // Restore channel subscriptions
            @SuppressWarnings( "unchecked" )
            ArrayList<String> channelAliases = (ArrayList<String>) playerConfig.getList( "channels" );
            for ( String alias : channelAliases )
            {
                Channel channel = FancyChat.getChannelRegistry().getChannel( alias );
                if ( channel != null )
                {
                    getPlayerRegistry().getPlayerChannels( player ).addChannel( channel );
                }
            }

            // Load default
            String defaultAlias = playerConfig.getString( "default" );
            debug( "Setting default channel to " + defaultAlias );
            Channel c = getChannelRegistry().getChannel( defaultAlias );
            if ( c != null )
            {
                getPlayerRegistry().getPlayerChannels( player ).setFocused( c );
            }
            // @todo throw an error and fix this

            // Restore channel bans
            @SuppressWarnings( "unchecked" )
            ArrayList<String> bannedIn = (ArrayList<String>) playerConfig.getList( "banned-in" );
            // If player was banned in this channel, restore that
            if ( bannedIn != null && !bannedIn.isEmpty() )
            {
                for ( String channelAlias : bannedIn )
                {
                    FancyChat.getChannelRegistry().getChannel( channelAlias ).ban( player );
                }
            }
        }
    }


    /**
     * @param player
     */
    public Channel resetDefaultChannelForPlayer( Player player )
    {
        ArrayList<Channel> channels = FancyChat.getChannelRegistry().getChannels();
        if ( !channels.isEmpty() )
        {
            for ( Channel c : channels )
            {
                if ( getConfig().getBoolean( "fancychat.channels." + c.getName() + ".default" ) )
                {
                    debug( "Resetting " + player.getName() + "'s default channel to " + c.getName() );
                    getPlayerRegistry().getPlayerChannels( player ).setFocused( c );
                    return c;
                }
            }
        }
        return null;
    }


    /**
     * Build a new player channel settings save file based on
     * current channels.
     */
    public void saveChannelSettingsForPlayer( Player player )
    {

        FileConfiguration playerConfig = new YamlConfiguration();

        // Save current channels
        PlayerChannels playerChannels = getPlayerRegistry().getPlayerChannels( player );
        if ( !playerChannels.getChannels().isEmpty() )
        {
            ArrayList<String> channelAliases = new ArrayList<String>();
            ArrayList<Channel> channels = playerChannels.getChannels();
            if ( !channels.isEmpty() )
            {
                for ( Channel c : channels )
                {
                    channelAliases.add( c.getShortName() );
                }
            }
            playerConfig.set( "channels", channelAliases );
        }

        playerConfig.set( "default", playerChannels.getFocused().getShortName() );

        // Set their channel bans
        ArrayList<String> bannedIn = getPlayerRegistry().getChannelBansForPlayer( player );
        if ( bannedIn != null && !bannedIn.isEmpty() )
        {
            debug( "Saving ban setting in " + bannedIn );
            playerConfig.set( "banned-in", bannedIn );
        }

        try
        {
            playerConfig.save( this.getDataFolder() + "/players/" + player.getName() + ".yml" );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }


    /**
     * @param player
     */
    public static void unloadChannelSettingsForPlayer( Player player )
    {
        getPlayerRegistry().removePlayer( player );
    }

    /**
     * @param message
     */
    public void log( String message )
    {
        log.info( "[" + plugin_name + "]: " + message );
    }


    /**
     * @param messages
     */
    public void logSection( String[] messages )
    {
        if ( messages.length > 0 )
        {
            log( "--------------------- ## Important ## ---------------------" );
            for ( String msg : messages )
            {
                log( msg );
            }
            log( "--------------------- ## ========= ## ---------------------" );
        }
    }


    /**
     * @param message
     */
    public static void debug( String message )
    {
        if ( configHandler.getConfig().getBoolean( "fancychat.debug" ) )
        {
            log.info( message );
        }
    }


    /**
     * Disable the plugin
     */
    public void disablePlugin()
    {
        this.setEnabled( false );
    }


    /**
     *
     */
    public void loadChannelsForAllPlayers()
    {
        // Load all channels for any online players (on reload)
        for ( Player pl : getServer().getOnlinePlayers() )
        {
            loadChannelSettingsForPlayer( pl );
        }
    }


    /**
     *
     */
    public void unloadChannels()
    {
        // Save and unload all channels for any online players
        for ( Player pl : getServer().getOnlinePlayers() )
        {
            saveChannelSettingsForPlayer( pl );
            unloadChannelSettingsForPlayer( pl );
        }
    }


    /**
     * Shutdown
     */
    @Override
    public void onDisable()
    {
        unloadChannels();
        this.log( "Closing plugin." );
    }


    public CaselessArrayList getChannelAliases()
    {
        CaselessArrayList list = new CaselessArrayList();
        for ( Channel channel : channelRegistry.getChannels() )
        {
            list.add( channel.getName() );
            list.add( channel.getShortName() );
        }
        return list;
    }
}
