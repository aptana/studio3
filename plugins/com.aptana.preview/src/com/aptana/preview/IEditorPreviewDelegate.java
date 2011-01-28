/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.preview;

import java.net.URI;

import org.eclipse.ui.IEditorPart;

public interface IEditorPreviewDelegate
{
	/**
	 * Initialize delegate for the specified editor part.
	 * @param targetEditorPart
	 */
	public void init(IEditorPart targetEditorPart);
	
	/**
	 * Release any references.
	 */
	public void dispose();
	
	/**
	 * Checks if a content change from an url should trigger the target editor to update its preview content.
	 * 
	 * @param uri
	 *            the uri which content has changed
	 * @return true if this editor should update its preview, false otherwise
	 */
	public boolean isLinked(URI uri);
}
