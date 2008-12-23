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

package org.eclipse.core.databinding.property.set;

import org.eclipse.core.databinding.property.IPropertyChangeListener;
import org.eclipse.core.databinding.property.PropertyChangeEvent;

/**
 * Set change event describing an incremental change of a set property on a
 * particular property source.
 * 
 * @since 1.2
 */
public class SetPropertyChangeEvent extends PropertyChangeEvent {
	private static final long serialVersionUID = 1L;

	/**
	 * The set property that changed
	 */
	public final SimpleSetProperty property;

	/**
	 * Constructs a SetPropertyChangeEvent with the given attributes
	 * 
	 * @param source
	 *            the property source
	 * @param property
	 *            the property that changed on the source
	 */
	public SetPropertyChangeEvent(Object source, SimpleSetProperty property) {
		super(source);
		this.property = property;
	}

	protected void dispatch(IPropertyChangeListener listener) {
		((ISetPropertyChangeListener) listener).handleSetPropertyChange(this);
	}
}
