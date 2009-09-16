/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.workbench.ui.renderers.swt;

import org.eclipse.e4.core.services.IDisposable;
import org.eclipse.e4.core.services.context.IEclipseContext;
import org.eclipse.e4.ui.model.application.MContributedPart;
import org.eclipse.e4.ui.model.application.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

/**
 * Create a contribute part.
 */
public class ContributedPartRenderer extends SWTPartRenderer {

	public Object createWidget(final MPart<?> part, Object parent) {
		if (!(parent instanceof Composite))
			return null;

		Widget parentWidget = (Widget) parent;
		IEclipseContext parentContext = getContextForParent(part);
		Widget newWidget = null;

		if (part instanceof MContributedPart<?>) {
			final Composite newComposite = new Composite(
					(Composite) parentWidget, SWT.NONE);
			newWidget = newComposite;
			bindWidget(part, newWidget);
			final MContributedPart<?> contributedPart = (MContributedPart<?>) part;
			final IEclipseContext localContext = part.getContext();
			localContext.set(Composite.class.getName(), newComposite);
			parentContext.set(IServiceConstants.ACTIVE_CHILD, localContext);
			Object newPart = contributionFactory.create(contributedPart
					.getURI(), localContext);
			contributedPart.setObject(newPart);
			newWidget.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					if (localContext instanceof IDisposable)
						((IDisposable) localContext).dispose();
				}
			});
		}

		return newWidget;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.workbench.ui.renderers.swt.PartFactory#hookControllerLogic
	 * (org.eclipse.e4.ui.model.application.MPart)
	 */
	@Override
	public void hookControllerLogic(final MPart<?> me) {
		// TODO Auto-generated method stub
		super.hookControllerLogic(me);
		Widget widget = (Widget) me.getWidget();
		if (widget instanceof Composite) {
			((Composite) widget).addListener(SWT.Activate, new Listener() {
				public void handleEvent(Event event) {
					activate(me);
				}
			});
		}
	}

}