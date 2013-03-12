package me.botsko.darmok.channels;

import java.util.HashMap;

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
	public Channel getChannel( String command ){
		return channels.get(command);
	}

	
	/**
	 * 
	 * @param c
	 */
	public void registerChannel( Channel c ){
		channels.put(c.getCommand(), c);
	}
}
