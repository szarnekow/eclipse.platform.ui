/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.ui.css.swt.properties.css2;

import java.lang.reflect.Method;
import org.eclipse.e4.ui.css.core.dom.properties.css2.AbstractCSSPropertyPaddingHandler;
import org.eclipse.e4.ui.css.core.dom.properties.css2.CSS2PaddingPropertiesImpl;
import org.eclipse.e4.ui.css.core.dom.properties.css2.ICSSPropertyPaddingHandler;
import org.eclipse.e4.ui.css.core.engine.CSSEngine;
import org.eclipse.e4.ui.css.swt.helpers.SWTElementHelpers;
import org.eclipse.e4.ui.widgets.CTabFolder;
import org.eclipse.e4.ui.widgets.CTabFolderRenderer;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.w3c.css.sac.CSSException;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;

public class CSSPropertyPaddingSWTHandler extends
		AbstractCSSPropertyPaddingHandler {

	public final static ICSSPropertyPaddingHandler INSTANCE = new CSSPropertyPaddingSWTHandler();
	
	public boolean applyCSSProperty(Object element, String property,
			CSSValue value, String pseudo, CSSEngine engine) throws Exception {
		Widget widget = SWTElementHelpers.getWidget(element);
		if (widget == null)
			return false;
				
		super.applyCSSProperty(element, property, value, pseudo, engine);
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.e4.ui.css.core.dom.properties.css2.AbstractCSSPropertyPaddingHandler#applyCSSPropertyPadding(java.lang.Object, org.w3c.dom.css.CSSValue, java.lang.String, org.eclipse.e4.ui.css.core.engine.CSSEngine)
	 * If single value then assigned to all four paddings
	 * If four values then assigned top/right/bottom/left
	 * If three values then assigned top=v1, left=v2, right=v2, bottom=v3
	 * If two values then assigned top/bottom=v1, left/right=v2
	 */
	public void applyCSSPropertyPadding(Object element, CSSValue value,
			String pseudo, CSSEngine engine) throws Exception {
		
		CSS2PaddingPropertiesImpl padding = new CSS2PaddingPropertiesImpl();
		// If single value then assigned to all four paddings
		if(value.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
			padding.top = padding.bottom = padding.left = padding.right = value;
			setPadding(element, padding, pseudo);
			return;
		}
		
		if(value.getCssValueType() == CSSValue.CSS_VALUE_LIST) {
			CSSValueList valueList = (CSSValueList) value;
			int length = valueList.getLength();

			if(length < 2 || length > 4)
				throw new CSSException("Invalid padding property list length");
			
			switch (length) {
			case 4:
				// If four values then assigned top/right/bottom/left
				padding.top = valueList.item(0);
				padding.right = valueList.item(1);
				padding.bottom = valueList.item(2);
				padding.left = valueList.item(3);				
				break;
			case 3:
				// If three values then assigned top=v1, left=v2, right=v2, bottom=v3
				padding.top = valueList.item(0);
				padding.right =  valueList.item(1);
				padding.bottom = valueList.item(2);
				padding.left = valueList.item(1);
				break;
			case 2:
				// If two values then assigned top/bottom=v1, left/right=v2
				padding.top = valueList.item(0);
				padding.right = valueList.item(1);
				padding.bottom = valueList.item(0);
				padding.left = valueList.item(1);
			}
			
			setPadding(element, padding, pseudo);
		} else {
			throw new CSSException("Invalid padding property value");
		}
	}

	public void applyCSSPropertyPaddingTop(Object element, CSSValue value,
			String pseudo, CSSEngine engine) throws Exception {
		CSS2PaddingPropertiesImpl padding = new CSS2PaddingPropertiesImpl();
		if(value.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
			padding.top = value;
			setPadding(element, padding, pseudo);
		}
	}

	public void applyCSSPropertyPaddingRight(Object element, CSSValue value,
			String pseudo, CSSEngine engine) throws Exception {
		CSS2PaddingPropertiesImpl padding = new CSS2PaddingPropertiesImpl();
		if(value.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
			padding.right = value;
			setPadding(element, padding, pseudo);
		}
	}

	public void applyCSSPropertyPaddingBottom(Object element, CSSValue value,
			String pseudo, CSSEngine engine) throws Exception {
		CSS2PaddingPropertiesImpl padding = new CSS2PaddingPropertiesImpl();
		if(value.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
			padding.bottom = value;
			setPadding(element, padding, pseudo);
		}
	}

	public void applyCSSPropertyPaddingLeft(Object element, CSSValue value,
			String pseudo, CSSEngine engine) throws Exception {
		CSS2PaddingPropertiesImpl padding = new CSS2PaddingPropertiesImpl();
		if(value.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
			padding.left = value;
			setPadding(element, padding, pseudo);
		}
	}

	public String retrieveCSSPropertyPadding(Object element, String pseudo,
			CSSEngine engine) throws Exception {
		Rectangle pad = getPadding(element, pseudo);
		if (pad == null) {
			return "";
		}
		// int top = pad.x, right = pad.y, bottom = pad.width, left =
		// pad.height;
		return pad.x + "px " + pad.y + "px " + pad.width + "px " + pad.height
				+ "px";
	}

	public String retrieveCSSPropertyPaddingTop(Object element, String pseudo,
			CSSEngine engine) throws Exception {
		Rectangle pad = getPadding(element, pseudo);
		if (pad == null) {
			return "";
		}
		// int top = pad.x, right = pad.y, bottom = pad.width, left =
		// pad.height;
		return pad.x + "px";
	}

	public String retrieveCSSPropertyPaddingRight(Object element, String pseudo,
			CSSEngine engine) throws Exception {
		Rectangle pad = getPadding(element, pseudo);
		if (pad == null) {
			return "";
		}
		// int top = pad.x, right = pad.y, bottom = pad.width, left =
		// pad.height;
		return pad.y + "px";
	}

	public String retrieveCSSPropertyPaddingBottom(Object element,
			String pseudo, CSSEngine engine) throws Exception {
		Rectangle pad = getPadding(element, pseudo);
		if (pad == null) {
			return "";
		}
		// int top = pad.x, right = pad.y, bottom = pad.width, left =
		// pad.height;
		return pad.width + "px";
	}

	public String retrieveCSSPropertyPaddingLeft(Object element, String pseudo,
			CSSEngine engine) throws Exception {
		Rectangle pad = getPadding(element, pseudo);
		if (pad == null) {
			return "";
		}
		// int top = pad.x, right = pad.y, bottom = pad.width, left =
		// pad.height;
		return pad.height + "px";
	}
	
	private void setPadding(Object element, CSSValue value, String pseudo) {
		Widget widget = SWTElementHelpers.getWidget(element);

		int top = 0, bottom = 0, left = 0, right = 0;
		Rectangle pad = getPadding(element, pseudo);
		if (pad != null) {
			// XXX: the following mapping seems strange!
			top = pad.x;
			right = pad.y;
			bottom = pad.width;
			left = pad.height;
		}
		CSS2PaddingPropertiesImpl padding = (CSS2PaddingPropertiesImpl) value;
		CSSValue vTop = padding.top;
		CSSValue vRight = padding.right;
		CSSValue vBottom = padding.bottom;
		CSSValue vLeft = padding.left;

		if (vTop != null
				&& (vTop.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE)) {
			top = (int) ((CSSPrimitiveValue) vTop)
					.getFloatValue(CSSPrimitiveValue.CSS_PX);
		}

		if (vRight != null
				&& (vRight.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE)) {
			right = (int) ((CSSPrimitiveValue) vRight)
					.getFloatValue(CSSPrimitiveValue.CSS_PX);
		}

		if (vBottom != null
				&& (vBottom.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE)) {
			bottom = (int) ((CSSPrimitiveValue) vBottom)
					.getFloatValue(CSSPrimitiveValue.CSS_PX);
		}

		if (vLeft != null
				&& (vLeft.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE)) {
			left = (int) ((CSSPrimitiveValue) vLeft)
					.getFloatValue(CSSPrimitiveValue.CSS_PX);
		}

		if (widget instanceof CTabFolder) {
			CTabFolder folder = (CTabFolder) widget;
			CTabFolderRenderer renderer = ((CTabFolder) folder).getRenderer();
			if (renderer == null)
				return;

			try {
				 // TBD: is there a CTF equivalent ?
				Method m2 = renderer.getClass().getMethod(
						"setPadding",
						new Class[] { int.class, int.class, int.class,
								int.class });
				m2.invoke(renderer, left, right, top, bottom);
			} catch (Exception e) {
				
			}
		} else if (widget instanceof Composite) {
			Composite composite = (Composite) widget;
			if (composite.getLayout() instanceof GridLayout) {
				GridLayout layout = (GridLayout) composite.getLayout();
				// perhaps we should subtract out marginHeigh and marginWidth?
				layout.marginTop = top;
				layout.marginBottom = bottom;
				layout.marginLeft = left;
				layout.marginRight = right;
			} else if (composite.getLayout() instanceof RowLayout) {
				RowLayout layout = (RowLayout) composite.getLayout();
				// perhaps we should subtract out marginHeigh and marginWidth?
				layout.marginTop = top;
				layout.marginBottom = bottom;
				layout.marginLeft = left;
				layout.marginRight = right;
			} else if (composite.getLayout() instanceof FormLayout) {
				FormLayout layout = (FormLayout) composite.getLayout();
				// perhaps we should subtract out marginHeigh and marginWidth?
				layout.marginTop = top;
				layout.marginBottom = bottom;
				layout.marginLeft = left;
				layout.marginRight = right;
			} else if (composite.getLayout() instanceof FillLayout) {
				FillLayout layout = (FillLayout) composite.getLayout();
				// top and left win
				layout.marginHeight = top;
				layout.marginWidth = left;
			}
			composite.layout(false);
		}

	}

	private Rectangle getPadding(Object element, String pseudo) {
		Widget widget = SWTElementHelpers.getWidget(element);

		if (widget instanceof CTabFolder) {
			CTabFolder folder = (CTabFolder) widget;
			CTabFolderRenderer renderer = ((CTabFolder) folder).getRenderer();
			if (renderer == null)
				return null;
			
			try {
				Method m = renderer.getClass().getMethod("getPadding",
						new Class[] {});
				return (Rectangle) m.invoke(renderer);
			} catch (Exception e) {
				
			}
		} else if (widget instanceof Composite) {
			Composite composite = (Composite) widget;
			int top = 0, bottom = 0, left = 0, right = 0;
			if (composite.getLayout() instanceof GridLayout) {
				GridLayout layout = (GridLayout) composite.getLayout();
				// perhaps we should add marginHeight or marginWidth as approp?
				top = layout.marginTop;
				bottom = layout.marginBottom;
				left = layout.marginLeft;
				right = layout.marginRight;
			} else if (composite.getLayout() instanceof RowLayout) {
				RowLayout layout = (RowLayout) composite.getLayout();
				// perhaps we should add marginHeight or marginWidth as approp?
				top = layout.marginTop;
				bottom = layout.marginBottom;
				left = layout.marginLeft;
				right = layout.marginRight;
			} else if (composite.getLayout() instanceof FormLayout) {
				FormLayout layout = (FormLayout) composite.getLayout();
				// perhaps we should add marginHeight or marginWidth as approp?
				top = layout.marginTop;
				bottom = layout.marginBottom;
				left = layout.marginLeft;
				right = layout.marginRight;
			} else if (composite.getLayout() instanceof FillLayout) {
				FillLayout layout = (FillLayout) composite.getLayout();
				top = bottom = layout.marginHeight;
				left = right = layout.marginWidth;
			}
			if (top == 0 && bottom == 0 && left == 0 && right == 0) {
				return null;
			}
			// int top = pad.x, right = pad.y, bottom = pad.width, left =
			// pad.height;
			return new Rectangle(top, right, bottom, left);
		}
		return null;
	}
}
