package com.aptana.js.debug.core.internal.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IProcess;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.aptana.core.IURIMapper;
import com.aptana.core.sourcemap.ISourceMap;
import com.aptana.core.sourcemap.ISourceMapRegistry;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.debug.core.DebugOptionsManager;
import com.aptana.debug.core.DetailFormatter;
import com.aptana.js.debug.core.IJSDebugConstants;
import com.aptana.js.debug.core.ILaunchConfigurationConstants;
import com.aptana.js.debug.core.model.IJSConnection;
import com.aptana.js.debug.core.model.IJSDebugConnectionHandler;
import com.aptana.js.debug.core.preferences.IJSDebugPreferenceNames;

public class JSDebugTargetTest
{
	private States test;
	private Mockery context;
	private JSDebugTarget target;
	private IJSConnection connection;
	private ILaunch launch;
	private IProcess process;
	private IURIMapper uriMapper;
	private ILaunchConfiguration launchConfig;
	private IBreakpointManager breakpointManager;
	private ISourceMapRegistry sourceMapRegistry;
	private DebugOptionsManager debugOptionsManager;
	private IWorkspaceRoot workspaceRoot;
	private IProject project;
	private ISourceMap sourceMap;
	private JSDebugThread thread;
	private IEclipsePreferences prefs;

	@Before
	public void setUp() throws Exception
	{
		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
		test = context.states("test");
		launch = context.mock(ILaunch.class);
		process = context.mock(IProcess.class);
		uriMapper = context.mock(IURIMapper.class);
		connection = context.mock(IJSConnection.class);
		launchConfig = context.mock(ILaunchConfiguration.class);
		breakpointManager = context.mock(IBreakpointManager.class);
		sourceMapRegistry = context.mock(ISourceMapRegistry.class);
		debugOptionsManager = context.mock(DebugOptionsManager.class);
		workspaceRoot = context.mock(IWorkspaceRoot.class);
		project = context.mock(IProject.class);
		sourceMap = context.mock(ISourceMap.class);
		thread = context.mock(JSDebugThread.class);
		prefs = context.mock(IEclipsePreferences.class);
		context.checking(new Expectations()
		{
			{
				allowing(launch).getLaunchConfiguration();
				will(returnValue(launchConfig));

				allowing(prefs).addPreferenceChangeListener(with(any(IPreferenceChangeListener.class)));
				allowing(prefs).removePreferenceChangeListener(with(any(IPreferenceChangeListener.class)));

				oneOf(launchConfig).getAttribute(ILaunchConfigurationConstants.ATTR_PROJECT_NAME, (String) null);
				will(returnValue("projectName"));

				oneOf(workspaceRoot).getProject("projectName");
				will(returnValue(project));

				oneOf(launch).getAttribute("ATTR_TITANIUM_DEPLOY_TARGET");
				will(returnValue("ios"));

				oneOf(sourceMapRegistry).getSourceMap(project, "ios");
				will(returnValue(sourceMap));

				oneOf(launch).addDebugTarget(with(any(JSDebugTarget.class)));

				oneOf(connection).start(with(any(IJSDebugConnectionHandler.class)));

				oneOf(connection).sendCommandAndWait("version");
				will(returnValue(new String[] { "version", "2", "1234" }));

				allowing(connection).sendCommandAndWait("update");
				will(returnValue(new String[] { "update", "something" }));

				allowing(connection).isConnected();
				when(test.isNot("fully-set-up"));
				will(returnValue(true));

				oneOf(connection).sendCommandAndWait("option*monitorXHR*true");

				oneOf(prefs).getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_FIRST_LINE, false);
				will(returnValue(Boolean.TRUE.toString()));
				oneOf(connection).sendCommandAndWait("option*suspendOnFirstLine*true");

				oneOf(prefs).getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_ALL_EXCEPTIONS, false);
				will(returnValue(Boolean.TRUE.toString()));
				oneOf(connection).sendCommandAndWait("option*suspendOnExceptions*true");

				oneOf(prefs).getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_UNCAUGHT_EXCEPTIONS, false);
				will(returnValue(Boolean.TRUE.toString()));
				oneOf(connection).sendCommandAndWait("option*suspendOnErrors*true");

				oneOf(prefs).getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_DEBUGGER_KEYWORD, false);
				will(returnValue(Boolean.TRUE.toString()));
				oneOf(connection).sendCommandAndWait("option*suspendOnKeywords*true");

