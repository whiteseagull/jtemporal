/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
 Copyright (C) 2002 by the author(s).
 Distributable under LGPL license version 2.1 or later, 
 with NO WARRANTIES given or implied.
 See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.util;

import java.util.NoSuchElementException;
import java.io.InvalidObjectException;
import java.io.Serializable;

/**
 * Immutable empty read-only iterator (singleton). <BR>
 * <B>For methods description, see java.util.Iterator </B>
 * 
 * @author Thomas Beck
 * @version $Id: EmptyIterator.java,v 1.3 2008/12/23 21:06:52 tabeck Exp $
 */

public class EmptyIterator<T> implements 
	Serializable, CloseableIterator<T>, Cloneable 
{

    private static final EmptyIterator<Object> INSTANCE = new EmptyIterator<Object>();

    private static final long serialVersionUID = 9365825104636L;

    /**
     * Given that the iterator is stateless, this is a singleton.
     * @return the shared instance
     */
    @SuppressWarnings("unchecked") // see Effective Java 2nd edition, pg 131
	public static <T> EmptyIterator<T> getInstance() {
        return (EmptyIterator<T>) INSTANCE;
    }

    private EmptyIterator() {
        super();
    }

    /**
     * Resolves instances being deserialized to the predefined unique instance.
     */
    private Object readResolve() throws InvalidObjectException {
        return INSTANCE;
    }

    public boolean hasNext() {
        return false;
    }

    public T next() {
        throw new NoSuchElementException();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public void close() {
        return;
    }

    public boolean isOpen() {
        return false;
    }
    
    public Object clone() {
        return this;
    }
}