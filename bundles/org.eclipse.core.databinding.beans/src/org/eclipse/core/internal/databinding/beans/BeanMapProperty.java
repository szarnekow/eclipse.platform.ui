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
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.beans.IBeanProperty;
import org.eclipse.core.databinding.observable.map.MapDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.map.IMapPropertyChangeListener;
import org.eclipse.core.databinding.property.map.MapPropertyChangeEvent;
import org.eclipse.core.databinding.property.map.SimpleMapProperty;

/**
 * @since 3.3
 * 
 */
public class BeanMapProperty extends SimpleMapProperty implements IBeanProperty {
	private final PropertyDescriptor propertyDescriptor;
	private final Class keyType;
	private final Class valueType;

	/**
	 * @param propertyDescriptor
	 * @param keyType
	 * @param valueType
	 */
	public BeanMapProperty(PropertyDescriptor propertyDescriptor,
			Class keyType, Class valueType) {
		this.propertyDescriptor = propertyDescriptor;
		this.keyType = keyType;
		this.valueType = valueType;
	}

	protected Object getKeyType() {
		return keyType;
	}

	protected Object getValueType() {
		return valueType;
	}

	protected Map doGetMap(Object source) {
		return asMap(BeanPropertyHelper
				.readProperty(source, propertyDescriptor));
	}

	private Map asMap(Object propertyValue) {
		if (propertyValue == null)
			return new HashMap();
		return (Map) propertyValue;
	}

	protected void doSetMap(Object source, Map map, MapDiff diff) {
		BeanPropertyHelper.writeProperty(source, propertyDescriptor, map);
	}

	public PropertyDescriptor getPropertyDescriptor() {
		return propertyDescriptor;
	}

	public INativePropertyListener adaptListener(
			final IMapPropertyChangeListener listener) {
		return new Listener(listener);
	}

	private class Listener implements INativePropertyListener,
			PropertyChangeListener {
		private final IMapPropertyChangeListener listener;

		private Listener(IMapPropertyChangeListener listener) {
			this.listener = listener;
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if (propertyDescriptor.getName().equals(evt.getPropertyName())) {
				listener.handleMapPropertyChange(new MapPropertyChangeEvent(evt
						.getSource(), BeanMapProperty.this));
			}
		}
	}

	public void doAddListener(Object source, INativePropertyListener listener) {
		BeanPropertyListenerSupport.hookListener(source, propertyDescriptor
				.getName(), (PropertyChangeListener) listener);
	}

	public void doRemoveListener(Object source, INativePropertyListener listener) {
		BeanPropertyListenerSupport.unhookListener(source, propertyDescriptor
				.getName(), (PropertyChangeListener) listener);
	}

	public String toString() {
		Class beanClass = propertyDescriptor.getReadMethod()
				.getDeclaringClass();
		String propertyName = propertyDescriptor.getName();
		String s = beanClass.getName() + "." + propertyName + "{:}"; //$NON-NLS-1$ //$NON-NLS-2$

		if (keyType != null || valueType != null) {
			s += " <" + keyType + ", " + valueType + ">"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return s;
	}
}
