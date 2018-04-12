/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.formatter.preferences;

import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.aptana.editor.html.formatter.HTMLFormatterConstants;
import com.aptana.formatter.ui.IFormatterControlManager;
import com.aptana.formatter.ui.preferences.FormatterModifyDialog;
import com.aptana.formatter.ui.preferences.FormatterModifyTabPage;
import com.aptana.formatter.ui.util.SWTFactory;

/**
 * A HTML formatter tab for new-lines insertions.
 * 
 * @author Shalom
 */
public class HTMLFormatterNewLinesPage extends FormatterModifyTabPage
{

	private static final String NEW_LINES_PREVIEW_FILE = "indentation-preview.html"; //$NON-NLS-1$

	/**
	 * Constructor.
	 * 
	 * @param dialog
	 *            A {@link FormatterModifyDialog}
	 */
	public HTMLFormatterNewLinesPage(FormatterModifyDialog dialog)
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
		Group group = SWTFactory.createGroup(parent, Messages.HTMLFormatterTabPage_newLinesGroupLabel, 1, 1,
				GridData.FILL_HORIZONTAL);
		manager.createCheckbox(group, HTMLFormatterConstants.NEW_LINES_EXCLUSION_IN_EMPTY_TAGS,
				Messages.HTMLFormatterTabPage_newLinesInEmptyTags);

		group = SWTFactory.createGroup(parent, Messages.HTMLFormatterTabPage_exclusionsGroupLabel, 1, 1,
				GridData.FILL_BOTH);
		Label exclutionLabel = new Label(group, SWT.WRAP);
		exclutionLabel.setText(Messages.HTMLFormatterNewLinesPage_exclusionsMessage);
		manager.createManagedList(group, HTMLFormatterConstants.NEW_LINES_EXCLUDED_TAGS);
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
