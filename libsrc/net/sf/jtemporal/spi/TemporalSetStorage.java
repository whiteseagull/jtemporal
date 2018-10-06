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
 * <br> You typically implement this interface to provide persistence.  
 * <br> {@linkplain TemporalSetImpl} talks to the storage using 
 * this interface, in order to provide a {@linkplain TemporalSet} implementation.
 * @author Thomas A Beck
 * @version $Id: TemporalSetStorage.java,v 1.6 2009/01/02 17:05:04 tabeck Exp $
 * @see net.sf.jtemporal.TemporalAttributeImpl
 * @param <T> the type of the value held by the storage 
 */
public interface TemporalSetStorage<T> {

	/**
	 * Returns the last (highest) period currently defined for the given value.
	 * @return the last (highest) period currently defined for the given value.
	 * @param value the values whose last period must be returned.
	 * @throws NoSuchElementException if the value is not found.
	 */
	Period lastPeriod(T value);
	
	/**
	 * Returns the first (lowest) period currently defined for the given value.
	 * @return the first (lowest) period currently defined for the given value.
	 * @param value the values whose first period must be returned.
	 * @throws    NoSuchElementException if the value is not found.
	 */
	Period firstPeriod(T value);

	/**
	 * Returns <tt>true</tt> if this storage does not contain entries.
	 * @return <tt>true</tt> if this storage does not contain entries.
	 */
	boolean isEmpty();

	/**
	 * Returns <tt>true</tt> if this storage does not contain entries 
	 * at the given instant.
	 * @param i the instant to be checked
	 * @return <tt>true</tt> if this storage does not contain entries
	 * at the given instant.
	 */
	boolean isEmpty(Instant i);

	/**
	 * Returns <tt>true</tt> if this storage contains one or more mappings
	 * for the specified value.
	 * @param value value whose presence in this map is to be tested.
	 * @return <tt>true</tt> if this map maps one or more keys to the
	 *         specified value.
	 */
	boolean containsValue(Object value);

	/**
	 * Returns <tt>true</tt> if this storage contains a mapping
	 * for the specified value at the given instant.
	 * @param i the instant to be checked
	 * @param value value whose presence in this map is to be checked.
	 * @return <tt>true</tt> if this map maps to the given value at
	 * the given instant.
	 */
	boolean containsValue(Instant i, Object value);

	/**
	 * Returns the period of the mapping valid at the specified instant for the given value.
	 * @return the period of the mapping valid at the specified instant for the given value.
	 * Returns <tt>null</tt> if this map does not contain a mapping for the given value at the
	 * specified instant.
	 * @param instant the instant where the value is valid
	 * @param value valid for the returned period.  The returned period 
	 * contains the passed instant.
	 */
	Period getPeriod(Instant instant, T value);
	
	/**
	 * Returns the period finishing exactly at the given instant, 
	 * for the given value, if any is found.
	 * @param instant the instant the equals the entry end
	 * @param value of the entry finishing at the given instant
	 * @return the Period ending exactly at the given instant, or <tt>null</tt>.
	 */
	Period getPeriodEndingAt(Instant instant, T value);
	
	/**
	 * Returns the period starting exactly at the given instant, 
	 * for the given value, if any is found.
	 * @param instant the instant the equals the entry end
	 * @param value of the entry starting at the given instant
	 * @return the Period starting exactly at the given instant, or <tt>null</tt>.
	 */
	Period getPeriodStartingAt(Instant instant, T value);

	/**
	 * Tells the storage that the JTemporal client is calling a method
	 * that can result in multiples updates in the storage. <br>
	 * This is not exactly a transaction. <br>
	 * If the storage supports transactions, this method suggests 
	 * to perform all the following operations inside the same transaction. <br>
	 * For example, this method is called by {@linkplain TemporalSetImpl}
	 * at the beginning of the 
	 * {@linkplain TemporalSetImpl#put(Period, Object) put(Period, Object)}
	 * method, then TemporalSetImpl calls some times 
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
	 * @throws UnsupportedOperationException clear is not supported by this
	 * 		  map.
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
	 * Returns the number of entries in this map whose value equals the given one.  
	 * If the there are more than <tt>Integer.MAX_VALUE</tt> elements, returns
	 * <tt>Integer.MAX_VALUE</tt>.
	 * @return the number of entries in this map whose value equals the given one.
	 * @param value the value of the entries to be counted
	 */
	int sizeFor(T value);
	
