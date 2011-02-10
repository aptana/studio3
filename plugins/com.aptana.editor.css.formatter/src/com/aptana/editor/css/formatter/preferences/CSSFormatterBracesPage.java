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
import com.aptana.formatter.ui.CodeFormatterConstants;
import com.aptana.formatter.ui.FormatterMessages;
import com.aptana.formatter.ui.IFormatterControlManager;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.preferences.FormatterModifyTabPage;
import com.aptana.formatter.ui.util.SWTFactory;

public class CSSFormatterBracesPage extends FormatterModifyTabPage
{
	private static final String BRACES_PREVIEW_NAME = "preview.css"; //$NON-NLS-1$
	private static final String[] TAB_OPTION_ITEMS = new String[] { CodeFormatterConstants.SAME_LINE,
			CodeFormatterConstants.NEW_LINE };
	private static final String[] TAB_OPTION_NAMES = new String[] {
			FormatterMessages.BracesTabPage_position_option_SAME_LINE,
			FormatterMessages.BracesTabPage_position_option_NEW_LINE };

	public CSSFormatterBracesPage(IFormatterModifyDialog dialog)
	{
		super(dialog);
	}

	protected void createOptions(IFormatterControlManager manager, Composite parent)
	{
		Group bracesGroup = SWTFactory.createGroup(parent, Messages.CSSFormatterBracesPage_braces_group_label, 1, 2,
				GridData.FILL_HORIZONTAL);
		manager.createCombo(bracesGroup, CSSFormatterConstants.NEW_LINES_BEFORE_BLOCKS,
				Messages.CSSFormatterBracesPage_blocks, TAB_OPTION_ITEMS, TAB_OPTION_NAMES);
	}

	protected URL getPreviewContent()
	{
		return getClass().getResource(BRACES_PREVIEW_NAME);
	}

}
