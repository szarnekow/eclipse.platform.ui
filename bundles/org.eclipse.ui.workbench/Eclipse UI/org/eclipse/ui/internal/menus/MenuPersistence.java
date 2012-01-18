/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.internal.menus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.internal.workbench.ContributionsAnalyzer;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuContribution;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarContribution;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarSeparator;
import org.eclipse.e4.ui.model.application.ui.menu.MTrimContribution;
import org.eclipse.ui.internal.registry.IWorkbenchRegistryConstants;
import org.eclipse.ui.internal.services.RegistryPersistence;

/**
 * <p>
 * A static class for accessing the registry.
 * </p>
 * <p>
 * This class is not intended for use outside of the
 * <code>org.eclipse.ui.workbench</code> plug-in.
 * </p>
 * 
 * @since 3.2
 */
final public class MenuPersistence extends RegistryPersistence {

	private MApplication application;
	private IEclipseContext appContext;
	private ArrayList<MenuAdditionCacheEntry> cacheEntries = new ArrayList<MenuAdditionCacheEntry>();
	private ArrayList<ActionSet> actionContributions = new ArrayList<ActionSet>();
	private ArrayList<EditorAction> editorActionContributions = new ArrayList<EditorAction>();
	private ArrayList<ViewAction> viewActionContributions = new ArrayList<ViewAction>();

	private ArrayList<MMenuContribution> menuContributions = new ArrayList<MMenuContribution>();
	private ArrayList<MToolBarContribution> toolBarContributions = new ArrayList<MToolBarContribution>();
	private ArrayList<MTrimContribution> trimContributions = new ArrayList<MTrimContribution>();

	private final Comparator<IConfigurationElement> comparer = new Comparator<IConfigurationElement>() {
		public int compare(IConfigurationElement c1, IConfigurationElement c2) {
			return c1.getContributor().getName().compareToIgnoreCase(c2.getContributor().getName());
		}
	};
	private Pattern contributorFilter;

	/**
	 * @param application
	 * @param appContext
	 */
	public MenuPersistence(MApplication application, IEclipseContext appContext) {
		this.application = application;
		this.appContext = appContext;
	}

