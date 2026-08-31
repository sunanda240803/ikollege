package com.iitm.hosteldine.exception;

public class RecordNotExistsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default Constructor.
	 */
	public RecordNotExistsException() {
	}

	/**
	 * @param msg - message.
	 */
	public RecordNotExistsException(String msg) {
		super(msg);
	}
}
