/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import com.aptana.core.build.UnifiedBuilder;

public class ResourceUtilTest extends TestCase
{
	private IProject testProject;
	private String WEBNATUREID = "com.aptana.projects.webnature";
	private String RUBYNATUREID = "com.aptana.ruby.core.rubynature";
	private String RAILSNATUREID = "org.radrails.rails.core.railsnature";
	private String PHPNATUREID = "com.aptana.editor.php.phpNature";
	private String JAVANATUREID = "org.eclipse.jdt.core.javanature";

	protected void setUp() throws Exception
	{
		// TODO Auto-generated method stub
		super.setUp();
		testProject = createProject();
	}

	protected void tearDown() throws Exception
	{
		// TODO Auto-generated method stub
		super.tearDown();
		deleteProject(testProject);
	}

	public void testResourcePathToFile()
	{
		//fail("Not yet implemented");
	}

	public void testResourcePathToString()
	{
		//fail("Not yet implemented");
	}

	public void testToURI()
	{
		//fail("Not yet implemented");
	}

	public void testGetLineSeparatorValue()
	{
		//fail("Not yet implemented");
	}

	public void testBuilders() throws CoreException
	{
		IProjectDescription desc = testProject.getDescription();
		int numCommands = desc.getBuildSpec().length;
				
		ResourceUtil.addBuilder(testProject, UnifiedBuilder.ID);

		// get new value
		desc = testProject.getDescription();

		// make sure we added a builder
		assertEquals(numCommands + 1, desc.getBuildSpec().length);

		boolean foundBuilder = false;
		ICommand[] commands = desc.getBuildSpec();
		for (int i = 0; i < commands.length; i++)
		{
			ICommand iCommand = commands[i];
			if (iCommand.getBuilderName().equals(UnifiedBuilder.ID))
			{
				foundBuilder = true;
			}
		}
		
		// That there is the builder in question;
		assertTrue(foundBuilder);

		ResourceUtil.removeBuilder(testProject, UnifiedBuilder.ID);

		// get new value
		desc = testProject.getDescription();

		// make sure we removed a builder
		assertEquals(numCommands, desc.getBuildSpec().length);

		foundBuilder = false;
		commands = desc.getBuildSpec();
		for (int i = 0; i < commands.length; i++)
		{
			ICommand iCommand = commands[i];
			if (iCommand.getBuilderName().equals(UnifiedBuilder.ID))
			{
				foundBuilder = true;
			}
		}
		
		// That there is _not_ the builder in question;
		assertFalse(foundBuilder);

		// re-add builder
		ResourceUtil.addBuilder(testProject, UnifiedBuilder.ID);
		
		desc = testProject.getDescription();
		
		// now add two Aptana natures
		ResourceUtil.addNature(testProject, WEBNATUREID);
		
		// try to remove the builder. It should not
		assertFalse(ResourceUtil.removeBuilderIfOrphaned(testProject, UnifiedBuilder.ID));
		
		// remove a nature. Internally will remove builder if orphaned
		ResourceUtil.removeNature(testProject, WEBNATUREID);
		
		// back to original builder length
		assertEquals(numCommands, testProject.getDescription().getBuildSpec().length);
	}

	public void testIsAptanaNature()
	{
		assertTrue(ResourceUtil.isAptanaNature(WEBNATUREID));
		assertTrue(ResourceUtil.isAptanaNature(RUBYNATUREID));
		assertTrue(ResourceUtil.isAptanaNature(RAILSNATUREID));
		assertTrue(ResourceUtil.isAptanaNature(PHPNATUREID));
		assertFalse(ResourceUtil.isAptanaNature(JAVANATUREID));
	}

	public void testNatures() throws CoreException
	{
		IProjectDescription desc = testProject.getDescription();
		
		int numIds = desc.getNatureIds().length;
		
		ResourceUtil.addNature(desc, WEBNATUREID);
		ResourceUtil.addNature(desc, RUBYNATUREID);
		String[] natures = ResourceUtil.getAptanaNatures(desc);
	
		// make sure we added two ids
		assertEquals(numIds + 2, desc.getNatureIds().length);
		
		// That there are two aptana natures
		assertEquals(2, natures.length);		
		assertEquals(WEBNATUREID, natures[0]);
		assertEquals(RUBYNATUREID, natures[1]);

		ResourceUtil.removeNature(desc, WEBNATUREID);
		ResourceUtil.removeNature(desc, RUBYNATUREID);

		// make sure we removed two ids
		assertEquals(numIds, desc.getNatureIds().length);
		natures = ResourceUtil.getAptanaNatures(desc);		
		assertEquals(0, natures.length);		
	}
	
	/**
	 * Creates a project for testing
	 * @return
	 * @throws IOException
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 * @throws CoreException
	 */
	private IProject createProject() throws IOException, InvocationTargetException, InterruptedException, CoreException {
		
		File baseTempFile = File.createTempFile("test", ".txt"); //$NON-NLS-1$ //$NON-NLS-2$
		String projectName = "ResourceUtilTest" + System.currentTimeMillis();
		File projectFolder = new File(baseTempFile.getParentFile(), projectName);
		projectFolder.mkdirs();
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProjectDescription description = workspace.newProjectDescription(projectName);
		description.setLocation(new Path(projectFolder.getAbsolutePath()));		

		final IProject project = workspace.getRoot().getProject(projectName);
		project.create(description, null);
		project.open(null);
		
		return project;
	}
	
	/**
	 * Deletes a project used for testing
	 * @param project
	 * @throws CoreException
	 */
	private void deleteProject(IProject project) throws CoreException {
		project.delete(true, null);
	}

}
