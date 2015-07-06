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

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class Configuration {
    private ConfigurationNode rootNode = null;

    /**
     * Loads (creates new if needed) Prism configuration file.
     * 
     * @param defaultConfig
     * @param configManager
     */
    public Configuration(File defaultConfig, ConfigurationLoader<CommentedConfigurationNode> configManager) {
        try {
            // If file does not exist, we must create it
            if (!defaultConfig.exists()) {
                defaultConfig.getParentFile().mkdirs();
                defaultConfig.createNewFile();
                rootNode = configManager.createEmptyNode(ConfigurationOptions.defaults());
                Darmok.getLogger().info("Creating new config at mods/Oracle/Oracle.conf");
            } else {
                rootNode = configManager.load();
            }

            ConfigurationNode defaultFormat = rootNode.getNode("channel", "default-format");
            if (defaultFormat.isVirtual()) {
                defaultFormat.setValue("%(color)[%(command)] %(prefix)§f%(player)%(suffix)%(color): %(msg)");
            }

            ConfigurationNode defaultChannel = rootNode.getNode("channel", "default");
            if (defaultChannel.isVirtual()) {
                defaultChannel.setValue("g");
            }

            // Caps censor
            ConfigurationNode censorCaps = rootNode.getNode("censor", "caps", "enabled");
            if (censorCaps.isVirtual()) {
                censorCaps.setValue(false);
            }

            ConfigurationNode censorCapsLength = rootNode.getNode("censor", "caps", "min-length");
            if (censorCapsLength.isVirtual()) {
                censorCapsLength.setValue(15);
            }

            ConfigurationNode censorCapsPerc = rootNode.getNode("censor", "caps", "min-percentage");
            if (censorCapsPerc.isVirtual()) {
                censorCapsPerc.setValue(30);
            }

            // Profanity censor
            ConfigurationNode profanity = rootNode.getNode("censor", "profanity", "enabled");
            if (profanity.isVirtual()) {
                profanity.setValue(true);
            }

            // Channels

            // Global
            ConfigurationNode gCmd = rootNode.getNode("channels", "global", "command");
            if (gCmd.isVirtual()) {
                gCmd.setValue("g");
            }

            ConfigurationNode gRange = rootNode.getNode("channels", "global", "range");
            if (gRange.isVirtual()) {
                gRange.setValue(-1);
            }

            ConfigurationNode gColor = rootNode.getNode("channels", "global", "color");
            if (gColor.isVirtual()) {
                gColor.setValue("§6");
            }

            ConfigurationNode gFormat = rootNode.getNode("channels", "global", "format");
            if (gFormat.isVirtual()) {
                gFormat.setValue("");
            }

            // Save
            try {
                configManager.save(rootNode);
            } catch (IOException e) {
                // @todo handle properly
                e.printStackTrace();
            }
        } catch (IOException e) {
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
