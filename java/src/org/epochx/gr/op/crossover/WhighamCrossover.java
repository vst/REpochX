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
package org.epochx.gr.op.crossover;

import java.util.*;

import org.epochx.gr.model.GRModel;
import org.epochx.gr.representation.GRCandidateProgram;
import org.epochx.op.ConfigOperator;
import org.epochx.representation.CandidateProgram;
import org.epochx.stats.*;
import org.epochx.stats.Stats.ExpiryEvent;
import org.epochx.tools.grammar.*;
import org.epochx.tools.random.RandomNumberGenerator;

public class WhighamCrossover extends ConfigOperator<GRModel> implements GRCrossover {

	/**
	 * Requests an <code>Integer</code> which is the index of the point chosen
	 * for the whigham crossover operation. The index is from the list of all
	 * non-terminal symbols in the parse tree of the first program, as would be
	 * returned by the <code>getNonTerminalSymbols</code> method.
	 */
	public static final Stat XO_POINT1 = new AbstractStat(ExpiryEvent.CROSSOVER) {};

	/**
	 * Requests an <code>Integer</code> which is the index of the point chosen
	 * for the whigham crossover operation. The index is from the list of all
	 * non-terminal symbols in the parse tree of the second program, as would be
	 * returned by the <code>getNonTerminalSymbols</code> method.
	 */
	public static final Stat XO_POINT2 = new AbstractStat(ExpiryEvent.CROSSOVER) {};

	/**
	 * Requests a <code>NonTerminalSymbol</code> which is the subtree from the
	 * first parent program which is being exchanged into the second parent.
	 */
	public static final Stat XO_SUBTREE1 = new AbstractStat(ExpiryEvent.CROSSOVER) {};

	/**
	 * Requests a <code>NonTerminalSymbol</code> which is the subtree from the
	 * second parent program which is being exchanged into the first parent.
	 */
	public static final Stat XO_SUBTREE2 = new AbstractStat(ExpiryEvent.CROSSOVER) {};

	// The random number generator in use.
	private RandomNumberGenerator rng;

	/**
	 * Constructs a <code>WhighamCrossover</code>.
	 * 
	 */
	public WhighamCrossover(final RandomNumberGenerator rng) {
		this((GRModel) null);

		this.rng = rng;
	}

	/**
	 * Constructs a <code>WhighamCrossover</code>.
	 * 
	 * @param model
	 */
	public WhighamCrossover(final GRModel model) {
		super(model);
	}

	/**
	 * Configures this operator with parameters from the model.
	 */
	@Override
	public void onConfigure() {
		rng = getModel().getRNG();
	}

	@Override
	public GRCandidateProgram[] crossover(final CandidateProgram p1, final CandidateProgram p2) {

		final GRCandidateProgram child1 = (GRCandidateProgram) p1;
		final GRCandidateProgram child2 = (GRCandidateProgram) p2;

		final NonTerminalSymbol parseTree1 = child1.getParseTree();
		final NonTerminalSymbol parseTree2 = child2.getParseTree();

		final List<NonTerminalSymbol> nonTerminals1 = parseTree1.getNonTerminalSymbols();
		final List<NonTerminalSymbol> nonTerminals2 = parseTree2.getNonTerminalSymbols();

		final int point1 = rng.nextInt(nonTerminals1.size());

		final NonTerminalSymbol subtree1 = nonTerminals1.get(point1);

		// Generate a list of matching non-terminals from the second program.
		final List<NonTerminalSymbol> matchingNonTerminals = new ArrayList<NonTerminalSymbol>();
		for (final NonTerminalSymbol nt: nonTerminals2) {
			if (nt.getGrammarRule().equals(subtree1.getGrammarRule())) {
				matchingNonTerminals.add(nt);
			}
		}

		if (matchingNonTerminals.isEmpty()) {
			// No valid points in second program, cancel crossover.
			return null;
		} else {
			// Randomly choose a second point out of the matching non-terminals.
			final int point2 = rng.nextInt(matchingNonTerminals.size());
			final NonTerminalSymbol subtree2 = matchingNonTerminals.get(point2);

			// Add crossover points to the stats manager.
			Stats.get().addData(XO_POINT1, point1);
			Stats.get().addData(XO_POINT2, point2);

			// Swap the non-terminals' children.
			final List<Symbol> temp = subtree1.getChildren();
			subtree1.setChildren(subtree2.getChildren());
			subtree2.setChildren(temp);

			// Add subtrees into the stats manager.
			Stats.get().addData(XO_SUBTREE1, subtree1);
			Stats.get().addData(XO_SUBTREE2, subtree2);
		}

		return new GRCandidateProgram[]{child1, child2};
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
	}
}
