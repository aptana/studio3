/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.old.editors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.compare.BufferedContent;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.IEditableContent;
import org.eclipse.compare.IModificationDate;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.IStructureComparator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

import com.aptana.core.util.FileUtil;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class FileNode extends BufferedContent implements IStructureComparator, ITypedElement, IEditableContent,
		IModificationDate
{

	private File file;

	/**
	 * Creates a new file node
	 * 
	 * @param file
	 */
	public FileNode(File file)
	{
		this.file = file;
	}

	/**
	 * @see org.eclipse.compare.BufferedContent#createStream()
	 */
	protected InputStream createStream() throws CoreException
	{
		if (this.file != null)
		{
			try
			{
				return new FileInputStream(file);
			}
			catch (FileNotFoundException e)
			{
			}
		}
		return null;
	}

	/**
	 * @see org.eclipse.compare.structuremergeviewer.IStructureComparator#getChildren()
	 */
	public Object[] getChildren()
	{
		return new Object[0];
	}

	/**
	 * @see org.eclipse.compare.ITypedElement#getImage()
	 */
	public Image getImage()
	{
		if (this.file != null)
		{
			return CompareUI.getImage(getType());
		}
		return null;
	}

	/**
	 * @see org.eclipse.compare.ITypedElement#getName()
	 */
	public String getName()
	{
		if (this.file != null)
		{
			return this.file.getName();
		}
		return null;
	}

	/**
	 * @see org.eclipse.compare.ITypedElement#getType()
	 */
	public String getType()
	{
		if (this.file != null)
		{
			return FileUtil.getExtension(this.file.getName());
		}
		return ITypedElement.UNKNOWN_TYPE;
	}

	/**
	 * @see org.eclipse.compare.IEditableContent#isEditable()
	 */
	public boolean isEditable()
	{
		return false;
	}

	/**
	 * @see org.eclipse.compare.IEditableContent#replace(org.eclipse.compare.ITypedElement,
	 *      org.eclipse.compare.ITypedElement)
	 */
	public ITypedElement replace(ITypedElement dest, ITypedElement src)
	{
		return null;
	}

	/**
	 * @see org.eclipse.compare.IModificationDate#getModificationDate()
	 */
	public long getModificationDate()
	{
		if (this.file != null)
		{
			return this.file.lastModified();
		}
		return 0;
	}

}
