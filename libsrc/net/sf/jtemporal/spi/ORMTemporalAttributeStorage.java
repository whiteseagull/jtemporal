/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.spi;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.Map;

import net.sf.jtemporal.Instant;
import net.sf.jtemporal.IsTime;
import net.sf.jtemporal.Period;
import net.sf.jtemporal.TemporalAttribute;
import net.sf.jtemporal.TimedObject;
import net.sf.jtemporal.util.CloseableIterator;
import net.sf.jtemporal.util.SimpleCloseableIterator;

/**
 * An implementation of TemporalAttributeStorage#, 
 * based on a TreeMap, mapped by an ORM tool. 
 * @author Thomas A Beck
 * @version $Id: ORMTemporalAttributeStorage.java,v 1.11 2008/12/23 21:07:46 tabeck Exp $
 */
public class ORMTemporalAttributeStorage<V> 
  implements TemporalAttributeStorage<V>, Serializable
{
    private static final long serialVersionUID = 207264223072748398L;

	/*
	 * The storage.
	 * key = Period (sorted by start instant)
	 * value = TimedObject (a mapped row in the database)
	 */
    private final SortedMap<IsTime, TimedObject<V>> sortedMap;

    private TimedObjectFactory<V> toFactory;

	/**
     * You specify an instance of SortedMap to be used internally 
     * by the storage.  This can be useful in the case your Object-Relational
     * mapping tool can provide a persistent implementation of the sorted Map. <br>
     * The SortedMap instance must comply with the following requirements: <br>
     * - use {@linkplain StartComparator#COMPARATOR} or a new 
     * instance of net.sf.jtemporal.spi.TreeTemporalAttributeStorage$StartComparator <br>
     * - have the key containing Periods and the value containing the value to
     * be returned by {@linkplain TemporalAttribute#get(Instant)}.  
	 * @param sortedMap a map mapped by an ORM tools, whose key is composed 
	 * of {@linkplain Period} and the value of {@linkplain TimedObject}, using as a comparator
	 * {@linkplain StartComparator#COMPARATOR}. <b>Note that the {@linkplain #getValue(Instant)}
	 * will return just the value held in the TimedObject and not the timedObject itself.</b>
	 * This, because the TimedObject is the closed representation of a row in 
	 * your database, that is mapped by your ORM tool.<br>
	 * <b>Note that the TimedObjects and their components must immutable.</b> They will be created
	 * and deleted from the database, but never updated.
	 * @param toFactory a callback for creating and destroying instances of {@linkplain TimedObject}
     * @throws IllegalArgumentException if the map does not use {@linkplain StartComparator#COMPARATOR}.
     * or if the map is null 
	 */
	public ORMTemporalAttributeStorage(
	    SortedMap<IsTime, TimedObject<V>> sortedMap, TimedObjectFactory<V> toFactory) 
	{
        if (sortedMap == null) {
            throw new IllegalArgumentException("The passed SortedMap is null");
        }

        if (toFactory == null) {
            throw new IllegalArgumentException("The passed TimedObjectFactory is null");
        }
        
        if (!(sortedMap.comparator() instanceof StartComparator)) {
            throw new IllegalArgumentException(
                "The SortedMap instance must be initialised using "
               +"StartComparator.COMPARATOR");
        }
        this.sortedMap  = sortedMap;
        this.toFactory = toFactory;
    }

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#lastPeriod()
	 */
	public Period lastPeriod() {
		return (Period) this.sortedMap.lastKey();
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#lastPeriod(net.sf.jtemporal.Period)
	 */
	public Period lastPeriod(Period range) {
		return (Period) getSubMap(range).lastKey();
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#firstPeriod()
	 */
	public Period firstPeriod() {
		return (Period) this.sortedMap.firstKey();
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#firstPeriod(net.sf.jtemporal.Period)
	 */
	public Period firstPeriod(Period range) {
		return (Period) getSubMap(range).firstKey();
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#isEmpty(net.sf.jtemporal.Period)
	 */
	public boolean isEmpty() {
	  return this.sortedMap.isEmpty();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#getValue(net.sf.jtemporal.Instant)
	 */
	public V getValue(Instant instant) {
		Period period = getPeriod(instant);  // find the enclosing period

		if (period == null) {
		  return null;   // no value found for this period
		}

		TimedObject<V> to = sortedMap.get(period);
        return to.getValue();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#getPeriod(net.sf.jtemporal.Instant)
	 */
	 public Period getPeriod(Instant instant) {

	   // speculative : first try quick wins  
	   {
		   if (this.sortedMap.isEmpty()) {
		       return null;
		   }
		   
	       // actually this period is returned the most often
		   Period lastPeriod =  (Period) this.sortedMap.lastKey();		   
		   if (lastPeriod.contains(instant)) {
		       return lastPeriod;
		   }
	   }

	   // quick wins did not work, let's do the normal job
	   
        SortedMap<IsTime, TimedObject<V>> headMap = this.sortedMap.headMap(instant);  //headMap < key

	   if (!headMap.isEmpty()) {
		 Period prev = (Period) headMap.lastKey();
		 if (prev.contains(instant)) return prev;
	   }

	   SortedMap<IsTime, TimedObject<V>> tailMap = this.sortedMap.tailMap(instant);  //tailMap >= key
	   if (!tailMap.isEmpty()) {
		 Period next = (Period) tailMap.firstKey();
		 if (next.contains(instant)) return next;
	   }

	   return null;  // no period found containing this instant
	 }
	 

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#getEntry(net.sf.jtemporal.Instant)
	 */
    public TimedObject<V> getEntry(Instant instant) {
        Period p = getPeriod(instant);

		if (p == null) {
			return null;
		}

        return sortedMap.get(p);
	}

	public TimedObject<V> getEntryEndingAt(Instant instant) {
		SortedMap<IsTime, TimedObject<V>> headMap = this.sortedMap.headMap(instant);  //headMap < key
		if (!headMap.isEmpty()) {
		  Period prev = (Period) headMap.lastKey();
		  if (prev != null && prev.getEnd().equals(instant)) {
		  	// I got it
	        return this.sortedMap.get(prev);
		  }
		}
		return null;  // no period found containing this instant
	}

    public TimedObject<V> getEntryStartingAt(Instant instant) {
        Period p = getPeriod(instant);

		if (p != null && p.getStart().equals(instant)) {
			// I got it
            return sortedMap.get(p);
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#beginAtomicOperation()
	 */
	public void beginAtomicOperation() {
		// ORM tool starts a transaction anyway
		return;
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#endAtomicOperation()
	 */
	public void endAtomicOperation() {
		// ORM tool starts a transaction anyway
		return;
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#clear()
	 */
	public void clear() {
		Iterator<TimedObject<V>> it = this.entryIterator();
		while (it.hasNext()) {
			it.next();
			it.remove();
		}
	}

	public int size() {
		return this.sortedMap.size();
	}

	public int size(Period range) {
		return getSubMap(range).size();
	}


	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#isEmpty(net.sf.jtemporal.Period)
	 */
	public boolean isEmpty(Period range) {
		return getSubMap(range).isEmpty();
	}



	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value, Period range) {
		//return getSubMap(range).containsValue(value);
        throw new UnsupportedOperationException();
	}

	
	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#put(net.sf.jtemporal.Period, java.lang.Object)
	 */
	public void put(Period p, V value) {
	    TimedObject<V> to = this.toFactory.create(p, value);
		TimedObject<V> previous = this.sortedMap.put(to.getPeriod(), to);
		if (previous != null) {
		    // Invariant broken
		    throw new IllegalStateException(
		        "Period "+p+" already contained "+previous
		        +" instead of "+value
		    );
		}

	}
	
	/**
	 * Translates instances of Map.Entry into instances of TimedObject
	 */
    private class EntryIterator implements CloseableIterator<TimedObject<V>> {

        private final SimpleCloseableIterator<Map.Entry<IsTime, TimedObject<V>>> iterator;

        EntryIterator(Iterator<Map.Entry<IsTime, TimedObject<V>>> model) {
            iterator = new SimpleCloseableIterator<Map.Entry<IsTime, TimedObject<V>>>(model);
		}
		
        public void remove() {
            TimedObject<V> lastFetched = iterator.getLastFetched().getValue();
            ORMTemporalAttributeStorage.this.toFactory.destroy(lastFetched);
            iterator.remove();
        }

        public TimedObject<V> next() {
            return iterator.next().getValue();
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }
        
        public void close() {
            iterator.close();
        }

        public boolean isOpen() {
            return iterator.isOpen();
        }
    }

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#iterator()
	 */
    public CloseableIterator<TimedObject<V>> entryIterator() {
		return new EntryIterator(sortedMap.entrySet().iterator());
	}
	
	/**
	 * Creates a subMap containing all the entries that intersects the given range.
	 * @param range
	 */
	private SortedMap<IsTime, TimedObject<V>> getSubMap(Period range) {
		if (range == null) {
			throw new IllegalArgumentException("range == null");
		}
		
		// firstPeriod <= range.start
		Period firstPeriod = this.getPeriod(range.getStart()); 
		
		// define submap start limit
		Instant firstLimit = firstPeriod == null ? range.getStart() : firstPeriod.getStart();
		
		// SortedMap.subMap returns fromKey inclusive toKey exclusive
		return sortedMap.subMap(firstLimit, range.getEnd());		
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#iterator(net.sf.jtemporal.Period)
	 */
	public CloseableIterator<TimedObject<V>> entryIterator(Period range) {
		SortedMap<IsTime, TimedObject<V>> subMap = this.getSubMap(range);
		return new EntryIterator(subMap.entrySet().iterator());
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#periodIterator()
	 */
	@SuppressWarnings("unchecked")
	public CloseableIterator<Period> periodIterator() {
		return new SimpleCloseableIterator(sortedMap.keySet().iterator()) {
            @Override
            public void remove() {
                throw new UnsupportedOperationException(
                    "remove() can be called through the entryIterator only"
                );
            }
		};
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#periodIterator(net.sf.jtemporal.Period)
	 */
	@SuppressWarnings("unchecked")
	public CloseableIterator<Period> periodIterator(Period range) {
		SortedMap<IsTime, TimedObject<V>> subMap = getSubMap(range);
		return new SimpleCloseableIterator(subMap.keySet().iterator()) {
            public void remove() {
                throw new UnsupportedOperationException(
                    "remove() can be called through the entryIterator only"
                );
            }
		};
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#remove(net.sf.jtemporal.Period)
	 */
	public void removeEntry(Period p) {
	    TimedObject<V> entry = this.sortedMap.remove(p);
		if (entry == null) {
			// this should never happen
			throw new NoSuchElementException("Period not found : "+p);
		}
	    this.toFactory.destroy(entry);
	    this.sortedMap.remove(entry.getPeriod());
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#removeRange(net.sf.jtemporal.Period)
	 */
	public int removeRange(Period range) {
		
        Iterator<Map.Entry<IsTime, TimedObject<V>>> it = getSubMap(range).entrySet().iterator();
		int count = 0;
		while (it.hasNext()) {
            TimedObject<V> entry = it.next().getValue();
			it.remove();
			this.toFactory.destroy(entry);
			count++;
		}
		return count;
	}

	/* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.sortedMap.toString();
    }
}
