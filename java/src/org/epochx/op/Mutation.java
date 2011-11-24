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
 * Mutation operators perform the task of manipulating a single program. The
 * manner in which the mutation is made is up to each implementation.
 */
public interface Mutation extends Operator {

	/**
	 * Performs the mutation operation.
	 * 
	 * @param program the program to undergo mutation of its genetic material.
	 * @return the result of performing a mutation operation. <code>null</code>
	 *         is a valid return value, which will result in a new program being
	 *         selected to undergo a mutation (this does not count as a
	 *         reversion).
	 */
	public CandidateProgram mutate(CandidateProgram program);

}
