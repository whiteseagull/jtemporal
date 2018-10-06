/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;

import java.io.Serializable;
import java.util.AbstractSet;

import java.util.Iterator;
import java.util.Set;

import net.sf.jtemporal.spi.TemporalAttributeStorage;

/**
 * Implements TemporalAttribute by using a TemporalAttributeStorage.
 * <br>To achieve this goal, some temporal logic is added to the put and remove
 * methods, and some collection-oriented behaviour is provided on top of the storage.
 * @author Thomas A Beck
 * @version $Id: TemporalAttributeImpl.java,v 1.19 2008/12/15 20:26:31 tabeck Exp $
 * @param <V> the type of the value
 * @todo implement versionnumber
 * @todo : cleanup generics usage in private code 
 * @todo add atomicoperation to the appropriate methods
 */
		
public class TemporalAttributeImpl<V> extends AbstractTemporalAttribute<V>
	implements Serializable
{
    private static final long serialVersionUID = 6546879831380212674L;
	
    private final TemporalAttributeStorage<V> storage;
	private int atomicLevel = 0;

	/**
	 * @param storage it is fine to pass a generic Object storage
	 */
    @SuppressWarnings("unchecked")
	public TemporalAttributeImpl(TemporalAttributeStorage<? super V> storage) {
		this.storage = (TemporalAttributeStorage<V>) storage; //unchecked cast
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

    /**
     * Returns the storage that has been passed to the constructor 
     * to build this TemporalAttribute.
     * @return the storage
     */
    public TemporalAttributeStorage<? super V> getStorage() {
        return this.storage;
    }
    
	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalAttribute#clear()
	 */
	public void clear() {
		this.storage.clear();
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalAttribute#size()
	 */
	public int size() {
		return this.storage.size();
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalAttribute#isEmpty()
	 */
	public boolean isEmpty() {
		return this.storage.isEmpty();
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.ReadableTemporalAttribute#get(net.sf.jtemporal.Instant)
	 */
	public V get(Instant instant) {
		return this.storage.getValue(instant);
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalAttribute#getPeriod(net.sf.jtemporal.Instant)
	 */
	public Period getPeriod(Instant instant) {
		return this.storage.getPeriod(instant);
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalAttribute#getEntry(net.sf.jtemporal.Instant)
	 */
    @Override
	public TimedObject<V> getEntry(Instant instant) {
		return this.storage.getEntry(instant);
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalAttribute#containsInstant(net.sf.jtemporal.Instant)
	 */
	public boolean containsInstant(Instant instant) {
	  return getPeriod(instant) != null ;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalAttribute#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
	  return this.storage.containsValue(value);
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalAttribute#put(net.sf.jtemporal.Period, java.lang.Object)
	 */
	public boolean put(Period _p, V _value) {
		boolean ret;
		Period p = _p;
		V value = _value;

		if (value == null) throw new IllegalArgumentException("value == null");
		if (p == null)     throw new IllegalArgumentException("p == null");


		this.beginAtomic();
		try {
		    // optimisation : check first whether the put is actually needed, 
		    // or whether by chance the correct value is already set
		    TimedObject<V> prev = this.getEntry(_p.getStart());
		    if (
		        prev != null
		        && (prev.getPeriod().contains(_p))
		        && (prev.getValue().equals(_value))
		    ) {
		        // nothing to do, skip storage update, but
		        // report that there was already a value
		        ret = true;
		    }
		    else {
		        // ok, there is actually some work to do
		        
			    // first make the necessary place to put the new value
				ret = this.remove(p);
				 
				// 1,2: remove fragmentation
				 
				//1. check whether to merge with the next period
				TimedObject<V> after = this.storage.getEntryStartingAt(p.getEnd());
				if (after != null && after.getValue().equals(value)) {
				   // let's merge
				   this.storage.removeEntry(after.getPeriod());
				   value = after.getValue(); // I prefer the existing value
				   p = new Period(p.getStart(), after.getPeriod().getEnd());
				}

				//2. check whether to merge with the previous period
				TimedObject<V> before = this.storage.getEntryEndingAt(p.getStart());
				if (before != null && before.getValue().equals(value)) {
				 	// let's merge
					this.storage.removeEntry(before.getPeriod());
					value = before.getValue(); // I prefer the existing instance
				 	p = new Period(before.getPeriod().getStart(), p.getEnd()); 
				}
				
				// throws RuntimeException
				this.storage.put(p, value);
		    }
		    
		}
		finally {		 
			this.endAtomic();
		}

		return ret;
	}




	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalAttribute#remove(net.sf.jtemporal.Period)
	 */
	public boolean remove(Period p) {
		if (p == null) throw new IllegalArgumentException("p == null");

		//this.versionNumber++; @TODO implement versionnumber
		
		// avoid updating the storage while an iterator is open, 
		// since some implmentations may throw
		// a ConcurrentModificationException
		
		boolean removed = false;
		Instant startRemove = p.getStart();
		Instant endRemove = p.getEnd();

		this.beginAtomic();
		try {
            TimedObject<V> firstEntry = storage.getEntry(startRemove);
            TimedObject<V> lastEntry  = storage.getEntry(endRemove);

			if (lastEntry != null
			    && lastEntry.getPeriod().getStart().equals(endRemove)
			) {
			    // actually lastEntry does not intersect p
			    lastEntry = null;
			}
			Period		firstPeriod = firstEntry == null ? null : firstEntry.getPeriod();
			Period		lastPeriod  = lastEntry  == null ? null : lastEntry.getPeriod();

			int cnt = storage.removeRange(p);
			removed = (cnt > 0); 

			//first overlapping entry
		    if (firstEntry != null 
		    	&& firstPeriod.getStart().compareTo(startRemove) < 0) {  // if startPeriod.start < start
		       // we have removed too much : fix
			   storage.put(
				 firstPeriod.precedingPeriod(p), 
				 firstEntry.getValue()
			   );
		    }
		      
			//last overlapping entry (eventually the same as the first)
		    if (lastEntry != null
		      	&& lastPeriod.getEnd().compareTo(endRemove) > 0) { // if startPeriod.end > end
		       // we have removed too much : fix
			   storage.put(
				 lastPeriod.succedingPeriod(p), 
			     lastEntry.getValue()
			   );
			}
		}
		finally {
			this.endAtomic();	
		}
		return removed; 
	} // remove(Period p)


	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalAttribute#periodSet()
	 */
	public Set<Period> periodSet() {
		return new PeriodSet(this) {
            @Override
            public Iterator<Period> iterator() {
				return TemporalAttributeImpl.this.storage.periodIterator();
			}
		};
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalAttribute#periodSet()
	 */
	public Set<TimedObject<V>> entrySet() {
        return new EntrySet<V>(this) {
            @Override
            public Iterator<TimedObject<V>> iterator() {
				return TemporalAttributeImpl.this.storage.entryIterator();
			}
		};
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalAttribute#firstInstant()
	 */
	public Instant firstInstant() {
		return this.storage.firstPeriod().getStart();
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalAttribute#lastInstant()
	 */
	public Instant lastInstant() {
		return this.storage.lastPeriod().getEnd();
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalAttribute#firstPeriod()
	 */
	public Period firstPeriod() {
		return this.storage.firstPeriod();
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalAttribute#lastPeriod()
	 */
	public Period lastPeriod() {
		return this.storage.lastPeriod();
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TemporalAttribute#subMap(net.sf.jtemporal.Period)
	 */
	public TemporalAttribute<V> subMap(Period p) {
		return new SubMap(p);
	}

	/* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return storage.toString();
    }
	
	/////////////////////////////////////////////////////////////////////
	private class SubMap extends AbstractTemporalAttribute<V>	{
		
		private final Period range;

		SubMap(Period p) {
		  this.range = p;
		}

		/* (non-Javadoc)
		 * @see net.sf.jtemporal.AbstractTemporalAttribute#clear()
		 */
		public void clear() {
			TemporalAttributeImpl.this.remove(range);
		}
	
		/* (non-Javadoc)
		 * @see net.sf.jtemporal.AbstractTemporalAttribute#size()
		 */
		public int size() {
			return TemporalAttributeImpl.this.storage.size(this.range);
		}
	
		/* (non-Javadoc)
		 * @see net.sf.jtemporal.AbstractTemporalAttribute#isEmpty()
		 */
		public boolean isEmpty() {
			return TemporalAttributeImpl.this.storage.isEmpty(this.range);
		}
	
		/* (non-Javadoc)
		 * @see net.sf.jtemporal.AbstractTemporalAttribute#get(net.sf.jtemporal.Instant)
		 */
		public V get(Instant instant) {
			if (!range.contains(instant)) {
				throw new IllegalArgumentException("instant " + instant
                                           + " out of range " + range);
			}
			return TemporalAttributeImpl.this.get(instant);
		}
	
		/* (non-Javadoc)
		 * @see net.sf.jtemporal.AbstractTemporalAttribute#getPeriod(net.sf.jtemporal.Instant)
		 */
		public Period getPeriod(Instant instant) {
			if (!range.contains(instant)) {
				throw new IllegalArgumentException("instant " + instant
                                           + " out of range " + range);
			}
			return TemporalAttributeImpl.this.getPeriod(instant).intersect(this.range);
		}
	
		/* (non-Javadoc)
		 * @see net.sf.jtemporal.AbstractTemporalAttribute#getPeriod(net.sf.jtemporal.Instant)
		 */
		@Override
		public TimedObject<V> getEntry(Instant instant) {
			if (!range.contains(instant)) {
				throw new IllegalArgumentException("instant " + instant
                                           + " out of range " + range
                );
			}

			TimedObject<V> to = TemporalAttributeImpl.this.getEntry(instant);
			return SimpleTimedObject.intersect(to, this.range);
		}
	
	
		/* (non-Javadoc)
		 * @see net.sf.jtemporal.AbstractTemporalAttribute#containsInstant(net.sf.jtemporal.Instant)
		 */
		public boolean containsInstant(Instant instant) {
			if (!range.contains(instant)) {
				throw new IllegalArgumentException("instant " + instant
                                           + " out of range " + range);
			}
			return TemporalAttributeImpl.this.containsInstant(instant);
		}
	
		/* (non-Javadoc)
		 * @see net.sf.jtemporal.AbstractTemporalAttribute#containsValue(java.lang.Object)
		 */
		public boolean containsValue(Object value) {
			return TemporalAttributeImpl.this.storage.containsValue(value); 
		}
	
		/* (non-Javadoc)
		 * @see net.sf.jtemporal.AbstractTemporalAttribute#put(net.sf.jtemporal.Period, java.lang.Object)
		 */
		public boolean put(Period p, V value) {
			if (!range.contains(p)) {
				throw new IllegalArgumentException("period " + p
                                           + " out of range " + range);
			}
			return TemporalAttributeImpl.this.put(p, value);
		}
	
		/* (non-Javadoc)
		 * @see net.sf.jtemporal.AbstractTemporalAttribute#remove(net.sf.jtemporal.Period)
		 */
		public boolean remove(Period p) {
			if (!range.contains(p)) {
				throw new IllegalArgumentException("period " + p
                                           + " out of range " + range);
			}
			return TemporalAttributeImpl.this.remove(p);
		}
	
		/* (non-Javadoc)
		 * @see net.sf.jtemporal.AbstractTemporalAttribute#periodSet()
		 */
		public Set<Period> periodSet() {
			return new PeriodSet(this) {
				@Override
				public Iterator<Period> iterator() {
					final Iterator<Period> storageIterator
					= TemporalAttributeImpl.this.storage.periodIterator(range); 
					return new Iterator<Period>() { // implement a proxy that limits to range

						public void remove() {
							storageIterator.remove();
						}

						public boolean hasNext() {
							return storageIterator.hasNext();
						}

						public Period next() {
							Period p =  storageIterator.next();
							return p.intersect(range);
						}						
					};
				}
			};
		}
	
		/* (non-Javadoc)
		 * @see net.sf.jtemporal.AbstractTemporalAttribute#firstInstant()
		 */
		public Instant firstInstant() {
			return TemporalAttributeImpl.this.storage.firstPeriod(range)
				.intersect(range).getStart();
		}
	
		/* (non-Javadoc)
		 * @see net.sf.jtemporal.AbstractTemporalAttribute#lastInstant()
		 */
		public Instant lastInstant() {
			return TemporalAttributeImpl.this.storage.lastPeriod(range)
				.intersect(range).getEnd();
		}
	
		/* (non-Javadoc)
		 * @see net.sf.jtemporal.AbstractTemporalAttribute#firstPeriod()
		 */
		public Period firstPeriod() {
			return TemporalAttributeImpl.this.storage.firstPeriod(range)
				.intersect(range);
		}
	
		/* (non-Javadoc)
		 * @see net.sf.jtemporal.AbstractTemporalAttribute#lastPeriod()
		 */
		public Period lastPeriod() {
			return TemporalAttributeImpl.this.storage.lastPeriod(range)
				.intersect(range);
		}
	
		/* (non-Javadoc)
		 * @see net.sf.jtemporal.AbstractTemporalAttribute#subMap(net.sf.jtemporal.Period)
		 */
		public TemporalAttribute<V> subMap(Period p) {
			if (!range.contains(p)) {
				throw new IllegalArgumentException("period " + p
                                     + " out of range " + range);
			}
			return TemporalAttributeImpl.this.subMap(p);
		}
	
		/* (non-Javadoc)
		 * @see net.sf.jtemporal.TemporalAttribute#entrySet()
		 */
		public Set<TimedObject<V>> entrySet() {
			return new EntrySet<V>(this) {
				@Override
				public Iterator<TimedObject<V>> iterator() {
					final Iterator<TimedObject<V>> storageIterator
						= TemporalAttributeImpl.this.storage.entryIterator(range)
					;

					return new Iterator<TimedObject<V>>() {
					// implement a proxy that limits to range
						public void remove() {
							storageIterator.remove();
						}

						public boolean hasNext() {
							return storageIterator.hasNext();
						}

						public TimedObject<V> next() {
							TimedObject<V> entry = storageIterator.next();
							return SimpleTimedObject.intersect(entry, range);
						} 						
					};
				};
			};
		}
	} // SubMap

	private static abstract class PeriodSet extends AbstractSet<Period> {
		
		private final TemporalAttribute<?> parent;
		
		public PeriodSet(TemporalAttribute<?> parent) {
			this.parent = parent;
		}

		@Override
		public int size() {
			return parent.size();
		}

		@Override
		public abstract Iterator<Period> iterator();

		@Override
		public void clear() {
			parent.clear();
		}

		@Override
		public boolean contains(Object o) {
			if (o == null) {
				throw new IllegalArgumentException("null");
			}

			if (o instanceof Instant) {
				return (parent.containsInstant((Instant)o)) ;
			}
			
			if (o instanceof Period) {
				Period p = parent.getPeriod(((Period) o).getStart());
				return (p != null  &&  p.equals(o)) ;
			}
			
			throw new ClassCastException();
		}

		public boolean remove(Period p) {
			return parent.remove(p);
		}

	} // PeriodSet


	private static abstract class EntrySet<V>  extends AbstractSet<TimedObject<V>> {

		private final TemporalAttribute<V> parent;

		public EntrySet(TemporalAttribute<V> parent) {
			this.parent = parent;
		}

		@Override
		public int size() {
			return parent.size();
		}

		@Override
		public abstract Iterator<TimedObject<V>> iterator();

		@Override
		public void clear() {
			parent.clear();
		}

		@Override
		public boolean contains(Object o) {
			if (o == null) {
				throw new IllegalArgumentException("null");
			}

			if (o instanceof Instant) {
				return (parent.containsInstant((Instant)o)) ;
			}

			if (o instanceof Period) {
				Period p = parent.getPeriod(((Period) o).getStart());
				return (p != null  &&  p.equals(o)) ;
			}

			if (o instanceof TimedObject) {
				TimedObject<?> to = (TimedObject<?>) o;
				TimedObject<V> match = parent.getEntry( 	
						to.getPeriod().getStart() 
				);
				if (match == null) {
					return false; 
				}
				return match.equals(to);
			}

			throw new ClassCastException();
		}

		@Override
		public boolean remove(Object o) {
			if (o instanceof Period) {
				return parent.remove((Period) o);
			}
			
			if (o instanceof TimedObject) {
				return parent.remove(((TimedObject<?>)o).getPeriod());
			}

			throw new ClassCastException();
		}

	} // EntrySet

}
