/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.formatter.preferences;

import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

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
		// Punctuation Group
		Group punctuationGroup = SWTFactory.createGroup(parent,
				Messages.JSFormatterWhiteSpacesPage_puctuationElementsGroupTitle, 5, 1, GridData.FILL_HORIZONTAL);
		// Comma
		Label label = new Label(punctuationGroup, SWT.NONE);
		label.setText(Messages.JSFormatterWhiteSpacesPage_commas);
		manager.createNumber(punctuationGroup, JSFormatterConstants.SPACES_BEFORE_COMMAS,
				Messages.JSFormatterWhiteSpacesPage_before);
		manager.createNumber(punctuationGroup, JSFormatterConstants.SPACES_AFTER_COMMAS,
				Messages.JSFormatterWhiteSpacesPage_after);

		// Parentheses
		label = new Label(punctuationGroup, SWT.NONE);
		label.setText(Messages.JSFormatterWhiteSpacesPage_parentheses);
		manager.createNumber(punctuationGroup, JSFormatterConstants.SPACES_BEFORE_PARENTHESES,
				Messages.JSFormatterWhiteSpacesPage_before);
		manager.createNumber(punctuationGroup, JSFormatterConstants.SPACES_AFTER_PARENTHESES,
				Messages.JSFormatterWhiteSpacesPage_after);

		// Semicolon in 'for' statements
		label = new Label(punctuationGroup, SWT.NONE);
		label.setText(Messages.JSFormatterWhiteSpacesPage_semicolonsInFor);
		manager.createNumber(punctuationGroup, JSFormatterConstants.SPACES_BEFORE_FOR_SEMICOLON,
				Messages.JSFormatterWhiteSpacesPage_before);
		manager.createNumber(punctuationGroup, JSFormatterConstants.SPACES_AFTER_FOR_SEMICOLON,
				Messages.JSFormatterWhiteSpacesPage_after);

		// Operators Group
		Group operatorsGroup = SWTFactory.createGroup(parent, Messages.JSFormatterWhiteSpacesPage_operatorsGroupTitle,
				5, 1, GridData.FILL_HORIZONTAL);
		// Arithmetic
		label = new Label(operatorsGroup, SWT.NONE);
		label.setText(Messages.JSFormatterWhiteSpacesPage_arithmeticOperators);
		manager.createNumber(operatorsGroup, JSFormatterConstants.SPACES_BEFORE_ARITHMETIC_OPERATOR,
				Messages.JSFormatterWhiteSpacesPage_before);
		manager.createNumber(operatorsGroup, JSFormatterConstants.SPACES_AFTER_ARITHMETIC_OPERATOR,
				Messages.JSFormatterWhiteSpacesPage_after);

		// Relational
		label = new Label(operatorsGroup, SWT.NONE);
		label.setText(Messages.JSFormatterWhiteSpacesPage_relationalOperators);
		manager.createNumber(operatorsGroup, JSFormatterConstants.SPACES_BEFORE_RELATIONAL_OPERATORS,
				Messages.JSFormatterWhiteSpacesPage_before);
		manager.createNumber(operatorsGroup, JSFormatterConstants.SPACES_AFTER_RELATIONAL_OPERATORS,
				Messages.JSFormatterWhiteSpacesPage_after);

		// Unary
		label = new Label(operatorsGroup, SWT.NONE);
		label.setText(Messages.JSFormatterWhiteSpacesPage_unaryOperators);
		manager.createNumber(operatorsGroup, JSFormatterConstants.SPACES_BEFORE_UNARY_OPERATOR,
				Messages.JSFormatterWhiteSpacesPage_before);
		manager.createNumber(operatorsGroup, JSFormatterConstants.SPACES_AFTER_UNARY_OPERATOR,
				Messages.JSFormatterWhiteSpacesPage_after);

		// Assignment
		label = new Label(operatorsGroup, SWT.NONE);
		label.setText(Messages.JSFormatterWhiteSpacesPage_assignments);
		manager.createNumber(operatorsGroup, JSFormatterConstants.SPACES_BEFORE_ASSIGNMENT_OPERATOR,
				Messages.JSFormatterWhiteSpacesPage_before);
		manager.createNumber(operatorsGroup, JSFormatterConstants.SPACES_AFTER_ASSIGNMENT_OPERATOR,
				Messages.JSFormatterWhiteSpacesPage_after);

		// Conditional
		label = new Label(operatorsGroup, SWT.NONE);
		label.setText(Messages.JSFormatterWhiteSpacesPage_conditionalOperators);
		manager.createNumber(operatorsGroup, JSFormatterConstants.SPACES_BEFORE_CONDITIONAL_OPERATOR,
				Messages.JSFormatterWhiteSpacesPage_before);
		manager.createNumber(operatorsGroup, JSFormatterConstants.SPACES_AFTER_CONDITIONAL_OPERATOR,
				Messages.JSFormatterWhiteSpacesPage_after);

		// Key-Value
		label = new Label(operatorsGroup, SWT.NONE);
		label.setText(Messages.JSFormatterWhiteSpacesPage_keyValueOperator);
		manager.createNumber(operatorsGroup, JSFormatterConstants.SPACES_BEFORE_KEY_VALUE_OPERATOR,
				Messages.JSFormatterWhiteSpacesPage_before);
		manager.createNumber(operatorsGroup, JSFormatterConstants.SPACES_AFTER_KEY_VALUE_OPERATOR,
				Messages.JSFormatterWhiteSpacesPage_after);
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
