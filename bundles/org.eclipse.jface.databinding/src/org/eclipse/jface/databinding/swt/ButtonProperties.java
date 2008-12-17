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
import org.eclipse.jface.internal.databinding.swt.ButtonSelectionProperty;

/**
 * A factory for creating properties of SWT Buttons.
 * 
 * @since 1.3
 */
public class ButtonProperties {
	/**
	 * Returns a value property for the selection state of a SWT Button.
	 * 
	 * @return a value property for the selection state of a SWT Button.
	 */
	public static IValueProperty selection() {
		return new ButtonSelectionProperty();
	}
}
