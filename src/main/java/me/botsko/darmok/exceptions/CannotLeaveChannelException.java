package me.botsko.darmok.exceptions;

public class CannotLeaveChannelException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7124771485626925739L;

	/**
	 * 
	 * @param message
	 */
	public CannotLeaveChannelException(String message) {
        super(message);
    }
}
