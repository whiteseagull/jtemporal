/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;

import java.io.Serializable;

import net.sf.jtemporal.spi.TreeTemporalAttributeStorage;

/**
 * Uses internally a TreeMap to store the mappings.
 * The Periods as used as keys.
 * <BR>
 * Links at any time an object (the map holder) to zero or one object (value),
 * example: Employee-Salary.
 * You get the value valid at a given time with the method Object get(Instant i).
 * To add a mapping, you do not specify an Instant, but a Period specifying the validity
 * of the object.  If another object exists valid during this period, the validity will be
 * overwritten for new object period.
 * <BR><b>
 * Note that this implementation is not synchronized.</b> If multiple
 * threads access a map concurrently, and at least one of the threads modifies
 * the map structurally, it <i>must</i> be synchronized externally.  (A
 * structural modification is any operation that adds or deletes one or more
 * mappings; merely changing the value associated with an existing key is not
 * a structural modification.)  This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the map.  If no
 * such object exists, the map should be "wrapped" using the
 * <tt>Collections.synchronizedMap</tt> method.  This is best done at creation
 * time, to prevent accidental unsynchronized access to the map:
 * <pre>
 *     TemporalAttribute tm = SynchronizedTemporalMap.getInstance(new TreeTemporalSet(...));
 * </pre><p>
 * <br>
 * Actually, this is just a TemporalAttributeImpl instance using a 
 * TreeTemporalAttributeStorage as a storage.
 * @author Thomas A Beck
 * @version $Id: TreeTemporalAttribute.java,v 1.11 2008/12/23 21:11:28 tabeck Exp $
 * @see net.sf.jtemporal.TemporalAttributeImpl
 * @see net.sf.jtemporal.spi.TreeTemporalAttributeStorage
 * @param <V> the type of the value
 */
public class TreeTemporalAttribute<V> extends TemporalAttributeImpl<V>
	implements Serializable
{
    private static final long serialVersionUID = 5645000497548798543L;

	public TreeTemporalAttribute() {
        super(new TreeTemporalAttributeStorage<V>());
	}
}
