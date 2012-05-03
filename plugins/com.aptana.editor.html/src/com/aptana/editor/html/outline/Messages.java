/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.outline;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages"; //$NON-NLS-1$
	
	public static String HTMLOutlineContentProvider_PlaceholderItemLabel;
	public static String HTMLOutlineContentProvider_FetchingExternalFilesJobName;
	public static String HTMLOutlineContentProvider_UnableToResolveFile_Error;
	public static String HTMLOutlineContentProvider_FileNotFound_Error;
	
	static
	{
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String HTMLOutlineContentProvider_ERR_ParseContent;
}
