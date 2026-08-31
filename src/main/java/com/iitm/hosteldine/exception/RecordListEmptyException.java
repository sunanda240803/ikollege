package com.iitm.hosteldine.exception;

public class RecordListEmptyException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default Constructor.
	 */
	public RecordListEmptyException() {
	}

	/**
	 * @param msg - message.
	 */
	public RecordListEmptyException(String msg) {
		super(msg);
	}
}
