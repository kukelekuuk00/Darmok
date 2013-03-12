package me.botsko.darmok.channels;

import java.util.Hashtable;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

public class Channel {

	private final String name;
	private final String command;
	private final String color;
	private final String format;
	private final int range;
	
	private boolean isDefault = false;
	
	
	/**
	 * 
	 * @param command
	 */
	public Channel( String name, String command, String color, String format, int range ){
		this.name = name;
		this.command = command;
		this.color = color;
		this.format = format;
		this.range = range;
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
	 * @return
	 */
	public int getRange(){
		return range;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public boolean isDefault(){
		return isDefault;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public void setDefault( boolean isDefault ){
		this.isDefault = isDefault;
	}
	
	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	public String formatMessage( Player player, String msg ){
		Hashtable<String,String> headVal = new Hashtable<String,String>();
		headVal.put("command", command );
		headVal.put("msg", msg );
		headVal.put("player", player.getDisplayName() );
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