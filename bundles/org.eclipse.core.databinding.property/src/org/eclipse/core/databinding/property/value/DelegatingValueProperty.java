/*******************************************************************************
 * Copyright (c) 2008 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 247997)
 ******************************************************************************/

package org.eclipse.core.databinding.property.value;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.IPropertyChangeListener;
import org.eclipse.core.databinding.property.Property;

/**
 * @since 1.2
 * 
 */
public abstract class DelegatingValueProperty extends ValueProperty {
	private final Object valueType;
	private final IValueProperty nullProperty = new NullValueProperty();

	protected DelegatingValueProperty() {
		this(null);
	}

	protected DelegatingValueProperty(Object valueType) {
		this.valueType = valueType;
	}

	protected final IValueProperty getDelegate(Object source) {
		if (source == null)
			return null;
		IValueProperty delegate = doGetDelegate(source);
		if (delegate == null)
			delegate = nullProperty;
		return delegate;
	}

	/**
	 * Returns the property to delegate to for the specified source object.
	 * Implementers must ensure that repeated calls to this method with the same
	 * source object returns the same delegate instance.
	 * 
	 * @param source
	 *            the property source
	 * @return the property to delegate to for the specified source object.
	 */
	protected abstract IValueProperty doGetDelegate(Object source);

	public Object getValueType() {
		return valueType;
	}

	public Realm getPreferredRealm(Object source) {
		IValueProperty delegate = getDelegate(source);
		Realm realm = null;
		if (delegate instanceof Property)
			realm = ((Property) delegate).getPreferredRealm(source);
		if (realm == null)
			realm = super.getPreferredRealm(source);
		return realm;
	}

	public IObservableValue observe(Realm realm, Object source) {
		return getDelegate(source).observe(realm, source);
	}

	public IObservableList observeDetail(IObservableList master) {
		return new ObservableListDelegatingValuePropertyObservableList(master,
				this);
	}

	public IObservableMap observeDetail(IObservableSet master) {
		return new ObservableSetDelegatingValuePropertyObservableMap(master,
				this);
	}

	public IObservableMap observeDetail(IObservableMap master) {
		return new ObservableMapDelegatingValuePropertyObservableMap(master,
				this);
	}

	private class NullValueProperty extends SimpleValueProperty {
		public Object getValueType() {
			return valueType;
		}

		protected Object doGetValue(Object source) {
			return null;
		}

		protected void doSetValue(Object source, Object value) {
		}

		protected INativePropertyListener adaptListener(
				IPropertyChangeListener listener) {
			return null;
		}

		protected void doAddListener(Object source,
				INativePropertyListener listener) {
		}

		protected void doRemoveListener(Object source,
				INativePropertyListener listener) {
		}
	}
}
