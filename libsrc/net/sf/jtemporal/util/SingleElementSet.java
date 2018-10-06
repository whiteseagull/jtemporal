/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.util;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * A read-only Set, contining a single element. <br>
 * This is intended to be used to replace other implementations
 * of {@linkplain Set} when they contain a single element, to save memory,
 * and improve read performances.  <br>
 * Thread-safe.
 * @author Thomas A Beck
 * @version $Id: SingleElementSet.java,v 1.3 2008/12/14 17:19:29 tabeck Exp $
 */

/**
 * @author Thomas A Beck
 * @version $Id: SingleElementSet.java,v 1.3 2008/12/14 17:19:29 tabeck Exp $
 */
public class SingleElementSet<V> extends AbstractSet<V> implements Cloneable {

    private final V element;
    
    /**
     * Unique constructor
     * @param element to be part of the Set
     */
    public SingleElementSet(V element) {
        this.element = element;
    }
    
    /* (non-Javadoc)
     * @see java.util.Set#size()
     */
    @Override
    public int size() {
        return 1;
    }

    /* (non-Javadoc)
     * @see java.util.Set#clear()
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see java.util.Set#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.Set#toArray()
     */
    @Override
    public Object[] toArray() {
        return  new Object[] {element};
    }

    /* (non-Javadoc)
     * @see java.util.Set#add(java.lang.Object)
     */
    @Override
    public boolean add(V o) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see java.util.Set#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object o) {
        return o.equals(this.element);
    }

    /* (non-Javadoc)
     * @see java.util.Set#remove(java.lang.Object)
     */
    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see java.util.Set#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(Collection<? extends V> c) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see java.util.Set#containsAll(java.util.Collection)
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c){
            if (!this.element.equals(o)) {
                return false;
            }
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.util.Set#removeAll(java.util.Collection)
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see java.util.Set#retainAll(java.util.Collection)
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see java.util.Set#iterator()
     */
    @Override
    public Iterator<V> iterator() {
        return new SingleElementIterator<V>(element);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone()  {
        // since I am immutable, this instance can be shared in any case ;-)
        return this;
    }
    
    /* (non-Javadoc)
     * @see java.util.AbstractSet#hashCode()
     */
    @Override
    public int hashCode() {
        return this.element.hashCode();
    }
    
    /* (non-Javadoc)
     * @see java.util.AbstractSet#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
    	if (!(o instanceof Set))
    	    return false
    	;
    	Set<V> other = (Set<V>)o;
    	if (other.size() != 1)
    	    return false
    	;
    	return other.contains(this.element);
    }
    
    /* (non-Javadoc)
     * @see java.util.AbstractCollection#toString()
     */
    @Override
    public String toString() {
        return "[" + this.element +"]";
    }
}
