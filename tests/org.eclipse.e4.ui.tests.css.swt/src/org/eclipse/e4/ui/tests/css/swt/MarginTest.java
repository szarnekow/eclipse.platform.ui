/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.ui.tests.css.swt;

import org.eclipse.e4.ui.css.core.engine.CSSEngine;
import org.eclipse.e4.ui.css.swt.CSSSWTConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;

public class MarginTest extends CSSSWTTestCase {

	private static final RGB RED = new RGB(255, 0, 0);
	CSSEngine engine;	
	
	private final static int TOP = 0;
	private final static int RIGHT = 1;
	private final static int BOTTOM = 2;
	private final static int LEFT = 3;
	
	protected Control createTestControl(String styleSheet, Layout layout) {
		Display display = Display.getDefault();
		engine = createEngine(styleSheet, display);
		
		// Create widgets
		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		Composite panel = new Composite(shell, SWT.NONE);
		panel.setData(CSSSWTConstants.MARGIN_WRAPPER_KEY, true);
		
		panel.setLayout(layout);

		Button buttonToTest = new Button(panel, SWT.CHECK);
		buttonToTest.setText("Some button text");

		if (layout instanceof FormLayout) {
			FormData fd = new FormData();
			fd.top = new FormAttachment();
			fd.left = new FormAttachment();
			fd.right = new FormAttachment();
			fd.bottom = new FormAttachment();
			buttonToTest.setLayoutData(fd);
		}

		// Apply styles
		engine.applyStyles(shell, true);

		return buttonToTest;
	}

	protected Control createBadControlNoLayout(String styleSheet) {
		Display display = Display.getDefault();
		engine = createEngine(styleSheet, display);
		
		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		Composite panel = new Composite(shell, SWT.NONE);
		panel.setData(CSSSWTConstants.MARGIN_WRAPPER_KEY);
		//No layout
		Button buttonToTest = new Button(panel, SWT.CHECK);
		buttonToTest.setText("Some button text");
		engine.applyStyles(shell, true);

		return buttonToTest;
	}

	protected Control createBadControlNoComposite(String styleSheet) {
		Display display = Display.getDefault();
		engine = createEngine(styleSheet, display);
		
		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		//No composite
		Button buttonToTest = new Button(shell, SWT.CHECK);
		buttonToTest.setText("Some button text");
		engine.applyStyles(shell, true);

		return buttonToTest;
	}
	
	protected Control createBadControlNoKey(String styleSheet) {
		Display display = Display.getDefault();
		engine = createEngine(styleSheet, display);

		// Create widgets
		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		Composite panel = new Composite(shell, SWT.NONE);
		//No key panel.setData(CSSSWTConstants.MARGIN_WRAPPER_KEY);

		// Must be grid, see CSSPropertyMarginSWTHandler
		GridLayout layout = new GridLayout();
		layout.marginTop = 0;
		layout.marginRight = 0;
		layout.marginBottom = 0;
		layout.marginLeft = 0;
		panel.setLayout(layout);

		Button buttonToTest = new Button(panel, SWT.CHECK);
		buttonToTest.setText("Some button text");

		// Apply styles
		engine.applyStyles(shell, true);

		return buttonToTest;
	}

	public void testGLTopMargin() {
		Control control = createTestControl("Button { margin-top: 10}",
				new GridLayout());
		assertEquals(10, getMargin(control, TOP));
		assertEquals(0, getMargin(control, RIGHT));
		// assertEquals(0, getMargin(control, BOTTOM)); // with GridLayout will
		// be 10 too
		assertEquals(0, getMargin(control, LEFT));
	}

	public void testGLRightMargin() {
		Control control = createTestControl("Button { margin-right: 20}",
				new GridLayout());
		assertEquals(0, getMargin(control, TOP));
		assertEquals(20, getMargin(control, RIGHT));
		assertEquals(0, getMargin(control, BOTTOM));
		// assertEquals(0, getMargin(control, LEFT)); // with GridLayout will be
		// 20 too
	}

	public void testGLBottomMargin() {
		Control control = createTestControl("Button { margin-bottom: 30}",
				new GridLayout());
		// assertEquals(0, getMargin(control, TOP)); // with GridLayout will be
		// 30 too
		assertEquals(0, getMargin(control, RIGHT));
		assertEquals(30, getMargin(control, BOTTOM));
		assertEquals(0, getMargin(control, LEFT));
	}

	public void testGLLeftMargin() {
		Control control = createTestControl("Button { margin-left: 40}",
				new GridLayout());
		assertEquals(0, getMargin(control, TOP));
		// assertEquals(0, getMargin(control, RIGHT)); // with GridLayout will
		// be 40 too
		assertEquals(0, getMargin(control, BOTTOM));
		assertEquals(40, getMargin(control, LEFT));
	}

	public void testGLMargin1Value() {
		Control control = createTestControl("Button { margin: 15}",
				new GridLayout());
		assertEquals(15, getMargin(control, TOP));
		assertEquals(15, getMargin(control, RIGHT));
		assertEquals(15, getMargin(control, BOTTOM));
		assertEquals(15, getMargin(control, LEFT));
	}
	
	public void testGLMargin2Values() {
		Control control = createTestControl("Button { margin: 10 15}",
				new GridLayout());
		assertEquals(10, getMargin(control, TOP));
		assertEquals(15, getMargin(control, RIGHT));
		assertEquals(10, getMargin(control, BOTTOM));
		assertEquals(15, getMargin(control, LEFT));
	}

