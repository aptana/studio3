/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ui.ftp.console;

import java.io.OutputStream;

import org.eclipse.core.runtime.IAdapterFactory;

import com.aptana.filesystem.ftp.IFTPCommandLog;

/**
 * @author Max Stepanov
 *
 */
public class FTPCommandLog implements IFTPCommandLog {

	/* (non-Javadoc)
	 * @see com.aptana.filesystem.ftp.IFTPCommandLog#getOutputStream()
	 */
	public OutputStream getOutputStream() {
		return FTPConsoleFactory.newConsoleOutputStream();
	}

	@SuppressWarnings("rawtypes")
	public static class Factory implements IAdapterFactory {
		
		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
		 */
		public Object getAdapter(Object adaptableObject, Class adapterType) {
			if (IFTPCommandLog.class.equals(adapterType)) {
				return new FTPCommandLog();
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
		 */
		public Class[] getAdapterList() {
			return new Class[] { IFTPCommandLog.class };
		}
		
	}

}
