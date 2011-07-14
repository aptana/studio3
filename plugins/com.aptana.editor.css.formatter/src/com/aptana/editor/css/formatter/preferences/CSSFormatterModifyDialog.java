/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.formatter.preferences;

import com.aptana.formatter.IScriptFormatterFactory;
import com.aptana.formatter.ui.IFormatterModifyDialogOwner;
import com.aptana.formatter.ui.preferences.FormatterModifyDialog;

/**
 * CSS formatter settings dialog.
 */
public class CSSFormatterModifyDialog extends FormatterModifyDialog
{
	/**
	 * Constructs a new CSSFormatterModifyDialog
	 * 
	 * @param dialogOwner
	 * @param formatterFactory
	 */
	public CSSFormatterModifyDialog(IFormatterModifyDialogOwner dialogOwner, IScriptFormatterFactory formatterFactory)
	{
		super(dialogOwner, formatterFactory);
	}

	protected void addPages()
	{
		addTabPage(Messages.CSSFormatterModifyDialog_indentation_page_tab_name, new CSSFormatterControlStatementsPage(
				this));
		addTabPage(Messages.CSSFormatterModifyDialog_braces_page_tab_name, new CSSFormatterBracesPage(this));
		addTabPage(Messages.CSSFormatterModifyDialog_blank_lines_page_tab_name, new CSSFormatterBlankLinesPage(this));
		addTabPage(Messages.CSSFormatterModifyDialog_spaces_page_tab_title, new CSSFormatterWhiteSpacesPage(this));
		addTabPage(Messages.CSSFormatterModifyDialog_comments_page_tab_name, new CSSFormatterCommentsPage(this));
		addTabPage(com.aptana.formatter.ui.preferences.Messages.FormatterModifyDialog_OffOnTags,
				new CSSFormatterOffOnPage(this));
	}
}