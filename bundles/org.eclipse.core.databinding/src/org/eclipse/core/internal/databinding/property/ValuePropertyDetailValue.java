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

	public IObservableValue observeValue(Realm realm, Object source) {
		IObservableValue master = masterProperty.observeValue(realm, source);
		return detailProperty.observeDetailValue(master);
	}

	public IObservableValue observeDetailValue(IObservableValue master) {
		IObservableValue masterValue = masterProperty
				.observeDetailValue(master);
		return detailProperty.observeDetailValue(masterValue);
	}

	public IObservableList observeDetailValues(IObservableList master) {
		master = masterProperty.observeDetailValues(master);
		return detailProperty.observeDetailValues(master);
	}

	public IObservableMap observeDetailValues(IObservableSet master) {
		IObservableMap masterMap = masterProperty.observeDetailValues(master);
		return detailProperty.observeDetailValues(masterMap);
	}

	public IObservableMap observeDetailValues(IObservableMap master) {
		master = masterProperty.observeDetailValues(master);
		return detailProperty.observeDetailValues(master);
	}

	public String toString() {
		return masterProperty + " => " + detailProperty; //$NON-NLS-1$
	}
}
