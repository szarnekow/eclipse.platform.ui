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

import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.value.IValuePropertyChangeListener;
import org.eclipse.core.databinding.property.value.ValuePropertyChangeEvent;
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
public class SelectionProviderSingleSelectionProperty extends
		ViewerValueProperty {
	protected Object getValueType() {
		return null;
	}

	protected Object doGetValue(Object source) {
		ISelection selection = ((ISelectionProvider) source).getSelection();
		if (selection instanceof IStructuredSelection) {
			return ((IStructuredSelection) selection).getFirstElement();
		}
		return null;
	}

	protected void doSetValue(Object source, Object value) {
		((ISelectionProvider) source)
				.setSelection(value == null ? StructuredSelection.EMPTY
						: new StructuredSelection(value));
	}

	public INativePropertyListener adaptListener(
			IValuePropertyChangeListener listener) {
		return new SelectionChangedListener(listener);
	}

	protected void doAddListener(Object source, INativePropertyListener listener) {
		((ISelectionProvider) source)
				.addSelectionChangedListener((ISelectionChangedListener) listener);
	}

	protected void doRemoveListener(Object source,
			INativePropertyListener listener) {
		((ISelectionProvider) source)
				.removeSelectionChangedListener((ISelectionChangedListener) listener);

	}

	private class SelectionChangedListener implements INativePropertyListener,
			ISelectionChangedListener {
		private IValuePropertyChangeListener listener;

		private SelectionChangedListener(IValuePropertyChangeListener listener) {
			this.listener = listener;
		}

		public void selectionChanged(SelectionChangedEvent event) {
			listener.handleValuePropertyChange(new ValuePropertyChangeEvent(
					event.getSource(),
					SelectionProviderSingleSelectionProperty.this));
		}
	}

	public String toString() {
		return "ISelectionProvider.selection"; //$NON-NLS-1$
	}
}
