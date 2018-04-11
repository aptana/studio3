/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.formatter.preferences;

import com.aptana.formatter.IScriptFormatterFactory;
import com.aptana.formatter.ui.IFormatterModifyDialogOwner;
import com.aptana.formatter.ui.preferences.FormatterModifyDialog;

/**
 * JS formatter settings dialog.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class JSFormatterModifyDialog extends FormatterModifyDialog
{
	/**
	 * Constructs a new JSFormatterModifyDialog
	 * 
	 * @param dialogOwner
	 * @param formatterFactory
	 */
	public JSFormatterModifyDialog(IFormatterModifyDialogOwner dialogOwner, IScriptFormatterFactory formatterFactory)
	{
		super(dialogOwner, formatterFactory);
	}

	protected void addPages()
	{
		addTabPage(Messages.JSFormatterModifyDialog_newLinesTabName, new JSFormatterNewLinesPage(this));
		addTabPage(Messages.JSFormatterModifyDialog_intentationTabName, new JSFormatterIndentationTabPage(this));
		addTabPage(Messages.JSFormatterModifyDialog_blankLinesTabName, new JSFormatterBlankLinesPage(this));
		addTabPage(Messages.JSFormatterModifyDialog_bracesTabName, new JSFormatterBracesPage(this));
		addTabPage(Messages.JSFormatterModifyDialog_whiteSpacesTabName, new JSFormatterWhiteSpacesPage(this));
		addTabPage(Messages.JSFormatterModifyDialog_commentsTabName, new JSFormatterCommentsPage(this));
		addTabPage(com.aptana.formatter.ui.preferences.Messages.FormatterModifyDialog_OffOnTags,
				new JSFormatterOffOnPage(this));
	}
}
