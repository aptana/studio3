/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.formatter.preferences;

import java.net.URL;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.aptana.editor.css.formatter.CSSFormatterConstants;
import com.aptana.formatter.ui.IFormatterControlManager;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.preferences.FormatterModifyTabPage;
import com.aptana.formatter.ui.util.SWTFactory;

public class CSSFormatterCommentsPage extends FormatterModifyTabPage
{
	private static final String COMMENTS_PREVIEW_NAME = "comments-preview.css"; //$NON-NLS-1$
	
	public CSSFormatterCommentsPage(IFormatterModifyDialog dialog)
	{
		super(dialog);
	}

	protected void createOptions(IFormatterControlManager manager, Composite parent)
	{
		Group commentGroup = SWTFactory.createGroup(parent, Messages.CSSFormatterCommentsPage_comments_group_label, 2,
				1, GridData.FILL_HORIZONTAL);
		manager.createCheckbox(commentGroup, CSSFormatterConstants.WRAP_COMMENTS,
				Messages.CSSFormatterCommentsPage_enable_warpping, 2);
		manager.createNumber(commentGroup, CSSFormatterConstants.WRAP_COMMENTS_LENGTH,
				Messages.CSSFormatterCommentsPage_max_line_width);

	}

	protected URL getPreviewContent()
	{
		return getClass().getResource(COMMENTS_PREVIEW_NAME);
	}

}
