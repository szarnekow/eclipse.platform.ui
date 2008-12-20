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

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.databinding.observable.value.ValueDiff;
import org.eclipse.core.databinding.property.IProperty;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.IPropertyObservable;
import org.eclipse.core.internal.databinding.Util;

/**
 * @since 1.2
 * 
 */
class SimpleValuePropertyObservableValue extends AbstractObservableValue
		implements IPropertyObservable {
	private Object source;
	private SimpleValueProperty property;

	private boolean updating = false;
	private Object cachedValue;

	private INativePropertyListener listener;

	/**
	 * @param realm
	 * @param source
	 * @param property
	 */
	public SimpleValuePropertyObservableValue(Realm realm, Object source,
			SimpleValueProperty property) {
		super(realm);
		this.source = source;
		this.property = property;
	}

	protected void firstListenerAdded() {
		if (!isDisposed()) {
			cachedValue = property.getValue(source);
			if (listener == null) {
				listener = property
						.adaptListener(new IValuePropertyChangeListener() {
							public void handleValuePropertyChange(
									final ValuePropertyChangeEvent event) {
								if (!isDisposed() && !updating) {
									getRealm().exec(new Runnable() {
										public void run() {
											ValueDiff diff = event.diff;
											if (diff == null) {
												Object oldValue = cachedValue;
												Object newValue = cachedValue = property
														.getValue(source);
												if (Util.equals(oldValue,
														newValue))
													return;

												diff = Diffs.createValueDiff(
														oldValue, newValue);
											}

											fireValueChange(diff);
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
		cachedValue = null;
	}

	protected Object doGetValue() {
		if (hasListeners()) {
			Object oldValue = cachedValue;
			Object newValue = cachedValue = property.getValue(source);

			if (hasListeners() && !Util.equals(oldValue, newValue)) {
				fireValueChange(Diffs.createValueDiff(oldValue, newValue));
			}
			return newValue;
		}
		return property.getValue(source);
	}

	protected void doSetValue(Object value) {
		if (hasListeners()) {
			Object oldValue = cachedValue;

			boolean changed;
			updating = true;
			try {
				changed = property.setValue(source, value);
			} finally {
				updating = false;
			}

			if (changed) {
				Object newValue = cachedValue = property.getValue(source);
				if (hasListeners() && !Util.equals(oldValue, newValue)) {
					fireValueChange(Diffs.createValueDiff(oldValue, newValue));
				}
			}
		} else {
			property.setValue(source, value);
		}
	}

	public Object getValueType() {
		return property.getValueType();
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
			source = null;
			property = null;
			listener = null;
		}
		super.dispose();
	}
}
