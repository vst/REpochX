/*
 * Copyright 2007-2011 Tom Castle & Lawrence Beadle
 * Licensed under GNU Lesser General Public License
 * 
 * This file is part of EpochX: genetic programming software for research
 * 
 * EpochX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EpochX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with EpochX. If not, see <http://www.gnu.org/licenses/>.
 * 
 * The latest version is available from: http://www.epochx.org
 */
package org.epochx.gr.op.init;

import java.util.*;

import org.epochx.gr.model.GRModel;
import org.epochx.gr.representation.GRCandidateProgram;
import org.epochx.op.ConfigOperator;
import org.epochx.representation.CandidateProgram;
import org.epochx.tools.grammar.*;
import org.epochx.tools.random.RandomNumberGenerator;

/**
 * Initialisation implementation which randomly grows program parse trees down
 * to a maximum maxDepth.
 * 
 * <p>
 * If a model is provided then the following parameters are loaded upon every
 * configure event:
 * 
 * <ul>
 * <li>population size</li>
 * <li>maximum initial program maxDepth</li>
 * <li>grammar</li>
 * <li>random number generator</li>
 * </ul>
 * 
 * <p>
 * If the <code>getModel</code> method returns <code>null</code> then no model
 * is set and whatever static parameters have been set as parameters to the
 * constructor or using the standard accessor methods will be used. If any
 * compulsory parameters remain unset when the initialiser is requested to
 * generate new programs, then an <code>IllegalStateException</code> will be
 * thrown.
 * 
 * @see FullInitialiser
 * @see RampedHalfAndHalfInitialiser
 */
public class GrowInitialiser extends ConfigOperator<GRModel> implements GRInitialiser {

	private RandomNumberGenerator rng;

	// The grammar all new programs must be valid against.
	private Grammar grammar;

	// The size of the populations to construct.
	private int popSize;

	// The maximum maxDepth of each program tree to generate.
	private int maxDepth;

	// Whether programs must be unique in generated populations.
	private boolean acceptDuplicates;

	/**
	 * Constructs a <code>GrowInitialiser</code> with all the necessary
	 * parameters given.
	 */
	public GrowInitialiser(final RandomNumberGenerator rng, final Grammar grammar, final int popSize,
			final int maxDepth, final boolean acceptDuplicates) {
		this(null, acceptDuplicates);

		this.rng = rng;
		this.grammar = grammar;
		this.popSize = popSize;
		this.maxDepth = maxDepth;
	}

	/**
	 * Constructs a <code>GrowInitialiser</code> with the necessary parameters
	 * loaded from the given model. The parameters are reloaded on configure
	 * events. Duplicate programs are allowed in the populations that are
	 * constructed.
	 * 
	 * @param model the <code>GRModel</code> instance from which the necessary
	 *        parameters should be loaded.
	 */
	public GrowInitialiser(final GRModel model) {
		this(model, true);
	}

	/**
	 * Constructs a <code>GrowInitialiser</code> with the necessary parameters
	 * loaded from the given model. The parameters are reloaded on configure
	 * events.
	 * 
	 * @param model the <code>GRModel</code> instance from which the necessary
	 *        parameters should be loaded.
	 * @param acceptDuplicates whether duplicates should be allowed in the
	 *        populations that are generated.
	 */
	public GrowInitialiser(final GRModel model, final boolean acceptDuplicates) {
		super(model);

		this.acceptDuplicates = acceptDuplicates;
	}

	/*
	 * Configure component with parameters from the model.
	 */
	@Override
	public void onConfigure() {
		rng = getModel().getRNG();
		grammar = getModel().getGrammar();
		popSize = getModel().getPopulationSize();
		maxDepth = getModel().getMaxInitialDepth();
	}

	/**
	 * Generates a population of new <code>GRCandidatePrograms</code>
	 * constructed
	 * from the <code>Grammar</code> attribute. The size of the
	 * population will be equal to the population size attribute. All programs
	 * in the population are only guarenteed to be unique (as defined by the
	 * <code>equals</code> method on <code>GRCandidateProgram</code>) if the
	 * <code>isDuplicatesEnabled</code> method returns <code>true</code>.
	 * 
	 * @return A <code>List</code> of newly generated
	 *         <code>GRCandidatePrograms</code>.
	 */
	@Override
	public List<CandidateProgram> getInitialPopulation() {
		if (popSize < 1) {
			throw new IllegalStateException("Population size must be 1 or greater");
		}

		// Create population list to be populated.
		final List<CandidateProgram> firstGen = new ArrayList<CandidateProgram>(popSize);

		// Create and add new programs to the population.
		for (int i = 0; i < popSize; i++) {
			GRCandidateProgram candidate;

			do {
				// Create a new program down to the models initial max maxDepth.
				candidate = getInitialProgram();
			} while (!acceptDuplicates && firstGen.contains(candidate));

			// Add to the new population.
			firstGen.add(candidate);
		}

		return firstGen;
	}

	/**
	 * Constructs and returns a new <code>GRCandidateProgram</code> with a grown
	 * parse tree with the given maximum maxDepth.
	 * 
	 * @return A new <code>GRCandidateProgram</code> with a grown parse tree.
	 */
	public GRCandidateProgram getInitialProgram() {
		if (grammar == null) {
			throw new IllegalStateException("No grammar has been set");
		}

		final GrammarRule startRule = grammar.getStartRule();

		// Check the min depth of a valid tree is not more than the max depth.
		if (startRule.getMinDepth() > maxDepth) {
			throw new IllegalStateException("No possible programs within given max depth parameter for this grammar.");
		}

		return new GRCandidateProgram(getGrownParseTree(maxDepth, startRule), getModel());
	}

