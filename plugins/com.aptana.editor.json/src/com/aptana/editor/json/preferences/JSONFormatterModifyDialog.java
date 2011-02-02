/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json.preferences;

import com.aptana.formatter.IScriptFormatterFactory;
import com.aptana.formatter.ui.IFormatterModifyDialogOwner;
import com.aptana.formatter.ui.preferences.FormatterModifyDialog;

/**
 * JSONFormatterModifyDialog
 */
public class JSONFormatterModifyDialog extends FormatterModifyDialog
{
	/**
	 * Constructs a new HTMLFormatterModifyDialog
	 * 
	 * @param dialogOwner
	 * @param formatterFactory
	 */
	public JSONFormatterModifyDialog(IFormatterModifyDialogOwner dialogOwner, IScriptFormatterFactory formatterFactory)
	{
		super(dialogOwner, formatterFactory);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.preferences.FormatterModifyDialog#addPages()
	 */
	protected void addPages()
	{
		addTabPage(Messages.JSONFormatterModifyDialog_indentation_page_tab_name, new JSONFormatWhitespacePage(this));
	}
}