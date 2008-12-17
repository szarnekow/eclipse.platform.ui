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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.databinding.beans.IBeanProperty;
import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.list.IListPropertyChangeListener;
import org.eclipse.core.databinding.property.list.ListPropertyChangeEvent;
import org.eclipse.core.databinding.property.list.SimpleListProperty;

/**
 * @since 3.3
 * 
 */
public class BeanListProperty extends SimpleListProperty implements
		IBeanProperty {
	private final PropertyDescriptor propertyDescriptor;

	/**
	 * @param propertyDescriptor
	 * @param elementType
	 */
	public BeanListProperty(PropertyDescriptor propertyDescriptor,
			Class elementType) {
		super(elementType == null ? BeanPropertyHelper
				.getCollectionPropertyElementType(propertyDescriptor)
				: elementType);
		this.propertyDescriptor = propertyDescriptor;
	}

	protected List doGetList(Object source) {
		if (source == null)
			return Collections.EMPTY_LIST;
		Object propertyValue = BeanPropertyHelper.readProperty(source,
				propertyDescriptor);
		return asList(propertyValue);
	}

	private List asList(Object propertyValue) {
		if (propertyValue == null)
			return new ArrayList();
		if (propertyDescriptor.getPropertyType().isArray())
			return new ArrayList(Arrays.asList((Object[]) propertyValue));
		return (List) propertyValue;
	}

	protected void setList(Object source, List list, ListDiff diff) {
		if (source != null) {
			BeanPropertyHelper.writeProperty(source, propertyDescriptor,
					convertListToBeanPropertyType(list));
		}
	}

	private Object convertListToBeanPropertyType(List list) {
		Object propertyValue = list;
		if (propertyDescriptor.getPropertyType().isArray()) {
			Class componentType = propertyDescriptor.getPropertyType()
					.getComponentType();
			Object[] array = (Object[]) Array.newInstance(componentType, list
					.size());
			list.toArray(array);
			propertyValue = array;
		}
		return propertyValue;
	}

	public PropertyDescriptor getPropertyDescriptor() {
		return propertyDescriptor;
	}

	public INativePropertyListener adaptListener(
			final IListPropertyChangeListener listener) {
		return new Listener(listener);
	}

	private class Listener implements INativePropertyListener,
			PropertyChangeListener {
		private final IListPropertyChangeListener listener;

		private Listener(IListPropertyChangeListener listener) {
			this.listener = listener;
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if (propertyDescriptor.getName().equals(evt.getPropertyName())) {
				Object oldValue = evt.getOldValue();
				Object newValue = evt.getNewValue();

				ListDiff diff;
				if (oldValue == null && newValue == null) {
					diff = null; // unknown change
				} else {
					diff = Diffs.computeListDiff(asList(oldValue),
							asList(newValue));
				}

				listener.handleListPropertyChange(new ListPropertyChangeEvent(
						evt.getSource(), BeanListProperty.this, diff));
			}
		}
	}

	public void addListener(Object source, INativePropertyListener listener) {
		if (source != null) {
			BeanPropertyListenerSupport.hookListener(source, propertyDescriptor
					.getName(), (PropertyChangeListener) listener);
		}
	}

	public void removeListener(Object source, INativePropertyListener listener) {
		if (source != null) {
			BeanPropertyListenerSupport.unhookListener(source,
					propertyDescriptor.getName(),
					(PropertyChangeListener) listener);
		}
	}

	public String toString() {
		Class beanClass = propertyDescriptor.getReadMethod()
				.getDeclaringClass();
		String propertyName = propertyDescriptor.getName();
		String s = beanClass.getName() + "." + propertyName + "[]"; //$NON-NLS-1$ //$NON-NLS-2$

		Class elementType = (Class) getElementType();
		if (elementType != null)
			s += " <" + elementType.getName() + ">"; //$NON-NLS-1$//$NON-NLS-2$
		return s;
	}
}
