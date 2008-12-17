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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.SetDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.set.ISetPropertyChangeListener;
import org.eclipse.core.databinding.property.set.SimpleSetProperty;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * @since 3.3
 * 
 */
public class StructuredViewerFiltersProperty extends SimpleSetProperty {
	/**
	 * 
	 */
	public StructuredViewerFiltersProperty() {
		super(ViewerFilter.class);
	}

	public IObservableSet observeSet(Object source) {
		Viewer viewer = (Viewer) source;
		return observeSet(SWTObservables.getRealm(viewer.getControl()
				.getDisplay()), viewer);
	}

	protected Set doGetSet(Object source) {
		return new HashSet(Arrays.asList(((StructuredViewer) source)
				.getFilters()));
	}

	public void setSet(Object source, Set set, SetDiff diff) {
		StructuredViewer viewer = (StructuredViewer) source;
		viewer.getControl().setRedraw(false);
		try {
			viewer.setFilters((ViewerFilter[]) set.toArray(new ViewerFilter[set
					.size()]));
		} finally {
			viewer.getControl().setRedraw(true);
		}
	}

	public INativePropertyListener adaptListener(
			ISetPropertyChangeListener listener) {
		return null;
	}

	public void addListener(Object source, INativePropertyListener listener) {
	}

	public void removeListener(Object source, INativePropertyListener listener) {
	}

	public String toString() {
		return "StructuredViewer.filters{} <ViewerFilter>"; //$NON-NLS-1$
	}
}
