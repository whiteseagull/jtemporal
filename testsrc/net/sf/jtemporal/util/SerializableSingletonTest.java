/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.util;

import junit.framework.*;
import java.io.*;

/**
 * @author Thomas A Beck
 * @version $Id: SerializableSingletonTest.java,v 1.2 2008/12/14 14:30:36 tabeck Exp $
 */
public class SerializableSingletonTest extends TestCase {

	public SerializableSingletonTest(String name) {
		super(name);
	}
	
	
	public void testEmptyIterator() {
	  testSingleton(EmptyIterator.getInstance());
	}

	/////////////////////////////////////////////////////////////////////////
	
	private void testSingleton(Serializable singleton) {
	  assertTrue(singleton == this.cloneBySerialisation(singleton));
	}

	private Serializable cloneBySerialisation(Serializable singleton) {
	  ByteArrayOutputStream aos;
	  ObjectOutputStream oos;
	  Serializable clone = null;
	  byte[] array = null;

	  // write the object
	  try {
		aos = new ByteArrayOutputStream(1000);
		oos = new ObjectOutputStream( aos );
		oos.writeObject( singleton );
		oos.flush();
		array = aos.toByteArray();
		oos.close();
		aos.close();
	  } catch (java.io.IOException ioex) {
		fail("The singleton "+singleton+" cannot be written." + ioex.getMessage());
	  }

	  // read the serialized collection
	  try {
		ByteArrayInputStream ais = new ByteArrayInputStream(array);
		ObjectInputStream ois = new ObjectInputStream( ais );
		clone = (Serializable) ois.readObject();
		ois.close();
		ais.close();
	  } catch (ClassNotFoundException cnex) {
		fail("The serializable "+singleton+" class cannot be read." + cnex.getMessage());
	  } catch (java.io.IOException ioex) {
		fail("The serializable "+singleton+" cannot be read." + ioex.getMessage());
	  }
	  if (clone == null) fail("clone == null");
	  return clone;
	}

}
