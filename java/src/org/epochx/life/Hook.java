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
 * A hook is much like a listener, except that it allows a 2-way interaction.
 * Typically a hook will extend the {@link AbstractHook} class which provides
 * neutral versions of each hook method as default, rather than by implementing
 * this interface. Each hook method may receive a number of parameters which
 * provide information about the event, and typically return a value which the
 * system may use in place of the parameter values received.
 * 
 * <p>
 * Hooks are chained, which means the ordering of hooks registered is important.
 * The values passed into the first hook are generally the result of some
 * operation in the framework, but the parameter to the second hook is the
 * result returned from the first hook, and so on. Some hooks also support
 * 'reversion', where the hook may return a value of <code>null</code> and have
 * the entire event reverted and repeated. Hook methods are documented with
 * whether they support reverting.
 */
public interface Hook extends Listener {

	/**
	 * Runs this initialisation hook with a population of programs.
	 * Implementations of this method should return a List of programs which
	 * if valid may become the initial population (unless later hooks modify or
	 * revert the returned value). If an initialisation hook returns
	 * <code>null</code> that will cause the hooks process to halt and for the
	 * entire initialisation process to be reverted. The number of times the
	 * initialisation process is asked to revert like this is available as a
	 * Stat from the stats manager. If no hooks return a <code>null</code> value
	 * then the list of programs returned from the final initialisation hook
	 * will be used as the initial population for the evolution.
	 * 
	 * @param pop the newly initialised population.
	 * @return the population of CandidatePrograms to continue with as the
	 *         newly initialised population, or null if initialisation should be
	 *         reverted.
	 */
	public List<CandidateProgram> initialisationHook(final List<CandidateProgram> pop);

	/**
	 * Runs this elitism hook with the population of elites.
	 * Implementations of this method should return a List of programs which
	 * may become elites for the next generation (unless later hooks modify the
	 * returned value). It is not valid for an elitism hook to return
	 * <code>null</code>.
	 * 
	 * @param elites the selection of chosen elites.
	 * @return a list of <code>CandidatePrograms</code> to use as the set of
	 *         elites. Note that it is not appropriate to return a value of
	 *         <code>null</code>.
	 */
	public List<CandidateProgram> elitismHook(final List<CandidateProgram> elites);

	/**
	 * Runs this pool selection hook with a breeding pool of programs.
	 * Implementations of this method should return a List of programs which
	 * if valid may become the breeding pool for the next generation (unless
	 * later hooks modify or revert the returned value). If a pool selection
	 * hook returns <code>null</code> that will cause the hooks process to halt
	 * and for the entire pool selection process to be reverted. The number of
	 * times the pool selection process is asked to revert like this is
	 * available as a Stat from the stats manager. If no hooks return a
	 * <code>null</code> value then the list of programs returned from the final
	 * pool selection hook will be used as the breeding pool for the next
	 * generation.
	 * 
	 * @param pool the suggested breeding pool of programs.
	 * @return the breeding pool of CandidatePrograms that should actually be
	 *         used, or <code>null</code> if breeding pool selection should be
	 *         reverted.
	 */
	public List<CandidateProgram> poolSelectionHook(final List<CandidateProgram> pool);

	/**
	 * Runs this mutation hook with the selected parent and the mutated child.
	 * Implementations of this method should return a program which
	 * may become part of the next population (unless later hooks modify or
	 * revert the returned value). If a mutation hook returns <code>null</code>
	 * that will cause the hooks process to halt and for the entire mutation
	 * process to be reverted. The number of times the mutation process is asked
	 * to revert like this is available as a Stat from the stats manager. If no
	 * hooks return a <code>null</code> value then the program returned from the
	 * final mutation hook will be accepted as the result of the mutation
	 * operation.
	 * 
	 * @param parent the program that was selected to undergo mutation.
	 * @param child the resultant program from the parent undergoing mutation.
	 * @return a GPCandidateProgram that should be considered the result of a
	 *         mutation operation, or <code>null</code> if the mutation should
	 *         be reverted.
	 */
	public CandidateProgram mutationHook(final CandidateProgram parent, final CandidateProgram child);

	/**
	 * Runs this crossover hook with the selected parents and children.
	 * Implementations of this method should return an array of programs which
	 * will replace the given children and may become part of the next
	 * population (unless later hooks modify or revert the returned value). If a
	 * crossover hook returns <code>null</code> that will cause the hooks
	 * process to halt and for the entire crossover process to be reverted. The
	 * number of times the crossover process is asked to revert like this is
	 * available as a Stat from the stats manager. If no hooks return a
	 * <code>null</code> value then the program returned from the final
	 * crossover hook will be accepted as the result of the crossover
	 * operation.
	 * 
	 * @param parents the programs that were selected to undergo crossover.
	 * @param children the programs that were generated as a result of the
	 *        crossover operation.
	 * @return an array of CandidatePrograms to be used as the children of the
	 *         crossover operation, or null if the crossover should be reverted.
	 */
	public CandidateProgram[] crossoverHook(final CandidateProgram[] parents, final CandidateProgram[] children);

	/**
	 * Runs this reproduction hook with the selected program. Implementations of
	 * this method should return a program which will replace the given program
	 * and may become part of the next population (unless later hooks modify or
	 * revert the returned value). If a reproduction hook returns
	 * <code>null</code> that will cause the hooks process to halt and for the
	 * entire reproduction process to be reverted. The number of times the
	 * reproduction process is asked to revert like this is available as a
	 * Stat from the stats manager. If no hooks return a <code>null</code> value
	 * then the program returned from the final reproduction hook will be
	 * accepted as the result of the reproduction operation.
	 * 
	 * @param program the program that was selected to be reproduced.
	 * @return a CandidateProgram that should be used as the reproduced program
	 *         and inserted into the next population or null if reproduction
	 *         should be reverted.
	 */
	public CandidateProgram reproductionHook(final CandidateProgram program);

	/**
	 * Runs this generation hook with the population from the end of the
	 * generation. Implementations of this method should return a list of
	 * programs which will become the next population (unless later hooks modify
	 * or revert the returned value). If a generation hook returns
	 * <code>null</code> that will cause the hooks process to halt and for the
	 * entire generation to be reverted. The number of times a
	 * generation is asked to revert like this is available as a Stat from the
	 * stats manager. If no hooks return a <code>null</code> value then the
	 * population returned from the final generation hook will become the
	 * next population.
	 * 
	 * @param pop the population that is the result of carrying out the
	 *        generation.
	 * @return the list of programs that should become the next population, or
	 *         null if the generation should be reverted.
	 */
	public List<CandidateProgram> generationHook(final List<CandidateProgram> pop);
}
