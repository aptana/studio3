/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

import org.eclipse.core.filebuffers.IAnnotationModelFactory;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.texteditor.ResourceMarkerAnnotationModel;

public class ExternalFileAnnotationModelFactory implements IAnnotationModelFactory
{

	public IAnnotationModel createAnnotationModel(IPath location)
	{
		try
		{
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(location);
			if (file != null && file.exists())
			{
				return new ResourceMarkerAnnotationModel(file);
			}
		}
		catch (Exception e)
		{
		}
		return new ExternalFileAnnotationModel(EFS.getLocalFileSystem().getStore(location));
	}
}
