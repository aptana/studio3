/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.ui.io.internal;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.runtime.Status;

import com.aptana.ide.ui.io.IOUIPlugin;

/**
 * @author Max Stepanov
 *
 */
public class FetchFileInfoStatus extends Status {

	private IFileInfo fileInfo;
	
	/**
	 * @param fileInfo
	 */
	public FetchFileInfoStatus(IFileInfo fileInfo) {
		super(OK, IOUIPlugin.PLUGIN_ID, OK_STATUS.getMessage());
		this.fileInfo = fileInfo;
	}

	/**
	 * @return the fileInfo
	 */
	public IFileInfo getFileInfo() {
		return fileInfo;
	}	

}
