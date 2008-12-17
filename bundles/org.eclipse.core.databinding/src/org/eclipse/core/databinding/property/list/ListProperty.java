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

package org.eclipse.core.databinding.property.list;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.property.Properties;
import org.eclipse.core.databinding.property.Property;
import org.eclipse.core.databinding.property.value.IValueProperty;

/**
 * Abstract implementation of IListProperty.
 * 
 * @since 1.2
 */
public abstract class ListProperty extends Property implements IListProperty {
	public IObservableList observeList(Object source) {
		return observeList(Realm.getDefault(), source);
	}

	public final IListProperty chain(IValueProperty detailValue) {
		return Properties.detailValues(this, detailValue);
	}
}
