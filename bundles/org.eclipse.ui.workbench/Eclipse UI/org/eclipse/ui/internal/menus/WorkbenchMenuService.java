/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Brian de Alwis (MT) - adapted for Eclipse 4
 ******************************************************************************/

package org.eclipse.ui.internal.menus;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.MApplicationElement;
import org.eclipse.e4.ui.model.application.MContribution;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimBar;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimElement;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindowElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuContribution;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.ExpressionContext;
import org.eclipse.jface.action.ContributionManager;
import org.eclipse.ui.ISourceProvider;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.activities.IActivityManager;
import org.eclipse.ui.activities.IIdentifier;
import org.eclipse.ui.activities.IIdentifierListener;
import org.eclipse.ui.activities.IdentifierEvent;
import org.eclipse.ui.internal.e4.compatibility.E4Util;
import org.eclipse.ui.internal.services.IWorkbenchLocationService;
import org.eclipse.ui.internal.services.ServiceLocator;
import org.eclipse.ui.menus.AbstractContributionFactory;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.services.IServiceLocator;
import org.osgi.service.event.EventHandler;

/**
 * @since 3.5
 *
 */
public class WorkbenchMenuService implements IMenuService {
	/** A simple internal visitor used for hooking and unhooking activity listeners */
	private static interface IUIElementVisitor {
		void visit(MUIElement o);
	}

	private static class ElementUpdater implements IIdentifierListener {
		/**
		 * 
		 */
		private static final String INACTIVE_TAG = "NotActive"; //$NON-NLS-1$
		private MUIElement element;
		private IIdentifier identifier;

		public ElementUpdater(MUIElement element, IIdentifier identifier) {
			this.element = element;
			this.identifier = identifier;
			this.identifier.addIdentifierListener(this);
			updateVisibility();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.ui.activities.IIdentifierListener#identifierChanged(org
		 * .eclipse.ui.activities.IdentifierEvent)
		 */
		public void identifierChanged(IdentifierEvent identifierEvent) {
			updateVisibility();
		}

		public void updateVisibility() {
			if (identifier.isEnabled()) {
				element.getTags().remove(INACTIVE_TAG);
			} else {
				element.getTags().add(INACTIVE_TAG);
			}
			element.setVisible(identifier.isEnabled());
		}

		public void dispose() {
			identifier.removeIdentifierListener(this);
		}
	}

	private final IUIElementVisitor elementHooker = new IUIElementVisitor() {
		public void visit(MUIElement element) {
			hookElement(element);
		}
	};

	private final IUIElementVisitor elementUnhooker = new IUIElementVisitor() {
		public void visit(MUIElement element) {
			unhookElement(element);
		}
	};

	private IEclipseContext e4Context;
	// private ServiceLocator serviceLocator;
	private MenuPersistence persistence;
	private ExpressionContext legacyContext;
	/**
	 * The service locator into which this service will be inserted.
	 */
	private IServiceLocator serviceLocator;

	private MApplication app;
	private IEventBroker eventBroker;
	private EventHandler shellListener;
	private EventHandler menuListener;
	private EventHandler menuContributionListener;

	private Map<MUIElement, ElementUpdater> elementUpdaters;

	/**
	 * @param serviceLocator
	 * @param e4Context
	 */
	public WorkbenchMenuService(ServiceLocator serviceLocator, IEclipseContext e4Context) {
		this.serviceLocator = serviceLocator;
		this.e4Context = e4Context;
		app = e4Context.get(MApplication.class);
		eventBroker = e4Context.get(IEventBroker.class);
		elementUpdaters = new HashMap<MUIElement, ElementUpdater>();

		persistence = new MenuPersistence(e4Context.get(MApplication.class), e4Context);

		// the command, handler, and binding services are now responsible for
		// processing registry changes and turning them into MCommand, MHandler,
		// and MBinding changes
		hookWorkbenchListeners();
		processApplication();
	}

	/**
	 * 
	 */
	private void processApplication() {
		for (MWindow window : app.getChildren()) {
			processWindow(window, elementHooker);
		}
		for (MMenuContribution mc : app.getMenuContributions()) {
			processMenuContribution(mc, elementHooker);
		}
	}

