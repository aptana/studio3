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
import com.aptana.formatter.ui.CodeFormatterConstants;
import com.aptana.formatter.ui.FormatterMessages;
import com.aptana.formatter.ui.IFormatterControlManager;
import com.aptana.formatter.ui.preferences.FormatterModifyDialog;
import com.aptana.formatter.ui.preferences.FormatterModifyTabPage;
import com.aptana.formatter.ui.util.SWTFactory;

/**
 * JavaScript formatter braces tab
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class JSFormatterBracesPage extends FormatterModifyTabPage
{

	private static final String BRACES_PREVIEW_FILE = "braces-preview.js"; //$NON-NLS-1$

	private final String[] bracesOptionNames = new String[] {
			FormatterMessages.BracesTabPage_position_option_SAME_LINE,
			FormatterMessages.BracesTabPage_position_option_NEW_LINE };
	private final String[] bracesOptionKeys = new String[] { CodeFormatterConstants.SAME_LINE,
			CodeFormatterConstants.NEW_LINE };

	/**
	 * Constructor.
	 * 
	 * @param dialog
	 *            A {@link FormatterModifyDialog}
	 */
	public JSFormatterBracesPage(FormatterModifyDialog dialog)
	{
		super(dialog);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.formatter.ui.FormatterModifyTabPage#createOptions(com.aptana.formatter.ui.IFormatterControlManager,
	 * org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createOptions(IFormatterControlManager manager, Composite parent)
	{
		Group group = SWTFactory.createGroup(parent, "Braces positions", 2, 1, //$NON-NLS-1$
				GridData.FILL_HORIZONTAL);
		manager.createCombo(group, JSFormatterConstants.BRACE_POSITION_BLOCK, Messages.JSFormatterBracesPage_blocks,
				bracesOptionKeys, bracesOptionNames);
		manager.createCombo(group, JSFormatterConstants.BRACE_POSITION_FUNCTION_DECLARATION,
				Messages.JSFormatterBracesPage_functionDeclaraion, bracesOptionKeys, bracesOptionNames);
		manager.createCombo(group, JSFormatterConstants.BRACE_POSITION_BLOCK_IN_SWITCH,
				Messages.JSFormatterBracesPage_switchStatement, bracesOptionKeys, bracesOptionNames);
		manager.createCombo(group, JSFormatterConstants.BRACE_POSITION_BLOCK_IN_CASE,
				Messages.JSFormatterBracesPage_caseStateMent, bracesOptionKeys, bracesOptionNames);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.FormatterModifyTabPage#getPreviewContent()
	 */
	protected URL getPreviewContent()
	{
		return getClass().getResource(BRACES_PREVIEW_FILE);
	}
}
