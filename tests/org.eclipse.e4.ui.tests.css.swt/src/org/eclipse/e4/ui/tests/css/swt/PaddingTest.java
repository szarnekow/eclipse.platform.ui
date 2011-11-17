/*******************************************************************************
 * Copyright (c) 2009,2011 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Brian de Alwis (MT) - adaptation for padding tests
 *******************************************************************************/
package org.eclipse.e4.ui.tests.css.swt;

import org.eclipse.e4.ui.css.core.engine.CSSEngine;
import org.eclipse.e4.ui.css.swt.CSSSWTConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;

public class PaddingTest extends CSSSWTTestCase {

	private static final RGB RED = new RGB(255, 0, 0);
	CSSEngine engine;
	Composite panel;

	private final static int TOP = 0;
	private final static int RIGHT = 1;
	private final static int BOTTOM = 2;
	private final static int LEFT = 3;

	protected Control createTestControl(String styleSheet, Layout layout) {
		Display display = Display.getDefault();
		engine = createEngine(styleSheet, display);

		// Create widgets
		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		panel = new Composite(shell, SWT.NONE);
		panel.setData(CSSSWTConstants.MARGIN_WRAPPER_KEY, true);

		panel.setLayout(layout);

		Button buttonToTest = new Button(panel, SWT.CHECK);
		buttonToTest.setText("Some button text");

		// Apply styles
		engine.applyStyles(shell, true);

		return buttonToTest;
	}

	protected Control createBadControlNoLayout(String styleSheet) {
		Display display = Display.getDefault();
		engine = createEngine(styleSheet, display);

		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		panel = new Composite(shell, SWT.NONE);
		panel.setData(CSSSWTConstants.MARGIN_WRAPPER_KEY);
		// No layout
		Button buttonToTest = new Button(panel, SWT.CHECK);
		buttonToTest.setText("Some button text");
		engine.applyStyles(shell, true);

		return buttonToTest;
	}

