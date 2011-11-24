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

import java.util.List;

import org.epochx.life.*;
import org.epochx.op.PoolSelector;
import org.epochx.representation.CandidateProgram;
import org.epochx.stats.Stats;

/**
 * This component manages the selection of a breeding pool to undergo the
 * genetic operators.
 * 
 * <p>
 * The task of pool selection is the selection of a subset of programs from a
 * larger population based upon some preference (typically a fitness bias,
 * although sometimes merely randomly). In the EpochX framework, it is from a
 * pool such as this that programs will be selected to undergo the genetic
 * operators.
 * 
 * <p>
 * The actual operation of selecting a pool of programs will be performed by an
 * implementation of <code>PoolSelector</code> which is obtained from the
 * <code>Model</code>.
 * 
 * <p>
 * Use of the pool selection operation may generate the following events:
 * 
 * <table border="1">
 * <tr>
 * <th>Event</th>
 * <th>Revert</th>
 * <th>Modify</th>
 * <th>Raised when?</th>
 * </tr>
 * <tr>
 * <td>onPoolSelectionStart</td>
 * <td>no</td>
 * <td>no</td>
 * <td>Before the selection operation is attempted for the first time.</td>
 * </tr>
 * <tr>
 * <td>onPoolSelection</td>
 * <td><strong>yes</strong></td>
 * <td><strong>yes</strong></td>
 * <td>Immediately after a pool is selected, giving the listener the opportunity
 * to request a revert which will cause a re-selection of the pool and this
 * event to be raised again.</td>
 * </tr>
 * <tr>
 * <td>onPoolSelectionEnd</td>
 * <td>no</td>
 * <td>no</td>
 * <td>After the selection operation has been completed and accepted.</td>
 * </tr>
 * </table>
 * 
 * @see PoolSelector
 */
public class PoolSelectionManager implements ConfigListener {

	// The controlling model.
	private final Model model;

	// The pool selector to use to generate the breeding pool.
	private PoolSelector poolSelector;

	// The number of programs that make up a breeding pool.
	private int poolSize;

	// The number of times the pool selection was rejected.
	private int reversions;

	/**
	 * Constructs an instance of PoolSelectionManager which will setup the
	 * breeding pool selection operation.
	 * 
	 * @param model the Model which defines the PoolSelector operator and any
	 *        other control parameters.
	 * 
	 * @see PoolSelector
	 */
	public PoolSelectionManager(final Model model) {
		this.model = model;

		// Configure parameters from the model.
		Life.get().addConfigListener(this, false);
	}

	/*
	 * Configure component with parameters from the model.
	 */
	@Override
	public void onConfigure() {
		poolSize = model.getPoolSize();
		poolSelector = model.getPoolSelector();
	}

	/**
	 * Selects a pool of <code>CandidatePrograms</code> by calling the
	 * <code>getPool</code> method on the PoolSelector retrieved from the model
	 * given at construction. The size of the program pool requested from the
	 * pool selector will also be obtained from this model.
	 * 
	 * <p>
	 * The returned breeding pool may be a subset of the given population, but
	 * the size of the returned list may equally be larger than the given
	 * population, for example, if the pool contains duplicates.
	 * 
	 * @param pop the population of programs from which the pool will be
	 *        selected.
	 * @return a list of <code>CandidatePrograms</code> to be used as a
	 *         breeding pool.
	 */
	public List<CandidateProgram> getPool(final List<CandidateProgram> pop) {
		if ((poolSelector != null) && ((poolSize == 0) || (poolSize < -1))) {
			throw new IllegalStateException("pool selector set but invalid pool size (=" + poolSize + ')');
		}

		// Inform all listeners that pool selection is starting.
		Life.get().firePoolSelectionStartEvent();

		// Record the start time.
		final long startTime = System.nanoTime();

		// Reset the number of reversions.
		reversions = 0;

		List<CandidateProgram> pool = null;
		do {
			if ((poolSelector == null) || (poolSize == -1)) {
				// Use population as the pool.
				pool = pop;
			} else {
				// Perform pool selection.
				pool = poolSelector.getPool(pop, poolSize);
			}

			// Allow life cycle listener to confirm or modify.
			pool = Life.get().runPoolSelectionHooks(pool);

			// If reverted then increment reversion counter.
			if (pool == null) {
				reversions++;
			}
		} while (pool == null);

		final long runtime = System.nanoTime() - startTime;

		// Store the stats from the pool selection.
		Stats.get().addData(POOL_REVERSIONS, reversions);
		Stats.get().addData(POOL_PROGRAMS, pool);
		Stats.get().addData(POOL_TIME, runtime);

		// Inform all listeners that pool selection has ended.
		Life.get().firePoolSelectionEndEvent();

		assert (pool != null);

		return pool;
	}

}
