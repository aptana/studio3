/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.formatter.preferences;

import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.aptana.editor.xml.formatter.XMLFormatterConstants;
import com.aptana.formatter.ui.IFormatterControlManager;
import com.aptana.formatter.ui.preferences.FormatterModifyDialog;
import com.aptana.formatter.ui.preferences.FormatterModifyTabPage;
import com.aptana.formatter.ui.util.SWTFactory;

public class XMLFormatterNewLinesPage extends FormatterModifyTabPage
{
	private static final String NEW_LINES_PREVIEW_FILE = "preview.xml"; //$NON-NLS-1$

	/**
	 * Constructor.
	 * 
	 * @param dialog
	 *            A {@link FormatterModifyDialog}
	 */
	public XMLFormatterNewLinesPage(FormatterModifyDialog dialog)
	{
		super(dialog);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.formatter.ui.FormatterModifyTabPage#createOptions(com.aptana.formatter.ui.IFormatterControlManager,
	 * org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createOptions(IFormatterControlManager manager, Composite parent)
	{
		Group group = SWTFactory.createGroup(parent, Messages.XMLFormatterNewLinesPage_exclusionsGroupLabel, 1, 1,
				GridData.FILL_BOTH);
		Label exclutionLabel = new Label(group, SWT.WRAP);
		exclutionLabel.setText(Messages.XMLFormatterNewLinesPage_exclusionsMessageLabel);
		manager.createManagedList(group, XMLFormatterConstants.NEW_LINES_EXCLUDED_TAGS);
		manager.createCheckbox(group, XMLFormatterConstants.NEW_LINES_EXCLUDED_ON_TEXT_NODES,
				Messages.XMLFormatterNewLinesPage_exclude_text_node_label);

	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.FormatterModifyTabPage#getPreviewContent()
	 */
	protected URL getPreviewContent()
	{
		return getClass().getResource(NEW_LINES_PREVIEW_FILE);
	}

}
