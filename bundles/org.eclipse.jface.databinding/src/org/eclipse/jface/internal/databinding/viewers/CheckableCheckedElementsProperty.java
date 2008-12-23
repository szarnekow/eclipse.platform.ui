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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.databinding.observable.set.SetDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.IPropertyChangeListener;
import org.eclipse.core.databinding.property.PropertyChangeEvent;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckable;

/**
 * @since 3.3
 * 
 */
public class CheckableCheckedElementsProperty extends ViewerSetProperty {
	private final Object elementType;

	/**
	 * @param elementType
	 */
	public CheckableCheckedElementsProperty(Object elementType) {
		this.elementType = elementType;
	}

	protected Object getElementType() {
		return elementType;
	}

	protected Set doGetSet(Object source) {
		ICheckable checkable = (ICheckable) source;

		Set set = doGetSet(checkable);
		if (set == null) {
			set = createElementSet(checkable);
		}

		return set;
	}

	protected Set doGetSet(ICheckable checkable) {
		return null; // overridden by viewer-specific subclasses
	}

	protected Set createElementSet(ICheckable checkable) {
		return new HashSet();
	}

	protected void doSetSet(Object source, Set set, SetDiff diff) {
		ICheckable checkable = (ICheckable) source;
		for (Iterator it = diff.getAdditions().iterator(); it.hasNext();) {
			checkable.setChecked(it.next(), true);
		}
		for (Iterator it = diff.getRemovals().iterator(); it.hasNext();) {
			checkable.setChecked(it.next(), false);
		}
	}

	public INativePropertyListener adaptListener(
			IPropertyChangeListener listener) {
		return new CheckStateListener(listener);
	}

	public void doAddListener(Object source, INativePropertyListener listener) {
		((ICheckable) source)
				.addCheckStateListener((ICheckStateListener) listener);
	}

	public void doRemoveListener(Object source, INativePropertyListener listener) {
		((ICheckable) source)
				.removeCheckStateListener((ICheckStateListener) listener);
	}

	private class CheckStateListener implements INativePropertyListener,
			ICheckStateListener {
		private IPropertyChangeListener listener;

		private CheckStateListener(IPropertyChangeListener listener) {
			this.listener = listener;
		}

		public void checkStateChanged(CheckStateChangedEvent event) {
			listener.handlePropertyChange(new PropertyChangeEvent(event
					.getSource(), CheckableCheckedElementsProperty.this));
		}
	}

	public String toString() {
		String s = "ICheckable.checkedElements{}"; //$NON-NLS-1$
		if (elementType != null)
			s += " <" + elementType + ">"; //$NON-NLS-1$//$NON-NLS-2$
		return s;
	}
}
