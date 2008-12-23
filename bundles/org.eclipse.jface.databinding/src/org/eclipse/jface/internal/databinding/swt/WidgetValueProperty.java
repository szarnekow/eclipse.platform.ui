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

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.IPropertyChangeListener;
import org.eclipse.core.databinding.property.PropertyChangeEvent;
import org.eclipse.core.databinding.property.value.SimpleValueProperty;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

abstract class WidgetValueProperty extends SimpleValueProperty {
	private int[] events;

	WidgetValueProperty() {
		this(null);
	}

	WidgetValueProperty(int event) {
		this(new int[] { event });
	}

	WidgetValueProperty(int[] events) {
		this.events = events;
	}

	protected Realm getPreferredRealm(Object source) {
		if (source instanceof Widget) {
			return SWTObservables.getRealm(((Widget) source).getDisplay());
		}
		return super.getPreferredRealm(source);
	}

	protected INativePropertyListener adaptListener(
			IPropertyChangeListener listener) {
		return new WidgetListener(listener);
	}

	protected void doAddListener(Object source, INativePropertyListener listener) {
		if (events != null) {
			for (int i = 0; i < events.length; i++) {
				int event = events[i];
				if (event != SWT.None) {
					((Widget) source).addListener(event, (Listener) listener);
				}
			}
		}
	}

	protected void doRemoveListener(Object source,
			INativePropertyListener listener) {
		if (events != null) {
			Widget widget = (Widget) source;
			if (!widget.isDisposed()) {
				for (int i = 0; i < events.length; i++) {
					int event = events[i];
					if (event != SWT.None)
						widget.removeListener(event, (Listener) listener);
				}
			}
		}
	}

	private class WidgetListener implements INativePropertyListener, Listener {
		private final IPropertyChangeListener listener;

		protected WidgetListener(IPropertyChangeListener listener) {
			this.listener = listener;
		}

		public void handleEvent(Event event) {
			listener.handlePropertyChange(new PropertyChangeEvent(event.widget,
					WidgetValueProperty.this));
		}
	}
}