	/**
	 * Returns the number of distinct values in this storage.  If there
	 * are more than <tt>Integer.MAX_VALUE</tt> elements, returns
	 * <tt>Integer.MAX_VALUE</tt>.
	 * @return the number of distinct values in this map.
	 */
	int sizeValues();

	/**
	 * Returns the number of entries, at the given instant.  
	 * If there are more than <tt>Integer.MAX_VALUE</tt> elements, 
	 * returns <tt>Integer.MAX_VALUE</tt>.
	 * @param i the instant to be checked
	 * @return the number of entries intersecting the given instant.
	 */
	int sizeAt(Instant i);

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
	 * Removes a mapping (entry, tuple, row) composed by period and value previously stored. <br>
	 * <br>The period and the value must match the parameter previously used
	 * to call the put method.
	 *
	 * @param p the period of the entry to be removed
	 * @param value the value of the entry to be removed
	 * @throws UnsupportedOperationException if the <tt>remove</tt> method is
	 *         not supported by this storage.
	 * @throws java.util.NoSuchElementException if the entry is not found
	 * @throws IllegalArgumentException if p is null
	 */
	void remove(Period p, T value);

	/**
	 * Removes all the entries having the given value. <br>
	 * @param value the value of the entry to be removed
	 * @return <code>true</code> if the storage contained at least one entry 
	 * for the given value
	 * @throws IllegalArgumentException if value is null
	 */
	boolean removeValue(T value);

	/**
	 * Iterates over all the entries of the storage.
	 * <br>The instances are sorted by {@linkplain Period#equals(Object)}
	 * @return instances of {@linkplain TimedObject}
	 * @see TimedObject
	 */
	CloseableIterator<TimedObject<T>> entryIterator();

	/**
	 * Returns an iterator looping over the entries contained
	 * et the given instant.
	 * @param instant instant whose entries are to be returned
	 * @return an iterator of instances of {@linkplain TimedObject}
	 * @see TimedObject
	 */
	CloseableIterator<TimedObject<T>> entryIterator(Instant instant);

	/**
	 * Iterates over all the entries of the storage that intersects the period P. <br>
	 * Note that the CloseableIterator#remove() method, removes the whole entry, even
	 * if the entry has a period lager then the period p.
	 * <br>The instances are sorted by {@linkplain Period#equals(Object)}
	 * @param period the intersecting period.
	 * @return instances of {@linkplain TimedObject}
	 * @see TimedObject
	 */
	CloseableIterator<TimedObject<T>> entryIterator(Period period);

	/**
	 * Iterates over all the entries of the storage for the given value 
	 * that intersects the given period. <br>
	 * Note that the CloseableIterator#remove() method, removes the whole entry, even
	 * if the entry has a period larger then the given period.
	 * <br>The instances are sorted by {@linkplain Period#equals(Object)}
	 * @param period the intersecting period.
	 * @param value the value to be iterated
	 * @return all the periods that intersects the given period
	 * @see TimedObject
	 */
	CloseableIterator<Period> periodIterator(Period period, T value);

	/**
	 * Iterates over all the periods of the entries having the given value. <br>
	 * <br>The instances are sorted by {@linkplain Period#compareTo(Object)}
	 * @param value the selected value
	 * @return instances of {@linkplain Period}
	 * @see Period
	 */
	CloseableIterator<Period> periodIterator(T value);

	/*
	 * Iterates over all the periods of the entries that intersects the period P
	 * and having the given value. <br>
	 * @param p the intersecting period.
	 * @param value the selected value
	 * @return instances of {@linkplain Period}
	 * @see Period
	 * @todo will this method be used ? entryIterator can be used anyway
	 */
	//CloseableIterator periodIterator(Period p, Object value);

	/**
	 * Iterates over all the distinct values of the entries in the storage.
	 * @return instances of Object
	 */
	CloseableIterator<T> valueIterator();

	/**
	 * Iterates over the set of values valid at the given instant. <br>
	 * Returns a read-only iterator.
	 * @param instant the instant where all the returned value are valid
	 * @return instances of Object, the iterator is read-only
	 */
	CloseableIterator<T> valueIterator(Instant instant);
}
