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
package org.epochx.core;

import static org.epochx.stats.StatField.*;

import org.epochx.life.*;
import org.epochx.op.*;
import org.epochx.representation.CandidateProgram;
import org.epochx.stats.Stats;

/**
 * This component is responsible for handling the crossover operation and for
 * raising crossover events.
 * 
 * <p>
 * Use of the crossover operation will generate the following events:
 * 
 * <p>
 * <table border="1">
 * <tr>
 * <th>Event</th>
 * <th>Revert</th>
 * <th>Modify</th>
 * <th>Raised when?</th>
 * </tr>
 * <tr>
 * <td>onCrossoverStart</td>
 * <td>no</td>
 * <td>no</td>
 * <td>Before the crossover operation is carried out.</td>
 * </tr>
 * <tr>
 * <td>onCrossover</td>
 * <td><strong>yes</strong></td>
 * <td><strong>yes</strong></td>
 * <td>Immediately after crossover is carried out, giving the listener the
 * opportunity to request a revert which will cause a re-selection of the parent
 * programs and crossing over of those programs which will result in this event
 * being raised again.</td>
 * </tr>
 * <tr>
 * <td>onCrossoverEnd</td>
 * <td>no</td>
 * <td>no</td>
 * <td>After the crossover operation has been completed.</td>
 * </tr>
 * </table>
 */
public class CrossoverManager implements ConfigListener {

	// The controlling model.
	private final Model model;

	// The selector for choosing parents.
	private ProgramSelector programSelector;

	// The crossover operator that will perform the actual operation.
	private Crossover crossover;

	// The number of times the crossover was rejected by the model.
	private int reversions;

	/**
	 * Constructs an instance of CrossoverManager which will setup the crossover
	 * operation. Note that the actual crossover operation will be performed by
	 * the subclass of <code>Crossover</code> returned by the models
	 * getCrossover() method.
	 * 
	 * @see Crossover
	 */
	public CrossoverManager(final Model model) {
		this.model = model;

		// Configure parameters from the model.
		Life.get().addConfigListener(this, false);
	}

	/*
	 * Configure component with parameters from the model.
	 */
	@Override
	public void onConfigure() {
		programSelector = model.getProgramSelector();
		crossover = model.getCrossover();
	}

	/**
	 * Selects two parents by calling <code>getProgramSelector()</code> on the
	 * instance of <code>GPModel</code> given at construction and submits them
	 * to the <code>Crossover</code> operator which is obtained by calling
	 * <code>getCrossover()</code> on the model.
	 * 
	 * <p>
	 * After a crossover is made, all the child programs are checked for
	 * validity by calling their <code>isValid()</code> method. If <strong>any
	 * </strong> of the programs are found to be invalid then the programs are
	 * discarded, two new parents are selected and the crossover operation is
	 * attempted again. If all the child programs are valid then the crossover
	 * event is fired which gives any listeners the opportunity to revert the
	 * operation by returning null, or modify the children that are returned. If
	 * null is returned to revert then the reversion count is incremented by 1
	 * and two new programs are selected and the crossover operation is
	 * repeated.
	 * 
	 * @return an array of CandidatePrograms generated through crossover. This
	 *         is typically 2 child programs, but could in theory be any number
	 *         as returned by the Crossover operator in use.
	 */
	public CandidateProgram[] crossover() {
		if (crossover == null) {
			throw new IllegalStateException("crossover operator not set");
		}
		if (programSelector == null) {
			throw new IllegalStateException("program selector not set");
		}

		// Inform everyone we're about to start crossover.
		Life.get().fireCrossoverStartEvent();

		// Record the start time.
		final long crossoverStartTime = System.nanoTime();

		CandidateProgram parent1;
		CandidateProgram parent2;

		CandidateProgram clone1;
		CandidateProgram clone2;

		CandidateProgram[] parents = null;
		CandidateProgram[] children = null;

		reversions = 0;
		do {
			// Select the parents for crossover.
			parent1 = programSelector.getProgram();
			parent2 = programSelector.getProgram();

			clone1 = parent1.clone();
			clone2 = parent2.clone();
			parents = new CandidateProgram[]{parent1, parent2};

			// Attempt crossover.
			children = crossover.crossover(clone1, clone2);

			// Start the loop again if all the children are not valid.
			if ((children == null) || !allValid(children)) {
				children = null;
				continue;
			}

			// Ask life cycle listener to confirm the crossover.
			children = Life.get().runCrossoverHooks(parents, children);

			// If reverted then increment reversion counter.
			if (children == null) {
				reversions++;
			}
		} while (children == null);

		final long runtime = System.nanoTime() - crossoverStartTime;

		Stats.get().addData(XO_PARENTS, parents);
		Stats.get().addData(XO_CHILDREN, children);
		Stats.get().addData(XO_REVERSIONS, reversions);
		Stats.get().addData(XO_TIME, runtime);

		Life.get().fireCrossoverEndEvent();

		assert (children != null);

		return children;
	}

	/*
	 * Tests whether all of the given array of CandidatePrograms is valid. If
	 * one or more are invalid then it returns false otherwise it returns true.
	 */
	private boolean allValid(final CandidateProgram[] programs) {
		assert (programs != null);

		boolean valid = true;
		for (final CandidateProgram p: programs) {
			if (!p.isValid()) {
				valid = false;
				break;
			}
		}
		return valid;
	}
}
