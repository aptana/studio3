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
 * A formatter comments page for HTML.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class HTMLFormatterCommentsPage extends FormatterModifyTabPage
{

	/**
	 * @param dialog
	 */
	public HTMLFormatterCommentsPage(IFormatterModifyDialog dialog)
	{
		super(dialog);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.formatter.ui.FormatterModifyTabPage#createOptions(com.aptana.formatter.ui.IFormatterControlManager,
	 * org.eclipse.swt.widgets.Composite)
	 */
	protected void createOptions(IFormatterControlManager manager, Composite parent)
	{
		Group commentWrappingGroup = SWTFactory.createGroup(parent,
				Messages.HTMLFormatterCommentsPage_formattingGroupLabel, 2, 1, GridData.FILL_HORIZONTAL);
		manager.createCheckbox(commentWrappingGroup, HTMLFormatterConstants.PLACE_COMMENTS_IN_SEPARATE_LINES,
				Messages.HTMLFormatterCommentsPage_commentsInSeparateLines, 2);
		manager.createCheckbox(commentWrappingGroup, HTMLFormatterConstants.WRAP_COMMENTS,
				Messages.HTMLFormatterCommentsPage_enableWrapping, 2);
		manager.createNumber(commentWrappingGroup, HTMLFormatterConstants.WRAP_COMMENTS_LENGTH,
				Messages.HTMLFormatterCommentsPage_maxLineWidth);

	}

	protected URL getPreviewContent()
	{
		return getClass().getResource("wrapping-preview.html"); //$NON-NLS-1$
	}
}
