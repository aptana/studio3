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
 * with certain Eclipse Public Licensed code and certain additional terms
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
