package com.aptana.git.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.git.core.model.GitRepository;

/**
 * @author kkolipaka
 */
public class GitRepositoryProviderTest
{
	private Mockery context;
	private IResource resource;
	private IProject project;
	private GitRepositoryProviderType repoProviderType;

	@Before
	public void setUp() throws Exception
	{
		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
		project = context.mock(IProject.class);
		resource = context.mock(IResource.class);
		repoProviderType = new GitRepositoryProviderType();

	}

	@After
	public void tearDown() throws Exception
	{
		context = null;
		project = null;
		repoProviderType = null;
		resource = null;
	}

	@Test
	public void testGitDirExists()
	{
		context.checking(new Expectations()
		{
			{
				oneOf(project).findMember(GitRepository.GIT_DIR);
				will(returnValue(resource));

				oneOf(resource).exists();
				will(returnValue(true));
			}
		});
		assertTrue(repoProviderType.hasGitDir(project));
	}

	@Test
	public void testGitDirNotExist()
	{
		context.checking(new Expectations()
		{
			{
				oneOf(project).findMember(GitRepository.GIT_DIR);
				will(returnValue(resource));

				oneOf(resource).exists();
				will(returnValue(false));
			}
		});
		assertFalse(repoProviderType.hasGitDir(project));
	}

}
