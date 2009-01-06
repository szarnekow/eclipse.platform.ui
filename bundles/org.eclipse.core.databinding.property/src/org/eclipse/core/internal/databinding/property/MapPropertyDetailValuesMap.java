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

package org.eclipse.core.internal.databinding.property;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.map.IMapProperty;
import org.eclipse.core.databinding.property.map.MapProperty;
import org.eclipse.core.databinding.property.value.IValueProperty;

/**
 * @since 3.3
 * 
 */
public class MapPropertyDetailValuesMap extends MapProperty {
	private final IMapProperty masterProperty;
	private final IValueProperty detailProperty;

	/**
	 * @param masterProperty
	 * @param detailProperty
	 */
	public MapPropertyDetailValuesMap(IMapProperty masterProperty,
			IValueProperty detailProperty) {
		this.masterProperty = masterProperty;
		this.detailProperty = detailProperty;
	}

	public Object getKeyType() {
		return masterProperty.getKeyType();
	}

	public Object getValueType() {
		return detailProperty.getValueType();
	}

	public IObservableMap observe(Realm realm, Object source) {
		IObservableMap master = masterProperty.observe(realm, source);
		return detailProperty.observeDetail(master);
	}

	public IObservableMap observeDetail(IObservableValue master) {
		IObservableMap masterMap = masterProperty.observeDetail(master);
		return detailProperty.observeDetail(masterMap);
	}

	public String toString() {
		return masterProperty + " => " + detailProperty; //$NON-NLS-1$
	}
}
