/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.ui.views;

import org.eclipse.osgi.util.NLS;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.samples.ui.views.messages"; //$NON-NLS-1$

	public static String SamplesView_ERR_UnableToOpenFile;
	public static String SamplesView_ERR_UnableToOpenHelp;
	public static String SamplesView_LBL_ImportSample;
	public static String SamplesView_LBL_PreviewSample;
	public static String SamplesView_LBL_ViewHelp;
	public static String SamplesView_LBL_CollapseAll;
	public static String SamplesView_LBL_Open;
	public static String SamplesView_TTP_PreviewSample;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
