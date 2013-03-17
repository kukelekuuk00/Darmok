package me.botsko.darmok.exceptions;

public class CannotJoinChannelException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -39403326180873341L;

	/**
	 * 
	 * @param message
	 */
	public CannotJoinChannelException(String message) {
        super(message);
    }
}
