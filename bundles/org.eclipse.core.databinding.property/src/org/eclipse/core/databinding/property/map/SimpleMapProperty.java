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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.MapDiff;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.masterdetail.MasterDetailObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.internal.databinding.Util;

/**
 * Simplified abstract implementation of IMapProperty. This class takes care of
 * most of the functional requirements for an IMapProperty implementation,
 * leaving only the property-specific details to subclasses.
 * <p>
 * Subclasses must implement these methods:
 * <ul>
 * <li>{@link #getKeyType()}
 * <li>{@link #getValueType()}
 * </ul>
 * 
 * @since 1.2
 */
public abstract class SimpleMapProperty extends MapProperty implements
		IMapProperty {
	private final Object keyType;
	private final Object valueType;

	protected SimpleMapProperty(Object keyType, Object valueType) {
		this.keyType = keyType;
		this.valueType = valueType;
	}

	/**
	 * Returns the element type of the map's key set or <code>null</code> if the
	 * key set is untyped.
	 * 
	 * @return the element type of the map's key set or <code>null</code> if the
	 *         key set is untyped.
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected final Object getKeyType() {
		return keyType;
	}

	/**
	 * Returns the element type of the map's values collection or
	 * <code>null</code> if the collection is untyped.
	 * 
	 * @return the element type of the map's values collection or
	 *         <code>null</code> if the collection is untyped.
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected final Object getValueType() {
		return valueType;
	}

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

	/**
	 * Returns whether the specified key is contained in the key set of the
	 * source's map property
	 * 
	 * @param source
	 *            the property source
	 * @param key
	 *            the key
	 * @return whether the specified key is contained in the key set of the
	 *         source's map property
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected boolean containsKey(Object source, Object key) {
		return getMap(source).containsKey(key);
	}

	/**
	 * Returns whether the specified value is contains in the values collection
	 * of the source's map property
	 * 
	 * @param source
	 *            the property source
	 * @param value
	 *            the value
	 * @return whether the specified value is contains in the values collection
	 *         of the source's map property
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected boolean containsValue(Object source, Object value) {
		return getMap(source).containsValue(value);
	}

	/**
	 * Returns whether the source's map property is equal to the argument
	 * 
	 * @param source
	 *            the property source
	 * @param o
	 *            the object to test for equality
	 * @return whether the source's map property is equal to the argument
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected boolean equals(Object source, Object o) {
		return getMap(source).equals(o);
	}

	/**
	 * Returns the value associated with the specified key in the source's map
	 * property
	 * 
	 * @param source
	 *            the property source
	 * @param key
	 *            the key
	 * @return the value associated with the specified key in the source's map
	 *         property
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected Object get(Object source, Object key) {
		return getMap(source).get(key);
	}

	/**
	 * Returns the hash code of the source's map property
	 * 
	 * @param source
	 *            the property source
	 * @return the hash code of the source's map property
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected int hashCode(Object source) {
		return getMap(source).hashCode();
	}

	/**
	 * Returns whether the source's map property is empty
	 * 
	 * @param source
	 *            the property source
	 * @return whether the source's map property is empty
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected boolean isEmpty(Object source) {
		return getMap(source).isEmpty();
	}

	/**
	 * Returns the size of the source's map property
	 * 
	 * @param source
	 *            the property source
	 * @return the size of the source's map property
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected int size(Object source) {
		return getMap(source).size();
	}

	/**
	 * Updates the property on the source with the specified change.
	 * 
	 * @param source
	 *            the property source
	 * @param map
	 *            the new map
	 * @param diff
	 *            a diff describing the change
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected abstract void setMap(Object source, Map map, MapDiff diff);

	/**
	 * Removes all mapping from the source's map property
	 * 
	 * @param source
	 *            the property source
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected void clear(Object source) {
		if (!isEmpty(source)) {
			setMap(source, new HashMap(), Diffs
					.createMapDiffRemoveAll(new HashMap(getMap(source))));
		}
	}

	/**
	 * Associates the specified value with the specified key in the source's map
	 * property
	 * 
	 * @param source
	 *            the property source
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @return the value that was previously associated with the given key in
	 *         the source's map property
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected Object put(Object source, Object key, Object value) {
		Map map = new HashMap(getMap(source));
		boolean addition = !map.containsKey(key);
		Object result = map.put(key, value);
		MapDiff diff;
		if (addition) {
			diff = Diffs.createMapDiffSingleAdd(key, value);
		} else {
			diff = Diffs.createMapDiffSingleChange(key, result, value);
		}
		setMap(source, map, diff);
		return result;
	}

	/**
	 * Adds all mappings in the specified map to the source's map property.
	 * 
	 * @param source
	 *            the property source
	 * @param t
	 *            the map
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected void putAll(Object source, Map t) {
		if (t.isEmpty())
			return;

		Map map = new HashMap(getMap(source));
		Set addedKeys = new HashSet();
		Set changedKeys = new HashSet();
		Map oldValues = new HashMap();
		Map newValues = new HashMap();
		for (Iterator it = t.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			Object key = entry.getKey();
			Object newValue = entry.getValue();
			boolean addition = !map.containsKey(key);
			Object oldValue = map.put(key, newValue);
			if (addition) {
				addedKeys.add(key);
			} else if (!Util.equals(oldValue, newValue)) {
				changedKeys.add(key);
				oldValues.put(key, oldValue);
			}
			newValues.put(key, newValue);
		}
		setMap(source, map, Diffs.createMapDiff(addedKeys,
				Collections.EMPTY_SET, changedKeys, oldValues, newValues));
	}

	/**
	 * Removes the mapping for the specified key from the source's map property
	 * 
	 * @param source
	 *            the property source
	 * @param key
	 *            the key
	 * @return the value that was previously associated with the specified key
	 *         in the source's map property, or null if no such mapping exists
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected Object remove(Object source, Object key) {
		Map map = getMap(source);
		if (map.containsKey(key)) {
			map = new HashMap(map);
			Object result = map.remove(key);
			setMap(source, map, Diffs.createMapDiffSingleRemove(key, result));
			return result;
		}
		return null;
	}

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
}
