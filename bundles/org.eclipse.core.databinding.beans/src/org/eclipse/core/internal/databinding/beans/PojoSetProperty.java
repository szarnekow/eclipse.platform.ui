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

package org.eclipse.core.internal.databinding.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.databinding.beans.IBeanProperty;
import org.eclipse.core.databinding.observable.set.SetDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.set.ISetPropertyChangeListener;
import org.eclipse.core.databinding.property.set.SimpleSetProperty;

/**
 * @since 3.3
 * 
 */
public class PojoSetProperty extends SimpleSetProperty implements IBeanProperty {
	private PropertyDescriptor propertyDescriptor;

	/**
	 * @param propertyDescriptor
	 * @param elementType
	 */
	public PojoSetProperty(PropertyDescriptor propertyDescriptor,
			Class elementType) {
		super(elementType == null ? BeanPropertyHelper
				.getCollectionPropertyElementType(propertyDescriptor)
				: elementType);
		this.propertyDescriptor = propertyDescriptor;
	}

	protected Set doGetSet(Object source) {
		if (source == null)
			return Collections.EMPTY_SET;
		Object propertyValue = BeanPropertyHelper.readProperty(source,
				propertyDescriptor);
		return asSet(propertyValue);
	}

	private Set asSet(Object propertyValue) {
		if (propertyValue == null)
			return Collections.EMPTY_SET;
		if (propertyDescriptor.getPropertyType().isArray())
			return new HashSet(Arrays.asList((Object[]) propertyValue));
		return (Set) propertyValue;
	}

	protected void setSet(Object source, Set set, SetDiff diff) {
		if (source != null) {
			BeanPropertyHelper.writeProperty(source, propertyDescriptor,
					convertSetToBeanPropertyType(set));
		}
	}

	private Object convertSetToBeanPropertyType(Set set) {
		Object propertyValue = set;
		if (propertyDescriptor.getPropertyType().isArray()) {
			Class componentType = propertyDescriptor.getPropertyType()
					.getComponentType();
			Object[] array = (Object[]) Array.newInstance(componentType, set
					.size());
			propertyValue = set.toArray(array);
		}
		return propertyValue;
	}

	public PropertyDescriptor getPropertyDescriptor() {
		return propertyDescriptor;
	}

	public INativePropertyListener adaptListener(
			ISetPropertyChangeListener listener) {
		return null;
	}

	public void addListener(Object source, INativePropertyListener listener) {
	}

	public void removeListener(Object source, INativePropertyListener listener) {
	}

	public String toString() {
		Class beanClass = propertyDescriptor.getReadMethod()
				.getDeclaringClass();
		String propertyName = propertyDescriptor.getName();
		String s = beanClass.getName() + "." + propertyName + "{}"; //$NON-NLS-1$ //$NON-NLS-2$

		Class elementType = (Class) getElementType();
		if (elementType != null)
			s += " <" + elementType.getName() + ">"; //$NON-NLS-1$//$NON-NLS-2$
		return s;
	}
}
