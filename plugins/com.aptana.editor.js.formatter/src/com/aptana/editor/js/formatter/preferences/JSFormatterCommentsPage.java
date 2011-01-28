/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.formatter.preferences;

import java.net.URL;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.aptana.editor.js.formatter.JSFormatterConstants;
import com.aptana.formatter.ui.IFormatterControlManager;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.preferences.FormatterModifyTabPage;
import com.aptana.formatter.ui.util.SWTFactory;

/**
 * A formatter comments page for JS.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class JSFormatterCommentsPage extends FormatterModifyTabPage
{
	private static final String WRAPPING_PREVIEW_FILE = "wrapping-preview.js"; //$NON-NLS-1$

	/**
	 * @param dialog
	 */
	public JSFormatterCommentsPage(IFormatterModifyDialog dialog)
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
				Messages.JSFormatterCommentsPage_formattingGroupLabel, 2, 1, GridData.FILL_HORIZONTAL);
		manager.createCheckbox(commentWrappingGroup, JSFormatterConstants.WRAP_COMMENTS,
				Messages.JSFormatterCommentsPage_enableWrapping, 2);
		manager.createNumber(commentWrappingGroup, JSFormatterConstants.WRAP_COMMENTS_LENGTH,
				Messages.JSFormatterCommentsPage_maxLineWidth);

	}

	protected URL getPreviewContent()
	{
		return getClass().getResource(WRAPPING_PREVIEW_FILE);
	}
}
