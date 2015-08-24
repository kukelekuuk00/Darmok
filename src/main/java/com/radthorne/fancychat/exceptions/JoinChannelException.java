package com.radthorne.fancychat.exceptions;

public class JoinChannelException extends Exception
{

    /**
     *
     */
    private static final long serialVersionUID = -39403326180873341L;

    /**
     * @param message
     */
    public JoinChannelException( String message )
    {
        super( message );
    }
}
