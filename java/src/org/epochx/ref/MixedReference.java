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
package org.epochx.ref;

import java.lang.ref.*;

import org.apache.commons.lang.ObjectUtils;

/**
 * A MixedReference provides an optionally weak reference.
 */
public class MixedReference<T> extends WeakReference<T> {

	// The strong reference which will be null if only using weak.
	private T strongInstance;

	/**
	 * Constructs a <code>MixedReference</code>, with either a strong or weak
	 * reference to the given object depending upon the type.
	 * 
	 * @param instance the object to hold a strong or weak reference too.
	 * @param strong true if a strong reference should be held to the given
	 *        instance, which will prevent it from being garbage collected or
	 *        false for
	 *        a weak reference which may be garbage collected if no other strong
	 *        references to the object exist.
	 */
	public MixedReference(final T instance, final boolean strong) {
		this(instance, strong, null);
	}

	/**
	 * Constructs a <code>MixedReference</code>, with either a strong or weak
	 * reference to the given object depending upon the type. If using a weak
	 * reference then the reference will be enqueued on the given
	 * <code>ReferenceQueue</code> when the instance is garbage collected.
	 * 
	 * @param instance the object to hold a strong or weak reference too.
	 * @param strong true if a strong reference should be held to the given
	 *        instance, which will prevent it from being garbage collected or
	 *        false for
	 *        a weak reference which may be garbage collected if no other strong
	 *        references to the object exist.
	 * @param queue a ReferenceQueue to enqueue the reference on when the
	 *        instance is garbage collected.
	 */
	public MixedReference(final T instance, final boolean strong, final ReferenceQueue<T> queue) {
		super(instance, queue);

		// Store a strong reference too if asked.
		if (strong) {
			this.strongInstance = instance;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T get() {
		if (strongInstance != null) {
			return strongInstance;
		} else {
			return super.get();
		}
	}

	/**
	 * Sets whether the reference should be strong or weak. If the reference was
	 * previously weak and then a call to this method sets it to be strong then
	 * there is no guarentee that the instance the reference points to has not
	 * already been cleared, in such a case the <code>get</code> method will
	 * return <code>null</code>.
	 * 
	 * @param strong <code>true</code> if a strong reference should be
	 *        maintained to the instance, and false if only a weak reference
	 *        should be
	 *        used.
	 */
	public void setStrong(final boolean strong) {
		if (strong) {
			strongInstance = get();
		}
	}

	/**
	 * A mixed reference is equal to another mixed reference if this reference's
	 * instance is equal to the given object's instance according to its
	 * <code>equals</code> method.
	 * 
	 * @return true if the two references are equal, and false otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof MixedReference<?>) {
			final MixedReference<?> ref = (MixedReference<?>) obj;
			return ObjectUtils.equals(this.get(), ref.get());
		}

		return false;
	}
}
