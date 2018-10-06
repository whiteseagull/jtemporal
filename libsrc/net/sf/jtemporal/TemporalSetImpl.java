/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;

import java.util.AbstractSet;
import java.util.Set;
import java.util.Iterator;

import net.sf.jtemporal.spi.TemporalSetStorage;
import net.sf.jtemporal.util.CloseableIterator;

/**
 * The standard implementation of {@linkplain TemporalSet}. <br>
 * Takes care about temporal logic, and delegates all the data storage
 * and retrieval to a {@linkplain TemporalSetStorage}.
 * @TODO complete
 * @author Thomas A Beck
 * @version $Id: TemporalSetImpl.java,v 1.8 2009/01/02 14:55:40 tabeck Exp $
 * @param <V> the type of the value
 */
public class TemporalSetImpl<V> extends AbstractTemporalSet<V> {

	private final TemporalSetStorage<V> storage;
	private int atomicLevel = 0;

	/**
	 * Creates a new {@linkplain TemporalSet} using the give storage.
	 * @param storage the storage containing all the data.
	 */
	@SuppressWarnings("unchecked")
	public TemporalSetImpl(TemporalSetStorage<? super V> storage) {
		this.storage = (TemporalSetStorage<V>)storage;
	}

	private void beginAtomic() {
		if (this.atomicLevel == 0) {
			this.storage.beginAtomicOperation();
		}
		this.atomicLevel++;
	}	

