/**
 * 
 */
package com.vsthost.gp.rbinding;

/**
 * Indicates that there was a problem with the parsing and evaluation of an expression.
 * 
 * @author vst
 */
@SuppressWarnings("serial")
public class RBindingParseAndEvalException extends Exception {
	/**
	 * @param message
	 * @param cause
	 */
	public RBindingParseAndEvalException(String message, Throwable cause) {
		super(message, cause);
	}
}
