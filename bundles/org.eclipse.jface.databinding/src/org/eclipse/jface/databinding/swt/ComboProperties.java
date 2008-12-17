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
import org.eclipse.jface.internal.databinding.swt.ComboItemsProperty;
import org.eclipse.jface.internal.databinding.swt.ComboSelectionProperty;
import org.eclipse.jface.internal.databinding.swt.ComboSingleSelectionIndexProperty;
import org.eclipse.jface.internal.databinding.swt.ComboTextProperty;

/**
 * A factory for creating properties of SWT Combos.
 * 
 * @since 1.3
 */
public class ComboProperties {
	/**
	 * Returns a value property for the selection text of a SWT Combo.
	 * 
	 * @return a value property for the selection text of a SWT Combo.
	 */
	public static IValueProperty selection() {
		return new ComboSelectionProperty();
	}

	/**
	 * Returns a value property for the text of a SWT Combo.
	 * 
	 * @return a value property for the text of a SWT Combo.
	 */
	public static IValueProperty text() {
		return new ComboTextProperty();
	}

	/**
	 * Returns a list property for the items of a SWT Combo.
	 * 
	 * @return a list property for the items of a SWT Combo.
	 */
	public static IListProperty items() {
		return new ComboItemsProperty();
	}

	/**
	 * Returns a value property for the single selection index of a SWT Combo.
	 * 
	 * @return a value property for the single selection index of a SWT Combo.
	 */
	public static IValueProperty singleSelectionIndex() {
		return new ComboSingleSelectionIndexProperty();
	}
}
