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

package org.eclipse.core.databinding.property.value;

import org.eclipse.core.databinding.observable.value.ValueDiff;
import org.eclipse.core.databinding.property.IPropertyChangeListener;
import org.eclipse.core.databinding.property.PropertyChangeEvent;

/**
 * Value change event describing a change of a value property on a particular
 * property source.
 * 
 * @since 1.2
 */
public class ValuePropertyChangeEvent extends PropertyChangeEvent {
	private static final long serialVersionUID = 1L;

	/**
	 * The value property that changed
	 */
	public final SimpleValueProperty property;

	/**
	 * ValueDiff with the old and new values of the property, or null if the
	 * change is unknown.
	 */
	public final ValueDiff diff;

	/**
	 * Constructs a ValuePropertyChangeEvent with the given attributes
	 * 
	 * @param source
	 *            the property source
	 * @param property
	 *            the property that changed on the source
	 * @param diff
	 *            a ValueDiff describing the changes to the value property
	 */
	public ValuePropertyChangeEvent(Object source, SimpleValueProperty property,
			ValueDiff diff) {
		super(source);
		this.property = property;
		this.diff = diff;
	}

	protected void dispatch(IPropertyChangeListener listener) {
		((IValuePropertyChangeListener) listener)
				.handleValuePropertyChange(this);
	}
}
