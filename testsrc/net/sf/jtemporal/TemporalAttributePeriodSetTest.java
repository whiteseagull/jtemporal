package net.sf.jtemporal;

import java.util.Set;

import net.sf.jtemporal.spi.TreeTemporalAttributeStorage;
import net.sf.jtemporal.util.GenericSetTst;

public class TemporalAttributePeriodSetTest 
	extends GenericSetTst implements TestConstants 
{
	private TemporalAttribute map;
	static private final Object[] VALUES = {p13,p34,p46,p68};
	
	public TemporalAttributePeriodSetTest(String name) {
		super(name);
	}
		
	public Set getNewSet() {
	    map = new TemporalAttributeImpl(
	        	new TreeTemporalAttributeStorage()
	    );
		map.put(p18, s1);
		map.put(p34, s2);
		map.put(p46, s3);
		Set set = map.periodSet();
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
