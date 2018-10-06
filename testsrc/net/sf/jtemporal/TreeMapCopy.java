/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with NO WARRANTIES given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;

import java.util.*;

/**
 * @author Thomas A Beck
 * @version $Id: TreeMapCopy.java,v 1.1 2003/11/16 17:43:09 tabeck Exp $
 */

public class TreeMapCopy implements MapSubscriber {

  private final TreeMap map = new TreeMap();

  public TreeMapCopy() {
  }
  public void put(Map publisher, Object key, Object value) {
    this.map.put(key, value);
  }

  public void removed(Map publisher, Object key) {
    this.map.remove(key);
  }
/*
  public void clear() {
    this.map.clear();
  }*/

  public TreeMap getTreeMap() {
    return this.map;
  }
}