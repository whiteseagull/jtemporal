/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with NO WARRANTIES given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;

import java.util.Iterator;

/**
 * @author Thomas A Beck
 * @version $Id: Bucket.java,v 1.1 2003/11/16 18:07:01 tabeck Exp $
 */
interface Bucket<V> {
    void add(TimedObject<V> to);

    void remove(TimedObject<V> to);

    Iterator<V> iterator();

    int size();
}
