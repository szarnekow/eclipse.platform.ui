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
import org.eclipse.jface.internal.databinding.swt.CComboItemsProperty;
import org.eclipse.jface.internal.databinding.swt.CComboSelectionProperty;
import org.eclipse.jface.internal.databinding.swt.CComboSingleSelectionIndexProperty;
import org.eclipse.jface.internal.databinding.swt.CComboTextProperty;

/**
 * A factory for creating properties of SWT CCombos
 * 
 * @since 1.3
 */
public class CComboProperties {
	/**
	 * Returns a value property for the selection text of a SWT CCombo.
	 * 
	 * @return a value property for the selection text of a SWT CCombo.
	 */
	public static IValueProperty selection() {
		return new CComboSelectionProperty();
	}

	/**
	 * Returns a value property for the text of a SWT CCombo.
	 * 
	 * @return a value property for the text of a SWT CCombo.
	 */
	public static IValueProperty text() {
		return new CComboTextProperty();
	}

	/**
	 * Returns a list property for the items of a SWT CCombo.
	 * 
	 * @return a list property for the items of a SWT CCombo.
	 */
	public static IListProperty items() {
		return new CComboItemsProperty();
	}

	/**
	 * Returns a value property for the single selection index of a SWT Combo.
	 * 
	 * @return a value property for the single selection index of a SWT Combo.
	 */
	public static IValueProperty singleSelectionIndex() {
		return new CComboSingleSelectionIndexProperty();
	}
}
