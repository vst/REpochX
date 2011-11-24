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
package org.epochx.gr.stats;

import static org.epochx.stats.Stats.ExpiryEvent.GENERATION;

import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.epochx.gr.representation.GRCandidateProgram;
import org.epochx.stats.*;

/**
 * Provides constants to be used as keys to request statistics from the
 * Stats specific to XGR.
 */
public class GRStatField extends StatField {

	/**
	 * Returns an <code>int[]</code> which contains the depths of all the
	 * <code>CandidateProgram</code>s in the population at the end of the
	 * previous generation.
	 */
	public static final Stat GEN_DEPTHS = new AbstractStat(GENERATION) {

		@Override
		public Object getStatValue() {
			int[] depths = null;

			// Request the population from the stats manager.
			@SuppressWarnings("unchecked")
			final List<GRCandidateProgram> pop = (List<GRCandidateProgram>) Stats.get().getStat(GEN_POP);

			// Get the depths of each program.
			if (pop != null) {
				depths = new int[pop.size()];

				for (int i = 0; i < pop.size(); i++) {
					depths[i] = pop.get(i).getDepth();
				}
			}

			return depths;
		};
	};

	/**
	 * Returns a <code>Double</code> which is the average depth of all the
	 * <code>CandidateProgram</code>s in the population at the end of the
	 * previous generation.
	 */
	public static final Stat GEN_DEPTH_AVE = new AbstractStat(GENERATION) {

		@Override
		public Object getStatValue() {
			Double aveDepth = null;

			// Request the population depths from the stats manager.
			final int[] depths = (int[]) Stats.get().getStat(GEN_DEPTHS);

			// Calculate the average depth.
			if (depths != null) {
				aveDepth = StatsUtils.ave(depths);
			}

			return aveDepth;
		};
	};

	/**
	 * Returns a <code>Double</code> which is the standard deviation of the
	 * depths of all the <code>CandidateProgram</code>s in the
	 * population at the end of the previous generation.
	 */
	public static final Stat GEN_DEPTH_STDEV = new AbstractStat(GENERATION) {

		@Override
		public Object getStatValue() {
			Double stdevDepth = null;

			// Request the population depths from the stats manager.
			final int[] depths = (int[]) Stats.get().getStat(GEN_DEPTHS);

			// Calculate the standard deviation of the depths.
			if (depths != null) {
				stdevDepth = StatsUtils.stdev(depths);
			}

			return stdevDepth;
		};
	};

	/**
	 * Returns an <code>Integer</code> which is the maximum program depth of
	 * all the <code>CandidateProgram</code>s in the population at the end of
	 * the previous generation.
	 */
	public static final Stat GEN_DEPTH_MAX = new AbstractStat(GENERATION) {

		@Override
		public Object getStatValue() {
			Integer maxDepth = null;

			// Request the population depths from the stats manager.
			final int[] depths = (int[]) Stats.get().getStat(GEN_DEPTHS);

			// Calculate the maximum depth.
			if (depths != null) {
				maxDepth = NumberUtils.max(depths);
			}

			return maxDepth;
		};
	};

	/**
	 * Returns an <code>Integer</code> which is the minimum program depth of all
	 * the <code>CandidateProgram</code>s in the population at the end of the
	 * previous generation.
	 */
	public static final Stat GEN_DEPTH_MIN = new AbstractStat(GENERATION) {

		@Override
		public Object getStatValue() {
			Integer minDepth = null;

			// Request the population depths from the stats manager.
			final int[] depths = (int[]) Stats.get().getStat(GEN_DEPTHS);

			// Calculate the minimum depth.
			if (depths != null) {
				minDepth = NumberUtils.min(depths);
			}

			return minDepth;
		};
	};

}
