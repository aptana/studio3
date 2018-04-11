/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.core.io.vfs;

import org.eclipse.core.filesystem.provider.FileInfo;

/**
 * @author Max Stepanov
 *
 */
public class ExtendedFileInfo extends FileInfo implements IExtendedFileInfo {

	private long permissions;
	private String owner;
	private String group;

	/**
	 * 
	 */
	public ExtendedFileInfo() {
	}

	/**
	 * @param name
	 */
	public ExtendedFileInfo(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see com.aptana.core.io.vfs.IExtendedFileInfo#getGroup()
	 */
	public String getGroup() {
		return group;
	}

	/* (non-Javadoc)
	 * @see com.aptana.core.io.vfs.IExtendedFileInfo#getOwner()
	 */
	public String getOwner() {
		return owner;
	}

	/* (non-Javadoc)
	 * @see com.aptana.core.io.vfs.IExtendedFileInfo#getPermissions()
	 */
	public long getPermissions() {
		return permissions;
	}

	/* (non-Javadoc)
	 * @see com.aptana.core.io.vfs.IExtendedFileInfo#setGroup(java.lang.String)
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/* (non-Javadoc)
	 * @see com.aptana.core.io.vfs.IExtendedFileInfo#setOwner(java.lang.String)
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/* (non-Javadoc)
	 * @see com.aptana.core.io.vfs.IExtendedFileInfo#setPermissions(long)
	 */
	public void setPermissions(long permissions) {
		this.permissions = permissions;
	}

}
