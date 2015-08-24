package com.radthorne.fancychat.exceptions;

public class LeaveChannelException extends Exception
{
    /**
     *
     */
    private static final long serialVersionUID = -7124771485626925739L;

    /**
     * @param message
     */
    public LeaveChannelException( String message )
    {
        super( message );
    }
}
