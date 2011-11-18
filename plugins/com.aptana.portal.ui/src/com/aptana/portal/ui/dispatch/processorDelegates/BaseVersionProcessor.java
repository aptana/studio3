/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.processorDelegates;

import com.aptana.configurations.processor.AbstractProcessorDelegate;

/**
 * A base application version retrieval delegate for applications that use '--version' in order to get their version.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public abstract class BaseVersionProcessor extends AbstractProcessorDelegate
{
	private static final String VERSION_COMMAND_SYNTAX = "--version"; //$NON-NLS-1$

	/**
	 * Constructs a new BaseVersionProcessor
	 */
	public BaseVersionProcessor()
	{
		// Make sure we add the supported commands
		supportedCommands.put(VERSION_COMMAND, VERSION_COMMAND_SYNTAX);
	}
}
