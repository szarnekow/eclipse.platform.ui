/*******************************************************************************
 * Copyright (c) 2008 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 ******************************************************************************/

package org.eclipse.core.databinding.property.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.masterdetail.MasterDetailObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.INativePropertyListener;

/**
 * Simplified abstract implementation of IListProperty. This class takes care of
 * most of the functional requirements for an IListProperty implementation,
 * leaving only the property-specific details to subclasses.
 * <p>
 * Subclasses must implement these methods:
 * <ul>
 * <li> {@link #doGetList(Object)}
 * <li> {@link #setList(Object, List, ListDiff)}
 * <li> {@link #adaptListener(IListPropertyChangeListener)}
 * <li> {@link #addListener(Object, INativePropertyListener)}
 * <li> {@link #removeListener(Object, INativePropertyListener)}
 * </ul>
 * <p>
 * In addition, we recommended overriding {@link #toString()} to return a
 * description suitable for debugging purposes.
 * 
 * @since 1.2
 */
public abstract class SimpleListProperty extends ListProperty {
	private final Object elementType;

	protected SimpleListProperty(Object elementType) {
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
		return getList(source).containsAll(c);
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
		return getList(source).contains(o);
	}

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
		return getList(source).equals(o);
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
		return getList(source).hashCode();
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
		return getList(source).isEmpty();
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
		return getList(source).size();
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
		return getList(source).toArray(array);
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
		return getList(source).toArray();
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
			IListPropertyChangeListener listener);

	/**
	 * Inserts the element into the source's list property at the specified
	 * position
	 * 
	 * @param source
	 *            the property source
	 * @param index
	 *            the insertion index
	 * @param element
	 *            the element to insert
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected void add(Object source, int index, Object element) {
		List list = new ArrayList(getList(source));
		list.add(index, element);
		setList(source, list, Diffs.createListDiff(Diffs.createListDiffEntry(
				index, true, element)));
	}

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
		add(source, size(source), o);
		return true;
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
		addAll(source, size(source), c);
		return true;
	}

	/**
	 * Inserts all elements in the specified collection into the source's list
	 * property at the specified index.
	 * 
	 * @param source
	 *            the property source
	 * @param index
	 *            the insertion index
	 * @param c
	 *            the collection of elements to add
	 * @return whether the source's list property was changed
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected boolean addAll(Object source, int index, Collection c) {
		if (c.isEmpty()) {
			return false;
		}

		List list = new ArrayList(getList(source));
		List entries = new ArrayList();
		int i = index;
		for (Iterator it = c.iterator(); it.hasNext(); i++) {
			Object o = it.next();
			list.add(i, o);
			entries.add(Diffs.createListDiffEntry(i, true, o));
		}
		boolean changed = !entries.isEmpty();
		if (changed) {
			ListDiffEntry[] ea = (ListDiffEntry[]) entries
					.toArray(new ListDiffEntry[entries.size()]);
			setList(source, list, Diffs.createListDiff(ea));
		}
		return changed;
	}

	/**
	 * Adds the specified listener as a listener for this property on the
	 * specified property source. If the source object has no listener API for
	 * this property (i.e. {@link #adaptListener(IListPropertyChangeListener)}
	 * returns null), this method does nothing.
	 * 
	 * @param source
	 *            the property source
	 * @param listener
	 *            a listener obtained from calling
	 *            {@link #adaptListener(IListPropertyChangeListener)}.
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected abstract void addListener(Object source,
			INativePropertyListener listener);

	/**
	 * Removes all elements from the source's collection property.
	 * 
	 * @param source
	 *            the property source
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected void clear(Object source) {
		if (!isEmpty(source)) {
			List list = getList(source);
			ListDiffEntry[] entries = new ListDiffEntry[list.size()];
			int i = 0;
			for (Iterator it = getList(source).iterator(); it.hasNext(); i++) {
				entries[i] = Diffs.createListDiffEntry(0, false, it.next());
			}
			setList(source, new ArrayList(), Diffs.createListDiff(entries));
		}
	}

	/**
	 * Returns the element at the specified position in the source's list
	 * property
	 * 
	 * @param source
	 *            the property source
	 * @param index
	 *            the element position
	 * @return the element at the given position in the source's list property
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected Object get(Object source, int index) {
		return getList(source).get(index);
	}

	/**
	 * Returns an unmodifiable List with the current contents of the source's
	 * list property
	 * 
	 * @param source
	 *            the property source
	 * @return an unmodifiable List with the current contents of the source's
	 *         list property
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected final List getList(Object source) {
		return Collections.unmodifiableList(doGetList(source));
	}

	/**
	 * Returns a List with the current contents of the source's list property
	 * 
	 * @param source
	 *            the property source
	 * @return a List with the current contents of the source's list property
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected abstract List doGetList(Object source);

	/**
	 * Returns the index of the first location of the given element in the
	 * source's list property, or -1 if the list does not contain the element.
	 * 
	 * @param source
	 *            the property source
	 * @param o
	 *            the element
	 * @return the index of the first location of the given element in the
	 *         source's list property, or -1 if the list does not contain the
	 *         element
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected int indexOf(Object source, Object o) {
		return getList(source).indexOf(o);
	}

	/**
	 * Returns the index of the last location of the given element in the
	 * source's list property, or -1 if the list does not contain the given
	 * element.
	 * 
	 * @param source
	 * @param o
	 * @return the index of the last location of the given element in the
	 *         source's list property, or -1 if the list does not contain the
	 *         element
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected int lastIndexOf(Object source, Object o) {
		return getList(source).lastIndexOf(o);
	}

	/**
	 * Moves the element at the specified old position in the source's list
	 * property to the specified new position
	 * 
	 * @param source
	 *            the property source
	 * @param oldIndex
	 *            the old element position
	 * @param newIndex
	 *            the new element position
	 * @return the element that was moved
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected Object move(Object source, int oldIndex, int newIndex) {
		if (oldIndex == newIndex)
			return get(source, oldIndex);
		List list = new ArrayList(getList(source));
		Object result = list.remove(oldIndex);
		list.add(newIndex, result);
		setList(source, list, Diffs.createListDiff(Diffs.createListDiffEntry(
				oldIndex, false, result), Diffs.createListDiffEntry(newIndex,
				true, result)));
		return result;
	}

	public IObservableList observeList(Realm realm, Object source) {
		return new SimpleListPropertyObservableList(realm, source, this);
	}

	public IObservableList observeDetailList(IObservableValue master) {
		final Realm realm = master.getRealm();
		IObservableFactory factory = new IObservableFactory() {
			public IObservable createObservable(Object target) {
				return SimpleListProperty.this.observeList(realm, target);
			}
		};
		return MasterDetailObservables.detailList(master, factory,
				getElementType());
	}

	/**
	 * Removes the element from the source's list property which is located at
	 * the specified position
	 * 
	 * @param source
	 *            the property source
	 * @param index
	 *            the index of the element to remove
	 * @return the element that was removed from the source's list property
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected Object remove(Object source, int index) {
		List list = new ArrayList(getList(source));
		Object result = list.remove(index);
		setList(source, list, Diffs.createListDiff(Diffs.createListDiffEntry(
				index, false, result)));
		return result;
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
		int i = indexOf(source, o);
		if (i == -1)
			return false;
		remove(source, i);
		return true;
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
		if (isEmpty(source)) {
			return false;
		}
		if (c.isEmpty()) {
			return false;
		}
		List list = new ArrayList(getList(source));
		List entries = new ArrayList();
		for (ListIterator it = list.listIterator(); it.hasNext();) {
			Object o = it.next();
			if (c.contains(o)) {
				entries.add(Diffs.createListDiffEntry(it.previousIndex(),
						false, o));
				it.remove();
			}
		}
		boolean changed = !entries.isEmpty();
		if (changed) {
			ListDiffEntry[] ea = (ListDiffEntry[]) entries
					.toArray(new ListDiffEntry[entries.size()]);
			setList(source, list, Diffs.createListDiff(ea));
		}
		return changed;
	}

	/**
	 * Removes the specified listener as a listener for this property on the
	 * specified property source. If the source object has no listener API for
	 * this property (i.e. {@link #adaptListener(IListPropertyChangeListener)}
	 * returns null), this method does nothing.
	 * 
	 * @param source
	 *            the property source
	 * @param listener
	 *            a listener obtained from calling
	 *            {@link #adaptListener(IListPropertyChangeListener)}.
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected abstract void removeListener(Object source,
			INativePropertyListener listener);

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
		if (isEmpty(source)) {
			return false;
		}
		if (c.isEmpty()) {
			clear(source);
			return true;
		}
		List list = new ArrayList(getList(source));
		List entries = new ArrayList();
		for (ListIterator it = list.listIterator(); it.hasNext();) {
			Object o = it.next();
			if (!c.contains(o)) {
				entries.add(Diffs.createListDiffEntry(it.previousIndex(),
						false, o));
				it.remove();
			}
		}
		boolean changed = !entries.isEmpty();
		if (changed) {
			ListDiffEntry[] ea = (ListDiffEntry[]) entries
					.toArray(new ListDiffEntry[entries.size()]);
			setList(source, list, Diffs.createListDiff(ea));
		}
		return changed;
	}

	/**
	 * Replaces the element at the specified position in the source's list
	 * property with the given element.
	 * 
	 * @param source
	 *            the property source
	 * @param index
	 *            the element position
	 * @param element
	 *            the replacement element
	 * @return the element previously at the specified position in the source's
	 *         list property
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected Object set(Object source, int index, Object element) {
		List list = new ArrayList(getList(source));
		Object result = list.set(index, element);
		setList(source, list, Diffs.createListDiff(Diffs.createListDiffEntry(
				index, false, result), Diffs.createListDiffEntry(index, true,
				element)));
		return result;
	}

	/**
	 * Updates the property on the source with the specified change.
	 * 
	 * @param source
	 *            the property source
	 * @param list
	 *            the new list
	 * @param diff
	 *            a diff describing the change
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected abstract void setList(Object source, List list, ListDiff diff);
}
