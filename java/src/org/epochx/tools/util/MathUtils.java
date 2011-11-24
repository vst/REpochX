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
 * The latest version is available from: http://www.epochx.org
 */
package org.epochx.tools.util;


/**
 * Thanks to the JSci science API (http://jsci.sourceforge.net/) for some of 
 * these.
 */
public final class MathUtils {

	private MathUtils() {}
	
	/**
	 * Arc-cosecant
	 * 
	 * @param d
	 * @return
	 */
	public static double arccsc(double d) {
		return Math.asin(1.0/d);
	}
	
	/**
	 * Arc-tangent
	 * 
	 * @param d
	 * @return
	 */
	public static double arccot(double d) {
		return Math.atan(1.0/d);
	}
	
	/**
	 * Arc-secant
	 * 
	 * @param d
	 * @return
	 */
	public static double arcsec(double d) {
		return Math.acos(1.0/d);
	}
	
	/**
	 * Cosecant
	 * 
	 * @param d
	 * @return
	 */
	public static double csc(double d) {
		return 1.0 / Math.sin(d);
	}
	
	/**
	 * Secant
	 * 
	 * @param d
	 * @return
	 */
	public static double sec(double d) {
		return 1.0 / Math.cos(d);
	}
	
	/**
	 * Cotangent
	 * 
	 * @param d
	 * @return
	 */
	public static double cot(double d) {
		return 1 / Math.tan(d);
	}
	
	/**
	 * Returns the inverse hyperbolic cosine of a <code>double</code> value.
	 * Note that <i>cosh(±acosh(x))&nbsp;=&nbsp;x</i>; this function arbitrarily returns the positive branch.
	 * <p>The identity is:
	 * <p><i>arcosh(x)&nbsp;=&nbsp;ln(x&nbsp;±&nbsp;sqrt(x<sup>2</sup>&nbsp;-&nbsp;1))</i>
	 * <p>Special cases:
	 * <ul>
	 * <li>If the argument is NaN or less than one, then the result is NaN.
	 * <li>If the argument is a positive infinity, then the result is (positive) infinity.
	 * <li>If the argument is one, then the result is (positive) zero.
	 * </ul>
	 * @param d The number whose inverse hyperbolic cosine is sought
	 * @return The inverse hyperbolic cosine of <code>x</code>
	 */ 
	public static double arcosh(double d) {
		return Math.log(d + Math.sqrt(d*d - 1.0));
	}
	
	/**
	 * Returns the area (inverse) hyperbolic sine of a <code>double</code> value.
	 * <p>The identity is:
	 * <p><i>arsinh(x)&nbsp;=&nbsp;ln(x&nbsp;+&nbsp;sqrt(x<sup>2</sup>&nbsp;+&nbsp;1))</i>
	 * <p>Special cases:
	 * <ul>
	 * <li>If the argument is NaN, then the result is NaN.
	 * <li>If the argument is infinite, then the result is an infinity with the same sign as the argument.
	 * <li>If the argument is zero, then the result is a zero with the same sign as the argument.
	 * </ul>
	 * @param d The number whose inverse hyperbolic sine is sought
	 * @return The inverse hyperbolic sine of <code>d</code>
	 */ 
	public static double arsinh(double d) {
		return Double.isInfinite(d) ? d : (d == 0.0) ? d : Math.log(d+Math.sqrt(d*d+1.0)); 
	}
	
	/**
	 * Returns the inverse hyperbolic tangent of a <code>double</code> value.
	 * <p>The identity is:
	 * <p><i>artanh(x)&nbsp;=&nbsp;(1/2)*ln((1&nbsp;+&nbsp;x)/(1&nbsp;-&nbsp;x))</i>
	 * <p>Special cases:
	 * <ul>
	 * <li>If the argument is NaN, an infinity, or has a modulus of greater than one, then the result is NaN.
	 * <li>If the argument is plus or minus one, then the result is infinity with the same sign as the argument.
	 * <li>If the argument is zero, then the result is a zero with the same sign as the argument.
	 * </ul>
	 * @param x A double specifying the value whose inverse hyperbolic tangent is sought
	 * @return A double specifying the inverse hyperbolic tangent of x
	 */ 
	public static double artanh(double d) {
		return (d != 0.0) ? (Math.log(1.0 + d) - Math.log(1.0 - d))/2.0 : d; 
	}
	
	/**
	 * Returns the hyperbolic secant of a <code>double</code> value.
	 * <p>The identity is:
	 * <p><i>sech(x)&nbsp;=&nbsp;(2/(e<sup>x</sup>&nbsp;+&nbsp;e<sup>-x</sup>)</i>,
	 * in other words, 1/{@linkplain Math#cosh cosh(<i>x</i>)}.
	 * <p>Special cases:
	 * <ul>
	 * <li>If the argument is NaN, then the result is NaN.
	 * <li>If the argument is an infinity (positive or negative), then the result is <code>+0.0</code>.
	 * </ul>
	 * @param d The number whose hyperbolic secant is sought
	 * @return The hyperbolic secant of <code>d</code>
	 */
	public static double sech(double d) {
		return 1.0/Math.cosh(d);
	}
	
	/**
	 * Returns the hyperbolic cosecant of a <code>double</code> value.
	 * <p>The identity is:
	 * <p><i>csch(x)&nbsp;=&nbsp;(2/(e<sup>x</sup>&nbsp;-&nbsp;e<sup>-x</sup>)</i>,
	 * in other words, 1/{@linkplain Math#sinh sinh(<i>x</i>)}.
	 * <p>Special cases:
	 * <ul>
	 * <li>If the argument is NaN, then the result is NaN.
	 * <li>If the argument is zero, then the result is an infinity with the same sign as the argument.
	 * <li>If the argument is positive infinity, then the result is <code>+0.0</code>.
	 * <li>If the argument is negative infinity, then the result is <code>-0.0</code>.
	 * </ul>
	 * @param x The number whose hyperbolic cosecant is sought
	 * @return The hyperbolic cosecant of <code>x</code>
	 */
	public static double csch(double x) {
		return 1.0/Math.sinh(x);
	}
	
	/**
	 * Returns the hyperbolic cotangent of a <code>double</code> value.
	 * <p>The identity is:
	 * <p><i>coth(x)&nbsp;=&nbsp;(e<sup>x</sup>&nbsp;+&nbsp;e<sup>-x</sup>)/(e<sup>x</sup>&nbsp;-&nbsp;e<sup>-x</sup>)</i>,	
	 * in other words, {@linkplain Math#cosh cosh(<i>x</i>)}/{@linkplain Math#sinh sinh(<i>x</i>)}.
	 * <p>Special cases:
	 * <ul>
	 * <li>If the argument is NaN, then the result is NaN.
	 * <li>If the argument is zero, then the result is an infinity with the same sign as the argument.
	 * <li>If the argument is positive infinity, then the result is <code>+1.0</code>.
	 * <li>If the argument is negative infinity, then the result is <code>-1.0</code>.
	 * </ul>
	 * @param x The number whose hyperbolic cotangent is sought
	 * @return The hyperbolic cotangent of <code>x</code>
	 */
	public static double coth(double x) {
		return 1.0/Math.tanh(x);
	}
}
