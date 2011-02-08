/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * A common information control that can also control the colors of the status-text.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class CommonInformationControl extends DefaultInformationControl
{
	private Label statusLabel;

	/**
	 * Constructs a new CommonInformationControl.
	 * 
	 * @param parent
	 * @param statusText
	 * @param informationPresenter
	 */
	public CommonInformationControl(Shell parent, String statusText, IInformationPresenter informationPresenter)
	{
		super(parent, (String) null, informationPresenter);
		createStatusComposite(statusText);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.DefaultInformationControl#setBackgroundColor(org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setBackgroundColor(Color background)
	{
		super.setBackgroundColor(background);
		if (statusLabel != null)
		{
			statusLabel.setBackground(background);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.DefaultInformationControl#setForegroundColor(org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setForegroundColor(Color foreground)
	{
		super.setForegroundColor(foreground);
		if (statusLabel != null)
		{
			statusLabel.setForeground(foreground);
		}
	}

	/*
	 * Create the status composite.
	 */
	private void createStatusComposite(String statusText)
	{
		if (statusText == null || statusText.trim().length() == 0)
		{
			return;
		}
		Composite statusComposite = new Composite(getShell(), SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.BOTTOM, true, false);
		statusComposite.setLayoutData(gd);
		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		statusComposite.setLayout(layout);
		Label separator = new Label(statusComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// Add the status label
		statusLabel = new Label(statusComposite, SWT.RIGHT);
		statusLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		statusLabel.setText(statusText);

		FontData[] fonts = JFaceResources.getDialogFont().getFontData();
		if (fonts.length > 0)
		{
			FontData fd = fonts[0];
			fd.setHeight(fd.getHeight() * 8 / 10);
			statusLabel.setFont(new Font(statusLabel.getDisplay(), fd));
		}
	}
}
