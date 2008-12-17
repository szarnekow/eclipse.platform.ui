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

import org.eclipse.core.databinding.observable.set.SetDiff;
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
	public final ISetProperty property;

	/**
	 * SetDiff enumerating the added and removed elements in the set, or null if
	 * the change is unknown.
	 */
	public final SetDiff diff;

	/**
	 * Constructs a SetPropertyChangeEvent with the given attributes
	 * 
	 * @param source
	 *            the property source
	 * @param property
	 *            the property that changed on the source
	 * @param diff
	 *            a SetDiff describing the changes to the set property
	 */
	public SetPropertyChangeEvent(Object source, ISetProperty property,
			SetDiff diff) {
		super(source);
		this.property = property;
		this.diff = diff;
	}

	protected void dispatch(IPropertyChangeListener listener) {
		((ISetPropertyChangeListener) listener).handleSetPropertyChange(this);
	}
}
