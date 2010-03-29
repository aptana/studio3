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
public abstract class Utilities
{

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
		IPath path = stateLocation.append("/_" + new Object().hashCode()); //$NON-NLS-1$ 
		return new File(path.toOSString());
	}

}