	// disabled as GridLayout doesn't support 4 independent margins
	// public void testGLMargin4Values() {
	// Control control = createTestControl("Button { margin: 10 15 20 40}", new
	// GridLayout());
	// assertEquals(10, getMargin(control, TOP));
	// assertEquals(15, getMargin(control, RIGHT));
	// assertEquals(20, getMargin(control, BOTTOM));
	// assertEquals(40, getMargin(control, LEFT));
	// }
	
	public void testFLTopMargin() {
		Control control = createTestControl("Button { margin-top: 10}",
				new FormLayout());
		assertEquals(10, getMargin(control, TOP));
		assertEquals(0, getMargin(control, RIGHT));
		// assertEquals(0, getMargin(control, BOTTOM)); // with GridLayout will
		// be 10 too
		assertEquals(0, getMargin(control, LEFT));
	}

	public void testFLRightMargin() {
		Control control = createTestControl("Button { margin-right: 20}",
				new FormLayout());
		assertEquals(0, getMargin(control, TOP));
		assertEquals(20, getMargin(control, RIGHT));
		assertEquals(0, getMargin(control, BOTTOM));
		// assertEquals(0, getMargin(control, LEFT)); // with GridLayout will be
		// 20 too
	}

	public void testFLBottomMargin() {
		Control control = createTestControl("Button { margin-bottom: 30}",
				new FormLayout());
		// assertEquals(0, getMargin(control, TOP)); // with GridLayout will be
		// 30 too
		assertEquals(0, getMargin(control, RIGHT));
		assertEquals(30, getMargin(control, BOTTOM));
		assertEquals(0, getMargin(control, LEFT));
	}

	public void testFLLeftMargin() {
		Control control = createTestControl("Button { margin-left: 40}",
				new FormLayout());
		assertEquals(0, getMargin(control, TOP));
		// assertEquals(0, getMargin(control, RIGHT)); // with GridLayout will
		// be 40 too
		assertEquals(0, getMargin(control, BOTTOM));
		assertEquals(40, getMargin(control, LEFT));
	}

	public void testFLMargin1Value() {
		Control control = createTestControl("Button { margin: 15}",
				new FormLayout());
		assertEquals(15, getMargin(control, TOP));
		assertEquals(15, getMargin(control, RIGHT));
		assertEquals(15, getMargin(control, BOTTOM));
		assertEquals(15, getMargin(control, LEFT));
	}

	public void testFLMargin2Values() {
		Control control = createTestControl("Button { margin: 10 15}",
				new FormLayout());
		assertEquals(10, getMargin(control, TOP));
		assertEquals(15, getMargin(control, RIGHT));
		assertEquals(10, getMargin(control, BOTTOM));
		assertEquals(15, getMargin(control, LEFT));
	}

	public void testFLMargin4Values() {
		Control control = createTestControl("Button { margin: 10 15 20 40}",
				new FormLayout());
		assertEquals(10, getMargin(control, TOP));
		assertEquals(15, getMargin(control, RIGHT));
		assertEquals(20, getMargin(control, BOTTOM));
		assertEquals(40, getMargin(control, LEFT));
	}

	/*
	 * Test handling if there is no layout on the control so can't set margins
	 */	
	public void testMarginNoLayout() {
		//shouldn't blow up, nothing should happen
		Control control = createBadControlNoLayout("Button { margin: 10 15 20 40; background-color: #FF0000 }");
		//ensure that any styling after 'margin:' gets processed
		assertEquals(RED, control.getBackground().getRGB());
	}

	/*
	 * Test handling if there is no composite on the control so can't set margins
	 */	
	public void testMarginNoComposite() {
		//shouldn't blow up, nothing should happen
		Control control = createBadControlNoComposite("Button { margin: 10 15 20 40; background-color: #FF0000 }");
		//ensure that any styling after 'margin:' gets processed
		assertEquals(RED, control.getBackground().getRGB());
	}

	/*
	 * Test handling if there is no key to tell us we can manipulate the composite's layout
	 */	
	public void testMarginNoKey() {
		//shouldn't blow up, nothing should happen
		Control control = createBadControlNoKey("Button { margin: 10 15 20 40; background-color: #FF0000 }");
		//ensure that any styling after 'margin:' gets processed
		assertEquals(RED, control.getBackground().getRGB());
		assertNotSame(10, getMargin(control, TOP));
	}
	
	
	private int getMargin(Control control, int side) {
		//Note: relies on implementation details of how we achieve margins on the widgets
		//See CSSPropertyMarginSWTHandler
		if (control.getLayoutData() instanceof GridData) {
			GridData data = (GridData) control.getLayoutData();
			switch (side) {
			case TOP:
			case BOTTOM:
				return data.verticalIndent;
			case LEFT:
			case RIGHT:
				return data.horizontalIndent;
			}
		} else if (control.getLayoutData() instanceof FormData) {
			FormData data = (FormData) control.getLayoutData();
			switch (side) {
			case TOP:
				return data.top.offset;
			case BOTTOM:
				return data.bottom.offset;
			case LEFT:
				return data.left.offset;
			case RIGHT:
				return data.right.offset;
			}
		}
		return -1;
	}

}
