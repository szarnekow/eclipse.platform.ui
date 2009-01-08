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

package org.eclipse.core.databinding.property.map;

import java.util.Collections;
import java.util.Map;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.MapDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.IPropertyChangeListener;
import org.eclipse.core.databinding.property.Property;

/**
 * @since 1.2
 * 
 */
public abstract class DelegatingMapProperty extends MapProperty {
	private final Object keyType;
	private final Object valueType;
	private final IMapProperty nullProperty = new NullMapProperty();

	protected DelegatingMapProperty() {
		this(null, null);
	}

	protected DelegatingMapProperty(Object keyType, Object valueType) {
		this.keyType = keyType;
		this.valueType = valueType;
	}

	protected final IMapProperty getDelegate(Object source) {
		if (source == null)
			return null;
		IMapProperty delegate = doGetDelegate(source);
		if (delegate == null)
			delegate = nullProperty;
		return delegate;
	}

	protected abstract IMapProperty doGetDelegate(Object source);

	public Object getKeyType() {
		return keyType;
	}

	public Object getValueType() {
		return valueType;
	}

	public Realm getPreferredRealm(Object source) {
		IMapProperty delegate = getDelegate(source);
		Realm realm = null;
		if (delegate instanceof Property)
			realm = ((Property) delegate).getPreferredRealm(source);
		if (realm == null)
			realm = super.getPreferredRealm(source);
		return realm;
	}

	public IObservableMap observe(Realm realm, Object source) {
		return getDelegate(source).observe(realm, source);
	}

	private class NullMapProperty extends SimpleMapProperty {
		protected Map doGetMap(Object source) {
			return Collections.EMPTY_MAP;
		}

		protected void doSetMap(Object source, Map map, MapDiff diff) {
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

		public Object getKeyType() {
			return keyType;
		}

		public Object getValueType() {
			return valueType;
		}
	}
}
