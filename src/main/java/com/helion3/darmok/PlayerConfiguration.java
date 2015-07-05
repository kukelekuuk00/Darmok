package com.helion3.darmok;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class PlayerConfiguration {
    private ConfigurationLoader<CommentedConfigurationNode> configLoader;
    private ConfigurationNode rootNode = null;;

    public PlayerConfiguration(File parentDirectory, UUID playerID) {
        try {
            // If files do not exist, we must create them
            File playerDir = new File(parentDirectory.getAbsolutePath() + "/players");
            if (!playerDir.exists()) {
                playerDir.mkdirs();
                Darmok.getLogger().info("Creating new player config directory at mods/Darmok/players");
            }

            File playerConf = new File(playerDir.getAbsolutePath() + "/" + playerID.toString() + ".conf");
            boolean fileCreated = false;

            if (!playerConf.exists()) {
                playerConf.createNewFile();
                Darmok.getLogger().info("Creating new player config file at mods/Darmok/players/" + playerID.toString() + ".conf");
                fileCreated = true;
            }

            configLoader = HoconConfigurationLoader.builder().setFile(playerConf).build();
            if (fileCreated) {
                rootNode = configLoader.createEmptyNode(ConfigurationOptions.defaults());
            } else {
                rootNode = configLoader.load();
            }

            ConfigurationNode channels = rootNode.getNode("channels");
            if (channels.isVirtual()) {
                List<String> defaultChannels = new ArrayList<String>();
                defaultChannels.add("g"); // @todo fix from config
                channels.setValue(defaultChannels);
            }

            ConfigurationNode defaultChannel = rootNode.getNode("default");
            if (defaultChannel.isVirtual()) {
                defaultChannel.setValue("g"); // @todo fix from config
            }

            // Save
            save();
        } catch (IOException e) {
            // @todo handle properly
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            configLoader.save(rootNode);
        } catch(IOException e) {
            // @todo handle properly
            e.printStackTrace();
        }
    }

    /**
     * Shortcut to rootNode.getNode().
     *
     * @param path Object[] Paths to desired node
     * @return ConfigurationNode
     */
    public ConfigurationNode getNode(Object... path) {
        return rootNode.getNode(path);
    }
}