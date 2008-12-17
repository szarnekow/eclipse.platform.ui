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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.beans.IBeanProperty;
import org.eclipse.core.databinding.observable.map.MapDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.map.IMapPropertyChangeListener;
import org.eclipse.core.databinding.property.map.SimpleMapProperty;

/**
 * @since 3.3
 * 
 */
public class PojoMapProperty extends SimpleMapProperty implements IBeanProperty {
	private PropertyDescriptor propertyDescriptor;

	/**
	 * @param propertyDescriptor
	 * @param keyType
	 * @param valueType
	 */
	public PojoMapProperty(PropertyDescriptor propertyDescriptor,
			Class keyType, Class valueType) {
		super(keyType, valueType);
		this.propertyDescriptor = propertyDescriptor;
	}

	protected Map doGetMap(Object source) {
		if (source == null)
			return Collections.EMPTY_MAP;
		Object propertyValue = BeanPropertyHelper.readProperty(source,
				propertyDescriptor);
		return asMap(propertyValue);
	}

	private Map asMap(Object propertyValue) {
		if (propertyValue == null)
			return new HashMap();
		return (Map) propertyValue;
	}

	protected void setMap(Object source, Map map, MapDiff diff) {
		if (source != null) {
			BeanPropertyHelper.writeProperty(source, propertyDescriptor, map);
		}
	}

	public PropertyDescriptor getPropertyDescriptor() {
		return propertyDescriptor;
	}

	public INativePropertyListener adaptListener(
			IMapPropertyChangeListener listener) {
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
		String s = beanClass.getName() + "." + propertyName + "{:}"; //$NON-NLS-1$ //$NON-NLS-2$

		Class keyType = (Class) getKeyType();
		Class valueType = (Class) getValueType();
		if (keyType != null || valueType != null) {
			s += " <" + keyType + ", " + valueType + ">"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return s;
	}
}
