/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.internal.platform;

import java.text.MessageFormat;

/**
 * @author Max Stepanov
 *
 */
public final class CoreNatives {

	/**
	 * Major version number (must be >= 0)
	 */
    private static final int MAJOR_VERSION = 3;
	
	/**
	 * Minor version number (must be in the range 0..999)
	 */
    private static final int MINOR_VERSION = 0;
	
	/**
	 * release number (must be >= 0)
	 */
	private static final int RELEASE = 0;

	
	/* Win32 */
	
	/**
	 * CSIDL_DESKTOPDIRECTORY
	 */
	public static final int CSIDL_DESKTOPDIRECTORY = 0x0010;
	
	/**
	 * CSIDL_DESKTOPDIRECTORY
	 */
	public static final int CSIDL_LOCAL_APPDATA = 0x001C;

	/**
	 * FILE_ATTRIBUTE_HIDDEN
	 */
	public static final int FILE_ATTRIBUTE_HIDDEN = 0x00000002;
	
	/**
	 * FILE_ATTRIBUTE_TEMPORARY
	 */
	public static final int FILE_ATTRIBUTE_TEMPORARY = 0x00000100;

	/**
	 * SHOP_PRINTERNAME
	 */
	public static final int SHOP_PRINTERNAME = 0x00000001;

	/**
	 * SHOP_FILEPATH
	 */
	public static final int SHOP_FILEPATH = 0x00000002;

	/**
	 * SHOP_VOLUMEGUID
	 */
	public static final int SHOP_VOLUMEGUID = 0x00000004;

	/**
	 * SW_SHOWNORMAL
	 */
	public static final int SW_SHOWNORMAL = 1;
	/**
	 * SW_SHOWMINNOACTIVE
	 */
	public static final int SW_SHOWMINNOACTIVE = 7;

	public static final long HKEY_CLASSES_ROOT = 0x80000000;
	public static final long HKEY_CURRENT_USER = 0x80000001;
	public static final long HKEY_LOCAL_MACHINE = 0x80000002;

	public static final int KEY_QUERY_VALUE	= 0x00000001;
	public static final int KEY_SET_VALUE	= 0x00000002;
	public static final int KEY_CREATE_SUB_KEY	= 0x00000004;
	public static final int KEY_ENUMERATE_SUB_KEYS	= 0x00000008;
	public static final int KEY_READ	= 0x00020019;
	public static final int KEY_WRITE	= 0x00020006;
	public static final int KEY_ALL_ACCESS	= 0x000F003F;

	static {
		System.loadLibrary("core"+getVersionString()); //$NON-NLS-1$
	}
	
	private CoreNatives() {
	}
	
	// CHECKSTYLE:OFF
	/**
	 * 
	 * GetProcessList
	 *
	 * @return Object[]
	 */
	public static final native Object[] GetProcessList();

	/**
	 * 
	 * GetCurrentProcessId
	 *
	 * @return int
	 */
	public static final native int GetCurrentProcessId();

	/**
	 * 
	 * KillProcess
	 *
	 * @param pid
	 */
	public static final native void KillProcess(int pid);

	/**
	 * 
	 * GetSpecialFolderPath
	 *
	 * @param csidl
	 * @return String
	 */
	public static final native String GetSpecialFolderPath(int csidl);
	
	/**
	 * 
	 * SHObjectProperties
	 *
	 * @param handle
	 * @param type
	 * @param object
	 * @param page
	 * @return boolean
	 */
	public static final native boolean SHObjectProperties(int/*long*/ handle, int type, String object, String page);

	/**
	 * 
	 * ExpandEnvironmentStrings
	 *
	 * @param path
	 * @return String
	 */
	public static final native String ExpandEnvironmentStrings(String path);
	
	/**
	 * SetFileAttributes
	 *
	 * @param path
	 * @param set
	 * @param clear
	 * @return boolean
	 */
	public static final native boolean SetFileAttributes(String path, int set, int clear);

	/**
	 * 
	 * RegOpenKey
	 * Open regestry key
	 *
	 * @param hKeyParent
	 * @param keyName
	 * @param accessMask
	 * @param hKeyResult
	 * @return boolean
	 */
	public static final native boolean RegOpenKey(long hKeyParent, String keyName, int accessMask, /*out*/long[] hKeyResult);

	/**
	 * 
	 * RegCreateKey
	 * Create regestry key
	 *
	 * @param hKeyParent
	 * @param keyName
	 * @param accessMask
	 * @param hKeyResult
	 * @return boolean
	 */
	public static final native boolean RegCreateKey(long hKeyParent, String keyName, int accessMask, /*out*/long[] hKeyResult);

	/**
	 * 
	 * RegCloseKey
	 * Close previously opened regestry key
	 *
	 * @param hKey
	 * @return boolean
	 */
	public static final native boolean RegCloseKey(long hKey);

	/**
	 * 
	 * RegQueryValue
	 * Read value of regestry key
	 *
	 * @param hKey
	 * @param valueName
	 * @param valueResult
	 * @return boolean
	 */
	public static final native boolean RegQueryValue(long hKey, String valueName, /*out*/String[] valueResult);

	/**
	 * 
	 * RegSetValue
	 * Set value of regestry key
	 *
	 * @param hKey
	 * @param valueName
	 * @param value
	 * @return boolean
	 */
	public static final native boolean RegSetValue(long hKey, String valueName, String value);

	/**
	 * IsUserAnAdmin
	 * Tests whether the current user is a member of the Administrator's group.
	 */
	public static final native boolean IsUserAnAdmin();
	
	/**
	 * ShellExecuteEx
	 * @param file
	 * @param params
	 * @param verb
	 * @param directory
	 * @param nShow
	 * @return
	 */
	public static final native boolean ShellExecuteEx(String file, String params, String verb, String directory, int nShow);
	// CHECKSTYLE:ON
	
	
	private static String getVersionString() {
		return MessageFormat.format("_{0}_{1}_{2}", new Object[]{ //$NON-NLS-1$
					Integer.toString(MAJOR_VERSION),
					Integer.toString(MINOR_VERSION),
					Integer.toString(RELEASE)
				});
	}
}
