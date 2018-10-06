/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.spi;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.sf.jtemporal.Instant;
import net.sf.jtemporal.Period;
import net.sf.jtemporal.TimedObject;
import net.sf.jtemporal.util.CloseableIterator;
import net.sf.jtemporal.util.SimpleCloseableIterator;

/**
 * This storage is based on a cache whose size grows lazily, 
 * until all the underlying data is in the cache.
 * This caching strategy is appropriate for applications
 * handling primarily recent data, which is the case of most applications<p>
 *
 * It loads lazily the data between the positive infinity and the requested
 * instant.  If subsequently you ask an instant that precedes the previous request,
 * it loads then the gap between both requested instants.  This continues until
 * the whole history is in cache. <br>
 * 
 * Unlike usual caching APIs, these methods intentionally return void, in
 * order to give you full freedom in implementing prefetching strategies,
 * where on a call you decide to load more data than requested, possibly
 * in other instances of LazyTemporalAttributeStorage. <br>
 *
 * Warning: the methods with no instant or period as a parameter
 * usually cause the entire underlying data to be loaded.  For example,
 * {@linkplain #isEmpty()} will load all the underlying data.
 * 
 * @author Thomas A Beck
 * @version $Id: LazyTemporalAttributeStorage.java,v 1.18 2008/12/28 21:24:12 tabeck Exp $
 * @param <V> the type of the value
 */
public class LazyTemporalAttributeStorage<V> implements TemporalAttributeStorage<V>
{
    /**
     * Loads the data from the real physical storage into the the 
     * LazyTemporalAttributeStorage cache, when additional data is requested.
     * @author Thomas A Beck
     * @version $Id: LazyTemporalAttributeStorage.java,v 1.18 2008/12/28 21:24:12 tabeck Exp $
     * @param <V> the value held in the related {@linkplain LazyTemporalAttributeStorage}
     */
    public interface MissingRangeHandler<V> {
        /**
         * When the LazyTemporalAttributeStorage calls you back using this method
         * you have to: <br>
         * 
         * 1- Populate at least this storage with at least all the entries existing 
         * intersecting the given period (=range), by calling the method 
         * {@linkplain LazyTemporalAttributeStorage#putInCache(Period, Object)}  <br>
         * It does not matter if you reload already loaded records, provided that they
         * are identical. <br>
         * It is recommended, but not required, to load a little bit further in the
         * pas than range.getStart(). This will improve your performance by reducing
         * subsequent loads, and will completely avoid calls to 
         * {@linkplain #populateEntryEndingAt(Instant)}. <br>
         *
         * 2- Call the method {@linkplain LazyTemporalAttributeStorage#populated(Period)} 
         * to tell which range you have actually populated (maybe you have populated more 
         * than requested to improve future caching. <br> 
         * This is needed, because LazyTemporalAttributeStorage must differentiate 
         * holes in cache from holes in the underlying storage.
         * 
         * @param target the LazyTemporalAttributeStorage requesting to pe populated.
         * @param period the range that must be populated.  If there are holes, the 
         * LazyTemporalAttributeStorage assumes there are no mapping in the underlying 
         * storage as well: the holes are part of the information that is cached.
         */
        void populateRange(LazyTemporalAttributeStorage<V> target, Period range);
        
        /**
         * When the LazyTemporalAttributeStorage calls you back using this method
         * you have to: <br>
         *
         * 1- load the entry when end instant is equal to instant, if any exists.
         * You are free to load more data if you wish.  To load the entry you have to
         * call the method 
         * {@linkplain LazyTemporalAttributeStorage#putInCache(Period, Object)} <br>
         * 
         * 2- if you have loaded more data than the one just requested, you have to call
         * {@linkplain LazyTemporalAttributeStorage#populated(Period)} to tell up
         * to which instant you have loaded.
         * <br><br>
         * Note : this method is called only when the 
         * LazyTemporalAttributeStorage is used in write mode.  It is never
         * called if you use it in read only mode.
         * 
         * @param target the LazyTemporalAttributeStorage requesting to pe populated.
         * @param instant the instant that must match to the end instant of the loaded entry
         */
        void populateEntryEndingAt(LazyTemporalAttributeStorage<V> target, Instant instant);
        
