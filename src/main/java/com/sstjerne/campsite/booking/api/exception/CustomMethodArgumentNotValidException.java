package com.sstjerne.campsite.booking.api.exception;

public class CustomMethodArgumentNotValidException extends Exception {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CustomMethodArgumentNotValidException() {
        super();
    }

    public CustomMethodArgumentNotValidException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CustomMethodArgumentNotValidException(final String message) {
        super(message);
    }

    public CustomMethodArgumentNotValidException(final Throwable cause) {
        super(cause);
    }

}
