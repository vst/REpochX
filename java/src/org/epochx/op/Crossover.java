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
package org.epochx.op;

import org.epochx.representation.CandidateProgram;

/**
 * Crossover operators perform the task of exchanging genetic material between
 * two programs. The manner in which the exchange is made is up to each
 * implementation, and the number of resulting child programs is also flexible.
 * 
 * <p>
 * Currently there is no provision for implementing crossover operators which
 * operate on more than 2 programs.
 */
public interface Crossover extends Operator {

	/**
	 * Performs the crossover operation.
	 * 
	 * @param parent1 the first parent that should have its genetic material
	 *        exchanged with the second parent.
	 * @param parent2 the second parent that should have its genetic material
	 *        exchanged with the first parent.
	 * @return an array of the children resulting from performing the crossover
	 *         operation. Any number of children may be returned.
	 *         <code>null</code> is
	 *         also a valid return value, which will result in 2 new parents
	 *         being
	 *         selected and attempted for crossover (this does not count as a
	 *         reversion).
	 */
	public CandidateProgram[] crossover(CandidateProgram parent1, CandidateProgram parent2);

}
