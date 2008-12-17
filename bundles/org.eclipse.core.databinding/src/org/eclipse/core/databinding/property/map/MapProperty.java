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

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.property.Properties;
import org.eclipse.core.databinding.property.Property;
import org.eclipse.core.databinding.property.value.IValueProperty;

/**
 * Abstract implementation of IMapProperty
 * 
 * @since 1.2
 */
public abstract class MapProperty extends Property implements IMapProperty {
	public IObservableMap observeMap(Object source) {
		return observeMap(Realm.getDefault(), source);
	}

	public final IMapProperty chain(IValueProperty detailValues) {
		return Properties.detailValues(this, detailValues);
	}
}