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
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.ui.internal.navigator.NavigatorDecoratingLabelProvider;

import com.aptana.ide.core.io.IBaseRemoteConnectionPoint;
import com.aptana.ide.core.io.IConnectionPoint;

/**
 * A custom label provider for file navigator to provide additional features such as tooltip support.
 */
@SuppressWarnings("restriction")
public class FileNavigatorDecoratingLabelProvider extends NavigatorDecoratingLabelProvider
{

	public FileNavigatorDecoratingLabelProvider(ILabelProvider commonLabelProvider)
	{
		super(commonLabelProvider);
	}

	@Override
	public String getToolTipText(Object element)
	{
		if (element instanceof IBaseRemoteConnectionPoint)
		{
			IPath path = ((IBaseRemoteConnectionPoint) element).getPath();
			if (path.segmentCount() > 0)
			{
				return MessageFormat.format(
						"{0} ({1})", new Object[] { ((IConnectionPoint) element).getName(), path.toPortableString() }); //$NON-NLS-1$
			}
		}
		return element.toString();
	}
}
