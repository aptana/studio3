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

		// Parentheses Group
		ExpandableComposite expandibleComposite = SWTFactory.createExpandibleComposite(wrappingGroup,
				Messages.JSFormatterWhiteSpacesPage_parenthesesGroupTitle, 4);
		Composite parenthesesGroup = SWTFactory
				.createComposite(expandibleComposite, 4, 20, 1, GridData.FILL_HORIZONTAL);
		expandibleComposite.setClient(parenthesesGroup);

		// @formatter:off
		SWTFactory.createCenteredLabel(parenthesesGroup, StringUtil.EMPTY);
		SWTFactory.createCenteredLabel(parenthesesGroup, Messages.JSFormatterWhiteSpacesPage_parentheses_beforeOpening);
		SWTFactory.createCenteredLabel(parenthesesGroup, Messages.JSFormatterWhiteSpacesPage_parentheses_afterOpening);
		SWTFactory.createCenteredLabel(parenthesesGroup, Messages.JSFormatterWhiteSpacesPage_parentheses_beforeClosing);
		// @formatter:on

		// Declarations parentheses
		SWTFactory.createLabel(parenthesesGroup, Messages.JSFormatterWhiteSpacesPage_declarationExpressions);
		manager.createSpinner(parenthesesGroup, JSFormatterConstants.SPACES_BEFORE_OPENING_DECLARATION_PARENTHESES);
		manager.createSpinner(parenthesesGroup, JSFormatterConstants.SPACES_AFTER_OPENING_DECLARATION_PARENTHESES);
		manager.createSpinner(parenthesesGroup, JSFormatterConstants.SPACES_BEFORE_CLOSING_DECLARATION_PARENTHESES);

		// Invocations parentheses
		SWTFactory.createLabel(parenthesesGroup, Messages.JSFormatterWhiteSpacesPage_invocationExpressions);
		manager.createSpinner(parenthesesGroup, JSFormatterConstants.SPACES_BEFORE_OPENING_INVOCATION_PARENTHESES);
		manager.createSpinner(parenthesesGroup, JSFormatterConstants.SPACES_AFTER_OPENING_INVOCATION_PARENTHESES);
		manager.createSpinner(parenthesesGroup, JSFormatterConstants.SPACES_BEFORE_CLOSING_INVOCATION_PARENTHESES);

		// Conditionals parentheses
		SWTFactory.createLabel(parenthesesGroup, Messages.JSFormatterWhiteSpacesPage_conditionalExpressions);
		manager.createSpinner(parenthesesGroup, JSFormatterConstants.SPACES_BEFORE_OPENING_CONDITIONAL_PARENTHESES);
		manager.createSpinner(parenthesesGroup, JSFormatterConstants.SPACES_AFTER_OPENING_CONDITIONAL_PARENTHESES);
		manager.createSpinner(parenthesesGroup, JSFormatterConstants.SPACES_BEFORE_CLOSING_CONDITIONAL_PARENTHESES);

		// Loops parentheses
		SWTFactory.createLabel(parenthesesGroup, Messages.JSFormatterWhiteSpacesPage_loopExpressions);
		manager.createSpinner(parenthesesGroup, JSFormatterConstants.SPACES_BEFORE_OPENING_LOOP_PARENTHESES);
		manager.createSpinner(parenthesesGroup, JSFormatterConstants.SPACES_AFTER_OPENING_LOOP_PARENTHESES);
		manager.createSpinner(parenthesesGroup, JSFormatterConstants.SPACES_BEFORE_CLOSING_LOOP_PARENTHESES);

		// Array-access parentheses
		SWTFactory.createLabel(parenthesesGroup, Messages.JSFormatterWhiteSpacesPage_arrayAccessExpressions);
		manager.createSpinner(parenthesesGroup, JSFormatterConstants.SPACES_BEFORE_OPENING_ARRAY_ACCESS_PARENTHESES);
		manager.createSpinner(parenthesesGroup, JSFormatterConstants.SPACES_AFTER_OPENING_ARRAY_ACCESS_PARENTHESES);
		manager.createSpinner(parenthesesGroup, JSFormatterConstants.SPACES_BEFORE_CLOSING_ARRAY_ACCESS_PARENTHESES);

		// All the rest of the parenthesis types
		SWTFactory.createLabel(parenthesesGroup, Messages.JSFormatterWhiteSpacesPage_otherParenthesesExpressions);
		manager.createSpinner(parenthesesGroup, JSFormatterConstants.SPACES_BEFORE_OPENING_PARENTHESES);
		manager.createSpinner(parenthesesGroup, JSFormatterConstants.SPACES_AFTER_OPENING_PARENTHESES);
		manager.createSpinner(parenthesesGroup, JSFormatterConstants.SPACES_BEFORE_CLOSING_PARENTHESES);

		// Punctuation Group
		expandibleComposite = SWTFactory.createExpandibleComposite(wrappingGroup,
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
