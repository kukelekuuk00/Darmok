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
package com.helion3.darmok.chatter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;

public class Censor {
    protected HashMap<String,String> leet = new HashMap<String,String>();
    private final List<String> rejectWords;
    private final List<String> censorWords;


    /**
     *
     * @param plugin
     * @throws SQLException
     */
    public Censor( List<String> rejectWords, List<String> censorWords ){
        this.rejectWords = rejectWords;
        this.censorWords = censorWords;

        // Build some basic "leetspeak" conversion
        leet.put("1", "l");
        leet.put("1", "i");
        leet.put("2", "z");
        leet.put("2", "r");
        leet.put("3", "e");
        leet.put("4", "h");
        leet.put("4", "a");
        leet.put("5", "s");
        leet.put("6", "g");
        leet.put("7", "l");
        leet.put("8", "a");
        leet.put("9", "p");
        leet.put("9", "g");
        leet.put("0", "o");
        leet.put("13", "b");
        leet.put("44", "m");
    }

    /**
     *
     * @param msg
     * @return
     */
    public Text filterCaps(Text msg, int minLength, int capsPercent ){
        String msgString = Texts.toPlain(msg);
        if( msgString.length() < minLength ){
            return msg;
        }
        if( capsPercentage(msgString) > capsPercent ){
            msgString = msgString.toLowerCase();
        }
        return Texts.of(msgString);
    }

    /**
     *
     * @param msg
     * @return
     */
    protected double capsPercentage(String msg){
        int count = 0;
        for (char ch : msg.toCharArray()){
            if (Character.isUpperCase(ch)){
                count++;
            }
        }
        return (count > 0 ? (( (double)count / (double)msg.length()) * 100) : 100);
    }

    /**
     *
     * @param msg
     * @return
     */
    protected boolean containsSuspectedProfanity(Text msg){
        // ensure lower case
        String _tmp = Texts.toPlain(msg).toLowerCase();

        // replace all invalid characters
        _tmp = _tmp.replaceAll("[^a-z0-9]", "");

        // get possible leet versions
        List<String> variations = convertLeetSpeak(_tmp);

        for(String variation : variations){
            // scan for illegal words
            for(String w : rejectWords){
                if(variation.contains(w)){
                    return true;
                }
            }
        }

        return false;
    }

    /**
     *
     * @return
     */
    public Text replaceCensoredWords(Text msg){
        String msgString = Texts.toPlain(msg);
        for(String w : censorWords){
            msgString = msgString.replaceAll("(?i)"+w, "*****");
        }
        return Texts.of(msgString);
    }

    /**
     *
     * @param msg
     * @return
     */
    protected List<String> convertLeetSpeak( String msg ){
        // Begin list of all variations, including original
        List<String> _variations = new ArrayList<String>();
        _variations.add(msg);

        for (Entry<String, String> entry : leet.entrySet()){
            if(msg.contains( entry.getKey() )){
                // entry.getValue()
                _variations.add( msg.replace(entry.getKey(), entry.getValue()) );
            }
        }

        return _variations;
    }
}
