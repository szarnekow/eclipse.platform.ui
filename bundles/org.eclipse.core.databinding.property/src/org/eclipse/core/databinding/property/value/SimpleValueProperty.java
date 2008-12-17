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

package org.eclipse.core.databinding.property.value;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.masterdetail.MasterDetailObservables;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.INativePropertyListener;

/**
 * Simplified abstract implementation of IValueProperty. This class takes care
 * of most of the functional requirements for an IValueProperty implementation,
 * leaving only the property-specific details to subclasses.
 * <p>
 * Subclasses must implement these methods:
 * <ul>
 * <li> {@link #getValue(Object)}
 * <li> {@link #setValue(Object, Object)}
 * <li> {@link #adaptListener(IValuePropertyChangeListener)}
 * <li> {@link #addListener(Object, INativePropertyListener)}
 * <li> {@link #removeListener(Object, INativePropertyListener)}
 * <li> {@link #toString()}
 * </ul>
 * 
 * @since 1.2
 */
public abstract class SimpleValueProperty extends ValueProperty {
	private final Object valueType;

	protected SimpleValueProperty(Object valueType) {
		this.valueType = valueType;
	}

	/**
	 * Returns the value type of the property, or <code>null</code> if untyped.
	 * 
	 * @return the value type of the property, or <code>null</code> if untyped.
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected final Object getValueType() {
		return valueType;
	}

	/**
	 * Returns the source's value property
	 * 
	 * @param source
	 *            the property source
	 * @return the current value of the source's value property
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected abstract Object getValue(Object source);

	/**
	 * Sets the source's value property to the specified value
	 * 
	 * @param source
	 *            the property source
	 * @param value
	 *            the new value
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected abstract void setValue(Object source, Object value);

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
			IValuePropertyChangeListener listener);

	/**
	 * Adds the specified listener as a listener for this property on the
	 * specified property source. If the source object has no listener API for
	 * this property (i.e. {@link #adaptListener(IValuePropertyChangeListener)}
	 * returns null), this method does nothing.
	 * 
	 * @param source
	 *            the property source
	 * @param listener
	 *            a listener obtained from calling
	 *            {@link #adaptListener(IValuePropertyChangeListener)}.
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected abstract void addListener(Object source,
			INativePropertyListener listener);

	/**
	 * Removes the specified listener as a listener for this property on the
	 * specified property source. If the source object has no listener API for
	 * this property (i.e. {@link #adaptListener(IValuePropertyChangeListener)}
	 * returns null), this method does nothing.
	 * 
	 * @param source
	 *            the property source
	 * @param listener
	 *            a listener obtained from calling
	 *            {@link #adaptListener(IValuePropertyChangeListener)}.
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected abstract void removeListener(Object source,
			INativePropertyListener listener);

	public IObservableValue observeValue(Realm realm, Object source) {
		return new SimpleValuePropertyObservableValue(realm, source, this);
	}

	public IObservableValue observeDetailValue(IObservableValue master) {
		final Realm realm = master.getRealm();
		IObservableFactory factory = new IObservableFactory() {
			public IObservable createObservable(Object target) {
				return SimpleValueProperty.this.observeValue(realm, target);
			}
		};
		return MasterDetailObservables.detailValue(master, factory, valueType);
	}

	public IObservableList observeDetailValues(IObservableList master) {
		return new ObservableListSimpleValuePropertyObservableList(master, this);
	}

	public IObservableMap observeDetailValues(IObservableSet master) {
		return new ObservableSetSimpleValuePropertyObservableMap(master, this);
	}

	public IObservableMap observeDetailValues(IObservableMap master) {
		return new ObservableMapSimpleValuePropertyObservableMap(master, this);
	}
}