        /**
         * JTemporal does not know what your implementation of Instant is.
         * In order to be able to construct open-ended periods, it needs to get
         * your infinity instant values. <br>
         * @return a constant (you could call it ALWAYS) containing 
         * a Period(NEGATIVE_INFINITY,POSITIVE_INFINITY)
         */
        Period getAlwaysPeriod(); // Not passed through the constructor, to save some memory.        
    }

	/**
	 * Applies the changes on the underlying data source. <br>
	 * There is only a single ChangeHandler for each LazyTemporalAttribute. <br>
	 * 
	 * In a performance point of view, it's usually a good idea here to buffer the
	 * requests, in order to reduce the underlying calls granularity.
	 * 
	 * @author Thomas A Beck
	 * @version $Id: LazyTemporalAttributeStorage.java,v 1.18 2008/12/28 21:24:12 tabeck Exp $
     * @param <V> the value held in the related {@linkplain LazyTemporalAttributeStorage}
	 */
	public interface ChangeHandler<V> {
		/**
		 * Invoked when {@linkplain LazyTemporalAttributeStorage#put(Instant,Object)}
		 *  has been called. <br>
		 * Throwing any exception will cancel the operation.
		 * @param source the {@linkplain LazyTemporalAttributeStorage} where the put 
		 * method has been invoked
		 * @param i the Instant
		 * @param value the new value
		 * @see LazyTemporalAttributeStorage#put(Instant,Object)
		 * @throws UnsupportedOperationException if the LazyTemporalAttributeStorage is read-only
		 */
		void put(LazyTemporalAttributeStorage<V> source, Period p, Object value);
		
		/**
		 * Invoked when {@linkplain LazyTemporalAttributeStorage#remove(Instant)}
		 *  has been called. <br>
		 * Throwing any exception will cancel the operation.
		 * @param source the LazyTemporalAttributeStorage where the remove method has been invoked
		 * @param i the Instant specifying the entry to be removed
		 * @see LazyTemporalAttributeStorage#remove(Instant)
		 * @throws UnsupportedOperationException if the LazyTemporalAttributeStorage is read-only
		 */
		void remove(LazyTemporalAttributeStorage<V> source, Period p);

		/**
		 * Invoked when TimeSeries.remove(Instant) has been called. <br>
		 * Throwing any exception will cancel the operation.
		 * @param source the LazyTemporalAttributeStorage where the clear method has been invoked
		 * @param i the Instant specifying the entry to be removed
		 * @see LazyTemporalAttributeStorage#clear()
		 * @throws UnsupportedOperationException if the LazyTemporalAttributeStorage is read-only
		 */
		void clear(LazyTemporalAttributeStorage<V> source);

	} // ChangeHandler

    
    // counts the number of times the method populated(Period) has been called.
    // this is used to check whether the client respect the contract
    // of the MissingPeriodHandler
    //private int loadCount = Integer.MIN_VALUE;
    
	// when this is null, this means the Storage is physically empty
    // otherwise, it means the cache contains all the entries whose start instant
    // is > = cachingLimit
    private Instant cachingLimit = null;
    // tells whether we are caching also the entry whose endInstant==cachingLimit
    private boolean includesEndsWithLimit = false;
    
    private TemporalAttributeStorage<V> cache = new TreeTemporalAttributeStorage<V>();
    
    private final MissingRangeHandler<V> loader;

	private ChangeHandler<V> writer;
    
    /**
     * Instantiates a read only LazyTemporalAttributeStorage.
     * @param loader
     */
    public LazyTemporalAttributeStorage(MissingRangeHandler<V> loader) {
        this.loader = loader;
        //this.clearCache();
    }
    
