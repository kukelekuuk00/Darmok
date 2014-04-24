package me.botsko.darmok.link;

import me.botsko.darmok.Darmok;
import me.botsko.darmok.channels.Channel;

public class LinkCommandHandler {
	
	
	/**
	 * Handles CMSG messages from clients/servers.
	 * @param args
	 */
	public static void cmsg( String[] args ){
		
		DarmokUser remoteUser = new RemoteUser(args[1]);
        
        String chatMessage = "";
        for(int i = 3; i < args.length; i++){
            chatMessage += " " + args[i];
        }
        
        Darmok.debug( "Finding channel for: " + args[2] );
        Channel channel = Darmok.getChannelRegistry().getChannel( args[2] );
        
        Darmok.getChatter().send( remoteUser, channel, chatMessage);
		
	}
	
	
	
	public static void cban( String[] args ){
		//player = new RemoteUser(args[1]);
//      new_args = new String[]{"unban", args[2].split("@")[0], args[3]};
	}
	
	public static void cunban( String[] args ){
		//player = new RemoteUser(args[1]);
//      new_args = new String[]{"unban", args[2].split("@")[0], args[3]};
	}
}