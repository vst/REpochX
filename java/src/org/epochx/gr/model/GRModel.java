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
package org.epochx.gr.model;

import org.epochx.core.Model;
import org.epochx.gr.op.crossover.WhighamCrossover;
import org.epochx.gr.op.init.RampedHalfAndHalfInitialiser;
import org.epochx.gr.op.mutation.WhighamMutation;
import org.epochx.tools.grammar.Grammar;

/**
 * Model implementation for performing grammar-guided evolution using the
 * approach outlined by Whigham he called CFG-GP.
 */
public abstract class GRModel extends Model {

	// Control parameters.
	private Grammar grammar;

	private int maxDepth;
	private int maxInitialDepth;

	/**
	 * Construct a GRModel with a set of sensible defaults. See the appropriate
	 * accessor method for information of each default value.
	 */
	public GRModel() {
		// Set default parameter values.
		grammar = null;

		maxDepth = 14;
		maxInitialDepth = 8;

		// Operators.
		setInitialiser(new RampedHalfAndHalfInitialiser(this));
		setCrossover(new WhighamCrossover(this));
		setMutation(new WhighamMutation(this));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		if (getGrammar() == null) {
			throw new IllegalStateException("no grammar set");
		}

		super.run();
	}

	/**
	 * Returns the grammar instance that determines the structure of the
	 * programs to be evolved. As well as defining the syntax of solutions, the
	 * grammar also essentially determines the function and terminal sets which
	 * are features of tree GP.
	 * 
	 * @return the language grammar that defines the syntax of solutions.
	 */
	public Grammar getGrammar() {
		return grammar;
	}

	/**
	 * Sets the grammar that defines the valid syntax of the programs to be
	 * evolves.
	 * 
	 * @param grammar the language grammar to use to define the syntax of
	 *        solutions.
	 */
	public void setGrammar(final Grammar grammar) {
		if (grammar != null) {
			this.grammar = grammar;
		} else {
			throw new IllegalArgumentException("grammar must not be null");
		}

		assert (this.grammar != null);
	}

	/**
	 * Returns the maximum depth of the derivation trees allowed. Crossovers or
	 * mutations that result in a larger chromosome will not be allowed.
	 * 
	 * <p>
	 * Defaults to 14.
	 * 
	 * @return the maximum depth of derivation trees to allow.
	 */
	public int getMaxDepth() {
		return maxDepth;
	}

	/**
	 * Overwrites the default maximum allowable depth of a program's derivation
	 * tree.
	 * 
	 * <p>
	 * Max depth of -1 is allowed to indicate no limit.
	 * 
	 * @param maxDepth the maximum depth to allow a program's derivation tree.
	 */
	public void setMaxDepth(final int maxDepth) {
		if ((maxDepth >= 1) || (maxDepth == -1)) {
			this.maxDepth = maxDepth;
		} else {
			throw new IllegalArgumentException("maxDepth must either be -1 or greater than 0");
		}

		assert ((this.maxDepth >= 1) || (this.maxDepth == -1));
	}

	/**
	 * Returns the maximum depth of the derivation trees allowed at
	 * initialisation.
	 * 
	 * <p>
	 * Defaults to 8.
	 * 
	 * @return the maximum depth of derivation trees to allow after
	 *         initialisation.
	 */
	public int getMaxInitialDepth() {
		return maxInitialDepth;
	}

	/**
	 * Overwrites the default maximum allowable depth of a program's derivation
	 * tree after initialisation.
	 * 
	 * <p>
	 * Max depth of -1 is allowed to indicate no limit.
	 * 
	 * @param maxInitialDepth the maximum depth to allow a program's derivation
	 *        tree after initialisation.
	 */
	public void setMaxInitialDepth(final int maxInitialDepth) {
		if ((maxInitialDepth >= 1) || (maxInitialDepth == -1)) {
			this.maxInitialDepth = maxInitialDepth;
		} else {
			throw new IllegalArgumentException("maxInitialDepth must either be -1 or greater than 0");
		}

		assert ((this.maxInitialDepth >= 1) || (this.maxInitialDepth == -1));
	}
}
