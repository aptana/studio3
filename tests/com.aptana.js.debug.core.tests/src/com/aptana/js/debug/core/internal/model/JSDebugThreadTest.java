package com.aptana.js.debug.core.internal.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.js.debug.core.model.IJSConnection;
import com.aptana.js.debug.core.model.IJSLineBreakpoint;

public class JSDebugThreadTest
{

	private JSDebugThread thread;
	private Mockery context;
	private JSDebugTarget target;
	private IJSConnection connection;

	@Before
	public void setUp() throws Exception
	{
		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
		target = context.mock(JSDebugTarget.class);
		connection = context.mock(IJSConnection.class);
		thread = new JSDebugThread(target, "id", "label");
	}

	@After
	public void tearDown() throws Exception
	{
		thread = null;
		target = null;
		context = null;
		connection = null;
	}

	@Test
	public void testStartThenSuspendOnBreakpointQueryForStackFramesAndBreakpoints() throws Exception
	{
		final URI uri = URI.create("sourceFile.js");
		final IJSLineBreakpoint breakpoint = context.mock(IJSLineBreakpoint.class);
		context.checking(new Expectations()
		{
			{
				oneOf(target).resolveSourceFile("sourcefile.js");
				will(returnValue(uri));

				oneOf(target).findBreakpointAt(uri, 3);
				will(returnValue(breakpoint));

				oneOf(breakpoint).getHitCount();
				will(returnValue(1));

				oneOf(breakpoint).setEnabled(false);

				// getStackFrames()
				oneOf(target).getConnection();
				will(returnValue(connection));

				// FIXME This doesn't properly override the package visible method and it uses default of 0
				// oneOf(target).getProtocolVersion();
				// will(returnValue(3));

				oneOf(connection).sendCommandAndWait("frames");
				will(returnValue(new String[] { "some_discarded_top_frame?",
						"0|function|arguments|sourceFile.js|3|native|0|0" }));

				oneOf(target).resolveSourceFile("sourceFile.js");
				will(returnValue(uri));

				oneOf(target).getOriginalMappedLocation(uri, 3);
				will(returnValue(null));
			}
		});
		// no stack frames, no breakpoints
		assertEquals(0, thread.getStackFrames().length);
		assertNull(thread.getTopStackFrame());
		assertFalse(thread.hasStackFrames());
		IBreakpoint[] breakpoints = thread.getBreakpoints();
		assertNotNull(breakpoints);
		assertEquals(0, breakpoints.length);

		// suspend on a breakpoint // TODO make sure we fire events from handleMessage?
		thread.handleMessage(new String[] { "suspended", "breakpoint", "sourcefile.js", "3" });

		// now we have stack frames and a breakpoint
		assertTrue(thread.hasStackFrames());
		IStackFrame[] frames = thread.getStackFrames();
		assertEquals(1, frames.length);
		assertEquals(3, frames[0].getLineNumber());
		assertEquals("function(arguments)", frames[0].getName());

		IStackFrame topFrame = thread.getTopStackFrame();
		assertNotNull(topFrame);
		assertEquals(3, topFrame.getLineNumber());
		assertEquals("function(arguments)", topFrame.getName());

		breakpoints = thread.getBreakpoints();
		assertNotNull(breakpoints);
		assertEquals(1, breakpoints.length);
		context.assertIsSatisfied();
	}

	@Test
	public void testGetStackFramesWhenNotSuspended() throws DebugException
	{
		IStackFrame[] frames = thread.getStackFrames();
		assertEquals(0, frames.length);
	}

	@Test
	public void testGetPriority() throws DebugException
	{
		assertEquals(0, thread.getPriority());
	}

	@Test
	public void testGetName() throws DebugException
	{
		assertEquals("Thread [label]", thread.getName());
		assertEquals("Thread [main]", new JSDebugThread(target, "0", null).getName());
		assertEquals("Thread [thread_id]", new JSDebugThread(target, "thread_id", null).getName());
	}

