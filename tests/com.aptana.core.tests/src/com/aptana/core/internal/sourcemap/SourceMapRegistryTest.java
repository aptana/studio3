package com.aptana.core.internal.sourcemap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Status;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.sourcemap.ISourceMap;

public class SourceMapRegistryTest
{

	private SourceMapRegistry reg;
	private Mockery context;
	private IConfigurationElement element;
	private IProject project;

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
		element = context.mock(IConfigurationElement.class);
		reg = new SourceMapRegistry()
		{
			@Override
			protected synchronized void lazyLoad()
			{
				sourceMappers = new HashMap<String, IConfigurationElement>();
				sourceMappers.put("example.nature", element);
			}
		};
	}

	@After
	public void tearDown() throws Exception
	{
		reg = null;
		project = null;
		element = null;
		context = null;
	}

	@Test
	public void testGetSourceMap() throws CoreException
	{
		final IProjectDescription description = context.mock(IProjectDescription.class);
		final ISourceMap sourceMap = context.mock(ISourceMap.class);
		final String platform = "android";
		context.checking(new Expectations()
		{
			{
				oneOf(project).isAccessible();
				will(returnValue(true));

				oneOf(project).getDescription();
				will(returnValue(description));

				oneOf(description).getNatureIds();
				will(returnValue(new String[] { "example.nature" }));

				oneOf(element).createExecutableExtension("class");
				will(returnValue(sourceMap));

				oneOf(sourceMap).setInitializationData(element, platform, project);
			}
		});
		ISourceMap map = reg.getSourceMap(project, platform);
		assertNotNull(map);
		context.assertIsSatisfied();
	}

	@Test
	public void testGetSourceMapWithInaccesibleProject() throws CoreException
	{
		final String platform = "android";
		context.checking(new Expectations()
		{
			{
				oneOf(project).isAccessible();
				will(returnValue(false));

				never(project).getDescription();
			}
		});
		ISourceMap map = reg.getSourceMap(project, platform);
		assertNull(map);
		context.assertIsSatisfied();
	}

	@Test
	public void testGetSourceMapWhereGetNatureIdsThrowsException() throws CoreException
	{
		final String platform = "android";
		context.checking(new Expectations()
		{
			{
				oneOf(project).isAccessible();
				will(returnValue(true));

				oneOf(project).getDescription();
				will(throwException(new CoreException(Status.CANCEL_STATUS)));

				never(element).createExecutableExtension("class");
			}
		});
		ISourceMap map = reg.getSourceMap(project, platform);
		assertNull(map);
		context.assertIsSatisfied();
	}

	@Test
	public void testGetSourceMapWithProjectHavingNoNatures() throws CoreException
	{
		final IProjectDescription description = context.mock(IProjectDescription.class);
		final String platform = "android";
		context.checking(new Expectations()
		{
			{
				oneOf(project).isAccessible();
				will(returnValue(true));

				oneOf(project).getDescription();
				will(returnValue(description));

				oneOf(description).getNatureIds();
				will(returnValue(new String[] {}));

				never(element).createExecutableExtension("class");
			}
		});
		ISourceMap map = reg.getSourceMap(project, platform);
		assertNull(map);
		context.assertIsSatisfied();
	}

	@Test
	public void testGetSourceMapWithNatureHavingNoSourceMappers() throws CoreException
	{
		final IProjectDescription description = context.mock(IProjectDescription.class);
		final String platform = "android";
		context.checking(new Expectations()
		{
			{
				oneOf(project).isAccessible();
				will(returnValue(true));

				oneOf(project).getDescription();
				will(returnValue(description));

				oneOf(description).getNatureIds();
				will(returnValue(new String[] { "some.other.nature" }));

				never(element).createExecutableExtension("class");
			}
		});
		ISourceMap map = reg.getSourceMap(project, platform);
		assertNull(map);
		context.assertIsSatisfied();
	}

	@Test
	public void testGetSourceMapWhereSourceMapperClassFailsToInitialize() throws CoreException
	{
		final IProjectDescription description = context.mock(IProjectDescription.class);
		final ISourceMap sourceMap = context.mock(ISourceMap.class);
		final String platform = "android";
		context.checking(new Expectations()
		{
			{
				oneOf(project).isAccessible();
				will(returnValue(true));

				oneOf(project).getDescription();
				will(returnValue(description));

				oneOf(description).getNatureIds();
				will(returnValue(new String[] { "example.nature" }));

				oneOf(element).createExecutableExtension("class");
				will(throwException(new CoreException(Status.CANCEL_STATUS)));

				never(sourceMap).setInitializationData(element, platform, project);
			}
		});
		ISourceMap map = reg.getSourceMap(project, platform);
		assertNull(map);
		context.assertIsSatisfied();
	}
}
