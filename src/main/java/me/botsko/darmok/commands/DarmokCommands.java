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
		
		final Darmok darmok = (Darmok) plugin;
	
		
		/**
		 * /darmok reload
		 */
		addSub("reload", "prism.reload")
		.allowConsole()
		.setHandler(new SubHandler() {
            public void handle(CallInfo call){
            	darmok.unloadChannels();
            	darmok.reloadConfig();
            	Darmok.config = plugin.getConfig();
            	darmok.loadChannelsForAllPlayers();
				call.getSender().sendMessage( Darmok.messenger.playerHeaderMsg("Configuration reloaded successfully.") );
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
            	call.getSender().sendMessage( Darmok.messenger.playerHelp("darmok","reload","Configuration reloaded successfully."));
            }
		});
	}
}