/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.aptana.core.util.StringUtil;
import com.aptana.ide.syncing.core.ISiteConnection;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class ConnectionEditorInput implements IEditorInput
{

	private ISiteConnection fSite;

	public ConnectionEditorInput(ISiteConnection site)
	{
		fSite = site;
	}

	public ISiteConnection getConnection()
	{
		return fSite;
	}

	public void setConnection(ISiteConnection site)
	{
		fSite = site;
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
		return (fSite == null) ? StringUtil.EMPTY : fSite.getName();
	}

	public IPersistableElement getPersistable()
	{
		return null;
	}

	public String getToolTipText()
	{
		return (fSite == null) ? StringUtil.EMPTY : fSite.toString();
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter)
	{
		return null;
	}

	@Override
	public int hashCode()
	{
		return fSite.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof ConnectionEditorInput))
		{
			return false;
		}
		ConnectionEditorInput other = (ConnectionEditorInput) obj;
		return fSite == other.getConnection();
	}
}
