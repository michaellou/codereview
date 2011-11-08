/**
 * 
 */
package com.dianping.codereview.maven;

/**
 * @author sean.wang
 * @since Nov 3, 2011
 */
public class MavenPomFormatInvalidException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7511746132325375172L;

	/**
	 * 
	 */
	public MavenPomFormatInvalidException() {
	}

	/**
	 * @param message
	 */
	public MavenPomFormatInvalidException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public MavenPomFormatInvalidException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MavenPomFormatInvalidException(String message, Throwable cause) {
		super(message, cause);
	}

}
