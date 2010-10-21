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

package com.aptana.ide.core.io.vfs;

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