    /**
     * Instantiates a read-write LazyTemporalAttributeStorage.
     * @param loader
     */
    public LazyTemporalAttributeStorage(MissingRangeHandler<V> loader, ChangeHandler<V> writer) {
        this.loader = loader;
        this.writer = writer;
        //this.clearCache();
    }
    
    public void clearCache() {
        cachingLimit = null;
        includesEndsWithLimit = false;
        cache.clear(); 
    }
    
    /**
     * Called by the implementation of
     * {@linkplain MissingRangeHandler#populateRange(LazyTemporalAttributeStorage, Period)}
     * to add entries in the cache. <br>
     * The semantics is the same as {@linkplain #put(Period, Object)}, except that 
     * the entries are loaded in the cache only.
     * @param p period with which the specified value is associated.  Cannot be null.
     * @param o value to be associated with the specified key.  Cannot be null. 
     * @see #put(Period, Object)
     */
    public void putInCache(Period p, V o) {
        cache.put(p, o);
        
        if ( this.cachingLimit == null ||
                // optimization: this block is not necessarily required
             (     p.getStart().compareTo(this.cachingLimit) < 0
                   // only if we are sure we are not missing a gap
                && p.getEnd().compareTo(this.cachingLimit) >= 0
             )
        ) {
            this.cachingLimit = p.getStart();
            this.includesEndsWithLimit = false;
        }
    }
    
    /**
     * Called by the implementation of
     * {@linkplain MissingRangeHandler#populateRange(LazyTemporalAttributeStorage, Period)}
     * to to tell which Period (or additional Period) can now considered as complete 
     * in the cache. <br>
     */
    public void populated(Period p) {
        if (cachingLimit == null) {
            // this was the first load
            if (!p.getEnd().isPositiveInfinity()) {
                throw new IllegalStateException(
                    "The populated period must end by positive infinity"
                );
            }
            this.cachingLimit = p.getStart();
            this.includesEndsWithLimit = false;
        }
        else {
            // this was not the first load
            if (p.getEnd().compareTo(cachingLimit) < 0) {
                throw new IllegalStateException(
                    "There is a gap between "+p.getEnd()+" and "+cachingLimit
                );
            }
            /*if (p.getStart().compareTo(cachingLimit) >= 0) {
                throw new IllegalStateException(
                    "The period "+p+" is useless: the cache was already populated from "
                    +cachingLimit
                );                    
            }*/
            if (p.getStart().compareTo(cachingLimit) < 0) {
                cachingLimit = p.getStart();
                this.includesEndsWithLimit = false;
            }
        }
    }
 
    private void loadAll() {
        loadToInstant(loader.getAlwaysPeriod().getStart());
    }
    
    private void loadToInstant(Instant i) {
        if (cachingLimit == null) {
            loader.populateRange(
                this, 
                new Period(i, loader.getAlwaysPeriod().getEnd()) 
            );            
        }
        else {
            loader.populateRange(
                this, 
                new Period(i, cachingLimit) 
            );            
        }
        
        // postconditions
        if (cachingLimit == null) {
            throw new IllegalStateException("The cache is not populated");
        }
        if (cachingLimit.compareTo(i) > 0) {
            throw new IllegalStateException(
                "The cache is not populated since "+cachingLimit
                +" but the instant "+i+" is requested"
            );            
        }
    }
    
    private void ensureCachingSince(Instant i) {
        if (cachingLimit == null  ||  i.compareTo(cachingLimit) < 0) {
            this.loadToInstant(i);
        }
    }
    
    private void ensureFullCaching() {
        if (cachingLimit == null  ||  !cachingLimit.isNegativeInfinity()) {
            this.loadAll();
        }
    }
    
