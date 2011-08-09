/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.core.internal.platform;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;

/**
 * @author Max Stepanov
 */
public final class CoreMacOSX {

	public static final String kDocumentsFolderType = "docs"; //$NON-NLS-1$
	public static final String kDesktopFolderType = "desk"; //$NON-NLS-1$
	public static final String kVolumeRootFolderType = "root"; //$NON-NLS-1$
	public static final String kCurrentUserFolderType = "cusr"; //$NON-NLS-1$

	/**
	 * 
	 */
	private CoreMacOSX() {
	}

	public static String FileManager_findFolder(boolean isUserDomain, String folderType) {
		try {
			Class<?> FileManagerClass = Class.forName("com.apple.eio.FileManager"); //$NON-NLS-1$
			Method findFolderMethod = FileManagerClass.getMethod("findFolder", new Class[] { short.class, int.class }); //$NON-NLS-1$
			Method OSTypeToIntMethod = FileManagerClass.getMethod("OSTypeToInt", new Class[] { String.class }); //$NON-NLS-1$
			Field kUserDomainField = FileManagerClass.getField("kUserDomain"); //$NON-NLS-1$
			Field kSystemDomainField = FileManagerClass.getField("kSystemDomain"); //$NON-NLS-1$

			short domain = isUserDomain ? kUserDomainField.getShort(FileManagerClass) : kSystemDomainField.getShort(FileManagerClass);
			int type = ((Integer) OSTypeToIntMethod.invoke(FileManagerClass, new Object[] { folderType })).intValue();
			return (String) findFolderMethod.invoke(FileManagerClass, new Object[] { domain, type });
		} catch (Exception e) {
			IdeLog.logError(CorePlugin.getDefault(), e);
		}
		return null;
	}
}
