/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.formatter.preferences;

import java.net.URL;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.aptana.editor.html.formatter.HTMLFormatterConstants;
import com.aptana.formatter.ui.IFormatterControlManager;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.preferences.FormatterModifyTabPage;
import com.aptana.formatter.ui.util.SWTFactory;

/**
 * Whitespace configuration tab for the HTML code formatter.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class HTMLFormatterWhitespacesPage extends FormatterModifyTabPage
{

	private static final String WHITESPACES_PREVIEW_FILE = "whitespace-preview.html"; //$NON-NLS-1$

	/**
	 * Constructs a new HTMLFormatterWhitespacesPage
	 * 
	 * @param dialog
	 */
	public HTMLFormatterWhitespacesPage(IFormatterModifyDialog dialog)
	{
		super(dialog);
	}

	/*
	 * (non-Javadoc)
	 * @seecom.aptana.formatter.ui.preferences.FormatterModifyTabPage#createOptions(com.aptana.formatter.ui.
	 * IFormatterControlManager, org.eclipse.swt.widgets.Composite)
	 */
	protected void createOptions(IFormatterControlManager manager, Composite parent)
	{
		// Spaces Group
		Group spacesGroup = SWTFactory.createGroup(parent,
				Messages.HTMLFormatterWhitespacesPage_spacesElementsGroupTitle, 1, 1, GridData.FILL_HORIZONTAL);
		manager.createCheckbox(spacesGroup, HTMLFormatterConstants.TRIM_SPACES,
				Messages.HTMLFormatterWhitespacesPage_trimSpaces);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.preferences.FormatterModifyTabPage#getPreviewContent()
	 */
	protected URL getPreviewContent()
	{
		return getClass().getResource(WHITESPACES_PREVIEW_FILE);
	}

}
