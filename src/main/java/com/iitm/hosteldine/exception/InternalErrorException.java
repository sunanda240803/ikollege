package com.iitm.hosteldine.exception;

public class InternalErrorException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default Constructor.
	 */
	public InternalErrorException() {
	}

	/**
	 * @param msg - message.
	 */
	public InternalErrorException(String msg) {
		super(msg);
	}
}
