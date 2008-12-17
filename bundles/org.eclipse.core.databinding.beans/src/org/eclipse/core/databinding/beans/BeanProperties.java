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

import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.core.databinding.property.map.IMapProperty;
import org.eclipse.core.databinding.property.set.ISetProperty;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.core.internal.databinding.beans.BeanListProperty;
import org.eclipse.core.internal.databinding.beans.BeanMapProperty;
import org.eclipse.core.internal.databinding.beans.BeanPropertyHelper;
import org.eclipse.core.internal.databinding.beans.BeanSetProperty;
import org.eclipse.core.internal.databinding.beans.BeanValueProperty;

/**
 * A factory for creating properties for Java objects that conform to the <a
 * href="http://java.sun.com/products/javabeans/docs/spec.html">JavaBean
 * specification</a> for bound properties.
 * 
 * @since 1.2
 */
public class BeanProperties {
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
		return new BeanValueProperty(BeanPropertyHelper.getPropertyDescriptor(
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
		return new BeanSetProperty(BeanPropertyHelper.getPropertyDescriptor(
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
		return new BeanListProperty(BeanPropertyHelper.getPropertyDescriptor(
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
	 *            the key type for the returned map property
	 * @param valueType
	 *            the value type for the returned map property
	 * @return a map property for the given property name of the given bean
	 *         class.
	 */
	public static IMapProperty map(Class beanClass, String propertyName,
			Class keyType, Class valueType) {
		return new BeanMapProperty(BeanPropertyHelper.getPropertyDescriptor(
				beanClass, propertyName), keyType, valueType);
	}
}
