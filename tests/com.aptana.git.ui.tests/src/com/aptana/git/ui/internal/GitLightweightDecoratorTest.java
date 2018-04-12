package com.aptana.git.ui.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IDecoration;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.git.core.model.GitRepository;

public class GitLightweightDecoratorTest
{

	private Mockery context;
	private GitRepository repo;
	private IProject project;
	private IDecoration decoration;

	@Before
	public void setUp() throws Exception
	{
		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
		repo = context.mock(GitRepository.class);
		project = context.mock(IProject.class);
		decoration = context.mock(IDecoration.class);
	}

	@After
	public void tearDown() throws Exception
	{
		repo = null;
		project = null;
		decoration = null;
		context = null;
	}

	@Test
	public void testAPSTUD3496() throws Exception
	{
		context.checking(new Expectations()
		{
			{
				oneOf(project).getType();
				will(returnValue(IResource.PROJECT));

				oneOf(project).exists();
				will(returnValue(true));

				oneOf(project).getType();
				will(returnValue(IResource.PROJECT));

				oneOf(repo).currentBranch();
				will(returnValue("master"));

				oneOf(repo).commitsAhead("master");
				will(returnValue(new String[] { "abcdef" }));

				oneOf(decoration).addSuffix(" [master+1]");

				oneOf(repo).resourceOrChildHasChanges(project);
				will(returnValue(true));

				oneOf(decoration).addPrefix("* ");
			}
		});
		GitLightweightDecorator decorator = new GitLightweightDecorator()
		{
			@Override
			protected GitRepository getRepo(IResource resource)
			{
				return repo;
			}
		};
		// Call dispose
		decorator.dispose();
		// Then call for decoration...
		decorator.decorate(project, decoration);
		context.assertIsSatisfied();
	}

}
