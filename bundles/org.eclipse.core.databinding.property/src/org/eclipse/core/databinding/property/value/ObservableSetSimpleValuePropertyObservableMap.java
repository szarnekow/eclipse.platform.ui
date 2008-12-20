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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.IObserving;
import org.eclipse.core.databinding.observable.map.ComputedObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.ValueDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;

/**
 * @since 1.2
 */
class ObservableSetSimpleValuePropertyObservableMap extends
		ComputedObservableMap implements IObserving {
	private SimpleValueProperty detailProperty;

	private INativePropertyListener listener;

	private Map cachedValues;

	private boolean updating;

	/**
	 * @param keySet
	 * @param valueProperty
	 */
	public ObservableSetSimpleValuePropertyObservableMap(IObservableSet keySet,
			SimpleValueProperty valueProperty) {
		super(keySet);
		this.detailProperty = valueProperty;
	}

	protected void firstListenerAdded() {
		if (listener == null) {
			cachedValues = new HashMap(this);

			listener = detailProperty
					.adaptListener(new IValuePropertyChangeListener() {
						public void handleValuePropertyChange(
								final ValuePropertyChangeEvent event) {
							if (!isDisposed() && !updating) {
								getRealm().exec(new Runnable() {
									public void run() {
										Object key = event.getSource();
										Object oldValue;
										Object newValue;

										ValueDiff diff = event.diff;
										if (diff == null) {
											oldValue = cachedValues.get(key);
											newValue = detailProperty
													.getValue(key);
										} else {
											oldValue = event.diff.getOldValue();
											newValue = event.diff.getNewValue();
										}

										cachedValues.put(key, newValue);
										fireMapChange(Diffs
												.createMapDiffSingleChange(key,
														oldValue, newValue));
									}
								});
							}
						}
					});
		}
		super.firstListenerAdded();
	}

	protected void lastListenerRemoved() {
		super.lastListenerRemoved();
	}

	protected void hookListener(Object addedKey) {
		if (listener != null) {
			detailProperty.addListener(addedKey, listener);
		}
	}

	protected void unhookListener(Object removedKey) {
		if (listener != null) {
			detailProperty.removeListener(removedKey, listener);
		}
	}

	protected Object doGet(Object key) {
		return detailProperty.getValue(key);
	}

	protected Object doPut(Object key, Object value) {
		Object oldValue = detailProperty.getValue(key);

		boolean changed;
		updating = true;
		try {
			changed = detailProperty.setValue(key, value);
		} finally {
			updating = false;
		}
		
		if (changed) {
			Object newValue = detailProperty.getValue(key);
			cachedValues.put(key, newValue);
			
			if (oldValue != newValue) {
				fireMapChange(Diffs.createMapDiffSingleChange(key, oldValue,
						newValue));
			}
		}

		return oldValue;
	}

	public Object getObserved() {
		return keySet();
	}

	public synchronized void dispose() {
		if (!isDisposed()) {
			if (listener != null) {
				for (Iterator it = values().iterator(); it.hasNext();) {
					unhookListener(it.next());
				}
				listener = null;
			}
			detailProperty = null;
		}

		super.dispose();
	}
}