				oneOf(connection).sendCommandAndWait("option*bypassConstructors*false");
				oneOf(connection).sendCommandAndWait("option*stepFiltersEnabled*false");

				oneOf(debugOptionsManager).getDetailFormatters();
				will(returnValue(CollectionsUtil.newList(new DetailFormatter("typeName", "snippet", true))));

				oneOf(connection).sendCommandAndWait("detailFormatters*typeName|snippet");

				oneOf(debugOptionsManager).addChangeListener(with(any(JSDebugTarget.class)));

				oneOf(breakpointManager).getBreakpoints(IJSDebugConstants.ID_DEBUG_MODEL);
				will(returnValue(new IBreakpoint[0]));

				// Register listeners
				oneOf(breakpointManager).addBreakpointManagerListener(with(any(JSDebugTarget.class)));
				oneOf(breakpointManager).addBreakpointListener(with(any(JSDebugTarget.class)));

				oneOf(connection).sendCommandAndWait("enable");
			}
		});
		target = new JSDebugTarget(launch, "label", process, uriMapper, connection, ILaunchManager.DEBUG_MODE, true)
		{
			@Override
			protected ISourceMapRegistry getSourceMapRegistry()
			{
				return sourceMapRegistry;
			}

			@Override
			protected IBreakpointManager getBreakpointManager()
			{
				return breakpointManager;
			};

			@Override
			protected DebugOptionsManager getDebugOptionsManager()
			{
				return debugOptionsManager;
			}

			@Override
			protected IWorkspaceRoot getWorkspaceRoot()
			{
				return workspaceRoot;
			}

			@Override
			protected JSDebugThread createThread(JSDebugTarget jsDebugTarget, String threadId, String string)
			{
				return thread;
			}

			@Override
			protected IEclipsePreferences getPreferences()
			{
				return prefs;
			}
		};
		test.become("fully-set-up");
	}

	@After
	public void tearDown() throws Exception
	{
		target = null;
		connection = null;
		launch = null;
		process = null;
		uriMapper = null;
		context = null;
		prefs = null;
	}

	@Test
	@Ignore("Not yet implemented")
	public void testGetOriginalMappedLocation()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testGetConnection()
	{
		assertEquals(connection, target.getConnection());
	}

	@Test
	@Ignore("Not yet implemented")
	public void testGetAdapterClass()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testGetLaunch()
	{
		assertEquals(launch, target.getLaunch());
	}

	@Test
	public void testGetDebugTarget()
	{
		assertSame(target, target.getDebugTarget());
	}

	@Test
	public void testGetProcess()
	{
		assertEquals(process, target.getProcess());
	}

	@Test
	@Ignore("Not yet implemented")
	public void testGetThreads()
	{
		fail("Not yet implemented");
	}

	@Test
	@Ignore("Not yet implemented")
	public void testHasThreads()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testGetName() throws CoreException
	{
		assertEquals("label", target.getName());
		// assertEquals("JS Debugger", new JSDebugTarget(launch, null, process, uriMapper, connection,
		// ILaunchManager.DEBUG_MODE, true).getName());
	}

	@Test
	@Ignore("Not yet implemented")
	public void testSupportsBreakpoint()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testCanTerminate()
	{
		context.checking(new Expectations()
		{
			{
				oneOf(connection).isTerminated();
				will(returnValue(false));
			}
		});
		assertTrue(target.canTerminate());
		context.assertIsSatisfied();
	}

	@Test
	public void testIsTerminated()
	{
		context.checking(new Expectations()
		{
			{
				oneOf(connection).isTerminated();
				will(returnValue(true));
			}
		});
		assertTrue(target.isTerminated());
		context.assertIsSatisfied();
	}

	@Test
	public void testTerminate() throws DebugException
	{
		context.checking(new Expectations()
		{
			{
				oneOf(connection).isTerminated();
				will(returnValue(false));

				oneOf(connection).sendCommand("terminate");
			}
		});
		target.terminate();
		context.assertIsSatisfied();
	}

	@Test
	public void testTerminateDoesNothingIfAlreadyTerminated() throws DebugException
	{
		context.checking(new Expectations()
		{
			{
				oneOf(connection).isTerminated();
				will(returnValue(true));

				never(connection).sendCommand("terminate");
			}
		});
		target.terminate();
		context.assertIsSatisfied();
	}

	@Test
	@Ignore("Not yet implemented")
	public void testCanResume()
	{
		fail("Not yet implemented");
	}

	@Test
	@Ignore("Not yet implemented")
	public void testCanSuspend()
	{
		fail("Not yet implemented");
	}

	@Test
	@Ignore("Not yet implemented")
	public void testIsSuspended()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testResume() throws DebugException
	{
		context.checking(new Expectations()
		{
			{
				// handleThreads
				oneOf(thread).fireCreationEvent();

				// resume
				oneOf(thread).resume();
			}
		});
		// create a thread
		target.handleThreads(new String[] { "threads", "created", "0", "" });
		// Now ask the threads to resum
		target.resume();
		context.assertIsSatisfied();
	}

	@Test
	public void testSuspend() throws DebugException
	{
		context.checking(new Expectations()
		{
			{
				// handleThreads
				oneOf(thread).fireCreationEvent();

				// suspend
				oneOf(connection).isConnected();
				will(returnValue(true));

				oneOf(thread).suspend();
			}
		});
		// create a thread
		target.handleThreads(new String[] { "threads", "created", "0", "" });
		// now suspend, make sure we ask thread to suspend
		target.suspend();
		context.assertIsSatisfied();
	}

	@Test
	public void testSuspendDoesNothingIfDisconnected() throws DebugException
	{
		context.checking(new Expectations()
		{
			{
				oneOf(connection).isConnected();
				will(returnValue(false));

				never(thread).suspend();
			}
		});
		target.suspend();
		context.assertIsSatisfied();
	}

	@Test
	@Ignore("Not yet implemented")
	public void testBreakpointAdded()
	{
		fail("Not yet implemented");
	}

	@Test
	@Ignore("Not yet implemented")
	public void testBreakpointRemoved()
	{
		fail("Not yet implemented");
	}

	@Test
	@Ignore("Not yet implemented")
	public void testBreakpointChanged()
	{
		fail("Not yet implemented");
	}

	@Test
	@Ignore("Not yet implemented")
	public void testCanDisconnect()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testDisconnect() throws DebugException
	{
		context.checking(new Expectations()
		{
			{
				// handleThreads
				oneOf(thread).fireCreationEvent();

				// disconnect
				oneOf(connection).sendCommandAndWait("disable");

				oneOf(connection).isConnected();

				will(returnValue(true));
				oneOf(connection).stop();

				oneOf(thread).fireTerminateEvent();

				oneOf(debugOptionsManager).removeChangeListener(target);
				oneOf(breakpointManager).removeBreakpointListener(target);
				oneOf(breakpointManager).removeBreakpointManagerListener(target);
			}
		});
		// create a thread
		target.handleThreads(new String[] { "threads", "created", "0", "" });
		target.disconnect();
		context.assertIsSatisfied();
	}

	@Test
	public void testIsDisconnected()
	{
		context.checking(new Expectations()
		{
			{
				oneOf(connection).isConnected();
				will(returnValue(false));
			}
		});
		assertTrue(target.isDisconnected());
		context.assertIsSatisfied();
	}

	@Test
	public void testSupportsStorageRetrieval()
	{
		assertFalse(target.supportsStorageRetrieval());
	}

	@Test(expected = DebugException.class)
	public void testGetMemoryBlock() throws DebugException
	{
		target.getMemoryBlock(0, 123);
	}

	@Test
	public void testIsFilterConstructors()
	{
		assertFalse(target.isFilterConstructors());
		target.setFilterConstructors(true);
		assertTrue(target.isFilterConstructors());
		target.setFilterConstructors(false);
		assertFalse(target.isFilterConstructors());
	}

	@Test
	public void testSetStepFiltersEnabled() throws DebugException
	{
		context.checking(new Expectations()
		{
			{
				oneOf(connection).isConnected();
				will(returnValue(true));

				oneOf(connection).sendCommandAndWait("option*stepFiltersEnabled*true");

				oneOf(connection).isConnected();
				will(returnValue(true));

				oneOf(connection).sendCommandAndWait("option*stepFiltersEnabled*false");
			}
		});

		assertFalse(target.isStepFiltersEnabled());
		target.setStepFiltersEnabled(true);
		assertTrue(target.isStepFiltersEnabled());
		target.setStepFiltersEnabled(false);
		assertFalse(target.isStepFiltersEnabled());
		context.assertIsSatisfied();
	}

	@Test
	public void testSupportsStepFilters() throws DebugException
	{
		context.checking(new Expectations()
		{
			{
				oneOf(connection).isTerminated();
				will(returnValue(false));

				oneOf(connection).isConnected();
				will(returnValue(true));
			}
		});
		assertTrue(target.supportsStepFilters());
		context.assertIsSatisfied();
	}

	@Test
	public void testDoesntSupportStepFiltersIfTerminated() throws DebugException
	{
		context.checking(new Expectations()
		{
			{
				oneOf(connection).isTerminated();
				will(returnValue(true));

				never(connection).isConnected();
			}
		});
		assertFalse(target.supportsStepFilters());
		context.assertIsSatisfied();
	}

	@Test
	public void testDoesntSupportStepFiltersIfDisconnected() throws DebugException
	{
		context.checking(new Expectations()
		{
			{
				oneOf(connection).isTerminated();
				will(returnValue(false));

				oneOf(connection).isConnected();
				will(returnValue(false));
			}
		});
		assertFalse(target.supportsStepFilters());
		context.assertIsSatisfied();
	}

	@Test
	@Ignore("Not yet implemented")
	public void testGetStepFilters()
	{
		fail("Not yet implemented");
	}

	@Test
	@Ignore("Not yet implemented")
	public void testSetStepFilters()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testGetAttribute()
	{
		context.checking(new Expectations()
		{
			{
				oneOf(launch).getAttribute("key");
				will(returnValue("value"));
			}
		});
		assertEquals("value", target.getAttribute("key"));
		context.assertIsSatisfied();
	}

	@Test
	public void testSetAttribute() throws DebugException
	{
		context.checking(new Expectations()
		{
			{
				oneOf(launch).setAttribute("key", "value");

				oneOf(launch).getAttribute("key");
				will(returnValue("value"));

				never(connection).sendCommandAndWait("option*key*value");
			}
		});
		target.setAttribute("key", "value");
		context.assertIsSatisfied();
	}

	@Test
	public void testSetAttributeWithSpecificAttributes() throws DebugException
	{
		context.checking(new Expectations()
		{
			{
				oneOf(launch).setAttribute("suspendOnFirstLine", "false");

				oneOf(launch).getAttribute("suspendOnFirstLine");
				will(returnValue("false"));

				oneOf(connection).isConnected();
				will(returnValue(true));

				oneOf(connection).sendCommandAndWait("option*suspendOnFirstLine*false");
			}
		});
		target.setAttribute("suspendOnFirstLine", "false");
		context.assertIsSatisfied();
	}

	@Test
	@Ignore("Not yet implemented")
	public void testDetailFormattersChanged()
	{
		fail("Not yet implemented");
	}

	@Test
	@Ignore("Not yet implemented")
	public void testOpenURL()
	{
		fail("Not yet implemented");
	}

	@Test
	@Ignore("Not yet implemented")
	public void testBreakpointManagerEnablementChanged()
	{
		fail("Not yet implemented");
	}

	@Test
	@Ignore("Not yet implemented")
	public void testLoadVariables()
	{
		fail("Not yet implemented");
	}

	@Test
	@Ignore("Not yet implemented")
	public void testEvaluateExpression()
	{
		fail("Not yet implemented");
	}

	@Test
	@Ignore("Not yet implemented")
	public void testSetValue()
	{
		fail("Not yet implemented");
	}

	@Test
	@Ignore("Not yet implemented")
	public void testComputeValueDetails()
	{
		fail("Not yet implemented");
	}

	@Test
	@Ignore("Not yet implemented")
	public void testFindVariable()
	{
		fail("Not yet implemented");
	}

	@Test
	@Ignore("Not yet implemented")
	public void testFindBreakpointAt()
	{
		fail("Not yet implemented");
	}

	@Test
	@Ignore("Not yet implemented")
	public void testFindBreakpointIn()
	{
		fail("Not yet implemented");
	}

	@Test
	@Ignore("Not yet implemented")
	public void testResolveSourceFile()
	{
		fail("Not yet implemented");
	}

	@Test
	@Ignore("Not yet implemented")
	public void testGetTopScriptElements()
	{
		fail("Not yet implemented");
	}

}
