/*******************************************************************************
 * Copyright (c) 2008, 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 *     Matthew Hall - bug 278550
 ******************************************************************************/

package org.eclipse.core.internal.databinding.property;

import org.eclipse.core.databinding.observable.ObservableTracker;
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
public class ValuePropertyDetailMap extends MapProperty {
	private final IValueProperty masterProperty;
	private final IMapProperty detailProperty;

	/**
	 * @param masterProperty
	 * @param detailProperty
	 */
	public ValuePropertyDetailMap(IValueProperty masterProperty,
			IMapProperty detailProperty) {
		this.masterProperty = masterProperty;
		this.detailProperty = detailProperty;
	}

	public Object getKeyType() {
		return detailProperty.getKeyType();
	}

	public Object getValueType() {
		return detailProperty.getValueType();
	}

	public IObservableMap observe(final Realm realm, final Object source) {
		final IObservableValue[] masterValue = new IObservableValue[1];

		ObservableTracker.runAndIgnore(new Runnable() {
			public void run() {
				masterValue[0] = masterProperty.observe(realm, source);
			}
		});

		IObservableMap detailMap = detailProperty.observeDetail(masterValue[0]);
		PropertyObservableUtil.cascadeDispose(detailMap, masterValue[0]);
		return detailMap;
	}

	public IObservableMap observeDetail(final IObservableValue master) {
		final IObservableValue[] masterValue = new IObservableValue[1];

		ObservableTracker.runAndIgnore(new Runnable() {
			public void run() {
				masterValue[0] = masterProperty.observeDetail(master);
			}
		});

		IObservableMap detailMap = detailProperty.observeDetail(masterValue[0]);
		PropertyObservableUtil.cascadeDispose(detailMap, masterValue[0]);
		return detailMap;
	}

	public String toString() {
		return masterProperty + " => " + detailProperty; //$NON-NLS-1$
	}
}