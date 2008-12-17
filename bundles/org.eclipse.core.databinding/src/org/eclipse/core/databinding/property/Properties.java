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

package org.eclipse.core.databinding.property;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.core.databinding.property.map.IMapProperty;
import org.eclipse.core.databinding.property.set.ISetProperty;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.core.internal.databinding.property.ListPropertyDetailValuesList;
import org.eclipse.core.internal.databinding.property.MapPropertyDetailValuesMap;
import org.eclipse.core.internal.databinding.property.SetPropertyDetailValuesMap;
import org.eclipse.core.internal.databinding.property.ValuePropertyDetailList;
import org.eclipse.core.internal.databinding.property.ValuePropertyDetailMap;
import org.eclipse.core.internal.databinding.property.ValuePropertyDetailSet;
import org.eclipse.core.internal.databinding.property.ValuePropertyDetailValue;

/**
 * A factory for chaining properties together to create nested properties.
 * <p>
 * Example: Suppose class <code>A</code> has a property <code>b</code> of type
 * <code>B</code>, and that class <code>B</code> has a property <code>c</code>
 * of type <code>C</code>:
 * 
 * <pre>
 * A a = new A();
 * B b = a.getB();
 * IValueProperty ab = BeanProperties.valueProperty(A.class, &quot;b&quot;);
 * assertTrue(ab.getValue(a) == b);
 * 
 * IValueProperty bc = BeanProperties.valueProperty(B.class, &quot;c&quot;);
 * C c = b.getC();
 * assertTrue(bc.getValue(b) == c);
 * </pre>
 * 
 * Using Properties, the <code>ab</code> and <code>bc</code> properties may be
 * combined to form a nested <code>abc</code> property:
 * 
 * <pre>
 * IValueProperty abc = Properties.detailValue(ab, bc)
 * assertTrue(abc.getValue(a) == c);
 * </pre>
 * 
 * @since 1.2
 */
public class Properties {
	// Properties of IValueProperty properties

	/**
	 * Returns the nested combination of the master value and detail value
	 * properties. Value modifications made through the returned property are
	 * delegated to the detail property, using the value of the master property
	 * as the source.
	 * 
	 * @param masterValue
	 *            the master property
	 * @param detailValue
	 *            the detail property
	 * @return the nested combination of the master and detail properties
	 */
	public static IValueProperty detailValue(IValueProperty masterValue,
			IValueProperty detailValue) {
		return new ValuePropertyDetailValue(masterValue, detailValue);
	}

	/**
	 * Returns the nested combination of the master value and detail list
	 * properties. List modifications made through the returned property are
	 * delegated to the detail property, using the value of the master property
	 * as the source.
	 * 
	 * @param masterValue
	 *            the master property
	 * @param detailList
	 *            the detail property
	 * @return the nested combination of the master value and detail list
	 *         properties
	 */
	public static IListProperty detailList(IValueProperty masterValue,
			IListProperty detailList) {
		return new ValuePropertyDetailList(masterValue, detailList);
	}

	/**
	 * Returns the nested combination of the master value and detail set
	 * properties. Set modifications made through the returned property are
	 * delegated to the detail property, using the value of the master property
	 * as the source.
	 * 
	 * @param masterValue
	 *            the master property
	 * @param detailSet
	 *            the detail property
	 * @return the nested combination of the master value and detail set
	 *         properties
	 */
	public static ISetProperty detailSet(IValueProperty masterValue,
			ISetProperty detailSet) {
		return new ValuePropertyDetailSet(masterValue, detailSet);
	}

	/**
	 * Returns the nested combination of the master value and detail map
	 * properties. Map modifications made through the returned property are
	 * delegated to the detail property, using the value of the master property
	 * as the source.
	 * 
	 * @param masterValue
	 *            the master property
	 * @param detailMap
	 *            the detail property
	 * @return the nested combination of the master value and detial map
	 *         properties
	 */
	public static IMapProperty detailMap(IValueProperty masterValue,
			IMapProperty detailMap) {
		return new ValuePropertyDetailMap(masterValue, detailMap);
	}

	// Properties of IListProperty master properties

	/**
	 * Returns the nested combination of the master list and detail value
	 * properties. Note that because this property is a projection of value
	 * properties over a list, the only modification supported is through the
	 * {@link IObservableList#set(int, Object)} method. Modifications made
	 * through the returned property are delegated to the detail property, using
	 * the corresponding list element from the master property as the source.
	 * 
	 * @param masterList
	 *            the master property
	 * @param detailValue
	 *            the detail property
	 * @return the nested combination of the master list and detail value
	 *         properties
	 */
	public static IListProperty detailValues(IListProperty masterList,
			IValueProperty detailValue) {
		return new ListPropertyDetailValuesList(masterList, detailValue);
	}

	// Properties of ISetProperty master properties

	/**
	 * Returns the nested combination of the master set and detail value
	 * properties. Note that because this property is a projection of value
	 * properties over a set, the only modifications supported are through the
	 * {@link IObservableMap#put(Object, Object)} and
	 * {@link IObservableMap#putAll(java.util.Map)} methods. In the latter case,
	 * this property does not put entries for keys not already in the master key
	 * set. Modifications made through the returned property are delegated to
	 * the detail property, using the corresponding set element from the master
	 * property as the source.
	 * 
	 * @param masterKeySet
	 *            the master property
	 * @param detailValues
	 *            the detail property
	 * @return the nested combination of the master set and detail value
	 *         properties
	 */
	public static IMapProperty detailValues(ISetProperty masterKeySet,
			IValueProperty detailValues) {
		return new SetPropertyDetailValuesMap(masterKeySet, detailValues);
	}

	// Properties of IMapProperty master properties

	/**
	 * Returns the nested combination of the master map and detail value
	 * properties. Note that because this property is a projection of value
	 * properties over a values collection, the only modifications supported are
	 * through the {@link IObservableMap#put(Object, Object)} and
	 * {@link IObservableMap#putAll(java.util.Map)} methods. In the latter case,
	 * this property does not entries for keys not already contained in the
	 * master map's key set. Modifications made through the returned property
	 * are delegated to the detail property, using the corresponding entry value
	 * from the master property as the source.
	 * 
	 * @param masterMap
	 *            the master property
	 * @param detailValues
	 *            the detail property
	 * @return the nested combination of the master map and detail value
	 *         properties.
	 */
	public static IMapProperty detailValues(IMapProperty masterMap,
			IValueProperty detailValues) {
		return new MapPropertyDetailValuesMap(masterMap, detailValues);
	}
}
