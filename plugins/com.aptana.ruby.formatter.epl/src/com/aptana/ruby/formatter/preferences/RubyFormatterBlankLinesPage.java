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
package com.aptana.ruby.formatter.preferences;

import java.net.URL;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.aptana.formatter.ui.FormatterModifyTabPage;
import com.aptana.formatter.ui.IFormatterControlManager;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.ruby.formatter.RubyFormatterConstants;
import com.aptana.ui.util.SWTFactory;

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
		Group emptyLinesGroup = SWTFactory.createGroup(parent, "Blank lines in source file", 2, 1,
				GridData.FILL_HORIZONTAL);
		manager.createNumber(emptyLinesGroup, RubyFormatterConstants.LINES_FILE_AFTER_REQUIRE,
				"After require directives");
		manager.createNumber(emptyLinesGroup, RubyFormatterConstants.LINES_FILE_BETWEEN_MODULE, "Between modules");
		manager.createNumber(emptyLinesGroup, RubyFormatterConstants.LINES_FILE_BETWEEN_CLASS, "Between classes");
		manager.createNumber(emptyLinesGroup, RubyFormatterConstants.LINES_FILE_BETWEEN_METHOD, "Between methods");
		//
		Group emptyLinesInternalGroup = SWTFactory.createGroup(parent, "Blank lines within class/module declarations",
				2, 1, GridData.FILL_HORIZONTAL);
		manager.createNumber(emptyLinesInternalGroup, RubyFormatterConstants.LINES_BEFORE_FIRST,
				"Before first declaration");
		manager.createNumber(emptyLinesInternalGroup, RubyFormatterConstants.LINES_BEFORE_MODULE,
				"Before nested module declarations");
		manager.createNumber(emptyLinesInternalGroup, RubyFormatterConstants.LINES_BEFORE_CLASS,
				"Before nested class declarations");
		manager.createNumber(emptyLinesInternalGroup, RubyFormatterConstants.LINES_BEFORE_METHOD,
				"Before method declarations");
		//
		Group preserveGroup = SWTFactory.createGroup(parent, "Existing blank lines", 2, 1, GridData.FILL_HORIZONTAL);
		manager.createNumber(preserveGroup, RubyFormatterConstants.LINES_PRESERVE, "Number of empty lines to preserve");
	}

	protected URL getPreviewContent()
	{
		return getClass().getResource("blank-lines-preview.rb");
	}

}
