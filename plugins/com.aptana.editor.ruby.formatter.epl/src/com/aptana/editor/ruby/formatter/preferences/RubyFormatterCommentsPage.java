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

public class RubyFormatterCommentsPage extends FormatterModifyTabPage
{

	/**
	 * @param dialog
	 */
	public RubyFormatterCommentsPage(IFormatterModifyDialog dialog)
	{
		super(dialog);
	}

	protected void createOptions(IFormatterControlManager manager, Composite parent)
	{
		Group commentWrappingGroup = SWTFactory.createGroup(parent,
				Messages.RubyFormatterCommentsPage_commentFormatting, 2, 1, GridData.FILL_HORIZONTAL);
		manager.createCheckbox(commentWrappingGroup, RubyFormatterConstants.WRAP_COMMENTS,
				Messages.RubyFormatterCommentsPage_enableCommentWrapping, 2);
		manager.createNumber(commentWrappingGroup, RubyFormatterConstants.WRAP_COMMENTS_LENGTH,
				Messages.RubyFormatterCommentsPage_maxCommentWidth);
	}

	protected URL getPreviewContent()
	{
		return getClass().getResource("wrapping-preview.rb"); //$NON-NLS-1$
	}

}
