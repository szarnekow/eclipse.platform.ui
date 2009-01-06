/*******************************************************************************
 * Copyright (c) 2008 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 *     Matthew Hall - bug 195222
 ******************************************************************************/

package org.eclipse.core.databinding.property.value;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.masterdetail.MasterDetailObservables;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.IPropertyChangeListener;

/**
 * Simplified abstract implementation of IValueProperty. This class takes care
 * of most of the functional requirements for an IValueProperty implementation,
 * leaving only the property-specific details to subclasses.
 * <p>
 * Subclasses must implement these methods:
 * <ul>
 * <li> {@link #getValueType()}
 * <li> {@link #doGetValue(Object)}
 * <li> {@link #doSetValue(Object, Object)}
 * <li> {@link #adaptListener(IPropertyChangeListener)}
 * <li> {@link #doAddListener(Object, INativePropertyListener)}
 * <li> {@link #doRemoveListener(Object, INativePropertyListener)}
 * </ul>
 * <p>
 * In addition, we recommended overriding {@link #toString()} to return a
 * description suitable for debugging purposes.
 * 
 * @since 1.2
 */
public abstract class SimpleValueProperty extends ValueProperty {
	/**
	 * Returns the value of the property on the specified source object
	 * 
	 * @param source
	 *            the property source (may be null)
	 * @return the current value of the source's value property
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected final Object getValue(Object source) {
		return source == null ? null : doGetValue(source);
	}

	/**
	 * Returns the value of the property on the specified source object
	 * 
	 * @param source
	 *            the property source
	 * @return the current value of the source's value property
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected abstract Object doGetValue(Object source);

	/**
	 * Sets the source's value property to the specified value
	 * 
	 * @param source
	 *            the property source
	 * @param value
	 *            the new value
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected final void setValue(Object source, Object value) {
		if (source != null)
			doSetValue(source, value);
	}

	protected abstract void doSetValue(Object source, Object value);

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
	 */
	protected abstract INativePropertyListener adaptListener(
			IPropertyChangeListener listener);

	/**
	 * Adds the specified listener as a listener for this property on the
	 * specified property source. If the source object has no listener API for
	 * this property (i.e. {@link #adaptListener(IPropertyChangeListener)}
	 * returns null), this method does nothing.
	 * 
	 * @param source
	 *            the property source
	 * @param listener
	 *            a listener obtained from calling
	 *            {@link #adaptListener(IPropertyChangeListener)}.
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected final void addListener(Object source,
			INativePropertyListener listener) {
		if (source != null)
			doAddListener(source, listener);
	}

	/**
	 * Adds the specified listener as a listener for this property on the
	 * specified property source. If the source object has no listener API for
	 * this property (i.e. {@link #adaptListener(IPropertyChangeListener)}
	 * returns null), this method does nothing.
	 * 
	 * @param source
	 *            the property source
	 * @param listener
	 *            a listener obtained from calling
	 *            {@link #adaptListener(IPropertyChangeListener)}.
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected abstract void doAddListener(Object source,
			INativePropertyListener listener);

	/**
	 * Removes the specified listener as a listener for this property on the
	 * specified property source. If the source object has no listener API for
	 * this property (i.e. {@link #adaptListener(IPropertyChangeListener)}
	 * returns null), this method does nothing.
	 * 
	 * @param source
	 *            the property source
	 * @param listener
	 *            a listener obtained from calling
	 *            {@link #adaptListener(IPropertyChangeListener)}.
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected final void removeListener(Object source,
			INativePropertyListener listener) {
		if (source != null)
			doRemoveListener(source, listener);
	}

	/**
	 * Removes the specified listener as a listener for this property on the
	 * specified property source. If the source object has no listener API for
	 * this property (i.e. {@link #adaptListener(IPropertyChangeListener)}
	 * returns null), this method does nothing.
	 * 
	 * @param source
	 *            the property source
	 * @param listener
	 *            a listener obtained from calling
	 *            {@link #adaptListener(IPropertyChangeListener)}.
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected abstract void doRemoveListener(Object source,
			INativePropertyListener listener);

	public IObservableValue observe(Realm realm, Object source) {
		return new SimpleValuePropertyObservableValue(realm, source, this);
	}

	public IObservableValue observeDetail(IObservableValue master) {
		return MasterDetailObservables.detailValue(master, valueFactory(master
				.getRealm()), getValueType());
	}

	public IObservableList observeDetail(IObservableList master) {
		return new ObservableListSimpleValuePropertyObservableList(master, this);
	}

	public IObservableMap observeDetail(IObservableSet master) {
		return new ObservableSetSimpleValuePropertyObservableMap(master, this);
	}

	public IObservableMap observeDetail(IObservableMap master) {
		return new ObservableMapSimpleValuePropertyObservableMap(master, this);
	}
}