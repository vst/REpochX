package com.vsthost.gp.rbinding;

/**
 * Indicates that there was a problem while loading a library.
 * 
 * @author vst
 */
@SuppressWarnings("serial")
public class RBindingLibraryLoadException extends Exception {
	public RBindingLibraryLoadException(String message, Throwable cause) {
		super(message, cause);
	}
}
