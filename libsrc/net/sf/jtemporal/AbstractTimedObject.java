/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with NO WARRANTIES given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;

/**
 * Associates an Object to a Period.
 * The semantics of the period is free.
 * Is immutable when the value is immutable as well.
 * @author Thomas A Beck
 * @version $Id: AbstractTimedObject.java,v 1.3 2009/01/02 14:55:40 tabeck Exp $
 * @param <V> the type of the value
 */
public abstract class AbstractTimedObject<V> implements TimedObject<V> {

  /**
   * Must always return the same Period.
   * @return the period associated to the value
   */
  public abstract Period getPeriod();

  /**
   * Must always return the same reference of value.
   * @return the value
   */
  public abstract V getValue();


    @Override
  public boolean equals(Object o) {
    if (o == null) return false;
    if (o == this) return true;
    if (!(o instanceof AbstractTimedObject)) return false;
    AbstractTimedObject<?> to = (AbstractTimedObject<?>)o;
    return getValue().equals(to.getValue())
        && getPeriod().equals(to.getPeriod());
  }

  /**
   * Computes an hash code for this TimedObject.
   * @return  an hash code value for this object.
   */
    @Override
  public int hashCode() {
    return this.getValue().hashCode() ^ this.getPeriod().hashCode();
  }

    @Override
  public String toString() {
    return "TimedObject:("+this.getPeriod()+","+this.getValue()+")";
  }

}
