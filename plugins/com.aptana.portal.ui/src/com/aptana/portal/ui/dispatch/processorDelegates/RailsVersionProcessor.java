/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.processorDelegates;

/**
 * A rails version processor that can get the current rails version in the system
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class RailsVersionProcessor extends BaseVersionProcessor
{
	private static final String RAILS = "rails"; //$NON-NLS-1$

	/**
	 * @return "rails"
	 */
	@Override
	public String getSupportedApplication()
	{
		return RAILS;
	}
}