	@Test
	public void testStartingSuspendThenResume() throws Exception
	{
		final URI uri = URI.create("sourceFile.js");
		final IJSLineBreakpoint breakpoint = context.mock(IJSLineBreakpoint.class);
		context.checking(new Expectations()
		{
			{
				oneOf(target).resolveSourceFile("sourcefile.js");
				will(returnValue(uri));

				oneOf(target).findBreakpointAt(uri, 3);
				will(returnValue(breakpoint));

				oneOf(breakpoint).getHitCount();
				will(returnValue(1));

				oneOf(breakpoint).setEnabled(false);

				oneOf(target).getConnection();
				will(returnValue(connection));

				oneOf(connection).sendCommand("resume");
			}
		});
		assertFalse(thread.isSuspended());
		assertFalse(thread.canSuspend()); // not running state yet, so we can't
		assertFalse(thread.canResume());
		assertFalse(thread.canStepInto());
		assertFalse(thread.canStepOver());
		assertFalse(thread.canStepReturn());
		assertNull(thread.getTopStackFrame()); // can't get stack frames until suspended
		assertFalse(thread.hasStackFrames());
		thread.handleMessage(new String[] { "suspended", "breakpoint", "sourcefile.js", "3" });
		assertTrue(thread.isSuspended());
		assertFalse(thread.canSuspend());
		assertTrue(thread.canResume());
		assertTrue(thread.canStepInto());
		assertTrue(thread.canStepOver());
		assertTrue(thread.canStepReturn());
		thread.resume();
		assertFalse(thread.isSuspended());
		assertTrue(thread.canSuspend());
		assertFalse(thread.canResume());
		assertFalse(thread.canStepInto());
		assertFalse(thread.canStepOver());
		assertFalse(thread.canStepReturn());
		context.assertIsSatisfied();
	}

	@Test
	public void testIsSuspendedInitiallyFalse()
	{
		assertFalse(thread.isSuspended());
	}

	@Test
	public void testSuspend() throws DebugException
	{
		context.checking(new Expectations()
		{
			{
				oneOf(target).getConnection();
				will(returnValue(connection));

				oneOf(connection).sendCommand("suspend");
			}
		});
		// Make sure our state is RUNNING
		thread.handleMessage(new String[] { "resumed", "" });
		thread.suspend();
		context.assertIsSatisfied();
	}

	@Test
	public void testSuspendDoesNothingWhenNotRunningState() throws DebugException
	{
		context.checking(new Expectations()
		{
			{
				never(target).getConnection();
				will(returnValue(connection));

				never(connection).sendCommand("suspend");
			}
		});
		thread.suspend();
		context.assertIsSatisfied();
	}

	@Test
	public void testResumeDoesNothingWhenNotSuspendedState() throws DebugException
	{
		context.checking(new Expectations()
		{
			{
				never(target).getConnection();
				will(returnValue(connection));

				never(connection).sendCommand("resume");
			}
		});
		thread.resume();
		context.assertIsSatisfied();
	}

	@Test
	public void testStepInto() throws Exception
	{
		final URI uri = URI.create("sourceFile.js");
		final IJSLineBreakpoint breakpoint = context.mock(IJSLineBreakpoint.class);
		context.checking(new Expectations()
		{
			{
				oneOf(target).resolveSourceFile("sourcefile.js");
				will(returnValue(uri));

				oneOf(target).findBreakpointAt(uri, 3);
				will(returnValue(breakpoint));

				oneOf(breakpoint).getHitCount();
				will(returnValue(1));

				oneOf(breakpoint).setEnabled(false);

				// step into
				oneOf(target).getConnection();
				will(returnValue(connection));

				oneOf(connection).sendCommand("stepInto");
			}
		});
		assertFalse(thread.isStepping());
		// suspend
		thread.handleMessage(new String[] { "suspended", "breakpoint", "sourcefile.js", "3" });
		assertFalse(thread.isStepping());
		thread.stepInto();
		assertTrue(thread.isStepping()); // in stepping state until next message?
		context.assertIsSatisfied();
	}

