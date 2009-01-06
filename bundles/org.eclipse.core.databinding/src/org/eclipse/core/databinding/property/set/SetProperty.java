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

package org.eclipse.core.databinding.property.set;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.property.Property;
import org.eclipse.core.databinding.property.map.IMapProperty;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.core.internal.databinding.property.SetPropertyDetailValuesMap;

/**
 * Abstract implementation of ISetProperty
 * 
 * @since 1.2
 */
public abstract class SetProperty extends Property implements ISetProperty {
	public IObservableSet observe(Object source) {
		Realm realm = getPreferredRealm(source);
		if (realm == null)
			realm = Realm.getDefault();
		return observe(realm, source);
	}

	public IObservableFactory setFactory() {
		return setFactory(Realm.getDefault());
	}

	public IObservableFactory setFactory(final Realm realm) {
		return new IObservableFactory() {
			public IObservable createObservable(Object target) {
				return observe(realm, target);
			}
		};
	}

	public final IMapProperty values(IValueProperty detailValues) {
		return new SetPropertyDetailValuesMap(this, detailValues);
	}
}
