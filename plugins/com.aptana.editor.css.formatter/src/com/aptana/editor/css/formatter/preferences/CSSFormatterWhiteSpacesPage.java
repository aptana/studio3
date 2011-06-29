/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.formatter.preferences;

import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.aptana.editor.css.formatter.CSSFormatterConstants;
import com.aptana.formatter.ui.IFormatterControlManager;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.preferences.FormatterModifyTabPage;
import com.aptana.formatter.ui.util.SWTFactory;

public class CSSFormatterWhiteSpacesPage extends FormatterModifyTabPage
{

	private static String SPACES_PREVIEW_NAME = "whitespace-preview.css"; //$NON-NLS-1$

	public CSSFormatterWhiteSpacesPage(IFormatterModifyDialog dialog)
	{
		super(dialog);
	}

	@Override
	protected void createOptions(IFormatterControlManager manager, Composite parent)
	{
		// Punctuation Group
		Group punctuationGroup = SWTFactory.createGroup(parent,
				Messages.CSSFormatterWhiteSpacesPage_punctuationGroupLabel, 5, 1, GridData.FILL_HORIZONTAL);

		// Commas
		Label label = new Label(punctuationGroup, SWT.NONE);
		label.setText(Messages.CSSFormatterWhiteSpacesPage_commasLabel);
		manager.createNumber(punctuationGroup, CSSFormatterConstants.SPACES_BEFORE_COMMAS,
				Messages.CSSFormatterWhiteSpacesPage_before);
		manager.createNumber(punctuationGroup, CSSFormatterConstants.SPACES_AFTER_COMMAS,
				Messages.CSSFormatterWhiteSpacesPage_after);

		// Colons
		label = new Label(punctuationGroup, SWT.NONE);
		label.setText(Messages.CSSFormatterWhiteSpacesPage_colonLabel);
		manager.createNumber(punctuationGroup, CSSFormatterConstants.SPACES_BEFORE_COLON,
				Messages.CSSFormatterWhiteSpacesPage_before);
		manager.createNumber(punctuationGroup, CSSFormatterConstants.SPACES_AFTER_COLON,
				Messages.CSSFormatterWhiteSpacesPage_after);

		// Parenthesis in selectors
		label = new Label(punctuationGroup, SWT.NONE);
		label.setText(Messages.CSSFormatterWhiteSpacesPage_parenthesisLabel);
		manager.createNumber(punctuationGroup, CSSFormatterConstants.SPACES_BEFORE_PARENTHESES,
				Messages.CSSFormatterWhiteSpacesPage_before);
		manager.createNumber(punctuationGroup, CSSFormatterConstants.SPACES_AFTER_PARENTHESES,
				Messages.CSSFormatterWhiteSpacesPage_after);

	}

	protected URL getPreviewContent()
	{
		return getClass().getResource(SPACES_PREVIEW_NAME);
	}

}
