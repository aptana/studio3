/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

/**
 * A interface to capture the various scopes available during debugging. These need to match the items in the .options
 * file at the root of the plugin
 * 
 * @author Ingo Muschenetz
 */
public interface IDebugScopes
{

	/**
	 * General debug scope
	 */
	String DEBUG = CommonEditorPlugin.PLUGIN_ID + "/debug"; //$NON-NLS-1$

	/**
	 * Items related to the presentation process
	 */
	String PARTITIONER = CommonEditorPlugin.PLUGIN_ID + "/debug/partitioner"; //$NON-NLS-1$

	/**
	 * Items related to the partitioning process
	 */
	String PRESENTATION = CommonEditorPlugin.PLUGIN_ID + "/debug/presentation"; //$NON-NLS-1$

	/**
	 * Items related to the content assist process
	 */
	String CONTENT_ASSIST = CommonEditorPlugin.PLUGIN_ID + "/debug/content_assist"; //$NON-NLS-1$

	/**
	 * Items related to drag/drop
	 */
	String DRAG_DROP = CommonEditorPlugin.PLUGIN_ID + "/debug/drag_drop"; //$NON-NLS-1$

	/**
	 * Items related to AST
	 */
	String AST = CommonEditorPlugin.PLUGIN_ID + "/debug/ast"; //$NON-NLS-1$
}