	/**
	 * Grows and returns a new parse tree with a maximum maxDepth of the
	 * specified
	 * maxDepth parameter.
	 * 
	 * @param maxDepth The maximum maxDepth of the parse tree, where the
	 *        maxDepth is the number of nodes from the root.
	 * @return The root node of a randomly generated full parse tree of the
	 *         requested maxDepth.
	 */
	public NonTerminalSymbol getGrownParseTree(final int maxDepth, final GrammarRule startRule) {
		if (rng == null) {
			throw new IllegalStateException("No random number generator has been set");
		} else if (maxDepth < 0) {
			throw new IllegalStateException("Maximum depth must be 0 or greater");
		}

		final NonTerminalSymbol parseTree = new NonTerminalSymbol(startRule);

		buildDerivationTree(parseTree, startRule, 0, maxDepth);

		return parseTree;
	}

	/*
	 * Recursive helper for the getGrownParseTree method.
	 */
	private void buildDerivationTree(final NonTerminalSymbol parseTree, final GrammarRule rule, final int currentDepth,
			final int maxDepth) {
		// Check if theres more than one production.
		int productionIndex = 0;
		final int noProductions = rule.getNoProductions();
		if (noProductions > 1) {
			final List<Integer> validProductions = getValidProductionIndexes(rule.getProductions(), maxDepth
					- currentDepth
					- 1);

			// Choose a production randomly.
			final int chosenProduction = rng.nextInt(validProductions.size());
			productionIndex = validProductions.get(chosenProduction);
		}

		// Drop down the tree at this production.
		final GrammarProduction p = rule.getProduction(productionIndex);

		final List<GrammarNode> grammarNodes = p.getGrammarNodes();
		for (final GrammarNode node: grammarNodes) {
			if (node instanceof GrammarRule) {
				final GrammarRule r = (GrammarRule) node;

				final NonTerminalSymbol nt = new NonTerminalSymbol((GrammarRule) node);

				buildDerivationTree(nt, r, currentDepth + 1, maxDepth);

				parseTree.addChild(nt);
			} else {
				// Must be a grammar literal.
				parseTree.addChild(new TerminalSymbol((GrammarLiteral) node));
			}
		}
	}

	/*
	 * Gets a List of indexes to those productions from the List of productions
	 * given that can be used with the specified maximum maxDepth constraint.
	 */
	private List<Integer> getValidProductionIndexes(final List<GrammarProduction> grammarProductions, final int maxDepth) {
		final List<Integer> valid = new ArrayList<Integer>();

		for (int i = 0; i < grammarProductions.size(); i++) {
			final GrammarProduction p = grammarProductions.get(i);

			if (p.getMinDepth() <= maxDepth) {
				valid.add(i);
			}
		}

		// If there were any valid recursive productions, return them, otherwise
		// use the others.
		return valid;
	}

	/**
	 * Returns whether or not duplicates are currently accepted or rejected from
	 * generated populations.
	 * 
	 * @return <code>true</code> if duplicates are currently accepted in any
	 *         populations generated by the <code>getInitialPopulation</code>
	 *         method and <code>false</code> otherwise
	 */
	public boolean isDuplicatesEnabled() {
		return acceptDuplicates;
	}

	/**
	 * Sets whether duplicates should be allowed in the populations that are
	 * generated, or if they should be discarded.
	 * 
	 * @param acceptDuplicates whether duplicates should be accepted in the
	 *        populations that are constructed.
	 */
	public void setDuplicatesEnabled(final boolean acceptDuplicates) {
		this.acceptDuplicates = acceptDuplicates;
	}

	/**
	 * Returns the random number generator that this initialiser is using or
	 * <code>null</code> if none has been set.
	 * 
	 * @return the rng the currently set random number generator.
	 */
	public RandomNumberGenerator getRNG() {
		return rng;
	}

	/**
	 * Sets the random number generator to use. If a model has been set then
	 * this parameter will be overwritten with the random number generator from
	 * that model on the next configure event.
	 * 
	 * @param rng the random number generator to set.
	 */
	public void setRNG(final RandomNumberGenerator rng) {
		this.rng = rng;
	}

	/**
	 * Returns the grammar that this initialiser is generating programs to
	 * satisfy.
	 * 
	 * @return the grammar that generated programs are being constructed for.
	 */
	public Grammar getGrammar() {
		return grammar;
	}

	/**
	 * Sets the grammar that program parse trees should be generated for.
	 * 
	 * @param grammar the <code>Grammar</code> that generated programs should be
	 *        constructed for.
	 */
	public void setGrammar(final Grammar grammar) {
		this.grammar = grammar;
	}

	/**
	 * Returns the size of the populations that this initialiser constructs or
	 * <code>-1</code> if none has been set.
	 * 
	 * @return the size of the populations that this initialiser will generate.
	 */
	public int getPopSize() {
		return popSize;
	}

	/**
	 * Sets the size of the populations that this initialiser should construct
	 * on calls to the <code>getInitialPopulation</code> method.
	 * 
	 * @param popSize the size of the populations that should be created by this
	 *        initialiser.
	 */
	public void setPopSize(final int popSize) {
		this.popSize = popSize;
	}

	/**
	 * Returns the maximum depth that any program parse tree generated by this
	 * initialiser will have.
	 * 
	 * @return the maximum depth the program trees constructed should be.
	 */
	public int getMaxDepth() {
		return maxDepth;
	}

	/**
	 * Sets the maximum depth that any program parse trees generated by this
	 * initialiser should be.
	 * 
	 * @param maxDepth the maximum depth of all new program trees.
	 */
	public void setMaxDepth(final int maxDepth) {
		this.maxDepth = maxDepth;
	}
}
