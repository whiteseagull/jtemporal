/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2004 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.demo;

import net.sf.jtemporal.*;

/**
 * @author Thomas A Beck
 * @version $Id: CompanyDemo.java,v 1.4 2007/09/14 20:03:12 tabeck Exp $
 */
@SuppressWarnings("unchecked") // because it is handy to not always specify Period
public class CompanyDemo {

	public static void main(String[] args) {
		CompanyDemo cd = new CompanyDemo();
		cd.init();
		cd.update();
		cd.printStatus();
	}
	
	private final Period ALWAYS 
		= new Period(FinalDate.NEGATIVE_INFINITY, FinalDate.POSITIVE_INFINITY);
	 
	private Employee boss;
	
	// initialize the company
	public void init() {
		boss = new Employee();
		boss.setName(ALWAYS, "John Smith");
		boss.setEmail(new Period(FinalDate.valueOf("1995-01-30"), FinalDate.POSITIVE_INFINITY), "john@smith.net");
		boss.addSkill(ALWAYS, Skill.COUNT);
		boss.addSkill(new Period(FinalDate.valueOf("2003-01-01"), FinalDate.POSITIVE_INFINITY), Skill.MANAGE);
	}
	
	// make some updates
	public void update() {
		// boss changes his e-mail next year
		// this call will remove the previous email mapping and store two new mappings:
		// 1- up to 2005-01-01 : john@smith.net
		// 2- after 2005-01-01 : john@smith.com 
		boss.setEmail(new Period(FinalDate.valueOf("2005-01-01"), FinalDate.POSITIVE_INFINITY), "john@smith.com");

		// boss is getting old, in 2010 he will not have the "manage" skill anymore
		// this call will remove the exists mapping for Skill.MANAGE and replace it by a shorter one
		boss.removeSkill(new Period(FinalDate.valueOf("2010-01-01"), FinalDate.POSITIVE_INFINITY), Skill.MANAGE);
		
		// boss is going in holiday in ClubMed, he will have a different email for a couple of weeks
		Period holidayPeriod = new Period(
			FinalDate.valueOf("2004-07-25"), FinalDate.valueOf("2004-08-10")
		);

		// this call will remove the "john@smith.net" entry and add three entries
		// 1- "john@smith.net" up to "2004-07-25"
		// 2- "john.smith@clubmed.com" from "2004-07-25" to "2004-08-10"
		// 3- "john@smith.net" from "2004-08-10" to "2005-01-01"
		boss.setEmail(holidayPeriod, "john.smith@clubmed.com");				
	}
	
	public void printStatus() {
		System.out.println(boss.toString(FinalDate.valueOf("2004-05-01")));
		System.out.println(boss.toString(FinalDate.valueOf("2004-07-30")));
		System.out.println(boss.toString(FinalDate.valueOf("2004-09-01")));
		System.out.println(boss.toString(FinalDate.valueOf("2015-07-30")));
	}
	
}
