/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with NO WARRANTIES given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;

import java.util.*;

/**
 * A TimeNode holds a State
 * which can be a SingleElementState or a HashState
 * (state pattern).
 * The goal of the of a "lazy collection" is to delay the instatiation of the
 * heavy part (HashMap) until a second element is added to the collection.  That
 * increases performances in a system where many collection contains only one element.
 *
 * A SingleElementState contains one and only one TimedObject,
 *
 * A HashState contains 1 or more TimedObjects.
 *
 * As soon a Node is empty, it asks the parent to be removed and is then garbaged.
 *
 * @deprecated
 * @author Thomas A Beck
 * @version $Id: TimeNode.java,v 1.3 2009/01/02 17:05:03 tabeck Exp $
 */
class TimeNode<V> {

	private static final State dead = new DeadState();
	private static final String NODE_DEAD_MSG = "Node Dead.";
	private static final String BAD_INSTANT_MSG =
		"A non equal endInstant is defined for this TimeNode."
		+" Is the equals method of your Instant implementation correct?";
    private State<V> state = dead;
	private final Instant endInstant;
    private final NodeParent<V> parent; // listener

    public Instant getEndInstant() {
        return endInstant;
	}

	/**
	 * @param parent is the object (usually the caller) which must be notified
	 * when a node dies.
	 * @param is the endInstant common to all the elements in the state.
	 */
    public TimeNode(NodeParent<V> parent, TimedObject<V> firstElement) {
        endInstant = firstElement.getPeriod().getEnd();
		this.parent = parent;
		this.state = new TimeNode.SingleElementState(firstElement);
	}

	private void die() {
		TimeNode.this.state = dead;
		// speed up garbage collection and avoid accidental recycling
		parent.nodeDead(TimeNode.this); // notify
	}

	// forwarded methods //

	/**
	 * Add an element to this TimeNode.
	 */
    public void add(TimedObject<V> to) {
        state.add(to);
	}

	/**
	 * Remove an element to this TimeNode.
	 */
    public void remove(TimedObject<V> to) {
        state.remove(to);
	}

	/**
	 * Iterated through the elements of this TimeNode.
	 */
    public Iterator<TimedObject<V>> iterator() {
        return state.iterator();
	}

	/**
	 * The number of elements in this TimeNode.
	 */
	public int size() {
        return state.size();
	}

    @Override
	public String toString() {
		StringBuffer s = new StringBuffer(30);
        s.append("TimeNode:endInstant=").append(endInstant).append("[");

        TimedObject<V> to;
		int firstLoop = 1;
        for (Iterator<TimedObject<V>> i = iterator(); i.hasNext(); firstLoop = 0) {
			if (firstLoop == 0) {
				s.append(",");
			}

          to = i.next();
			s.append(to.getValue().toString());
		}

		s.append("]");
		return s.toString();
	}

	/**
	 * private interface
	 * role pattern cannot be applied because a singleElementState must be able
	 * to request his own replacement by a HashState
	 */
    private interface State<U> {
        void add(TimedObject<U> to);
        void remove(TimedObject<U> to);
        Iterator<TimedObject<U>> iterator();
		int size();
	}

	/**
	 * Immutable, single instance.
	 */
    private static class DeadState<U> implements State<U> {
        public void add(TimedObject<U> to) {
			throw new IllegalStateException(NODE_DEAD_MSG);
		}

        public void remove(TimedObject<U> to) {
			throw new IllegalStateException(NODE_DEAD_MSG);
		}

        public Iterator<TimedObject<U>> iterator() {
			throw new IllegalStateException(NODE_DEAD_MSG);
		}

		public int size() {
			throw new IllegalStateException(NODE_DEAD_MSG);
		}
	}

	/**
	 * Because statistically more than 50% of states contain a single element, it
	 * is convenient in terms of performance and memory usage, to first try
	 * to instantiate a SingleElementState and avoid to instantiate
	 * an expensive HashMap.
	 * @author Thomas Beck
	 * @version $Id: TimeNode.java,v 1.3 2009/01/02 17:05:03 tabeck Exp $
	 */
    class SingleElementState implements State<V> {

        private final TimedObject<V> element;

        public SingleElementState(TimedObject<V> element) {
			this.element = element;
		}

        public void add(TimedObject<V> newElement) {
			//pre-condition
			if (!newElement.getPeriod().getEnd() .equals(TimeNode.this.endInstant)) {
				throw new IllegalArgumentException(BAD_INSTANT_MSG);
			}

			// @todo what if already exists ?
            State<V> b = new HashState(element);
			b.add(newElement);
            state = b;
		}

        public void remove(TimedObject<V> to) {
            if (!element.equals(to)) {
				throw new NoSuchElementException();
			}
			TimeNode.this.die();
		}

        public Iterator<TimedObject<V>> iterator() {
            return new Iterator<TimedObject<V>>() {
				int counter = 0;

				public boolean hasNext() {
					return counter == 0 ? true : false;
				}

                public TimedObject<V> next() {
					if (counter > 0)
						throw new NoSuchElementException();

					counter++;
					return SingleElementState.this.element;
				}

				public void remove() {
					if (counter == 0)
						throw new IllegalStateException();
					TimeNode.this.die();
				}
			};
		} // Iterator()

		public int size() {
			return 1;
		}
	} // SingleElementState

	/**
	 * Contains 1 or more TimedObjects
	 * @author Thomas Beck
	 * @version $Id: TimeNode.java,v 1.3 2009/01/02 17:05:03 tabeck Exp $
	 */
    public class HashState implements State<V> {

        private final Set<TimedObject<V>> set = new HashSet<TimedObject<V>>();

        public HashState(TimedObject<V> element) {
			set.add(element);
		}

        public void add(TimedObject<V> to) {
			//pre-condition
			if (!to.getPeriod().getEnd().equals(TimeNode.this.endInstant))
				throw new IllegalArgumentException(BAD_INSTANT_MSG);

			if (!set.add(to)) {
				throw new IllegalStateException();
			}
		}

        public void remove(TimedObject<V> to) {
			if (!set.remove(to)) {
				throw new NoSuchElementException();
			}
			if (set.isEmpty()) {
				TimeNode.this.die();
			}
		}

        public Iterator<TimedObject<V>> iterator() {
            return new Iterator<TimedObject<V>>() {
                Iterator<TimedObject<V>> it = set.iterator();

				public boolean hasNext() {
					return it.hasNext();
				}

                public TimedObject<V> next() {
					return it.next();
				}
				public void remove() {
					it.remove();
					if (set.isEmpty()) {
						TimeNode.this.die();
					}
				}
			};
		}

		public int size() {
			return set.size();
		}
	} // HashState
}
