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

package org.eclipse.jface.internal.databinding.swt;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;

/**
 * @since 3.3
 * 
 */
public class ControlForegroundProperty extends WidgetValueProperty {
	protected Object getValueType() {
		return Color.class;
	}

	public Object getValue(Object source) {
		return ((Control) source).getForeground();
	}

	public boolean setValue(Object source, Object value) {
		if (source == null)
			return false;
		((Control) source).setForeground((Color) value);
		return true;
	}

	public String toString() {
		return "Control.foreground <Color>"; //$NON-NLS-1$
	}
}
