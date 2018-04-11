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
 * Blank-lines configuration tab for the HTML code formatter.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class HTMLFormatterBlankLinesPage extends FormatterModifyTabPage
{

	/**
	 * @param dialog
	 */
	public HTMLFormatterBlankLinesPage(IFormatterModifyDialog dialog)
	{
		super(dialog);
	}

	protected void createOptions(IFormatterControlManager manager, Composite parent)
	{
		Group blankLinesGroup = SWTFactory.createGroup(parent,
				Messages.HTMLFormatterBlankLinesPage_blankLinesGroupLabel, 2, 1, GridData.FILL_HORIZONTAL);
		manager.createNumber(blankLinesGroup, HTMLFormatterConstants.LINES_AFTER_ELEMENTS,
				Messages.HTMLFormatterBlankLinesPage_afterElements);
		manager.createNumber(blankLinesGroup, HTMLFormatterConstants.LINES_AFTER_NON_HTML_ELEMENTS,
				Messages.HTMLFormatterBlankLinesPage_afterSpecialElements);
		manager.createNumber(blankLinesGroup, HTMLFormatterConstants.LINES_BEFORE_NON_HTML_ELEMENTS,
				Messages.HTMLFormatterBlankLinesPage_beforeSpecialElements);

		Group preserveLinesGroup = SWTFactory.createGroup(parent,
				Messages.HTMLFormatterBlankLinesPage_existingBlankLinesGroupLabel, 2, 1, GridData.FILL_HORIZONTAL);
		manager.createNumber(preserveLinesGroup, HTMLFormatterConstants.PRESERVED_LINES,
				Messages.HTMLFormatterBlankLinesPage_existingBlankLinesToPreserve);
	}

	protected URL getPreviewContent()
	{
		return getClass().getResource("blank-lines-preview.html"); //$NON-NLS-1$
	}

}
