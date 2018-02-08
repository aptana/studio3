/**
 * Appcelerator Titanium Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package com.aptana.ui.dialogs;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import com.aptana.core.util.StringUtil;
import com.aptana.ui.util.WorkbenchBrowserUtil;

/**
 * An enhanced version of GenericInfoPopupDialog that uses the Link control to target multiple hyper-links. The default
 * implementation utilizes the anchor "href" attributes. This behavior can be overridden with a custom SelectionAdapter
 * 
 * @author Nam Le <nle@appcelerator.com>
 */
public class HyperlinkInfoPopupDialog extends GenericInfoPopupDialog
{
	private final SelectionListener selectionListener;

	public HyperlinkInfoPopupDialog(Shell parentShell, String title, String message, SelectionListener selectionListener)
	{
		super(parentShell, title, message);

		if (selectionListener == null)
		{
			selectionListener = new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					String text = e.text;
					if (!StringUtil.isEmpty(text))
					{
						WorkbenchBrowserUtil.openURL(text);
					}
				}
			};
		}
		this.selectionListener = selectionListener;

		// Null out the click listener. This dialog only listens for the closing of the dialog and hyper-link navigation
		clickListener = null;
	}

	protected Control createDialogArea(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(GridLayoutFactory.swtDefaults().create());
		main.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		Link infoLabel = new Link(main, SWT.WRAP);
		infoLabel.setText(message);
		infoLabel.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		infoLabel.addSelectionListener(selectionListener);
		return main;
	}

}
