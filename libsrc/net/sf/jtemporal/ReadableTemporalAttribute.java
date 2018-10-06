/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;

/**
 * Superinterface common to anything able to return an object for a given instant.
 * @author Thomas A Beck
 * @version $Id: ReadableTemporalAttribute.java,v 1.4 2007/09/14 20:03:04 tabeck Exp $
 * @param <V> the type of the value
 */
public interface ReadableTemporalAttribute<V> {
	
	/**
	 * Returns the object valid at the given instant.
	 * @param i instant to query
	 * @return the object valid at the given instant
	 */
	public V get(Instant i);
}
