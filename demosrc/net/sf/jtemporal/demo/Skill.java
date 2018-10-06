
package net.sf.jtemporal.demo;

public class Skill {
	public static final Skill DEVELOP	= new Skill("Develop");
	public static final Skill MANAGE	= new Skill("Manage");
	public static final Skill COUNT		= new Skill("Count");
	public static final Skill SING		= new Skill("Sing");
	public static final Skill DANCE		= new Skill("Dance");

	private final String name;
	
	private Skill(String name) {
		this.name = name;
	}
	
	public String toString() {
		return this.name; 
	}

}
