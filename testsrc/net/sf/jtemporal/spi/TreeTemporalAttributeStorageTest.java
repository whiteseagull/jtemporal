/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.spi;


/**
 * @author Thomas A Beck
 * @version $Id: TreeTemporalAttributeStorageTest.java,v 1.6 2005/10/02 18:34:10 tabeck Exp $
 */
public class TreeTemporalAttributeStorageTest extends AbstractTemporalAttributeStorageTest 
{
	public final TemporalAttributeStorage newStorage() {
		return new TreeTemporalAttributeStorage();
	}
	
	/**
	 * Constructor for TreeTemporalAttributeStorageTest.
	 * @param name
	 */
	public TreeTemporalAttributeStorageTest(String name) {
		super(name);
	}


}
