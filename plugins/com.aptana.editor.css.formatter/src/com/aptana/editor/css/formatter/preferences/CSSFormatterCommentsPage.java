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

public class CSSFormatterCommentsPage extends FormatterModifyTabPage
{
	public CSSFormatterCommentsPage(IFormatterModifyDialog dialog)
	{
		super(dialog);
	}

	protected void createOptions(IFormatterControlManager manager, Composite parent)
	{
		Group commentGroup = SWTFactory.createGroup(parent, "Comments Formatting", 2, 1, GridData.FILL_HORIZONTAL);
		manager.createCheckbox(commentGroup, CSSFormatterConstants.WRAP_COMMENTS, "Enable comments wrapping", 2);
		manager.createNumber(commentGroup, CSSFormatterConstants.WRAP_COMMENTS_LENGTH, "Maximum line width");

	}

	protected URL getPreviewContent()
	{
		return getClass().getResource("comments-preview.css");
	}

}
