package com.radthorne.fancychat.exceptions;

public class ChannelPermissionException extends Exception
{

    /**
     *
     */
    private static final long serialVersionUID = -7461136511835347393L;

    /**
     * @param message
     */
    public ChannelPermissionException( String message )
    {
        super( message );
    }
}
