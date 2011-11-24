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
package org.epochx.stats;

import java.io.*;
import java.util.*;

import org.epochx.life.*;

/**
 * Gathers data and statistics about events that occur during execution of the
 * given <code>Model</code> and makes them available for use. Any component may
 * add data into the <code>Stats</code> by using the <code>addData</code>
 * method. The <code>Stats</code> manager will also make requests of
 * <code>Stat</code> objects to generate the statistic and then
 * stash the results internally. The data that is stored will only be held for
 * until the next occurrence of the <code>ExpiryEvent</code> of the Stat.
 * 
 * <p>
 * To print to a file use the <code>printToStream</code> method, passing in a
 * suitable <code>OutputStream</code> such as <code>FileOutputStream</code>.
 */
public class Stats {

	// Singleton instance.
	private static Stats instance;

	// Map of all stats data, by event at which they should be cleared.
	private final Map<ExpiryEvent, Map<Stat, Object>> data;

	/**
	 * Possible events at which statistics fields can be considered expired.
	 * Each one refers to the start of the next occurrence of that event. For
	 * example, all fields with an expiry of <code>ExpiryEvent.RUN</code> will
	 * be cleared on the start of the next run.
	 */
	public enum ExpiryEvent {
		RUN, GENERATION, INITIALISATION, ELITISM, POOL_SELECTION, CROSSOVER, MUTATION, REPRODUCTION
	}

	/*
	 * Constructs a <code>Stats</code> - this class uses the singleton pattern
	 * so this constructor is private. An instance can be obtained using the
	 * get() method.
	 */
	private Stats() {
		// Setup the stats manager.
		data = new HashMap<ExpiryEvent, Map<Stat, Object>>();

		// Construct a map for each event now because will only be needed to be
		// done later.
		final ExpiryEvent[] events = ExpiryEvent.values();
		for (final ExpiryEvent e: events) {
			data.put(e, new HashMap<Stat, Object>());
		}

		// Setup listeners to clear stats at appropriate times.
		setupListeners();
	}

	/**
	 * Returns the singleton instance of Stats.
	 * 
	 * @return the only Stats instance.
	 */
	public static Stats get() {
		if (instance == null) {
			instance = new Stats();
		}
		return instance;
	}

	/**
	 * Inserts an item of data about a run into the stats manager
	 * associated with the given field key. If data is already stored against
	 * the given field then it will be overwritten. The value will be cleared
	 * upon the appropriate event as designated by the given stat field's
	 * <code>getExpiryEvent</code> method.
	 * 
	 * @param field the key to associate with the given data value.
	 * @param value the statistics/data to be stored.
	 */
	public void addData(final Stat field, final Object value) {
		data.get(field.getExpiryEvent()).put(field, value);
	}

	/**
	 * Retrieves the statistic data associated with the provided field in the
	 * stats being stored. If a value for the stat field is not currently held
	 * then the <code>getStatValue</code> method on the Stat will be called,
	 * with the resulting value returned from this method and also stored for
	 * future requests.
	 * 
	 * <p>
	 * The object type of the instance that is returned will be dependent upon
	 * the field requested. The object type is normally specified in the
	 * documentation for the field.
	 * 
	 * @param field the name of the statistics field to retrieve.
	 * @return an object which represents the statistics data requested or
	 *         <code>null</code> if the field does not exist or the data is
	 *         otherwise unavailable.
	 */
	public Object getStat(final Stat field) {
		Object value = data.get(field.getExpiryEvent()).get(field);

		// If stat not stored then ask the stat if it can generate the value.
		if (value == null) {
			value = field.getStatValue();

			if (value != null) {
				addData(field, value);
			}
		}

		return value;
	}

	/**
	 * Retrieves a sequence of statistics associated with the given
	 * fields. The returned array will be of the same length as the number of
	 * fields provided and each element will represent each field in order.
	 * 
	 * <p>
	 * Each statistic field requested will be obtained according to the contract
	 * specified by the <code>getStat(Stat)</code> method.
	 * 
	 * @param fields the names of the statistics fields to retrieve.
	 * @return an array of Objects where each element is the statistic field
	 *         generated for the requested field at that array index. If a field
	 *         is for data that does not exist or is otherwise unavailable then
	 *         that array element will be <code>null</code>.
	 */
	public Object[] getStats(final Stat ... fields) {
		final Object[] stats = new Object[fields.length];
		for (int i = 0; i < fields.length; i++) {
			stats[i] = getStat(fields[i]);
		}
		return stats;
	}

