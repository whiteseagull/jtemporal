/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with NO WARRANTIES given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;

import java.util.Map;

/**
 * This interface is a minimal specification, to allow a MapSubscriber call-back
 * to reach the best possible performances.
 * For this reason there is ony one subscriber, and no event objects are created.
 * If you need standard events and listeners, then you should create a gateway object,
 * which is subscriber. This object will then instantiate and distribute the
 * events to the listeners.
 * The performance cost of having one more intermediate object is negligible
 * compared to the cost of instantiating events and sending them to multiple listeners.
 * @author Thomas Beck
 * @version $Id: MapSubscriber.java,v 1.2 2009/01/02 17:05:03 tabeck Exp $
 */

interface MapSubscriber<K, V> {
  void put(Map<K, V> publisher, K key, V value);
  void removed(Map<K, V> publisher, K key);
}
