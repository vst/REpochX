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
package org.epochx.tools.util;

import org.apache.commons.lang.*;

/**
 * 
 */
public final class TypeUtils {

	private TypeUtils() {}
	
	public static Class<?> getSuper(final Class<?> ... classes) {
		return getSuper(false, classes);
	}

	/**
	 * Returns whichever class is the super class of the other, or null if
	 * neither are a super class of the other.
	 * 
	 * @return
	 */
	public static Class<?> getSuper(final boolean autobox, final Class<?> ... classes) {
		outer:for (final Class<?> cls1: classes) {
			for (final Class<?> cls2: classes) {
				if (!ClassUtils.isAssignable(cls2, cls1, autobox)) {
					continue outer;
				}
			}
			return cls1;
		}

		return null;
	}

	public static Class<?> getSub(final Class<?> ... classes) {
		return getSuper(false, classes);
	}

	/**
	 * Returns whichever class is the sub class of the other, or null if
	 * neither are a sub class of the other.
	 * 
	 * @return
	 */
	public static Class<?> getSub(final boolean autobox, final Class<?> ... classes) {
		outer:for (final Class<?> cls1: classes) {
			for (final Class<?> cls2: classes) {
				if (!ClassUtils.isAssignable(cls1, cls2, autobox)) {
					continue outer;
				}
			}
			return cls1;
		}

		return null;
	}

	/**
	 * Returns true if the given collection contains an element which represents
	 * a class which is the same as or is a sub type of the given class.
	 * 
	 * @param collection the collection to seach for a class that is assignable
	 *        from amongst.
	 * @param cls the class that is being searched against the given collection.
	 * @return true if the given collection contains a Class which is the same
	 *         as or a subtype of the given class parameter. It returns false
	 *         otherwise.
	 */
	public static boolean containsSub(final Class<?>[] collection, final Class<?> cls) {
		for (final Class<?> c: collection) {
			if (ClassUtils.isAssignable(c, cls)) {
				return true;
			}
		}

		return false;
	}

	public static boolean containsSuper(final Class<?>[] collection, final Class<?> cls) {
		for (final Class<?> c: collection) {
			if (ClassUtils.isAssignable(cls, c)) {
				return true;
			}
		}

		return false;
	}

	public static boolean allSub(final Class<?>[] collection, final Class<?> cls) {
		for (final Class<?> c: collection) {
			if (!ClassUtils.isAssignable(c, cls)) {
				return false;
			}
		}

		return true;
	}

	public static boolean allSuper(final Class<?>[] collection, final Class<?> cls) {
		for (final Class<?> c: collection) {
			if (!ClassUtils.isAssignable(cls, c)) {
				return false;
			}
		}

		return true;
	}

	public static boolean allEqual(final Class<?>[] collection, final Class<?> cls) {
		for (final Class<?> c: collection) {
			if (c != cls) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Get a compatible numeric type for two primitive numeric
	 * class types. Any of (byte, short, int) will resolve to int.
	 * 
	 * @param classes a numeric class types (int, long, float, or double)
	 * @return the compatible numeric type for binary operations involving
	 *         both types, or null if there is no compatible numeric type.
	 */
	public static Class<?> getNumericType(final Class<?> ... classes) {
		if (!isAllNumericType(classes)) {
			return null;
		}
		
		if (ArrayUtils.contains(classes, Double.class)) {
			return Double.class;
		} else if (ArrayUtils.contains(classes, Float.class)) {
			return Float.class;
		} else if (ArrayUtils.contains(classes, Long.class)) {
			return Long.class;
		} else {
			return Integer.class;
		}
	}

	/**
	 * Indicates if a given class type is a primitive integer type
	 * (one of byte, short, int, or long).
	 * 
	 * @param type the type to check
	 * @return true if it is a primitive numeric type, false otherwise
	 */
	public static boolean isIntegerType(final Class<?> type) {
		return ((type == Byte.class) || (type == Short.class) || (type == Integer.class) || (type == Long.class));
	}

	/**
	 * Indicates if a given class type is a primitive numeric one type
	 * (one of byte, short, int, long, float, or double).
	 * 
	 * @param type the type to check
	 * @return true if it is a primitive numeric type, false otherwise
	 */
	public static boolean isNumericType(final Class<?> type) {
		return ((type == Byte.class)
				|| (type == Short.class)
				|| (type == Integer.class)
				|| (type == Long.class)
				|| (type == Double.class) || (type == Float.class));
	}

	public static boolean isAllNumericType(final Class<?>[] classes) {
		for (final Class<?> c: classes) {
			if (!TypeUtils.isNumericType(c)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Given a numeric (byte, short, int, long, float, or double) class type or
	 * associated wrapper class type, return the primitive class type
	 * 
	 * @param type the type to look up, must be a numerical type, but can be
	 *        either primitive or a wrapper.
	 * @return the primitive class type
	 */
	public static Class<?> getPrimitiveType(final Class<?> type) {
		if (type.isPrimitive()) {
			return type;
		} else if (Integer.class.equals(type)) {
			return int.class;
		} else if (Long.class.equals(type)) {
			return long.class;
		} else if (Float.class.equals(type)) {
			return float.class;
		} else if (Double.class.equals(type)) {
			return double.class;
		} else if (Byte.class.equals(type)) {
			return byte.class;
		} else if (Short.class.equals(type)) {
			return short.class;
		} else {
			throw new IllegalArgumentException("Input class must be a numeric type");
		}
	}

	/**
	 * Get the wrapper class type for a primitive class type.
	 * 
	 * @param type a class type
	 * @return the wrapper class for the input type if it is a
	 *         primitive class type, otherwise returns the input type
	 */
	public static Class<?> getWrapperType(final Class<?> type) {
		if (!type.isPrimitive()) {
			return type;
		} else if (int.class == type) {
			return Integer.class;
		} else if (long.class == type) {
			return Long.class;
		} else if (float.class == type) {
			return Float.class;
		} else if (double.class == type) {
			return Double.class;
		} else if (boolean.class == type) {
			return Boolean.class;
		} else if (short.class == type) {
			return Short.class;
		} else if (char.class == type) {
			return Character.class;
		} else if (byte.class == type) {
			return Byte.class;
		} else if (short.class == type) {
			return Short.class;
		} else {
			throw new IllegalArgumentException();
		}
	}

}
