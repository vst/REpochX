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

import java.util.List;

import org.epochx.representation.CandidateProgram;

/**
 * Pool selector operators perform the task of selecting a pool of programs
 * from a population of programs. The way in which the programs are selected is
 * up to each implementation to decide. The returned pool may contain duplicate
 * programs. The number of programs in the returned pool should be equal to the
 * pool size given here as a parameter.
 */
public interface PoolSelector {

	/**
	 * Performs the selection of a pool of programs from a population.
	 * 
	 * @param pop the population of programs from which the pool should be
	 *        selected or otherwise constructed.
	 * @param poolSize the requested size for the pool to be constructed.
	 * @return a list of the programs that have been selected to form the
	 *         breeding pool. The size of the list should equal the poolSize
	 *         parameter.
	 */
	public List<CandidateProgram> getPool(List<CandidateProgram> pop, int poolSize);

}
