/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.spi;

import net.sf.jtemporal.Instant;
import net.sf.jtemporal.Period;
import net.sf.jtemporal.TimedObject;
import net.sf.jtemporal.util.CloseableIterator;

/**
 * A skeleton for building a proxy. 
 * Delegates all the calls to a given instance of 
 * {@linkplain TemporalAttributeStorage}.  You can intercept any delegate call
 * by overriding the method. <br> 
 * It has been marked as abstract, even there is nothing abstract inside,
 * since it does not make sense to use it without subclassing it.
 * @author Thomas A Beck
 * @version $Id: TemporalAttributeStorageProxy.java,v 1.4 2008/12/23 20:08:19 tabeck Exp $
 */
abstract public class TemporalAttributeStorageProxy<T>
  implements TemporalAttributeStorage<T> {

    private final TemporalAttributeStorage<T> storage;

	/**
	 * Creates a new instance on top of an existing storage.
	 * @param storage the storage whose updates are interesting you.
	 */
    public TemporalAttributeStorageProxy(TemporalAttributeStorage<T> storage) {
		this.storage = storage;		
	}

	public Period lastPeriod() {
		return storage.lastPeriod();
	}

	public Period lastPeriod(Period p) {
		return storage.lastPeriod(p);
	}

	public Period firstPeriod() {
		return storage.firstPeriod();
	}

	public Period firstPeriod(Period p) {
		return storage.firstPeriod(p);
	}

	public boolean isEmpty() {
		return storage.isEmpty();
	}

	public boolean isEmpty(Period p) {
		return storage.isEmpty(p);
	}

	public boolean containsValue(Object value) {
		return storage.containsValue(value);
	}

	public boolean containsValue(Object value, Period range) {
		return storage.containsValue(value, range);
	}

    public T getValue(Instant instant) {
		return storage.getValue(instant);
	}

	public Period getPeriod(Instant instant) {
		return storage.getPeriod(instant);
	}

    public TimedObject<T> getEntry(Instant instant) {
		return storage.getEntry(instant);
	}

    public TimedObject<T> getEntryEndingAt(Instant instant) {
		return storage.getEntryEndingAt(instant);
	}

    public TimedObject<T> getEntryStartingAt(Instant instant) {
		return storage.getEntryStartingAt(instant);
	}

	public void clear() {
		storage.clear();
	}

	public int size() {
		return storage.size();
	}

	public int size(Period p) {
		return storage.size(p);
	}
	
	/////////////////////////////////////////////////////////////

	public void beginAtomicOperation() {
		storage.beginAtomicOperation();
	}

	public void endAtomicOperation() {
		storage.endAtomicOperation();
	}

    public void put(Period p, T value) {
		storage.put(p, value);
	}

	public void removeEntry(Period p) {
		storage.removeEntry(p);
	}
	
	public int removeRange(Period range) {
		return storage.removeRange(range);
	}

    public CloseableIterator<TimedObject<T>> entryIterator() {
		return storage.entryIterator();
	}

    public CloseableIterator<TimedObject<T>> entryIterator(Period p) {
		return storage.entryIterator(p);
	}

    public CloseableIterator<Period> periodIterator() {
		return storage.periodIterator();
	}

    public CloseableIterator<Period> periodIterator(Period p) {
		return storage.periodIterator(p);
	}
}
