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

package org.eclipse.core.databinding.property.map;

import java.util.Collections;
import java.util.Map;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.MapDiff;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.masterdetail.MasterDetailObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.INativePropertyListener;

/**
 * Simplified abstract implementation of IMapProperty. This class takes care of
 * most of the functional requirements for an IMapProperty implementation,
 * leaving only the property-specific details to subclasses.
 * <p>
 * Subclasses must implement these methods:
 * <ul>
 * <li> {@link #doGetMap(Object)}
 * <li> {@link #setMap(Object, Map, MapDiff)}
 * <li> {@link #adaptListener(IMapPropertyChangeListener)}
 * <li> {@link #addListener(Object, INativePropertyListener)}
 * <li> {@link #removeListener(Object, INativePropertyListener)}
 * </ul>
 * <p>
 * In addition, we recommended overriding {@link #toString()} to return a
 * description suitable for debugging purposes.
 * 
 * @since 1.2
 */
public abstract class SimpleMapProperty extends MapProperty implements
		IMapProperty {
	public IObservableMap observeMap(Realm realm, Object source) {
		return new SimpleMapPropertyObservableMap(realm, source, this);
	}

	public IObservableMap observeDetailMap(IObservableValue master) {
		final Realm realm = master.getRealm();
		IObservableFactory factory = new IObservableFactory() {
			public IObservable createObservable(Object target) {
				return SimpleMapProperty.this.observeMap(realm, target);
			}
		};
		return MasterDetailObservables.detailMap(master, factory);
	}

	/**
	 * Returns the element type of the map's key set or <code>null</code> if the
	 * key set is untyped.
	 * 
	 * @return the element type of the map's key set or <code>null</code> if the
	 *         key set is untyped.
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected abstract Object getKeyType();

	/**
	 * Returns the element type of the map's values collection or
	 * <code>null</code> if the collection is untyped.
	 * 
	 * @return the element type of the map's values collection or
	 *         <code>null</code> if the collection is untyped.
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected abstract Object getValueType();

	// Accessors

	/**
	 * Returns an unmodifiable Map with the current contents of the source's map
	 * property.
	 * 
	 * @param source
	 *            the property source
	 * @return a Map with the current contents of the source's map property
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected final Map getMap(Object source) {
		return Collections.unmodifiableMap(doGetMap(source));
	}

	/**
	 * Returns a Map with the current contents of the source's map property
	 * 
	 * @param source
	 *            the property source
	 * @return a Map with the current contents of the source's map property
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected abstract Map doGetMap(Object source);

	// Mutators

	/**
	 * Updates the property on the source with the specified change.
	 * 
	 * @param source
	 *            the property source
	 * @param map
	 *            the new map
	 * @param diff
	 *            a diff describing the change
	 * @return true if the property was modified on the source object, false
	 *         otherwise
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected abstract boolean setMap(Object source, Map map, MapDiff diff);

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
	 */
	protected abstract INativePropertyListener adaptListener(
			IMapPropertyChangeListener listener);

	/**
	 * Adds the specified listener as a listener for this property on the
	 * specified property source. If the source object has no listener API for
	 * this property (i.e. {@link #adaptListener(IMapPropertyChangeListener)}
	 * returns null), this method does nothing.
	 * 
	 * @param source
	 *            the property source
	 * @param listener
	 *            a listener obtained from calling
	 *            {@link #adaptListener(IMapPropertyChangeListener)} .
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected abstract void addListener(Object source,
			INativePropertyListener listener);

	/**
	 * Removes the specified listener as a listener for this property on the
	 * specified property source. If the source object has no listener API for
	 * this property (i.e. {@link #adaptListener(IMapPropertyChangeListener)}
	 * returns null), this method does nothing.
	 * 
	 * @param source
	 *            the property source
	 * @param listener
	 *            a listener obtained from calling
	 *            {@link #adaptListener(IMapPropertyChangeListener)} .
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected abstract void removeListener(Object source,
			INativePropertyListener listener);
}