	public MenuPersistence(MApplication application, IEclipseContext appContext, String filterRegex) {
		this(application, appContext);
		contributorFilter = Pattern.compile(filterRegex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.internal.services.RegistryPersistence#dispose()
	 */
	@Override
	public void dispose() {
		ControlContributionRegistry.clear();
		application.getMenuContributions().removeAll(menuContributions);
		application.getToolBarContributions().removeAll(toolBarContributions);
		application.getTrimContributions().removeAll(trimContributions);
		menuContributions.clear();
		cacheEntries.clear();
		actionContributions.clear();
		editorActionContributions.clear();
		viewActionContributions.clear();
		super.dispose();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.ui.tests.workbench.RegistryPersistence#isChangeImportant
	 * (org.eclipse.core.runtime.IRegistryChangeEvent)
	 */
	@Override
	protected boolean isChangeImportant(IRegistryChangeEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	public void reRead() {
		read();
	}

	protected final void read() {
		super.read();

		readAdditions();
		readActionSets();
		readEditorActions();
		readViewActions();

		ArrayList<MMenuContribution> tmp = new ArrayList<MMenuContribution>(menuContributions);
		menuContributions.clear();
		ContributionsAnalyzer.mergeContributions(tmp, menuContributions);
		application.getMenuContributions().addAll(menuContributions);

		ArrayList<MToolBarContribution> tmpToolbar = new ArrayList<MToolBarContribution>(
				toolBarContributions);
		toolBarContributions.clear();
		ContributionsAnalyzer.mergeToolBarContributions(tmpToolbar, toolBarContributions);
		application.getToolBarContributions().addAll(toolBarContributions);

		ArrayList<MTrimContribution> tmpTrim = new ArrayList<MTrimContribution>(trimContributions);
		trimContributions.clear();
		ContributionsAnalyzer.mergeTrimContributions(tmpTrim, trimContributions);
		application.getTrimContributions().addAll(trimContributions);
	}

	private void readAdditions() {
		final IExtensionRegistry registry = Platform.getExtensionRegistry();
		ArrayList<IConfigurationElement> configElements = new ArrayList<IConfigurationElement>();

		final IConfigurationElement[] menusExtensionPoint = registry
				.getConfigurationElementsFor(EXTENSION_MENUS);

		// Create a cache entry for every menu addition;
		for (int i = 0; i < menusExtensionPoint.length; i++) {
			if (PL_MENU_CONTRIBUTION.equals(menusExtensionPoint[i].getName())) {
				if (contributorFilter == null
						|| contributorFilter.matcher(
								menusExtensionPoint[i].getContributor().getName()).matches()) {
					configElements.add(menusExtensionPoint[i]);
				}
			}
		}
		Collections.sort(configElements, comparer);
		Iterator<IConfigurationElement> i = configElements.iterator();
		while (i.hasNext()) {
			final IConfigurationElement configElement = i.next();

			if (isProgramaticContribution(configElement)) {
				MenuFactoryGenerator gen = new MenuFactoryGenerator(application, appContext,
						configElement,
						configElement.getAttribute(IWorkbenchRegistryConstants.TAG_LOCATION_URI));
				gen.mergeIntoModel(menuContributions, toolBarContributions, trimContributions);
			} else {
				MenuAdditionCacheEntry menuContribution = new MenuAdditionCacheEntry(application,
						appContext, configElement,
						configElement.getAttribute(IWorkbenchRegistryConstants.TAG_LOCATION_URI),
						configElement.getNamespaceIdentifier());
				cacheEntries.add(menuContribution);
				menuContribution.mergeIntoModel(menuContributions, toolBarContributions,
						trimContributions);
			}
		}
	}

	private void readActionSets() {
		final IExtensionRegistry registry = Platform.getExtensionRegistry();
		ArrayList<IConfigurationElement> configElements = new ArrayList<IConfigurationElement>();

		final IConfigurationElement[] extElements = registry
				.getConfigurationElementsFor(IWorkbenchRegistryConstants.EXTENSION_ACTION_SETS);
		for (IConfigurationElement element : extElements) {
			if (contributorFilter == null
					|| contributorFilter.matcher(element.getContributor().getName()).matches()) {
				configElements.add(element);
			}
		}

		Collections.sort(configElements, comparer);

		HashMap<String, ArrayList<MToolBarContribution>> postProcessing = new HashMap<String, ArrayList<MToolBarContribution>>();
		for (IConfigurationElement element : configElements) {
			ArrayList<MToolBarContribution> localToolbarContributions = new ArrayList<MToolBarContribution>();
			ActionSet actionSet = new ActionSet(application, appContext, element);
			actionContributions.add(actionSet);
			actionSet.addToModel(menuContributions, localToolbarContributions, trimContributions);
			toolBarContributions.addAll(localToolbarContributions);
			postProcessing.put(actionSet.getId(), localToolbarContributions);
		}
		for (Entry<String, ArrayList<MToolBarContribution>> entry : postProcessing.entrySet()) {
			for (MToolBarContribution contribution : entry.getValue()) {
				String targetParentId = contribution.getParentId();
				if (entry.getKey().equals(targetParentId)) {
					continue;
				}
				ArrayList<MToolBarContribution> adjunctContributions = postProcessing
						.get(targetParentId);
				if (adjunctContributions == null) {
					continue;
				}
				boolean processed = false;
				Iterator<MToolBarContribution> i = adjunctContributions.iterator();
				while (i.hasNext() && !processed) {
					MToolBarContribution adjunctContribution = i.next();
					if (targetParentId.equals(adjunctContribution.getParentId())) {
						for (MToolBarElement item : adjunctContribution.getChildren()) {
							if (!(item instanceof MToolBarSeparator) && item.getElementId() != null) {
								processed = true;
								contribution.setPositionInParent("before=" + item.getElementId()); //$NON-NLS-1$
								break;
							}
						}
					}
				}
			}
		}
		postProcessing.clear();
	}

	private void readEditorActions() {
		final IExtensionRegistry registry = Platform.getExtensionRegistry();
		ArrayList<IConfigurationElement> configElements = new ArrayList<IConfigurationElement>();

		final IConfigurationElement[] extElements = registry
				.getConfigurationElementsFor(IWorkbenchRegistryConstants.EXTENSION_EDITOR_ACTIONS);
		for (IConfigurationElement element : extElements) {
			if (contributorFilter == null
					|| contributorFilter.matcher(element.getContributor().getName()).matches()) {
				configElements.add(element);
			}
		}

		Collections.sort(configElements, comparer);

		for (IConfigurationElement element : configElements) {
			for (IConfigurationElement child : element.getChildren()) {
				if (child.getName().equals(IWorkbenchRegistryConstants.TAG_ACTION)) {
					EditorAction editorAction = new EditorAction(application, appContext, element,
							child);
					editorActionContributions.add(editorAction);
					editorAction.addToModel(menuContributions, toolBarContributions,
							trimContributions);
				}
			}
		}
	}

	private void readViewActions() {
		final IExtensionRegistry registry = Platform.getExtensionRegistry();
		ArrayList<IConfigurationElement> configElements = new ArrayList<IConfigurationElement>();

		final IConfigurationElement[] extElements = registry
				.getConfigurationElementsFor(IWorkbenchRegistryConstants.EXTENSION_VIEW_ACTIONS);
		for (IConfigurationElement element : extElements) {
			if (contributorFilter == null
					|| contributorFilter.matcher(element.getContributor().getName()).matches()) {
				configElements.add(element);
			}
		}

		Collections.sort(configElements, comparer);

		for (IConfigurationElement element : configElements) {
			IConfigurationElement[] children = element.getChildren();
			// go in reverse order
			for (int i = children.length - 1; i >= 0; i--) {
				IConfigurationElement child = children[i];
				if (child.getName().equals(IWorkbenchRegistryConstants.TAG_ACTION)) {
					ViewAction viewAction = new ViewAction(application, appContext, element, child,
							false);
					viewActionContributions.add(viewAction);
					viewAction.addToModel(menuContributions, toolBarContributions,
							trimContributions);
				} else if (child.getName().equals(IWorkbenchRegistryConstants.TAG_MENU)) {
					ViewAction viewAction = new ViewAction(application, appContext, element, child,
							true);
					viewActionContributions.add(viewAction);
					viewAction.addToModel(menuContributions, toolBarContributions,
							trimContributions);
				}
			}
		}
	}

	private boolean isProgramaticContribution(IConfigurationElement menuAddition) {
		return menuAddition.getAttribute(IWorkbenchRegistryConstants.ATT_CLASS) != null;
	}
}
