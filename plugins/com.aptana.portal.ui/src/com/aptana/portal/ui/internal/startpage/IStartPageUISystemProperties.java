/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.internal.startpage;

/**
 * Various options that can be passed on the command line to control Studio Start-Page behavior
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public interface IStartPageUISystemProperties
{
	/**
	 * A flag to indicate if the start page should be hidden, no matter what.
	 */
	public static String HIDE_START_PAGE = "studio.hideStartPage"; //$NON-NLS-1$

	/**
	 * A flag to force the start page to load when the Studio loads.
	 */
	public static String FORCE_START_PAGE = "studio.forceStartPage"; //$NON-NLS-1$

	/**
	 * The URL of the Studio start page
	 */
	public static String START_PAGE_URL = "studio.startPageUrl"; //$NON-NLS-1$

	/**
	 * The portal browser option
	 */
	public static String PORTAL_BROWSER = "studio.portalBrowser"; //$NON-NLS-1$

}
