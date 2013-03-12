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
		 * /ch leave 
		 */
		addSub("leave", "darmok.chat")
		.allowConsole()
		.setHandler(new SubHandler() {
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
		 * /darmok help
		 */
		addSub( new String[]{"help","?"}, "darmok.help")
		.allowConsole()
		.setHandler(new SubHandler() {
            public void handle(CallInfo call) {
            	call.getSender().sendMessage( Darmok.messenger.playerHeaderMsg( "Welcome to Darmok - By Viveleroi" ) );
            	call.getSender().sendMessage( Darmok.messenger.playerHelp("order","[quant] [item] [price]", "Order [quant] items for payment of [price]."));
            	call.getSender().sendMessage( Darmok.messenger.playerHelp("order","cancel [id]", "Cancel an order that has no delivery"));
            	call.getSender().sendMessage( Darmok.messenger.playerHelp("order","deliver [id]", "Deliver items requested by an order."));
            	call.getSender().sendMessage( Darmok.messenger.playerHelp("order","claim [id]", "Claim delivered items if you were offline"));
            	call.getSender().sendMessage( Darmok.messenger.playerHelp("order","list", "List all unfulfilled orders"));
            }
		});
	}
}