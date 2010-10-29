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

import com.aptana.formatter.IScriptFormatterFactory;
import com.aptana.formatter.ui.IFormatterModifyDialogOwner;
import com.aptana.formatter.ui.preferences.FormatterModifyDialog;

public class RubyFormatterModifyDialog extends FormatterModifyDialog
{

	/**
	 * @param parent
	 */
	public RubyFormatterModifyDialog(IFormatterModifyDialogOwner dialogOwner, IScriptFormatterFactory formatterFactory)
	{
		super(dialogOwner, formatterFactory);
	}

	protected void addPages()
	{
		addTabPage(Messages.RubyFormatterModifyDialog_indentationTabTitle, new RubyFormatterIndentationTabPage(this));
		addTabPage(Messages.RubyFormatterModifyDialog_blanksTabTitle, new RubyFormatterBlankLinesPage(this));
		addTabPage(Messages.RubyFormatterModifyDialog_commentsTabTitle, new RubyFormatterCommentsPage(this));
	}

}
