/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core;

/**
 * A interface to capture the various scopes available during debugging. These need to match the items in the .options
 * file at the root of the plugin
 * 
 * @author Ingo Muschenetz
 */
public interface IDebugScopes
{
	/**
	 * Items related to the logging process
	 */
	String LOGGER = CorePlugin.PLUGIN_ID + "/debug/logger"; //$NON-NLS-1$

	/**
	 * Items related to the indexing process
	 */
	String BUILDER = CorePlugin.PLUGIN_ID + "/debug/builder"; //$NON-NLS-1$

	/**
	 * Items related to the indexing process
	 */
	String BUILDER_INDEXER = CorePlugin.PLUGIN_ID + "/debug/builder/indexer"; //$NON-NLS-1$

	/**
	 * Items related to the indexing process, specific to build participants
	 */
	String BUILDER_PARTICIPANTS = CorePlugin.PLUGIN_ID + "/debug/builder/participants"; //$NON-NLS-1$

	/**
	 * Items related to the indexing process, but additional information, like when we wipe the index
	 */
	String BUILDER_ADVANCED = CorePlugin.PLUGIN_ID + "/debug/builder/advanced"; //$NON-NLS-1$

	/**
	 * Items related to running things on the command line
	 */
	String SHELL = CorePlugin.PLUGIN_ID + "/debug/shell"; //$NON-NLS-1$

	/**
	 * Items related to firefox-specific configuration
	 */
	String FIREFOX = CorePlugin.PLUGIN_ID + "/debug/firefox"; //$NON-NLS-1$

	/**
	 * Items related to extension points configuration
	 */
	String EXTENSION_POINTS = CorePlugin.PLUGIN_ID + "/debug/extension_points"; //$NON-NLS-1$

	/**
	 * Items related to zip utils
	 */
	String ZIPUTIL = CorePlugin.PLUGIN_ID + "/debug/ziputil"; //$NON-NLS-1$

	/**
	 * Items related to the process output on command line.
	 */
	String SHELL_OUTPUT = CorePlugin.PLUGIN_ID + "/debug/shell/output"; //$NON-NLS-1$

}
