/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ui.internal;

import java.net.URI;

import org.eclipse.ui.ide.FileStoreEditorInput;

import com.aptana.core.resources.AbstractUniformResource;

/**
 * @author Max Stepanov
 */
/* package */final class FileStoreEditorInputUniformResource extends AbstractUniformResource
{

	private final FileStoreEditorInput editorInput;

	FileStoreEditorInputUniformResource(FileStoreEditorInput editorInput)
	{
		super();
		this.editorInput = editorInput;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.resources.IUniformResource#getURI()
	 */
	public URI getURI()
	{
		return editorInput.getURI();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.PlatformObject#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter)
	{
		Object object = super.getAdapter(adapter);
		if (object == null)
		{
			object = editorInput.getAdapter(adapter);
		}
		return object;
	}

}