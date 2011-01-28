/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.processorDelegates;

/**
 * A Ruby version processor that can get the current Ruby version in the system
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class RubyVersionProcessor extends BaseVersionProcessor
{
	private static final String RUBY = "ruby"; //$NON-NLS-1$

	/**
	 * @return "ruby"
	 */
	@Override
	public String getSupportedApplication()
	{
		return RUBY;
	}
}
