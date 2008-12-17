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

package org.eclipse.jface.databinding.viewers;

import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.jface.internal.databinding.viewers.ViewerInputProperty;

/**
 * A factory for creating properties of JFace Viewers
 * 
 * @since 1.3
 */
public class ViewerProperties {
	/**
	 * Returns a value property for the input of a JFace Viewer.
	 * 
	 * @return a value property for the input of a JFace Viewer.
	 */
	public static IValueProperty input() {
		return new ViewerInputProperty();
	}
}
