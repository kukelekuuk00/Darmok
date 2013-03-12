package me.botsko.darmok.channels;

import java.util.Hashtable;
import java.util.Map.Entry;

public class Channel {

	private final String name;
	private final String command;
	private final String color;
	private final String format;
	
	
	/**
	 * 
	 * @param command
	 */
	public Channel( String name, String command, String color, String format ){
		this.name = name;
		this.command = command;
		this.color = color;
		this.format = format;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getName(){
		return name;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getCommand(){
		return command;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getColor(){
		return color;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getFormat(){
		return format;
	}
	
	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	public String formatMessage( String msg ){
		Hashtable<String,String> headVal = new Hashtable<String,String>();
		headVal.put("command", command );
		headVal.put("msg", msg );
		return getString( format, headVal );
	}
	
	
	/**
	 * 
	 * @param key
	 * @param replacer
	 * @return
	 */
	protected String getString( String msg, Hashtable<String,String> replacer ){
		if( msg != null && !replacer.isEmpty() ){
			for (Entry<String,String> entry : replacer.entrySet()){
			    msg = msg.replace("%("+entry.getKey()+")", entry.getValue());
			}
		}
		return colorize( msg );
	}
	
	
	/**
	 * Converts colors place-holders.
	 * @param text
	 * @return
	 */
	protected String colorize(String text){
        String colorized = text.replaceAll("(&([a-f0-9A-F]))", "\u00A7$2");
        return colorized;
    }
}