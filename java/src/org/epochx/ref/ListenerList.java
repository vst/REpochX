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

import java.lang.ref.ReferenceQueue;
import java.util.*;

import org.epochx.life.Listener;

/**
 * A List specifically for managing listeners. Each listener element is wrapped
 * in a MixedReference instance which optionally allows each individual
 * listener to be referenced either strongly or weakly - thus allowing the
 * listener to be cleared by the garbage collected when it is no longer needed.
 * By default strong references are used and in such a case the list behaves
 * exactly like a normal list of listeners. Part of the process of adding a new
 * listener to the list is checking for dead entries and removing them. The
 * process of removing weakly referenced listeners is at the mercy of the
 * garbage collector and so there is no guarentee that listeners will be removed
 * promptly when only weak references remain.
 * 
 * In some applications it might be appropriate to make a call to
 * <code>System.gc()</code> to encourage the garbage collector to release these
 * references.
 * 
 * @param <T>
 */
public class ListenerList<T extends Listener> extends AbstractList<T> {

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static final long serialVersionUID = -8968847606607124719L;

	// Queue of references to be removed.
	private final ReferenceQueue<T> referenceQueue;

	// The actual list of listeners.
	private final List<MixedReference<T>> data;

	/**
	 * Constructs a <code>ListenerList</code>.
	 */
	public ListenerList() {
		data = new ArrayList<MixedReference<T>>();
		referenceQueue = new ReferenceQueue<T>();
	}

	/**
	 * Clears any entries from the list that have been cleared by the garbage
	 * collector.
	 */
	protected void expunge() {
		// Clear any references on the reference queue.
		MixedReference<? extends Listener> ref;
		while ((ref = (MixedReference<? extends Listener>) referenceQueue.poll()) != null) {
			data.remove(ref);
		}

		// This is only for testing - comment out for production.
		// System.gc();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final int index, final T element) {
		add(index, element, true);
	}

	/**
	 * Adds a new listener to the list at the given index using either a strong
	 * or a weak reference. After adding the new listener, the list will be
	 * expunged of any null references and so on return from this method the
	 * listener being set may no longer be at the given index.
	 * 
	 * @param index the index that the given element should be set at.
	 * @param element the listener instance to be inserted into the list.
	 * @param strong true if a strong reference should be maintained to the
	 *        listener instance, and false if only a weak reference should be
	 *        used.
	 */
	public void add(final int index, final T element, final boolean strong) {
		final MixedReference<T> next = new MixedReference<T>(element, strong, referenceQueue);
		data.add(index, next);

		expunge();
	}

	/**
	 * Adds a new listener to the end of the list using either a strong or a
	 * weak reference. The list will also be expunged of any null references and
	 * so on return from this method the size of the list may have reduced.
	 * 
	 * @param element the listener instance to be appended to the list.
	 * @param strong true if a strong reference should be maintained to the
	 *        listener instance, and false if only a weak reference should be
	 *        used.
	 * @return true if the given element was successfully added to the list,
	 *         false otherwise.
	 */
	public boolean add(final T element, final boolean strong) {
		expunge();

		final MixedReference<T> next = new MixedReference<T>(element, strong, referenceQueue);
		return data.add(next);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T remove(final int index) {
		final MixedReference<T> removed = data.remove(index);
		return removed.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T set(final int index, final T element) {
		return set(index, element, true);
	}

	/**
	 * Puts the given listener into this list at the specified index, replacing
	 * the current listener at that index. Either a strong or a weak reference
	 * is maintained to the listener depending upon the <code>strong</code>
	 * parameter.
	 * 
	 * @param index the index that the given element should be set at.
	 * @param element the listener instance to be inserted into the list.
	 * @param strong true if a strong reference should be maintained to the
	 *        listener instance, and false if only a weak reference should be
	 *        used.
	 */
	public T set(final int index, final T element, final boolean strong) {
		final MixedReference<T> next = new MixedReference<T>(element, strong, referenceQueue);
		final MixedReference<T> prev = data.set(index, next);

		return prev.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return data.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T get(final int index) {
		// We cannot expunge here because it will break iteration.
		return data.get(index).get();
	}
}
