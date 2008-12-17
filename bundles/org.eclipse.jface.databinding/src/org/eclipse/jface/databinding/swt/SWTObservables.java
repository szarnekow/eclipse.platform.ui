/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Matt Carter - bug 170668
 *     Brad Reynolds - bug 170848
 *     Matthew Hall - bugs 180746, 207844, 245647, 248621, 232917, 194734
 *     Michael Krauter - bug 180223
 *     Boris Bokowski - bug 245647
 *     Tom Schindl - bug 246462
 *******************************************************************************/
package org.eclipse.jface.databinding.swt;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IVetoableValue;
import org.eclipse.core.databinding.observable.value.ValueChangingEvent;
import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.jface.internal.databinding.swt.SWTDelayedObservableValueDecorator;
import org.eclipse.jface.internal.databinding.swt.SWTObservableListDecorator;
import org.eclipse.jface.internal.databinding.swt.SWTObservableValueDecorator;
import org.eclipse.jface.internal.databinding.swt.SWTVetoableValueDecorator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.Widget;

/**
 * A factory for creating observables for SWT widgets
 * 
 * @since 1.1
 * 
 */
public class SWTObservables {

	private static java.util.List realms = new ArrayList();

	/**
	 * Returns the realm representing the UI thread for the given display.
	 * 
	 * @param display
	 * @return the realm representing the UI thread for the given display
	 */
	public static Realm getRealm(final Display display) {
		synchronized (realms) {
			for (Iterator it = realms.iterator(); it.hasNext();) {
				DisplayRealm displayRealm = (DisplayRealm) it.next();
				if (displayRealm.display == display) {
					return displayRealm;
				}
			}
			DisplayRealm result = new DisplayRealm(display);
			realms.add(result);
			return result;
		}
	}

	/**
	 * Returns an observable which delays notification of value change events
	 * from <code>observable</code> until <code>delay</code> milliseconds have
	 * elapsed since the last change event, or until a FocusOut event is
	 * received from the underlying widget (whichever happens first). This
	 * observable helps to boost performance in situations where an observable
	 * has computationally expensive listeners (e.g. changing filters in a
	 * viewer) or many dependencies (master fields with multiple detail fields).
	 * A common use of this observable is to delay validation of user input
	 * until the user stops typing in a UI field.
	 * <p>
	 * To notify about pending changes, the returned observable fires a stale
	 * event when the wrapped observable value fires a change event, and remains
	 * stale until the delay has elapsed and the value change is fired. A call
	 * to {@link IObservableValue#getValue() getValue()} while a value change is
	 * pending will fire the value change immediately, short-circuiting the
	 * delay.
	 * <p>
	 * Note that this observable will not forward {@link ValueChangingEvent}
	 * events from a wrapped {@link IVetoableValue}.
	 * 
	 * @param delay
	 *            the delay in milliseconds
	 * @param observable
	 *            the observable being delayed
	 * @return an observable which delays notification of value change events
	 *         from <code>observable</code> until <code>delay</code>
	 *         milliseconds have elapsed since the last change event.
	 * 
	 * @since 1.2
	 */
	public static ISWTObservableValue observeDelayedValue(int delay,
			ISWTObservableValue observable) {
		return new SWTDelayedObservableValueDecorator(Observables
				.observeDelayedValue(delay, observable), observable.getWidget());
	}

	private static ISWTObservableValue observeWidgetProperty(Widget widget,
			IValueProperty property) {
		return new SWTObservableValueDecorator(property.observeValue(
				getRealm(widget.getDisplay()), widget), widget);
	}

	/**
	 * Returns an observable value tracking the enabled state of the given
	 * control
	 * 
	 * @param control
	 *            the control to observe
	 * @return an observable value tracking the enabled state of the given
	 *         control
	 */
	public static ISWTObservableValue observeEnabled(Control control) {
		return observeWidgetProperty(control, ControlProperties.enabled());
	}

	/**
	 * Returns an observable value tracking the visible state of the given
	 * control
	 * 
	 * @param control
	 *            the control to observe
	 * @return an observable value tracking the visible state of the given
	 *         control
	 */
	public static ISWTObservableValue observeVisible(Control control) {
		return observeWidgetProperty(control, ControlProperties.visible());
	}

