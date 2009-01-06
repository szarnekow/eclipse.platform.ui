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
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.set.ISetProperty;
import org.eclipse.core.databinding.property.set.SetProperty;
import org.eclipse.core.databinding.property.value.IValueProperty;

/**
 * @since 3.3
 * 
 */
public class ValuePropertyDetailSet extends SetProperty {
	private IValueProperty masterProperty;
	private ISetProperty detailProperty;

	/**
	 * @param masterProperty
	 * @param detailProperty
	 */
	public ValuePropertyDetailSet(IValueProperty masterProperty,
			ISetProperty detailProperty) {
		this.masterProperty = masterProperty;
		this.detailProperty = detailProperty;
	}

	public Object getElementType() {
		return detailProperty.getElementType();
	}

	public IObservableSet observe(Realm realm, Object source) {
		IObservableValue master = masterProperty.observe(realm, source);
		return detailProperty.observeDetail(master);
	}

	public IObservableSet observeDetail(IObservableValue master) {
		IObservableValue masterValue = masterProperty
				.observeDetail(master);
		return detailProperty.observeDetail(masterValue);
	}

	public String toString() {
		return masterProperty + " => " + detailProperty; //$NON-NLS-1$
	}
}
