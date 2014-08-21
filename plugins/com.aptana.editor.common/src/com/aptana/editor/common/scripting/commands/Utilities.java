/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.scripting.commands;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;

import com.aptana.editor.common.CommonEditorPlugin;

/**
 * This implements some utility methods.
 * 
 * @author Sandip V. Chitale
 */
public final class Utilities
{

	private Utilities()
	{
	}

	/**
	 * Creates a new file editor input.
	 * 
	 * @param file
	 * @param fileName
	 * @return IEditorInput
	 */
	public static IEditorInput createFileEditorInput(File file, final String name)
	{
		IEditorInput input = null;
		IFileSystem fs = EFS.getLocalFileSystem();
		IFileStore localFile = fs.fromLocalFile(file);
		input = new FileStoreEditorInput(localFile)
		{
			@Override
			public String getName()
			{
				return name;
			}
		};
		return input;
	}

	public static File getFile()
	{
		IPath stateLocation = CommonEditorPlugin.getDefault().getStateLocation();
		long unique = new Object().hashCode();
		IPath path = stateLocation.append("_" + unique); //$NON-NLS-1$ 
		File file = path.toFile();
		while (file.exists())
		{
			unique++;
			path = stateLocation.append("_" + unique); //$NON-NLS-1$ 
			file = path.toFile();
		}
		return file;
	}

}
