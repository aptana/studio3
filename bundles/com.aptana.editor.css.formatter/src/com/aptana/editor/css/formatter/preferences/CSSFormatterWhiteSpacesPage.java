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
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import com.aptana.core.util.StringUtil;
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
		Group wrappingGroup = SWTFactory.createGroup(parent, Messages.CSSFormatterWhiteSpacesPage_spacingSettings, 1,
				1, GridData.FILL_HORIZONTAL);

		// Punctuation Group (we only have one group, so we expand it by default)
		ExpandableComposite expandibleComposite = SWTFactory.createExpandibleComposite(wrappingGroup,
				Messages.CSSFormatterWhiteSpacesPage_punctuationGroupLabel, 3, true);
		Composite punctuationGroup = SWTFactory
				.createComposite(expandibleComposite, 3, 20, 1, GridData.FILL_HORIZONTAL);
		expandibleComposite.setClient(punctuationGroup);

		SWTFactory.createCenteredLabel(punctuationGroup, StringUtil.EMPTY);
		SWTFactory.createCenteredLabel(punctuationGroup, Messages.CSSFormatterWhiteSpacesPage_before);
		SWTFactory.createCenteredLabel(punctuationGroup, Messages.CSSFormatterWhiteSpacesPage_after);

		// Commas
		SWTFactory.createLabel(punctuationGroup, Messages.CSSFormatterWhiteSpacesPage_commasLabel);
		manager.createSpinner(punctuationGroup, CSSFormatterConstants.SPACES_BEFORE_COMMAS);
		manager.createSpinner(punctuationGroup, CSSFormatterConstants.SPACES_AFTER_COMMAS);

		// Colons
		SWTFactory.createLabel(punctuationGroup, Messages.CSSFormatterWhiteSpacesPage_colonLabel);
		manager.createSpinner(punctuationGroup, CSSFormatterConstants.SPACES_BEFORE_COLON);
		manager.createSpinner(punctuationGroup, CSSFormatterConstants.SPACES_AFTER_COLON);

		// Parenthesis in selectors
		SWTFactory.createLabel(punctuationGroup, Messages.CSSFormatterWhiteSpacesPage_parenthesisLabel);
		manager.createSpinner(punctuationGroup, CSSFormatterConstants.SPACES_BEFORE_PARENTHESES);
		manager.createSpinner(punctuationGroup, CSSFormatterConstants.SPACES_AFTER_PARENTHESES);
	}

	protected URL getPreviewContent()
	{
		return getClass().getResource(SPACES_PREVIEW_NAME);
	}

}
