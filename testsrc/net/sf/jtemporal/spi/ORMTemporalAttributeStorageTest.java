/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.spi;

import java.util.TreeMap;

import net.sf.jtemporal.Period;
import net.sf.jtemporal.SimpleTimedObject;
import net.sf.jtemporal.TimedObject;


/**
 * @author Thomas A Beck
 * @version $Id: ORMTemporalAttributeStorageTest.java,v 1.2 2006/02/16 14:23:04 tabeck Exp $
 */
public class ORMTemporalAttributeStorageTest extends AbstractTemporalAttributeStorageTest 
{
	public final TemporalAttributeStorage newStorage() {
		return new ORMTemporalAttributeStorage(
		    new TreeMap(StartComparator.COMPARATOR),
		    new TimedObjectFactory(
		        ) {
                    public TimedObject create(Period p, Object value) {
                        return new SimpleTimedObject(p,value);
                    }
                    public void destroy(TimedObject to) {
                    }
                }
		);
	}
	
	/**
	 * Constructor for TreeTemporalAttributeStorageTest.
	 * @param name
	 */
	public ORMTemporalAttributeStorageTest(String name) {
		super(name);
	}

	public void testContainsValue() {
	    // ignore this test
	}
}
