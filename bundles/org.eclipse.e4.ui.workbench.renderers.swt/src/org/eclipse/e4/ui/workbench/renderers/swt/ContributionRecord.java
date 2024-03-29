/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.e4.ui.workbench.renderers.swt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.expressions.ExpressionInfo;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.internal.workbench.ContributionsAnalyzer;
import org.eclipse.e4.ui.model.application.ui.MCoreExpression;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuContribution;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuSeparator;
import org.eclipse.e4.ui.workbench.modeling.ExpressionContext;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.action.MenuManager;

public class ContributionRecord {
	public static final String FACTORY = "ContributionFactory"; //$NON-NLS-1$
	static final String STATIC_CONTEXT = "ContributionFactoryContext"; //$NON-NLS-1$

	MMenu menuModel;
	MMenuContribution menuContribution;
	ArrayList<MMenuElement> generatedElements = new ArrayList<MMenuElement>();
	HashSet<MMenuElement> sharedElements = new HashSet<MMenuElement>();
	MenuManagerRenderer renderer;
	boolean isVisible = true;
	private IEclipseContext infoContext;
	private Runnable factoryDispose;

	public ContributionRecord(MMenu menuModel, MMenuContribution contribution,
			MenuManagerRenderer renderer) {
		this.menuModel = menuModel;
		this.menuContribution = contribution;
		this.renderer = renderer;
	}

	public MenuManager getManagerForModel() {
		return renderer.getManager(menuModel);
	}

	public MMenuContribution getMenuContribution() {
		return menuContribution;
	}

	/**
	 * Access to analyze for tests. For Looking, not touching!
	 * 
	 * @return the shared elements collection
	 */
	public Collection<MMenuElement> getSharedElements() {
		return sharedElements;
	}

	/**
	 * Access to analyze for tests. For Looking, not touching!
	 * 
	 * @return the generated elements collection
	 */
	public Collection<MMenuElement> getGeneratedElements() {
		return generatedElements;
	}

	/**
	 * @param context
	 */
	public void updateVisibility(IEclipseContext context) {
		ExpressionContext exprContext = new ExpressionContext(context);
		updateIsVisible(exprContext);
		HashSet<ContributionRecord> recentlyUpdated = new HashSet<ContributionRecord>();
		recentlyUpdated.add(this);
		for (MMenuElement item : generatedElements) {
			boolean currentVisibility = computeVisibility(recentlyUpdated,
					item, exprContext);
			item.setVisible(currentVisibility);
		}
		for (MMenuElement item : sharedElements) {
			boolean currentVisibility = computeVisibility(recentlyUpdated,
					item, exprContext);
			item.setVisible(currentVisibility);
		}
		getManagerForModel().markDirty();
	}

	public void collectInfo(ExpressionInfo info) {
		ContributionsAnalyzer.collectInfo(info,
				menuContribution.getVisibleWhen());
		for (MMenuElement item : generatedElements) {
			ContributionsAnalyzer.collectInfo(info, item.getVisibleWhen());
		}
		for (MMenuElement item : sharedElements) {
			ContributionsAnalyzer.collectInfo(info, item.getVisibleWhen());
		}
	}

	public void updateIsVisible(ExpressionContext exprContext) {
		isVisible = ContributionsAnalyzer.isVisible(menuContribution,
				exprContext);
	}

	public boolean computeVisibility(
			HashSet<ContributionRecord> recentlyUpdated, MMenuElement item,
			ExpressionContext exprContext) {
		boolean currentVisibility = isVisible;
		if (item instanceof MMenu || item instanceof MMenuSeparator) {
			ArrayList<ContributionRecord> list = renderer.getList(item);
			if (list != null) {
				Iterator<ContributionRecord> cr = list.iterator();
				while (!currentVisibility && cr.hasNext()) {
					ContributionRecord rec = cr.next();
					if (!recentlyUpdated.contains(rec)) {
						rec.updateIsVisible(exprContext);
						recentlyUpdated.add(rec);
					}
					currentVisibility |= rec.isVisible;
				}
			}
		}
		if (currentVisibility
				&& item.getVisibleWhen() instanceof MCoreExpression) {
			boolean val = ContributionsAnalyzer.isVisible(
					(MCoreExpression) item.getVisibleWhen(), exprContext);
			currentVisibility = val;
		}
		return currentVisibility;
	}

