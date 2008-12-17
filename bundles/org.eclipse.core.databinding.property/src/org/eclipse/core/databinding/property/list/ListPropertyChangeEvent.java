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

package org.eclipse.core.databinding.property.list;

import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.property.IPropertyChangeListener;
import org.eclipse.core.databinding.property.PropertyChangeEvent;

/**
 * List change event describing an incremental change of a list property on a
 * particular property source.
 * 
 * @since 1.2
 */
public class ListPropertyChangeEvent extends PropertyChangeEvent {
	private static final long serialVersionUID = 1L;

	/**
	 * The list property that changed
	 */
	public final IListProperty property;

	/**
	 * ListDiff enumerating the added and removed elements in the list, or null
	 * if the change is unknown.
	 */
	public final ListDiff diff;

	/**
	 * Constructs a ListPropertyChangeEvent with the given attributes
	 * 
	 * @param source
	 *            the property source
	 * @param property
	 *            the property that changed on the source
	 * @param diff
	 *            a ListDiff describing the changes to the list property
	 */
	public ListPropertyChangeEvent(Object source, IListProperty property,
			ListDiff diff) {
		super(source);
		this.property = property;
		this.diff = diff;
	}

	protected void dispatch(IPropertyChangeListener listener) {
		((IListPropertyChangeListener) listener).handleListPropertyChange(this);
	}
}
