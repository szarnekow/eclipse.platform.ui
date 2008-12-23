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

package org.eclipse.core.databinding.property.map;

import org.eclipse.core.databinding.property.IPropertyChangeListener;
import org.eclipse.core.databinding.property.PropertyChangeEvent;

/**
 * Map change event describing an incremental change of a map property on a
 * particular property source.
 * 
 * @since 1.2
 */
public class MapPropertyChangeEvent extends PropertyChangeEvent {
	private static final long serialVersionUID = 1L;

	/**
	 * The map property that changed
	 */
	public final SimpleMapProperty property;

	/**
	 * Constructs a MapPropertyChangeEvent with the given attributes
	 * 
	 * @param source
	 *            the property source
	 * @param property
	 *            the property that changed on the source
	 */
	public MapPropertyChangeEvent(Object source, SimpleMapProperty property) {
		super(source);
		this.property = property;
	}

	protected void dispatch(IPropertyChangeListener listener) {
		((IMapPropertyChangeListener) listener).handleMapPropertyChange(this);
	}
}