	/**
	 * Returns an observable tracking the tooltip text of the given item. The
	 * supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Control</li>
	 * <li>org.eclipse.swt.custom.CTabItem</li>
	 * <li>org.eclipse.swt.widgets.TabItem</li>
	 * <li>org.eclipse.swt.widgets.TableColumn</li>
	 * <li>org.eclipse.swt.widgets.ToolItem</li>
	 * <li>org.eclipse.swt.widgets.TrayItem</li>
	 * <li>org.eclipse.swt.widgets.TreeColumn</li>
	 * </ul>
	 * 
	 * @param widget
	 * @return an observable value tracking the tooltip text of the given item
	 * 
	 * @since 1.3
	 */
	public static ISWTObservableValue observeTooltipText(Widget widget) {
		if (widget instanceof Control) {
			return observeTooltipText((Control) widget);
		}

		IValueProperty property;
		if (widget instanceof CTabItem) {
			property = CTabItemProperties.tooltipText();
		} else if (widget instanceof TabItem) {
			property = TabItemProperties.tooltipText();
		} else if (widget instanceof TableColumn) {
			property = TableColumnProperties.tooltipText();
		} else if (widget instanceof ToolItem) {
			property = ToolItemProperties.tooltipText();
		} else if (widget instanceof TrayItem) {
			property = TrayItemProperties.tooltipText();
		} else if (widget instanceof TreeColumn) {
			property = TreeColumnProperties.tooltipText();
		} else {
			throw new IllegalArgumentException(
					"Item [" + widget.getClass().getName() + "] is not supported."); //$NON-NLS-1$//$NON-NLS-2$
		}

		return observeWidgetProperty(widget, property);
	}

	/**
	 * Returns an observable value tracking the tooltip text of the given
	 * control
	 * 
	 * @param control
	 *            the control to observe
	 * @return an observable value tracking the tooltip text of the given
	 *         control
	 */
	public static ISWTObservableValue observeTooltipText(Control control) {
		return observeWidgetProperty(control, ControlProperties.toolTipText());
	}

	/**
	 * Returns an observable observing the selection attribute of the provided
	 * <code>control</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Spinner</li>
	 * <li>org.eclipse.swt.widgets.Button</li>
	 * <li>org.eclipse.swt.widgets.Combo</li>
	 * <li>org.eclipse.swt.custom.CCombo</li>
	 * <li>org.eclipse.swt.widgets.List</li>
	 * <li>org.eclipse.swt.widgets.Scale</li>
	 * </ul>
	 * 
	 * @param control
	 * @return observable value
	 * @throws IllegalArgumentException
	 *             if <code>control</code> type is unsupported
	 */
	public static ISWTObservableValue observeSelection(Control control) {
		IValueProperty property;
		if (control instanceof Spinner) {
			property = SpinnerProperties.selection();
		} else if (control instanceof Button) {
			property = ButtonProperties.selection();
		} else if (control instanceof Combo) {
			property = ComboProperties.selection();
		} else if (control instanceof CCombo) {
			property = CComboProperties.selection();
		} else if (control instanceof List) {
			property = ListProperties.selection();
		} else if (control instanceof Scale) {
			property = ScaleProperties.selection();
		} else {
			throw new IllegalArgumentException(
					"Widget [" + control.getClass().getName() + "] is not supported."); //$NON-NLS-1$//$NON-NLS-2$
		}

		return observeWidgetProperty(control, property);
	}

	/**
	 * Returns an observable observing the minimum attribute of the provided
	 * <code>control</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Spinner</li>
	 * <li>org.eclipse.swt.widgets.Scale</li>
	 * </ul>
	 * 
	 * @param control
	 * @return observable value
	 * @throws IllegalArgumentException
	 *             if <code>control</code> type is unsupported
	 */
	public static ISWTObservableValue observeMin(Control control) {
		IValueProperty property;
		if (control instanceof Spinner) {
			property = SpinnerProperties.minimum();
		} else if (control instanceof Scale) {
			property = ScaleProperties.minimum();
		} else {
			throw new IllegalArgumentException(
					"Widget [" + control.getClass().getName() + "] is not supported."); //$NON-NLS-1$//$NON-NLS-2$
		}

		return observeWidgetProperty(control, property);
	}

	/**
	 * Returns an observable observing the maximum attribute of the provided
	 * <code>control</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Spinner</li>
	 * <li>org.eclipse.swt.widgets.Scale</li>
	 * </ul>
	 * 
	 * @param control
	 * @return observable value
	 * @throws IllegalArgumentException
	 *             if <code>control</code> type is unsupported
	 */
	public static ISWTObservableValue observeMax(Control control) {
		IValueProperty property;
		if (control instanceof Spinner) {
			property = SpinnerProperties.maximum();
		} else if (control instanceof Scale) {
			property = ScaleProperties.maximum();
		} else {
			throw new IllegalArgumentException(
					"Widget [" + control.getClass().getName() + "] is not supported."); //$NON-NLS-1$//$NON-NLS-2$
		}

		return observeWidgetProperty(control, property);
	}

