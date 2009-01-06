/*******************************************************************************
 * Copyright (c) 2008 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 *     Matthew Hall - bug 195222
 ******************************************************************************/

package org.eclipse.core.databinding.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.internal.databinding.beans.BeanListPropertyDecorator;
import org.eclipse.core.internal.databinding.beans.BeanMapPropertyDecorator;
import org.eclipse.core.internal.databinding.beans.BeanPropertyHelper;
import org.eclipse.core.internal.databinding.beans.BeanSetPropertyDecorator;
import org.eclipse.core.internal.databinding.beans.BeanValuePropertyDecorator;
import org.eclipse.core.internal.databinding.beans.PojoListProperty;
import org.eclipse.core.internal.databinding.beans.PojoMapProperty;
import org.eclipse.core.internal.databinding.beans.PojoSetProperty;
import org.eclipse.core.internal.databinding.beans.PojoValueProperty;

/**
 * A factory for creating properties for POJOs (plain old java objects) that
 * conform to idea of an object with getters and setters but does not provide
 * {@link PropertyChangeEvent property change events} on change. This factory is
 * identical to {@link BeanProperties} except for this fact.
 * 
 * @since 1.2
 */
public class PojoProperties {
	/**
	 * Returns a value property for the given property name of the given bean
	 * class.
	 * 
	 * @param beanClass
	 *            the bean class
	 * @param propertyName
	 *            the property name
	 * @return a value property for the given property name of the given bean
	 *         class.
	 */
	public static IBeanValueProperty value(Class beanClass, String propertyName) {
		return value(beanClass, propertyName, null);
	}

	/**
	 * Returns a value property for the given property name of the given bean
	 * class.
	 * 
	 * @param beanClass
	 *            the bean class
	 * @param propertyName
	 *            the property name
	 * @param valueType
	 *            the value type of the returned value property
	 * @return a value property for the given property name of the given bean
	 *         class.
	 */
	public static IBeanValueProperty value(Class beanClass,
			String propertyName, Class valueType) {
		String[] propertyNames = split(propertyName);
		if (propertyNames.length > 1)
			valueType = null;

		PropertyDescriptor propertyDescriptor = BeanPropertyHelper
				.getPropertyDescriptor(beanClass, propertyNames[0]);
		IBeanValueProperty property = new BeanValuePropertyDecorator(
				new PojoValueProperty(propertyDescriptor, valueType),
				propertyDescriptor);
		for (int i = 1; i < propertyNames.length; i++) {
			property = property.value(propertyNames[i]);
		}
		return property;
	}

	private static String[] split(String propertyName) {
		if (propertyName.indexOf('.') == -1)
			return new String[] { propertyName };
		List propertyNames = new ArrayList();
		int index;
		while ((index = propertyName.indexOf('.')) != -1) {
			propertyNames.add(propertyName.substring(0, index));
			propertyName = propertyName.substring(index + 1);
		}
		propertyNames.add(propertyName);
		return (String[]) propertyNames
				.toArray(new String[propertyNames.size()]);
	}

	/**
	 * Returns a value property array for the given property names of the given
	 * bean class.
	 * 
	 * @param beanClass
	 * @param propertyNames
	 * @return a value property array for the given property names of the given
	 *         bean class.
	 */
	public static IBeanValueProperty[] values(Class beanClass,
			String[] propertyNames) {
		IBeanValueProperty[] properties = new IBeanValueProperty[propertyNames.length];
		for (int i = 0; i < properties.length; i++)
			properties[i] = value(beanClass, propertyNames[i]);
		return properties;
	}

	/**
	 * Returns a set property for the given property name of the given bean
	 * class.
	 * 
	 * @param beanClass
	 *            the bean class
	 * @param propertyName
	 *            the property name
	 * @return a set property for the given property name of the given bean
	 *         class.
	 */
	public static IBeanSetProperty set(Class beanClass, String propertyName) {
		return set(beanClass, propertyName, null);
	}

	/**
	 * Returns a set property for the given property name of the given bean
	 * class.
	 * 
	 * @param beanClass
	 *            the bean class
	 * @param propertyName
	 *            the property name
	 * @param elementType
	 *            the element type of the returned set property
	 * @return a set property for the given property name of the given bean
	 *         class.
	 */
	public static IBeanSetProperty set(Class beanClass, String propertyName,
			Class elementType) {
		PropertyDescriptor propertyDescriptor = BeanPropertyHelper
				.getPropertyDescriptor(beanClass, propertyName);
		PojoSetProperty property = new PojoSetProperty(propertyDescriptor,
				elementType);
		return new BeanSetPropertyDecorator(property, propertyDescriptor);
	}

	/**
	 * Returns a list property for the given property name of the given bean
	 * class.
	 * 
	 * @param beanClass
	 *            the bean class
	 * @param propertyName
	 *            the property name
	 * @return a list property for the given property name of the given bean
	 *         class.
	 */
	public static IBeanListProperty list(Class beanClass, String propertyName) {
		return list(beanClass, propertyName, null);
	}

	/**
	 * Returns a list property for the given property name of the given bean
	 * class.
	 * 
	 * @param beanClass
	 *            the bean class
	 * @param propertyName
	 *            the property name
	 * @param elementType
	 *            the element type of the returned list property
	 * @return a list property for the given property name of the given bean
	 *         class.
	 */
	public static IBeanListProperty list(Class beanClass, String propertyName,
			Class elementType) {
		PropertyDescriptor propertyDescriptor = BeanPropertyHelper
				.getPropertyDescriptor(beanClass, propertyName);
		PojoListProperty property = new PojoListProperty(propertyDescriptor,
				elementType);
		return new BeanListPropertyDecorator(property, propertyDescriptor);
	}

	/**
	 * Returns a map property for the given property name of the given bean
	 * class.
	 * 
	 * @param beanClass
	 *            the bean class
	 * @param propertyName
	 *            the property name
	 * @return a map property for the given property name of the given bean
	 *         class.
	 */
	public static IBeanMapProperty map(Class beanClass, String propertyName) {
		return map(beanClass, propertyName, null, null);
	}

	/**
	 * Returns a map property for the given property name of the given bean
	 * class.
	 * 
	 * @param beanClass
	 *            the bean class
	 * @param propertyName
	 *            the property name
	 * @param keyType
	 *            the key type of the returned map property
	 * @param valueType
	 *            the value type of the returned map property
	 * @return a map property for the given property name of the given bean
	 *         class.
	 */
	public static IBeanMapProperty map(Class beanClass, String propertyName,
			Class keyType, Class valueType) {
		PropertyDescriptor propertyDescriptor = BeanPropertyHelper
				.getPropertyDescriptor(beanClass, propertyName);
		PojoMapProperty property = new PojoMapProperty(propertyDescriptor,
				keyType, valueType);
		return new BeanMapPropertyDecorator(property, propertyDescriptor);
	}
}
