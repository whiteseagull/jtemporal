/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;

import net.sf.jtemporal.util.LightLRUCache;

/**
 * Caches an external TimeSeries (for ex. to hand code a database access).
 * Each time this collection is queried, if the value for the given instant is unknown,
 * the MissingInstantHandler is invoked.  Then, the handler has the choice whether 
 * to retrieve just the missing value, or other values, or other objects as well 
 * to reduce call granularity (prefetch). <br>
 * Internally a LightLRUCache is used to cache reads. Writes are not cached, if you
 * want to cache them, you should do it at the ChangeHandler level.<br>
 * If you already have a persistence framework, you will eventually use this class on 
 * top of that, in order to control and customize prefetching.<br>
 * This class is NOT thread-safe.
 * 
 * @author Thomas A Beck
 * @see net.sf.jtemporal.util.LightLRUCache
 * @version $Id: CachedTimeSeries.java,v 1.15 2008/12/14 19:33:42 tabeck Exp $
 * @param <V> the type of the value
 */
public class CachedTimeSeries<V> implements TimeSeries<V> {

	//holds instances of V and NoElementObject.VALUE
	private final LightLRUCache<Instant,Object> cache;
	
	private final MissingElementHandler<V> reader;
	
	private final ChangeHandler<V> writer;
	
	/**
	 * Creates a READ-ONLY CachedTimeSeries.
	 * @param size the size of the cache (number of entries)
	 * @param reader handles physical read requests
	 */
	public CachedTimeSeries(
		int size, 
		MissingElementHandler<? super V> reader
	) {
		this(size, reader, NoChangeHandler.INSTANCE);		
	}
	
	/**
	 * Creates a normal instance of CachedTimeSeries
	 * @param size size the size of the cache (number of entries)
	 * @param reader handles physical read requests
	 * @param writer handles write requests
	 */
	@SuppressWarnings("unchecked") // it is fine to pass handlers that handle generic Object
	public CachedTimeSeries(
		int size, 
		MissingElementHandler<? super V> reader, 
		ChangeHandler        <? super V> writer
	) {
		this.cache = new LightLRUCache<Instant,Object>(size);
		this.reader = (MissingElementHandler<V>) reader;	// unchecked cast
		this.writer = (ChangeHandler<V>) writer;			// unchecked cast
	}

	/*
	 *  (non-Javadoc)
	 * @see net.sf.jtemporal.TimeSeries#put(net.sf.jtemporal.Instant, java.lang.Object)
	 */
	public V put(Instant i, V value) {
		if (i == null || value == null) {
			throw new IllegalArgumentException("null");
		}
		V previous = this.get(i);
		this.writer.put(this, i, value); // RuntimeException possible here
		this.cache.put(i, value);
		return previous;
	}
	
