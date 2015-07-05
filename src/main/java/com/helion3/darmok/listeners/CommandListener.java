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
package com.helion3.darmok.listeners;

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.message.CommandEvent;
import org.spongepowered.api.text.Texts;

import com.helion3.darmok.Darmok;
import com.helion3.darmok.channels.Channel;
import com.helion3.darmok.exceptions.JoinChannelException;
import com.helion3.darmok.utils.Format;

public class CommandListener {
    /**
     *
     * @param event
     */
    @Subscribe
    public void onCommandEvent(CommandEvent event){
        if (!(event.getSource() instanceof Player)) return;

        Player player = (Player) event.getSource();

        String cmdArgs[] = event.getArguments().split(" ");
        String primaryCmd = cmdArgs[0].trim().toLowerCase().replace("/", "");

        // Does channel exist?
        Channel channel = Darmok.getChannelRegistry().getChannel( primaryCmd );
        if( channel != null ){

           // Are they in the channel?
           if( ! Darmok.getPlayerRegistry().getPlayerChannels( player ).inChannel( channel ) ){
               Darmok.getLogger().debug("Trying to auto-join player to " + channel.getName());

               try {
                   Darmok.getPlayerRegistry().getPlayerChannels( player ).joinChannel( channel );
               } catch (JoinChannelException e) {
                   player.sendMessage(Format.error(e.getMessage()));
                   event.setCancelled(true);
                   return;
               }

               player.sendMessage(Format.subduedHeading("Auto-joining channel " + channel.getName() + "..." ));
           }

           // if message actually sent
           if( cmdArgs.length > 1 ){

               String[] messageArgs = new String[(cmdArgs.length - 1)];
               for(int i = 1; i < cmdArgs.length; i++ ){
                   messageArgs[ (i-1) ] = cmdArgs[i];
               }

               String message = StringUtils.join(messageArgs, " ");

               // Chat!
               Darmok.getChatter().send(player, channel, Texts.of(message));

           } else {
               Darmok.getLogger().debug("Setting " + player.getName() + "'s default channel to " + channel.getName());
               if( Darmok.getPlayerRegistry().getPlayerChannels( player ).setDefault( channel ) ){
                   player.sendMessage(Format.heading("Default channel switched to " + channel.getName()));
               } else {
                   player.sendMessage(Format.error("Failed setting channel as default. Are you allowed?"));
               }
           }

           event.setCancelled(true);
           return;

        }
        // it's not our command, ignore it
    }
}
