/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
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

import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.FileUtil;
import com.aptana.ide.ui.io.Utils;

/**
 * @author Michael Xia (mxia@appcelerator.com)
 */
public class FileStoreNode extends BufferedContent implements IStructureComparator, ITypedElement, IEditableContent,
		IModificationDate
{

	private IFileStore fileStore;
	private String name;

	/**
	 * Creates a new file store node.
	 * 
	 * @param fileStore
	 */
	public FileStoreNode(IFileStore fileStore)
	{
		this.fileStore = fileStore;
	}

	public FileStoreNode(IFileStore fileStore, String name)
	{
		this(fileStore);
		this.name = name;
	}

	@Override
	protected InputStream createStream() throws CoreException
	{
		if (fileStore == null || Utils.isDirectory(fileStore))
		{
			return null;
		}
		return fileStore.openInputStream(EFS.NONE, null);
	}

	public Object[] getChildren()
	{
		return ArrayUtil.NO_OBJECTS;
	}

	public Image getImage()
	{
		return (fileStore == null) ? null : CompareUI.getImage(getType());
	}

	public String getName()
	{
		return (name != null) ? name : (fileStore == null) ? null : fileStore.getName();
	}

	public String getType()
	{
		return (fileStore == null) ? ITypedElement.UNKNOWN_TYPE : FileUtil.getExtension(fileStore.getName());
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
		return (fileStore == null) ? 0 : Utils.getDetailedFileInfo(fileStore).getLastModified();
	}
}
