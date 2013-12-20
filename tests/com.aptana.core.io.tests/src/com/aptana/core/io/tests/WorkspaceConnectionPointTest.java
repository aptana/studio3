/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.core.io.tests;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

import com.aptana.ide.core.io.WorkspaceConnectionPoint;

/**
 * @author Max Stepanov
 */
public class WorkspaceConnectionPointTest extends CommonConnectionTest
{

	private IProject project;

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.io.tests.CommonConnectionTest#setUp()
	 */
	@Override
	public void setUp() throws Exception
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		WorkspaceConnectionPoint wcp = new WorkspaceConnectionPoint();
		wcp.setResource(workspace.getRoot());
		cp = wcp;

		String projectName = WorkspaceConnectionPointTest.class.getSimpleName() + System.currentTimeMillis();
		File projectDir = File.createTempFile(projectName, null);
		assertTrue(projectDir.delete());
		assertTrue(projectDir.mkdirs());
		IProjectDescription description = workspace.newProjectDescription(projectName);
		description.setLocation(Path.fromOSString(projectDir.getAbsolutePath()));
		project = workspace.getRoot().getProject(projectName);
		project.create(description, null);
		project.open(null);
		IFolder folder = project.getFolder(Path.fromPortableString("test")); //$NON-NLS-1$
		folder.create(true, true, null);
		testPath = folder.getFullPath();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.io.tests.CommonConnectionTest#tearDown()
	 */
	@Override
	public void tearDown() throws Exception
	{
		super.tearDown();
		project.delete(true, true, null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.io.tests.CommonConnectionTest#persistentConnection()
	 */
	@Override
	protected boolean persistentConnection()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.io.tests.CommonConnectionTest#supportsSetModificationTime()
	 */
	@Override
	protected boolean supportsSetModificationTime()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.io.tests.CommonConnectionTest#supportsChangePermissions()
	 */
	@Override
	protected boolean supportsChangePermissions()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.io.tests.CommonConnectionTest#supportsChangeGroup()
	 */
	@Override
	protected boolean supportsChangeGroup()
	{
		return false;
	}

}
