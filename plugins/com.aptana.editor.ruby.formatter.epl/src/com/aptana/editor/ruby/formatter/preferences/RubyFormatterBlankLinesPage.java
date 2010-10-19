/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.editor.ruby.formatter.preferences;

import java.net.URL;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.aptana.editor.ruby.formatter.RubyFormatterConstants;
import com.aptana.formatter.ui.IFormatterControlManager;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.preferences.FormatterModifyTabPage;
import com.aptana.formatter.ui.util.SWTFactory;

public class RubyFormatterBlankLinesPage extends FormatterModifyTabPage
{

	/**
	 * @param dialog
	 */
	public RubyFormatterBlankLinesPage(IFormatterModifyDialog dialog)
	{
		super(dialog);
	}

	protected void createOptions(IFormatterControlManager manager, Composite parent)
	{
		Group emptyLinesGroup = SWTFactory.createGroup(parent, Messages.RubyFormatterBlankLinesPage_blankLines, 2, 1,
				GridData.FILL_HORIZONTAL);
		manager.createNumber(emptyLinesGroup, RubyFormatterConstants.LINES_FILE_AFTER_REQUIRE,
				Messages.RubyFormatterBlankLinesPage_afterRequire);
		manager.createNumber(emptyLinesGroup, RubyFormatterConstants.LINES_FILE_BETWEEN_MODULE,
				Messages.RubyFormatterBlankLinesPage_betweenModules);
		manager.createNumber(emptyLinesGroup, RubyFormatterConstants.LINES_FILE_BETWEEN_CLASS,
				Messages.RubyFormatterBlankLinesPage_betweenClasses);
		manager.createNumber(emptyLinesGroup, RubyFormatterConstants.LINES_FILE_BETWEEN_METHOD,
				Messages.RubyFormatterBlankLinesPage_betweenMethods);
		//
		Group emptyLinesInternalGroup = SWTFactory.createGroup(parent,
				Messages.RubyFormatterBlankLinesPage_blanksWithinClassesAndModules, 2, 1, GridData.FILL_HORIZONTAL);
		manager.createNumber(emptyLinesInternalGroup, RubyFormatterConstants.LINES_BEFORE_FIRST,
				Messages.RubyFormatterBlankLinesPage_beforeFirstDeclare);
		manager.createNumber(emptyLinesInternalGroup, RubyFormatterConstants.LINES_BEFORE_MODULE,
				Messages.RubyFormatterBlankLinesPage_beforeNestedModule);
		manager.createNumber(emptyLinesInternalGroup, RubyFormatterConstants.LINES_BEFORE_CLASS,
				Messages.RubyFormatterBlankLinesPage_beforeNestedClass);
		manager.createNumber(emptyLinesInternalGroup, RubyFormatterConstants.LINES_BEFORE_METHOD,
				Messages.RubyFormatterBlankLinesPage_beforeMethodsDeclare);
		//
		Group preserveGroup = SWTFactory.createGroup(parent, Messages.RubyFormatterBlankLinesPage_existingBlankLines,
				2, 1, GridData.FILL_HORIZONTAL);
		manager.createNumber(preserveGroup, RubyFormatterConstants.LINES_PRESERVE,
				Messages.RubyFormatterBlankLinesPage_emptyLinesToPreserve);
	}

	protected URL getPreviewContent()
	{
		return getClass().getResource("blank-lines-preview.rb"); //$NON-NLS-1$
	}

}
