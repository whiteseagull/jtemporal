/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with NO WARRANTIES given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;


import net.sf.jtemporal.util.LightLRUCache;

/**
 * You must implement this interface (basically a Comparable) to
 * represent time. <BR>
 * You can write a little adapter wrapping java.util.Date.  Some users 
 * prefer instead using "pure" dates, to avoid issues with timezones and
 * avoid performances losses with milliseconds conversions. 
 * There are many open source implementations of Julian and Gregorian date.
 * <B>Any object implementing this interface is required to be immutable.  This
 * is part of the contract.</B><BR>
 * Do not forget to implement equals and hashCode properly, as defined in the
 * {@link java.lang.Object} documentation: this is necessary.
 * Also, the equals method must be consistent with the compareTo method. <p>
 * Please note that the performances of the whole JTemporal heavily depend on 
 * the performance of the methods implementing this interface.  
 * Therefore, you should try to avoid instantiating new objects during 
 * these method calls (and also the methods equals and hashcode).
 * <p> Given that Instants are immutable, it usually makes sense to share
 * the implementing instances: there are never millions of different dates
 * in a system.  This can be done easily using for example a 
 * {@linkplain LightLRUCache}
 * @stereotype immutable
 * @author Thomas A Beck
 * @version $Id: Instant.java,v 1.13 2008/12/23 17:44:37 tabeck Exp $
 */
@SuppressWarnings("unchecked")
public interface Instant extends Comparable, IsTime
{
   // There have been discussions on the appropriateness of having
   // a type parameter in this class.  We initially tried it.
   // It turned out that although useful in some cases, in most cases
   // the client code becomes too heavy.  For example declaring 
   // a TemporalAttribute<I,K>, forces you all the time to specify
   // I, the subtype of Instant that you are using, although usually
   // you use the same everywhere.  When getting some instants back
   // out from JTemporal, for example from a TemporalAttribute, it happens
   // that either:
   // 1- You pass this instant to JTemporal again, so no need for casting
   // 2- You really want to call some method on your specific subclass
   // of instant.  Here you cast, but usually the amount of such places
   // is quite limited, much more limited that the amount of places
   // where you declare a TemporalAttribute
	
	
  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.<p>
   *
   * The implementor must ensure <tt>sgn(x.compareTo(y)) ==
   * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
   * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
   * <tt>y.compareTo(x)</tt> throws an exception.)<p>
   *
   * The implementor must also ensure that the relation is transitive:
   * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
   * <tt>x.compareTo(z)&gt;0</tt>.<p>
   *
   * Finally, the implementer must ensure that <tt>x.compareTo(y)==0</tt>
   * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
   * all <tt>z</tt>.<p>
   *
   * <B>It is required that <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.</B>
   *
   * @param   i the other instant to be compared to.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException if the specified object's type prevents it
   *         from being compared to this Object.
   */
   public int compareTo(Object i);

  
  /**
   * Tells whether this Instant is the positive infinity.    
   * <p>The properties of the positive infinity are: <BR><tt>
   * positiveInfinity.compareTo(positiveInfinity) == 0  <BR>
   * positiveInfinity.equals(positiveInfinity) == true  <BR>
   * positiveInfinity.compareTo(anyOtherInstant) > 0  <BR></tt>
   * @return <tt>true</tt> if this instance is the positive infinity
   */
  boolean isPositiveInfinity();
  

  /**
   * Tells whether this Instant is the negative infinity.    
   * <p>The properties of the negative infinity are: <BR><tt>
   * negativeInfinity.compareTo(negativeInfinity) == 0  <BR>
   * negativeInfinity.equals(negativeInfinity) == true  <BR>
   * negativeInfinity.compareTo(anyOtherInstant) < 0  <BR></tt>
   * @return <tt>true</tt> if this instance is the positive infinity
   */
  boolean isNegativeInfinity();

}
