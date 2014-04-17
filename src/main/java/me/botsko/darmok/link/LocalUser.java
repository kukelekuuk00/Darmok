package me.botsko.darmok.link;

import me.botsko.darmok.channels.Channel;

import org.bukkit.command.CommandSender;

public class LocalUser implements DarmokUser {
    
    final CommandSender sender;
    
    /**
     * 
     * @param sender
     */
    public LocalUser( final CommandSender sender ){
        if( sender == null ){
            throw new IllegalArgumentException("DarmokUser may not contain a null sender");
        }
        this.sender = sender;
    }
    
    
    /**
     * 
     * @param message
     */
    public void sendMessage( String message ){
        sender.sendMessage( message );
    }
    
    
    /**
     * 
     */
    public boolean hasPermission( String node ){
        return sender.hasPermission( node );
    }
    
    
    /**
     * 
     * @return
     */
    public CommandSender getSender(){
        return sender;
    }


    /**
     * 
     */
    public void banFromChannel(DarmokUser source, Channel channel) {
        // TODO Auto-generated method stub
        
    }


    /**
     * 
     */
    public void unbanFromChannel(DarmokUser source, Channel channel) {
        // TODO Auto-generated method stub
        
    }

    
    /**
     * 
     */
    public String getName() {
        return sender.getName();
    }
}