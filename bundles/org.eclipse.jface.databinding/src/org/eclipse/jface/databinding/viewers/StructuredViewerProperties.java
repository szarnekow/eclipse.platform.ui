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

import org.eclipse.core.databinding.property.set.ISetProperty;
import org.eclipse.jface.internal.databinding.viewers.StructuredViewerFiltersProperty;

/**
 * A factory for creating properties of JFace StructuredViewers
 * 
 * @since 1.3
 */
public class StructuredViewerProperties {
	/**
	 * Returns a value property for the input of a JFace StructuredViewer.
	 * 
	 * @return a value property for the input of a JFace StructuredViewer.
	 */
	public static ISetProperty filters() {
		return new StructuredViewerFiltersProperty();
	}
}
