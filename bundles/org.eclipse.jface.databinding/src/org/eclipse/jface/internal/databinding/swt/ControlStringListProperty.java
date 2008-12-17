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

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.list.IListPropertyChangeListener;
import org.eclipse.core.databinding.property.list.SimpleListProperty;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

/**
 * @since 3.3
 * 
 */
public abstract class ControlStringListProperty extends SimpleListProperty {
	protected ControlStringListProperty() {
		super(String.class);
	}

	public IObservableList observeList(Object source) {
		Widget widget = (Widget) source;
		return observeList(SWTObservables.getRealm(widget.getDisplay()), widget);
	}

	protected void setList(Object source, List list, ListDiff diff) {
		String[] strings = (String[]) list.toArray(new String[list.size()]);
		doSetStringList((Control) source, strings);
	}

	abstract void doSetStringList(Control control, String[] list);

	protected List doGetList(Object source) {
		String[] list = doGetStringList((Control) source);
		return Arrays.asList(list);
	}

	abstract String[] doGetStringList(Control control);

	public INativePropertyListener adaptListener(
			IListPropertyChangeListener listener) {
		return null;
	}

	public void addListener(Object source, INativePropertyListener listener) {
	}

	public void removeListener(Object source, INativePropertyListener listener) {
	}
}
