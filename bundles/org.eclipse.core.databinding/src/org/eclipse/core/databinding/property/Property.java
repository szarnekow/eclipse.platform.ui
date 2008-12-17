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

package org.eclipse.core.databinding.property;

/**
 * Abstract IProperty implementation
 */
public abstract class Property implements IProperty {
	/**
	 * Returns a string description of this property, suitable for debugging
	 * purposes
	 * 
	 * @return a string description of this property, suitable for debugging
	 *         purposes
	 */
	public abstract String toString();
}