	/**
	 * Hook model changes to process the menu and toolbar changes
	 */
	private void hookWorkbenchListeners() {
		// watch for a window's "widget" attribute being flipped to a shell
		shellListener = new EventHandler() {
			public void handleEvent(org.osgi.service.event.Event event) {
				Object element = event.getProperty(UIEvents.EventTags.ELEMENT);
				Object newValue = event.getProperty(UIEvents.EventTags.NEW_VALUE);
				Object oldValue = event.getProperty(UIEvents.EventTags.OLD_VALUE);
				if (element instanceof MWindow && oldValue != null) {
					processWindow((MWindow) element, elementUnhooker);
				}
				if (element instanceof MWindow && newValue != null) {
					processWindow((MWindow) element, elementHooker);
				}
			}
		};
		eventBroker.subscribe(
				UIEvents.buildTopic(UIEvents.UIElement.TOPIC, UIEvents.UIElement.WIDGET),
				shellListener);

		// this listener is handling the Eclipse 4.0 compatibility case,
		// where the window is created without a main menu or trim first,
		// and then later when the main menu is being set it is time
		// for us to do our work. It also handles dynamically created
		// windows too.
		menuListener = new EventHandler() {
			public void handleEvent(org.osgi.service.event.Event event) {
				Object newValue = event.getProperty(UIEvents.EventTags.NEW_VALUE);
				Object oldValue = event.getProperty(UIEvents.EventTags.OLD_VALUE);
				Object element = event.getProperty(UIEvents.EventTags.ELEMENT);
				if (element instanceof MWindow && oldValue instanceof MMenu) {
					processMenu((MMenu) oldValue, elementUnhooker);
				}
				if (element instanceof MWindow && newValue instanceof MMenu) {
					processMenu((MMenu) newValue, elementHooker);
				}
			}
		};
		eventBroker.subscribe(UIEvents.buildTopic(UIEvents.Window.TOPIC, UIEvents.Window.MAINMENU),
				menuListener);

		// watch for new menu contributions
		menuContributionListener = new EventHandler() {
			public void handleEvent(org.osgi.service.event.Event event) {
				Object newValue = event.getProperty(UIEvents.EventTags.NEW_VALUE);
				Object oldValue = event.getProperty(UIEvents.EventTags.OLD_VALUE);
				Object element = event.getProperty(UIEvents.EventTags.ELEMENT);
				Object type = event.getProperty(UIEvents.EventTags.TYPE);
				if (element instanceof MApplication && oldValue != null) {
					processMenuContribution((MMenuContribution) oldValue, elementUnhooker);
				}
				if (element instanceof MApplication && newValue != null) {
					processMenuContribution((MMenuContribution) newValue, elementHooker);
				}
				// workaround bug 364529
				if (element instanceof MApplication && type.equals("UNKNOWN")) { //$NON-NLS-1$
					for (MMenuContribution mc : ((MApplication) element).getMenuContributions()) {
						processMenuContribution(mc, elementHooker);
					}
				}
			}
		};
		eventBroker.subscribe(UIEvents.buildTopic(UIEvents.MenuContributions.TOPIC,
				UIEvents.MenuContributions.MENUCONTRIBUTIONS), menuContributionListener);
	}

	/***********************************************************************************
	 * Activities doc says that "The following contributions are affected by
	 * activities: Views and editors; Perspectives; Preference and property pages;
	 * Menus and toolbars; New wizard; Common Navigator Action Providers"
	 * (http://help.eclipse.org/indigo/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Fguide%2Fworkbench_advext_activities.htm)
	 * *********************************************************************************/
	/**
	 * @param element
	 */
	protected void processWindow(MWindow window, IUIElementVisitor visitor) {
		if (window instanceof MTrimmedWindow) {
			MTrimmedWindow trimmed = (MTrimmedWindow) window;
			for (MTrimBar trimbar : trimmed.getTrimBars()) {
				visitor.visit(trimbar);
				for (MTrimElement trimElement : trimbar.getChildren()) {
					if (trimElement instanceof MToolBar) {
						processToolBar((MToolBar) trimElement, visitor);
					}
				}
			}
		}
		if (window.getMainMenu() != null) {
			for (MMenuElement me : window.getMainMenu().getChildren()) {
				if (me instanceof MMenu) {
					processMenu((MMenu) me, visitor);
				}
			}
		}
		for (MWindowElement child : window.getChildren()) {
			processWindowElement(child, visitor);
		}
	}

	/**
	 * @param child
	 */
	private void processWindowElement(MWindowElement element, IUIElementVisitor visitor) {
		if(element instanceof MPart || element instanceof MPerspective) {
			visitor.visit(element);
		} 
		// not an else-if: it's possible that someone will create a part that is also a container
		if (element instanceof MElementContainer<?>) {
			for (MUIElement e : ((MElementContainer<?>) element).getChildren()) {
				if (e instanceof MWindowElement) {
					processWindowElement((MWindowElement) e, visitor);
				}
			}
		}
	}

	/**
	 * @param toolbar
	 */
	private void processToolBar(MToolBar toolbar, IUIElementVisitor visitor) {
		hookElement(toolbar);
		for (MToolBarElement tbe : toolbar.getChildren()) {
			visitor.visit(tbe);
			if (tbe instanceof MToolBar) {
				processToolBar((MToolBar) tbe, visitor);
			}
		}
	}

