/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.formatter.preferences;

import com.aptana.formatter.IScriptFormatterFactory;
import com.aptana.formatter.ui.IFormatterModifyDialogOwner;
import com.aptana.formatter.ui.preferences.FormatterModifyDialog;

/**
 * XML formatter settings dialog.
 */
public class XMLFormatterModifyDialog extends FormatterModifyDialog
{
	/**
	 * Constructs a new XMLFormatterModifyDialog
	 * 
	 * @param dialogOwner
	 * @param formatterFactory
	 */
	public XMLFormatterModifyDialog(IFormatterModifyDialogOwner dialogOwner, IScriptFormatterFactory formatterFactory)
	{
		super(dialogOwner, formatterFactory);
	}

	protected void addPages()
	{
		addTabPage(Messages.XMLFormatterModifyDialog_indentationTabName, new XMLFormatterIndentationPage(this));
		addTabPage(Messages.XMLFormatterModifyDialog_newLinesTabName, new XMLFormatterNewLinesPage(this));
		addTabPage(Messages.XMLFormatterModifyDialog_BlankLinesTabName, new XMLFormatterBlankLinesPage(this));
		addTabPage(com.aptana.formatter.ui.preferences.Messages.FormatterModifyDialog_OffOnTags,
				new XMLFormatterOffOnPage(this));

		// TODO: Fix issue with comments not wrapping correctly with newlines
		// Something like: /*
		// border: 1px solid red;
		// */
		// addTabPage("Comments", new CSSFormatterCommentsPage(this));
	}
}