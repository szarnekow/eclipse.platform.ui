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

package org.eclipse.core.databinding.property.map;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.ObservableTracker;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.map.AbstractObservableMap;
import org.eclipse.core.databinding.observable.map.MapDiff;
import org.eclipse.core.databinding.property.IProperty;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.IPropertyObservable;

/**
 * @since 1.2
 */
class SimpleMapPropertyObservableMap extends AbstractObservableMap implements
		IPropertyObservable {
	private Object source;
	private SimpleMapProperty property;

	private volatile boolean updating = false;

	private volatile int modCount = 0;

	private INativePropertyListener listener;

	private Map cachedMap;

	/**
	 * @param realm
	 * @param source
	 * @param property
	 */
	public SimpleMapPropertyObservableMap(Realm realm, Object source,
			SimpleMapProperty property) {
		super(realm);
		this.source = source;
		this.property = property;
	}

	private void getterCalled() {
		ObservableTracker.getterCalled(this);
	}

	protected void firstListenerAdded() {
		if (!isDisposed()) {
			cachedMap = property.getMap(source);

			if (listener == null) {
				listener = property
						.adaptListener(new IMapPropertyChangeListener() {
							public void handleMapPropertyChange(
									final MapPropertyChangeEvent event) {
								modCount++;
								if (!isDisposed() && !updating) {
									getRealm().exec(new Runnable() {
										public void run() {
											Map oldMap = cachedMap;
											Map newMap = cachedMap = property
													.getMap(source);
											MapDiff diff = event.diff;
											if (diff == null) {
												diff = Diffs.computeMapDiff(
														oldMap, newMap);
											}
											fireMapChange(diff);
										}
									});
								}
							}
						});
			}
			property.addListener(source, listener);
		}
	}

	protected void lastListenerRemoved() {
		if (listener != null) {
			property.removeListener(source, listener);
		}

		cachedMap = null;
	}

	public boolean containsKey(Object key) {
		getterCalled();
		return property.containsKey(source, key);
	}

	public boolean containsValue(Object value) {
		getterCalled();
		return property.containsValue(source, value);
	}

	public Set entrySet() {
		getterCalled();
		// unmodifiable for now
		return Collections.unmodifiableSet(property.getMap(source).entrySet());
	}

	public Object get(Object key) {
		getterCalled();
		return property.get(source, key);
	}

	public boolean isEmpty() {
		getterCalled();
		return property.isEmpty(source);
	}

	public Set keySet() {
		getterCalled();
		return Collections.unmodifiableSet(property.getMap(source).keySet());
	}

	public Object put(Object key, Object value) {
		checkRealm();

		boolean add;
		Object oldValue;

		boolean wasUpdating = updating;
		updating = true;
		try {
			add = !property.containsKey(source, key);
			oldValue = property.put(source, key, value);
			modCount++;
		} finally {
			updating = wasUpdating;
		}

		cachedMap = property.getMap(source);
		MapDiff diff;
		if (add)
			diff = Diffs.createMapDiffSingleAdd(key, value);
		else
			diff = Diffs.createMapDiffSingleChange(key, oldValue, value);
		fireMapChange(diff);

		return property.put(source, key, value);
	}

	public void putAll(Map m) {
		checkRealm();

		Map oldValues = cachedMap;
		Set changedKeys = new HashSet();
		Set addedKeys = new HashSet();
		for (Iterator it = m.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Entry) it.next();
			Object key = entry.getKey();
			if (property.containsKey(source, key)) {
				changedKeys.add(key);
			} else {
				addedKeys.add(key);
			}
		}

		boolean wasUpdating = updating;
		updating = true;
		try {
			property.putAll(source, m);
			modCount++;
		} finally {
			updating = wasUpdating;
		}

		Map newValues = cachedMap = property.getMap(source);
		fireMapChange(Diffs.createMapDiff(addedKeys, Collections.EMPTY_SET,
				changedKeys, oldValues, newValues));
	}

	public Object remove(Object key) {
		checkRealm();

		if (!property.containsKey(source, key))
			return null;

		Object oldValue;

		boolean wasUpdating = updating;
		updating = true;
		try {
			oldValue = property.remove(source, key);
			modCount++;
		} finally {
			updating = wasUpdating;
		}

		cachedMap = property.getMap(source);
		fireMapChange(Diffs.createMapDiffSingleRemove(key, oldValue));

		return oldValue;
	}

	public int size() {
		getterCalled();
		return property.size(source);
	}

	public Collection values() {
		getterCalled();
		return Collections.unmodifiableCollection(property.getMap(source)
				.values());
	}

	public void clear() {
		getterCalled();
		property.clear(source);
	}

	public boolean equals(Object o) {
		getterCalled();
		return property.equals(source, o);
	}

	public int hashCode() {
		getterCalled();
		return property.hashCode(source);
	}

	public Object getObserved() {
		return source;
	}

	public IProperty getProperty() {
		return property;
	}

	public synchronized void dispose() {
		if (!isDisposed()) {
			if (listener != null)
				property.removeListener(source, listener);
			property = null;
			source = null;
			listener = null;
		}
		super.dispose();
	}
}
