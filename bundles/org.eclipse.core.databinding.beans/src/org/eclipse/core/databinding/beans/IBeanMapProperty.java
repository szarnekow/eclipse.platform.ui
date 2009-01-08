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

package org.eclipse.core.databinding.beans;

import java.util.Map;

import org.eclipse.core.databinding.property.map.IMapProperty;

/**
 * @since 1.2
 * 
 */
public interface IBeanMapProperty extends IBeanProperty, IMapProperty {
	/**
	 * Returns a master-detail combination of this property and the specified
	 * value property.
	 * 
	 * @param propertyName
	 *            the value property to observe.
	 * @return a master-detail combination of this property and the specified
	 *         value property.
	 * @see #values(IBeanValueProperty)
	 */
	public IBeanMapProperty values(String propertyName);

	/**
	 * Returns a master-detail combination of this property and the specified
	 * value property.
	 * 
	 * @param propertyName
	 *            the value property to observe.
	 * @param valueType
	 *            the value type of the named property
	 * @return a master-detail combination of this property and the specified
	 *         value property.
	 * @see #values(IBeanValueProperty)
	 */
	public IBeanMapProperty values(String propertyName, Class valueType);

	/**
	 * Returns a master-detail combination of this property and the specified
	 * value property.
	 * 
	 * @param beanClass
	 *            the class of values in this map property
	 * @param propertyName
	 *            the value property to observe
	 * @return a master-detail combination of this property and the specified
	 *         value property.
	 * @see #values(IBeanValueProperty)
	 */
	public IBeanMapProperty values(Class beanClass, String propertyName);

	/**
	 * Returns a master-detail combination of this property and the specified
	 * value property.
	 * 
	 * @param beanClass
	 *            the class of values in this map property
	 * @param propertyName
	 *            the value property to observe
	 * @param valueType
	 *            the value type of the named property
	 * @return a master-detail combination of this property and the specified
	 *         value property.
	 * @see #values(IBeanValueProperty)
	 */
	public IBeanMapProperty values(Class beanClass, String propertyName,
			Class valueType);

	/**
	 * Returns a master-detail combination of this property and the specified
	 * value property. The returned property will observe the specified value
	 * property for all {@link Map#values() values} observed by this map
	 * property, mapping from this map property's {@link Map#keySet() key set}
	 * to the specified value property's value for each element in the master
	 * property's {@link Map#values() values} collection.
	 * 
	 * @param property
	 *            the detail property to observe
	 * @return a master-detail combination of this property and the specified
	 *         value property.
	 */
	public IBeanMapProperty values(IBeanValueProperty property);
}
