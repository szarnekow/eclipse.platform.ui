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

import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.jface.internal.databinding.swt.SpinnerMaximumProperty;
import org.eclipse.jface.internal.databinding.swt.SpinnerMinimumProperty;
import org.eclipse.jface.internal.databinding.swt.SpinnerSelectionProperty;

/**
 * A factory for creating properties of SWT Spinners.
 * 
 * @since 1.3
 */
public class SpinnerProperties {
	/**
	 * Returns a value property for the selected value of a SWT Spinner.
	 * 
	 * @return a value property for the selected value of a SWT Spinner.
	 */
	public static IValueProperty selection() {
		return new SpinnerSelectionProperty();
	}

	/**
	 * Returns a value property for the minimum value of a SWT Spinner.
	 * 
	 * @return a value property for the minimum value of a SWT Spinner.
	 */
	public static IValueProperty minimum() {
		return new SpinnerMinimumProperty();
	}

	/**
	 * Returns a value property for the maximum value of a SWT Spinner.
	 * 
	 * @return a value property for the maximum value of a SWT Spinner.
	 */
	public static IValueProperty maximum() {
		return new SpinnerMaximumProperty();
	}
}
