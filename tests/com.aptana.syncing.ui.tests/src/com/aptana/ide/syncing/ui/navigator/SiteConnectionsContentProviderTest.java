/**
 * Appcelerator Titanium Studio
 * Copyright (c) 2014 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

package com.aptana.ide.syncing.ui.navigator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SiteConnectionsContentProviderTest
{

	private Mockery context;
	private IProject project;
	private SiteConnectionsContentProvider provider;
	private IProjectDescription description;

	@Before
	public void setUp() throws CoreException
	{
		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
		project = context.mock(IProject.class);
		description = context.mock(IProjectDescription.class);
		provider = new SiteConnectionsContentProvider();
		context.checking(new Expectations()
		{
			{
				allowing(project).isAccessible();
				will(returnValue(true));

				allowing(project).getDescription();
				will(returnValue(description));

			}
		});
	}

	@Test
	public void testPhpProjectConnections()
	{
		context.checking(new Expectations()
		{
			{
				allowing(description).getNatureIds();
				will(returnValue(new String[] { "com.aptana.editor.php.phpNature", "com.java.nature" }));
			}
		});
		Object[] children = provider.getChildren(project);

		assertEquals(1, children.length);
		assertTrue(children[0] instanceof ProjectSiteConnections);

		context.assertIsSatisfied();
	}

	@Test
	public void testJavaProjectConnections()
	{
		context.checking(new Expectations()
		{
			{
				allowing(description).getNatureIds();
				will(returnValue(new String[] { "com.java.nature", "com.aptana.editor.php.phpNature" }));
			}
		});
		Object[] children = provider.getChildren(project);

		assertEquals(0, children.length);

		context.assertIsSatisfied();
	}

	@Test
	public void testWebProjectConnections()
	{
		context.checking(new Expectations()
		{
			{
				allowing(description).getNatureIds();
				will(returnValue(new String[] { "com.aptana.projects.webnature", "com.aptana.editor.php.phpNature",
						"com.java.nature" }));
			}
		});
		Object[] children = provider.getChildren(project);

		assertEquals(1, children.length);
		assertTrue(children[0] instanceof ProjectSiteConnections);

		context.assertIsSatisfied();
	}

	@After
	public void tearDown()
	{
		context = null;
	}
}
