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
 * This component is responsible for handling the mutation operation and for
 * firing mutation events.
 * 
 * <p>
 * Use of the mutation operation will generate the following events:
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
 * <td>onMutationStart</td>
 * <td>no</td>
 * <td>no</td>
 * <td>Before the mutation operation is carried out.</td>
 * </tr>
 * <tr>
 * <td>onMutation</td>
 * <td><strong>yes</strong></td>
 * <td><strong>yes</strong></td>
 * <td>Immediately after mutation is carried out, giving the listener the
 * opportunity to request a revert which will cause a re-selection of a program
 * and mutation of that program which will result in this event being raised
 * again.</td>
 * </tr>
 * <tr>
 * <td>onMutationEnd</td>
 * <td>no</td>
 * <td>no</td>
 * <td>After the mutation operation has been completed.</td>
 * </tr>
 * </table>
 */
public class MutationManager implements ConfigListener {

	// The controlling model.
	private final Model model;

	// The selector for choosing the individual to mutate.
	private ProgramSelector programSelector;

	// The mutation operator that will perform the actual operation.
	private Mutation mutator;

	// The number of times the mutation was rejected by the model.
	private int reversions;

	/**
	 * Constructs an instance of MutationManager which will setup the mutation
	 * operation. Note that the actual mutation operation will be performed by
	 * the subclass of <code>Mutation</code> returned by the model's
	 * <code>getMutation()</code> method.
	 * 
	 * @param model the Model which defines the Mutation operator and
	 *        ProgramSelector to use to perform one act of mutation on
	 *        an individual in the population.
	 * @see Mutation
	 */
	public MutationManager(final Model model) {
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
		mutator = model.getMutation();
	}

	/**
	 * Selects a <code>CandidateProgram</code> from the population using the
	 * <code>ProgramSelector</code> returned by a call to
	 * <code>getProgramSelector()</code> on the model given at construction and
	 * submits it to the <code>Mutation</code> operator which is obtained by
	 * calling <code>getMutation()</code> on the model.
	 * 
	 * <p>
	 * After a mutation is made, the child program is checked for validity by
	 * calling its <code>isValid()</code> method. If the program is found to be
	 * invalid then the program is discarded, a new program is and the mutation
	 * operation is attempted again. If the child program is valid then the
	 * mutation event is fired which gives any listeners the opportunity to
	 * revert the operation by returning null, or modify the child that is
	 * returned. If null is returned to revert then the reversion count is
	 * incremented by 1 and a new program is selected mutation operation is
	 * repeated.
	 * 
	 * @return a CandidateProgram generated through mutation by the Mutation
	 *         operator in use.
	 */
	public CandidateProgram mutate() {
		if (mutator == null) {
			throw new IllegalStateException("mutation operator not set");
		}
		if (programSelector == null) {
			throw new IllegalStateException("program selector not set");
		}

		// Inform everyone we're about to start crossover.
		Life.get().fireMutationStartEvent();

		// Record the start time.
		final long mutationStartTime = System.nanoTime();

		CandidateProgram parent = null;
		CandidateProgram child = null;

		reversions = 0;
		do {
			// Attempt mutation.
			parent = programSelector.getProgram();

			// Create child as a clone of parent.
			child = parent.clone();

			// Mutate the child.
			child = mutator.mutate(child);

			// Start the loop again if the program is not valid.
			if ((child == null) || !child.isValid()) {
				child = null;
				continue;
			}

			// Allow the life cycle listener to confirm or modify.
			child = Life.get().runMutationHooks(parent, child);

			if (child == null) {
				reversions++;
			}
		} while (child == null);

		final long runtime = System.nanoTime() - mutationStartTime;

		// Store the stats from the mutation.
		Stats.get().addData(MUT_PARENT, parent);
		Stats.get().addData(MUT_CHILD, child);
		Stats.get().addData(MUT_TIME, runtime);
		Stats.get().addData(MUT_REVERSIONS, reversions);

		Life.get().fireMutationEndEvent();

		assert (child != null);

		return child;
	}
}