	public boolean mergeIntoModel() {
		int idx = getIndex(menuModel, menuContribution.getPositionInParent());
		if (idx == -1) {
			return false;
		}

		final List<MMenuElement> copyElements;
		if (menuContribution.getTransientData().get(FACTORY) != null) {
			copyElements = mergeFactoryIntoModel();
		} else {
			copyElements = new ArrayList<MMenuElement>();
			for (MMenuElement item : menuContribution.getChildren()) {
				MMenuElement copy = (MMenuElement) EcoreUtil
						.copy((EObject) item);
				copyElements.add(copy);
			}
		}

		for (MMenuElement copy : copyElements) {
			if (copy instanceof MMenu) {
				MMenu shared = findExistingMenu(copy.getElementId());
				if (shared == null) {
					shared = (MMenu) copy;
					renderer.linkElementToContributionRecord(copy, this);
					menuModel.getChildren().add(idx++, copy);
				} else {
					copy = shared;
				}
				sharedElements.add(shared);
			} else if (copy instanceof MMenuSeparator) {
				MMenuSeparator shared = findExistingSeparator(copy
						.getElementId());
				if (shared == null) {
					shared = (MMenuSeparator) copy;
					renderer.linkElementToContributionRecord(copy, this);
					menuModel.getChildren().add(idx++, copy);
				} else {
					copy = shared;
				}
				sharedElements.add(shared);
			} else {
				generatedElements.add(copy);
				renderer.linkElementToContributionRecord(copy, this);
				menuModel.getChildren().add(idx++, copy);
			}
			if (copy instanceof MMenu || copy instanceof MMenuSeparator) {
				ArrayList<ContributionRecord> array = renderer.getList(copy);
				array.add(this);
			}
		}
		return true;
	}

	/**
	 * @return
	 */
	private List<MMenuElement> mergeFactoryIntoModel() {
		Object obj = menuContribution.getTransientData().get(FACTORY);
		if (!(obj instanceof IContextFunction)) {
			return Collections.EMPTY_LIST;
		}
		IEclipseContext staticContext = getStaticContext();
		staticContext.remove(List.class);
		factoryDispose = (Runnable) ((IContextFunction) obj)
				.compute(staticContext);
		return staticContext.get(List.class);
	}

	private IEclipseContext getStaticContext() {
		if (infoContext == null) {
			IEclipseContext parentContext = renderer.getContext(menuModel);
			if (parentContext != null) {
				infoContext = parentContext.createChild(STATIC_CONTEXT);
			} else {
				infoContext = EclipseContextFactory.create(STATIC_CONTEXT);
			}
			ContributionsAnalyzer.populateModelInterfaces(menuModel,
					infoContext, menuModel.getClass().getInterfaces());
			infoContext.set(MenuManagerRenderer.class, renderer);
		}
		return infoContext;
	}

	MMenu findExistingMenu(String id) {
		if (id == null) {
			return null;
		}
		for (MMenuElement item : menuModel.getChildren()) {
			if (item instanceof MMenu && id.equals(item.getElementId())) {
				return (MMenu) item;
			}
		}
		return null;
	}

	MMenuSeparator findExistingSeparator(String id) {
		if (id == null) {
			return null;
		}
		for (MMenuElement item : menuModel.getChildren()) {
			if (item instanceof MMenuSeparator
					&& id.equals(item.getElementId())) {
				return (MMenuSeparator) item;
			}
		}
		return null;
	}

	public void dispose() {
		for (MMenuElement copy : generatedElements) {
			menuModel.getChildren().remove(copy);
		}
		for (MMenuElement shared : sharedElements) {
			ArrayList<ContributionRecord> array = renderer.getList(shared);
			array.remove(this);
			if (array.isEmpty()) {
				menuModel.getChildren().remove(shared);
			}
		}
		if (factoryDispose != null) {
			factoryDispose.run();
			factoryDispose = null;
		}
	}

	private static int getIndex(MElementContainer<?> menuModel,
			String positionInParent) {
		String id = null;
		String modifier = null;
		if (positionInParent != null && positionInParent.length() > 0) {
			String[] array = positionInParent.split("="); //$NON-NLS-1$
			modifier = array[0];
			id = array[1];
		}
		if (id == null) {
			return menuModel.getChildren().size();
		}

		int idx = 0;
		int size = menuModel.getChildren().size();
		while (idx < size) {
			if (id.equals(menuModel.getChildren().get(idx).getElementId())) {
				if ("after".equals(modifier)) { //$NON-NLS-1$
					idx++;
				}
				return idx;
			}
			idx++;
		}
		return id.equals("additions") ? menuModel.getChildren().size() : -1; //$NON-NLS-1$
	}
}