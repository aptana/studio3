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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;


/**
 * 
 *
 */
public class BlankLinesTabPage extends FormatterTabPage {

	private final String PREVIEW=
	createPreviewHeader(FormatterMessages.BlankLinesTabPage_preview_header) + 
	"package foo.bar.baz;" + //$NON-NLS-1$
	"import java.util.List;\n" + //$NON-NLS-1$
	"import java.util.Vector;\n" + //$NON-NLS-1$
	"\n" + //$NON-NLS-1$
	"import java.net.Socket;\n" + //$NON-NLS-1$
	"public class Another {}" + //$NON-NLS-1$
	"public class Example {" + //$NON-NLS-1$
	"public static class Pair {" + //$NON-NLS-1$
	"public String first;" + //$NON-NLS-1$
	"public String second;\n" + //$NON-NLS-1$
	"// Between here...\n" + //$NON-NLS-1$
	"\n\n\n\n\n\n\n\n\n\n" + //$NON-NLS-1$
	"// ...and here are 10 blank lines\n" + //$NON-NLS-1$
	"};" + //$NON-NLS-1$
	"private LinkedList fList;" + //$NON-NLS-1$
	"public int counter;" + //$NON-NLS-1$
	"public Example(LinkedList list) {" + //$NON-NLS-1$
	"  fList= list;" + //$NON-NLS-1$
	"  counter= 0;" + //$NON-NLS-1$
	"}" + //$NON-NLS-1$
	"public void push(Pair p) {" + //$NON-NLS-1$
	"  fList.add(p);" + //$NON-NLS-1$
	"  ++counter;" + //$NON-NLS-1$
	"}" + //$NON-NLS-1$
	"public Object pop() {" + //$NON-NLS-1$
	"  --counter;" + //$NON-NLS-1$
	"  return (Pair)fList.getLast();" + //$NON-NLS-1$
	"}" + //$NON-NLS-1$
	"}"; //$NON-NLS-1$
	
	private final static int MIN_NUMBER_LINES= 0;
	private final static int MAX_NUMBER_LINES= 99;
	

	private CompilationUnitPreview fPreview;

	private String editor;
	
	/**
	 * Create a new BlankLinesTabPage.
	 * @param modifyDialog The main configuration dialog
	 * 
	 * @param workingValues The values wherein the options are stored. 
	 * @param editor 
	 */
	public BlankLinesTabPage(ModifyDialog modifyDialog, Map<String, String> workingValues,String editor) {
		super(modifyDialog, workingValues);
		this.editor=editor;
	}

	
	protected void doCreatePreferences(Composite composite, int numColumns) {
				
	    Group group;
	    
		group= createGroup(numColumns, composite, FormatterMessages.BlankLinesTabPage_compilation_unit_group_title); 
		createBlankLineTextField(group, numColumns, FormatterMessages.BlankLinesTabPage_compilation_unit_option_before_package, DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_PACKAGE); 
		createBlankLineTextField(group, numColumns, FormatterMessages.BlankLinesTabPage_compilation_unit_option_after_package, DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_AFTER_PACKAGE); 
		createBlankLineTextField(group, numColumns, FormatterMessages.BlankLinesTabPage_compilation_unit_option_before_import, DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_IMPORTS); 
		createBlankLineTextField(group, numColumns, FormatterMessages.BlankLinesTabPage_compilation_unit_option_between_import_groups, DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BETWEEN_IMPORT_GROUPS); 
		createBlankLineTextField(group, numColumns, FormatterMessages.BlankLinesTabPage_compilation_unit_option_after_import, DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_AFTER_IMPORTS); 
		createBlankLineTextField(group, numColumns, FormatterMessages.BlankLinesTabPage_compilation_unit_option_between_type_declarations, DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BETWEEN_TYPE_DECLARATIONS); 
		
		group= createGroup(numColumns, composite, FormatterMessages.BlankLinesTabPage_class_group_title); 
		createBlankLineTextField(group, numColumns, FormatterMessages.BlankLinesTabPage_class_option_before_first_decl, DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_FIRST_CLASS_BODY_DECLARATION); 
		createBlankLineTextField(group, numColumns, FormatterMessages.BlankLinesTabPage_class_option_before_decls_of_same_kind, DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_NEW_CHUNK); 
		createBlankLineTextField(group, numColumns, FormatterMessages.BlankLinesTabPage_class_option_before_member_class_decls, DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_MEMBER_TYPE); 
		createBlankLineTextField(group, numColumns, FormatterMessages.BlankLinesTabPage_class_option_before_field_decls, DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_FIELD); 
		createBlankLineTextField(group, numColumns, FormatterMessages.BlankLinesTabPage_class_option_before_method_decls, DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_METHOD); 
		createBlankLineTextField(group, numColumns, FormatterMessages.BlankLinesTabPage_class_option_at_beginning_of_method_body, DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_AT_BEGINNING_OF_METHOD_BODY); 

		group= createGroup(numColumns, composite, FormatterMessages.BlankLinesTabPage_blank_lines_group_title); 
		createBlankLineTextField(group, numColumns, FormatterMessages.BlankLinesTabPage_blank_lines_option_empty_lines_to_preserve, DefaultCodeFormatterConstants.FORMATTER_NUMBER_OF_EMPTY_LINES_TO_PRESERVE); 
	}
	
	
	protected void initializePage() {
	    fPreview.setPreviewText(PREVIEW);
	}
	
	/*
	 * A helper method to create a number preference for blank lines.
	 */
	private void createBlankLineTextField(Composite composite, int numColumns, String message, String key) {
		createNumberPref(composite, numColumns, message, key, MIN_NUMBER_LINES, MAX_NUMBER_LINES);
	}

    
    protected Preview doCreateJavaPreview(Composite parent) {
        fPreview= new CompilationUnitPreview(fWorkingValues, parent,editor, null);
        return fPreview;
    }

    
    protected void doUpdatePreview() {
    	super.doUpdatePreview();
        fPreview.update();
    }
}