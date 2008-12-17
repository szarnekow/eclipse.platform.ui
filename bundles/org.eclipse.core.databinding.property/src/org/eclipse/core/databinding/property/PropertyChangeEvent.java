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

package org.eclipse.core.databinding.property;

import java.util.EventObject;

/**
 * Base class for change events in the properties API
 * 
 * @since 1.2
 */
public abstract class PropertyChangeEvent extends EventObject {
	private static final long serialVersionUID = 1L;

	protected PropertyChangeEvent(Object source) {
		super(source);
	}

	protected abstract void dispatch(IPropertyChangeListener listener);

	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		return getSource().equals(((PropertyChangeEvent) obj).getSource());
	}

	public int hashCode() {
		return getSource().hashCode();
	}
}
