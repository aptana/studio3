package com.aptana.editor.css.formatter.preferences;

import com.aptana.formatter.ui.FormatterModifyDialog;
import com.aptana.formatter.ui.IFormatterModifyDialogOwner;
import com.aptana.formatter.ui.IScriptFormatterFactory;

/**
 * HTML formatter settings dialog.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class CSSFormatterModifyDialog extends FormatterModifyDialog
{
	/**
	 * Constructs a new HTMLFormatterModifyDialog
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
	}
}