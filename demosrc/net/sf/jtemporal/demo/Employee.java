
package net.sf.jtemporal.demo;

import java.util.Set;
import net.sf.jtemporal.*;

/**
 * @author Thomas A Beck
 * @version $Id: Employee.java,v 1.5 2007/09/14 20:03:12 tabeck Exp $
 */
public class Employee {

	private TemporalAttribute<String> name 
		= new TreeTemporalAttribute<String>()
	;
	
	private TemporalAttribute<String> email		
		= new TreeTemporalAttribute<String>()
	;
		
	private TemporalSet<Skill> skills
		= new TreeTemporalSet<Skill>()
	; 

	 
	public String getName(FinalDate d) {
		return (String) name.get(d);
	}

	public void setName(Period p, String s) {
		this.name.put(p, s);
	}
	
	public String getEmail(FinalDate d) {
		return (String) email.get(d);
	}

	public void setEmail(Period p, String s) {
		this.email.put(p, s);
	}
	 
	public Set getSkills(FinalDate d) {
		return this.skills.valueSet(d);		
	}
	
	public void addSkill(Period p, Skill s) {
		this.skills.put(p, s);
	}
	
	public void removeSkill(Period p, Skill s) {
		this.skills.remove(p, s);
	}
	
	public boolean hasSkill(FinalDate d, Skill s) {
		return this.skills.contains(d, s); 
	}

	public String toString(FinalDate d) {
		String ret = "Employee as of " + d + ":"
			+ "\n\tName  : "	+ this.getName(d)
			+ "\n\te-mail: "	+ this.getEmail(d)
			+ "\n\tskills: "	+ this.getSkills(d);
		return ret;
	}
}
