package org.epochx.stats;

import org.epochx.stats.Stats.ExpiryEvent;

/**
 * A Stat is both a statistic field and a way of generating a statistic
 * dynamically.
 */
public interface Stat {

	/**
	 * Returns when the data for this statistics field expires. For example a
	 * value of CROSSOVER means it expires at the start of every crossover
	 * event, and as such is likely to be cleared or otherwise unreliable from
	 * then until the next time it is set.
	 * 
	 * @return the event at which this statistics field is available to be
	 *         cleared.
	 */
	public ExpiryEvent getExpiryEvent();

	/**
	 * Generates and returns a value for this statistic. It is not necessary for
	 * a Stat to implement this method to do anything other than return
	 * <code>null</code> if the value is going to be provided directly to the
	 * Stats manager. However, if this stat is requested and the stats manager
	 * does not have a value for it then this method will be called to attempt
	 * to generate it. It is typical for a stat that implements this method to
	 * request other stats that it is dependent upon from the stats manager.
	 * 
	 * @return an Object value for this stat. The actual type of the value
	 *         returned will be dependent upon the stat that it represents.
	 */
	public Object getStatValue();
}
