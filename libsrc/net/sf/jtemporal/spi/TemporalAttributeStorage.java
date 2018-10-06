/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.spi;

import java.util.NoSuchElementException;

import net.sf.jtemporal.*;
import net.sf.jtemporal.util.CloseableIterator;

/**
 * Defines the contract the storage must fulfil to store and retrieve 
 * values.
 * <br> You typically implement this interface when you want to provide 
 * persistence.  
 * <br> {@linkplain TemporalAttributeImpl} talks to the storage using 
 * this interface, in order to provide a {@linkplain TemporalAttribute} implementation.
 * @author Thomas A Beck
 * @version $Id: TemporalAttributeStorage.java,v 1.5 2008/12/14 22:15:46 tabeck Exp $
 * @see net.sf.jtemporal.TemporalSetImpl
 * @param <T> the type of the value that is stored
 */
public interface TemporalAttributeStorage<T> {

	/**
	 * Returns the last (highest) period currently in this storage.
	 * @return the last (highest) period currently in this storage.
	 * @throws NoSuchElementException if the storage is empty.
	 */
	Period lastPeriod();

	/**
	 * Returns the last (highest) period currently in this storage,
	 * that intersects the given period.
	 * <br>Note the the returned period may be larger than p.
	 * @param p the intersecting period
	 * @return the last (highest) period currently in this storage.
	 * @throws    NoSuchElementException if none.
	 */
	Period lastPeriod(Period p);

	/**
	 * Returns the first (lowest) period currently in this storage.
	 *
	 * @return the first (lowest) period currently in this storage.
	 * @throws NoSuchElementException if this storage is empty.
	 */
	Period firstPeriod();

	/**
	 * Returns the first (lowest) period currently in this storage,
	 * that intersects the given period.
	 * <br>Note the the returned period may be larger than p.
	 * @param p the intersecting period
	 * @return the first (lowest) period currently in this storage.
	 * @throws NoSuchElementException if none.
	 */
	Period firstPeriod(Period p);

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
	 * Returns <tt>true</tt> if this map maps one or more Periods to the
	 * specified value.
	 * @param value value whose presence in this map is to be tested.
	 * @return <tt>true</tt> if this map maps one or more keys to the
	 *         specified value.
	 */
    boolean containsValue(Object value);

	/**
	 * Returns <tt>true</tt> if this map maps one or more Periods to the
	 * specified value, during the specified period.
	 * @param value value whose presence in this map is to be tested.
	 * @param range the period intersecting the value mappings
	 * @return <tt>true</tt> if this map maps one or more keys to the
	 *         specified value.
	 */
    boolean containsValue(Object value, Period range);

	/**
	 * Returns the value to which this map maps the specified Instant.  
	 * Returns null if the map contains no mapping at this Instant.
	 * It is not possible to map to a null Instant.
	 * @param instant instant whose associated value is to be returned
	 * @return the value to which this map maps the specified instant.
	 * returns <tt>null</tt> if this map does not contain a mapping for the
	 * specified instant.
	 */
    T getValue(Instant instant);

	/**
	 * Returns the period that encloses the given instant, if any is found.
	 * @param instant instant whose associated value is to be returned
	 * @return the value to which this map maps the specified instant.
	 * returns <tt>null</tt> if this map does not contain a mapping for the
	 * specified instant.
	 */
	Period getPeriod(Instant instant);

	/**
	 * Returns the entry valid at the given instant, if any is found.
	 * @param instant instant whose associated value is to be returned
	 * @return the period and value to which this map maps the specified instant.
	 * returns <tt>null</tt> if this map does not contain a mapping for the
	 * specified instant.
	 */
    TimedObject<T> getEntry(Instant instant);
	
	/**
	 * Returns the entry finishing exactly at the given instant, if any is found.
	 * @param instant the instant the equals the entry end
	 * @return the entry ending exactly at the given instant, or <tt>null</tt>.
	 */
    TimedObject<T> getEntryEndingAt(Instant instant);
	
	/**
	 * Returns the entry starting exactly at the given instant, if any is found.
	 * @param instant the instant the equals the entry end
	 * @return the entry starting exactly at the given instant, or <tt>null</tt>.
	 */
    TimedObject<T> getEntryStartingAt(Instant instant);

	/**
	 * Tells the storage that the JTemporal client is calling a method
	 * that can result in multiples updates in the storage. <br>
	 * This is not exactly a transaction. <br>
	 * If the storage supports transactions, this method suggests 
	 * to perform all the following operations inside the same transaction. <br>
	 * For example, this method is called by {@linkplain TemporalAttributeImpl}
	 * at the beginning of the 
	 * {@linkplain TemporalAttributeImpl#put(Period, Object) put(Period, Object)}
	 * method, then TemporalAttributeImpl calls some times 
	 * the put and remove methods, then finally it calls
	 * {@linkplain #endAtomicOperation}. <br>
	 * If your storage supports nested transactions, this is the inner one.
	 * Non persistent implemetations just return with no action. <br>
	 * Must not throw any exception (including unchecked exceptions).
	 * @see #endAtomicOperation()
	 */
	void beginAtomicOperation();

