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

package org.eclipse.core.databinding.property.map;

import org.eclipse.core.databinding.property.IPropertyChangeListener;

/**
 * Listener for changes to map properties on a property source
 * 
 * @since 1.2
 */
public interface IMapPropertyChangeListener extends IPropertyChangeListener {
	/**
	 * Handle a change to a map property on a specific property source.
	 * 
	 * @param event
	 *            an event describing the map change that occured.
	 */
	public void handleMapPropertyChange(MapPropertyChangeEvent event);
}
