/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

import java.net.URI;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IURIEditorInput;

@SuppressWarnings("rawtypes")
public class UntitledFileStorageEditorInput implements IURIEditorInput
{

	private URI uri;
	private String name;

	public UntitledFileStorageEditorInput(URI uri, String name)
	{
		this.uri = uri;
		this.name = name;
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
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	public URI getURI()
	{
		return uri;
	}
}