	private void endAtomic() {
		this.atomicLevel--;
		if (this.atomicLevel == 0) {
			this.storage.endAtomicOperation();
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalSet#clear()
	 */
	public void clear() {
		this.storage.clear();
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalSet#size()
	 */
	public int size() {
		return this.storage.size();
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalSet#sizeOf(java.lang.Object)
	 */
	public int sizeFor(V value) {
		return this.storage.sizeFor(value);
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalSet#sizeAt(net.sf.jtemporal.Instant)
	 */
	public int sizeAt(Instant instant) {
		return this.storage.sizeAt(instant);
	} 

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalSet#sizeValues()
	 */
	public int sizeValues() {
		return this.storage.sizeValues();
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalSet#isEmpty()
	 */
	public boolean isEmpty() {
		return this.storage.isEmpty();
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalSet#isEmpty(net.sf.jtemporal.Instant)
	 */
	public boolean isEmpty(Instant i) {
		return this.storage.isEmpty(i);
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalSet#valueSet(net.sf.jtemporal.Instant)
	 */
	public Set<V> valueSet(final Instant instant) {
		return new ValueSetAt<V>(this, instant) {
			@Override
			public Iterator<V> iterator() {
				return TemporalSetImpl.this.storage.valueIterator(instant);
			}
		};
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalSet#valueSet()
	 */
	public Set<V> valueSet() {
		return new ValueSet<V>(this) {
			@Override
			public Iterator<V> iterator() {
				return TemporalSetImpl.this.storage.valueIterator();
			}
		};
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalSet#entrySet()
	 */
	public Set<TimedObject<V>> entrySet() {
		return new EntrySet<V>(this) {
			@Override
			public Iterator<TimedObject<V>> iterator() {
				return TemporalSetImpl.this.storage.entryIterator();				
			}
		};
	} 

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalSet#getPeriod(net.sf.jtemporal.Instant, java.lang.Object)
	 */
	public Period getPeriod(Instant instant, V value) {
		return storage.getPeriod(instant, value);
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalSet#contains(net.sf.jtemporal.Instant, java.lang.Object)
	 */
	public boolean contains(Instant instant, Object value) {
		return storage.containsValue(instant, value);
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalSet#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		return storage.containsValue(value);
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalSet#put(net.sf.jtemporal.Period, java.lang.Object)
	 */
	public boolean put(Period _p, V _value) {
		boolean ret;
		Period p = _p;
		V value = _value;

		if (value == null) throw new IllegalArgumentException("value == null");
		if (p == null)     throw new IllegalArgumentException("p == null");

		//this.versionNumber++; @TODO implement versionnumber

		//TODO remove redundancy with TemporalAttributeImpl#put(Period,Object) 
		// by creating a stateful StorageClient that uses an implicit current value


		this.beginAtomic();
		try {
			ret = this.remove(p, value);

			// 1,2: remove fragmentation

			//1. check whether to merge with the next period
			Period after = this.storage.getPeriodStartingAt(p.getEnd(), value);
			if (after != null /*&& after.getValue().equals(value)*/) {
				// let's merge
				this.storage.remove(after, value);
				p = new Period(p.getStart(), after.getEnd());
			}

			//2. check whether to merge with the previous period
			Period before = this.storage.getPeriodEndingAt(p.getStart(), value);
			if (before != null /*&& before.getValue().equals(value)*/) {
				// let's merge
				this.storage.remove(before, value);
				p = new Period(before.getStart(), p.getEnd()); 
			}

			// throws RuntimeException
			this.storage.put(p, value);
		}
		finally {		 
			this.endAtomic();
		}

		return ret;

	} // put

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalSet#remove(net.sf.jtemporal.Period, java.lang.Object)
	 */
	public boolean remove(Period p, V value) {
		if (p == null) throw new IllegalArgumentException("p == null");

		//this.versionNumber++; @TODO implement versionnumber

		//TODO remove redundancy with TemporalAttributeImpl#remove(Period) 
		// by creating a stateful StorageClient that uses an implicit current value

		// avoid updating the storage while an iterator is open, 
		// since some implementations may throw
		// a ConcurrentModificationException

		boolean removed = false;
		Instant startRemove = p.getStart();
		Instant endRemove = p.getEnd();

		this.beginAtomic();
		try {

			//first overlapping entry
			Period firstPeriod = storage.getPeriod(startRemove, value);

			if (firstPeriod != null) {
				storage.remove(firstPeriod, value);
				removed = true;

				if (firstPeriod.getStart().compareTo(startRemove) < 0) {  // if startPeriod.start < start
					// we have removed too much : fix
					storage.put(firstPeriod.precedingPeriod(p), value);
				}

				if (firstPeriod.getEnd().compareTo(endRemove) > 0) { // if startPeriod.end > end
					// we have removed too much : fix
					storage.put(firstPeriod.succedingPeriod(p), value);
				}
			}

			//get remaining overlapping entries
			CloseableIterator<Period> i = this.storage.periodIterator(p, value);
			while (i.hasNext()) {
				Period period = i.next();
				removed = true;
				i.remove();

				if (period.getEnd().compareTo(endRemove) > 0) { 
					// if end > endRemove
					i.close();
					storage.put(period.succedingPeriod(p), value);
					break;
				}						
			}

			i.close(); //if already closed, ignored
		}
		finally {
			this.endAtomic();	
		}

		return removed; 
	} // remove(period, value)

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalSet#removeValue(java.lang.Object)
	 */
	public boolean removeValue(V value) {
		return storage.removeValue(value);
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalSet#removePeriod(net.sf.jtemporal.Period)
	 */
	public boolean removePeriod(Period p) {
		boolean ret = false;
		CloseableIterator<TimedObject<V>>  i = this.storage.entryIterator(p);
		try {
			while (i.hasNext()) {
				TimedObject<V> entry = i.next();
				this.remove(p, entry.getValue());
				ret = true;
			}
		}
		finally {
			i.close();
		}

		return ret;
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalSet#periodSet(java.lang.Object)
	 */
	public Set<Period> periodSet(final V value) {
		return new PeriodSet<V>(this, value) {
			@Override
			public Iterator<Period> iterator() {
				return TemporalSetImpl.this.storage.periodIterator(value);
			}
		};
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalSet#firstInstant(java.lang.Object)
	 */
	public Instant firstInstant(V value) {
		return this.storage.firstPeriod(value).getStart();
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalSet#lastInstant(java.lang.Object)
	 */
	public Instant lastInstant(V value) {
		return this.storage.lastPeriod(value).getEnd();
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalSet#firstPeriod(java.lang.Object)
	 */
	public Period firstPeriod(V value) {
		return this.storage.firstPeriod(value);
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalSet#lastPeriod(java.lang.Object)
	 */
	public Period lastPeriod(V value) {
		return this.storage.lastPeriod(value);
	}


	private static abstract class PeriodSet<U> extends AbstractSet<Period> {

		private final TemporalSet<U> parent;
		private final U value;

		PeriodSet(TemporalSet<U> parent, U value) {
			this.parent = parent;
			this.value = value;
		}

		@Override
		public int size() {
			return parent.sizeFor(this.value);
		}

		@Override
		public abstract Iterator<Period> iterator();

		@Override
		public void clear() {
			parent.removeValue(value);
		}

		@Override
		public boolean contains(Object o) {
			if (o == null) {
				throw new IllegalArgumentException("null");
			}

			if (o instanceof Instant) {
				return (parent.contains( (Instant)o, value)) ;
			}

			if (o instanceof Period) {
				Period p = parent.getPeriod(((Period) o).getStart(), value);
				return (p != null  &&  p.equals(o)) ;
			}

			throw new ClassCastException();
		}

		@Override
		public boolean remove(Object o) {
			if (o instanceof Period) {
				return parent.remove((Period)o, value);
			}

			throw new ClassCastException();
		}
	} // PeriodSet

	private static abstract class ValueSet<U> extends AbstractSet<U> {

		private final TemporalSet<U> parent;

		ValueSet(TemporalSet<U> parent) {
			this.parent = parent;
		}

		@Override
		public int size() {
			return this.parent.sizeValues();
		}

		@Override
		public abstract Iterator<U> iterator();

		@Override
		public void clear() {
			this.parent.clear();
		}

		@Override
		public boolean contains(Object o) {
			if (o == null) {
				throw new IllegalArgumentException("null");
			}
			return parent.containsValue(o);
		}

		@SuppressWarnings("unchecked")
		public boolean remove(Object o) {
			return parent.removeValue((U)o);
		}
	}

	private static abstract class ValueSetAt<U> extends AbstractSet<U> {

		private final TemporalSet<U> parent;
		private final Instant instant;

		ValueSetAt(TemporalSet<U> parent, Instant instant) {
			this.parent = parent;
			this.instant = instant;
		}

		@Override
		public int size() {
			return parent.sizeAt(instant);
		}

		@Override
		public abstract Iterator<U> iterator();

		@Override
		public void clear() {
			throw new UnsupportedOperationException("An instant cannot be cleared");
		}

		@Override
		public boolean contains(Object o) {
			if (o == null) {
				throw new IllegalArgumentException("null");
			}
			return parent.contains(instant, o);
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException("A period must be specified");
		}

		/* (non-Javadoc)
		 * @see java.util.Set#add(java.lang.Object)
		 */
		@Override
		public boolean add(Object o) {
			throw new UnsupportedOperationException("A period must be specified");
		}
	}

	private static abstract class EntrySet<U> extends AbstractSet<TimedObject<U>> {

		private final TemporalSet<U> parent;

		EntrySet(TemporalSet<U> parent) {
			this.parent = parent;
		}

		@Override
		public int size() {
			return this.parent.size();
		}

		@Override
		public abstract Iterator<TimedObject<U>> iterator();

		@Override
		public void clear() {
			this.parent.clear();
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean contains(Object o) {
			if (o == null) {
				throw new IllegalArgumentException("null");
			}

			TimedObject<U> to = (TimedObject<U>)o;
			Period p = parent.getPeriod(to.getPeriod().getStart(), to.getValue());
			return p != null && p.equals(to.getPeriod());
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean remove(Object o) {
			if (o == null) {
				throw new IllegalArgumentException("null");
			}

			TimedObject<U> to = (TimedObject<U>)o;
			Period p = parent.getPeriod(to.getPeriod().getStart(), to.getValue());
			U value = to.getValue();			

			return parent.remove(p, value);
		}
	}
}

