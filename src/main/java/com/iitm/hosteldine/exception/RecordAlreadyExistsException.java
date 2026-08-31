package com.iitm.hosteldine.exception;

public class RecordAlreadyExistsException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default Constructor.
	 */
	public RecordAlreadyExistsException() {
	}

	/**
	 * @param msg - message.
	 */
	public RecordAlreadyExistsException(String msg) {
		super(msg);
	}
}
