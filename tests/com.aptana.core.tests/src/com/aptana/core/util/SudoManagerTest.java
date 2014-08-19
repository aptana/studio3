/**
 * Appcelerator Titanium Studio
 * Copyright (c) 2014 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

package com.aptana.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SudoManagerTest
{

	private SudoManager sudoManager;
	private Mockery context;
	private IProcessRunner processRunner;
	private Process process;
	private ByteArrayOutputStream out;

	@Before
	public void setUp()
	{
		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
		context.setThreadingPolicy(new Synchroniser());
		processRunner = context.mock(IProcessRunner.class);
		process = context.mock(Process.class);
		out = new ByteArrayOutputStream();
		sudoManager = new SudoManager()
		{
			@Override
			protected IProcessRunner getProcessRunner()
			{
				return processRunner;
			}
		};
		context.checking(new Expectations()
		{
			{
				atMost(1).of(process).destroy();
			}
		});
	}

	@After
	public void tearDown()
	{
		sudoManager = null;
		process = null;
		out = null;
		processRunner = null;
	}

	@Test
	public void testAuthenticate() throws Exception
	{
		context.checking(new Expectations()
		{
			{
				oneOf(processRunner).run(with(any(Map.class)),
						with(new String[] { "sudo", "-k", "-S", "-p", "password:", "--", "echo", "SUCCESS" }));
				will(returnValue(process));

				oneOf(process).getInputStream();
				will(returnValue(new ByteArrayInputStream(("password:SUCCESS").getBytes())));

				oneOf(process).getOutputStream();
				will(returnValue(out));
			}
		});
		assertEquals(true, sudoManager.authenticate("fake".toCharArray()));
		assertEquals("fake\n", out.toString()); // assert we wrote password to STDIN
		context.assertIsSatisfied();
	}

	@Test
	public void testAuthenticateWithBadPassword() throws Exception
	{
		context.checking(new Expectations()
		{
			{
				oneOf(processRunner).run(with(any(Map.class)),
						with(new String[] { "sudo", "-k", "-S", "-p", "password:", "--", "echo", "SUCCESS" }));
				will(returnValue(process));

				oneOf(process).getInputStream();
				will(returnValue(new ByteArrayInputStream(("password:\nSorry, try again.\npassword:").getBytes())));

				oneOf(process).getOutputStream();
				will(returnValue(out));
			}
		});
		assertEquals(false, sudoManager.authenticate("fake".toCharArray()));
		assertEquals("fake\n", out.toString()); // assert we wrote password to STDIN
		context.assertIsSatisfied();
	}

	@Test
	public void testAuthenticateWithNoPassword() throws Exception
	{
		context.checking(new Expectations()
		{
			{
				oneOf(processRunner).run(with(any(Map.class)),
						with(new String[] { "sudo", "-k", "-n", "--", "echo", "SUCCESS" }));
				will(returnValue(process));

				oneOf(process).getInputStream();
				will(returnValue(new ByteArrayInputStream(("SUCCESS").getBytes())));

				oneOf(process).getOutputStream();
				will(returnValue(out));
			}
		});
		assertEquals(true, sudoManager.authenticate(null));
		assertEquals("", out.toString()); // assert we wrote no password to STDIN
		context.assertIsSatisfied();
	}

	@Test
	public void testAuthenticateWithLecture() throws Exception
	{
		context.checking(new Expectations()
		{
			{
				oneOf(processRunner).run(with(any(Map.class)),
						with(new String[] { "sudo", "-k", "-S", "-p", "password:", "--", "echo", "SUCCESS" }));
				will(returnValue(process));

				oneOf(process).getInputStream();
				will(returnValue(new ByteArrayInputStream(
						("\nWARNING: Improper use of the sudo command could lead to data loss\n"
								+ "or the deletion of important system files. Please double-check your\n"
								+ "typing when using sudo. Type \"man sudo\" for more information.\n\n"
								+ "To proceed, enter your password, or type Ctrl-C to abort.\n\n" + "password:SUCCESS")
								.getBytes())));

				oneOf(process).getOutputStream();
				will(returnValue(out));
			}
		});
		assertEquals(true, sudoManager.authenticate("fake".toCharArray()));
		assertEquals("fake\n", out.toString()); // assert we wrote password to STDIN
		context.assertIsSatisfied();
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
