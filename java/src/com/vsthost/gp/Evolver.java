package com.vsthost.gp;

import java.io.FileNotFoundException;	
import java.io.IOException;
import java.util.logging.Logger;

import org.epochx.gr.model.GRModel;
import org.epochx.gr.representation.GRCandidateProgram;
import org.epochx.life.GenerationAdapter;
import org.epochx.life.Life;
import org.epochx.life.RunAdapter;
import org.epochx.representation.CandidateProgram;
import org.epochx.stats.StatField;
import org.epochx.stats.Stats;
import org.epochx.tools.grammar.Grammar;
import org.rosuda.REngine.REXPMismatchException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.vsthost.gp.rbinding.RBinding;
import com.vsthost.gp.rbinding.RBindingClassNotFoundException;
import com.vsthost.gp.rbinding.RBindingIllegalAccessException;
import com.vsthost.gp.rbinding.RBindingInvocationTargetException;
import com.vsthost.gp.rbinding.RBindingLibraryLoadException;
import com.vsthost.gp.rbinding.RBindingNoSuchMethodException;
import com.vsthost.gp.rbinding.RBindingParseAndEvalException;
import com.vsthost.gp.rbinding.RBindingREXPMismatchException;
import com.vsthost.gp.rbinding.RBindingREngineException;
import com.vsthost.gp.rbinding.RBindingScriptLoadException;

/**
 * Provides the main entry of the evolution process along with the Main function. 
 * 
 * @author vst
 */
public class Evolver extends GRModel {
	/**
	 * The logger.
	 */
    private static Logger logger = Logger.getLogger(Evolver.class.getName());
	
	/**
	 * The configuration of the entire evolution system.
	 */
	Configuration configuration;
	
	/**
	 * The RBinding to be used throughout the evolution.
	 */
	RBinding rBinding;
	
	/**
	 * The name of the function to be invoked on evolution start.
	 */
	private static final String OnEvolutionStart = ".onEvolutionStart";
	
	/**
	 * The name of the function to be invoked on evolution end.
	 */
	private static final String OnEvolutionEnd = ".onEvolutionEnd";
	
	/**
	 * The name of the function to be invoked on run start.
	 */
	private static final String OnRunStart = ".onRunStart";
	
	/**
	 * The name of the function to be invoked on run end.
	 */
	private static final String OnRunEnd = ".onRunEnd";
	
	/**
	 * The name of the function to be invoked on generation start.
	 */
	private static final String OnGenerationStart = ".onGenerationStart";
	
	/**
	 * The name of the function to be invoked on generation end.
	 */
	private static final String OnGenerationEnd = ".onGenerationEnd";
	
	/**
	 * The name of the function to be invoked for fitness calculation.
	 */
	private static final String FitnessFunction = ".getFitness";
	
	/**
	 * Constructs the Evolve object with the given configuration file path.
	 *	
	 * @param rBinding 
	 * @param configFilepath
	 * @throws JsonIOException 
	 * @throws JsonSyntaxException 
	 * @throws ConfigFileNotFoundException 
	 * @throws GrammarFileNotFoundException 
	 * @throws IOException 
	 * @throws RBindingLibraryLoadException 
	 * @throws RBindingScriptLoadException 
	 * @throws RBindingParseAndEvalException 
	 */
	public Evolver(RBinding rBinding, String configFilepath) throws JsonSyntaxException, JsonIOException, IOException, GrammarFileNotFoundException, ConfigFileNotFoundException, RBindingLibraryLoadException, RBindingScriptLoadException, RBindingParseAndEvalException {
		// set the RBinding:
		this.rBinding = rBinding;
		
		// Configure the evolution system:
		this.configure(configFilepath);
		
		// Load the necessary libraries:
		for (String library : this.configuration.getLibraries()) {
			this.rBinding.loadLibrary(library);
		}
		
		// Load the necessary scripts:
		for (String script : this.configuration.getScripts()) {
			this.rBinding.loadScript(script);
		}
	}
	
