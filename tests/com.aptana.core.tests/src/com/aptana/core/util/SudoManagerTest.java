/**
 * Appcelerator Titanium Studio
 * Copyright (c) 2014 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

package com.aptana.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SudoManagerTest
{

	private SudoManager sudoManager;
	private Mockery context;
	private IProcessRunner processRunner;

	@Before
	public void setUp()
	{
		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
		processRunner = context.mock(IProcessRunner.class);
		sudoManager = new SudoManager()
		{
			@Override
			protected IProcessRunner getProcessRunner()
			{
				return processRunner;
			}

			@Override
			protected IStatus getResult(ProcessRunnable runnable) throws InterruptedException
			{
				return Status.OK_STATUS;
			}
		};
	}

	@After
	public void tearDown()
	{
		sudoManager = null;
	}

	@Test
	public void testAuthenticate() throws Exception
	{
		final Process process = context.mock(Process.class);
		context.checking(new Expectations()
		{
			{
				oneOf(processRunner).run(with(any(Map.class)),
						with(new String[] { "sudo", "-k", "-S", "--", "echo", "SUCCESS" }));
				will(returnValue(process));
			}
		});
		assertEquals(true, sudoManager.authenticate("fake".toCharArray()));
	}

	@Test
	public void testSudoArguments()
	{
		List<String> arguments = sudoManager.getArguments("testpassword".toCharArray());
		if (PlatformUtil.isWindows())
		{
			assertEquals(arguments, Collections.emptyList());
		}
		else
		{
			assertEquals(5, arguments.size());
			assertTrue(arguments.contains(SudoManager.PROMPT_MSG));
		}
	}

	@Test
	public void testEmptyPwdSudoArguments()
	{
		List<String> arguments = sudoManager.getArguments("".toCharArray());
		if (PlatformUtil.isWindows())
		{
			assertEquals(arguments, Collections.emptyList());
		}
		else
		{
			assertEquals(3, arguments.size());
		}
	}

}
