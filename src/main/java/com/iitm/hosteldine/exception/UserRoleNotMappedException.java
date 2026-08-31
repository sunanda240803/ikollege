package com.iitm.hosteldine.exception;

public class UserRoleNotMappedException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default Constructor.
     */
    public UserRoleNotMappedException() {
    }

    /**
     * @param msg - message.
     */
    public UserRoleNotMappedException(String msg) {
        super(msg);
    }
}
