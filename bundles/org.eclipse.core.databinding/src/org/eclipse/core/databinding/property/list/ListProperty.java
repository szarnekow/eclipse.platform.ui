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

package org.eclipse.core.databinding.property.list;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.masterdetail.MasterDetailObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.Property;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.core.internal.databinding.property.ListPropertyDetailValuesList;

/**
 * Abstract implementation of IListProperty.
 * 
 * @since 1.2
 */
public abstract class ListProperty extends Property implements IListProperty {
	public IObservableList observe(Object source) {
		Realm realm = getPreferredRealm(source);
		if (realm == null)
			realm = Realm.getDefault();
		return observe(realm, source);
	}

	public IObservableFactory listFactory() {
		return listFactory(Realm.getDefault());
	}

	public IObservableFactory listFactory(final Realm realm) {
		return new IObservableFactory() {
			public IObservable createObservable(Object target) {
				return observe(realm, target);
			}
		};
	}

	public IObservableList observeDetail(IObservableValue master) {
		return MasterDetailObservables.detailList(master, listFactory(master
				.getRealm()), getElementType());
	}

	public final IListProperty values(IValueProperty detailValue) {
		return new ListPropertyDetailValuesList(this, detailValue);
	}
}
