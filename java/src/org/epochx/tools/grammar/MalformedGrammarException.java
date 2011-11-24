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
package org.epochx.tools.grammar;

/**
 * A runtime exception that is thrown to indicate that a grammar string is
 * not in a valid BNF format.
 */
public class MalformedGrammarException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4253637393156061665L;

	/**
	 * Constructs a <code>MalformedGrammarException</code> with the specified
	 * message.
	 * 
	 * @param message {@inheritDoc}
	 */
	public MalformedGrammarException(final String message) {
		super(message);
	}

}
