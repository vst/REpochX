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
 * Program selectors perform the task of selecting individuals from a
 * population to be used as input to genetic operators.
 * 
 * <p>
 * The population from which individual programs are to be selected is provided
 * as a parameter to the separate <code>setSelectionPool</code> method. The
 * reason for the separation of providing population and retrieving programs is
 * that this allows implementations to perform a series of interconnected
 * selections.
 */
public interface ProgramSelector {

	/**
	 * Sets the population of programs from which individuals should be
	 * selected.
	 * 
	 * @param pop the population from which programs should be selected.
	 */
	public void setSelectionPool(List<CandidateProgram> pop);

	/**
	 * Selects an individual program. Implementers should select the program
	 * from the most recently provided pool of programs to the <code>
	 * setSelectionPool</code> method. Typically programs would be selected
	 * with some fitness bias which will provide the evolutionary selection
	 * pressure.
	 * 
	 * @return a program selected from the currently held pool of programs.
	 */
	public CandidateProgram getProgram();

}
