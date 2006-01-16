/******************************************************************************* * Copyright (c) 2005 IBM Corporation and others. * All rights reserved. This program and the accompanying materials * are made available under the terms of the Eclipse Public License v1.0 * which accompanies this distribution, and is available at * http://www.eclipse.org/legal/epl-v10.html * * Contributors: *     IBM Corporation - initial API and implementation ******************************************************************************/package org.eclipse.jface.menus;import org.eclipse.core.commands.common.HandleObject;import org.eclipse.core.commands.common.NotDefinedException;/** * <p> * A handle object existing in the menus. This can be either a menu, a group, an * item or a widget. * </p> * <p> * For menu elements, there is the concept of "showing" and "visible". "Showing" * means that the menu element could potentially be visible to the end user. * This property is under control of the user. So, for example, clicking on a * top-level menu while make all of its contents "showing". "Visible" means that * the menu element should be painted on the display if it is showing. This * property is under control of the application. So, for example, an application * could decide to hide some items in a context menu if they do not apply to the * current selection. * </p> * <p> * Clients must not implement or extend. * </p> * <p> * <strong>EXPERIMENTAL</strong>. This class or interface has been added as * part of a work in progress. There is a guarantee neither that this API will * work nor that it will remain the same. Please do not use this API without * consulting with the Platform/UI team. * </p> *  * @since 3.2 * @see org.eclipse.jface.menus.SMenu * @see org.eclipse.jface.menus.SGroup * @see org.eclipse.jface.menus.SItem * @see org.eclipse.jface.menus.SWidget */public abstract class MenuElement extends HandleObject {	/**	 * The array to return from {@link #getLocations()} if there are no	 * locations.	 */	private static final SLocation[] NO_LOCATIONS = new SLocation[0];	/**	 * The locations in which this menu element appears. This value may be empty	 * or <code>null</code>.	 */	protected SLocation[] locations;	/**	 * Whether this menu element is currently showing.	 */	private boolean showing = false;	/**	 * Whether this menu element is currently visible.	 */	private boolean visible = false;	/**	 * Constructs a new instance of <code>MenuElement</code>.	 * 	 * @param id	 *            The identifier of the element to create; must not be	 *            <code>null</code>.	 */	public MenuElement(final String id) {		super(id);	}	/**	 * Indicates that the visible property has changed. Subclasses should	 * forward an event to any listeners.	 */	protected abstract void fireVisibleChanged();	/**	 * Returns the locations for this menu collection. This performs a copy of	 * the internal data structure.	 * 	 * @return The locations for this menu collection; never <code>null</code>.	 * @throws NotDefinedException	 *             If the handle is not currently defined.	 */	public final SLocation[] getLocations() throws NotDefinedException {		if (!isDefined()) {			throw new NotDefinedException(					"Cannot get the locations from an undefined menu element"); //$NON-NLS-1$		}		if (locations == null) {			return NO_LOCATIONS;		}		final SLocation[] result = new SLocation[locations.length];		System.arraycopy(locations, 0, result, 0, locations.length);		return result;	}	/**	 * Returns whether this menu element is showing. A menu element is showing	 * if it could be visible to the user. A menu element must be showing before	 * the <code>isVisible</code> is checked.	 * 	 * @return <code>true</code> if the menu element is showing;	 *         <code>false</code> otherwise.	 */	public final boolean isShowing() {		return showing;	}	/**	 * Returns whether this menu element is visible. A menu element is visible	 * if it will be visible to the user if showing.	 * 	 * @return <code>true</code> if the menu element is visible;	 *         <code>false</code> otherwise.	 */	public final boolean isVisible() {		return visible;	}	/**	 * Sets the showing property. Changing this property does not trigger a	 * property change event. Developers interested in listening to changes in	 * this property should attach a listener to the <code>SMenuManager</code>.	 * 	 * @param showing	 *            Whether the menu element should be showing.	 * @see SMenuManager#addListener(IMenuManagerListener)	 */	final void setShowing(final boolean showing) {		this.showing = showing;	}	/**	 * Sets the visible property. Changing this property triggers an event	 * appropriate for the subclasses.	 * 	 * @param visible	 *            Whether the menu element should be visible.	 */	public final void setVisible(final boolean visible) {		if (this.visible != visible) {			this.visible = visible;			fireVisibleChanged();		}	}}