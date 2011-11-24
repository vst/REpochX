/**
 * 
 */
package com.vsthost.gp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

/**
 * Provides a JSON compatible configuration container for the evolution.
 * 
 * @author Vehbi Sinan Tunalioglu
 */
public class Configuration {
	
	private String configFile;
	private String uuid = UUID.randomUUID().toString();
	private String name = "#N/A";
	private String description = "#N/A";
	private String loggerName = "repochx";
	private String loggerPath = "repochx.log";
	private int loggerLevel = 20;
	private String[] libraries = new String[0];
	private String[] scripts = new String[0];
	private double probCrossover = 0.80;
	private double probMutation = 0.15;
	private double probReproduction = 0.05;
	private double  terminationFitness = Double.NEGATIVE_INFINITY;
	private int sizeElites = 5;
	private int sizePool = 10000;
	private int sizePopulation = 1000;
	private int noOfGenerations = 10;
	private int noOfRuns = 1;
	private int maxInitDepth = 8;
	private int maxDepth = 16;
	private String grammarFile = "grammar.bnf";
	private String grammar;
	private String fitnessFunction;
	private String onEvolutionStart = "function (configuration) configuration";
	private String onEvolutionEnd = "function (fittestProgram) fittestProgram";
	private String onRunStart = "function (run) run";
	private String onRunEnd = "function (run, fittestProgram) fittestProgram";
	private String onGenerationStart = "function (generation) generation";
	private String onGenerationEnd = "function (generation, fittestProgram) fittestProgram";
	private Object custom = null;

	/**
	 * @return the configFile
	 */
	public String getConfigFile() {
		return configFile;
	}

