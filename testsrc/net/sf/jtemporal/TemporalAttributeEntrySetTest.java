package net.sf.jtemporal;

import java.util.Set;

import net.sf.jtemporal.spi.TreeTemporalAttributeStorage;
import net.sf.jtemporal.util.GenericSetTst;

public class TemporalAttributeEntrySetTest 
	extends GenericSetTst implements TestConstants 
{
	private TemporalAttribute map;
  	static private final Object[] VALUES = {
  	  		new SimpleTimedObject(p13, s1),  
  			new SimpleTimedObject(p34, s2),  
  			new SimpleTimedObject(p46, s3),  
  			new SimpleTimedObject(p68, s1)  
  	  	};

	public TemporalAttributeEntrySetTest(String name) {
		super(name);
	}
		
	public Set getNewSet() {
	    map = new TemporalAttributeImpl(
	        	new TreeTemporalAttributeStorage()
	    );
		map.put(p18, s1);
		map.put(p34, s2);
		map.put(p46, s3);
		Set set = map.entrySet();
		return set;
	}

	public Object[] getValues() {
		return VALUES;
	}

	public boolean allowsAdd() {
		return false;
	}

	public boolean allowsRemove() {
		return true;
	}
}
