/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.ftp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.deploy.IDeployProvider;
import com.aptana.deploy.util.DeployProviderUtil;
import com.aptana.filesystem.ftp.FTPConnectionPoint;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.SiteConnectionUtils;
import com.aptana.ide.syncing.core.SyncingPlugin;
import com.aptana.ide.syncing.ui.internal.SyncUtils;

public class FTPDeployProviderTest
{

	private IProject testProject;
	private IConnectionPoint sourceConnectionPoint;
	private IConnectionPoint destinationConnectionPoint;
	private ISiteConnection siteConnection;

	@Before
	public void setUp() throws Exception
	{
		testProject = createProject();
		sourceConnectionPoint = SyncUtils.findOrCreateConnectionPointFor(testProject);
		CoreIOPlugin.getConnectionPointManager().addConnectionPoint(sourceConnectionPoint);
		destinationConnectionPoint = new FTPConnectionPoint();
		((FTPConnectionPoint) destinationConnectionPoint).setName("FTP");
		CoreIOPlugin.getConnectionPointManager().addConnectionPoint(destinationConnectionPoint);
		siteConnection = SiteConnectionUtils.createSite(
				MessageFormat.format("{0} <-> {1}", testProject.getName(), destinationConnectionPoint.getName()),
				sourceConnectionPoint, destinationConnectionPoint);
		SyncingPlugin.getSiteConnectionManager().addSiteConnection(siteConnection);
	}

	@After
	public void tearDown() throws Exception
	{
		SyncingPlugin.getSiteConnectionManager().removeSiteConnection(siteConnection);
		CoreIOPlugin.getConnectionPointManager().removeConnectionPoint(sourceConnectionPoint);
		CoreIOPlugin.getConnectionPointManager().removeConnectionPoint(destinationConnectionPoint);
		deleteProject(testProject);
	}

	@Test
	public void testHandleDeploy()
	{
		IDeployProvider provider = DeployProviderUtil.getDeployProvider(testProject);
		assertNotNull(provider);
		assertEquals(FTPDeployProvider.class, provider.getClass());
		assertEquals(FTPDeployProvider.ID, DeployProviderUtil.getIdForProvider(provider));
		assertTrue(provider.handles(testProject));
	}

	/**
	 * Creates a project for testing
	 * 
	 * @return
	 * @throws IOException
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 * @throws CoreException
	 */
	private IProject createProject() throws IOException, InvocationTargetException, InterruptedException, CoreException
	{

		File baseTempFile = File.createTempFile("test", ".txt"); //$NON-NLS-1$ //$NON-NLS-2$
		String projectName = "FTPDeployProviderTest" + System.currentTimeMillis();
		File projectFolder = new File(baseTempFile.getParentFile(), projectName);
		projectFolder.mkdirs();

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProjectDescription description = workspace.newProjectDescription(projectName);
		description.setLocation(new Path(projectFolder.getAbsolutePath()));

		IProject project = workspace.getRoot().getProject(projectName);
		project.create(description, null);
		project.open(null);

		return project;
	}

	/**
	 * Deletes the project used for testing.
	 * 
	 * @param project
	 * @throws CoreException
	 */
	private void deleteProject(IProject project) throws CoreException
	{
		project.delete(true, null);
	}
}
