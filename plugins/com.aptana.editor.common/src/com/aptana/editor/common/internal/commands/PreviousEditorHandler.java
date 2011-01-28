/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.internal.commands;

/**
 * This handler activates the Next editor without showing the Editor list popup.
 * 
 * @author schitale
 */
public class PreviousEditorHandler extends NextEditorHandler
{
	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.internal.commands.NextEditorHandler#next()
	 */
	@Override
	protected boolean next()
	{
		return false;
	}
}
