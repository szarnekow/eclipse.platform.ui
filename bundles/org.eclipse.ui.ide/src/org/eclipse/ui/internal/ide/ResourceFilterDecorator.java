/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Serge Beauchamp (Freescale Semiconductor) - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.ide;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResourceFilter;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

/**
 * Lightweight decorator for filtered container.
 * 
 * @since 3.4
 * 
 */
public class ResourceFilterDecorator implements ILightweightLabelDecorator {

	private static final String IMG_MARKERS_RESOURCE_FILTER_DECORATION_PATH = "markers/filter_ovr.gif"; //$NON-NLS-1$
	ImageDescriptor descriptorImage = null;
	
	/**
	 * 
	 */
	public ResourceFilterDecorator() {
		descriptorImage = IDEWorkbenchPlugin
		.getIDEImageDescriptor(IMG_MARKERS_RESOURCE_FILTER_DECORATION_PATH);
	}
	
	public void decorate(Object element, IDecoration decoration) {

		if (element instanceof IContainer == false) {
			return;
		}
		IContainer container = (IContainer) element;
		IResourceFilter[] filters = null;
		try {
			filters = container.getFilters();
			if ((filters.length > 0) && (descriptorImage != null))
				decoration.addOverlay(descriptorImage);
		} catch (CoreException e) {
		}
	}

	public void addListener(ILabelProviderListener listener) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
	}

}