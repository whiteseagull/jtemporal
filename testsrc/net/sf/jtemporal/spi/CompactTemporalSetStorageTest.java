/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2006 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.spi;

/**
 * @author Thomas A Beck
 * @version $Id: CompactTemporalSetStorageTest.java,v 1.1 2006/12/04 20:48:44 tabeck Exp $
 */
public class CompactTemporalSetStorageTest extends AbstractTemporalSetStorageTest {

    protected TemporalSetStorage newStorage() {
        return new CompactTemporalSetStorage();
    }

}
