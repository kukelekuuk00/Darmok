package me.botsko.darmok.commands;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import me.botsko.darmok.Darmok;
import me.botsko.darmok.channels.Channel;
import me.botsko.darmok.channels.ChannelPermissions;
import me.botsko.darmok.commandlibs.CallInfo;
import me.botsko.darmok.commandlibs.Executor;
import me.botsko.darmok.commandlibs.SubHandler;
import me.botsko.darmok.players.PlayerChannels;


public class ChannelCommands extends Executor {

	
	/**
	 * 
	 * @param prism
	 */
	public ChannelCommands(Darmok darmok){
		super( darmok, "subcommand", "darmok" );
		setupCommands();
	}
	

	/**
	 * 
	 */
	private void setupCommands(){
		
		final Darmok darmok = (Darmok) plugin;
		
		
		/**
		 * /ch ban (player) (channel) 
		 */
		addSub("ban", "darmok.chat")
		.allowConsole()
		.setHandler(new SubHandler(){
            public void handle(CallInfo call){

            	if( call.getArgs().length != 3 ){
            		call.getPlayer().sendMessage( Darmok.messenger.playerError( "You must provide a player name and channel, like /ch ban viveleroi g" ) );
            		return;
            	}
            	
            	// Get the player
            	Player player = darmok.getServer().getPlayer( call.getArg(1) );
            	if( player == null ){
            		call.getPlayer().sendMessage( Darmok.messenger.playerError( "Can't find a player with that name." ) );
            		return;
            	}
            	
            	// Get the channel
            	Channel channel = Darmok.getChannelRegistry().getChannel( call.getArg(2) );
            	if( channel == null ){
            		call.getPlayer().sendMessage( Darmok.messenger.playerError( "Channel '"+call.getArg(1)+"' does not exist." ) );
            		return;
            	}
            	
            	// Do they have permission to ban
            	if( !ChannelPermissions.playerCanBan( call.getPlayer(), channel ) ){
            		call.getPlayer().sendMessage( Darmok.messenger.playerError( "You do not have permission to ban players from this channel." ) );
            		return;
            	}
            	
            	
            	Darmok.getPlayerRegistry().banFromChannel( player, channel );
            	
            	player.sendMessage( Darmok.messenger.playerError( "You have been banned from the "+channel.getName()+" channel." ) );
            	call.getPlayer().sendMessage( Darmok.messenger.playerHeaderMsg( "You have banned "+player.getName()+" from the "+channel.getName()+" channel." ) );
            	
            	// @todo alert other players in this channel that they left
            	
            }
		});
		
	
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
            	
            	if( ! Darmok.getPlayerRegistry().getPlayerChannels( call.getPlayer() ).joinChannel( channel ) ){
            		call.getPlayer().sendMessage( Darmok.messenger.playerError( "You may not join this channel." ) );
            		return;
            	}
            	
            	call.getPlayer().sendMessage( Darmok.messenger.playerHeaderMsg( "Joined "+channel.getName()+" channel." ) );
        		return;
            	
            	// @todo alert other players in this channel that they joined
            	
            }
		});
		
		
		/**
		 * /ch kick (player) (channel) 
		 */
		addSub("kick", "darmok.chat")
		.allowConsole()
		.setHandler(new SubHandler(){
            public void handle(CallInfo call){

            	if( call.getArgs().length != 3 ){
            		call.getPlayer().sendMessage( Darmok.messenger.playerError( "You must provide a player name and channel, like /ch kick viveleroi g" ) );
            		return;
            	}
            	
            	// Get the player
            	Player player = darmok.getServer().getPlayer( call.getArg(1) );
            	if( player == null ){
            		call.getPlayer().sendMessage( Darmok.messenger.playerError( "Can't find a player with that name." ) );
            		return;
            	}
            	
            	// Get the channel
            	Channel channel = Darmok.getChannelRegistry().getChannel( call.getArg(2) );
            	if( channel == null ){
            		call.getPlayer().sendMessage( Darmok.messenger.playerError( "Channel '"+call.getArg(1)+"' does not exist." ) );
            		return;
            	}
            	
            	// Do they have permission to kick
            	if( !ChannelPermissions.playerCanKick( call.getPlayer(), channel ) ){
            		call.getPlayer().sendMessage( Darmok.messenger.playerError( "You do not have permission to kick players from this channel." ) );
            		return;
            	}
            	
            	Darmok.getPlayerRegistry().getPlayerChannels( player ).removeChannel( channel );
            	
            	player.sendMessage( Darmok.messenger.playerError( "You have been kicked from the "+channel.getName()+" channel." ) );
            	call.getPlayer().sendMessage( Darmok.messenger.playerHeaderMsg( "You have kicked "+player.getName()+" from the "+channel.getName()+" channel." ) );
            	
            	// @todo alert other players in this channel that they left
            	
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
		
		
		/**
		 * /ch list 
		 */
		addSub("list", "darmok.list")
		.allowConsole()
		.setHandler(new SubHandler(){
            public void handle(CallInfo call){
            	
            	Player limitTo = null;
            	if( call.getArgs().length == 2 && call.getArg(1).equals("mine") ){
            		limitTo= call.getPlayer();
            	}
            	
            	ArrayList<Channel> channels;
            	
            	// Load the channels
            	if( limitTo != null ){
            		PlayerChannels playerChannels = Darmok.getPlayerRegistry().getPlayerChannels( limitTo );
    				if( playerChannels == null ){
    					call.getPlayer().sendMessage( Darmok.messenger.playerError( "This player has no active channel subscriptions." ) );
                		return;
    				}
    				channels = playerChannels.getChannels();
            	} else {
            		channels = Darmok.getChannelRegistry().getChannels();
            	}
            	
            	if( channels.isEmpty() ){
            		call.getPlayer().sendMessage( Darmok.messenger.playerError( "There are no channels." ) );
            		return;
            	}
            	
            	// List them
            	call.getPlayer().sendMessage( Darmok.messenger.playerHeaderMsg( "-- All Channels --" ) );
            	for ( Channel c : channels ){
            		
            		boolean youreBanned = Darmok.getPlayerRegistry().isPlayerBannedFromChannel(call.getPlayer(), c);
        
            		String list = c.getColor() + c.getName() + " /" + c.getCommand() + " &f" + (youreBanned ? "You're Banned" : "");
            		list += " &7Read: " + ( ChannelPermissions.playerCanRead(call.getPlayer(), c) ? "&aY" : "&cN" ) ;
            		list += " &7Speak: " + ( ChannelPermissions.playerCanSpeak(call.getPlayer(), c) ? "&aY" : "&cN" ) ;
            		
            		call.getPlayer().sendMessage( Darmok.messenger.playerMsg( c.colorize( list ) ) );
            		
            	}
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
		 * /ch unban (player) (channel) 
		 */
		addSub("unban", "darmok.chat")
		.allowConsole()
		.setHandler(new SubHandler(){
            public void handle(CallInfo call){

            	if( call.getArgs().length != 3 ){
            		call.getPlayer().sendMessage( Darmok.messenger.playerError( "You must provide a player name and channel, like /ch unban viveleroi g" ) );
            		return;
            	}
            	
            	// Get the player
            	Player player = darmok.getServer().getPlayer( call.getArg(1) );
            	if( player == null ){
            		call.getPlayer().sendMessage( Darmok.messenger.playerError( "Can't find a player with that name." ) );
            		return;
            	}
            	
            	// Get the channel
            	Channel channel = Darmok.getChannelRegistry().getChannel( call.getArg(2) );
            	if( channel == null ){
            		call.getPlayer().sendMessage( Darmok.messenger.playerError( "Channel '"+call.getArg(1)+"' does not exist." ) );
            		return;
            	}
            	
            	// Do they have permission to unban
            	if( !ChannelPermissions.playerCanBan( call.getPlayer(), channel ) ){
            		call.getPlayer().sendMessage( Darmok.messenger.playerError( "You do not have permission to unban players from this channel." ) );
            		return;
            	}
            	
            	Darmok.getPlayerRegistry().unbanFromChannel( player, channel );

            	player.sendMessage( Darmok.messenger.playerError( "You have been unbanned from the "+channel.getName()+" channel." ) );
            	call.getPlayer().sendMessage( Darmok.messenger.playerHeaderMsg( "You have unbanned "+player.getName()+" from the "+channel.getName()+" channel." ) );
            	
            }
		});

		
		/**
		 * /darmok help
		 */
		addSub( new String[]{"help","?"}, "darmok.help")
		.allowConsole()
		.setHandler(new SubHandler() {
            public void handle(CallInfo call) {
            	call.getSender().sendMessage( Darmok.messenger.playerHeaderMsg( "Welcome to Darmok - By Viveleroi" ) );
            	call.getSender().sendMessage( Darmok.messenger.playerHelp("ch join","(channel)", "Join a channel."));
            	call.getSender().sendMessage( Darmok.messenger.playerHelp("ch leave","(channel)", "Leave a channel."));
            	call.getSender().sendMessage( Darmok.messenger.playerHelp("ch kick","(user) (channel)", "Kick a player from a channel."));
            	call.getSender().sendMessage( Darmok.messenger.playerHelp("ch ban","(user) (channel)", "Ban a player from a channel."));
            	call.getSender().sendMessage( Darmok.messenger.playerHelp("ch unban","(user) (channel)", "Unban a player from a channel."));
            	call.getSender().sendMessage( Darmok.messenger.playerHelp("ch list","", "List channels"));
            }
		});
	}
}