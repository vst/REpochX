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
package org.epochx.gr.op.mutation;

import java.util.List;

import org.epochx.gr.model.GRModel;
import org.epochx.gr.op.init.GrowInitialiser;
import org.epochx.gr.representation.GRCandidateProgram;
import org.epochx.op.ConfigOperator;
import org.epochx.representation.CandidateProgram;
import org.epochx.stats.*;
import org.epochx.stats.Stats.ExpiryEvent;
import org.epochx.tools.grammar.*;
import org.epochx.tools.random.RandomNumberGenerator;

public class WhighamMutation extends ConfigOperator<GRModel> implements GRMutation {

	/**
	 * Requests an <code>Integer</code> which is the index of the non-terminal
	 * which was selected to be modified by the whigham mutation.
	 */
	public static final Stat MUT_POINT = new AbstractStat(ExpiryEvent.MUTATION) {};

	/**
	 * Requests a <code>NonTerminalSymbol</code> which is the subtree that was
	 * inserted in the program undergoing whigham mutation.
	 */
	public static final Stat MUT_SUBTREE = new AbstractStat(ExpiryEvent.MUTATION) {};

	private RandomNumberGenerator rng;

	private final GrowInitialiser grower;

	/**
	 * Constructs a <code>WhighamMutation</code>.
	 * 
	 */
	public WhighamMutation(final RandomNumberGenerator rng) {
		this((GRModel) null);

		this.rng = rng;

		grower.setRNG(rng);
	}

	/**
	 * Constructs a <code>WhighamMutation</code>.
	 * 
	 * @param model
	 */
	public WhighamMutation(final GRModel model) {
		super(model);

		grower = new GrowInitialiser(model);
	}

	/*
	 * Configure component with parameters from the model.
	 */
	@Override
	public void onConfigure() {
		rng = getModel().getRNG();
	}

	@Override
	public GRCandidateProgram mutate(final CandidateProgram program) {
		final GRCandidateProgram mutatedProgram = (GRCandidateProgram) program.clone();

		final NonTerminalSymbol parseTree = mutatedProgram.getParseTree();

		// This is v.inefficient because we have to fly up and down the tree
		// lots of times.
		final List<Integer> nonTerminals = parseTree.getNonTerminalIndexes();

		// Choose a node to change.
		final int point = nonTerminals.get(rng.nextInt(nonTerminals.size()));
		final NonTerminalSymbol original = (NonTerminalSymbol) parseTree.getNthSymbol(point);
		final int originalDepth = original.getDepth();

		// Add subtree into the stats manager.
		Stats.get().addData(MUT_POINT, point);

		// Construct a new subtree from that node's grammar rule.
		final GrammarRule rule = original.getGrammarRule();
		final NonTerminalSymbol subtree = grower.getGrownParseTree(originalDepth, rule);

		// Add subtree into the stats manager.
		Stats.get().addData(MUT_SUBTREE, subtree);

		// Replace node.
		if (point == 0) {
			mutatedProgram.setParseTree(subtree);
		} else {
			parseTree.setNthSymbol(point, subtree);
		}

		return mutatedProgram;
	}

	/**
	 * Returns the random number generator that this crossover is using or
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

		grower.setRNG(rng);
	}
}
