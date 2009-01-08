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
import org.eclipse.core.databinding.beans.IBeanListProperty;
import org.eclipse.core.databinding.beans.IBeanMapProperty;
import org.eclipse.core.databinding.beans.IBeanSetProperty;
import org.eclipse.core.databinding.beans.IBeanValueProperty;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.core.databinding.property.value.ValueProperty;

/**
 * @since 3.3
 * 
 */
public class BeanValuePropertyDecorator extends ValueProperty implements
		IBeanValueProperty {
	private final IValueProperty delegate;
	private final PropertyDescriptor propertyDescriptor;

	/**
	 * @param delegate
	 * @param propertyDescriptor
	 */
	public BeanValuePropertyDecorator(IValueProperty delegate,
			PropertyDescriptor propertyDescriptor) {
		this.delegate = delegate;
		this.propertyDescriptor = propertyDescriptor;
	}

	public PropertyDescriptor getPropertyDescriptor() {
		return propertyDescriptor;
	}

	public Object getValueType() {
		return delegate.getValueType();
	}

	public IBeanValueProperty value(String propertyName) {
		Class beanClass = (Class) delegate.getValueType();
		if (beanClass != null)
			return value(beanClass, propertyName);
		return value(BeanProperties.value(propertyName));
	}

	public IBeanValueProperty value(String propertyName, Class valueType) {
		Class beanClass = (Class) delegate.getValueType();
		if (beanClass != null)
			return value(beanClass, propertyName, valueType);
		return value(BeanProperties.value(propertyName, valueType));
	}

	public IBeanValueProperty value(Class beanClass, String propertyName) {
		return value(BeanProperties.value(beanClass, propertyName));
	}

	public IBeanValueProperty value(Class beanClass, String propertyName,
			Class valueType) {
		return value(BeanProperties.value(beanClass, propertyName, valueType));
	}

	public IBeanValueProperty value(IBeanValueProperty property) {
		return new BeanValuePropertyDecorator(super.value(property), property
				.getPropertyDescriptor());
	}

	public IBeanListProperty list(String propertyName) {
		Class beanClass = (Class) delegate.getValueType();
		if (beanClass != null)
			return list(beanClass, propertyName);
		return list(BeanProperties.list(propertyName));
	}

	public IBeanListProperty list(String propertyName, Class elementType) {
		Class beanClass = (Class) delegate.getValueType();
		if (beanClass != null)
			return list(beanClass, propertyName, elementType);
		return list(BeanProperties.list(propertyName, elementType));
	}

	public IBeanListProperty list(Class beanClass, String propertyName) {
		return list(BeanProperties.list(beanClass, propertyName));
	}

	public IBeanListProperty list(Class beanClass, String propertyName,
			Class elementType) {
		return list(BeanProperties.list(beanClass, propertyName, elementType));
	}

	public IBeanListProperty list(IBeanListProperty property) {
		return new BeanListPropertyDecorator(super.list(property), property
				.getPropertyDescriptor());
	}

	public IBeanSetProperty set(String propertyName) {
		Class beanClass = (Class) delegate.getValueType();
		if (beanClass != null)
			return set(beanClass, propertyName);
		return set(BeanProperties.set(propertyName));
	}

	public IBeanSetProperty set(String propertyName, Class elementType) {
		Class beanClass = (Class) delegate.getValueType();
		if (beanClass != null)
			return set(beanClass, propertyName, elementType);
		return set(BeanProperties.set(propertyName, elementType));
	}

	public IBeanSetProperty set(Class beanClass, String propertyName) {
		IBeanSetProperty property = BeanProperties.set(beanClass, propertyName);
		return new BeanSetPropertyDecorator(set(property), property
				.getPropertyDescriptor());
	}

	public IBeanSetProperty set(Class beanClass, String propertyName,
			Class elementType) {
		return set(BeanProperties.set(beanClass, propertyName, elementType));
	}

	public IBeanSetProperty set(IBeanSetProperty property) {
		return new BeanSetPropertyDecorator(super.set(property), property
				.getPropertyDescriptor());
	}

	public IBeanMapProperty map(String propertyName) {
		Class beanClass = (Class) delegate.getValueType();
		if (beanClass != null)
			return map(beanClass, propertyName);
		return map(BeanProperties.map(propertyName));
	}

	public IBeanMapProperty map(String propertyName, Class keyType,
			Class valueType) {
		Class beanClass = (Class) delegate.getValueType();
		if (beanClass != null)
			return map(beanClass, propertyName, keyType, valueType);
		return map(BeanProperties.map(propertyName, keyType, valueType));
	}

	public IBeanMapProperty map(Class beanClass, String propertyName) {
		return map(BeanProperties.map(beanClass, propertyName));
	}

	public IBeanMapProperty map(Class beanClass, String propertyName,
			Class keyType, Class valueType) {
		return map(BeanProperties.map(beanClass, propertyName, keyType,
				valueType));
	}

	public IBeanMapProperty map(IBeanMapProperty property) {
		return new BeanMapPropertyDecorator(super.map(property), property
				.getPropertyDescriptor());
	}

	public IObservableValue observe(Object source) {
		return delegate.observe(source);
	}

	public IObservableValue observe(Realm realm, Object source) {
		return delegate.observe(realm, source);
	}

	public IObservableFactory valueFactory() {
		return delegate.valueFactory();
	}

	public IObservableFactory valueFactory(Realm realm) {
		return delegate.valueFactory(realm);
	}

	public IObservableValue observeDetail(IObservableValue master) {
		return delegate.observeDetail(master);
	}

	public IObservableList observeDetail(IObservableList master) {
		return delegate.observeDetail(master);
	}

	public IObservableMap observeDetail(IObservableSet master) {
		return delegate.observeDetail(master);
	}

	public IObservableMap observeDetail(IObservableMap master) {
		return delegate.observeDetail(master);
	}
}
