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
import java.util.TreeMap;
import java.util.Map;

import net.sf.jtemporal.Instant;
import net.sf.jtemporal.IsTime;
import net.sf.jtemporal.Period;
import net.sf.jtemporal.SimpleTimedObject;
import net.sf.jtemporal.TemporalAttribute;
import net.sf.jtemporal.TimedObject;
import net.sf.jtemporal.util.CloseableIterator;
import net.sf.jtemporal.util.SimpleCloseableIterator;

/**
 * A transient implementation of TemporalAttributeStorage#, based on a TreeMap. 
 * @author Thomas A Beck
 * @version $Id: TreeTemporalAttributeStorage.java,v 1.13 2008/12/23 21:07:46 tabeck Exp $
 */
public class TreeTemporalAttributeStorage<T>
  implements TemporalAttributeStorage<T>, Serializable
{
    private static final long serialVersionUID = 207260923072748398L;

    /**
	 * The storage.
	 * key = Period (sorted by start instant)
	 * value = object
	 */
    private final SortedMap<IsTime, T> sortedMap;
	
	/**
	 * The number of changes to the keys (Periods).
	 * Needed by views to know whether the model has changed.
	 */
	//private transient int versionNumber = 0;

	/**
	 * Creates an empty TemporalAttributeStorage.
	 */
	public TreeTemporalAttributeStorage() {
	  this.sortedMap    = new TreeMap<IsTime, T>(StartComparator.COMPARATOR);
	}	

	/**
     * You can specify an instance of SortedMap to be used internally 
     * by the storage.  This can be useful in the case your Object-Relational
     * mapping tool can provide a persistent implementation of the sorted Map. <br>
     * The SortedMap instance must comply with the following requirements: <br>
     * - use {@linkplain StartComparator#COMPARATOR} or a new 
     * instance of net.sf.jtemporal.spi.TreeTemporalAttributeStorage$StartComparator <br>
     * - have the key containing Periods and the value containg the value to
     * be returned by {@linkplain TemporalAttribute#get(Instant)}.  
	 * @param sortedMap
     * @throws IllegalArgumentException if the map does not use COMPARATOR
     * or if the map is null 
	 */
	public TreeTemporalAttributeStorage(SortedMap<IsTime, T> sortedMap) {
        if (sortedMap == null) {
            throw new IllegalArgumentException("The passed SortedMap is null");
        }
        if (!(sortedMap.comparator() instanceof StartComparator)) {
            throw new IllegalArgumentException(
                "The SortedMap instance must be initialised using "
               +"StartComparator.COMPARATOR");
        }
        this.sortedMap  = sortedMap;  
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
    public T getValue(Instant instant) {
		Period period = getPeriod(instant);  // find the enclosing period

		if (period == null) {
		  return null;   // no value found for this period
		}

        return sortedMap.get(period);
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
	   
	   SortedMap<IsTime, T> headMap = this.sortedMap.headMap(instant);  //headMap < key
	   if (!headMap.isEmpty()) {
		 Period prev = (Period) headMap.lastKey();
		 if (prev.contains(instant)) return prev;
	   }

	   SortedMap<IsTime, T> tailMap = this.sortedMap.tailMap(instant);  //tailMap >= key
	   if (!tailMap.isEmpty()) {
		 Period next = (Period) tailMap.firstKey();
		 if (next.contains(instant)) return next;
	   }

	   return null;  // no period found containing this instant
	 }
	 

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#getEntry(net.sf.jtemporal.Instant)
	 */
    public TimedObject<T> getEntry(Instant instant) {
		Period p = this.getPeriod(instant);
		if (p == null) {
			return null;
		}

        T v = getValue(instant);
        return new SimpleTimedObject<T>(p, v);
	}

	public TimedObject<T> getEntryEndingAt(Instant instant) {
		SortedMap<IsTime, T> headMap = this.sortedMap.headMap(instant);  //headMap < key
		if (!headMap.isEmpty()) {
		  Period prev = (Period) headMap.lastKey();
		  if (prev != null && prev.getEnd().equals(instant)) {
		  	// I got it
		  	T value = this.sortedMap.get(prev);
		  	return new SimpleTimedObject<T>(prev, value);
		  }
		}
		return null;  // no period found containing this instant
	}

	public TimedObject<T> getEntryStartingAt(Instant instant) {
		Period p = this.getPeriod(instant);
		if (p != null && p.getStart().equals(instant)) {
			// I got it
			T value = this.sortedMap.get(p);
			return new SimpleTimedObject<T>(p, value);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#beginAtomicOperation()
	 */
	public void beginAtomicOperation() {
		// this storage is not persistent
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#endAtomicOperation()
	 */
	public void endAtomicOperation() {
		// this storage is not persistent
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#clear()
	 */
	public void clear() {
	  this.sortedMap.clear();
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
		return this.sortedMap.containsValue(value);
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value, Period range) {
		return getSubMap(range).containsValue(value);
	}

	
	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#put(net.sf.jtemporal.Period, java.lang.Object)
	 */
	public void put(Period p, T value) {
		T previous = this.sortedMap.put(p, value);
		if (previous != null) {
		    // Invariant broken
		    throw new IllegalStateException(
		        "Period " + p + " already contained "+previous
		        +" instead of "+value
		    );
		}
	}
	
	/**
	 * Translates instances of Map.Entry into instances of TimedObject
	 */
    private static class EntryIterator<V>
      implements CloseableIterator<TimedObject<V>>
    {
        private final SimpleCloseableIterator<Map.Entry<IsTime, V>> iterator;

        EntryIterator(Iterator<Map.Entry<IsTime, V>> model) {
            iterator = new SimpleCloseableIterator<Map.Entry<IsTime, V>>(model);
		}

        public TimedObject<V> next() {
			// transform a Map.Entry into a TimedObject 
            Map.Entry<IsTime, V> entry = iterator.next();
            return new SimpleTimedObject<V>(
            	(Period) entry.getKey(), entry.getValue()
            );
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
        
        public void remove() {
            iterator.remove();
        }
	} // class EntryIterator

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#iterator()
	 */
    public CloseableIterator<TimedObject<T>> entryIterator() {
        return new EntryIterator<T>(sortedMap.entrySet().iterator());
	}
	
	/**
	 * Creates a subMap containing all the entries that intersects the given range.
	 * @param range
	 */
	private SortedMap<IsTime, T> getSubMap(Period range) {
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
	public CloseableIterator<TimedObject<T>> entryIterator(Period range) {
		SortedMap<IsTime, T> subMap = this.getSubMap(range);
		return new EntryIterator<T>(subMap.entrySet().iterator());
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#periodIterator()
	 */
	@SuppressWarnings("unchecked")
	public CloseableIterator<Period> periodIterator() {
		return new SimpleCloseableIterator(
			(sortedMap.keySet().iterator())
		);
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#periodIterator(net.sf.jtemporal.Period)
	 */
	@SuppressWarnings("unchecked")
	public CloseableIterator<Period> periodIterator(Period range) {
		SortedMap subMap = getSubMap(range);
		return new SimpleCloseableIterator(subMap.keySet().iterator());
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#remove(net.sf.jtemporal.Period)
	 */
	public void removeEntry(Period p) {
		T prevValue = this.sortedMap.remove(p);
		if (prevValue == null) {
			// this should never happen
			throw new NoSuchElementException("Period not found : "+p);
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#removeRange(net.sf.jtemporal.Period)
	 */
	public int removeRange(Period range) {
		
		Iterator<IsTime> it = getSubMap(range).keySet().iterator();
		int count = 0;
		while (it.hasNext()) {
			it.next();
			it.remove();
			count++;
		}
		return count;
	}

	/* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return this.sortedMap.toString();
    }
    
    /*private static class InstantAsPeriodAdapter extends Period {
		private static final long serialVersionUID = 987625961L;
    	private final Instant i;
		InstantAsPeriodAdapter(Instant start) {
			super();
			i=start;
		}
		@Override
		public Instant getStart() {
			return i;
		}
		@Override
		public Instant getEnd() {
			return i;
		}
    }*/
}
