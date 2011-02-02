/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.preview;

import org.eclipse.core.runtime.CoreException;

/**
 * @author Max Stepanov
 * 
 */
public interface IPreviewHandler {

	/**
	 * The function does a translation from a source file to a preview request
	 * @param config
	 * @return PreviewConfig null if sources are not supported by this handler
	 * @throws CoreException
	 */
	public PreviewConfig handle(SourceConfig config) throws CoreException;
}
