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

/**
 * Provides an <code>abstract</code> implementation of
 * <code>ConfigListener</code>.
 * 
 * <p>
 * Typical use of this class would be in an anonymous class. For example:
 * 
 * <blockquote>
 * 
 * <pre>
 *     Life.get().addConfigListener(new ConfigAdapter(){
 *     		public void onConfigure() {
 *     			... do something ...
 *     		}
 *     });
 * </pre>
 * 
 * </blockquote>
 * 
 * <p>
 * Creating an anonymous implementation of this class is often preferable to
 * implementing <code>ConfigListener</code> since it avoids the need to
 * implement methods which may be of no interest.
 */
public class ConfigAdapter implements ConfigListener {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onConfigure() {
	}

}
