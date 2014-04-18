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


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( sender == null ) ? 0 : sender.hashCode() );
        return result;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if( this == obj )
            return true;
        if( obj == null )
            return false;
        if( getClass() != obj.getClass() )
            return false;
        LocalUser other = (LocalUser) obj;
        if( sender == null ) {
            if( other.sender != null )
                return false;
        } else if( !sender.equals( other.sender ) )
            return false;
        return true;
    }
}