	/**
	 * Retrieve and print to the standard output the sequence of statistics
	 * referenced by the given fields. The statistics will be obtained according
	 * to the contract specified by the <code>getStats(Stat[])</code> method.
	 * The result of calling the <code>toString()</code> method on each
	 * statistic object returned will be printed separated by a '\t' tab
	 * character, and the line terminated with a '\n' newline character.
	 * 
	 * @param fields the Stat fields to print out.
	 */
	public void print(final Stat ... fields) {
		print("\t", fields);
	}

	/**
	 * Retrieve and print to the standard output the sequence of statistics
	 * referenced by the given fields. The statistics will be obtained according
	 * to the contract specified by the <code>getStats(Stat[])</code> method.
	 * The result of calling the <code>toString()</code> method on each
	 * statistic object returned will be printed separated by the
	 * <code>separator</code> <code>String</code> parameter provided, and the
	 * line terminated with a '\n' newline character.
	 * 
	 * @param fields the Stat fields to print out.
	 * @param separator the String to be printed between each statistic.
	 */
	public void print(final String separator, final Stat ... fields) {
		final Object[] stats = getStats(fields);

		printArray(System.out, stats, separator);
	}

	/**
	 * Retrieve and print to an output stream the sequence of statistics
	 * referenced by the given fields. The statistics will be obtained according
	 * to the contract specified by the <code>getStats(Stat[])</code> method.
	 * The result of calling the <code>toString()</code> method on each
	 * statistic object returned will be printed separated by a '\t' tab
	 * character, and the line terminated with a '\n' newline character.
	 * 
	 * @param out the OutputStream to print to.
	 * @param fields the Stat fields to print to the stream.
	 */
	public void printToStream(final OutputStream out, final Stat ... fields) {
		printToStream(out, "\t", fields);
	}

	/**
	 * Retrieve and print to an output stream the sequence of statistics
	 * referenced by the given fields. The statistics will be obtained according
	 * to the contract specified by the <code>getStats(Stat[])</code> method.
	 * The result of calling the <code>toString()</code> method on each
	 * statistic object returned will be printed separated by the
	 * <code>separator</code> <code>String</code> parameter provided, and the
	 * line terminated with a '\n' newline character.
	 * 
	 * @param out the OutputStream to print to.
	 * @param fields the Stat fields to print to the stream.
	 * @param separator the String to be printed between each statistic.
	 */
	public void printToStream(final OutputStream out, final String separator, final Stat ... fields) {
		final Object[] stats = getStats(fields);

		printArray(out, stats, separator);
	}

	/*
	 * Print each element of the given array separated by the provided separator
	 * character followed by a new line character.
	 */
	private static void printArray(final OutputStream outputStream, final Object[] array, final String separator) {
		final PrintStream out = new PrintStream(outputStream);
		for (int i = 0; i < array.length; i++) {
			if (i != 0) {
				out.print(separator);
			}
			out.print(array[i]);
		}
		out.println();
		out.flush();
	}

	/*
	 * Listen for life cycle events and if they happen then clear appropriate
	 * old data out of the data maps.
	 */
	private void setupListeners() {
		// Clear the run data.
		Life.get().addRunListener(new RunAdapter() {

			@Override
			public void onRunStart() {
				data.get(ExpiryEvent.RUN).clear();
			}
		});

		// Clear the generation data.
		Life.get().addGenerationListener(new GenerationAdapter() {

			@Override
			public void onGenerationStart() {
				data.get(ExpiryEvent.GENERATION).clear();
			}
		});

		// Clear the initialisation data.
		Life.get().addInitialisationListener(new InitialisationAdapter() {

			@Override
			public void onInitialisationStart() {
				data.get(ExpiryEvent.INITIALISATION).clear();
			}
		});

		// Clear the elitism data.
		Life.get().addElitismListener(new ElitismAdapter() {

			@Override
			public void onElitismStart() {
				data.get(ExpiryEvent.INITIALISATION).clear();
			}
		});

		// Clear the elitism data.
		Life.get().addElitismListener(new ElitismAdapter() {

			@Override
			public void onElitismStart() {
				data.get(ExpiryEvent.ELITISM).clear();
			}
		});

		// Clear the pool selection data.
		Life.get().addPoolSelectionListener(new PoolSelectionAdapter() {

			@Override
			public void onPoolSelectionStart() {
				data.get(ExpiryEvent.POOL_SELECTION).clear();
			}
		});

		// Clear the crossover data.
		Life.get().addCrossoverListener(new CrossoverAdapter() {

			@Override
			public void onCrossoverStart() {
				data.get(ExpiryEvent.CROSSOVER).clear();
			}
		});

		// Clear the mutation data.
		Life.get().addMutationListener(new MutationAdapter() {

			@Override
			public void onMutationStart() {
				data.get(ExpiryEvent.MUTATION).clear();
			}
		});
	}

}
