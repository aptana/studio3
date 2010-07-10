/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ui.preferences.formatter;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;


/**
 * @author Pavel Petrochenko
 *
 */
public class ControlStatementsTabPage extends FormatterTabPage {

	
	String editor;
	
	/**
	 * Constant array for boolean selection 
	 */
	private static String[] FALSE_TRUE = {
		DefaultCodeFormatterConstants.FALSE,
		DefaultCodeFormatterConstants.TRUE
	};	
	
    /**
     * Constant array for insert / not_insert. 
     */
    private static String[] DO_NOT_INSERT_INSERT = {
        CommentsTabPage.DO_NOT_INSERT,
        CommentsTabPage.INSERT
    };
	
	
	private final String PREVIEW=
	createPreviewHeader(FormatterMessages.ControlStatementsTabPage_preview_header) + 
	"class Example {" +	//$NON-NLS-1$	
	"  void bar() {" +	//$NON-NLS-1$
	"    do {} while (true);" +	//$NON-NLS-1$
	"    try {} catch (Exception e) { } " +	//$NON-NLS-1$
	"  }" +	//$NON-NLS-1$
	"  void foo2() {" +	//$NON-NLS-1$
	"    if (true) { " + //$NON-NLS-1$
	"      return;" + //$NON-NLS-1$
	"    }" + //$NON-NLS-1$
	"    if (true) {" + //$NON-NLS-1$
	"      return;" + //$NON-NLS-1$
	"    } else if (false) {" +	//$NON-NLS-1$
	"      return; " + //$NON-NLS-1$
	"    } else {" + //$NON-NLS-1$
	"      return;" + //$NON-NLS-1$
	"    }" + //$NON-NLS-1$
	"  }" + //$NON-NLS-1$
	"  void foo(int state) {" + //$NON-NLS-1$
	"    if (true) return;" + //$NON-NLS-1$
	"    if (true) " + //$NON-NLS-1$
	"      return;" + //$NON-NLS-1$
	"    else if (false)" + //$NON-NLS-1$
	"      return;" + //$NON-NLS-1$
	"    else return;" + //$NON-NLS-1$
	"  }" + //$NON-NLS-1$
	"}"; //$NON-NLS-1$
	
	
	
	private CompilationUnitPreview fPreview;
	
	/**
	 * 
	 */
	protected CheckboxPreference fThenStatementPref;
	/**
	 * 
	 */
	protected CheckboxPreference  fSimpleIfPref;

	
	/**
	 * @param modifyDialog
	 * @param workingValues
	 * @param editor
	 */
	public ControlStatementsTabPage(ModifyDialog modifyDialog, Map<String, String> workingValues,String editor) {
		super(modifyDialog, workingValues);
		this.editor=editor;
	}

	
	protected void doCreatePreferences(Composite composite, int numColumns) {
		
		final Group generalGroup= createGroup(numColumns, composite, FormatterMessages.ControlStatementsTabPage_general_group_title); 
		createOption(generalGroup, numColumns, FormatterMessages.ControlStatementsTabPage_general_group_insert_new_line_before_else_statements, DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_ELSE_IN_IF_STATEMENT, DO_NOT_INSERT_INSERT); 
		createOption(generalGroup, numColumns, FormatterMessages.ControlStatementsTabPage_general_group_insert_new_line_before_catch_statements, DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_CATCH_IN_TRY_STATEMENT, DO_NOT_INSERT_INSERT); 
		createOption(generalGroup, numColumns, FormatterMessages.ControlStatementsTabPage_general_group_insert_new_line_before_finally_statements, DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_FINALLY_IN_TRY_STATEMENT, DO_NOT_INSERT_INSERT); 
		createOption(generalGroup, numColumns, FormatterMessages.ControlStatementsTabPage_general_group_insert_new_line_before_while_in_do_statements, DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_WHILE_IN_DO_STATEMENT, DO_NOT_INSERT_INSERT); 
				
		final Group ifElseGroup= createGroup(numColumns, composite, FormatterMessages.ControlStatementsTabPage_if_else_group_title); 
		fThenStatementPref= createOption(ifElseGroup, numColumns, FormatterMessages.ControlStatementsTabPage_if_else_group_keep_then_on_same_line, DefaultCodeFormatterConstants.FORMATTER_KEEP_THEN_STATEMENT_ON_SAME_LINE, FALSE_TRUE); 
		
		Label l= new Label(ifElseGroup, SWT.NONE);
		GridData gd= new GridData();
		gd.widthHint= fPixelConverter.convertWidthInCharsToPixels(4);
		l.setLayoutData(gd);
		
		fSimpleIfPref= createOption(ifElseGroup, numColumns - 1, FormatterMessages.ControlStatementsTabPage_if_else_group_keep_simple_if_on_one_line, DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_IF_ON_ONE_LINE, FALSE_TRUE); 
		
		fThenStatementPref.addObserver( new Observer() {
			public void update(Observable o, Object arg) {
				fSimpleIfPref.setEnabled(!fThenStatementPref.getChecked());
			}
			
		});
		
		fSimpleIfPref.setEnabled(!fThenStatementPref.getChecked());
		
		createOption(ifElseGroup, numColumns, FormatterMessages.ControlStatementsTabPage_if_else_group_keep_else_on_same_line, DefaultCodeFormatterConstants.FORMATTER_KEEP_ELSE_STATEMENT_ON_SAME_LINE, FALSE_TRUE); 
		createCheckboxPref(ifElseGroup, numColumns, FormatterMessages.ControlStatementsTabPage_if_else_group_keep_else_if_on_one_line, DefaultCodeFormatterConstants.FORMATTER_COMPACT_ELSE_IF, FALSE_TRUE); 
		createCheckboxPref(ifElseGroup, numColumns, FormatterMessages.ControlStatementsTabPage_if_else_group_keep_guardian_clause_on_one_line, DefaultCodeFormatterConstants.FORMATTER_KEEP_GUARDIAN_CLAUSE_ON_ONE_LINE, FALSE_TRUE); 
	}
	
	
	protected void initializePage() {
	    fPreview.setPreviewText(PREVIEW);
	}

	
    
    protected Preview doCreateJavaPreview(Composite parent) {
        fPreview= new CompilationUnitPreview(fWorkingValues, parent,editor,null);
        return fPreview;
    }

    
    protected void doUpdatePreview() {
    	super.doUpdatePreview();
        fPreview.update();
    }

    private CheckboxPreference createOption(Composite composite, int span, String name, String key, String [] values) {
		return createCheckboxPref(composite, span, name, key, values);
	}
}
