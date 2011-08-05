/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.usage;

/**
 * @author Kevin Lindsey
 */
public interface ILogEventTypes
{

	/**
	 * Default event type used when no type is specified
	 */
	public static final String UNKNOWN = "<unknown>"; //$NON-NLS-1$

	/**
	 * Preview event type
	 */
	public static final String PREVIEW = "PREVIEW"; //$NON-NLS-1$

	/**
	 * Date/time stamp event
	 */
	public static final String DATE_TIME = "DATE_TIME"; //$NON-NLS-1$

	/**
	 * Studio key type
	 */
	public static final String STUDIO_KEY = "STUDIO_KEY"; //$NON-NLS-1$

	/**
	 * Plug-in key type
	 */
	public static final String FEATURE = "FEATURE"; //$NON-NLS-1$
}
