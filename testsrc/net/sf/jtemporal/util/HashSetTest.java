/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.util;

import java.util.*;

/**
 * Test intended to test the SetTest test (yes, test the test).
 * Will test a HashSet
 * @author Thomas A Beck
 * @version $Id: HashSetTest.java,v 1.2 2005/10/29 16:33:57 tabeck Exp $
 */
public class HashSetTest extends GenericSetTst {

    public static void main(String[] args) {
        HashSetTest i = new HashSetTest("HashSetTest");
        junit.textui.TestRunner.run(HashSetTest.class);
    }
    
	/**
	 * Constructor for SetTestTest.
	 * @param name
	 */
	public HashSetTest(String name) {
		super(name);
	}

	/* (non-Javadoc)
     * @see net.sf.jtemporal.util.GenericSetTst#getValues()
     */
    public Object[] getValues() {
        return VALUES;
    }
	
	private static final Object[] VALUES = {
		new Integer(4),
		new Integer(1),
		new Integer(3),
		new Integer(5)
	};
	


	/* (non-Javadoc)
	 * @see net.sf.jtemporal.util.GenericSetTst#getNewSet()
	 */
	public Set getNewSet() {
		HashSet set = new HashSet();
		List list = Arrays.asList(this.getValues());
		set.addAll(list);
		return set;
	}

    /* (non-Javadoc)
     * @see net.sf.jtemporal.util.GenericSetTst#allowsAdd()
     */
    public boolean allowsAdd() {
        return true;
    }

    /* (non-Javadoc)
     * @see net.sf.jtemporal.util.GenericSetTst#allowsRemove()
     */
    public boolean allowsRemove() {
        return true;
    }	

	
}
