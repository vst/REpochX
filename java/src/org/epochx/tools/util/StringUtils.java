/*
 * Copyright 2007-2010 Tom Castle & Lawrence Beadle
 * Licensed under GNU General Public License
 * 
 * This file is part of EpochX: genetic programming software for research
 * 
 * EpochX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EpochX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with EpochX. If not, see <http://www.gnu.org/licenses/>.
 * 
 * The latest version is available from: http:/www.epochx.org
 */
package org.epochx.tools.util;

import org.apache.commons.lang.ArrayUtils;

/**
 * 
 */
public final class StringUtils {

	private StringUtils() {}
	
	/**
	 * Tests whether the given string contains any of the chars in the array.
	 * 
	 * @param str
	 * @param chrs
	 * @return
	 */
	public static boolean containsAny(final String str, final char[] chrs) {
		for (int i = 0; i < str.length(); i++) {
			final char c = str.charAt(i);
			if (ArrayUtils.contains(chrs, c)) {
				return true;
			}
		}
		return false;
	}

}