	/**
	 * @param contribution
	 */
	protected void processMenuContribution(MMenuContribution contribution, IUIElementVisitor visitor) {
		hookElement(contribution);
		for (MMenuElement me : contribution.getChildren()) {
			visitor.visit(me);
			if (me instanceof MMenu) {
				processMenu((MMenu) me, visitor);
			}
		}
	}

	/**
	 * Process menu items
	 */
	protected void processMenu(MMenu menu, IUIElementVisitor visitor) {
		visitor.visit(menu);
		for (MMenuElement m : menu.getChildren()) {
			visitor.visit(m);
			if (m instanceof MMenu) {
				processMenu((MMenu) m, visitor);
			}
		}
	}


	/**
	 * Request notifications of identifier changes
	 */
	private void hookElement(MUIElement element) {
		if (elementUpdaters.containsKey(element)) {
			return;
		}
		String uid = createUnifiedId(element);
		if (uid == null) {
			return;
		}
		IActivityManager mgr = getActivityManager();
		IIdentifier id = mgr.getIdentifier(uid);

		elementUpdaters.put(element, new ElementUpdater(element, id));
	}

	/**
	 * Stop notifications of identifier changes
	 */
	private void unhookElement(MUIElement element) {
		ElementUpdater updater = elementUpdaters.remove(element);
		if (updater != null) {
			updater.dispose();
		}
	}

	/**
	 * Return an activity-style identifier (i.e., 'plugin/id')
	 * 
	 * @param element
	 *            the UI element
	 * @return the identifier
	 */
	private String createUnifiedId(MApplicationElement element) {
		String pluginId = getDefiningPlugin(element);
		if (element.getElementId() == null || element.getElementId().isEmpty() || pluginId == null) {
			return null;
		}
		return pluginId + "/" + element.getElementId(); //$NON-NLS-1$
	}

	/** Pattern to match class contributions */
	private final Pattern pluginPattern = Pattern.compile("^platform:/plugin/([^/]+)(/(.*))?$"); //$NON-NLS-1$

	/**
	 * @param element
	 * @return
	 */
	private String getDefiningPlugin(MApplicationElement element) {
		Matcher m = element.getContributorURI() == null ? null : pluginPattern.matcher(element
				.getContributorURI());
		if (m == null || !m.matches()) {
			if (element instanceof MContribution
					&& ((MContribution) element).getContributionURI() != null) {
				m = pluginPattern.matcher(((MContribution) element).getContributorURI());
				if (!m.matches()) {
					return null;
				}
			} else {
				return null;
			}
		}
		return m.group(1);
	}

	/**
	 * @return the workbench's activity manager
	 */
	private IActivityManager getActivityManager() {
		if (serviceLocator == null) {
			return null;
		}
		IWorkbenchLocationService wls = (IWorkbenchLocationService) serviceLocator
				.getService(IWorkbenchLocationService.class);
		IWorkbench workbench = wls.getWorkbench();
		if (workbench != null) {
			return workbench.getActivitySupport().getActivityManager();
		}
		return null;
	}

	public final void addSourceProvider(final ISourceProvider provider) {
		// no-op
	}

	public final void dispose() {
		persistence.dispose();
		for (ElementUpdater eu : elementUpdaters.values()) {
			eu.dispose();
		}
		elementUpdaters.clear();
		eventBroker.unsubscribe(shellListener);
		eventBroker.unsubscribe(menuListener);
		eventBroker.unsubscribe(menuContributionListener);
	}

	public final void removeSourceProvider(final ISourceProvider provider) {
		// no-op
	}

	/**
	 * @return Returns the persistence.
	 */
	public MenuPersistence getMenuPersistence() {
		return persistence;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.menus.IMenuService#addContributionFactory(org.eclipse.
	 * ui.menus.AbstractContributionFactory)
	 */
	public void addContributionFactory(AbstractContributionFactory factory) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.menus.IMenuService#removeContributionFactory(org.eclipse
	 * .ui.menus.AbstractContributionFactory)
	 */
	public void removeContributionFactory(AbstractContributionFactory factory) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.menus.IMenuService#populateContributionManager(org.eclipse
	 * .jface.action.ContributionManager, java.lang.String)
	 */
	public void populateContributionManager(ContributionManager mgr, String location) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.menus.IMenuService#releaseContributions(org.eclipse.jface
	 * .action.ContributionManager)
	 */
	public void releaseContributions(ContributionManager mgr) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.menus.IMenuService#getCurrentState()
	 */
	public IEvaluationContext getCurrentState() {
		if (legacyContext == null) {
			legacyContext = new ExpressionContext(e4Context);
		}
		return legacyContext;
	}


	/**
	 * read in the menu contributions and turn them into model menu
	 * contributions
	 */
	public void readRegistry() {
		persistence.read();
	}

	public void updateManagers() {
		E4Util.unsupported("WorkbenchMenuService.updateManagers - time to update ... something"); //$NON-NLS-1$
	}

}
