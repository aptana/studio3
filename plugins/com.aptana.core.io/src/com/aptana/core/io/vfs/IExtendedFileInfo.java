/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.core.io.vfs;

import org.eclipse.core.filesystem.IFileInfo;

/**
 * @author Max Stepanov
 *
 */
public interface IExtendedFileInfo extends IFileInfo {

	public static final long PERMISSION_OWNER_READ = 1 << 8;
	public static final long PERMISSION_OWNER_WRITE = 1 << 7;
	public static final long PERMISSION_OWNER_EXECUTE = 1 << 6;

	public static final long PERMISSION_GROUP_READ = 1 << 5;
	public static final long PERMISSION_GROUP_WRITE = 1 << 4;
	public static final long PERMISSION_GROUP_EXECUTE = 1 << 3;

	public static final long PERMISSION_OTHERS_READ = 1 << 2;
	public static final long PERMISSION_OTHERS_WRITE = 1 << 1;
	public static final long PERMISSION_OTHERS_EXECUTE = 1 << 0;

	/**
	 * Option flag constant (value 1 &lt;&lt;16) indicating that a
	 * file's permissions should be updated.
	 * 
	 * @see org.eclipse.core.filesystem.IFileStore#putInfo(IFileInfo, int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public static final int SET_PERMISSIONS = 1 << 16;

	/**
	 * Option flag constant (value 1 &lt;&lt;17) indicating that a
	 * file's group should be updated.
	 * 
	 * @see org.eclipse.core.filesystem.IFileStore#putInfo(IFileInfo, int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public static final int SET_GROUP = 1 << 17;

	public long getPermissions();
	public void setPermissions(long permissions);
	
	public String getOwner();
	public void setOwner(String owner);
	
	public String getGroup();
	public void setGroup(String group);

}
