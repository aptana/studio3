/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IBaseRemoteConnectionPoint;
import com.aptana.ide.core.io.IConnectionPoint;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class ConnectionPointLabelDecorator implements ILabelDecorator
{

	public void addListener(ILabelProviderListener listener)
	{
	}

	public void dispose()
	{
	}

	public boolean isLabelProperty(Object element, String property)
	{
		return false;
	}

	public void removeListener(ILabelProviderListener listener)
	{
	}

	public Image decorateImage(Image image, Object element)
	{
		return null;
	}

	public String decorateText(String text, Object element)
	{
		if (element instanceof IBaseRemoteConnectionPoint)
		{
			IBaseRemoteConnectionPoint currentConnection = (IBaseRemoteConnectionPoint) element;
			String currentName = currentConnection.getName();
			if (currentName == null)
			{
				return text;
			}
			IPath currentPath = currentConnection.getPath();
			if (Path.ROOT.equals(currentPath))
			{
				return text;
			}

			IConnectionPoint[] connections = CoreIOPlugin.getConnectionPointManager().getConnectionPoints();
			for (IConnectionPoint connection : connections)
			{
				if (!connection.equals(currentConnection) && connection instanceof IBaseRemoteConnectionPoint
						&& currentName.equals(connection.getName()))
				{
					// there are remote connections with the same name, so adds the compressed path to distinguish
					String decoratedText = null;
					IPath path = ((IBaseRemoteConnectionPoint) connection).getPath();
					int count = currentPath.segmentCount();
					for (int i = 0; i < count; ++i)
					{
						// finds the first segment in the path that does not match
						if (!currentPath.segment(i).equals(path.segment(i)))
						{
							decoratedText = currentPath.removeFirstSegments(i).toPortableString();
							if (i > 0)
							{
								decoratedText = ".../" + decoratedText; //$NON-NLS-1$
							}
							break;
						}
					}
					return MessageFormat.format("{0} ({1})", text, //$NON-NLS-1$
							(decoratedText == null) ? currentPath.toPortableString() : decoratedText);
				}
			}
		}
		return text;
	}
}
