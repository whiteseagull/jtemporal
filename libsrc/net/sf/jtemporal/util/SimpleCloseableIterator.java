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
 * Decorates an existing Iterator to make it closeable. <br>
 * Can also be used just as an iterator proxy to be subclassed and
 * decorated. <br>
 * Keeps track of what object has been fetched the lat time the
 * method {@linkplain #next()} has been called. <br>
 * This class is designed to be subclassed to add more functionality. <br>
 * Not thread safe.
 * @author Thomas A Beck
 * @version $Id: SimpleCloseableIterator.java,v 1.8 2008/12/23 21:06:26 tabeck Exp $
 */
public class SimpleCloseableIterator<T> implements CloseableIterator<T> {

    private Iterator<T> model = null;
    private T lastFetched = null;
	private boolean fetchedAlready = false;
	
 	/**
 	 * If the passed iterator is instanceof {@linkplain CloseableIterator}
 	 * then it will closed when this iterator is closed.
 	 * @param model the model iterator
 	 */
    public SimpleCloseableIterator(Iterator<T> model) {
		this.model = model;
	}

	/**
	 * Returns the last object that has been retrieved using the 
	 * {@linkplain #next()} method.
	 * @return the last object that has been retrieved using the 
	 * {@linkplain #next()} method.
	 * @throws IllegalStateException if {@linkplain #next()} has
	 * not been called yet.
	 */
	public T getLastFetched() {
	    if (!fetchedAlready) {
	        throw new IllegalStateException("next() has not been called yet");
	    }
	    return this.lastFetched;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jtemporal.util.CloseableIterator#close()
	 */
	public void close() {
 	    if (this.model instanceof CloseableIterator) {
 	        ((CloseableIterator<T>)this.model).close();
 	    }
		// allows model garbage collection
		this.model = null;
		this.lastFetched = null;
		this.fetchedAlready = false;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		if (model == null) {
            throw new NoSuchElementException("Iterator is closed");
		}
		model.remove();
		this.lastFetched = null;
		this.fetchedAlready = false;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		if (model == null) {
		    return false;
		}

		boolean hasNext = model.hasNext();

		if (!hasNext) 
		    // even if you keep a reference to this iterator
		    // the model can be garbaged
		    this.close()
		;
		return hasNext;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
    public T next() {
		if (model == null) {
            throw new NoSuchElementException("Iterator is closed");
		}
		fetchedAlready = true;
		lastFetched = model.next();
		return lastFetched;
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.util.CloseableIterator#isOpen()
	 */
	public boolean isOpen() {
		return model != null;
	}
}