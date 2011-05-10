/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.io.compare;

import java.io.InputStream;

import org.eclipse.compare.BufferedContent;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.IEditableContent;
import org.eclipse.compare.IModificationDate;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.IStructureComparator;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

import com.aptana.core.util.FileUtil;

/**
 * @author Michael Xia (mxia@appcelerator.com)
 */
public class FileStoreNode extends BufferedContent implements IStructureComparator, ITypedElement, IEditableContent,
		IModificationDate
{

	private IFileStore fileStore;

	/**
	 * Creates a new file store node.
	 * 
	 * @param fileStore
	 */
	public FileStoreNode(IFileStore fileStore)
	{
		this.fileStore = fileStore;
	}

	@Override
	protected InputStream createStream() throws CoreException
	{
		if (fileStore == null || fileStore.fetchInfo().isDirectory())
		{
			return null;
		}
		return fileStore.openInputStream(EFS.NONE, null);
	}

	public Object[] getChildren()
	{
		return new Object[0];
	}

	public Image getImage()
	{
		return fileStore == null ? null : CompareUI.getImage(getType());
	}

	public String getName()
	{
		return fileStore == null ? null : fileStore.getName();
	}

	public String getType()
	{
		return fileStore == null ? ITypedElement.UNKNOWN_TYPE : FileUtil.getExtension(fileStore.getName());
	}

	public boolean isEditable()
	{
		return false;
	}

	public ITypedElement replace(ITypedElement dest, ITypedElement src)
	{
		return null;
	}

	public long getModificationDate()
	{
		return fileStore == null ? 0 : fileStore.fetchInfo().getLastModified();
	}
}
