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
import org.eclipse.jface.internal.databinding.viewers.CheckboxTableViewerCheckedElementsProperty;

/**
 * A factory for creating properties of JFace CheckboxTableViewer
 * 
 * @since 1.3
 */
public class CheckboxTableViewerProperties {
	/**
	 * Returns a set property for the checked elements of a JFace
	 * CheckboxTableViewer.
	 * 
	 * @param elementType
	 *            the element type of the returned property
	 * 
	 * @return a set property for the checked elements of a JFace
	 *         CheckboxTableViewer.
	 */
	public static ISetProperty checkedElements(Object elementType) {
		return new CheckboxTableViewerCheckedElementsProperty(elementType);
	}
}
