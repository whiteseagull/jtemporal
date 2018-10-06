/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.spi;

import net.sf.jtemporal.Instant;
import net.sf.jtemporal.Period;
import net.sf.jtemporal.TemporalSet;
import net.sf.jtemporal.TimedObject;
import net.sf.jtemporal.util.CloseableIterator;

/**
 * A storage for periods (sorted).  <br> 
 * This is used by some other storages, such as {@linkplain TemporalSet}
 * @author Thomas A Beck
 * @version $Id: PeriodStorage.java,v 1.3 2008/12/14 13:38:39 tabeck Exp $
 */
public interface PeriodStorage {
	
	/**
	 * Returns the last (highest) period currently in this storage 
	 * or <tt>null</tt> if empty.
	 * @return the last (highest) period currently in this storage or <tt>null</tt>.
	 */
	Period lastPeriod();

	/**
	 * Returns the last (highest) period currently in this storage,
	 * that intersects the given period, or <tt>null</tt> if any is found.
	 * <br>Note the the returned period may be larger than p.
	 * @param p the intersecting period
	 * @return the last (highest) period currently in this storage, or <tt>null</tt>
	 */
	//Period lastPeriod(Period p);

	/**
	 * Returns the first (lowest) period currently in this storage,
	 * or <tt>null</tt> if any is found.
	 * @return the first (lowest) period currently in this storage, or <tt>null</tt>.
	 */
	Period firstPeriod();

	/**
	 * Returns the first (lowest) period currently in this storage,
	 * that intersects the given period, or <tt>null</tt> if any is found.
	 * <br>Note the the returned period may be larger than p.
	 * @param p the intersecting period
	 * @return the first (lowest) period currently in this storage, or <tt>null</tt>.
	 */
	//Period firstPeriod(Period p);

	/**
	 * Returns <tt>true</tt> if this storage does not contain objects.
	 * @return <tt>true</tt> if this storage does not contain objects..
	 */
	boolean isEmpty();

	/**
	 * Returns <tt>true</tt> if this storage does not contain objects 
	 * intersecting the given period.
	 * @param p the intersecting period
	 * @return <tt>true</tt> if this storage does not contain objects
	 * intersecting the given period.
	 */
	boolean isEmpty(Period p);
	
	/**
	 * Returns the period valid at the specified instant.
	 * @return the period valid at the specified instant.
	 * Returns <tt>null</tt> if this storage does not contain a period 
	 * at the specified instant.
	 * @param instant the instant whose enclosing period must be returned
	 */
	Period getPeriod(Instant instant);
	
	/**
	 * Returns the period finishing exactly at the given instant, 
	 * if any is found.
	 * @param instant the instant the equals the entry end
	 * @return the Period ending exactly at the given instant, or <tt>null</tt>.
	 */
	Period getPeriodEndingAt(Instant instant);
	
	/**
	 * Returns the period starting exactly at the given instant, 
	 * if any is found.
	 * @param instant the instant the equals the entry end
	 * @return the Period starting exactly at the given instant, or <tt>null</tt>.
	 */
	Period getPeriodStartingAt(Instant instant);
	
	/**
	 * Removes all the stored periods.
	 *
	 * @throws RuntimeException (or any other subclass) if the operation could not be performed. 
	 */
	void clear();
	
	/**
	 * Returns the number of Periods in this storage.  If the
	 * storage contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
	 * <tt>Integer.MAX_VALUE</tt>.
	 * @return the number of periods in this storage.
	 */
	int size();
	
	/**
	 * Stores a period. <br>
	 * No particular check or rich semantics is expected, this method just stores.
	 * @param p period to be added to the storage.  Cannot be null.
	 * @throws IllegalArgumentException if some aspect of this period
	 *	          prevents it from being stored.
	 */
	void put(Period p);
	
	/**
	 * Removes a period previously stored. <br>
	 * <br>The period must match the parameter previously used
	 * to call the {@linkplain PeriodStorage#put(Period)} method.
	 *
	 * @param p the period to be removed
	 * @throws java.util.NoSuchElementException if the entry is not found
	 * @throws IllegalArgumentException if p is null
	 */
	void remove(Period p);

	/**
	 * Iterates over all the periods of the storage 
	 * that intersects the given period. <br>
	 * Note that the CloseableIterator#remove() method, removes the whole period,
	 * even if the entry has a period larger then the given period.
	 * <br>The instances are sorted by {@linkplain Period#equals(Object)}
	 * @param period the intersecting period.
	 * @return instances of {@linkplain TimedObject}
	 * @see Period
	 */
	CloseableIterator<Period> periodIterator(Period period);
	
	/**
	 * Iterates over all the periods of the entries. <br>
	 * <br>The instances are sorted by {@linkplain Period#compareTo(Object)}
	 * @return instances of {@linkplain Period}
	 * @see Period
	 */
	CloseableIterator<Period> periodIterator();
}
