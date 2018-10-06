/*
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.util;

import java.util.Iterator;
 * An iterator the should be closed as soon it is not used anymore. <br>
 * This is useful to avoid waiting the garbage collection to free resources, 
 * such as database resources.
 * @author Thomas A Beck
 * @version $Id: CloseableIterator.java,v 1.5 2007/08/22 19:09:05 tabeck Exp $
 */
public interface CloseableIterator<V> extends Iterator<V> {
	
	/**
	 * Closes the iterator. <br>
	 * Once this method has been called, the iterator behaves as 
	 * If the cursor is already closed, this method just returns,
	 */
	void	close();
	
	/**
	 * Checks whether the iterator is open, or already closed.
	 * @return <code>true</code> if the cursor is open.
	 */	
	boolean isOpen();
}