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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;

import org.eclipse.core.databinding.beans.IBeanProperty;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.value.IValuePropertyChangeListener;
import org.eclipse.core.databinding.property.value.SimpleValueProperty;
import org.eclipse.core.databinding.property.value.ValuePropertyChangeEvent;

/**
 * @since 3.3
 * 
 */
public class BeanValueProperty extends SimpleValueProperty implements
		IBeanProperty {
	private final PropertyDescriptor propertyDescriptor;
	private final Class valueType;

	/**
	 * @param propertyDescriptor
	 * @param valueType
	 */
	public BeanValueProperty(PropertyDescriptor propertyDescriptor,
			Class valueType) {
		this.propertyDescriptor = propertyDescriptor;
		this.valueType = valueType == null ? propertyDescriptor
				.getPropertyType() : valueType;
	}

	protected Object getValueType() {
		return valueType;
	}

	protected Object doGetValue(Object source) {
		return BeanPropertyHelper.readProperty(source, propertyDescriptor);
	}

	protected void doSetValue(Object source, Object value) {
		BeanPropertyHelper.writeProperty(source, propertyDescriptor, value);
	}

	public PropertyDescriptor getPropertyDescriptor() {
		return propertyDescriptor;
	}

	public INativePropertyListener adaptListener(
			final IValuePropertyChangeListener listener) {
		return new Listener(listener);
	}

	private class Listener implements INativePropertyListener,
			PropertyChangeListener {
		private final IValuePropertyChangeListener listener;

		private Listener(IValuePropertyChangeListener listener) {
			this.listener = listener;
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if (propertyDescriptor.getName().equals(evt.getPropertyName())) {
				listener
						.handleValuePropertyChange(new ValuePropertyChangeEvent(
								evt.getSource(), BeanValueProperty.this));
			}
		}
	}

	protected void doAddListener(Object source, INativePropertyListener listener) {
		BeanPropertyListenerSupport.hookListener(source, propertyDescriptor
				.getName(), (PropertyChangeListener) listener);
	}

	protected void doRemoveListener(Object source,
			INativePropertyListener listener) {
		BeanPropertyListenerSupport.unhookListener(source, propertyDescriptor
				.getName(), (PropertyChangeListener) listener);
	}

	public String toString() {
		Class beanClass = propertyDescriptor.getReadMethod()
				.getDeclaringClass();
		String propertyName = propertyDescriptor.getName();
		String s = beanClass.getName() + "." + propertyName + ""; //$NON-NLS-1$ //$NON-NLS-2$

		if (valueType != null)
			s += " <" + valueType.getName() + ">"; //$NON-NLS-1$//$NON-NLS-2$
		return s;
	}
}
