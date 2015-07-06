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
package com.helion3.darmok.channels;

import java.util.Hashtable;
import java.util.Map.Entry;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.TextMessageException;

public class Channel {
    private final String name;
    private final String command;
    private final String color;
    private final String format;
    private final int range;
    private final String context;
    private boolean isDefault = false;
    private boolean isMuted = false;

    /**
     *
     * @param command
     */
    public Channel(String name, String command, String color, String format, int range, String context) {
        this.name = name;
        this.command = command;
        this.color = color;
        this.format = format;
        this.range = range;
        this.context = context;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public String getCommand() {
        return command;
    }

    /**
     *
     * @return
     */
    public String getColor() {
        return color;
    }

    /**
     *
     * @return
     */
    public String getFormat() {
        return format;
    }

    /**
     *
     * @return
     */
    public int getRange() {
        return range;
    }

    /**
     *
     * @return
     */
    public String getContext() {
        return context;
    }

    /**
     *
     * @return
     */
    public boolean isDefault() {
        return isDefault;
    }

    /**
     *
     * @return
     */
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     *
     * @return
     */
    public void setMuted(boolean isMuted) {
        this.isMuted = isMuted;
    }

    /**
     *
     * @return
     */
    public boolean isMuted() {
        return isMuted;
    }

    /**
     *
     * @param msg
     * @return
     * @throws TextMessageException
     */
    @SuppressWarnings("deprecation")
    public Text formatMessage(Player player, Text msg) throws TextMessageException {
        Hashtable<String, String> tokens = new Hashtable<String, String>();
        tokens.put("color", color);
        tokens.put("command", command);
        tokens.put("msg", Texts.toPlain(msg));
        tokens.put("player", player.getName());

        // Not used currently
        tokens.put("prefix", "");
        tokens.put("suffix", "");

        return Texts.legacy().from(getString(format, tokens));
    }

    /**
     *
     * @param key
     * @param replacer
     * @return
     */
    protected String getString(String msg, Hashtable<String, String> replacer) {
        if (msg != null && !replacer.isEmpty()) {
            for (Entry<String, String> entry : replacer.entrySet()) {
                msg = msg.replace("%(" + entry.getKey() + ")", entry.getValue());
            }
        }
        return msg;
    }

    /**
     *
     */
    @Override
    public Channel clone() throws CloneNotSupportedException {
        return (Channel) super.clone();
    }
}