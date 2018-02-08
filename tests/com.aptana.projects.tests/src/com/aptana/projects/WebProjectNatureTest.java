package com.aptana.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.build.UnifiedBuilder;
import com.aptana.core.util.FileUtil;
import com.aptana.core.util.ResourceUtil;

public class WebProjectNatureTest
{

	private IProject testProject;

	@Before
	public void setUp() throws Exception
	{
		testProject = createProject();
	}

	@After
	public void tearDown() throws Exception
	{
		deleteProject(testProject);
	}

	// TODO Use our common utils for creating test projects....
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
		String projectName = "ResourceUtilTest" + System.currentTimeMillis();
		File projectFolder = FileUtil.getTempDirectory().append(projectName).toFile();
		projectFolder.mkdirs();

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProjectDescription description = workspace.newProjectDescription(projectName);
		description.setLocation(new Path(projectFolder.getAbsolutePath()));

		IProject project = workspace.getRoot().getProject(projectName);
		project.create(description, null);
		project.open(null);
		ResourceUtil.isAccessible(project);

		return project;
	}

	/**
	 * Deletes a project used for testing
	 * 
	 * @param project
	 * @throws CoreException
	 */
	private void deleteProject(IProject project) throws CoreException
	{
		project.delete(true, null);
	}

	@Test
	public void testBuilders() throws CoreException
	{
		// record original state of project
		IProjectDescription desc = testProject.getDescription();
		int numCommands = desc.getBuildSpec().length;

		// Add web nature
		ResourceUtil.addNature(testProject, WebProjectNature.ID);

		// Verify it added our unified builder as a side-effect
		desc = testProject.getDescription();
		boolean foundBuilder = false;
		ICommand[] commands = desc.getBuildSpec();
		for (ICommand command : commands)
		{
			if (UnifiedBuilder.ID.equals(command.getBuilderName()))
			{
				foundBuilder = true;
				break;
			}
		}

		// That there is the builder in question;
		assertTrue(foundBuilder);

		// try to remove the builder. It should not
		assertFalse(ResourceUtil.removeBuilderIfOrphaned(testProject, UnifiedBuilder.ID));

		// remove a nature. Internally will remove builder if orphaned
		ResourceUtil.removeNature(testProject, WebProjectNature.ID);

		// back to original builder length
		assertEquals(numCommands, testProject.getDescription().getBuildSpec().length);
	}

	@Test
	public void testIsAptanaNature()
	{
		assertTrue(ResourceUtil.isAptanaNature(WebProjectNature.ID));
	}

}
