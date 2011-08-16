/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.internal;

import java.net.URI;
import java.text.MessageFormat;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.ide.FileStoreEditorInput;

import com.aptana.core.io.vfs.IExtendedFileStore;
import com.aptana.ide.ui.io.IUniformFileStoreEditorInput;
import com.aptana.ide.ui.io.preferences.RemotePreferenceUtil;

public class UniformFileStoreEditorInput extends FileStoreEditorInput implements IUniformFileStoreEditorInput
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
		String name = fRealFileStore.getName();
		if (fRealFileStore instanceof IExtendedFileStore)
		{
			name = MessageFormat.format("({0}) {1}", //$NON-NLS-1$
					((IExtendedFileStore) fRealFileStore).toCanonicalURI().getScheme(), name);
		}
		return name;
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
		boolean reopen = RemotePreferenceUtil.getReopenRemoteOnStartup();
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
		if (this == o)
		{
			return true;
		}
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
		if (IFileStore.class.equals(adapter))
		{
			return fRealFileStore;
		}
		else if (IFileInfo.class.equals(adapter))
		{
			return fRealFileInfo;
		}
		else if (URI.class.equals(adapter))
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
