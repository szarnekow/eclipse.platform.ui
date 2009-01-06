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
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.core.databinding.property.value.ValueProperty;

/**
 * @since 1.2
 * 
 */
public class ValuePropertyDetailValue extends ValueProperty implements
		IValueProperty {
	private IValueProperty masterProperty;
	private IValueProperty detailProperty;

	/**
	 * @param masterProperty
	 * @param detailProperty
	 */
	public ValuePropertyDetailValue(IValueProperty masterProperty,
			IValueProperty detailProperty) {
		this.masterProperty = masterProperty;
		this.detailProperty = detailProperty;
	}

	public Object getValueType() {
		return detailProperty.getValueType();
	}

	public IObservableValue observe(Realm realm, Object source) {
		IObservableValue master = masterProperty.observe(realm, source);
		return detailProperty.observeDetail(master);
	}

	public IObservableValue observeDetail(IObservableValue master) {
		IObservableValue masterValue = masterProperty
				.observeDetail(master);
		return detailProperty.observeDetail(masterValue);
	}

	public IObservableList observeDetail(IObservableList master) {
		master = masterProperty.observeDetail(master);
		return detailProperty.observeDetail(master);
	}

	public IObservableMap observeDetail(IObservableSet master) {
		IObservableMap masterMap = masterProperty.observeDetail(master);
		return detailProperty.observeDetail(masterMap);
	}

	public IObservableMap observeDetail(IObservableMap master) {
		master = masterProperty.observeDetail(master);
		return detailProperty.observeDetail(master);
	}

	public String toString() {
		return masterProperty + " => " + detailProperty; //$NON-NLS-1$
	}
}
