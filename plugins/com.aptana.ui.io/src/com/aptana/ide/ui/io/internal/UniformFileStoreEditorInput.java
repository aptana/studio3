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
package com.aptana.ide.ui.io.internal;

import java.net.URI;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.ide.FileStoreEditorInput;

import com.aptana.ide.core.io.vfs.IExtendedFileStore;
import com.aptana.ide.ui.io.preferences.FTPPreferenceUtil;

public class UniformFileStoreEditorInput extends FileStoreEditorInput
{

	private IFileStore fLocalFileStore;
	private IFileStore fRealFileStore;
	private IFileInfo fRealFileInfo;

	/**
	 * @param localFileStore
	 *            the local cached copy of the real file store; if the real file is already local, the instance will be
	 *            the same as what realFileStore represents
	 * @param realFileStore
	 *            the real file store
	 * @param realFileInfo
	 *            the real file info
	 */
	public UniformFileStoreEditorInput(IFileStore localFileStore, IFileStore realFileStore, IFileInfo realFileInfo)
	{
		super(localFileStore);
		fLocalFileStore = localFileStore;
		fRealFileStore = realFileStore;
		fRealFileInfo = realFileInfo;
	}

	@Override
	public String getName()
	{
		return fRealFileStore.getName();
	}

	@Override
	public String getToolTipText()
	{
		return fRealFileStore.toString();
	}

	public IFileStore getLocalFileStore()
	{
		return fLocalFileStore;
	}

	public IFileStore getFileStore()
	{
		return fRealFileStore;
	}

	public IFileInfo getFileInfo()
	{
		return fRealFileInfo;
	}

	public boolean isRemote()
	{
		return !fLocalFileStore.equals(fRealFileStore);
	}

	public void setFileInfo(IFileInfo fileInfo)
	{
		fRealFileInfo = fileInfo;
	}

	@Override
	public IPersistableElement getPersistable()
	{
		boolean reopen = FTPPreferenceUtil.getReopenRemoteOnStartup();
		return reopen ? this : null;
	}

	@Override
	public int hashCode()
	{
		return fRealFileStore.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof UniformFileStoreEditorInput)
		{
			return fRealFileStore.equals(((UniformFileStoreEditorInput) o).fRealFileStore);
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter)
	{
		if (IFileStore.class == adapter)
		{
			return fRealFileStore;
		}
		else if (IFileInfo.class == adapter)
		{
			return fRealFileInfo;
		}
		else if (URI.class == adapter)
		{
			if (fRealFileStore instanceof IExtendedFileStore)
			{
				return ((IExtendedFileStore) fRealFileStore).toCanonicalURI();
			}
		}
		return super.getAdapter(adapter);
	}

	@Override
	public String getFactoryId()
	{
		return UniformFileStoreEditorInputFactory.ID;
	}

	@Override
	public void saveState(IMemento memento)
	{
		UniformFileStoreEditorInputFactory.saveState(memento, this);
	}
}
