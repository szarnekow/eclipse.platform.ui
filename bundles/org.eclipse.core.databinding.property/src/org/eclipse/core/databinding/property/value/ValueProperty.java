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

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.Properties;
import org.eclipse.core.databinding.property.Property;
import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.core.databinding.property.map.IMapProperty;
import org.eclipse.core.databinding.property.set.ISetProperty;

/**
 * Abstract implementation of IValueProperty
 * 
 * @since 1.2
 */
public abstract class ValueProperty extends Property implements IValueProperty {
	public IObservableValue observeValue(Object source) {
		Realm realm = getPreferredRealm(source);
		if (realm == null)
			realm = Realm.getDefault();
		return observeValue(realm, source);
	}

	public IObservableFactory valueFactory() {
		return valueFactory(Realm.getDefault());
	}

	public IObservableFactory valueFactory(final Realm realm) {
		return new IObservableFactory() {
			public IObservable createObservable(Object target) {
				return observeValue(realm, target);
			}
		};
	}

	public final IValueProperty chain(IValueProperty detailValue) {
		return Properties.detailValue(this, detailValue);
	}

	public final IListProperty chain(IListProperty detailList) {
		return Properties.detailList(this, detailList);
	}

	public final ISetProperty chain(ISetProperty detailSet) {
		return Properties.detailSet(this, detailSet);
	}

	public final IMapProperty chain(IMapProperty detailMap) {
		return Properties.detailMap(this, detailMap);
	}
}
