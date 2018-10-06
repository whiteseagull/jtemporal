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
 * A read-only Iterator, iterating over a single element. <br>
 * @author Thomas A Beck
 * @version $Id: SingleElementIterator.java,v 1.2 2008/12/14 13:47:23 tabeck Exp $
 */
public class SingleElementIterator<V> implements Iterator<V> {

    private V element;
    private boolean hasNext = true;
    
    /**
     * Unique constructor.
     * @param element the element that will be returned once and
     * only once by the {@linkplain SingleElementIterator#next()}
     * method
     */
    public SingleElementIterator(V element) {
        this.element = element;
    }
    
    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        return this.hasNext;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public V next() {
        if (!hasNext)
            throw new NoSuchElementException()
        ;
        hasNext = false;
        V ret = element;
        element = null; // allow gc
        return ret;
    }
}
