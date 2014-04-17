package me.botsko.darmok.link;

import me.botsko.darmok.channels.Channel;

public interface DarmokUser {
    
    public void sendMessage( String message );
    
    public boolean hasPermission( String node );
    
    public void banFromChannel( DarmokUser source, Channel channel );
    
    public void unbanFromChannel( DarmokUser source, Channel channel );
    
    public String getName();

}