    /// TemporalAttributeStorage //////////////////////////////////////
    
	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#lastPeriod()
	 */
	public Period lastPeriod() {
        if (cachingLimit == null) {
            loadAll();
        }
        // try quick-win
        if (!cache.isEmpty()) {
            return cache.lastPeriod();
        }
        
        if (cachingLimit.isNegativeInfinity()) {
            // we know the cache is empty
            throw new NoSuchElementException();
        }
        // our cache is potentially insufficient
        loadAll();
        return cache.lastPeriod();        
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#lastPeriod(net.sf.jtemporal.Period)
	 */
	public Period lastPeriod(Period p) {
        if (cachingLimit == null) {
            loadToInstant(p.getStart());
        }

        if (p.getStart().compareTo(cachingLimit) < 0) {
            // TODO : to be tuned later, maybe we do not need to load something
            loadToInstant(p.getStart());
            
            if (p.getStart().compareTo(cachingLimit) < 0) {
                throw new IllegalStateException();
            }
        }
        
		return cache.lastPeriod(p);
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#firstPeriod()
	 */
	public Period firstPeriod() {
	    this.ensureFullCaching();
	    return cache.firstPeriod();
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#firstPeriod(net.sf.jtemporal.Period)
	 */
	public Period firstPeriod(Period p) {
	    this.ensureCachingSince(p.getStart());
	    return cache.firstPeriod(p);
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#isEmpty()
	 */
	public boolean isEmpty() {
	    this.ensureFullCaching();
	    return cache.isEmpty();
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#isEmpty(net.sf.jtemporal.Period)
	 */
	public boolean isEmpty(Period p) {
	    this.ensureFullCaching();
	    return cache.isEmpty(p);
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
	    this.ensureFullCaching();
	    return cache.containsValue(value);
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#containsValue(java.lang.Object, net.sf.jtemporal.Period)
	 */
	public boolean containsValue(Object value, Period range) {
	    this.ensureCachingSince(range.getStart());
	    return cache.containsValue(value, range);
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#getValue(net.sf.jtemporal.Instant)
	 */
	public V getValue(Instant instant) {
	    this.ensureCachingSince(instant);
	    return cache.getValue(instant);
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#getPeriod(net.sf.jtemporal.Instant)
	 */
	public Period getPeriod(Instant instant) {
	    this.ensureCachingSince(instant);
	    return cache.getPeriod(instant);
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#getEntry(net.sf.jtemporal.Instant)
	 */
	public TimedObject<V> getEntry(Instant instant) {
	    this.ensureCachingSince(instant);
	    return cache.getEntry(instant);
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#getEntryEndingAt(net.sf.jtemporal.Instant)
	 */
	public TimedObject<V> getEntryEndingAt(Instant instant) {
	    
	    this.ensureCachingSince(instant);
	    
	    if (this.cachingLimit.compareTo(instant) < 0
	       || this.includesEndsWithLimit
	    ) {
	        // fine, the query is already covered by the cache
	        return cache.getEntryEndingAt(instant);
	    }

	    // now we know that cachingLimit == 0 && includesEndsWithLimit == false 
	    if (! cachingLimit.equals(instant)) {
	        throw new IllegalStateException("! cachingLimit.equals(instant)");
	    }

	    if (includesEndsWithLimit) {
	        throw new IllegalStateException("includesEndsWithLimit");
	    }
	    
	    // we have to load the little part missed by ensureCachingSince(instant)
	    loader.populateEntryEndingAt(this, instant);
	    
	    if (cachingLimit.equals(instant)) {
	        // nothing has been loaded, but we have to cache this knowledge
	        this.includesEndsWithLimit = true;
	    }
	    
        // fine, the query is now covered by the cache
        return cache.getEntryEndingAt(instant);
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#getEntryStartingAt(net.sf.jtemporal.Instant)
	 */
	public TimedObject<V> getEntryStartingAt(Instant instant) {
	    this.ensureCachingSince(instant);
	    return cache.getEntryStartingAt(instant);
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#beginAtomicOperation()
	 */
	public void beginAtomicOperation() {
	    cache.beginAtomicOperation();
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#endAtomicOperation()
	 */
	public void endAtomicOperation() {
	    cache.endAtomicOperation();
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#clear()
	 */
	public void clear() {
		if (this.writer == null ) 
			throw new UnsupportedOperationException("Not implemented yet.")
	    ;
		this.writer.clear(this);
		this.cache.clear();
		this.cachingLimit = this.loader.getAlwaysPeriod().getStart();
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#size()
	 */
	public int size() {
	    this.ensureFullCaching();
	    return cache.size();
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#size(net.sf.jtemporal.Period)
	 */
	public int size(Period p) {
	    this.ensureCachingSince(p.getStart());
	    return cache.size(p);
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#put(net.sf.jtemporal.Period, java.lang.Object)
	 */
	public void put(Period p, V value) {
		if (p == null || value == null) {
			throw new IllegalArgumentException("null");
		}
		this.ensureCachingSince(p.getStart());
		this.writer.put(this, p, value); // RuntimeException possible here
		this.cache.put(p, value);
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#removeEntry(net.sf.jtemporal.Period)
	 */
	public void removeEntry(Period p) {
		if (p == null) {
			throw new IllegalArgumentException("null");
		}
		this.ensureCachingSince(p.getStart());
		this.writer.remove(this, p);
		this.cache.removeEntry(p);
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#removeRange(net.sf.jtemporal.Period)
	 */
	public int removeRange(Period range) {
		if (range == null) {
			throw new IllegalArgumentException("null");
		}
	    
	    this.ensureCachingSince(range.getStart());
	    
		Iterator<Period> it = cache.periodIterator(range);
		int count = 0;
		while (it.hasNext()) {
		    Period p = it.next();
			it.remove();
			writer.remove(this, p);
			count++;
		}
		return count;
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#entryIterator()
	 */
	public CloseableIterator<TimedObject<V>> entryIterator() {
	    this.ensureFullCaching();
	    
	    return new SimpleCloseableIterator<TimedObject<V>>(cache.entryIterator()) {

            @Override
            public void remove() {
                writer.remove(
                    LazyTemporalAttributeStorage.this, 
                    getLastFetched().getPeriod()
                );
                super.remove();
            }
	    };
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#entryIterator(net.sf.jtemporal.Period)
	 */
	public CloseableIterator<TimedObject<V>> entryIterator(Period p) {
		if (p == null) {
			throw new IllegalArgumentException("null");
		}
	    this.ensureCachingSince(p.getStart());
	    
	    return new SimpleCloseableIterator<TimedObject<V>>(cache.entryIterator(p)) {

            @Override
            public void remove() {
                writer.remove(
                    LazyTemporalAttributeStorage.this, 
                    getLastFetched().getPeriod()
                );
                super.remove();
            }
	    };
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#periodIterator()
	 */
	public CloseableIterator<Period> periodIterator() {
	    this.ensureFullCaching();
	    
	    return new SimpleCloseableIterator<Period>(cache.periodIterator()) {

            @Override
            public void remove() {
                writer.remove(
                    LazyTemporalAttributeStorage.this, 
                    ((Period)getLastFetched())
                );
                super.remove();
            }
	    };
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.spi.TemporalAttributeStorage#periodIterator(net.sf.jtemporal.Period)
	 */
	public CloseableIterator<Period> periodIterator(Period p) {
		if (p == null) {
			throw new IllegalArgumentException("null");
		}
	    this.ensureCachingSince(p.getStart());

	    return new SimpleCloseableIterator<Period>(cache.periodIterator(p)) {

            @Override
            public void remove() {
                writer.remove(
                    LazyTemporalAttributeStorage.this, 
                    getLastFetched()
                );
                super.remove();
            }
	    };
	}

	Instant getCachingLimit() {
		return cachingLimit;
	}

	/* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{LazyTepomralAttributeStorage: caching limit:"
        	+ cachingLimit+", cache content:"+cache.toString()+"}"
        ;
    }
}
