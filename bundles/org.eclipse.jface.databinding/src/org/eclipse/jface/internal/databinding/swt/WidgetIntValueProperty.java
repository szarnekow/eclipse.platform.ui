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

/**
 * @since 3.3
 * 
 */
public abstract class WidgetIntValueProperty extends WidgetValueProperty {
	WidgetIntValueProperty() {
		super(Integer.TYPE);
	}

	WidgetIntValueProperty(int event) {
		super(event, Integer.TYPE);
	}

	WidgetIntValueProperty(int[] events) {
		super(events, Integer.TYPE);
	}

	public Object getValue(Object source) {
		return new Integer(doGetIntValue(source));
	}

	public void setValue(Object source, Object value) {
		doSetIntValue(source, ((Integer) value).intValue());
	}

	abstract int doGetIntValue(Object source);

	abstract void doSetIntValue(Object source, int intValue);
}
