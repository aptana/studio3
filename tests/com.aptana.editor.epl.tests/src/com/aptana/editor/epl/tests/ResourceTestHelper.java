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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
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
import org.eclipse.core.runtime.Path;

import com.aptana.core.util.IOUtil;


public class ResourceTestHelper {

	public static final int FAIL_IF_EXISTS= 0;

	public static final int OVERWRITE_IF_EXISTS= 1;

	public static final int SKIP_IF_EXISTS= 2;

	private static final int DELETE_MAX_RETRY= 5;

	private static final long DELETE_RETRY_DELAY= 1000;

	public static void replicate(String src, String destPrefix, String destSuffix, int n, int ifExists) throws CoreException {
		for (int i= 0; i < n; i++)
			copy(src, destPrefix + i + destSuffix, ifExists);
	}

	public static void copy(String src, String dest) throws CoreException {
		copy(src, dest, FAIL_IF_EXISTS);
	}

	public static void copy(String src, String dest, int ifExists) throws CoreException {
		if (handleExisting(dest, ifExists))
			getFile(src).copy(new Path(dest), true, null);
	}

	private static boolean handleExisting(String dest, int ifExists) throws CoreException {
		IFile destFile= getFile(dest);
		switch (ifExists) {
			case FAIL_IF_EXISTS:
				if (destFile.exists())
					throw new IllegalArgumentException("Destination file exists: " + dest);
				return true;
			case OVERWRITE_IF_EXISTS:
				if (destFile.exists())
					delete(destFile);
				return true;
			case SKIP_IF_EXISTS:
				if (destFile.exists())
					return false;
				return true;
			default:
				throw new IllegalArgumentException();
		}
	}

	private static IFile getFile(String path) {
		return getRoot().getFile(new Path(path));
	}

	public static void delete(String file) throws CoreException {
		delete(getFile(file));
	}

	private static void delete(IFile file) throws CoreException {
		CoreException x= null;
		for (int i= 0; i < DELETE_MAX_RETRY; i++) {
			try {
				file.delete(true, null);
				return;
			} catch (CoreException x0) {
				x= x0;
				try {
					Thread.sleep(DELETE_RETRY_DELAY);
				} catch (InterruptedException x1) {
					// should not happen
				}
			}
		}
		throw x;
	}

	/**
	 * Deletes replicated files "prefix + i + suffix" where i = 0; i < n; i++
	 * @param prefix
	 * @param suffix
	 * @param n
	 * @throws CoreException
	 */
	public static void delete(String prefix, String suffix, int n) throws CoreException {
		for (int i= 0; i < n; i++)
			delete(prefix + i + suffix);
	}

	public static IFile findFile(String pathStr) {
		IFile file= getFile(pathStr);
		Assert.assertTrue(file != null && file.exists());
		return file;
	}

	/**
	 * Finds replicated files named "prefix + j + suffix" where j = startNumber; j < startNumber + length; j++
	 * @param prefix
	 * @param suffix
	 * @param startNumber
	 * @param length
	 * @return
	 */
	public static IFile[] findFiles(String prefix, String suffix, int startNumber, int length) {
		List<IFile> files= new ArrayList<IFile>(length);
		for (int j= startNumber; j < startNumber + length; j++) {
			String path= prefix + j + suffix;
			files.add(findFile(path));
		}
		return files.toArray(new IFile[files.size()]);
	}

	public static StringBuffer read(String src) throws IOException, CoreException {
		return new StringBuffer(IOUtil.read(getFile(src).getContents()));
	}

	public static void write(String dest, final String content) throws CoreException {
		InputStream stream= new InputStream() {
			private Reader fReader= new StringReader(content);
			public int read() throws IOException {
				return fReader.read();
			}
		};
		getFile(dest).create(stream, true, null);
	}


	public static void replicate(String src, String destPrefix, String destSuffix, int n, String srcName, String destNamePrefix, int ifExists) throws IOException, CoreException {
		StringBuffer s= read(src);
		for (int j= 0; j < n; j++) {
			String dest= destPrefix + j + destSuffix;
			if (handleExisting(dest, ifExists)) {
				StringBuffer c= new StringBuffer(s.toString());
				write(dest, c.toString());
			}
		}
	}

	public static void copy(String src, String dest, String srcName, String destName, int ifExists) throws IOException, CoreException {
		if (handleExisting(dest, ifExists)) {
			StringBuffer buf= read(src);
			write(dest, buf.toString());
		}
	}

	private static IWorkspaceRoot getRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	public static void incrementalBuild() throws CoreException {
		ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
	}

	public static void fullBuild() throws CoreException {
		ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, null);
	}

	public static IProject createExistingProject(String projectName) throws CoreException {
		IProject project= getProject(projectName);
		IProjectDescription description= ResourcesPlugin.getWorkspace().newProjectDescription(projectName);
		description.setLocation(null);

		project.create(description, null);
		project.open(null);
		return project;
	}

	public static IProject getProject(String projectName) {
		IWorkspace workspace= ResourcesPlugin.getWorkspace();
		return workspace.getRoot().getProject(projectName);
	}

	public static boolean projectExists(String projectName) {
		return getProject(projectName).exists();
	}
}