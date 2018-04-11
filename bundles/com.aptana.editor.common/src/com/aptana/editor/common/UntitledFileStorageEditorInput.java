/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

import java.io.InputStream;
import java.net.URI;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.editors.text.ILocationProvider;
import org.eclipse.ui.editors.text.ILocationProviderExtension;

@SuppressWarnings("rawtypes")
public class UntitledFileStorageEditorInput implements IStorageEditorInput, ILocationProvider,
		ILocationProviderExtension, IURIEditorInput
{

	private URI uri;
	private String name;
	private InputStream input;

	public UntitledFileStorageEditorInput(URI uri, String name, InputStream input)
	{
		this.uri = uri;
		this.name = name;
		this.input = input;
	}

	public boolean exists()
	{
		return false;
	}

	public ImageDescriptor getImageDescriptor()
	{
		return null;
	}

	public String getName()
	{
		return name;
	}

	public IPersistableElement getPersistable()
	{
		return null;
	}

	public String getToolTipText()
	{
		return name;
	}

	public Object getAdapter(Class adapter)
	{
		if (adapter == ILocationProvider.class)
		{
			return this;
		}
		return null;
	}

	public IStorage getStorage() throws CoreException
	{
		return new IStorage()
		{

			public Object getAdapter(Class adapter)
			{
				return null;
			}

			public InputStream getContents() throws CoreException
			{
				return input;
			}

			public IPath getFullPath()
			{
				return URIUtil.toPath(uri);
			}

			public String getName()
			{
				return UntitledFileStorageEditorInput.this.getName();
			}

			public boolean isReadOnly()
			{
				return false;
			}
		};
	}

	public URI getURI(Object element)
	{
		return uri;
	}

	public IPath getPath(Object element)
	{
		return URIUtil.toPath(uri);
	}

	public URI getURI()
	{
		return uri;
	}
}
