/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.core.expressions.Expression;
import org.eclipse.jface.action.ContributionManager;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.internal.expressions.AlwaysEnabledExpression;
import org.eclipse.ui.menus.AbstractContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.menus.IMenuService;

/**
 * Default implementation.
 * 
 * @since 3.3
 */
final class ContributionRoot implements
		IContributionRoot {

	private List topLevelItems = new ArrayList();
	private List itemsToExpressions = new ArrayList();
	private WorkbenchMenuService menuService;
	Set restriction;
	private ContributionManager mgr;
	private AbstractContributionFactory factory;

	public ContributionRoot(IMenuService menuService, Set restriction,
			ContributionManager mgr, AbstractContributionFactory factory) {
		this.menuService = (WorkbenchMenuService) menuService;
		this.restriction = restriction;
		this.mgr = mgr;
		this.factory = factory;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.menus.IContributionRoot#addContributionItem(org.eclipse.jface.action.IContributionItem, org.eclipse.core.expressions.Expression, org.eclipse.core.expressions.Expression)
	 */
	public void addContributionItem(IContributionItem item,
			Expression visibleWhen) {
		if (item == null)
			throw new IllegalArgumentException();
		topLevelItems.add(item);
		if (visibleWhen == null) 
			visibleWhen = AlwaysEnabledExpression.INSTANCE;
		
		menuService.registerVisibleWhen(item, visibleWhen, restriction,
				createIdentifierId(item));
		itemsToExpressions.add(item);
	}

	/**
     * Create the activity identifier for this contribution item.
     *
	 * @param item the item
	 * @return the identifier
	 */
	private String createIdentifierId(IContributionItem item) {
		String namespace = factory.getNamespace();
		String identifierID = namespace != null ? namespace + '/'
				+ item.getId() : null; // create the activity identifier ID. If
										// this factory doesn't have a namespace
										// it will be null.
		return identifierID;
	}

	public List getItems() {
		return topLevelItems;
	}

	/**
	 * Unregister all visible when expressions from the menu service.
	 */
	public void release() {
		for (Iterator itemIter = itemsToExpressions.iterator(); itemIter.hasNext();) {
			IContributionItem item = (IContributionItem) itemIter.next();
			menuService.unregisterVisibleWhen(item, restriction);
			item.dispose();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.menus.IContributionRoot#registerVisibilityForChild(org.eclipse.jface.action.IContributionItem,
	 *      org.eclipse.core.expressions.Expression,
	 *      org.eclipse.core.expressions.Expression)
	 */
	public void registerVisibilityForChild(IContributionItem item,
			Expression visibleWhen) {
		if (item == null)
			throw new IllegalArgumentException();
		if (visibleWhen == null) 
			visibleWhen = AlwaysEnabledExpression.INSTANCE;
		menuService.registerVisibleWhen(item, visibleWhen, restriction,
				createIdentifierId(item));
		itemsToExpressions.add(item);
	}

	/**
	 * @return Returns the mgr.
	 */
	public ContributionManager getManager() {
		return mgr;
	}
}