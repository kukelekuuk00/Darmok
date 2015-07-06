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
package com.helion3.darmok;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.Types;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.config.DefaultConfig;

import com.google.inject.Inject;
import com.helion3.darmok.channels.Channel;
import com.helion3.darmok.channels.ChannelRegistry;
import com.helion3.darmok.chatter.Censor;
import com.helion3.darmok.chatter.Chatter;
import com.helion3.darmok.commands.ChannelCommands;
import com.helion3.darmok.listeners.CommandListener;
import com.helion3.darmok.listeners.PlayerChatListener;
import com.helion3.darmok.listeners.PlayerJoinListener;
import com.helion3.darmok.listeners.PlayerQuitListener;
import com.helion3.darmok.players.PlayerChannels;
import com.helion3.darmok.players.PlayerRegistry;

@Plugin(id = "Darmok", name = "Darmok", version = "2.0")
final public class Darmok {
    private static Censor censor;
    private static ChannelRegistry channelRegistry = new ChannelRegistry();
    private static Chatter chatter;
    private static Configuration config;
    private static File parentDir;
    private static Game game;
    private static Logger logger;
    private static PlayerRegistry playerRegistry = new PlayerRegistry();

    @Inject
    @DefaultConfig(sharedRoot = false)
    private File defaultConfig;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;

