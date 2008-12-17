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
import org.eclipse.jface.internal.databinding.swt.StyledTextTextProperty;
import org.eclipse.swt.SWT;

/**
 * A factory for creating properties of SWT StyledTexts.
 * 
 * @since 1.3
 */
public class StyledTextProperties {
	/**
	 * Returns a value property for the text of a SWT StyledText.
	 * 
	 * @param event
	 *            the SWT event type to register for change events. May be
	 *            {@link SWT#None}, {@link SWT#Modify} or {@link SWT#FocusOut}.
	 * 
	 * @return a value property for the text of a SWT StyledText.
	 */
	public static IValueProperty text(int event) {
		return new StyledTextTextProperty(event);
	}
}
