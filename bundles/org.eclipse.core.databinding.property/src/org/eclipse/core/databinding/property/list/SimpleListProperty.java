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

import java.util.Collections;
import java.util.List;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListDiff;
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
	 * Returns the type of the elements in the collection or <code>null</code>
	 * if untyped
	 * 
	 * @return the type of the elements in the collection or <code>null</code>
	 *         if untyped
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected abstract Object getElementType();

	// Accessors

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

	// Mutators

	/**
	 * Updates the property on the source with the specified change.
	 * 
	 * @param source
	 *            the property source
	 * @param list
	 *            the new list
	 * @param diff
	 *            a diff describing the change
	 * @return true if the property was modified on the source object, false
	 *         otherwise
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected abstract boolean setList(Object source, List list, ListDiff diff);

	// Listeners

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

}
