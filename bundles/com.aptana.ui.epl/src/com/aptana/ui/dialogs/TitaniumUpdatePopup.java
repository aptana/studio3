/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.dialogs;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.aptana.core.util.EclipseUtil;
import com.aptana.ui.epl.UIEplPlugin;

/**
 * TitaniumUpdatePopup is a more simple version of AutomaticUpdatesPopup that has been modified for Titanium Updates.
 * FIXME Move to com.appcelerator.titanium.update?
 * 
 * @author ayeung
 */
public class TitaniumUpdatePopup extends PopupDialog
{
	MouseListener clickListener;
	Composite dialogArea;
	private static final int POPUP_OFFSET = 20;

	public TitaniumUpdatePopup(Shell parentShell, final Runnable updateAction)
	{
		super(parentShell, PopupDialog.INFOPOPUPRESIZE_SHELLSTYLE | SWT.MODELESS, false, true, true, false, false,
				MessageFormat.format(EplMessages.TitaniumUpdatePopup_update_title, EclipseUtil.getStudioPrefix()), null);

		clickListener = new MouseAdapter()
		{
			public void mouseDown(MouseEvent e)
			{
				updateAction.run();
				close();
			}
		};

	}

	protected Control createDialogArea(Composite parent)
	{
		dialogArea = new Composite(parent, SWT.NONE);
		dialogArea.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		dialogArea.setLayout(layout);
		dialogArea.addMouseListener(clickListener);

		// The "click to update" label
		Label infoLabel = new Label(dialogArea, SWT.NONE);
		infoLabel.setText(MessageFormat.format(EplMessages.TitaniumUpdatePopup_update_detail,
				EclipseUtil.getStudioPrefix()));
		infoLabel.setLayoutData(new GridData(GridData.FILL_BOTH));
		infoLabel.addMouseListener(clickListener);

		return dialogArea;

	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#getInitialLocation(org.eclipse.swt.graphics.Point)
	 */
	protected Point getInitialLocation(Point initialSize)
	{
		Shell parent = getParentShell();
		Point parentSize, parentLocation;

		if (parent != null)
		{
			parentSize = parent.getSize();
			parentLocation = parent.getLocation();
		}
		else
		{
			Rectangle bounds = getShell().getDisplay().getBounds();
			parentSize = new Point(bounds.width, bounds.height);
			parentLocation = new Point(0, 0);
		}
		// We have to take parent location into account because SWT considers all
		// shell locations to be in display coordinates, even if the shell is parented.
		return new Point(parentSize.x - initialSize.x + parentLocation.x - POPUP_OFFSET, parentSize.y - initialSize.y
				+ parentLocation.y - POPUP_OFFSET);
	}

	/*
	 * Overridden so that clicking in the title menu area closes the dialog. Also creates a close box menu in the title
	 * area.
	 */
	protected Control createTitleMenuArea(Composite parent)
	{
		Composite titleComposite = (Composite) super.createTitleMenuArea(parent);
		titleComposite.addMouseListener(clickListener);

		ToolBar toolBar = new ToolBar(titleComposite, SWT.FLAT);
		ToolItem closeButton = new ToolItem(toolBar, SWT.PUSH, 0);

		GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).applyTo(toolBar);
		closeButton.setImage(UIEplPlugin.getDefault().getImageRegistry().get((UIEplPlugin.IMG_TOOL_CLOSE)));
		closeButton.setHotImage(UIEplPlugin.getDefault().getImageRegistry().get((UIEplPlugin.IMG_TOOL_CLOSE_HOT)));
		closeButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				close();
			}
		});
		toolBar.addMouseListener(new MouseAdapter()
		{
			public void mouseDown(MouseEvent e)
			{
				close();
			}
		});
		return titleComposite;
	}

	/*
	 * Overridden to adjust the span of the title label. Reachy, reachy.... (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.PopupDialog#createTitleControl(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createTitleControl(Composite parent)
	{
		Control control = super.createTitleControl(parent);
		Object data = control.getLayoutData();
		if (data instanceof GridData)
		{
			((GridData) data).horizontalSpan = 1;
		}
		return control;
	}

}
