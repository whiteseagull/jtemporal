/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.spi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import net.sf.jtemporal.Instant;
import net.sf.jtemporal.Period;
import net.sf.jtemporal.SimpleTimedObject;
import net.sf.jtemporal.TimedObject;
import net.sf.jtemporal.util.CloseableIterator;
import net.sf.jtemporal.util.EmptyIterator;
import net.sf.jtemporal.util.IteratorFilter;
import net.sf.jtemporal.util.SimpleCloseableIterator;
import net.sf.jtemporal.util.SingleElementMap;

/**
 * Uses a {@linkplain HashMap}
 * containing {@linkplain CompactPeriodStorage}s <br>
 * <b>Note: this implementation is not synchronized.</b> If multiple
 * threads access the map concurrently, and at least one of the threads modifies
 * the map structurally, it <i>must</i> be synchronized externally.  
 * @author Thomas A Beck
 * @version $Id: CompactTemporalSetStorage.java,v 1.10 2009/01/02 14:55:40 tabeck Exp $
 */
public class CompactTemporalSetStorage<T> implements TemporalSetStorage<T> {

    private Map<T, PeriodStorage> valueMap = null; // initialized lazily

    private PeriodStorage getPeriodStorage(T value) {
        PeriodStorage periodStorage = this.getPeriodStorageOrNull(value);
        if (periodStorage == null) 
            fail(value);
        ;
        return periodStorage;
    }
    
    private PeriodStorage getPeriodStorageOrNull(Object value) {
        PeriodStorage periodStorage = null;
        if (valueMap != null) 
            periodStorage= valueMap.get(value)
        ;
        return periodStorage;
    }
    
    
    private void fail(T value) {
        throw new NoSuchElementException("Value "+value+" not found");        
    }
	
    /* (non-Javadoc)
     * @see net.sf.jtemporal.spi.TemporalSetStorage#lastPeriod(java.lang.Object)
     */
    public Period lastPeriod(T value) {
        Period ret = getPeriodStorage(value).lastPeriod(); 
        if (ret == null) fail(value);
        return ret;
    }

    /* (non-Javadoc)
     * @see net.sf.jtemporal.spi.TemporalSetStorage#firstPeriod(java.lang.Object)
     */
    public Period firstPeriod(T value) {
        Period ret = getPeriodStorage(value).firstPeriod(); 
        if (ret == null) fail(value);
        return ret;
    }

