/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

/**
 * A interface to capture the various scopes available during debugging. These need to match the items in the .options
 * file at the root of the plugin
 * 
 * @author Max Stepanov
 */
public interface IDebugScopes
{
	/**
	 * Items related to the partitioning process
	 */
	String PARTITIONER = CommonEditorPlugin.PLUGIN_ID + "/debug/partitioner"; //$NON-NLS-1$

	/**
	 * Items related to the partitioning process
	 */
	String PRESENTATION = CommonEditorPlugin.PLUGIN_ID + "/debug/presentation"; //$NON-NLS-1$
}
