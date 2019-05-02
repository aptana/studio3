/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.editor.epl.tests;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.common.CommonEditorPlugin;

public class ResourceTestHelper
{

	public enum IfExists
	{
		FAIL, OVERWRITE, SKIP
	}

	private static final int DELETE_MAX_RETRY = 5;
	private static final long DELETE_RETRY_DELAY = 1000;

	public static void replicate(IPath srcFilePath, String destPrefix, String destSuffix, int copies, IfExists ifExists)
			throws CoreException
	{
		IdeLog.logError(CommonEditorPlugin.getDefault(),
				MessageFormat.format("Copying {0} {1} times", srcFilePath.toPortableString(), copies));
		for (int i = 0; i < copies; i++)
		{
			copy(srcFilePath, Path.fromPortableString(destPrefix + i + destSuffix), ifExists);
		}
	}

	public static void copy(IPath srcFilePath, IPath destFilePath, IfExists ifExists) throws CoreException
	{
		if (handleExisting(destFilePath, ifExists))
		{
			IFile src = findFile(srcFilePath);
			src.copy(destFilePath, true, null);
			Assert.assertTrue(MessageFormat.format("src file {0} didn'get copied to destination: {1}",
					srcFilePath.toPortableString(), destFilePath.toPortableString()), getFile(destFilePath).exists());
			System.err.println(MessageFormat.format("Copied {0} to {1}", srcFilePath.toPortableString(),
					destFilePath.toPortableString()));
		}
		else
		{
			System.err.println(MessageFormat.format("Skipping copy of {0} to {1}, since dest already exists",
					srcFilePath.toPortableString(), destFilePath.toPortableString()));
		}
	}

	private static boolean handleExisting(IPath dest, IfExists ifExists) throws CoreException
	{
		IFile destFile = getFile(dest);
		switch (ifExists)
		{
			case FAIL:
				if (destFile.exists())
				{
					throw new IllegalArgumentException("Destination file exists: " + dest);
				}
				return true;
			case OVERWRITE:
				if (destFile.exists())
				{
					delete(destFile);
				}
				return true;
			case SKIP:
				if (destFile.exists())
				{
					return false;
				}
				return true;
			default:
				throw new IllegalArgumentException();
		}
	}

	private static IFile getFile(IPath path)
	{
		return getRoot().getFile(path);
	}

	public static void delete(IPath file) throws CoreException
	{
		delete(getFile(file));
	}

	private static void delete(IFile file) throws CoreException
	{
		CoreException x = null;
		for (int i = 0; i < DELETE_MAX_RETRY; i++)
		{
			try
			{
				file.delete(true, null);
				return;
			}
			catch (CoreException x0)
			{
				x = x0;
				try
				{
					Thread.sleep(DELETE_RETRY_DELAY);
				}
				catch (InterruptedException x1)
				{
					// should not happen
				}
			}
		}
		throw x;
	}

	/**
	 * Deletes replicated files "prefix + i + suffix" where i = 0; i < n; i++
	 * 
	 * @param prefix
	 * @param suffix
	 * @param n
	 * @throws CoreException
	 */
	public static void delete(String prefix, String suffix, int n) throws CoreException
	{
		for (int i = 0; i < n; i++)
		{
			delete(Path.fromPortableString(prefix + i + suffix));
		}
	}

	public static IFile findFile(IPath pathStr)
	{
		IFile file = getFile(pathStr);
		Assert.assertTrue(MessageFormat.format("Unable to find file: {0}", pathStr), file != null && file.exists());
		return file;
	}

	/**
	 * Finds replicated files named "prefix + j + suffix" where j = startNumber; j < startNumber + length; j++
	 * 
	 * @param prefix
	 * @param suffix
	 * @param startNumber
	 * @param length
	 * @return
	 */
	public static IFile[] findFiles(String prefix, String suffix, int startNumber, int length)
	{
		List<IFile> files = new ArrayList<IFile>(length);
		for (int j = startNumber; j < startNumber + length; j++)
		{
			IPath path = Path.fromPortableString(prefix + j + suffix);
			files.add(findFile(path));
		}
		return files.toArray(new IFile[files.size()]);
	}

	private static IWorkspaceRoot getRoot()
	{
		return getWorkspace().getRoot();
	}

	public static void incrementalBuild() throws CoreException
	{
		getWorkspace().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
	}

	public static void fullBuild() throws CoreException
	{
		getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, null);
	}

	public static IProject createExistingProject(String projectName) throws CoreException
	{
		IProject project = getProject(projectName);
		IProjectDescription description = getWorkspace().newProjectDescription(projectName);
		description.setLocation(null);

		project.create(description, null);
		project.open(null);
		return project;
	}

	public static IProject getProject(String projectName)
	{
		return getWorkspace().getRoot().getProject(projectName);
	}

	private static IWorkspace getWorkspace()
	{
		return ResourcesPlugin.getWorkspace();
	}

	public static boolean projectExists(String projectName)
	{
		return getProject(projectName).exists();
	}
}