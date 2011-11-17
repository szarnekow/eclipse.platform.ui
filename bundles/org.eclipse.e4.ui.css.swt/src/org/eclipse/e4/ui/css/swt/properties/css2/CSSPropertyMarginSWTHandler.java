/*******************************************************************************
 * Copyright (c) 2010,2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Brian de Alwis (MT) - adaptation for padding tests
 *******************************************************************************/
package org.eclipse.e4.ui.css.swt.properties.css2;

import org.eclipse.e4.ui.css.core.dom.properties.css2.AbstractCSSPropertyMarginHandler;
import org.eclipse.e4.ui.css.core.dom.properties.css2.ICSSPropertyMarginHandler;
import org.eclipse.e4.ui.css.core.engine.CSSEngine;
import org.eclipse.e4.ui.css.swt.helpers.SWTElementHelpers;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Widget;
import org.w3c.css.sac.CSSException;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;

public class CSSPropertyMarginSWTHandler extends
		AbstractCSSPropertyMarginHandler {

	public final static ICSSPropertyMarginHandler INSTANCE = new CSSPropertyMarginSWTHandler();
	
	private final static int TOP = 0;
	private final static int RIGHT = 1;
	private final static int BOTTOM = 2;
	private final static int LEFT = 3;

	public boolean applyCSSProperty(Object element, String property,
			CSSValue value, String pseudo, CSSEngine engine) throws Exception {
		
		super.applyCSSProperty(element, property, value, pseudo, engine);
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.e4.ui.css.core.dom.properties.css2.AbstractCSSPropertyMarginHandler#applyCSSPropertyMargin(java.lang.Object, org.w3c.dom.css.CSSValue, java.lang.String, org.eclipse.e4.ui.css.core.engine.CSSEngine)
	 * If single value then assigned to all four margins
	 * If four values then assigned top/right/bottom/left
	 * If three values then assigned top=v1, left=v2, right=v2, bottom=v3
	 * If two values then assigned top/bottom=v1, left/right=v2
	 */
	public void applyCSSPropertyMargin(Object element, CSSValue value,
			String pseudo, CSSEngine engine) throws Exception {
		
		// If single value then assigned to all four margins
		if(value.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
			setMargin(element, TOP, value, pseudo);
			setMargin(element, RIGHT, value, pseudo);
			setMargin(element, BOTTOM, value, pseudo);
			setMargin(element, LEFT, value, pseudo);
			return;
		}
		
		if(value.getCssValueType() == CSSValue.CSS_VALUE_LIST) {
			CSSValueList valueList = (CSSValueList) value;
			int length = valueList.getLength();

			if(length < 2 || length > 4)
				throw new CSSException("Invalid margin property list length");
			
			// Reverse application order to ensure last value holds (in case
			// the underlying layout only supports a subset
			switch (length) {
			case 4:
				// If four values then assigned top/right/bottom/left
				setMargin(element, LEFT, valueList.item(3), pseudo);
				setMargin(element, BOTTOM, valueList.item(2), pseudo);
				setMargin(element, RIGHT, valueList.item(1), pseudo);
				setMargin(element, TOP, valueList.item(0), pseudo);
				break;
			case 3:
				// If three values then assigned top=v1, left=v2, right=v2, bottom=v3
				setMargin(element, LEFT, valueList.item(1), pseudo);
				setMargin(element, BOTTOM, valueList.item(2), pseudo);
				setMargin(element, RIGHT, valueList.item(1), pseudo);
				setMargin(element, TOP, valueList.item(0), pseudo);
			case 2:
				// If two values then assigned top/bottom=v1, left/right=v2
				setMargin(element, LEFT, valueList.item(1), pseudo);
				setMargin(element, BOTTOM, valueList.item(0), pseudo);
				setMargin(element, RIGHT, valueList.item(1), pseudo);
				setMargin(element, TOP, valueList.item(0), pseudo);
			}
		} else {
			throw new CSSException("Invalid margin property value");
		}
	}

	public void applyCSSPropertyMarginTop(Object element, CSSValue value,
			String pseudo, CSSEngine engine) throws Exception {
		setMargin(element, TOP, value, pseudo);
	}

	public void applyCSSPropertyMarginRight(Object element, CSSValue value,
			String pseudo, CSSEngine engine) throws Exception {
		setMargin(element, RIGHT, value, pseudo);
	}

	public void applyCSSPropertyMarginBottom(Object element, CSSValue value,
			String pseudo, CSSEngine engine) throws Exception {
		setMargin(element, BOTTOM, value, pseudo);
	}

	public void applyCSSPropertyMarginLeft(Object element, CSSValue value,
			String pseudo, CSSEngine engine) throws Exception {
		setMargin(element, LEFT, value, pseudo);
	}

	public String retrieveCSSPropertyMargin(Object element, String pseudo,
			CSSEngine engine) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(getMargin(element, TOP, pseudo)).append("px ");
		sb.append(getMargin(element, RIGHT, pseudo)).append("px ");
		sb.append(getMargin(element, BOTTOM, pseudo)).append("px ");
		sb.append(getMargin(element, LEFT, pseudo)).append("px");
		return sb.toString();
	}

	public String retrieveCSSPropertyMarginTop(Object element, String pseudo,
			CSSEngine engine) throws Exception {
		return getMargin(element, TOP, pseudo) + "px";
	}

	public String retrieveCSSPropertyMarginRight(Object element, String pseudo,
			CSSEngine engine) throws Exception {
		return getMargin(element, RIGHT, pseudo) + "px";
	}

	public String retrieveCSSPropertyMarginBottom(Object element,
			String pseudo, CSSEngine engine) throws Exception {
		return getMargin(element, BOTTOM, pseudo) + "px";
	}

	public String retrieveCSSPropertyMarginLeft(Object element, String pseudo,
			CSSEngine engine) throws Exception {
		return getMargin(element, LEFT, pseudo) + "px";
	}

	private void setMargin(Object element, int side, CSSValue value, String pseudo) {
		if(value.getCssValueType() != CSSValue.CSS_PRIMITIVE_VALUE)
			return;
		int pixelValue = (int) ((CSSPrimitiveValue) value).getFloatValue(CSSPrimitiveValue.CSS_PX);

		Widget widget = SWTElementHelpers.getWidget(element);

		if(! (widget instanceof Control))
			return;
		
		Control control = (Control) widget;
		Layout layout = control.getParent().getLayout();
		Object layoutData = control.getLayoutData();

		if (layout instanceof GridLayout) {
			GridData data = layoutData instanceof GridData ? (GridData) layoutData
					: new GridData();
			if (data != null) {
				switch (side) {
				case TOP:
				case BOTTOM:
					data.verticalIndent = pixelValue;
					break;
				case RIGHT:
				case LEFT:
					data.horizontalIndent = pixelValue;
					break;
				}
			}
			layoutData = data;
		} else if (layout instanceof FormLayout) {
			FormData data = layoutData instanceof FormData ? (FormData) layoutData
					: new FormData();
			// unclear what should happen if no FormAttachment exists
			switch (side) {
			case TOP:
				if (data.top != null) {
					data.top.offset = pixelValue;
				}
				break;
			case BOTTOM:
				if (data.bottom != null) {
					data.bottom.offset = pixelValue;
				}
				break;
			case RIGHT:
				if (data.right != null) {
					data.right.offset = pixelValue;
				}
				break;
			case LEFT:
				if (data.left != null) {
					data.left.offset = pixelValue;
				}
				break;
			}
			layoutData = data;
		}
		control.setLayoutData(layoutData);
	}

	private int getMargin(Object element, int side, String pseudo) {
		Widget widget = SWTElementHelpers.getWidget(element);

		if (!(widget instanceof Control))
			return 0;

		Control control = (Control) widget;
		Layout layout = control.getParent().getLayout();
		if (layout instanceof GridLayout) {
			if (!(control.getLayoutData() instanceof GridData)) {
				return 0;
			}
			GridData data = (GridData) control.getLayoutData();
			if (data != null) {
				switch (side) {
				case TOP:
				case BOTTOM:
					return data.verticalIndent;
				case RIGHT:
				case LEFT:
					return data.horizontalIndent;
				}
			}
		} else if (layout instanceof FormLayout) {
			if (!(control.getLayoutData() instanceof GridData)) {
				return 0;
			}
			FormData data = (FormData) control.getLayoutData();
			// could search other children to find their refs?
			switch (side) {
			case TOP:
				return data.top != null ? data.top.offset : 0;
			case BOTTOM:
				return data.bottom != null ? data.bottom.offset : 0;
			case RIGHT:
				return data.right != null ? data.right.offset : 0;
			case LEFT:
				return data.left != null ? data.left.offset : 0;
			}
		}
		return 0;
	}

}