	/**
	 * Reads in the configuration file, prepares and sets the configuration slot.
	 * 
	 * @param configFilepath
	 * @throws JsonIOException 
	 * @throws JsonSyntaxException 
	 * @throws ConfigFileNotFoundException 
	 * @throws GrammarFileNotFoundException 
	 * @throws IOException 
	 * @throws RBindingAssignmentException 
	 * @throws RBindingParseAndEvalException 
	 */
	void configure(String configFilepath) throws JsonSyntaxException, JsonIOException, IOException, GrammarFileNotFoundException, ConfigFileNotFoundException, RBindingParseAndEvalException {
		// Read the configuration:
		this.configuration = Configuration.fromJsonFile(configFilepath);
		
		// Set the GRModel specific parameters:
		this.setCrossoverProbability(this.configuration.getProbCrossover());
		this.setMutationProbability(this.configuration.getProbMutation());
		this.setReproductionProbability(this.configuration.getProbReproduction());
		this.setTerminationFitness(this.configuration.getTerminationFitness());
		this.setNoElites(this.configuration.getSizeElites());
		this.setPoolSize(this.configuration.getSizePool());
		this.setPopulationSize(this.configuration.getSizePopulation());
		this.setNoGenerations(this.configuration.getNoOfGenerations());
		this.setNoRuns(this.configuration.getNoOfRuns());
		this.setMaxInitialDepth(this.configuration.getMaxInitDepth());
		this.setMaxDepth(this.configuration.getMaxDepth());
		this.setGrammar(new Grammar(this.configuration.getGrammar()));
				
		// Set the methods to be invoked as slots:
		this.rBinding.assignEvalExpr(Evolver.OnEvolutionStart, this.configuration.getOnEvolutionStart());
		this.rBinding.assignEvalExpr(Evolver.OnEvolutionEnd, this.configuration.getOnEvolutionEnd());
		this.rBinding.assignEvalExpr(Evolver.OnRunStart, this.configuration.getOnRunStart());
		this.rBinding.assignEvalExpr(Evolver.OnRunEnd, this.configuration.getOnRunEnd());
		this.rBinding.assignEvalExpr(Evolver.OnGenerationStart, this.configuration.getOnGenerationStart());
		this.rBinding.assignEvalExpr(Evolver.OnGenerationEnd, this.configuration.getOnGenerationEnd());
		this.rBinding.assignEvalExpr(Evolver.FitnessFunction, this.configuration.getFitnessFunction());
	}

	/**
	 * Slot to be invoked on evolution start.
	 * 
	 * @param configuration
	 */
	public void onEvolutionStart(String configuration) {
		String toEval = Evolver.OnEvolutionStart + "('" + configuration + "')";
		try {
			this.rBinding.evalExpr(toEval);
		} catch (RBindingParseAndEvalException e) {
			Evolver.errorExit("Cannot start evolution.\n" + toEval + "\nExiting...");
		}
	}
	
	/**
	 * Slot to be invoked on evolution end.
	 * 
	 * @param configuration
	 */
	public void onEvolutionEnd(String fittestProgram) {
		try {
			this.rBinding.evalExpr(Evolver.OnEvolutionEnd+ "(\"" + fittestProgram + "\")");
		} catch (RBindingParseAndEvalException e) {
			Evolver.errorExit("Cannot end evolution properly. Exiting...");
		}
	}

	/**
	 * Slot to be invoked on run start.
	 * 
	 * @param configuration
	 */
	public void onRunStart(int run) {
		try {
			this.rBinding.evalExpr(Evolver.OnRunStart + "(" + run + ")");
		} catch (RBindingParseAndEvalException e) {
			Evolver.errorExit("Cannot start run. Exiting...");
		}
	}
	
	/**
	 * Slot to be invoked on run end.
	 * 
	 * @param configuration
	 */
	public void onRunEnd(int run, String fittestProgram) {
		try {
			this.rBinding.evalExpr(Evolver.OnRunEnd+ "(" + run + ", \"" + fittestProgram + "\")");
		} catch (RBindingParseAndEvalException e) {
			Evolver.errorExit("Cannot end run properly. Exiting...");
		}
	}
	
