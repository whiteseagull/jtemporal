/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.util;

/**
 * Always return a new object to be tested, build in the same way as the previous one.
 * @author Thomas A Beck
 * @version $Id: TestObjectFactory.java,v 1.1 2005/03/20 17:17:58 tabeck Exp $
 */
public interface TestObjectFactory {

	Object getTestObject();
}
