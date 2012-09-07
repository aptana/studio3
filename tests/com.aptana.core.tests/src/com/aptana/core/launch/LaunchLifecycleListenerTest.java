/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.launch;

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;

import com.aptana.core.util.StringUtil;

public class LaunchLifecycleListenerTest extends TestCase
{
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TestLaunchState.states.clear();
	}

	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
		TestLaunchState.states.clear();
	}

	public void testLaunchLifecycleListener() throws Exception
	{
		DebugPlugin debugPlugin = org.eclipse.debug.core.DebugPlugin.getDefault();
		ILaunchManager manager = debugPlugin.getLaunchManager();
		ILaunchConfigurationType type = manager
				.getLaunchConfigurationType("com.aptana.core.tests.launchLifecycleLaunchConfigurationTest");
		ILaunchConfigurationWorkingCopy config = type.newInstance(null, "test_launch_lifecycle");
		config.setAttribute(DebugPlugin.ATTR_CAPTURE_OUTPUT, false);
		TestLaunchState.states.add("START_LAUNCH");
		ILaunch launch = config.launch(ILaunchManager.RUN_MODE, null);
		TestLaunchState.states.add("END_LAUNCH");

		TestLaunchState.states.add("START_TERMINATE");

		launch.terminate();
		// Note: it's not enough just doing a launch.terminate, it has to actually call the fireTerminate()
		// -- it's usually called from Eclipse, but it's hard to emulate it as it depends on threads being
		// called, so, just call it directly at this point so that the test knows about the termination.
		Method method = Launch.class.getDeclaredMethod("fireTerminate");
		method.setAccessible(true);
		method.invoke(launch);
		TestLaunchState.states.add("END_TERMINATE");

		//@formatter:off
		assertEquals(
				StringUtil.join("\n", TestLaunchState.states), 
				"START_LAUNCH\n" +
				"LAUNCH_LISTENER:BEFORE:P2\n" +
				"LAUNCH_LISTENER:BEFORE\n" +
				"ON_LAUNCH_DELEGATE\n" +
				"END_LAUNCH\n" +
				"START_TERMINATE\n" +
				"LAUNCH_LISTENER:AFTER:P2\n" +
				"LAUNCH_LISTENER:AFTER\n" +
				"END_TERMINATE" 
		);
		//@formatter:on
	}
}
