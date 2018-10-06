/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with NO WARRANTIES given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;

/**
 * @author Thomas A Beck
 * @version $Id: TestConstants.java,v 1.5 2007/08/19 16:31:46 tabeck Exp $
 */

public interface TestConstants {
  final IntInstant i0    = new IntInstant(  0);
  final IntInstant i1    = new IntInstant(100);
  final IntInstant i1bis = new IntInstant(100);
  final IntInstant i2    = new IntInstant(200);
  final IntInstant i3    = new IntInstant(300);
  final IntInstant i4    = new IntInstant(400);
  final IntInstant i5    = new IntInstant(500);
  final IntInstant i6    = new IntInstant(600);
  final IntInstant i7    = new IntInstant(700);
  final IntInstant i8    = new IntInstant(800);
  final IntInstant i9    = new IntInstant(900);
  final IntInstant infinite = new IntInstant(Integer.MAX_VALUE);

  final IntPeriod p12    = new IntPeriod(i1,i2);
  final IntPeriod p13    = new IntPeriod(i1,i3);
  final IntPeriod p14    = new IntPeriod(i1,i4);
  final IntPeriod p15    = new IntPeriod(i1,i5);
  final IntPeriod p23    = new IntPeriod(i2,i3);
  final IntPeriod p24    = new IntPeriod(i2,i4);
  final IntPeriod p24bis = new IntPeriod(i2,i4);
  final IntPeriod p34    = new IntPeriod(i3,i4);
  final IntPeriod p34bis = new IntPeriod(i3,i4);

  final IntPeriod p25    = new IntPeriod(i2,i5);
  final IntPeriod p35    = new IntPeriod(i3,i5);
  final IntPeriod p45    = new IntPeriod(i4,i5);
  final IntPeriod p46    = new IntPeriod(i4,i6);
  final IntPeriod p46bis = new IntPeriod(i4,i6);
  final IntPeriod p48    = new IntPeriod(i4,i8);
  final IntPeriod p56    = new IntPeriod(i5,i6);
  final IntPeriod p78    = new IntPeriod(i7,i8);
  
  final IntPeriod p18	 = new IntPeriod(i1,i8);
  final IntPeriod p68	 = new IntPeriod(i6,i8);
  final IntPeriod p89    = new IntPeriod(i8,i9);
  
  final IntPeriod ALWAYS = new IntPeriod(IntInstant.NEGATIVE_INFINITY, IntInstant.POSITIVE_INFINITY);


  final String  s1    = "one";
  final String  s2    = "two";
  final String  s3    = "three";
  final String  s4    = "four";
  final String  s5    = "five";
  final String  s6    = "six";
  final String  s7    = "seven";
  final String  s1bis = new String(s1);
  final String  s1ind = s1;

}