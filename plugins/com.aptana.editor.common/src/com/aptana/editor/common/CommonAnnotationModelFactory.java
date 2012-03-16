/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

import org.eclipse.core.filebuffers.IAnnotationModelFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;

/**
 * Attaches the {@link CommonAnnotationModel} to the editor for IFiles.
 * 
 * @author cwilliams
 */
public class CommonAnnotationModelFactory implements IAnnotationModelFactory
{

	public IAnnotationModel createAnnotationModel(IPath path)
	{
		IResource file = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		if (file instanceof IFile)
		{
			return new CommonAnnotationModel(file);
		}
		return new AnnotationModel();
	}

}
