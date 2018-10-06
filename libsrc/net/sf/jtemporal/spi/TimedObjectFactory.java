package net.sf.jtemporal.spi;

import net.sf.jtemporal.Period;
import net.sf.jtemporal.TimedObject;

/**
 * Creates and destroys a TimedObject, used typically by
 * {@linkplain ORMTemporalAttributeStorage} to ask
 * to the ORM tool to add or remove a row to the database.
 * @author Thomas Beck
 * @param <V> the value type held by the {@linkplain TimedObject}
 */
public interface TimedObjectFactory<V> {
	TimedObject<V> create(Period p, V value);
	void destroy(TimedObject<V> to);
}
