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

public class CSSFormatterBlankLinesPage extends FormatterModifyTabPage
{
	private static final String BLANK_LINES_PREVIEW_NAME = "preview.css"; //$NON-NLS-1$

	public CSSFormatterBlankLinesPage(IFormatterModifyDialog dialog)
	{
		super(dialog);
	}

	protected void createOptions(IFormatterControlManager manager, Composite parent)
	{
		Group blankLinesGroup = SWTFactory.createGroup(parent,
				Messages.CSSFormatterBlankLinesPage_blankLinesGroupLabel, 2, 1, GridData.FILL_HORIZONTAL);
		manager.createNumber(blankLinesGroup, CSSFormatterConstants.LINES_AFTER_ELEMENTS,
				Messages.CSSFormatterBlankLinesPage_afterCSSRule);
		manager.createNumber(blankLinesGroup, CSSFormatterConstants.LINES_AFTER_DECLARATION,
				Messages.CSSFormatterBlankLinesPage_afterCSSDeclaration);

		Group preserveLinesGroup = SWTFactory.createGroup(parent,
				Messages.CSSFormatterBlankLinesPage_existingBlankLinesLabel, 2, 1, GridData.FILL_HORIZONTAL);
		manager.createNumber(preserveLinesGroup, CSSFormatterConstants.PRESERVED_LINES,
				Messages.CSSFormatterBlankLinesPage_existingBlankLinesToPreserve);
	}

	protected URL getPreviewContent()
	{
		return getClass().getResource(BLANK_LINES_PREVIEW_NAME);
	}

}
