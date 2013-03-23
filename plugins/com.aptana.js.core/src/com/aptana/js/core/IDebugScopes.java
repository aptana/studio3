/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core;


/**
 * A interface to capture the various scopes available during debugging. These need to match the items in the .options
 * file at the root of the plugin
 * 
 * @author Ingo Muschenetz
 */
public interface IDebugScopes
{
	/**
	 * A debug scope used to display info on writes to JS indexes
	 */
	String INDEX_WRITES = JSCorePlugin.PLUGIN_ID + "/debug/show_index_writes"; //$NON-NLS-1$

	/**
	 * A debug scope used to display the stages of JS indexing
	 */
	String INDEXING_STEPS = JSCorePlugin.PLUGIN_ID + "/debug/show_indexing_steps"; //$NON-NLS-1$
}
