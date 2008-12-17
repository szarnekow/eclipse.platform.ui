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

package org.eclipse.jface.internal.databinding.viewers;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.value.IValuePropertyChangeListener;
import org.eclipse.core.databinding.property.value.SimpleValueProperty;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.Viewer;

/**
 * @since 3.3
 * 
 */
public class ViewerInputProperty extends SimpleValueProperty {
	/**
	 * 
	 */
	public ViewerInputProperty() {
		super(null);
	}

	public IObservableValue observeValue(Object source) {
		Viewer viewer = (Viewer) source;
		return observeValue(SWTObservables.getRealm(viewer.getControl()
				.getDisplay()), viewer);
	}

	public Object getValue(Object source) {
		return ((Viewer) source).getInput();
	}

	public void setValue(Object source, Object value) {
		((Viewer) source).setInput(value);
	}

	public INativePropertyListener adaptListener(
			IValuePropertyChangeListener listener) {
		return null;
	}

	public void addListener(Object source, INativePropertyListener listener) {
	}

	public void removeListener(Object source, INativePropertyListener listener) {
	}

	public String toString() {
		return "Viewer.input"; //$NON-NLS-1$
	}
}
