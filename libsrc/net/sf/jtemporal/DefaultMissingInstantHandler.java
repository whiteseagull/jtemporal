/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with NO WARRANTIES given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;

/**
 * Dumb handler, it just does nothing.
 * @author Thomas A Beck
 * @version $Id: DefaultMissingInstantHandler.java,v 1.5 2008/12/14 19:39:08 tabeck Exp $
 * @deprecated
 */
class DefaultMissingInstantHandler implements MissingInstantHandler<Object> {

	static final DefaultMissingInstantHandler INSTANCE = new DefaultMissingInstantHandler(); 

	public void handleMissingElement(TimeSeries<Object> ts, Instant i) {
	}

}