	/**
	 * Slot to be invoked on generation start.
	 * 
	 * @param configuration
	 */
	public void onGenerationStart(int generation) {
		try {
			this.rBinding.evalExpr(Evolver.OnGenerationStart + "(" + generation + ")");
		} catch (RBindingParseAndEvalException e) {
			Evolver.errorExit("Cannot start generation. Exiting...");
		}
	}
	
	/**
	 * Slot to be invoked on generation end.
	 * 
	 * @param configuration
	 */
	public void onGenerationEnd(int generation, String fittestProgram) {
		try {
			this.rBinding.evalExpr(Evolver.OnGenerationEnd+ "(" + generation + ", \"" + fittestProgram + "\")");
		} catch (RBindingParseAndEvalException e) {
			Evolver.errorExit("Cannot end run properly. Exiting...");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.epochx.core.Model#getFitness(org.epochx.representation.CandidateProgram)
	 */
	@Override
	public double getFitness(CandidateProgram program) {
		// Prepare the program string:
		String expr = Evolver.FitnessFunction + "(\"" + program.toString() + "\")";
		//System.out.println(expr);
		try {
			double fitness = this.rBinding.evalExpr(expr).asDouble();
			//System.out.println(fitness);
			return fitness;
		} catch (REXPMismatchException e) {
			logger.severe("No fitness score produced by " + expr);
		} catch (RBindingParseAndEvalException e) {
			logger.severe("Could not be evaluated: " + expr);
		}
		return(Double.POSITIVE_INFINITY);
	}
	
	/**
	 * @return the configuration
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * @param configuration the configuration to set
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Prints out the error message and exits.
	 * 
	 * @param errorMessage the error message to be shown
	 */
	public static void errorExit (String errorMessage) {
		// Print the error message.
		System.err.println(errorMessage);

		// Close the RBinding:
		RBinding.closeREngine();
		
		// Done, bye bye to JVM:
		System.exit(1);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Check the program arguments:
		if (args.length == 0) {
			System.err.println("No configuration file path provided. Exiting...");
			System.exit(1);
		}
		
		// Instantiate and set the RBinding:
		Evolver.logger.info("Initializing the R binding.");
		RBinding rBinding = null;
		try {
			rBinding = RBinding.getInstance();
		} catch (RBindingClassNotFoundException e1) {
			Evolver.errorExit("ClassNotFound exception while initializing REngine.");
		} catch (RBindingNoSuchMethodException e1) {
			Evolver.errorExit("NoSuchMethod exception while initializing REngine.");
		} catch (RBindingIllegalAccessException e1) {
			Evolver.errorExit("IllegalAccess exception while initializing REngine.");
		} catch (RBindingInvocationTargetException e1) {
			Evolver.errorExit("InvocationTarget exception while initializing REngine.");
		} catch (RBindingREngineException e1) {
			Evolver.errorExit("REngine exception while initializing REngine.");
		} catch (RBindingREXPMismatchException e1) {
			Evolver.errorExit("REXPMismatch exception while initializing REngine.");
		}
		
		System.out.print("Dneme");
		
		// Attempt to initialize end configure the evolution system:
		Evolver evolver = null;
		try {
			evolver = new Evolver(rBinding, args[0]);
		} catch (JsonSyntaxException e) {
			Evolver.errorExit("Syntax error in configuration. Exiting...");
		} catch (JsonIOException e) {
			Evolver.errorExit("Error in reading the configuration file. Exiting...");
		} catch (FileNotFoundException e) {
			Evolver.errorExit("Error in reading the configuration file. Exiting...");
		} catch (IOException e) {
			Evolver.errorExit("IO Error. Exiting...");
		} catch (GrammarFileNotFoundException e) {
			Evolver.errorExit("Grammar file not found. Exiting...");
		} catch (ConfigFileNotFoundException e) {
			Evolver.errorExit("Config file not found. Exiting...");
		} catch (RBindingLibraryLoadException e) {
			Evolver.errorExit("Cannot load the library: " + e.getMessage());
		} catch (RBindingScriptLoadException e) {
			Evolver.errorExit("Cannot load the script: " + e.getMessage());
		} catch (RBindingParseAndEvalException e) {
			Evolver.errorExit("Problem while evaluating expression: " + e.getMessage());
		}

		// Check the evolver instance:
		if (evolver == null) {
			Evolver.errorExit("Evolution system could not been setup. Exiting...");
		}
		
		// Get the final version of the evolver:
		final Evolver finalEvolver = evolver;
		
        // Set up the run listener:
        Life.get().addRunListener(new RunAdapter(){
            public void onRunStart() {
        		// Get the run number:
        		Integer runNumber = (Integer) Stats.get().getStat(StatField.RUN_NUMBER);
        		if (runNumber == null) {
        			runNumber = 1;
        		}

            	// Log:
            	Evolver.logger.info("Starting run: " + runNumber);
            	
        		// Invoke the onRunStart of the evolver:
        		finalEvolver.onRunStart(runNumber);
            }
        
            public void onRunEnd() {
        		// Get the run number:
        		Integer runNumber = (Integer) Stats.get().getStat(StatField.RUN_NUMBER);
       
        		// Extract the fittest program of the run:
        		final GRCandidateProgram program = (GRCandidateProgram) Stats.get().getStat(StatField.RUN_FITTEST_PROGRAM);
        		
            	// Log:
            	Evolver.logger.info("Ending run: " + runNumber + " [" + ((Double) Stats.get().getStat(StatField.RUN_FITNESS_MIN)) + "] " + program.toString());
            	
        		// Invoke the onRunEnd of the evolver:
        		finalEvolver.onRunEnd(runNumber, program.toString());
            }
        });

        // Set up the generation listener:
        Life.get().addGenerationListener(new GenerationAdapter(){
            public void onGenerationStart() {
        		// Get the generation number:
        		Integer generationNumber = (Integer) Stats.get().getStat(StatField.GEN_NUMBER);
        		if (generationNumber == null) {
        			generationNumber = -1;
        		}

            	// Log:
            	Evolver.logger.info("Starting generation: " + generationNumber);
            	
        		// Invoke the onRunStart of the evolver:
        		finalEvolver.onGenerationStart(generationNumber);
            }
        
            public void onGenerationEnd() {
        		// Get the generation number:
        		Integer generationNumber = (Integer) Stats.get().getStat(StatField.GEN_NUMBER);
       
        		// Extract the fittest program of the generation:
        		final GRCandidateProgram program = (GRCandidateProgram) Stats.get().getStat(StatField.GEN_FITTEST_PROGRAM);
        		
            	// Log:
            	Evolver.logger.info("Ending generation: " + generationNumber + " [" + ((Double) Stats.get().getStat(StatField.GEN_FITNESS_MIN)) + "] " + program.toString());

            	// Invoke the onRunEnd of the evolver:
        		finalEvolver.onGenerationEnd(generationNumber, program.toString());
            }
        });

        // Fire the onEvolutionStart event:
        Evolver.logger.info("Starting evolution.");
        evolver.onEvolutionStart(evolver.getConfiguration().toString());
        
		// Run the evolution:
		evolver.run();
		
        // Fire the onEvolutionEnd event:
        Evolver.logger.info("Ending evolution: " + ((GRCandidateProgram) Stats.get().getStat(StatField.RUN_FITTEST_PROGRAM)).toString());
        evolver.onEvolutionEnd(((GRCandidateProgram) Stats.get().getStat(StatField.RUN_FITTEST_PROGRAM)).toString());
        System.out.println("[" + ((Double) Stats.get().getStat(StatField.RUN_FITNESS_MIN)) + "] " + ((GRCandidateProgram) Stats.get().getStat(StatField.RUN_FITTEST_PROGRAM)).toString());
        
		// Done. Exit...
		RBinding.closeREngine();
	}
}
