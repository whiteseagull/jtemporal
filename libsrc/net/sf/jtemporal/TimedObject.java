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
 * @version $Id: TimedObject.java,v 1.8 2007/09/23 20:07:52 tabeck Exp $
 * @param <V> the type of the value
 */
public interface TimedObject<V> {

  /**
   * Must always return the same Period.
   * @return the period associated to the value
   */
  Period getPeriod();

  /**
   * Must always return the same reference of value.
   * @return the value
   */
  V getValue();

}
