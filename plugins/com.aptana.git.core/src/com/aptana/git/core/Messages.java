/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS // NO_UCD
{
	private static final String BUNDLE_NAME = "com.aptana.git.core.messages"; //$NON-NLS-1$
	public static String GitMoveDeleteHook_CannotModifyRepository_ErrorMessage; // NO_UCD
	public static String GitRepositoryProviderType_AttachingProject_Message; // NO_UCD
	public static String GitRepositoryProviderType_AutoShareJob_Title; // NO_UCD
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