	protected Control createBadControlNoComposite(String styleSheet) {
		Display display = Display.getDefault();
		engine = createEngine(styleSheet, display);

		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		// No composite
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
		panel = new Composite(shell, SWT.NONE);
		// No key panel.setData(CSSSWTConstants.MARGIN_WRAPPER_KEY);

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

	public void testGLTopPadding() {
		Control control = createTestControl("Composite { padding-top: 10}",
				new GridLayout());
		assertEquals(10, getPadding(panel, TOP));
		assertEquals(0, getPadding(panel, RIGHT));
		assertEquals(0, getPadding(panel, BOTTOM));
		assertEquals(0, getPadding(panel, LEFT));
	}

	public void testGLRightPadding() {
		Control control = createTestControl("Composite { padding-right: 20}",
				new GridLayout());
		assertEquals(0, getPadding(panel, TOP));
		assertEquals(20, getPadding(panel, RIGHT));
		assertEquals(0, getPadding(panel, BOTTOM));
		assertEquals(0, getPadding(panel, LEFT));
	}

	public void testGLBottomPadding() {
		Control control = createTestControl("Composite { padding-bottom: 30}",
				new GridLayout());
		assertEquals(0, getPadding(panel, TOP));
		assertEquals(0, getPadding(panel, RIGHT));
		assertEquals(30, getPadding(panel, BOTTOM));
		assertEquals(0, getPadding(panel, LEFT));
	}

	public void testGLLeftPadding() {
		Control control = createTestControl("Composite { padding-left: 40}",
				new GridLayout());
		assertEquals(0, getPadding(panel, TOP));
		assertEquals(0, getPadding(panel, RIGHT));
		assertEquals(0, getPadding(panel, BOTTOM));
		assertEquals(40, getPadding(panel, LEFT));
	}

	public void testGLPadding1Value() {
		Control control = createTestControl("Composite { padding: 15}",
				new GridLayout());
		assertEquals(15, getPadding(panel, TOP));
		assertEquals(15, getPadding(panel, RIGHT));
		assertEquals(15, getPadding(panel, BOTTOM));
		assertEquals(15, getPadding(panel, LEFT));
	}

	public void testGLPadding2Values() {
		Control control = createTestControl("Composite { padding: 10 15}",
				new GridLayout());
		assertEquals(10, getPadding(panel, TOP));
		assertEquals(15, getPadding(panel, RIGHT));
		assertEquals(10, getPadding(panel, BOTTOM));
		assertEquals(15, getPadding(panel, LEFT));
	}

	public void testGLPadding4Values() {
		Control control = createTestControl(
				"Composite { padding: 10 15 20 40}", new GridLayout());
		assertEquals(10, getPadding(panel, TOP));
		assertEquals(15, getPadding(panel, RIGHT));
		assertEquals(20, getPadding(panel, BOTTOM));
		assertEquals(40, getPadding(panel, LEFT));
	}

	public void testFLTopPadding() {
		Control control = createTestControl("Composite { padding-top: 10}",
				new FormLayout());
		assertEquals(10, getPadding(panel, TOP));
		assertEquals(0, getPadding(panel, RIGHT));
		assertEquals(0, getPadding(panel, BOTTOM));
		assertEquals(0, getPadding(panel, LEFT));
	}

	public void testFLRightPadding() {
		Control control = createTestControl("Composite { padding-right: 20}",
				new FormLayout());
		assertEquals(0, getPadding(panel, TOP));
		assertEquals(20, getPadding(panel, RIGHT));
		assertEquals(0, getPadding(panel, BOTTOM));
		assertEquals(0, getPadding(panel, LEFT));
	}

	public void testFLBottomPadding() {
		Control control = createTestControl("Composite { padding-bottom: 30}",
				new FormLayout());
		assertEquals(0, getPadding(panel, TOP));
		assertEquals(0, getPadding(panel, RIGHT));
		assertEquals(30, getPadding(panel, BOTTOM));
		assertEquals(0, getPadding(panel, LEFT));
	}

	public void testFLLeftPadding() {
		Control control = createTestControl("Composite { padding-left: 40}",
				new FormLayout());
		assertEquals(0, getPadding(panel, TOP));
		assertEquals(0, getPadding(panel, RIGHT));
		assertEquals(0, getPadding(panel, BOTTOM));
		assertEquals(40, getPadding(panel, LEFT));
	}

	public void testFLPadding1Value() {
		Control control = createTestControl("Composite { padding: 15}",
				new FormLayout());
		assertEquals(15, getPadding(panel, TOP));
		assertEquals(15, getPadding(panel, RIGHT));
		assertEquals(15, getPadding(panel, BOTTOM));
		assertEquals(15, getPadding(panel, LEFT));
	}

	public void testFLPadding2Values() {
		Control control = createTestControl("Composite { padding: 10 15}",
				new FormLayout());
		assertEquals(10, getPadding(panel, TOP));
		assertEquals(15, getPadding(panel, RIGHT));
		assertEquals(10, getPadding(panel, BOTTOM));
		assertEquals(15, getPadding(panel, LEFT));
	}

	public void testFLPadding4Values() {
		Control control = createTestControl(
				"Composite { padding: 10 15 20 40}",
				new FormLayout());
		assertEquals(10, getPadding(panel, TOP));
		assertEquals(15, getPadding(panel, RIGHT));
		assertEquals(20, getPadding(panel, BOTTOM));
		assertEquals(40, getPadding(panel, LEFT));
	}

	/*
	 * Test handling if there is no layout on the control so can't set Paddings
	 */
	public void testPaddingNoLayout() {
		// shouldn't blow up, nothing should happen
		Control control = createBadControlNoLayout("Composite { padding: 10 15 20 40; background-color: #FF0000 }");
		// ensure that any styling after 'Padding:' gets processed
		assertEquals(RED, panel.getBackground().getRGB());
	}

	/*
	 * Test handling if there is no key to tell us we can manipulate the
	 * composite's layout
	 */
	public void testPaddingNoKey() {
		// shouldn't blow up, nothing should happen
		Control control = createBadControlNoKey("Composite { padding: 10 15 20 40; background-color: #FF0000 }");
		// ensure that any styling after 'padding:' gets processed
		assertEquals(RED, panel.getBackground().getRGB());
		assertNotSame(10, getPadding(panel, TOP));
	}

	private int getPadding(Composite panel, int side) {
		// Note: relies on implementation details of how we achieve Paddings on
		// the widgets
		// See CSSPropertyPaddingSWTHandler
		if (panel.getLayout() == null) {
			fail("Composite missing layout");
		}
		if (panel.getLayout() instanceof GridLayout) {
			GridLayout layout = (GridLayout) panel.getLayout();
			switch (side) {
			case TOP:
				return layout.marginTop;
			case BOTTOM:
				return layout.marginBottom;
			case LEFT:
				return layout.marginLeft;
			case RIGHT:
				return layout.marginRight;
			}
		} else if (panel.getLayout() instanceof RowLayout) {
			RowLayout layout = (RowLayout) panel.getLayout();
			switch (side) {
			case TOP:
				return layout.marginTop;
			case BOTTOM:
				return layout.marginBottom;
			case LEFT:
				return layout.marginLeft;
			case RIGHT:
				return layout.marginRight;
			}
		} else if (panel.getLayout() instanceof FormLayout) {
			FormLayout layout = (FormLayout) panel.getLayout();
			switch (side) {
			case TOP:
				return layout.marginTop;
			case BOTTOM:
				return layout.marginBottom;
			case LEFT:
				return layout.marginLeft;
			case RIGHT:
				return layout.marginRight;
			}
		} else if (panel.getLayout() instanceof FillLayout) {
			FillLayout layout = (FillLayout) panel.getLayout();
			switch (side) {
			case TOP:
			case BOTTOM:
				return layout.marginHeight;
			case LEFT:
			case RIGHT:
				return layout.marginWidth;
			}
		}
		return -1;
	}

}
