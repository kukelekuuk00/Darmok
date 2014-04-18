package me.botsko.darmok.link;

import me.botsko.darmok.channels.Channel;

public class RemoteUser implements DarmokUser {
    
    private final String identityName;
    
    
    /**
     * 
     * @param ident
     */
    public RemoteUser( String identityName ){
        this.identityName = identityName;
    }
    
    
    /**
     * 
     */
    public void sendMessage( String message ){
//        DarmokClient.out.println("EMSG " + this.uid + " " + arg0);
//        DarmokClient.out.flush();
//        System.out.println("DEBUG: sending from " + this.uid + ": " + arg0);
    }
    
    
    /**
     * 
     */
    public boolean hasPermission( String node ){
        return true;
    }
    
    
    public void banFromChannel( DarmokUser source, Channel channel ){
        DarmokClient.out.println(String.format("CBAN %s@%s %s %s", source.getName(), DarmokClient.ident, this.identityName, channel.getCommand()));
        DarmokClient.out.flush();
    }


    /**
     * 
     * @param source
     * @param channel
     */
    public void unbanFromChannel( DarmokUser source, Channel channel ){
        DarmokClient.out.println(String.format("CUNBAN %s@%s %s %s", source.getName(), DarmokClient.ident, this.identityName, channel.getCommand()));
        DarmokClient.out.flush();
    }
//
//    /**
//     * 
//     * @param channel
//     * @param msg
//     */
//    public void writeToChannel( DarmokUser source, Channel channel, String msg ){
//        DarmokClient.out.println(String.format("CMSG %s@%s %s %s", source.getName(), DarmokClient.ident, channel.getCommand(), msg));
//        DarmokClient.out.flush();
//    }


    /**
     * 
     */
    public String getName() {
        return identityName;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( identityName == null ) ? 0 : identityName.hashCode() );
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
        RemoteUser other = (RemoteUser) obj;
        if( identityName == null ) {
            if( other.identityName != null )
                return false;
        } else if( !identityName.equals( other.identityName ) )
            return false;
        return true;
    }
}