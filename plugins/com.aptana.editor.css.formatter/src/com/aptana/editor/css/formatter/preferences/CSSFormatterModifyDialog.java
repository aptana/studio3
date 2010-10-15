package com.aptana.editor.css.formatter.preferences;

import com.aptana.formatter.ui.FormatterModifyDialog;
import com.aptana.formatter.ui.IFormatterModifyDialogOwner;
import com.aptana.formatter.ui.IScriptFormatterFactory;

/**
 * CSS formatter settings dialog.
 * 
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
		setTitle("CSS Formatter");
	}

	protected void addPages()
	{
		addTabPage("Control Statements", new CSSFormatterControlStatementsPage(this));
		addTabPage("Braces", new CSSFormatterBracesPage(this));

		// TODO: Fix issue with comments not wrapping correctly with newlines
		// Something like: /* 
		//						border: 1px solid red;
		//					*/
//		addTabPage("Comments", new CSSFormatterCommentsPage(this));
	}
}