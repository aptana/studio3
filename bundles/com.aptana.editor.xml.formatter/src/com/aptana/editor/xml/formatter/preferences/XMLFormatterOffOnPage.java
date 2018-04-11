/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.formatter.preferences;

import java.net.URL;

import com.aptana.editor.xml.formatter.XMLFormatterConstants;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.preferences.AbstractFormatterOffOnPage;

/**
 * A formatter On/Off page for XML.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class XMLFormatterOffOnPage extends AbstractFormatterOffOnPage
{
	private static final String ON_OFF_PREVIEW_FILE = "off-on-preview.xml"; //$NON-NLS-1$

	/**
	 * @param dialog
	 */
	public XMLFormatterOffOnPage(IFormatterModifyDialog dialog)
	{
		super(dialog);
	}

	protected URL getPreviewContent()
	{
		return getClass().getResource(ON_OFF_PREVIEW_FILE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.preferences.AbstractFormatterOffOnPage#getOffOnEnablementKey()
	 */
	@Override
	protected String getOffOnEnablementKey()
	{
		return XMLFormatterConstants.FORMATTER_OFF_ON_ENABLED;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.preferences.AbstractFormatterOffOnPage#getOffTextIdentifierKey()
	 */
	@Override
	protected String getOffTextIdentifierKey()
	{
		return XMLFormatterConstants.FORMATTER_OFF;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.preferences.AbstractFormatterOffOnPage#getOnTextIdentifierKey()
	 */
	@Override
	protected String getOnTextIdentifierKey()
	{
		return XMLFormatterConstants.FORMATTER_ON;
	}
}
