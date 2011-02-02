/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.formatter.preferences;

import java.net.URL;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.aptana.editor.xml.formatter.XMLFormatterConstants;
import com.aptana.formatter.ui.IFormatterControlManager;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.preferences.FormatterModifyTabPage;
import com.aptana.formatter.ui.util.SWTFactory;

public class XMLFormatterBlankLinesPage extends FormatterModifyTabPage
{
	private static final String BLANK_LINES_PREVIEW_NAME = "preview.xml"; //$NON-NLS-1$

	public XMLFormatterBlankLinesPage(IFormatterModifyDialog dialog)
	{
		super(dialog);
	}

	protected void createOptions(IFormatterControlManager manager, Composite parent)
	{
		Group blankLinesGroup = SWTFactory.createGroup(parent,
				Messages.XMLFormatterBlankLinesPage_blankLinesGroupLabel, 2, 1, GridData.FILL_HORIZONTAL);
		manager.createNumber(blankLinesGroup, XMLFormatterConstants.LINES_AFTER_ELEMENTS,
				Messages.XMLFormatterBlankLinesPage_afterElementsLabel);
		manager.createNumber(blankLinesGroup, XMLFormatterConstants.LINES_AFTER_NON_XML_ELEMENTS,
				Messages.XMLFormatterBlankLinesPage_afterNonXMLElementsLabel);
		manager.createNumber(blankLinesGroup, XMLFormatterConstants.LINES_BEFORE_NON_XML_ELEMENTS,
				Messages.XMLFormatterBlankLinesPage_beforeNonXMLElementsLabel);

		Group preserveLinesGroup = SWTFactory.createGroup(parent,
				Messages.XMLFormatterBlankLinesPage_existingBlankLinesLabel, 2, 1, GridData.FILL_HORIZONTAL);
		manager.createNumber(preserveLinesGroup, XMLFormatterConstants.PRESERVED_LINES,
				Messages.XMLFormatterBlankLinesPage_existingBlankLinesToPreserveLabel);
	}

	protected URL getPreviewContent()
	{
		return getClass().getResource(BLANK_LINES_PREVIEW_NAME);
	}

}