	@Test
	public void testStepIntoDoesNothingIfNotSuspended() throws Exception
	{
		context.checking(new Expectations()
		{
			{
				never(target).getConnection();
				never(connection).sendCommand("stepInto");
			}
		});
		thread.stepInto();
		context.assertIsSatisfied();
	}

	@Test
	public void testStepOver() throws Exception
	{
		final URI uri = URI.create("sourceFile.js");
		final IJSLineBreakpoint breakpoint = context.mock(IJSLineBreakpoint.class);
		context.checking(new Expectations()
		{
			{
				oneOf(target).resolveSourceFile("sourcefile.js");
				will(returnValue(uri));

				oneOf(target).findBreakpointAt(uri, 3);
				will(returnValue(breakpoint));

				oneOf(breakpoint).getHitCount();
				will(returnValue(1));

				oneOf(breakpoint).setEnabled(false);

				// step over
				oneOf(target).getConnection();
				will(returnValue(connection));

				oneOf(connection).sendCommand("stepOver");
			}
		});
		assertFalse(thread.isStepping());
		// suspend
		thread.handleMessage(new String[] { "suspended", "breakpoint", "sourcefile.js", "3" });
		assertFalse(thread.isStepping());
		thread.stepOver();
		assertTrue(thread.isStepping()); // in stepping state until next message?
		context.assertIsSatisfied();
	}

	@Test
	public void testStepOverDoesNothingIfNotSuspended() throws Exception
	{
		context.checking(new Expectations()
		{
			{
				never(target).getConnection();
				never(connection).sendCommand("stepOver");
			}
		});
		thread.stepOver();
		context.assertIsSatisfied();
	}

	@Test
	public void testStepReturn() throws Exception
	{
		final URI uri = URI.create("sourceFile.js");
		final IJSLineBreakpoint breakpoint = context.mock(IJSLineBreakpoint.class);
		context.checking(new Expectations()
		{
			{
				oneOf(target).resolveSourceFile("sourcefile.js");
				will(returnValue(uri));

				oneOf(target).findBreakpointAt(uri, 3);
				will(returnValue(breakpoint));

				oneOf(breakpoint).getHitCount();
				will(returnValue(1));

				oneOf(breakpoint).setEnabled(false);

				// step return
				oneOf(target).getConnection();
				will(returnValue(connection));

				oneOf(connection).sendCommand("stepReturn");
			}
		});
		assertFalse(thread.isStepping());
		// suspend
		thread.handleMessage(new String[] { "suspended", "breakpoint", "sourcefile.js", "3" });
		assertFalse(thread.isStepping());
		thread.stepReturn();
		assertTrue(thread.isStepping()); // in stepping state until next message?
		context.assertIsSatisfied();
	}

	@Test
	public void testStepReturnDoesNothingIfNotSuspended() throws Exception
	{

		context.checking(new Expectations()
		{
			{
				never(target).getConnection();
				never(connection).sendCommand("stepReturn");
			}
		});
		thread.stepReturn();
		context.assertIsSatisfied();
	}

	@Test
	public void testCanTerminate()
	{
		context.checking(new Expectations()
		{
			{
				oneOf(target).canTerminate();
				will(returnValue(true));
			}
		});
		assertTrue(thread.canTerminate());
		context.assertIsSatisfied();
	}

	@Test
	public void testIsTerminated()
	{
		context.checking(new Expectations()
		{
			{
				oneOf(target).isTerminated();
				will(returnValue(true));
			}
		});
		assertTrue(thread.isTerminated());
		context.assertIsSatisfied();
	}

	@Test
	public void testTerminate() throws DebugException
	{
		context.checking(new Expectations()
		{
			{
				oneOf(target).terminate();
			}
		});
		thread.terminate();
		context.assertIsSatisfied();
	}
}
