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