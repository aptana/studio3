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
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.js.formatter.JSFormatterConstants;
import com.aptana.formatter.ui.IFormatterControlManager;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.preferences.FormatterModifyTabPage;
import com.aptana.formatter.ui.util.SWTFactory;

/**
 * White-Spaces configuration tab for the JavaScript code formatter.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class JSFormatterWhiteSpacesPage extends FormatterModifyTabPage
{

	private static final String WHITE_SPACES_PREVIEW_FILE = "white-spaces-preview.js"; //$NON-NLS-1$

	/**
	 * Constructs a new JSFormatterWhiteSpacesPage
	 * 
	 * @param dialog
	 */
	public JSFormatterWhiteSpacesPage(IFormatterModifyDialog dialog)
	{
		super(dialog);
	}

	/*
	 * (non-Javadoc)
	 * @seecom.aptana.formatter.ui.preferences.FormatterModifyTabPage#createOptions(com.aptana.formatter.ui.
	 * IFormatterControlManager, org.eclipse.swt.widgets.Composite)
	 */
	protected void createOptions(IFormatterControlManager manager, Composite parent)
	{
		Group wrappingGroup = SWTFactory.createGroup(parent, Messages.JSFormatterWhiteSpacesPage_spacingSettings, 1, 1,
				GridData.FILL_HORIZONTAL);

		// Punctuation Group
		ExpandableComposite expandibleComposite = SWTFactory.createExpandibleComposite(wrappingGroup,
				Messages.JSFormatterWhiteSpacesPage_puctuationElementsGroupTitle, 3);
		Composite punctuationGroup = SWTFactory
				.createComposite(expandibleComposite, 3, 20, 1, GridData.FILL_HORIZONTAL);
		expandibleComposite.setClient(punctuationGroup);

		SWTFactory.createCenteredLabel(punctuationGroup, StringUtil.EMPTY);
		SWTFactory.createCenteredLabel(punctuationGroup, Messages.JSFormatterWhiteSpacesPage_before);
		SWTFactory.createCenteredLabel(punctuationGroup, Messages.JSFormatterWhiteSpacesPage_after);

		// Commas
		SWTFactory.createLabel(punctuationGroup, Messages.JSFormatterWhiteSpacesPage_commas);
		manager.createSpinner(punctuationGroup, JSFormatterConstants.SPACES_BEFORE_COMMAS);
		manager.createSpinner(punctuationGroup, JSFormatterConstants.SPACES_AFTER_COMMAS);

		// Parentheses
		SWTFactory.createLabel(punctuationGroup, Messages.JSFormatterWhiteSpacesPage_parentheses);
		manager.createSpinner(punctuationGroup, JSFormatterConstants.SPACES_BEFORE_PARENTHESES);
		manager.createSpinner(punctuationGroup, JSFormatterConstants.SPACES_AFTER_PARENTHESES);

		// Semicolon in 'for' statements
		SWTFactory.createLabel(punctuationGroup, Messages.JSFormatterWhiteSpacesPage_semicolonsInFor);
		manager.createSpinner(punctuationGroup, JSFormatterConstants.SPACES_BEFORE_FOR_SEMICOLON);
		manager.createSpinner(punctuationGroup, JSFormatterConstants.SPACES_AFTER_FOR_SEMICOLON);

		// Operators Group
		expandibleComposite = SWTFactory.createExpandibleComposite(wrappingGroup,
				Messages.JSFormatterWhiteSpacesPage_operatorsGroupTitle, 3);
		Composite operatorsGroup = SWTFactory.createComposite(expandibleComposite, 3, 20, 1, GridData.FILL_HORIZONTAL);
		expandibleComposite.setClient(operatorsGroup);

		SWTFactory.createCenteredLabel(operatorsGroup, StringUtil.EMPTY);
		SWTFactory.createCenteredLabel(operatorsGroup, Messages.JSFormatterWhiteSpacesPage_before);
		SWTFactory.createCenteredLabel(operatorsGroup, Messages.JSFormatterWhiteSpacesPage_after);

		// Arithmetic
		SWTFactory.createLabel(operatorsGroup, Messages.JSFormatterWhiteSpacesPage_arithmeticOperators);
		manager.createSpinner(operatorsGroup, JSFormatterConstants.SPACES_BEFORE_ARITHMETIC_OPERATOR);
		manager.createSpinner(operatorsGroup, JSFormatterConstants.SPACES_AFTER_ARITHMETIC_OPERATOR);

		// Relational
		SWTFactory.createLabel(operatorsGroup, Messages.JSFormatterWhiteSpacesPage_relationalOperators);
		manager.createSpinner(operatorsGroup, JSFormatterConstants.SPACES_BEFORE_RELATIONAL_OPERATORS);
		manager.createSpinner(operatorsGroup, JSFormatterConstants.SPACES_AFTER_RELATIONAL_OPERATORS);

		// Unary
		SWTFactory.createLabel(operatorsGroup, Messages.JSFormatterWhiteSpacesPage_unaryOperators);
		manager.createSpinner(operatorsGroup, JSFormatterConstants.SPACES_BEFORE_UNARY_OPERATOR);
		manager.createSpinner(operatorsGroup, JSFormatterConstants.SPACES_AFTER_UNARY_OPERATOR);

		// Assignment
		SWTFactory.createLabel(operatorsGroup, Messages.JSFormatterWhiteSpacesPage_assignments);
		manager.createSpinner(operatorsGroup, JSFormatterConstants.SPACES_BEFORE_ASSIGNMENT_OPERATOR);
		manager.createSpinner(operatorsGroup, JSFormatterConstants.SPACES_AFTER_ASSIGNMENT_OPERATOR);

		// Conditional
		SWTFactory.createLabel(operatorsGroup, Messages.JSFormatterWhiteSpacesPage_conditionalOperators);
		manager.createSpinner(operatorsGroup, JSFormatterConstants.SPACES_BEFORE_CONDITIONAL_OPERATOR);
		manager.createSpinner(operatorsGroup, JSFormatterConstants.SPACES_AFTER_CONDITIONAL_OPERATOR);

		// Key-Value
		SWTFactory.createLabel(operatorsGroup, Messages.JSFormatterWhiteSpacesPage_keyValueOperator);
		manager.createSpinner(operatorsGroup, JSFormatterConstants.SPACES_BEFORE_KEY_VALUE_OPERATOR);
		manager.createSpinner(operatorsGroup, JSFormatterConstants.SPACES_AFTER_KEY_VALUE_OPERATOR);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.preferences.FormatterModifyTabPage#getPreviewContent()
	 */
	protected URL getPreviewContent()
	{
		return getClass().getResource(WHITE_SPACES_PREVIEW_FILE);
	}

}
