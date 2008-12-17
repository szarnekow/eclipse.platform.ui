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

package org.eclipse.jface.databinding.swt;

import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.jface.internal.databinding.swt.ControlBackgroundProperty;
import org.eclipse.jface.internal.databinding.swt.ControlBoundsProperty;
import org.eclipse.jface.internal.databinding.swt.ControlEnabledProperty;
import org.eclipse.jface.internal.databinding.swt.ControlFocusedProperty;
import org.eclipse.jface.internal.databinding.swt.ControlFontProperty;
import org.eclipse.jface.internal.databinding.swt.ControlForegroundProperty;
import org.eclipse.jface.internal.databinding.swt.ControlLocationProperty;
import org.eclipse.jface.internal.databinding.swt.ControlSizeProperty;
import org.eclipse.jface.internal.databinding.swt.ControlTooltipTextProperty;
import org.eclipse.jface.internal.databinding.swt.ControlVisibleProperty;

/**
 * A factory for creating properties of SWT controls.
 * 
 * @since 1.3
 */
public class ControlProperties {
	/**
	 * Returns a value property for the enablement state of a SWT Control.
	 * 
	 * @return a value property for the enablement state of a SWT Control.
	 */
	public static IValueProperty enabled() {
		return new ControlEnabledProperty();
	}

	/**
	 * Returns a value property for the visibility state of a SWT Control.
	 * 
	 * @return a value property for the visibility state of a SWT Control.
	 */
	public static IValueProperty visible() {
		return new ControlVisibleProperty();
	}

	/**
	 * Returns a value property for the tooltip text of a SWT Control.
	 * 
	 * @return a value property for the tooltip text of a SWT Control.
	 */
	public static IValueProperty toolTipText() {
		return new ControlTooltipTextProperty();
	}

	/**
	 * Returns a value property for the foreground color of a SWT Control.
	 * 
	 * @return a value property for the foreground color of a SWT Control.
	 */
	public static IValueProperty foreground() {
		return new ControlForegroundProperty();
	}

	/**
	 * Returns a value property for the background color of a SWT Control.
	 * 
	 * @return a value property for the background color of a SWT Control.
	 */
	public static IValueProperty background() {
		return new ControlBackgroundProperty();
	}

	/**
	 * Returns a value property for the font of a SWT Control.
	 * 
	 * @return a value property for the font of a SWT Control.
	 */
	public static IValueProperty font() {
		return new ControlFontProperty();
	}

	/**
	 * Returns a value property for the size of a SWT Control.
	 * 
	 * @return a value property for the size of a SWT Control.
	 */
	public static IValueProperty size() {
		return new ControlSizeProperty();
	}

	/**
	 * Returns a value property for the location of a SWT Control.
	 * 
	 * @return a value property for the location of a SWT Control.
	 */
	public static IValueProperty location() {
		return new ControlLocationProperty();
	}

	/**
	 * Returns a value property for the bounds of a SWT Control.
	 * 
	 * @return a value property for the bounds of a SWT Control.
	 */
	public static IValueProperty bounds() {
		return new ControlBoundsProperty();
	}

	/**
	 * Returns a value property for the focus state of a SWT Control.
	 * 
	 * @return a value property for the focus state of a SWT Control.
	 */
	public static IValueProperty focused() {
		return new ControlFocusedProperty();
	}
}
