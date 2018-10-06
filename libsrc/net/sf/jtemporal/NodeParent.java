/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with NO WARRANTIES given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */


package net.sf.jtemporal;

/**
 * Title:        JTemporal
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @deprecated
 * @author Thomas A Beck
 * @version $Id: NodeParent.java,v 1.2 2009/01/02 17:05:03 tabeck Exp $
 */

interface NodeParent<V> {
    void nodeDead(TimeNode<V> n);
}
