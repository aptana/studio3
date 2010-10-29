/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.formatter.preferences.profile;

import org.eclipse.osgi.util.NLS;

/**
 * @author Yuri Strot
 */
public class ProfilesMessages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.formatter.preferences.profile.ProfilesMessages"; //$NON-NLS-1$
	public static String ProfileStore_noValueForKey;
	public static String ProfileStore_readingProblems;
	public static String ProfileStore_serializingProblems;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, ProfilesMessages.class);
	}

	private ProfilesMessages()
	{
	}
}
