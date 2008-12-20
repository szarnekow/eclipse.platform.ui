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
		super();
	}

	WidgetIntValueProperty(int event) {
		super(event);
	}

	WidgetIntValueProperty(int[] events) {
		super(events);
	}

	protected Object getValueType() {
		return Integer.TYPE;
	}

	public Object getValue(Object source) {
		return new Integer(doGetIntValue(source));
	}

	public boolean setValue(Object source, Object value) {
		if (source == null)
			return false;
		doSetIntValue(source, ((Integer) value).intValue());
		return true;
	}

	abstract int doGetIntValue(Object source);

	abstract void doSetIntValue(Object source, int intValue);
}
