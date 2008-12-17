/*******************************************************************************
 * Copyright (c) 2008 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation
 ******************************************************************************/

package org.eclipse.core.databinding.property.set;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.masterdetail.MasterDetailObservables;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.SetDiff;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.INativePropertyListener;

/**
 * Simplified abstract implementation of ISetProperty. This class takes care of
 * most of the functional requirements for an ISetProperty implementation,
 * leaving only the property-specific details to subclasses.
 * <p>
 * Subclasses must implement these methods:
 * <ul>
 * <li> {@link #getSet(Object)}
 * <li> {@link #setSet(Object, Set, SetDiff)}
 * <li> {@link #adaptListener(ISetPropertyChangeListener)}
 * <li> {@link #addListener(Object, INativePropertyListener)}
 * <li> {@link #removeListener(Object, INativePropertyListener)}
 * <li> {@link #toString()}
 * </ul>
 * 
 * @since 1.2
 */
public abstract class SimpleSetProperty extends SetProperty {
	private final Object elementType;

	protected SimpleSetProperty(Object elementType) {
		this.elementType = elementType;
	}

	/**
	 * Returns the type of the elements in the collection or <code>null</code>
	 * if untyped
	 * 
	 * @return the type of the elements in the collection or <code>null</code>
	 *         if untyped
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected final Object getElementType() {
		return elementType;
	}

	/**
	 * Returns whether the source's collection property contains the given
	 * element.
	 * 
	 * @param source
	 *            the property source
	 * @param o
	 *            the element
	 * @return whether the source's collection property contains the given
	 *         element.
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected boolean contains(Object source, Object o) {
		return getSet(source).contains(o);
	}

	/**
	 * Returns whether the source's collection property contains all elements in
	 * the given collection
	 * 
	 * @param source
	 *            the property source
	 * @param c
	 *            the collection of elements to test for
	 * @return whether the source's collection property contains all elements in
	 *         the given collection
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected boolean containsAll(Object source, Collection c) {
		return getSet(source).containsAll(c);
	}

	/**
	 * Returns a Set with the current contents of the source's set property
	 * 
	 * @param source
	 *            the property source
	 * @return a Set with the current contents of the source's set property
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected final Set getSet(Object source) {
		return Collections.unmodifiableSet(doGetSet(source));
	}

	/**
	 * Returns an unmodifiable Set with the current contents of the source's set
	 * property
	 * 
	 * @param source
	 *            the property source
	 * @return an unmodifiable Set with the current contents of the source's set
	 *         property
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected abstract Set doGetSet(Object source);

	/**
	 * Returns whether the source's collection property is equal to the
	 * argument.
	 * 
	 * @param source
	 *            the property source
	 * @param o
	 *            the object to test for equality to the source's collection
	 *            property
	 * @return whether the source's collection property is equal to the argument
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected boolean equals(Object source, Object o) {
		return getSet(source).equals(o);
	}

	/**
	 * Returns the hash code of the source's collection property.
	 * 
	 * @param source
	 *            the property source
	 * @return the hash code of the source's collection property
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected int hashCode(Object source) {
		return getSet(source).hashCode();
	}

	/**
	 * Returns whether the source's collection property is empty
	 * 
	 * @param source
	 *            the property source
	 * @return whether the source's collection property is empty
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected boolean isEmpty(Object source) {
		return getSet(source).isEmpty();
	}

	/**
	 * Returns the size of the source's collection property
	 * 
	 * @param source
	 *            the property source
	 * @return the size of the source's collection property
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected int size(Object source) {
		return getSet(source).size();
	}

	/**
	 * Returns an array of all elements in the source's collection property
	 * 
	 * @param source
	 *            the property source
	 * @param array
	 *            the array into which the elements will be copied. If the array
	 *            is not large enough to hold all elements, the elements will be
	 *            returned in a new array of the same runtime type.
	 * @return an array of all elements in the source's collection property
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected Object[] toArray(Object source, Object[] array) {
		return getSet(source).toArray(array);
	}

	/**
	 * Returns an array of all elements in the source's collection property
	 * 
	 * @param source
	 *            the property source
	 * @return an array of all elements in the source's collection property
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected Object[] toArray(Object source) {
		return getSet(source).toArray();
	}

	/**
	 * Updates the property on the source with the specified change.
	 * 
	 * @param source
	 *            the property source
	 * @param set
	 *            the new set
	 * @param diff
	 *            a diff describing the change
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected abstract void setSet(Object source, Set set, SetDiff diff);

	/**
	 * Adds the element to the source's collection property
	 * 
	 * @param source
	 *            the property source
	 * @param o
	 *            the element to add
	 * @return whether the element was added to the source's collection property
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected boolean add(Object source, Object o) {
		Set set = getSet(source);
		if (!set.contains(o)) {
			set = new HashSet(set);
			boolean added = set.add(o);
			if (added) {
				setSet(source, set, Diffs.createSetDiff(Collections
						.singleton(o), Collections.EMPTY_SET));
			}
			return added;
		}
		return false;
	}

	/**
	 * Adds all elements in the specified collection to the source's collection
	 * property.
	 * 
	 * @param source
	 *            the property source
	 * @param c
	 *            the collection of elements to add.
	 * @return whether the source's collection property was changed
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected boolean addAll(Object source, Collection c) {
		if (c.isEmpty())
			return false;

		Set set = getSet(source);
		Set additions = new HashSet();
		for (Iterator it = c.iterator(); it.hasNext();) {
			Object o = it.next();
			if (!set.contains(o)) {
				additions.add(o);
			}
		}
		boolean changed = !additions.isEmpty();
		if (changed) {
			set = new HashSet(set);
			set.addAll(additions);

			setSet(source, set, Diffs.createSetDiff(additions,
					Collections.EMPTY_SET));
		}
		return changed;
	}

	/**
	 * Removes all elements from the source's collection property.
	 * 
	 * @param source
	 *            the property source
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected void clear(Object source) {
		if (!isEmpty(source)) {
			setSet(source, new HashSet(), Diffs.createSetDiff(
					Collections.EMPTY_SET, getSet(source)));
		}
	}

	/**
	 * Removes the element from the source's collection property
	 * 
	 * @param source
	 *            the property source
	 * @param o
	 *            the element to remove
	 * @return whether the element was removed from the source's collection
	 *         property
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected boolean remove(Object source, Object o) {
		Set set = getSet(source);
		if (set.contains(o)) {
			set = new HashSet(set);
			boolean removed = set.remove(o);
			if (removed) {
				setSet(source, set, Diffs.createSetDiff(Collections.EMPTY_SET,
						Collections.singleton(o)));
			}
			return removed;
		}
		return false;
	}

	/**
	 * Removes all elements from the source's collection property which are
	 * contained in the specified collection.
	 * 
	 * @param source
	 *            the property source
	 * @param c
	 *            the collection of elements to be removed
	 * @return whether the source's collection property was changed
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected boolean removeAll(Object source, Collection c) {
		if (c.isEmpty())
			return false;

		Set set = new HashSet(getSet(source));
		Set removals = new HashSet();
		for (Iterator it = set.iterator(); it.hasNext();) {
			Object o = it.next();
			if (c.contains(o)) {
				removals.add(o);
				it.remove();
			}
		}
		boolean changed = !removals.isEmpty();
		if (changed) {
			setSet(source, set, Diffs.createSetDiff(Collections.EMPTY_SET,
					removals));
		}
		return changed;
	}

	/**
	 * Removes all elements from the source's collection property which are not
	 * contained in the specified collection.
	 * 
	 * @param source
	 *            the property source
	 * @param c
	 *            the collection of elements to retain
	 * @return whether the source's collection property was changed
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected boolean retainAll(Object source, Collection c) {
		if (isEmpty(source))
			return false;
		if (c.isEmpty()) {
			clear(source);
			return true;
		}

		Set set = new HashSet(getSet(source));
		Set removals = new HashSet();
		for (Iterator it = set.iterator(); it.hasNext();) {
			Object o = it.next();
			if (!c.contains(o)) {
				removals.add(o);
				it.remove();
			}
		}
		boolean changed = !removals.isEmpty();
		if (changed) {
			setSet(source, set, Diffs.createSetDiff(Collections.EMPTY_SET,
					removals));
		}
		return changed;
	}

	/**
	 * Returns a listener which implements the correct listener interface for
	 * the expected source object, and which parlays property change events from
	 * the source object to the given listener. If there is no listener API for
	 * this property, this method returns null.
	 * 
	 * @param listener
	 *            the property listener to receive events
	 * @return a native listener which parlays property change events to the
	 *         specified listener.
	 * @throws ClassCastException
	 *             if the provided listener does not implement the correct
	 *             listener interface (IValueProperty, IListProperty,
	 *             ISetProperty or IMapProperty) depending on the property.
	 * @noreference This method is not intended to be referenced by clients.
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected abstract INativePropertyListener adaptListener(
			ISetPropertyChangeListener listener);

	/**
	 * Adds the specified listener as a listener for this property on the
	 * specified property source. If the source object has no listener API for
	 * this property (i.e. {@link #adaptListener(ISetPropertyChangeListener)}
	 * returns null), this method does nothing.
	 * 
	 * @param source
	 *            the property source
	 * @param listener
	 *            a listener obtained from calling
	 *            {@link #adaptListener(ISetPropertyChangeListener)}.
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected abstract void addListener(Object source,
			INativePropertyListener listener);

	/**
	 * Removes the specified listener as a listener for this property on the
	 * specified property source. If the source object has no listener API for
	 * this property (i.e. {@link #adaptListener(ISetPropertyChangeListener)}
	 * returns null), this method does nothing.
	 * 
	 * @param source
	 *            the property source
	 * @param listener
	 *            a listener obtained from calling
	 *            {@link #adaptListener(ISetPropertyChangeListener)} .
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected abstract void removeListener(Object source,
			INativePropertyListener listener);

	public IObservableSet observeSet(Realm realm, Object source) {
		return new SimpleSetPropertyObservableSet(realm, source, this);
	}

	public IObservableSet observeDetailSet(IObservableValue master) {
		final Realm realm = master.getRealm();
		IObservableFactory factory = new IObservableFactory() {
			public IObservable createObservable(Object target) {
				return SimpleSetProperty.this.observeSet(realm, target);
			}
		};
		return MasterDetailObservables.detailSet(master, factory,
				getElementType());
	}
}
