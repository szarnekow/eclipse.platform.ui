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

import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.jface.internal.databinding.viewers.SelectionProviderMultipleSelectionProperty;
import org.eclipse.jface.internal.databinding.viewers.SelectionProviderSingleSelectionProperty;

/**
 * A factory for creating properties of JFace ISelectionProviders
 * 
 * @since 1.3
 */
public class SelectionProviderProperties {
	/**
	 * Returns a value property for the single selection of a JFace
	 * ISelectionProvider.
	 * 
	 * @return a value property for the single selection of a JFace
	 *         ISelectionProvider.
	 */
	public static IValueProperty singleSelection() {
		return new SelectionProviderSingleSelectionProperty();
	}

	/**
	 * Returns a list property for the multiple selection of a JFace
	 * ISelectionProvider.
	 * 
	 * @return a list property for the multiple selection of a JFace
	 *         ISelectionProvider.
	 */
	public static IListProperty multipleSelection() {
		return new SelectionProviderMultipleSelectionProperty();
	}
}
