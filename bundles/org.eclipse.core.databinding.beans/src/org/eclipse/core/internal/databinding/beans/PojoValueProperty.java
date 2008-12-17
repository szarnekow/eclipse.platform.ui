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

import org.eclipse.core.databinding.beans.IBeanProperty;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.value.IValuePropertyChangeListener;
import org.eclipse.core.databinding.property.value.SimpleValueProperty;

/**
 * @since 3.3
 * 
 */
public class PojoValueProperty extends SimpleValueProperty implements
		IBeanProperty {
	private PropertyDescriptor propertyDescriptor;

	/**
	 * @param propertyDescriptor
	 * @param valueType
	 */
	public PojoValueProperty(PropertyDescriptor propertyDescriptor,
			Class valueType) {
		super(valueType == null ? propertyDescriptor.getPropertyType()
				: valueType);
		this.propertyDescriptor = propertyDescriptor;
	}

	public Object getValue(Object source) {
		if (source == null)
			return null;
		return BeanPropertyHelper.readProperty(source, propertyDescriptor);
	}

	public void setValue(Object source, Object value) {
		if (source != null) {
			BeanPropertyHelper.writeProperty(source, propertyDescriptor, value);
		}
	}

	public PropertyDescriptor getPropertyDescriptor() {
		return propertyDescriptor;
	}

	public INativePropertyListener adaptListener(
			IValuePropertyChangeListener listener) {
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
		String s = beanClass.getName() + "." + propertyName + ""; //$NON-NLS-1$ //$NON-NLS-2$

		Class valueType = (Class) getValueType();
		if (valueType != null)
			s += " <" + valueType.getName() + ">"; //$NON-NLS-1$//$NON-NLS-2$
		return s;
	}
}
