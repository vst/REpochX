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
package org.epochx.life;

import java.util.List;

import org.epochx.representation.CandidateProgram;

/**
 * Provides an abstract <code>Hook</code> implementation which for each hook
 * method simply returns the parameter as received.
 */
public abstract class AbstractHook implements Hook {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CandidateProgram> initialisationHook(final List<CandidateProgram> pop) {
		return pop;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CandidateProgram> elitismHook(final List<CandidateProgram> elites) {
		return elites;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CandidateProgram> poolSelectionHook(final List<CandidateProgram> pool) {
		return pool;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CandidateProgram[] crossoverHook(final CandidateProgram[] parents, final CandidateProgram[] children) {
		return children;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CandidateProgram mutationHook(final CandidateProgram parent, final CandidateProgram child) {
		return child;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CandidateProgram reproductionHook(final CandidateProgram program) {
		return program;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CandidateProgram> generationHook(final List<CandidateProgram> pop) {
		return pop;
	}
}
