package com.aptana.editor.css.formatter.preferences;

import java.net.URL;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.aptana.editor.css.formatter.CSSFormatterConstants;
import com.aptana.formatter.ui.FormatterModifyTabPage;
import com.aptana.formatter.ui.IFormatterControlManager;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.ui.util.SWTFactory;

public class CSSFormatterBracesPage extends FormatterModifyTabPage
{
//	private final String[] tabOptionItems = new String[] { 1, 2, 3 };
//	private final String[] tabOptionNames = new String[] { "Same line", "Next line", "Next line indented" };

	public CSSFormatterBracesPage(IFormatterModifyDialog dialog)
	{
		super(dialog);
	}

	protected void createOptions(IFormatterControlManager manager, Composite parent)
	{
		Group bracesGroup = SWTFactory.createGroup(parent, "Brace positions", 1, 2, GridData.FILL_HORIZONTAL);
		
		manager.createCheckbox(bracesGroup, CSSFormatterConstants.NEW_LINES_BEFORE_BLOCKS, "Insert new line before block");
//		manager.createCombo(parent, /*some constant for braces indent*/, "Blocks", tabOptionItems, tabOptionNames);
	}

	protected URL getPreviewContent()
	{
		return getClass().getResource("braces-preview.css");
	}

}
