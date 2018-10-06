/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2007 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */


package net.sf.jtemporal;

/**
 * Convenience class to avoid to use generics when creating a Period
 * @author Thomas A Beck
 * @version $Id: IntPeriod.java,v 1.2 2007/09/14 20:03:11 tabeck Exp $
 */
public class IntPeriod extends Period {

	private static final long serialVersionUID = 1L;

	public IntPeriod(IntInstant start, IntInstant end) {
		super(start, end);
	}

	@Override
	public IntInstant getEnd() {
		return (IntInstant) super.getEnd();
	}

	@Override
	public IntInstant getStart() {
		return (IntInstant) super.getStart();
	}
	
	public int elapsed() {
		if (this.getEnd().isPositiveInfinity()
			|| this.getStart().isNegativeInfinity()
		) {
			return Integer.MAX_VALUE;
		}
		return 
		    this.getEnd().toIntValue()- this.getStart().toIntValue()
		;
	}
}