	/**
	 * @param configFile the configFile to set
	 */
	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	/**
	 * @return the uuid
	 */
	public String getUUID() {
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUUID(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the loggerName
	 */
	public String getLoggerName() {
		return loggerName;
	}

	/**
	 * @param loggerName the loggerName to set
	 */
	public void setLoggerName(String loggerName) {
		this.loggerName = loggerName;
	}

	/**
	 * @return the loggerPath
	 */
	public String getLoggerPath() {
		return loggerPath;
	}

	/**
	 * @param loggerPath the loggerPath to set
	 */
	public void setLoggerPath(String loggerPath) {
		this.loggerPath = loggerPath;
	}

	/**
	 * @return the loggerLevel
	 */
	public int getLoggerLevel() {
		return loggerLevel;
	}

	/**
	 * @param loggerLevel the loggerLevel to set
	 */
	public void setLoggerLevel(int loggerLevel) {
		this.loggerLevel = loggerLevel;
	}

	/**
	 * @return the scripts
	 */
	public String[] getScripts() {
		return scripts;
	}

	/**
	 * @param scripts the scriptFilepath to set
	 */
	public void setScripts(String[] scripts) {
		this.scripts = scripts;
	}

	/**
	 * @return the probCrossover
	 */
	public double getProbCrossover() {
		return probCrossover;
	}

	/**
	 * @param probCrossover the probCrossover to set
	 */
	public void setProbCrossover(double probCrossover) {
		this.probCrossover = probCrossover;
	}

	/**
	 * @return the probMutation
	 */
	public double getProbMutation() {
		return probMutation;
	}

	/**
	 * @param probMutation the probMutation to set
	 */
	public void setProbMutation(double probMutation) {
		this.probMutation = probMutation;
	}

	/**
	 * @return the probReproduction
	 */
	public double getProbReproduction() {
		return probReproduction;
	}

	/**
	 * @param probReproduction the probReproduction to set
	 */
	public void setProbReproduction(double probReproduction) {
		this.probReproduction = probReproduction;
	}

	/**
	 * @return the terminationFitness
	 */
	public double getTerminationFitness() {
		return terminationFitness;
	}

	/**
	 * @param terminationFitness the terminationFitness to set
	 */
	public void setTerminationFitness(double terminationFitness) {
		this.terminationFitness = terminationFitness;
	}

	/**
	 * @return the sizeElites
	 */
	public int getSizeElites() {
		return sizeElites;
	}

	/**
	 * @param sizeElites the sizeElites to set
	 */
	public void setSizeElites(int sizeElites) {
		this.sizeElites = sizeElites;
	}

	/**
	 * @return the sizePool
	 */
	public int getSizePool() {
		return sizePool;
	}

	/**
	 * @param sizePool the sizePool to set
	 */
	public void setSizePool(int sizePool) {
		this.sizePool = sizePool;
	}

	/**
	 * @return the sizePopulation
	 */
	public int getSizePopulation() {
		return sizePopulation;
	}

	/**
	 * @param sizePopulation the sizePopulation to set
	 */
	public void setSizePopulation(int sizePopulation) {
		this.sizePopulation = sizePopulation;
	}

	/**
	 * @return the noOfGenerations
	 */
	public int getNoOfGenerations() {
		return noOfGenerations;
	}

	/**
	 * @param noOfGenerations the noOfGenerations to set
	 */
	public void setNoOfGenerations(int noOfGenerations) {
		this.noOfGenerations = noOfGenerations;
	}

	/**
	 * @return the maxInitDepth
	 */
	public int getMaxInitDepth() {
		return maxInitDepth;
	}

	/**
	 * @param maxInitDepth the maxInitDepth to set
	 */
	public void setMaxInitDepth(int maxInitDepth) {
		this.maxInitDepth = maxInitDepth;
	}

	/**
	 * @return the maxDepth
	 */
	public int getMaxDepth() {
		return maxDepth;
	}

	/**
	 * @param maxDepth the maxDepth to set
	 */
	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	/**
	 * @return the grammarFile
	 */
	public String getGrammarFile() {
		return grammarFile;
	}

	/**
	 * @param grammarFile the grammarFile to set
	 * @throws IOException 
	 */
	public void setGrammarFile(String grammarFile) throws IOException {
		// Save the file:
		this.grammarFile = grammarFile;
	}

	/**
	 * @return the fitnessFunction
	 */
	public String getFitnessFunction() {
		return fitnessFunction;
	}

	/**
	 * @param fitnessFunction the fitnessFunction to set
	 */
	public void setFitnessFunction(String fitnessFunction) {
		this.fitnessFunction = fitnessFunction;
	}

	/**
	 * @return the onRunStart
	 */
	public String getOnRunStart() {
		return onRunStart;
	}

	/**
	 * @param onRunStart the onRunStart to set
	 */
	public void setOnRunStart(String onRunStart) {
		this.onRunStart = onRunStart;
	}

	/**
	 * @return the onRunEnd
	 */
	public String getOnRunEnd() {
		return onRunEnd;
	}

	/**
	 * @param onRunEnd the onRunEnd to set
	 */
	public void setOnRunEnd(String onRunEnd) {
		this.onRunEnd = onRunEnd;
	}

	/**
	 * @return the onGenerationStart
	 */
	public String getOnGenerationStart() {
		return onGenerationStart;
	}

	/**
	 * @param onGenerationStart the onGenerationStart to set
	 */
	public void setOnGenerationStart(String onGenerationStart) {
		this.onGenerationStart = onGenerationStart;
	}

	/**
	 * @return the onGenerationEnd
	 */
	public String getOnGenerationEnd() {
		return onGenerationEnd;
	}

	/**
	 * @param onGenerationEnd the onGenerationEnd to set
	 */
	public void setOnGenerationEnd(String onGenerationEnd) {
		this.onGenerationEnd = onGenerationEnd;
	}

	/**
	 * @return the noOfRuns
	 */
	public int getNoOfRuns() {
		return noOfRuns;
	}

	/**
	 * @param noOfRuns the noOfRuns to set
	 */
	public void setNoOfRuns(int noOfRuns) {
		this.noOfRuns = noOfRuns;
	}

	/**
	 * @return the grammar
	 */
	public String getGrammar() {
		return grammar;
	}

	/**
	 * @param grammar the grammar to set
	 */
	public void setGrammar(String grammar) {
		this.grammar = grammar;
	}

	/**
	 * @return the onEvolutionStart
	 */
	public String getOnEvolutionStart() {
		return onEvolutionStart;
	}

	/**
	 * @param onEvolutionStart the onEvolutionStart to set
	 */
	public void setOnEvolutionStart(String onEvolutionStart) {
		this.onEvolutionStart = onEvolutionStart;
	}

	/**
	 * @return the onEvolutionEnd
	 */
	public String getOnEvolutionEnd() {
		return onEvolutionEnd;
	}

	/**
	 * @param onEvolutionEnd the onEvolutionEnd to set
	 */
	public void setOnEvolutionEnd(String onEvolutionEnd) {
		this.onEvolutionEnd = onEvolutionEnd;
	}

	/**
	 * @return the libraries
	 */
	public String[] getLibraries() {
		return libraries;
	}

	/**
	 * @param libraries the libraries to set
	 */
	public void setLibraries(String[] libraries) {
		this.libraries = libraries;
	}

	/**
	 * @return the custom
	 */
	public Object getCustom() {
		return custom;
	}

	/**
	 * @param custom the custom to set
	 */
	public void setCustom(Object custom) {
		this.custom = custom;
	}

	public String toString() {
		Gson gson = new GsonBuilder().setPrettyPrinting().serializeSpecialFloatingPointValues().create();
        return(gson.toJson(this));
	}
	
	/**
	 * Provides an efficient way of reading files into a string.
	 * 
	 * Credits: http://stackoverflow.com/questions/326390/how-to-create-a-java-string-from-the-contents-of-a-file
	 * 
	 * @param path the path to the file.
	 * @return the contents of the file given
	 * @throws IOException
	 */
	private static String readFile(String path) throws IOException {
		FileInputStream stream = new FileInputStream(new File(path));
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		    return Charset.defaultCharset().decode(bb).toString();
		}
		finally {
			stream.close();
		}
	}
	
	/**
	 * Reads a JSON config file and returns a Configuration instance.
	 * 
	 * @param jsonFile
	 * @return configuration instance.
	 * @throws JsonSyntaxException
	 * @throws JsonIOException
	 * @throws IOException
	 * @throws GrammarFileNotFoundException
	 * @throws ConfigFileNotFoundException
	 */
	public static Configuration fromJsonFile(String jsonFile) throws JsonSyntaxException, JsonIOException, IOException, GrammarFileNotFoundException, ConfigFileNotFoundException {
		// Read in the contents of the configuration file and instantiate the Configuration:
		Configuration config = null;
        try {
        	config = new Gson().fromJson(new BufferedReader(new FileReader(jsonFile)), Configuration.class);
		}
        catch (FileNotFoundException e) {
			throw new ConfigFileNotFoundException();
		}
        
        // Save the config file path:
        config.setConfigFile(jsonFile);
		
		// Attempt to set the grammar if not set yet:
        if (config.getGrammar() == null) {
        	try {
        		config.setGrammar(Configuration.readFile(config.getGrammarFile()));
        	}
        	catch (FileNotFoundException e) {
        		throw new GrammarFileNotFoundException();
        	}
        }
        
        // Done, return the configuration:
		return config;
	}
}