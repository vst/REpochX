package org.epochx.stats;

import org.epochx.stats.Stats.ExpiryEvent;

/**
 * This class is an abstract implementation of Stat that allows a slightly
 * simpler inline syntax for implementing new Stats anonymously.
 * 
 * <p>
 * For example:
 * </p>
 * 
 * <blockquote><code>
 * Stat s = new AbstractStat(RUN) {};
 * </code></blockquote>
 */
public abstract class AbstractStat implements Stat {

	// When this stat should be cleared from any caches.
	private final ExpiryEvent expiry;

	/**
	 * Constructs an <code>AbstractStat</code> to expire upon the given
	 * expiry event.
	 * 
	 * @param expiry the event upon which the statistic should be cleared from
	 *        any caches.
	 */
	public AbstractStat(final ExpiryEvent expiry) {
		this.expiry = expiry;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExpiryEvent getExpiryEvent() {
		return expiry;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getStatValue() {
		return null;
	}

}
