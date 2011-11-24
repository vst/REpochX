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
 * Initialiser implementations perform the task of generating an initial set of
 * programs. The technique used to construct the programs is up to each
 * implementation. The number of programs to be generated would normally be
 * equal to the population size requested by the model's <code>
 * getPopulationSize</code> method but it is not required to be.
 */
public interface Initialiser {

	/**
	 * Generates and returns a population of new programs.
	 * 
	 * @return a population of programs.
	 */
	public List<CandidateProgram> getInitialPopulation();

}
