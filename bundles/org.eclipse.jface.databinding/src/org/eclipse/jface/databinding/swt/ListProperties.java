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

package org.eclipse.jface.databinding.swt;

import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.jface.internal.databinding.swt.ListItemsProperty;
import org.eclipse.jface.internal.databinding.swt.ListSelectionProperty;
import org.eclipse.jface.internal.databinding.swt.ListSingleSelectionIndexProperty;

/**
 * A factory for creating properties of SWT List controls.
 * 
 * @since 1.3
 */
public class ListProperties {
	/**
	 * Returns a value property for observing the selection text of a SWT List.
	 * 
	 * @return a value property for observing the selection text of a SWT List.
	 */
	public static IValueProperty selection() {
		return new ListSelectionProperty();
	}

	/**
	 * Returns a list property for observing the items in a SWT List.
	 * 
	 * @return a list property for observing the items in a SWT List.
	 */
	public static IListProperty items() {
		return new ListItemsProperty();
	}

	/**
	 * Returns a value property for the single selection index of a SWT List.
	 * 
	 * @return a value property for the single selection index of a SWT List.
	 */
	public static IValueProperty singleSelectionIndex() {
		return new ListSingleSelectionIndexProperty();
	}
}