	/**
	 * Tells the storage that an atomic operation has finished.
	 * If the storage supports transactions, it should commit 
	 * if a transaction has been started by {@linkplain #beginAtomicOperation}.  <br>
	 * Non persistent implemetations just return with no action. <br>
	 * Must not throw any exception (including unchecked exceptions).
	 * @see #beginAtomicOperation()
	 */
	void endAtomicOperation();

	/**
	 * Removes all the stored mappings.
	 *
     * @throws UnsupportedOperationException clear is not supported by this map.
	 * @throws RuntimeException (or any other subclass) if the operation could not be performed. 
	 */
	void clear();

	/**
	 * Returns the number of Period-value mappings in this map.  If the
	 * map contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
	 * <tt>Integer.MAX_VALUE</tt>.
	 * @return the number of Instant-value mappings in this map.
	 */
	int size();
	
	/**
	 * Returns the number of Period-value mappings in this map, for the given priod.  
	 * If the map contains more than <tt>Integer.MAX_VALUE</tt> elements, 
	 * returns <tt>Integer.MAX_VALUE</tt>.
	 * @param p the intersecting period
	 * @return the number of Instant-value mappings in this map.
	 */
	int size(Period p);

	/**
	 * Stores a mapping (entry, tuple, row) composed by period and value. <br>
	 * No particular check or rich semantics is expected, this method just stores.
	 * @param p period with which the specified value is associated.  Cannot be null.
	 * @param value value to be associated with the specified key.  Cannot be null.
	 * @throws UnsupportedOperationException if the <tt>put</tt> operation is
	 *	          not supported by this storage.
	 * @throws IllegalArgumentException if some aspect of this period or value
	 *	          prevents it from being stored.
	 * @throws RuntimeException (or any other subclass) if the tuple could not be stored
	 *              for other reasons. 
	 */
    void put(Period p, T value);

	/**
	 * Removes an entry composed by period and value previously stored. <br>
	 *
	 * @param p period whose mapping is to be removed from the storage.  It 
	 * must exactly match the key of an existing entry.
	 * @throws UnsupportedOperationException if the <tt>remove</tt> method is
	 *         not supported by this storage.
	 * @throws java.util.NoSuchElementException if the entry is not found
	 * @throws IllegalArgumentException if p is null
	 */
	void removeEntry(Period p);
	
	/**
	 * Removes all the entries whose period indersects the given period.
	 * @param p the period specifying that range whose interecting entries are 
	 * to be deleted.
	 * @return the number of deleted entries (or Integer.MAX_VALUE if bigger
	 * than that). 
	 * @throws UnsupportedOperationException if the <tt>remove</tt> method is
	 *         not supported by this storage.
	 * @throws IllegalArgumentException if p is null
	 */
	int removeRange(Period p);  

	/**
	 * Iterates over all the entries of the storage.
	 * <br>The instances are sorted by {@linkplain Period#equals(Object)}
	 * @return instances of {@linkplain TimedObject}
	 * @see TimedObject
	 */
    CloseableIterator<TimedObject<T>> entryIterator();

	/**
	 * Iterates over all the entries of the storage that intersects the period P. <br>
	 * Note that the CloseableIterator#remove() method, removes the whole entry, even
	 * if the entry has a period lager then the period p.
	 * <br>The instances are sorted by {@linkplain Period#equals(Object)}
	 * @param p the intersecting period.
	 * @return instances of {@linkplain TimedObject}
	 * @see TimedObject
	 */
    CloseableIterator<TimedObject<T>> entryIterator(Period p);

	/**
	 * Iterates over all the periods of the storage.
	 * <br>The instances are sorted by {@linkplain Period#equals(Object)}
	 * @return instances of {@linkplain Period}
	 * @see Period
	 */
    CloseableIterator<Period> periodIterator();

	/**
	 * Iterates over all the periods of the storage that intersects the period P. <br>
	 * Note that the CloseableIterator#remove() method, removes the whole entry, even
	 * if the entry has a period lager then the period p.
	 * <br>The instances are sorted by {@linkplain Period#equals(Object)}
	 * @param p the intersecting period.
	 * @return instances of {@linkplain Period}
	 * @see Period
	 */
    CloseableIterator<Period> periodIterator(Period p);
}
