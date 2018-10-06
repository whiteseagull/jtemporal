/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with NO WARRANTIES given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;

/**
 * This is more or less a Map, whose key is an Instant. <BR>
 * <b>Warning, some more methods will be added later.</b> 
 * especially given that for the moment no sorting-related methods 
 * are present (iterators, etc).  
 * @author Thomas A Beck
 * @version $Id: TimeSeries.java,v 1.9 2008/12/23 17:44:07 tabeck Exp $
 * @TODO create a SortedTimeSeries subinterface
 * @param <V> the type of the value
 */
public interface TimeSeries<V> extends ReadableTemporalAttribute<V> {
	
	/**
	 * Creates a new entry in this timeseries.
	 * @param i the instant whom the value must be associated to.  Must be not null.
	 * @param value the value.  Must be not null.
	 * @throws UnsupportedOperationException if the TimeSeries is read-only
	 * @return the value previously associated to the i instant, or null.
	 */
	public V put(Instant i, V value);
	
	/**
	 * Gets the value currently associated to the i instant.
	 * @param the instant, key for this value.
	 * @return the value currently associated to the i instant, or null.
	 */
	public V get(Instant i); // from ReadableTemporalAttribute
	
	/**
	 * Removes an entry from this timeseries.
	 * @param i the instant to be removed. Must be not null.
	 * @return the value that was previously associated to the i instant
	 * @throws UnsupportedOperationException if the TimeSeries is read-only
	 */
	public V remove(Instant i);
	
	/**
	 * Remove a whole period from the timeseries (optional implementation). 
	 * @param p the period enclosing the entries to be removed.
	 * @throws UnsupportedOperationException if the TimeSeries is read-only
	 * @return <code>true</code> if something has been actually removed
	 */	
	public void remove(Period p);
	
	/**
	 * Returns <tt>true</tt> if this TimeSeries contains a mapping for the specified
	 * Instant.
	 * @param i instant whose presence in this TimeSeries is to be tested.
	 * @return <tt>true</tt> if this TimeSeries contains a mapping for the specified
	 * instant.
	 */
	public boolean containsInstant(Instant i);
	
	/**
	* Returns a Set view of the Instants (keys) contained in this TimeSeries.  
	* The set's iterator will return the keys in ascending order.  
	* The set is backed by this <tt>TimeSeries</tt> instance, 
	* so changes to this TimeSeries are reflected in the Set, and vice-versa.  
	* The Set supports element removal, which removes the corresponding mapping 
	* from the map, via the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>,
	* <tt>retainAll</tt>, and <tt>clear</tt> operations.  It does not support
	* the <tt>add</tt> or <tt>addAll</tt> operations.
	*
	* @return a set view of the instants that are keys in this TimeSeries.
	*/
	//public Set<I> instantSet();
}
