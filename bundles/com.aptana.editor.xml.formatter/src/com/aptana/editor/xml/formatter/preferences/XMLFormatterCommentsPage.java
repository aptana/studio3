/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.formatter.preferences;

import java.net.URL;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.aptana.editor.xml.formatter.XMLFormatterConstants;
import com.aptana.formatter.ui.IFormatterControlManager;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.preferences.FormatterModifyTabPage;
import com.aptana.formatter.ui.util.SWTFactory;

public class XMLFormatterCommentsPage extends FormatterModifyTabPage
{
	private static final String COMMENTS_PREVIEW_NAME = "preview.xml"; //$NON-NLS-1$

	public XMLFormatterCommentsPage(IFormatterModifyDialog dialog)
	{
		super(dialog);
	}

	protected void createOptions(IFormatterControlManager manager, Composite parent)
	{
		Group commentGroup = SWTFactory.createGroup(parent, Messages.XMLFormatterCommentsPage_commentsGroupLabel, 2, 1,
				GridData.FILL_HORIZONTAL);
		manager.createCheckbox(commentGroup, XMLFormatterConstants.WRAP_COMMENTS,
				Messages.XMLFormatterCommentsPage_enableWrappingLabel, 2);
		manager.createNumber(commentGroup, XMLFormatterConstants.WRAP_COMMENTS_LENGTH,
				Messages.XMLFormatterCommentsPage_maxWidthLabel);
	}

	protected URL getPreviewContent()
	{
		return getClass().getResource(COMMENTS_PREVIEW_NAME);
	}

}
