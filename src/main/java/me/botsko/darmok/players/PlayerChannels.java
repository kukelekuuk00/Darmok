package me.botsko.darmok.players;

import java.util.HashMap;
import java.util.Map.Entry;

import me.botsko.darmok.channels.Channel;

public class PlayerChannels {
	
	
	/**
	 * 
	 */
	private HashMap<String,Channel> channels = new HashMap<String,Channel>();
	
	
	/**
	 * 
	 * @param command
	 * @return
	 */
	public Channel getChannel( String command ){
		return channels.get(command);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public HashMap<String,Channel> getChannels(){
		return channels;
	}

	
	/**
	 * 
	 * @param c
	 */
	public void addChannel( Channel c ){
		channels.put(c.getCommand(), c);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public Channel getDefault(){
		for (Entry<String,Channel> entry : channels.entrySet()){
		    if( entry.getValue().isDefault() ){
		    	return entry.getValue();
		    }
		}
		return null;
	}
}