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

import java.util.Collections;
import java.util.List;

import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.list.IListPropertyChangeListener;
import org.eclipse.core.databinding.property.list.ListPropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * @since 3.3
 * 
 */
public class SelectionProviderMultipleSelectionProperty extends
		ViewerListProperty {
	protected Object getElementType() {
		return Object.class;
	}

	protected List doGetList(Object source) {
		ISelection selection = ((ISelectionProvider) source).getSelection();
		if (selection instanceof IStructuredSelection) {
			return ((IStructuredSelection) selection).toList();
		}
		return Collections.EMPTY_LIST;
	}

	protected void doSetList(Object source, List list, ListDiff diff) {
		((ISelectionProvider) source)
				.setSelection(new StructuredSelection(list));
	}

	public INativePropertyListener adaptListener(
			IListPropertyChangeListener listener) {
		return new SelectionChangedListener(listener);
	}

	public void doAddListener(Object source, INativePropertyListener listener) {
		((ISelectionProvider) source)
				.addSelectionChangedListener((ISelectionChangedListener) listener);
	}

	public void doRemoveListener(Object source, INativePropertyListener listener) {
		((ISelectionProvider) source)
				.removeSelectionChangedListener((ISelectionChangedListener) listener);

	}

	private class SelectionChangedListener implements INativePropertyListener,
			ISelectionChangedListener {
		private IListPropertyChangeListener listener;

		private SelectionChangedListener(IListPropertyChangeListener listener) {
			this.listener = listener;
		}

		public void selectionChanged(SelectionChangedEvent event) {
			listener.handleListPropertyChange(new ListPropertyChangeEvent(event
					.getSource(),
					SelectionProviderMultipleSelectionProperty.this));
		}
	}

	public String toString() {
		return "ISelectionProvider.selection[]"; //$NON-NLS-1$
	}
}
