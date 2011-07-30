/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.formatter.preferences;

import com.aptana.formatter.IScriptFormatterFactory;
import com.aptana.formatter.ui.IFormatterModifyDialogOwner;
import com.aptana.formatter.ui.preferences.FormatterModifyDialog;

/**
 * HTML formatter settings dialog.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class HTMLFormatterModifyDialog extends FormatterModifyDialog
{
	/**
	 * Constructs a new HTMLFormatterModifyDialog
	 * 
	 * @param dialogOwner
	 * @param formatterFactory
	 */
	public HTMLFormatterModifyDialog(IFormatterModifyDialogOwner dialogOwner, IScriptFormatterFactory formatterFactory)
	{
		super(dialogOwner, formatterFactory);
	}

	protected void addPages()
	{
		addTabPage(Messages.HTMLFormatterModifyDialog_newLinesTabName, new HTMLFormatterNewLinesPage(this));
		addTabPage(Messages.HTMLFormatterModifyDialog_intentationTabName, new HTMLFormatterIndentationTabPage(this));
		addTabPage(Messages.HTMLFormatterModifyDialog_blankLinesTabName, new HTMLFormatterBlankLinesPage(this));
		addTabPage(Messages.HTMLFormatterModifyDialog_spacesTabName, new HTMLFormatterWhitespacesPage(this));
		addTabPage(Messages.HTMLFormatterModifyDialog_commentsTabName, new HTMLFormatterCommentsPage(this));
		addTabPage(com.aptana.formatter.ui.preferences.Messages.FormatterModifyDialog_OffOnTags,
				new HTMLFormatterOffOnPage(this));
	}
}
