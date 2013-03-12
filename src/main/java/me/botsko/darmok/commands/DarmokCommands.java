package me.botsko.darmok.commands;

import me.botsko.darmok.Darmok;
import me.botsko.darmok.commandlibs.CallInfo;
import me.botsko.darmok.commandlibs.Executor;
import me.botsko.darmok.commandlibs.SubHandler;


public class DarmokCommands extends Executor {

	
	/**
	 * 
	 * @param prism
	 */
	public DarmokCommands(Darmok darmok) {
		super( darmok, "subcommand", "darmok" );
		setupCommands();
	}
	

	/**
	 * 
	 */
	private void setupCommands() {
		
//		final Darmok darmok = (Darmok) plugin;
	
		
//		/**
//		 * /darmok 
//		 */
//		addSub("", "darmok")
//		.allowConsole()
//		.setHandler(new SubHandler() {
//            public void handle(CallInfo call){
//            	
//            	
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
            	call.getSender().sendMessage( Darmok.messenger.playerHelp("order","[quant] [item] [price]", "Order [quant] items for payment of [price]."));
            	call.getSender().sendMessage( Darmok.messenger.playerHelp("order","cancel [id]", "Cancel an order that has no delivery"));
            	call.getSender().sendMessage( Darmok.messenger.playerHelp("order","deliver [id]", "Deliver items requested by an order."));
            	call.getSender().sendMessage( Darmok.messenger.playerHelp("order","claim [id]", "Claim delivered items if you were offline"));
            	call.getSender().sendMessage( Darmok.messenger.playerHelp("order","list", "List all unfulfilled orders"));
            }
		});
	}
}