    /* (non-Javadoc)
     * @see net.sf.jtemporal.spi.TemporalSetStorage#isEmpty()
     */
    public boolean isEmpty() {
        if (this.valueMap == null || this.valueMap.isEmpty())
            return true
        ;
        
        for (PeriodStorage periodStorage :  this.valueMap.values()) {
            if (!periodStorage.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /* (non-Javadoc)
     * @see net.sf.jtemporal.spi.TemporalSetStorage#isEmpty(net.sf.jtemporal.Instant)
     */
    public boolean isEmpty(Instant instant) {
        if (this.valueMap == null || this.valueMap.isEmpty())
            return true
        ;
        for (PeriodStorage periodStorage : this.valueMap.values()) {
            if (periodStorage.getPeriod(instant) != null) {
                return false;
            }
        }
        return true;
    }

    /* (non-Javadoc)
     * @see net.sf.jtemporal.spi.TemporalSetStorage#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object value) {
        if (this.valueMap == null)
            return false
        ;
        return this.valueMap.containsKey(value);
    }

    /* (non-Javadoc)
     * @see net.sf.jtemporal.spi.TemporalSetStorage#containsValue(net.sf.jtemporal.Instant, java.lang.Object)
     */
    public boolean containsValue(Instant i, Object value) {
        PeriodStorage periodStorage = this.getPeriodStorageOrNull(value);
        if (periodStorage == null) 
            return false
        ;
        return periodStorage.getPeriod(i) != null;
    }

    /* (non-Javadoc)
     * @see net.sf.jtemporal.spi.TemporalSetStorage#getPeriod(net.sf.jtemporal.Instant, java.lang.Object)
     */
    public Period getPeriod(Instant instant, T value) {
        PeriodStorage periodStorage = this.getPeriodStorageOrNull(value);
        if (periodStorage == null) 
            return null
        ;
        return periodStorage.getPeriod(instant);
    }

    /* (non-Javadoc)
     * @see net.sf.jtemporal.spi.TemporalSetStorage#getPeriodEndingAt(net.sf.jtemporal.Instant, java.lang.Object)
     */
    public Period getPeriodEndingAt(Instant instant, T value) {
        PeriodStorage periodStorage = this.getPeriodStorageOrNull(value);
        if (periodStorage == null) 
            return null
        ;
        return periodStorage.getPeriodEndingAt(instant);
    }

    /* (non-Javadoc)
     * @see net.sf.jtemporal.spi.TemporalSetStorage#getPeriodStartingAt(net.sf.jtemporal.Instant, java.lang.Object)
     */
    public Period getPeriodStartingAt(Instant instant, T value) {
        PeriodStorage periodStorage = this.getPeriodStorageOrNull(value);
        if (periodStorage == null) 
            return null
        ;
        return periodStorage.getPeriodStartingAt(instant);
    }

    /* (non-Javadoc)
     * @see net.sf.jtemporal.spi.TemporalSetStorage#beginAtomicOperation()
     */
    public void beginAtomicOperation() {
        // nothing to do for in-memory operations
    }

    /* (non-Javadoc)
     * @see net.sf.jtemporal.spi.TemporalSetStorage#endAtomicOperation()
     */
    public void endAtomicOperation() {
        // nothing to do for in-memory operations
    }

    /* (non-Javadoc)
     * @see net.sf.jtemporal.spi.TemporalSetStorage#clear()
     */
    public void clear() {
        if (this.valueMap != null) {
            this.valueMap.clear(); // speed up gc
            this.valueMap = null;
        }
    }

    /* (non-Javadoc)
     * @see net.sf.jtemporal.spi.TemporalSetStorage#size()
     */
    public int size() {
        if (this.valueMap == null || this.valueMap.isEmpty())
            return 0
        ;
        long size = 0L;
        for (PeriodStorage periodStorage : this.valueMap.values()) {
            size += periodStorage.size();
        }
        if (size > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        return (int) size;
    }

    /* (non-Javadoc)
     * @see net.sf.jtemporal.spi.TemporalSetStorage#sizeFor(java.lang.Object)
     */
    public int sizeFor(T value) {
        PeriodStorage periodStorage = this.getPeriodStorageOrNull(value);
        if (periodStorage == null) 
            return 0
        ;
        return periodStorage.size();
    }

    /* (non-Javadoc)
     * @see net.sf.jtemporal.spi.TemporalSetStorage#sizeValues()
     */
    public int sizeValues() {
        if (this.valueMap == null)
            return 0
        ;
        return this.valueMap.size();
    }

    /* (non-Javadoc)
     * @see net.sf.jtemporal.spi.TemporalSetStorage#sizeAt(net.sf.jtemporal.Instant)
     */
    public int sizeAt(Instant instant) {
        if (this.valueMap == null || this.valueMap.isEmpty())
            return 0
        ;
        int size = 0;
        for (PeriodStorage periodStorage : this.valueMap.values()) {
            if (periodStorage.getPeriod(instant) != null) {
                size++;
            }
        }
        return size;
    }

    /* (non-Javadoc)
     * @see net.sf.jtemporal.spi.TemporalSetStorage#put(net.sf.jtemporal.Period, java.lang.Object)
     */
    public void put(Period p, T value) {
        PeriodStorage periodStorage;
        
        if (this.valueMap == null) { // adding the first ?
            periodStorage = new CompactPeriodStorage();
            periodStorage.put(p);
            valueMap = new SingleElementMap<T, PeriodStorage>(value, periodStorage);
            return;
        }
        
        periodStorage = this.valueMap.get(value);
        if (periodStorage == null) {  // we need to add a new one
            
            if (this.valueMap.size() == 1) { //adding the second ?
                // create a larger map
                Map<T,PeriodStorage> newMap = new HashMap<T,PeriodStorage>(2);
                newMap.putAll(this.valueMap);
                this.valueMap = newMap;
            }
            
            // anyway, add normally
            periodStorage = new CompactPeriodStorage();
            this.valueMap.put(value, periodStorage);
        }
        //assert periodStorage != null;
        
        periodStorage.put(p);
    }

    /* (non-Javadoc)
     * @see net.sf.jtemporal.spi.TemporalSetStorage#remove(net.sf.jtemporal.Period, java.lang.Object)
     */
    public void remove(Period p, T value) {
        PeriodStorage periodStorage = getPeriodStorage(value);
        //assert periodStorage != null;
        periodStorage.remove(p);
        
        if (periodStorage.isEmpty()) {
            // do not keep an empty PeriodStorage
            this.removeValue(value);
        }
    }

    /* (non-Javadoc)
     * @see net.sf.jtemporal.spi.TemporalSetStorage#removeValue(java.lang.Object)
     */
    public boolean removeValue(T value) {
        if (this.valueMap == null)	
            return false
        ;
        PeriodStorage periodStorage = this.valueMap.get(value);
        if (periodStorage == null) 
            return false
        ;
        
        // we have now found the periodStorage for this value
        periodStorage.clear(); // invalidate iterators
        
        if (this.valueMap instanceof SingleElementMap) {
            this.valueMap = null;
        }
        else {
            this.valueMap.remove(value);
            this.onValueMapDelete();
        }
        return true;
    }

    private void onValueMapDelete() {
        if (this.valueMap.isEmpty()) {
            // do not keep an empty map
            this.valueMap = null;
        }
    }
    
    private class EntryIterator implements CloseableIterator<TimedObject<T>> {
    	
        boolean isOpen = true;
        
        // outer loop
        Iterator<Map.Entry<T,PeriodStorage>> valueIterator; 
        
        T currentValue;
        
        PeriodStorage currentPeriodStorage;

        // inner loop
        CloseableIterator<Period> periodIterator;
        
        EntryIterator() {
        	// we can assume that there is at least a value entry
        	// containing at least a period entry
        	
        	valueIterator 
        		= CompactTemporalSetStorage.this.valueMap.entrySet().iterator()
            ;
        	periodIterator = EmptyIterator.getInstance();
        }
        
        /**
         * @throws NoSuchElementException
         */
        private void advanceValue() {
        	Map.Entry<T,PeriodStorage> entry = valueIterator.next();
        	currentValue = entry.getKey();
        	currentPeriodStorage = entry.getValue();
        	periodIterator = currentPeriodStorage.periodIterator();
        }
        
        public void close() {
            this.currentValue = null;
            this.valueIterator = null;
            this.periodIterator = null;
            this.currentPeriodStorage = null;
            this.isOpen = false;
        }

        public boolean isOpen() {
            return this.isOpen;
        }

        public boolean hasNext() {
        	return periodIterator.hasNext() || valueIterator.hasNext();
        }

        public TimedObject<T> next() {
        	if (!periodIterator.hasNext()) {
        		advanceValue();
        	}
        	
        	Period nextPeriod = periodIterator.next();
        	TimedObject<T> nextTimedObject = 
        		new SimpleTimedObject<T> (nextPeriod, this.currentValue)
        	;
        	
            return nextTimedObject;
        }
        
        public void remove() {
        	if (periodIterator == (Object)EmptyIterator.getInstance()) {
        		throw new IllegalStateException("Called remove() before next()");
        	}

        	periodIterator.remove();
        	if (currentPeriodStorage.isEmpty()) {
        		currentPeriodStorage.clear(); // invalidate iterators
        		
                if (valueMap instanceof SingleElementMap) {
                    valueMap = null;
                }
                else {
                	valueIterator.remove();
                    onValueMapDelete();
                }
        	}
        }

    }
    
    /* (non-Javadoc)
     * @see net.sf.jtemporal.spi.TemporalSetStorage#entryIterator()
     */
    public CloseableIterator<TimedObject<T>> entryIterator() {
        if (CompactTemporalSetStorage.this.valueMap == null) {
            return EmptyIterator.getInstance();
        }
        
        return new EntryIterator();
    }

    /* (non-Javadoc)
     * @see net.sf.jtemporal.spi.TemporalSetStorage#entryIterator(net.sf.jtemporal.Instant)
     */
    public CloseableIterator<TimedObject<T>> entryIterator(Instant instant) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /* (non-Javadoc)
     * @see net.sf.jtemporal.spi.TemporalSetStorage#entryIterator(net.sf.jtemporal.Period)
     */
    public CloseableIterator<TimedObject<T>> entryIterator(Period period) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /* (non-Javadoc)
     * @see net.sf.jtemporal.spi.TemporalSetStorage#entryIterator(net.sf.jtemporal.Period, java.lang.Object)
     */
    public CloseableIterator<Period> periodIterator(final Period period, final T value) {
        final PeriodStorage periodStorage 
        	= this.getPeriodStorageOrNull(value)
        ;
        if (periodStorage == null)
            return EmptyIterator.getInstance()
        ;
        
        return new CloseableIterator<Period>() {
            CloseableIterator<Period> i = periodStorage.periodIterator(period);
            public void close() {
                if (i != null) {
                    i.close();
                    i = null;
                }
            }
            public boolean isOpen() {
                return i != null;
            }
            public boolean hasNext() {
                if (i == null) return false;
                return i.hasNext();
            }

            public void remove() {
                if (i == null) throw new NoSuchElementException();
                i.remove();
                if (periodStorage.isEmpty()) {
                    this.close();
                    CompactTemporalSetStorage.this.removeValue(value);
                }
            }

            public Period next() {
                if (i == null) throw new NoSuchElementException();
                return i.next();
            }            
        }; // new CloseableIterator
    }

    /* (non-Javadoc)
     * @see net.sf.jtemporal.spi.TemporalSetStorage#periodIterator(java.lang.Object)
     */
    public CloseableIterator<Period> periodIterator(T value) {
    	PeriodStorage periodStorage = this.getPeriodStorageOrNull(value);
    	if (periodStorage == null) return EmptyIterator.getInstance();
    	return new SimpleCloseableIterator<Period>(periodStorage.periodIterator()) {
    		@Override
    		public void remove() {
    			// TODO Auto-generated method stub
    			throw new UnsupportedOperationException("Not implemented yet");
    		};
    	};
    }

    /* (non-Javadoc)
     * @see net.sf.jtemporal.spi.TemporalSetStorage#valueIterator()
     */
    public CloseableIterator<T> valueIterator() {
        if (this.valueMap == null) {
            return EmptyIterator.getInstance();
        }
        
        return new SimpleCloseableIterator<T>(this.valueMap.keySet().iterator()){
            @Override
            public void remove() {
                // TODO remove redundancy
                if (valueMap instanceof SingleElementMap) {
                    valueMap = null;
                    this.close();
                    return;
                }
                super.remove();
                CompactTemporalSetStorage.this.onValueMapDelete();
            }
        };
    }

    /* (non-Javadoc)
     * @see net.sf.jtemporal.spi.TemporalSetStorage#valueIterator(net.sf.jtemporal.Instant)
     */
    public CloseableIterator<T> valueIterator(final Instant instant) {
        if (this.valueMap == null) {
            return EmptyIterator.getInstance();
        }

        return new IteratorFilter<Map.Entry<T,PeriodStorage>,T>(this.valueMap.entrySet().iterator()) {
            
            /* (non-Javadoc)
             * @see net.sf.jtemporal.util.IteratorFilter#shows(java.lang.Object)
             */
            @Override
            protected boolean shows(Map.Entry<T,PeriodStorage> mapEntry) {
                
                PeriodStorage periodStorage = mapEntry.getValue();
                return periodStorage.getPeriod(instant) != null;
            }
            /* (non-Javadoc)
             * @see net.sf.jtemporal.util.IteratorFilter#next()
             */
            public T next() {
                return super.nextOnIteratorFilter().getKey();
            }
            
            /* (non-Javadoc)
             * @see net.sf.jtemporal.util.IteratorFilter#remove()
             */
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (this.valueMap == null) {
            return "[]";
        }
        return this.valueMap.toString();
    }
}
