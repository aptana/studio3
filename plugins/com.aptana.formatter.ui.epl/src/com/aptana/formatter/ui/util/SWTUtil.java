/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.formatter.ui.util;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;

import com.aptana.ui.util.UIUtils;

/**
 * Utility class to simplify access to some SWT resources.
 */
public class SWTUtil
{

	/**
	 * Returns the standard display to be used. The method first checks, if the thread calling this method has an
	 * associated disaply. If so, this display is returned. Otherwise the method returns the default display.
	 */
	public static Display getStandardDisplay()
	{
		Display display = UIUtils.getDisplay();
		if (display == null)
		{
			display = Display.getCurrent();
		}
		if (display == null)
		{
			display = Display.getDefault();
		}
		return display;
	}

	/**
	 * Returns the shell for the given widget. If the widget doesn't represent a SWT object that manage a shell,
	 * <code>null</code> is returned.
	 * 
	 * @return the shell for the given widget
	 */
	public static Shell getShell(Widget widget)
	{
		if (widget instanceof Control)
			return ((Control) widget).getShell();
		if (widget instanceof Caret)
			return ((Caret) widget).getParent().getShell();
		if (widget instanceof DragSource)
			return ((DragSource) widget).getControl().getShell();
		if (widget instanceof DropTarget)
			return ((DropTarget) widget).getControl().getShell();
		if (widget instanceof Menu)
			return ((Menu) widget).getParent().getShell();
		if (widget instanceof ScrollBar)
			return ((ScrollBar) widget).getParent().getShell();

		return null;
	}

	public static int getTableHeightHint(Table table, int rows)
	{
		if (table.getFont().equals(JFaceResources.getDefaultFont()))
			table.setFont(JFaceResources.getDialogFont());
		int result = table.getItemHeight() * rows + table.getHeaderHeight();
		if (table.getLinesVisible())
			result += table.getGridLineWidth() * (rows - 1);
		return result;
	}

	/**
	 * Creates the tab folder for displaying the composite fragments
	 * 
	 * @param parent
	 */
	public static CTabFolder createTabFolder(Composite parent)
	{
		Display display = getStandardDisplay();
		Color c1 = display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND), c2 = display
				.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT);
		CTabFolder tabs = new CTabFolder(parent, SWT.NO_REDRAW_RESIZE | SWT.NO_TRIM | SWT.FLAT);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		tabs.setSelectionBackground(new Color[] { c1, c2 }, new int[] { 100 }, true);
		tabs.setSelectionForeground(getStandardDisplay().getSystemColor(SWT.COLOR_TITLE_FOREGROUND));
		tabs.setSimple(PlatformUI.getPreferenceStore().getBoolean(
				IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS));
		tabs.setLayoutData(gd);
		tabs.setBorderVisible(true);
		tabs.setFont(parent.getFont());
		return tabs;
	}

	/**
	 * This method allows us to open the preference dialog on the specific page, in this case the perspective page
	 * 
	 * @param id
	 *            the id of pref page to show
	 * @param page
	 *            the actual page to show Copied from org.eclipse.debug.internal.ui.SWTUtil
	 */
	public static void showPreferencePage(String id, IPreferencePage page)
	{
		final IPreferenceNode targetNode = new PreferenceNode(id, page);
		PreferenceManager manager = new PreferenceManager();
		manager.addToRoot(targetNode);
		final PreferenceDialog dialog = new PreferenceDialog(UIUtils.getActiveShell(), manager);
		BusyIndicator.showWhile(getStandardDisplay(), new Runnable()
		{
			public void run()
			{
				dialog.create();
				dialog.setMessage(targetNode.getLabelText());
				dialog.open();
			}
		});
	}

	/**
	 * Returns a width hint for a button control.
	 */
	public static int getButtonWidthHint(Button button)
	{
		button.setFont(JFaceResources.getDialogFont());
		PixelConverter converter = new PixelConverter(button);
		int widthHint = converter.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		return Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
	}

	/**
	 * Sets width and height hint for the button control. <b>Note:</b> This is a NOP if the button's layout data is not
	 * an instance of <code>GridData</code>.
	 * 
	 * @param the
	 *            button for which to set the dimension hint
	 */
	public static void setButtonDimensionHint(Button button)
	{
		Assert.isNotNull(button);
		Object gd = button.getLayoutData();
		if (gd instanceof GridData)
		{
			((GridData) gd).widthHint = getButtonWidthHint(button);
			((GridData) gd).horizontalAlignment = GridData.FILL;
		}
	}

	/**
	 * Creates and returns a new push button with the given label and/or image.
	 * 
	 * @param parent
	 *            parent control
	 * @param label
	 *            button label or <code>null</code>
	 * @param image
	 *            image of <code>null</code>
	 * @return a new push button
	 */
	public static Button createPushButton(Composite parent, String label, Image image)
	{
		Button button = new Button(parent, SWT.PUSH);
		button.setFont(parent.getFont());
		if (image != null)
		{
			button.setImage(image);
		}
		if (label != null)
		{
			button.setText(label);
		}
		GridData gd = new GridData();
		button.setLayoutData(gd);
		SWTUtil.setButtonDimensionHint(button);
		return button;
	}

	/**
	 * Creates and returns a new radio button with the given label.
	 * 
	 * @param parent
	 *            parent control
	 * @param label
	 *            button label or <code>null</code>
	 * @return a new radio button
	 */
	public static Button createRadioButton(Composite parent, String label)
	{
		Button button = new Button(parent, SWT.RADIO);
		button.setFont(parent.getFont());
		if (label != null)
		{
			button.setText(label);
		}
		GridData gd = new GridData();
		button.setLayoutData(gd);
		SWTUtil.setButtonDimensionHint(button);
		return button;
	}

	/**
	 * Creates a new label widget
	 * 
	 * @param parent
	 *            the parent composite to add this label widget to
	 * @param text
	 *            the text for the label
	 * @param hspan
	 *            the horizontal span to take up in the parent composite
	 * @return the new label
	 * @since 3.2
	 */
	public static Label createLabel(Composite parent, String text, int hspan)
	{
		Label l = new Label(parent, SWT.NONE);
		l.setFont(parent.getFont());
		l.setText(text);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = hspan;
		l.setLayoutData(gd);
		return l;
	}

	/**
	 * Creates a new text widget
	 * 
	 * @param parent
	 *            the parent composite to add this text widget to
	 * @param hspan
	 *            the horizontal span to take up on the parent composite
	 * @return the new text widget
	 * @since 3.2
	 */
	public static Text createSingleText(Composite parent, int hspan)
	{
		Text t = new Text(parent, SWT.SINGLE | SWT.BORDER);
		t.setFont(parent.getFont());
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = hspan;
		t.setLayoutData(gd);
		return t;
	}

	/**
	 * Creates a Group widget
	 * 
	 * @param parent
	 *            the parent composite to add this group to
	 * @param text
	 *            the text for the heading of the group
	 * @param columns
	 *            the number of columns within the group
	 * @param hspan
	 *            the horizontal span the group should take up on the parent
	 * @param fill
	 *            the style for how this composite should fill into its parent
	 * @return the new group
	 * @since 3.2
	 */
	public static Group createGroup(Composite parent, String text, int columns, int hspan, int fill)
	{
		Group g = new Group(parent, SWT.NONE);
		g.setLayout(new GridLayout(columns, false));
		g.setText(text);
		g.setFont(parent.getFont());
		GridData gd = new GridData(fill);
		gd.horizontalSpan = hspan;
		g.setLayoutData(gd);
		return g;
	}
}
