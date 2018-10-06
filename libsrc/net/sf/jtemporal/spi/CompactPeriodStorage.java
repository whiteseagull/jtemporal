/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.spi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import net.sf.jtemporal.Instant;
import net.sf.jtemporal.IsTime;
import net.sf.jtemporal.Period;
import net.sf.jtemporal.util.CloseableIterator;
import net.sf.jtemporal.util.EmptyIterator;
import net.sf.jtemporal.util.IteratorFilter;
import net.sf.jtemporal.util.SimpleCloseableIterator;

/**
 * A sorted storage for Periods, intended to allocate a small 
 * amount of memory; fast for small collections, but does not scale well
 * for insertions in large collections.  <br>
 * The internal storage is based on a sorted ArrayList.  <br>
 * Given that for each {@linkplain CompactPeriodStorage#put(Period)}
 * or {@linkplain CompactPeriodStorage#remove(Period)} in average 
 * 50% of the records are moved, write performance of this
 * storage does not scale well. <br>
 * the arraylist is instantiated only the first time 
 * {@linkplain CompactPeriodStorage#put(Period)} is called. 
 * @TODO:complete unit tests 
 * @author Thomas A Beck
 * @version $Id: CompactPeriodStorage.java,v 1.10 2009/01/02 14:55:40 tabeck Exp $
 */
public class CompactPeriodStorage implements PeriodStorage {

    private List<Period> list = null;
	
	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.PeriodStorage#lastPeriod()
	 */
	public Period lastPeriod() {
		if (list == null) return null;
        return list.get(list.size()-1);
	}

	/**
	 * Returns the index of the search key, otherwise
	 * (-(insertion point) - 1)
	 */
	private int search(IsTime isTime) {
		return Collections.binarySearch(
			this.list, isTime, StartComparator.COMPARATOR
		);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.PeriodStorage#lastPeriod(net.sf.jtemporal.Period)
	 */
	public Period lastPeriod(Period p) {
		if (list == null) return null;
		int pos = this.search(p.getEnd()); // (-(insertion point) - 1)
		if (pos < 0)
			pos = -(pos+1)
		;
		// pos currently contains the matching or insertion point for p.end 
		pos--;
		if (pos < 0)
		    return null
		;
		Period lastPeriod = (Period) list.get(pos);
		if (lastPeriod.overlaps(p))
		    return lastPeriod
		;
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.PeriodStorage#firstPeriod()
	 */
	public Period firstPeriod() {
		if (list == null) return null;
		return list.get(0);
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.PeriodStorage#firstPeriod(net.sf.jtemporal.Period)
	 */
	public Period firstPeriod(Period p) {
		if (list == null) return null;
		int pos = this.search(p.getStart()); // (-(insertion point) - 1)
		if (pos < 0)
			pos = -(pos+1)
		;
		// pos currently contains the matching or insertion point for p.end 
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.PeriodStorage#isEmpty()
	 */
	public boolean isEmpty() {
		return this.list == null;
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.PeriodStorage#isEmpty(net.sf.jtemporal.Period)
	 */
	public boolean isEmpty(Period p) {
		if (list == null) return true;
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.PeriodStorage#getPeriod(net.sf.jtemporal.Instant)
	 */
	public Period getPeriod(Instant instant) {
		if (this.list == null) return null;
		int pos = this.search(instant);
		if (pos >= 0) { // found matching
			return this.list.get(pos);
		}
		pos = -(pos+1);
		if (pos == 0) return null;
		Period previous = this.list.get(pos-1);
		if (previous.contains(instant)) return previous;
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.PeriodStorage#put(net.sf.jtemporal.Period)
	 */
	public void put(Period p) {
		if (this.list == null) {
			this.list = new ArrayList<Period>(1);
			this.list.add(p);
		}
		else {
			int pos = this.search(p);
			if (pos >= 0) {// found matching start
				throw new IllegalArgumentException(
				    "The existing period "+list.get(pos)+
					" conflicts with the argument period "+p
				);
			}
			pos = -(pos+1);
			this.list.add(pos, p);
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.PeriodStorage#remove(net.sf.jtemporal.Period)
	 */
	public void remove(Period p) {
		int pos;
		if (this.list == null || (pos=this.search(p))<0 ) {
			throw new NoSuchElementException("Period "+p+" not found.");
		}
		this.list.remove(pos);
		
		if (this.list.isEmpty()) {
			this.list = null;
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.PeriodStorage#getPeriodEndingAt(net.sf.jtemporal.Instant)
	 */
	public Period getPeriodEndingAt(Instant instant) {
		if (this.list == null) return null;
		int pos = this.search(instant);
		if (pos < 0)
			pos = -(pos+1)
		;
		if (pos == 0) 
			return null
		;
		Period p = (Period) this.list.get(pos-1);
		if (p.getEnd().equals(instant))
			return p
		;
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.PeriodStorage#getPeriodStartingAt(net.sf.jtemporal.Instant)
	 */
	public Period getPeriodStartingAt(Instant instant) {
		if (this.list == null) return null;
		int pos = this.search(instant);
		if (pos >= 0) { // found matching
			return this.list.get(pos);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.PeriodStorage#clear()
	 */
	public void clear() {
		this.list = null;
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.PeriodStorage#size()
	 */
	public int size() {
		if (this.list == null) 
		    return 0
		;
		return this.list.size();
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.PeriodStorage#periodIterator(net.sf.jtemporal.Period)
	 */
	public CloseableIterator<Period> periodIterator(final Period period) {
		if (list == null) {
			return EmptyIterator.getInstance();
		}
		int pos = this.search(period);
		if (pos == this.list.size()) {
			return EmptyIterator.getInstance();		    
		}
		if (pos < 0) {
			pos = -(pos+1);
			if (pos > 0) {
				if ((list.get(pos-1)).overlaps(period)) pos--;
			}
		}

		return new IteratorFilter<Period,Period>(list.listIterator(pos))
		{
			@Override
			protected boolean shows(Period p) {

				if (p.overlaps(period)) {
					return true;
				}
				// if it does not overlap, we are out of range
				// we can stop looking further
				this.close();
				return false;
			}

			@Override
			public void remove() {
				super.remove();
				if (list.isEmpty()) {
					list = null;
				}
			}
		};
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.PeriodStorage#periodIterator()
	 */
	public CloseableIterator<Period> periodIterator() {
		if (this.list == null) {
			return EmptyIterator.getInstance();
		}
		return new SimpleCloseableIterator<Period>(this.list.iterator()) {
			@Override
			public void remove() {
				super.remove();
				if (list.isEmpty()) {
					list = null;
				}
			}
		};
	}
	
    /**
     * @see ArrayList#trimToSize()
     */
	public void trimToSize() {
	    if (this.list instanceof ArrayList) {
	        ((ArrayList<Period>)list).trimToSize();
	    }
	}

	/* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.list.toString();
    }
}
