/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common;

/**
 * @author Max Stepanov
 */
public interface ICommonConstants
{

	public String CONTENT_TYPE_UKNOWN = "com.aptana.contenttype.unknown"; //$NON-NLS-1$

	public String DEFAULT_PARTITIONING = "com.aptana.editor.default_partitioning"; //$NON-NLS-1$

	/**
	 * The position category used to manage positions for scopes. We sadd/remove TypedPositions under this category to
	 * query and update the scopes in the file.
	 */
	public String SCOPE_CATEGORY = "scopes"; //$NON-NLS-1$

	/**
	 * Code formatter action definition ID, as defined in the plugin.xml ("com.aptana.editor.commands.Format")
	 */
	public String FORMATTER_ACTION_DEFINITION_ID = "com.aptana.editor.commands.Format"; //$NON-NLS-1$

	/**
	 * Code-formatter action ID ("com.aptana.editor.action.Format")
	 */
	public String FORMATTER_ACTION_ID = "com.aptana.editor.action.Format"; //$NON-NLS-1$
	
	/**
	 * JavaScript Source editor id. Make sure will have the same id here and in the js editor extension.
	 */
	public static final String JS_EDITOR_ID = "com.aptana.editor.js"; //$NON-NLS-N$

}
