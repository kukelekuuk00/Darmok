package me.botsko.darmok.commands;

import me.botsko.darmok.Darmok;
import me.botsko.darmok.channels.Channel;
import me.botsko.darmok.commandlibs.CallInfo;
import me.botsko.darmok.commandlibs.Executor;
import me.botsko.darmok.commandlibs.SubHandler;


public class ChannelCommands extends Executor {

	
	/**
	 * 
	 * @param prism
	 */
	public ChannelCommands(Darmok darmok) {
		super( darmok, "subcommand", "darmok" );
		setupCommands();
	}
	

	/**
	 * 
	 */
	private void setupCommands() {
		
//		final Darmok darmok = (Darmok) plugin;
	
		/**
		 * /ch join 
		 */
		addSub("join", "darmok.chat")
		.allowConsole()
		.setHandler(new SubHandler(){
            public void handle(CallInfo call){
            	
            	if( call.getArgs().length != 2 ){
            		call.getPlayer().sendMessage( Darmok.messenger.playerError( "You must provide a channel to join, like /ch leave g" ) );
            		return;
            	}
            	
            	// Get the channel
            	Channel channel = Darmok.getChannelRegistry().getChannel( call.getArg(1) );
            	if( channel == null ){
            		call.getPlayer().sendMessage( Darmok.messenger.playerError( "Channel '"+call.getArg(1)+"' does not exist." ) );
            		return;
            	}
            	
            	if( ! Darmok.getPlayerRegistry().getPlayerChannels( call.getPlayer() ).addChannel( channel ) ){
            		call.getPlayer().sendMessage( Darmok.messenger.playerError( "You may not join this channel." ) );
            		return;
            	}
            	
            	call.getPlayer().sendMessage( Darmok.messenger.playerHeaderMsg( "Joined "+channel.getName()+" channel." ) );
        		return;
            	
            	// @todo alert other players in this channel that they joined
            	
            }
		});
		
		
		/**
		 * /ch leave 
		 */
		addSub("leave", "darmok.chat")
		.allowConsole()
		.setHandler(new SubHandler(){
            public void handle(CallInfo call){
            	
            	if( call.getArgs().length != 2 ){
            		call.getPlayer().sendMessage( Darmok.messenger.playerError( "You must provide a channel to leave, like /ch leave g" ) );
            		return;
            	}
            	
            	// Get the channel
            	Channel channel = Darmok.getChannelRegistry().getChannel( call.getArg(1) );
            	if( channel == null ){
            		call.getPlayer().sendMessage( Darmok.messenger.playerError( "Channel '"+call.getArg(1)+"' does not exist." ) );
            		return;
            	}
            	
            	if( ! Darmok.getPlayerRegistry().getPlayerChannels( call.getPlayer() ).leaveChannel( channel ) ){
            		call.getPlayer().sendMessage( Darmok.messenger.playerError( "You may not leave this channel." ) );
            		return;
            	}
            	
            	call.getPlayer().sendMessage( Darmok.messenger.playerHeaderMsg( "You have left "+channel.getName()+" channel." ) );
        		return;
            	
            	// @todo alert other players in this channel that they left
            	
            }
		});
		
		
//		/**
//		 * /ch mute 
//		 */
//		addSub("mute", "darmok.chat")
//		.allowConsole()
//		.setHandler(new SubHandler(){
//            public void handle(CallInfo call){
//            	
//            	if( call.getArgs().length != 3 ){
//            		call.getPlayer().sendMessage( Darmok.messenger.playerError( "You must provide a player name and channel, like /ch mute viveleroi g" ) );
//            		return;
//            	}
//            	
//            	// @todo verify we know the player
//            	
//            	// Get the channel
//            	Channel channel = Darmok.getChannelRegistry().getChannel( call.getArg(2) );
//            	if( channel == null ){
//            		call.getPlayer().sendMessage( Darmok.messenger.playerError( "Channel '"+call.getArg(1)+"' does not exist." ) );
//            		return;
//            	}
//            	
//            	//@todo mute the player for the channel, but directly in settings so they can be offline
//            	
//            	call.getPlayer().sendMessage( Darmok.messenger.playerHeaderMsg( "Player has been muted in "+channel.getName()+" channel." ) );
//        		return;
//            	
//            	// @todo alert other players in this channel that they left
//            	
//            }
//		});

		
		/**
		 * /darmok help
		 */
		addSub( new String[]{"help","?"}, "darmok.help")
		.allowConsole()
		.setHandler(new SubHandler() {
            public void handle(CallInfo call) {
            	call.getSender().sendMessage( Darmok.messenger.playerHeaderMsg( "Welcome to Darmok - By Viveleroi" ) );
            	call.getSender().sendMessage( Darmok.messenger.playerHelp("join","(channel)", "Join a channel."));
            	call.getSender().sendMessage( Darmok.messenger.playerHelp("leave","(channel)", "Leave a channel."));
            }
		});
	}
}