    /**
     * Bootstrap
     * @param event Server started
     */
    @Subscribe
    public void onServerStart(ServerStartedEvent event) {
        // Game reference
        game = event.getGame();

        // Load configuration file
        config = new Configuration(defaultConfig, configManager);
        parentDir = defaultConfig.getParentFile();

        // Init censor
        List<String> rejectWords = new ArrayList<String>();
        List<String> censorWords = new ArrayList<String>();
        try {
            ConfigurationNode node = getCensorConfig();
            rejectWords = node.getNode("reject-words").getList(Types::asString);
            censorWords = node.getNode("censor-words").getList(Types::asString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        censor = new Censor(rejectWords, censorWords);

        // Init vars
        chatter = new Chatter();

        // Register commands
        game.getCommandDispatcher().register(this, ChannelCommands.getCommand(game), "ch", "channel");

        // Register event listeners
        game.getEventManager().register(this, new CommandListener());
        game.getEventManager().register(this, new PlayerChatListener());
        game.getEventManager().register(this, new PlayerJoinListener());
        game.getEventManager().register(this, new PlayerQuitListener());

        // Register channels
        registerChannels();

        loadChannelsForAllPlayers();

        logger.info("Darmok active. His eyes open.");
    }

    /**
     * Returns the plugin configuration
     *
     * @return Configuration
     */
    public static Configuration getConfig() {
        return config;
    }

    /**
     * Returns the current game
     *
     * @return Game
     */
    public static Game getGame() {
        return game;
    }

    /**
     * Returns the Logger instance for this plugin.
     *
     * @return Logger instance
     */
    public static Logger getLogger() {
        return logger;
    }

    /**
     * Injects the Logger instance for this plugin
     *
     * @param log Logger
     */
    @Inject
    private void setLogger(Logger log) {
        logger = log;
    }

    /**
     *
     * @return
     */
    public static ChannelRegistry getChannelRegistry() {
        return channelRegistry;
    }

   /**
     *
     */
    public static Chatter getChatter() {
        return chatter;
    }

   /**
     *
     * @return
     */
    public static PlayerRegistry getPlayerRegistry() {
        return playerRegistry;
    }

   /**
     *
     * @return
     */
    public static Censor getCensor() {
        return censor;
    }

    /**
     * Loads profanity config.
     * @return
     * @throws IOException
     */
    private CommentedConfigurationNode getCensorConfig() throws IOException {
        File profanityConf = new File(parentDir.getAbsolutePath() + "/profanity.conf");

        if (!profanityConf.exists()) {
            URL jarConfigFile = this.getClass().getResource("/com/helion3/darmok/profanity.conf");
            return HoconConfigurationLoader.builder().setURL(jarConfigFile).build().load();
        } else {
            return HoconConfigurationLoader.builder().setFile(profanityConf).build().load();
        }
    }

    /**
     *
     */
    private void registerChannels() {
        ConfigurationNode channels = getConfig().getNode("channels");
        String format = getConfig().getNode("channel", "default-format").getString();

        Map<Object, ? extends ConfigurationNode> children = channels.getChildrenMap();
        for (Object key : children.keySet()) {
            if (!(key instanceof String)) {
                continue;
            }

            ConfigurationNode channel = children.get(key);

            channelRegistry.registerChannel(new Channel((String) key, channel.getNode("command").getString(), channel
                    .getNode("color").getString(), (channel.getNode("format").getString().isEmpty() ? format : channel
                    .getNode("format").getString()), channel.getNode("range").getInt(), channel.getNode("context")
                    .getString()));
        }
    }

    /**
     * Load existing yaml channel settings for a player when they join, or when
     * the server reloads.
     *
     * @param player
     */
    public static void loadChannelSettingsForPlayer(Player player) {
        PlayerConfiguration playerConfig = new PlayerConfiguration(parentDir, player.getUniqueId());

        // Restore channel subscriptions
        List<String> channelAliases = playerConfig.getNode("channels").getList(Types::asString);
        for (String alias : channelAliases) {
            Channel channel = Darmok.getChannelRegistry().getChannel(alias);
            if (channel != null) {
                getPlayerRegistry().getPlayerChannels(player).addChannel(channel);
            }
        }

        // Load default
        String defaultAlias = playerConfig.getNode("default").getString();
        Channel c = getChannelRegistry().getChannel(defaultAlias);
        if (c != null) {
            getPlayerRegistry().getPlayerChannels(player).setDefault(c);
        }

        // Restore channel bans
        List<String> bannedIn = playerConfig.getNode("banned-in").getList(Types::asString);
        // If player was banned in this channel, restore that
        if (bannedIn != null && !bannedIn.isEmpty()) {
            for (String channelAlias : bannedIn) {
                Darmok.getPlayerRegistry().setChannelBanForPlayer(player, channelAlias);
            }
        }

        // re-save just in case server crashes, etc
        saveChannelSettingsForPlayer(player);
    }

    /**
     *
     * @param player
     */
    public static Channel resetDefaultChannelForPlayer(Player player) {
        ArrayList<Channel> channels = Darmok.getChannelRegistry().getChannels();
        if (!channels.isEmpty()) {
            for (Channel c : channels) {
                if (getConfig().getNode("channels", c.getName(), "default").getBoolean()) {
                    getPlayerRegistry().getPlayerChannels(player).setDefault(c);
                    return c;
                }
            }
        }
        return null;
    }

    /**
     * Build a new player channel settings save file based on current channels.
     */
    public static void saveChannelSettingsForPlayer(Player player) {
        PlayerConfiguration playerConfig = new PlayerConfiguration(parentDir, player.getUniqueId());

        // Save current channels
        PlayerChannels playerChannels = getPlayerRegistry().getPlayerChannels(player);
        if (!playerChannels.getChannels().isEmpty()) {
            ArrayList<String> channelAliases = new ArrayList<String>();
            ArrayList<Channel> channels = playerChannels.getChannels();
            if (!channels.isEmpty()) {
                for (Channel c : channels) {
                    channelAliases.add(c.getCommand());
                }
            }
            playerConfig.getNode("channels").setValue(channelAliases);
        }

        playerConfig.getNode("default").setValue(playerChannels.getDefault().getCommand());

        // Set their channel bans
        ArrayList<String> bannedIn = getPlayerRegistry().getChannelBansForPlayer(player);
        if (bannedIn != null && !bannedIn.isEmpty()) {
            playerConfig.getNode("banned-in").setValue(bannedIn);
        }

        playerConfig.save();
    }

    /**
     *
     * @param player
     */
    public static void unloadChannelSettingsForPlayer(Player player) {
        getPlayerRegistry().removePlayer(player);
    }

    /**
     * Load all channels for any online players (on reload)
     */
    public static void loadChannelsForAllPlayers() {
        for (Player pl : game.getServer().getOnlinePlayers()) {
            loadChannelSettingsForPlayer(pl);
        }
    }

    /**
     * Save and unload all channels for any online players
     */
    public static void unloadChannels() {
        for (Player pl : game.getServer().getOnlinePlayers()) {
            saveChannelSettingsForPlayer(pl);
            unloadChannelSettingsForPlayer(pl);
        }
    }

    @Subscribe
    public void onServerStop(ServerStoppingEvent event) {
        unloadChannels();
    }
}
