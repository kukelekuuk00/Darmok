package me.botsko.darmok.channels;

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
	public Channel getChannel( String command ){
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
