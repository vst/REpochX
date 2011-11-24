package com.vsthost.gp.rbinding;

import java.lang.reflect.InvocationTargetException;	

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngine;
import org.rosuda.REngine.REngineException;

/**
 * Provides a singleton REngine binding and related convenience methods.
 * 
 * @author vst
 */
public class RBinding {
	/**
	 * The RBinding instance which is the singleton. 
	 */
    private static RBinding _instance;
    
	/**
	 * The REngine.
	 */
	private REngine rEngine;
	
	/**
	 * The R version string.
	 */
	private String rVersion;
	
	/**
	 * Provides a private constructor to prevent instantiation of RBinding classes from other classes.
	 * @throws RBindingClassNotFoundException 
	 * @throws RBindingNoSuchMethodException 
	 * @throws RBindingIllegalAccessException 
	 * @throws RBindingInvocationTargetException 
	 * @throws RBindingREngineException 
	 * @throws RBindingREXPMismatchException 
	 */
	private RBinding() throws RBindingClassNotFoundException, RBindingNoSuchMethodException, RBindingIllegalAccessException, RBindingInvocationTargetException, RBindingREngineException, RBindingREXPMismatchException {
		// Check if an REngine has been created before. If not,
		// attempt to create one and return it.
		if(REngine.getLastEngine() == null) {
			try {
				// Attempt to instantiate the REngine: 
				this.rEngine = REngine.engineForClass("org.rosuda.REngine.JRI.JRIEngine");
				
				// Get the R version:
				this.rVersion = rEngine.parseAndEval("version$version.string").asString();
			} catch (ClassNotFoundException e) {
				throw new RBindingClassNotFoundException();
			} catch (NoSuchMethodException e) {
				throw new RBindingNoSuchMethodException();
			} catch (IllegalAccessException e) {
				throw new RBindingIllegalAccessException();
			} catch (InvocationTargetException e) {
				throw new RBindingInvocationTargetException();
			} catch (REngineException e) {
				throw new RBindingREngineException();
			} catch (REXPMismatchException e) {
				throw new RBindingREXPMismatchException();
			}
		}
	}
	
	/**
	 * @return the rEngine
	 */
	public REngine getREngine() {
		return rEngine;
	}

	/**
	 * @param rEngine the rEngine to set
	 */
	public void setREngine(REngine rEngine) {
		this.rEngine = rEngine;
	}

	/**
	 * @return the rVersion
	 */
	public String getRVersion() {
		return rVersion;
	}

	/**
	 * @param rVersion the rVersion to set
	 */
	public void setRVersion(String rVersion) {
		this.rVersion = rVersion;
	}	
	
	/**
	 * Parses and evaluates the given string.
	 * 
	 * @param expression the expression to be parsed and evaluated.
	 * @return the return value of the evaluation.
	 * @throws RBindingParseAndEvalException 
	 */
	public REXP evalExpr(String expression) throws RBindingParseAndEvalException {
		try {
			return this.rEngine.parseAndEval(expression);
		} catch (REngineException e) {
			throw new RBindingParseAndEvalException(expression, e);
		} catch (REXPMismatchException e) {
			throw new RBindingParseAndEvalException(expression, e);
		}
	}
	
	/**
	 * Parses, evaluates and assigns the given string to the given symbol.
	 * 
	 * @param symbol the symbol to assign to.
	 * @param expression the expression to be parsed and evaluated.
	 * @throws RBindingParseAndEvalException 
	 * @throws RBindingAssignmentException 
	 */
	public void assignEvalExpr(String symbol, String expression) throws RBindingParseAndEvalException {
		this.evalExpr(symbol + " = eval(parse(text=\"" + expression.replace("\"", "\\\"") + "\"))");
	}
	
	/**
	 * Loads a library provided as a string.
	 * @throws RBindingLibraryLoadException 
	 */
	public void loadLibrary(String libraryName) throws RBindingLibraryLoadException {
		try {
			this.evalExpr("library(" + libraryName + ")");
		} catch (RBindingParseAndEvalException e) {
			throw new RBindingLibraryLoadException(libraryName, e);
		}
	}
	
	/**
	 * Loads a script of which its path provided as a string.
	 * @throws RBindingScriptLoadException 
	 */
	public void loadScript(String scriptPath) throws RBindingScriptLoadException {
		try {
			this.evalExpr("source(\"" + scriptPath + "\")");
		} catch (RBindingParseAndEvalException e) {
			throw new RBindingScriptLoadException(scriptPath, e);
		}
	}
	
	/**
	 * Closes the REngine if any available.
	 */
	public static void closeREngine() {
		// Check if any REngine has been initialized yet:
		if (REngine.getLastEngine() != null) {
			// Close the REngine:
			REngine.getLastEngine().close();
		}
	}

	/**
	 * Returns the RBinding singleton instance.
	 * 
	 * @return RBinding singleton instance.
	 * @throws RBindingInvocationTargetException 
	 * @throws RBindingIllegalAccessException 
	 * @throws RBindingNoSuchMethodException 
	 * @throws RBindingClassNotFoundException 
	 * @throws RBindingREXPMismatchException 
	 * @throws RBindingREngineException 
	 */
	public static synchronized RBinding getInstance() throws RBindingClassNotFoundException, RBindingNoSuchMethodException, RBindingIllegalAccessException, RBindingInvocationTargetException, RBindingREngineException, RBindingREXPMismatchException {
            if (_instance == null) {
                    _instance = new RBinding();
            }
            return _instance;
    }
}