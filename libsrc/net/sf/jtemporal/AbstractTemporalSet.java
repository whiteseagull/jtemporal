/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with NO WARRANTIES given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;

/**
 * Features common to the TemporalSets.
 * @author Thomas A Beck
 * @version $Id: AbstractTemporalSet.java,v 1.6 2009/01/02 14:55:40 tabeck Exp $
 * @param <V> the type of the value
 */
abstract public class AbstractTemporalSet<V> implements TemporalSet<V> {


  public Period extent(V value) {
    return new Period(this.firstInstant(value), this.lastInstant(value));
  }

    @Override
  public boolean equals(Object o) {
    throw new UnsupportedOperationException();
  }

  public boolean putAll(TemporalSet<? extends V> temporalSet) {
    if (temporalSet == null) throw new IllegalArgumentException("null");
    boolean ret = false;
    
    for (TimedObject<? extends V> to : temporalSet.entrySet()) {
    	ret |= this.put(to.getPeriod(), to.getValue());
    }
    
    return ret;
  }

}
