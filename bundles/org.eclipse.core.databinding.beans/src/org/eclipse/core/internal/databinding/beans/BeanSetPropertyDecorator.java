/*******************************************************************************
 * Copyright (c) 2008 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 195222)
 ******************************************************************************/

package org.eclipse.core.internal.databinding.beans;

import java.beans.PropertyDescriptor;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.IBeanMapProperty;
import org.eclipse.core.databinding.beans.IBeanSetProperty;
import org.eclipse.core.databinding.beans.IBeanValueProperty;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.set.ISetProperty;
import org.eclipse.core.databinding.property.set.SetProperty;

/**
 * @since 3.3
 * 
 */
public class BeanSetPropertyDecorator extends SetProperty implements
		IBeanSetProperty {
	private final ISetProperty delegate;
	private final PropertyDescriptor propertyDescriptor;

	/**
	 * @param delegate
	 * @param propertyDescriptor
	 */
	public BeanSetPropertyDecorator(ISetProperty delegate,
			PropertyDescriptor propertyDescriptor) {
		this.delegate = delegate;
		this.propertyDescriptor = propertyDescriptor;
	}

	public PropertyDescriptor getPropertyDescriptor() {
		return propertyDescriptor;
	}

	public Object getElementType() {
		return delegate.getElementType();
	}

	public IBeanMapProperty values(String propertyName) {
		Class beanClass = (Class) delegate.getElementType();
		if (beanClass != null)
			return values(beanClass, propertyName);
		return values(BeanProperties.value(propertyName));
	}

	public IBeanMapProperty values(String propertyName, Class valueType) {
		Class beanClass = (Class) delegate.getElementType();
		if (beanClass != null)
			return values(beanClass, propertyName, valueType);
		return values(BeanProperties.value(propertyName, valueType));
	}

	public IBeanMapProperty values(Class beanClass, String propertyName) {
		return values(BeanProperties.value(beanClass, propertyName));
	}

	public IBeanMapProperty values(Class beanClass, String propertyName,
			Class valueType) {
		return values(BeanProperties.value(beanClass, propertyName, valueType));
	}

	public IBeanMapProperty values(IBeanValueProperty property) {
		return new BeanMapPropertyDecorator(super.values(property), property
				.getPropertyDescriptor());
	}

	public IObservableSet observe(Object source) {
		return delegate.observe(source);
	}

	public IObservableSet observe(Realm realm, Object source) {
		return delegate.observe(realm, source);
	}

	public IObservableFactory setFactory() {
		return delegate.setFactory();
	}

	public IObservableFactory setFactory(Realm realm) {
		return delegate.setFactory(realm);
	}

	public IObservableSet observeDetail(IObservableValue master) {
		return delegate.observeDetail(master);
	}
}
