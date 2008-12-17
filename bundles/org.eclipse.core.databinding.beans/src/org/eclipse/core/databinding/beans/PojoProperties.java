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

package org.eclipse.core.databinding.beans;

import java.beans.PropertyChangeEvent;

import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.core.databinding.property.map.IMapProperty;
import org.eclipse.core.databinding.property.set.ISetProperty;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.core.internal.databinding.beans.BeanPropertyHelper;
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
	public static IValueProperty value(Class beanClass, String propertyName) {
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
	public static IValueProperty value(Class beanClass, String propertyName,
			Class valueType) {
		return new PojoValueProperty(BeanPropertyHelper.getPropertyDescriptor(
				beanClass, propertyName), valueType);
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
	public static ISetProperty set(Class beanClass, String propertyName) {
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
	public static ISetProperty set(Class beanClass, String propertyName,
			Class elementType) {
		return new PojoSetProperty(BeanPropertyHelper.getPropertyDescriptor(
				beanClass, propertyName), elementType);
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
	public static IListProperty list(Class beanClass, String propertyName) {
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
	public static IListProperty list(Class beanClass, String propertyName,
			Class elementType) {
		return new PojoListProperty(BeanPropertyHelper.getPropertyDescriptor(
				beanClass, propertyName), elementType);
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
	public static IMapProperty map(Class beanClass, String propertyName,
			Class keyType, Class valueType) {
		return new PojoMapProperty(BeanPropertyHelper.getPropertyDescriptor(
				beanClass, propertyName), keyType, valueType);
	}
}
