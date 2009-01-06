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

package org.eclipse.core.databinding.property;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.core.databinding.property.map.IMapProperty;
import org.eclipse.core.databinding.property.set.ISetProperty;
import org.eclipse.core.databinding.property.value.IValueProperty;

/**
 * Abstract IProperty implementation
 * 
 * @since 1.2
 */
public abstract class Property implements IProperty {
	/**
	 * Returns the preferred realm to use when observing the specified property
	 * source. This method is used to determine the default realm for methods
	 * lacking an explicit or implicit realm.
	 * <p>
	 * The default implementation of this method returns <code>null</code>
	 * (indicating no preference). Subclasses may override this method to
	 * return an appropriate realm.
	 * 
	 * @param source
	 *            the property source
	 * @return the preferred realm to use when observing the specified property
	 *         source, or null if the source object has no implicit preference.
	 * 
	 * @see IValueProperty#observe(Object)
	 * @see IListProperty#observe(Object)
	 * @see ISetProperty#observe(Object)
	 * @see IMapProperty#observe(Object)
	 */
	protected Realm getPreferredRealm(Object source) {
		return null;
	}
}