	/**
	 * Returns an observable observing the text attribute of the provided
	 * <code>control</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Text</li>
	 * <li>org.eclipse.swt.custom.StyledText (as of 1.3)</li>
	 * </ul>
	 * 
	 * @param control
	 * @param event
	 *            event type to register for change events
	 * @return observable value
	 * @throws IllegalArgumentException
	 *             if <code>control</code> type is unsupported
	 */
	public static ISWTObservableValue observeText(Control control, int event) {
		IValueProperty property;
		if (control instanceof Text) {
			property = TextProperties.text(event);
		} else if (control instanceof StyledText) {
			property = StyledTextProperties.text(event);
		} else {
			throw new IllegalArgumentException(
					"Widget [" + control.getClass().getName() + "] is not supported."); //$NON-NLS-1$//$NON-NLS-2$
		}

		return new SWTVetoableValueDecorator(property.observeValue(
				getRealm(control.getDisplay()), control), control);
	}

	/**
	 * Returns an observable observing the text attribute of the provided
	 * <code>widget</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Label</li>
	 * <li>org.eclipse.swt.widgets.Link</li>
	 * <li>org.eclipse.swt.custom.Label</li>
	 * <li>org.eclipse.swt.widgets.Combo</li>
	 * <li>org.eclipse.swt.custom.CCombo</li>
	 * <li>org.eclipse.swt.widgets.Shell</li>
	 * <li>org.eclipse.swt.widgets.Text</li>
	 * <li>org.eclipse.swt.widgets.Item</li>
	 * </ul>
	 * 
	 * @param widget
	 * @return observable value
	 * @throws IllegalArgumentException
	 *             if the type of <code>widget</code> is unsupported
	 * 
	 * @since 1.3
	 */
	public static ISWTObservableValue observeText(Widget widget) {
		if (widget instanceof Control) {
			return observeText((Control) widget);
		} else if (widget instanceof Item) {
			return observeWidgetProperty(widget, ItemProperties.text());
		}

		throw new IllegalArgumentException(
				"Widget [" + widget.getClass().getName() + "] is not supported."); //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 * Returns an observable observing the text attribute of the provided
	 * <code>control</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Label</li>
	 * <li>org.eclipse.swt.widgets.Link (as of 1.2)</li>
	 * <li>org.eclipse.swt.custom.Label</li>
	 * <li>org.eclipse.swt.widgets.Combo</li>
	 * <li>org.eclipse.swt.custom.CCombo</li>
	 * <li>org.eclipse.swt.widgets.Shell</li>
	 * <li>org.eclipse.swt.widgets.Text (as of 1.3)</li>
	 * <li>org.eclipse.swt.custom.StyledText (as of 1.3)</li>
	 * </ul>
	 * 
	 * @param control
	 * @return observable value
	 * @throws IllegalArgumentException
	 *             if <code>control</code> type is unsupported
	 */
	public static ISWTObservableValue observeText(Control control) {
		if (control instanceof Text || control instanceof StyledText) {
			return observeText(control, SWT.None);
		}

		IValueProperty property;
		if (control instanceof Label) {
			property = LabelProperties.text();
		} else if (control instanceof Link) {
			property = LinkProperties.text();
		} else if (control instanceof CLabel) {
			property = CLabelProperties.text();
		} else if (control instanceof Combo) {
			property = ComboProperties.text();
		} else if (control instanceof CCombo) {
			property = CComboProperties.text();
		} else if (control instanceof Shell) {
			property = ShellProperties.text();
		} else {
			throw new IllegalArgumentException(
					"Widget [" + control.getClass().getName() + "] is not supported."); //$NON-NLS-1$//$NON-NLS-2$
		}

		return observeWidgetProperty(control, property);
	}

	/**
	 * Returns an observable observing the items attribute of the provided
	 * <code>control</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Combo</li>
	 * <li>org.eclipse.swt.custom.CCombo</li>
	 * <li>org.eclipse.swt.widgets.List</li>
	 * </ul>
	 * 
	 * @param control
	 * @return observable list
	 * @throws IllegalArgumentException
	 *             if <code>control</code> type is unsupported
	 */
	public static IObservableList observeItems(Control control) {
		IListProperty property;
		if (control instanceof Combo) {
			property = ComboProperties.items();
		} else if (control instanceof CCombo) {
			property = CComboProperties.items();
		} else if (control instanceof List) {
			property = ListProperties.items();
		} else {
			throw new IllegalArgumentException(
					"Widget [" + control.getClass().getName() + "] is not supported."); //$NON-NLS-1$//$NON-NLS-2$
		}

		return new SWTObservableListDecorator(property.observeList(
				getRealm(control.getDisplay()), control), control);
	}

	/**
	 * Returns an observable observing the single selection index attribute of
	 * the provided <code>control</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Table</li>
	 * <li>org.eclipse.swt.widgets.Combo</li>
	 * <li>org.eclipse.swt.custom.CCombo</li>
	 * <li>org.eclipse.swt.widgets.List</li>
	 * </ul>
	 * 
	 * @param control
	 * @return observable value
	 * @throws IllegalArgumentException
	 *             if <code>control</code> type is unsupported
	 */
	public static ISWTObservableValue observeSingleSelectionIndex(
			Control control) {
		IValueProperty property;
		if (control instanceof Table) {
			property = TableProperties.singleSelectionIndex();
		} else if (control instanceof Combo) {
			property = ComboProperties.singleSelectionIndex();
		} else if (control instanceof CCombo) {
			property = CComboProperties.singleSelectionIndex();
		} else if (control instanceof List) {
			property = ListProperties.singleSelectionIndex();
		} else {
			throw new IllegalArgumentException(
					"Widget [" + control.getClass().getName() + "] is not supported."); //$NON-NLS-1$//$NON-NLS-2$
		}

		return observeWidgetProperty(control, property);
	}

	/**
	 * Returns an observable value tracking the foreground color of the given
	 * control
	 * 
	 * @param control
	 *            the control to observe
	 * @return an observable value tracking the foreground color of the given
	 *         control
	 */
	public static ISWTObservableValue observeForeground(Control control) {
		return observeWidgetProperty(control, ControlProperties.foreground());
	}

	/**
	 * Returns an observable value tracking the background color of the given
	 * control
	 * 
	 * @param control
	 *            the control to observe
	 * @return an observable value tracking the background color of the given
	 *         control
	 */
	public static ISWTObservableValue observeBackground(Control control) {
		return observeWidgetProperty(control, ControlProperties.background());
	}

	/**
	 * Returns an observable value tracking the font of the given control.
	 * 
	 * @param control
	 *            the control to observe
	 * @return an observable value tracking the font of the given control
	 */
	public static ISWTObservableValue observeFont(Control control) {
		return observeWidgetProperty(control, ControlProperties.font());
	}

	/**
	 * Returns an observable value tracking the size of the given control.
	 * 
	 * @param control
	 *            the control to observe
	 * @return an observable value tracking the size of the given control
	 * @since 1.3
	 */
	public static ISWTObservableValue observeSize(Control control) {
		return observeWidgetProperty(control, ControlProperties.size());
	}

	/**
	 * Returns an observable value tracking the location of the given control.
	 * 
	 * @param control
	 *            the control to observe
	 * @return an observable value tracking the location of the given control
	 * @since 1.3
	 */
	public static ISWTObservableValue observeLocation(Control control) {
		return observeWidgetProperty(control, ControlProperties.location());
	}

	/**
	 * Returns an observable value tracking the focus of the given control.
	 * 
	 * @param control
	 *            the control to observe
	 * @return an observable value tracking the focus of the given control
	 * @since 1.3
	 */
	public static ISWTObservableValue observeFocus(Control control) {
		return observeWidgetProperty(control, ControlProperties.focused());
	}

	/**
	 * Returns an observable value tracking the bounds of the given control.
	 * 
	 * @param control
	 *            the control to observe
	 * @return an observable value tracking the bounds of the given control
	 * @since 1.3
	 */
	public static ISWTObservableValue observeBounds(Control control) {
		return observeWidgetProperty(control, ControlProperties.bounds());
	}

	/**
	 * Returns an observable observing the editable attribute of the provided
	 * <code>control</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Text</li>
	 * </ul>
	 * 
	 * @param control
	 * @return observable value
	 * @throws IllegalArgumentException
	 *             if <code>control</code> type is unsupported
	 */
	public static ISWTObservableValue observeEditable(Control control) {
		IValueProperty property;
		if (control instanceof Text) {
			property = TextProperties.editable();
		} else {
			throw new IllegalArgumentException(
					"Widget [" + control.getClass().getName() + "] is not supported."); //$NON-NLS-1$//$NON-NLS-2$
		}

		return observeWidgetProperty(control, property);
	}

	private static class DisplayRealm extends Realm {
		private Display display;

		/**
		 * @param display
		 */
		private DisplayRealm(Display display) {
			this.display = display;
		}

		public boolean isCurrent() {
			return Display.getCurrent() == display;
		}

		public void asyncExec(final Runnable runnable) {
			Runnable safeRunnable = new Runnable() {
				public void run() {
					safeRun(runnable);
				}
			};
			if (!display.isDisposed()) {
				display.asyncExec(safeRunnable);
			}
		}

		public void timerExec(int milliseconds, final Runnable runnable) {
			if (!display.isDisposed()) {
				Runnable safeRunnable = new Runnable() {
					public void run() {
						safeRun(runnable);
					}
				};
				display.timerExec(milliseconds, safeRunnable);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode() {
			return (display == null) ? 0 : display.hashCode();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final DisplayRealm other = (DisplayRealm) obj;
			if (display == null) {
				if (other.display != null)
					return false;
			} else if (!display.equals(other.display))
				return false;
			return true;
		}
	}
}
