package com.vsthost.gp.rbinding;

/**
 * Indicates that there was a problem while loading a script.
 * 
 * @author vst
 */
@SuppressWarnings("serial")
public class RBindingScriptLoadException extends Exception {
	public RBindingScriptLoadException(String message, Throwable cause) {
		super(message, cause);
	}
}
