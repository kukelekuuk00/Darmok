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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class ChannelRegistry {

    /**
     *
     */
    private HashMap<String,Channel> channels = new HashMap<String,Channel>();

    /**
     *
     * @param command
     * @return
     */
    public Channel getChannel(String command){
        return channels.get(command);
    }

    /**
     *
     * @return
     */
    public ArrayList<Channel> getChannels(){
        ArrayList<Channel> returnChannels = new ArrayList<Channel>();
        for (Entry<String,Channel> entry : channels.entrySet()){
            returnChannels.add( entry.getValue() );
        }
        return returnChannels;
    }

    /**
     *
     * @param c
     */
    public void registerChannel( Channel c ){
        channels.put(c.getCommand(), c);
    }
}
