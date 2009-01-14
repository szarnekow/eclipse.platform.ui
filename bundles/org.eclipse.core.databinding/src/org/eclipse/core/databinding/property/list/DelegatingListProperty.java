/*******************************************************************************
 * Copyright (c) 2008 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 247997)
 ******************************************************************************/

package org.eclipse.core.databinding.property.list;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.IPropertyChangeListener;

/**
 * @since 1.2
 * 
 */
public abstract class DelegatingListProperty extends ListProperty {
	private final IListProperty nullProperty;
	private final Object elementType;

	protected DelegatingListProperty() {
		this(null);
	}

	protected DelegatingListProperty(Object elementType) {
		this.elementType = elementType;
		this.nullProperty = new NullListProperty();
	}

	protected final IListProperty getDelegate(Object source) {
		if (source == null)
			return null;
		IListProperty delegate = doGetDelegate(source);
		if (delegate == null)
			delegate = nullProperty;
		return delegate;
	}

	protected abstract IListProperty doGetDelegate(Object source);

	public Object getElementType() {
		return elementType;
	}

	public IObservableList observe(Realm realm, Object source) {
		return getDelegate(source).observe(realm, source);
	}

	private class NullListProperty extends SimpleListProperty {
		public Object getElementType() {
			return elementType;
		}

		protected List doGetList(Object source) {
			return Collections.EMPTY_LIST;
		}

		protected void doSetList(Object source, List list, ListDiff diff) {
		}

		protected INativePropertyListener adaptListener(
				IPropertyChangeListener listener) {
			return null;
		}

		protected void doAddListener(Object source,
				INativePropertyListener listener) {
		}

		protected void doRemoveListener(Object source,
				INativePropertyListener listener) {
		}
	}
}