	/**
	 * Writes in the cache only, without triggering the 
	 * {@linkplain ChangeHandler}. <br>
	 * Usually this method is only called by the 
	 * {@linkplain MissingElementHandler} implementation. <br>
+	 * If you want to cache the information that no value is available for the 
	 * given instant, then you must put a value=null. 
	 * @param i the key of the entry
	 * @param value the value to be associated to the given instant,
	 * or <code>null</code>, if no value exists at the given instant.
	 */
	public void putInCache(Instant i, V value) {
		if (i == null) {
			throw new IllegalArgumentException("null");
		}
		if (value == null) {
			this.cache.put(i, NoElementObject.VALUE );
		}
		else
			this.cache.put(i, value)
		;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see net.sf.jtemporal.TimeSeries#get(net.sf.jtemporal.Instant)
	 */
	@SuppressWarnings("unchecked")
	public V get(Instant i) {
		if (i == null) {
			throw new IllegalArgumentException("null");
		}
		Object value = this.cache.get(i);
		
		if (value == null) { 
			this.reader.populateElement(this, i); // RuntimeException possible here
			value = this.cache.get(i);
			if (value == null) { //still?
				this.cache.put(i, NoElementObject.VALUE); // remember for the next time
			}
		}
		if (value == NoElementObject.VALUE) {
			// the cache knows, anything is associated to this instant
			return null;
		}
		return (V) value;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TimeSeries#remove(net.sf.jtemporal.Instant)
	 */
	public V remove(Instant i) {
		if (i == null) {
            throw new IllegalArgumentException("null instant");
		}
		V previous = this.get(i);
		this.writer.remove(this, i); // RuntimeException possible here
		this.cache.put(i, NoElementObject.VALUE);
		return previous;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TimeSeries#remove(net.sf.jtemporal.Period)
	 */
	public void remove(Period p) {
		if (p == null) {
            throw new IllegalArgumentException("null period");
		}
		this.writer.remove(this, p);
		this.cache.clear(); // good enough for the time being // TODO
	}
	
	/**
	 * Clears the underlying cache.  
	 * Useful when you know that the underlying data has been changed by somebody else,
	 * and your cached imformation is not valid anymore.
	 */
	public void clearCache() {
		this.cache.clear();
	}
	
	
	/**
	 * Reacts on missing elements. <br>
	 * Populates the CachedTimeSeries when data is requested. <br>
	 * There is only a single MissingElementHandler for each CachedTimeSeries.
	 * @author Thomas A Beck
	 * @version $Id: CachedTimeSeries.java,v 1.15 2008/12/14 19:33:42 tabeck Exp $
	 */
	public interface MissingElementHandler<V> {
		/**
		 * This method is called when get(Instant) is called on the timeseries
		 * and no value is found in the cache. <BR>
		 * At his point, it's up to the implementor to populate the timeseries 
		 * with one or more entries, by calling
		 * {@linkplain CachedTimeSeries#putInCache(Instant, Object)}. <br>
		 * In a performance point of view, it's usually a good idea here to populate
		 * a little bit more than just the requested value, in order to reduce the 
		 * underlying calls granularity. <br>
		 * <b>Note:</b> If there exist no value valid at the requested instant, 
		 * then you should store a <code>null</code> value.  
		 * This will cache the information that there is no entry for this instant.
		 * @param ts the CachedTimeSeries instance originating the call
		 * @param i the key whose no value is associated
		 * @see net.sf.jtemporal.TimeSeries#get(Instant)
		 * @see net.sf.jtemporal.CachedTimeSeries#putInCache(Instant, Object)
		 */
		void populateElement(CachedTimeSeries<V> ts, Instant i);		

	} // MissingElementHandler

	
	/**
	 * Applies the changes on the underlying data source. <br>
	 * There is only a single ChangeHandler for each CachedTimeSeries. <br>
	 * 
	 * In a performance point of view, it's usually a good idea here to buffer the
	 * requests, in order to reduce the underlying calls granularity.
	 * 
	 * @author Thomas A Beck
	 * @version $Id: CachedTimeSeries.java,v 1.15 2008/12/14 19:33:42 tabeck Exp $
	 */
	public interface ChangeHandler<V> {
		/**
		 * Invoked when {@linkplain TimeSeries#put(Instant, Object)} has been called. <br>
		 * Throwing any exception will cancel the operation.
		 * @param source the TimeSeries where the put method has been invoked
		 * @param i the Instant
		 * @param value the new value
		 * @see TimeSeries#put(Instant,Object)
		 * @throws UnsupportedOperationException if the TimeSeries is read-only
		 */
		void put(CachedTimeSeries<V> source, Instant i, V value);
		
		/**
		 * Invoked when {@linkplain TimeSeries#remove(Instant)} has been called. <br>
		 * Throwing any exception will cancel the operation.
		 * @param source the TimeSeries where the remove method has been invoked
		 * @param i the Instant specifying the entry to be removed
		 * @see TimeSeries#remove(Instant)
		 * @throws UnsupportedOperationException if the TimeSeries is read-only
		 */
		void remove(CachedTimeSeries<V> source, Instant i);

		/**
		 * Invoked when TimeSeries.remove(Instant) has been called. <br>
		 * Throwing any exception will cancel the operation.
		 * @param source the TimeSeries where the remove method has been invoked
		 * @param p the Period specifying the range to be removed
		 * @see TimeSeries#remove(Period)
		 * @throws UnsupportedOperationException if the TimeSeries is read-only
		 */
		void remove(CachedTimeSeries<V> source, Period p);

	} // ChangeHandler


	/**
	 * Makes the TimeSeries read-only.
	 * @author Thomas A Beck
	 * @version $Id: CachedTimeSeries.java,v 1.15 2008/12/14 19:33:42 tabeck Exp $
	 */
	private static class NoChangeHandler implements ChangeHandler<Object> {
		static final ChangeHandler<Object> INSTANCE = new NoChangeHandler();
		private NoChangeHandler() { /*singleton*/}

		public void put(CachedTimeSeries<Object> source, Instant i, Object value) {
			throw new UnsupportedOperationException();			
		}

		public void remove(CachedTimeSeries<Object> source, Instant i) {
			throw new UnsupportedOperationException();						
		}

		public void remove(CachedTimeSeries<Object> source, Period p) {
			throw new UnsupportedOperationException();						
		}		 
	}

	/**
	 * Tells that this value is not existing in the underlying physical datasource.
	 * The CachedTimeSeries needs to know it, to avoid to repeatedly query for this 
	 * instant.
	 * The fact that there is not value for a given instant, is an information that
	 * must be cached. 
	 * @author Thomas A Beck
	 * @version $Id: CachedTimeSeries.java,v 1.15 2008/12/14 19:33:42 tabeck Exp $
	 */
	private static class NoElementObject {
		static final NoElementObject VALUE = new NoElementObject();
		private NoElementObject() { // singleton
		}
	}


	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TimeSeries#containsInstant(net.sf.jtemporal.Instant)
	 */
	public boolean containsInstant(Instant i) {
		V value = this.get(i);
		return value != null;
	}

	/* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return cache.toString();
    }
}
