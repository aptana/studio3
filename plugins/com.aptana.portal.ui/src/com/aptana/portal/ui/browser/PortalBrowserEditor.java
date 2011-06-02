/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.browser;

import com.aptana.portal.ui.internal.Portal;

/**
 * A portal browser editor.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 * @author Max Stepanov
 */
public class PortalBrowserEditor extends AbstractPortalBrowserEditor
{

	public static final String WEB_BROWSER_EDITOR_ID = "com.aptana.portal.ui.browser.portal"; //$NON-NLS-1$

	private static final String TITLE_TIP = "Aptana Portal"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#getTitleToolTip()
	 */
	@Override
	public String getTitleToolTip()
	{
		return TITLE_TIP;
	}

	/**
	 * Returns the base URL prefix that will be used to verify the location of the page and register the dispatcher in
	 * case the page is under this path.
	 */
	protected String getBaseURLPrefix()
	{
		return Portal.BASE_URL_PREFIX;
	}

}