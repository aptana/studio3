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
import com.aptana.formatter.ui.preferences.FormatterModifyDialog;
import com.aptana.formatter.ui.preferences.FormatterModifyTabPage;
import com.aptana.formatter.ui.util.SWTFactory;

/**
 * A JS formatter tab for new-lines insertions.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class JSFormatterNewLinesPage extends FormatterModifyTabPage
{

	private static final String NEW_LINES_PREVIEW_FILE = "indentation-preview.js"; //$NON-NLS-1$

	/**
	 * Constructor.
	 * 
	 * @param dialog
	 *            A {@link FormatterModifyDialog}
	 */
	public JSFormatterNewLinesPage(FormatterModifyDialog dialog)
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
		Group group = SWTFactory.createGroup(parent, Messages.JSFormatterTabPage_newLinesGroupLabel, 1, 1,
				GridData.FILL_HORIZONTAL);
		manager.createCheckbox(group, JSFormatterConstants.NEW_LINES_BEFORE_ELSE_STATEMENT,
				Messages.JSFormatterNewLinesPage_newLineBeforeElse);
		manager.createCheckbox(group, JSFormatterConstants.NEW_LINES_BEFORE_IF_IN_ELSEIF_STATEMENT,
				Messages.JSFormatterNewLinesPage_newLineBreakElseIf);
		manager.createCheckbox(group, JSFormatterConstants.NEW_LINES_BEFORE_CATCH_STATEMENT,
				Messages.JSFormatterNewLinesPage_newLineBeforeCatch);
		manager.createCheckbox(group, JSFormatterConstants.NEW_LINES_BEFORE_FINALLY_STATEMENT,
				Messages.JSFormatterNewLinesPage_newLineBeforeFinally);
		manager.createCheckbox(group, JSFormatterConstants.NEW_LINES_BEFORE_DO_WHILE_STATEMENT,
				Messages.JSFormatterNewLinesPage_newLineBeforeWhileInDo);
		manager.createCheckbox(group, JSFormatterConstants.NEW_LINES_BEFORE_NAME_VALUE_PAIRS,
				Messages.JSFormatterNewLinesPage_newLineBeforeNameValuePairs);
		manager.createCheckbox(group, JSFormatterConstants.NEW_LINES_BETWEEN_VAR_DECLARATIONS,
				Messages.JSFormatterNewLinesPage_newLineBetweenVarDeclarations);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.FormatterModifyTabPage#getPreviewContent()
	 */
	protected URL getPreviewContent()
	{
		return getClass().getResource(NEW_LINES_PREVIEW_FILE);
	}
}
