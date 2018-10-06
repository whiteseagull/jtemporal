/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

 /**
  * A proxy over an existing iterator, that filters some entries. <br>
  * Not thread safe.
  * @param <M> the type of value held by the underlying model iterator. (M stands for Model)
  * @param <V> the type of value held by the iterator we are creating, as returned by {@linkplain #next()} (V stands for View)
  * @author Thomas A Beck
  * @version $Id: IteratorFilter.java,v 1.4 2009/01/02 15:20:27 tabeck Exp $
  */
 public abstract class IteratorFilter<M,V> implements CloseableIterator<V> {

    private Iterator<M> model = null;
    private M prefetched = null;

 	/**
 	 * If the passed iterator is instanceof {@linkplain CloseableIterator}
 	 * then it will closed when this iterator is closed.
 	 * @param model the iterator whose entries must be filtered
 	 */
     public IteratorFilter(Iterator<M> model) {
 		this.model = model;
 	}

 	/**
 	 * Tells whether an entry must be filtered or not. <br>
 	 * It is allowed for the implementation of this method
 	 * to call {@linkplain IteratorFilter#close()}.  In this case,
 	 * the method will return normally, but the method
 	 * {@linkplain IteratorFilter#hasNext()} will stop 
 	 * looking further in the model iterator.
      * @param o the object retrieved from the underlying iterator
 	 * that must be tested
 	 * @return <code>true</code> if the object is be visible 
 	 * to the client of this iterator, <code>false</code> if the
 	 * object is filtered out.
 	 */
     protected abstract boolean shows(M o);

 	/**
 	 * {@inheritDoc} <br>
 	 * If the passed iterator is instanceof {@linkplain CloseableIterator}
 	 * then it will closed when this iterator is closed.
 	 */
 	public void close() {
 		if (!isOpen()) {
 		    return;
 		}

         if (model instanceof CloseableIterator) {
             ((CloseableIterator<M>)model).close();
 	    }

 		// allows gc
         model = null;
         prefetched = null;
 	}

 	/**
 	 * {@inheritDoc} <br>
 	 * As soon {@linkplain IteratorFilter#hasNext()} has been called, 
 	 * this method cannot be used anymore. <br>
 	 * This method can be called only just after 
 	 * {@linkplain IteratorFilter#next()} 
 	 */
 	public void remove() {
 		if (!isOpen()) {
 			throw new IllegalStateException();
 		}

         if (prefetched != null) {
 			throw new IllegalStateException(); 		    
 		}

 		model.remove();
 	}

 	/* (non-Javadoc)
 	 * @see java.util.Iterator#hasNext()
 	 */
 	public boolean hasNext() {

 	    if (!isOpen()) {
 			return false;
 		}

         if (prefetched != null) {
 		    return true;
 		}
 		
 		// so, prefetched == null
 		while (model.hasNext()) {
             M o = model.next();

             if (shows(o)) {
                 if (isOpen()) // since shows can close
                     prefetched = o;
	 		    return true;
	 		}

	 		// since shows can close
             if (!isOpen()) {
 		        return false;
 		    }
 		}
 		
 		// so, !hasNext
	    // even if you keep a reference to this iterator
	    // the model can be garbaged
         close();
	    return false;
	    
 	} // hasNext()

 	/* (non-Javadoc)
 	 * @see java.util.Iterator#next()
 	 */
     public final M nextOnIteratorFilter() {
 		if (!isOpen()) {
 			throw new NoSuchElementException();
 		}

         if (prefetched == null && !hasNext()) {
             close();
 		    throw new NoSuchElementException();
 		} 		

         M ret = prefetched;
         prefetched = null;
	    return ret;
 	}
    
    /**
     * This default implementation returns the what the model 
     * iterator returns.  It works only when the <M> and <V> are
     * of the same type.  If they are of different type, you need
     * to override this method, call {@linkplain #nextOnIteratorFilter()}
     * to get the model value, and create the V value to be returned. 
     */ 
    @SuppressWarnings("unchecked")
	public V next() {
    	// cast from M to V
    	return (V) this.nextOnIteratorFilter();
    }

 	/* (non-Javadoc)
 	 * @see net.sf.jtemporal.util.CloseableIterator#isOpen()
 	 */
 	public boolean isOpen() {
 		return model != null;
 	}
 }
