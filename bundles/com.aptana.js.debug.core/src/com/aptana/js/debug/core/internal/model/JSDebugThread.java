/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.internal.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Vector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.sourcemap.ISourceMapResult;
import com.aptana.core.util.StringUtil;
import com.aptana.debug.core.DebugCorePlugin;
import com.aptana.js.debug.core.JSDebugPlugin;
import com.aptana.js.debug.core.internal.Util;
import com.aptana.js.debug.core.model.IJSLineBreakpoint;

/**
 * @author Max Stepanov
 */
public class JSDebugThread extends JSDebugElement implements IThread
{

	protected static final String SUSPENDED = "suspended"; //$NON-NLS-1$
	private static final String SUSPEND = "suspend"; //$NON-NLS-1$
	private static final String SUSPEND_V2 = "suspend*{0}"; //$NON-NLS-1$
	private static final String BREAKPOINT = "breakpoint"; //$NON-NLS-1$
	private static final String KEYWORD = "keyword"; //$NON-NLS-1$
	private static final String FIRST_LINE = "firstLine"; //$NON-NLS-1$
	private static final String EXCEPTION = "exception"; //$NON-NLS-1$
	private static final String WATCHPOINT = "watchpoint"; //$NON-NLS-1$
	protected static final String RESUMED = "resumed"; //$NON-NLS-1$
	private static final String STEP_INTO = "stepInto"; //$NON-NLS-1$
	private static final String STEP_INTO_V2 = "stepInto*{0}"; //$NON-NLS-1$
	private static final String STEP_OVER = "stepOver"; //$NON-NLS-1$
	private static final String STEP_OVER_V2 = "stepOver*{0}"; //$NON-NLS-1$
	private static final String RESUME = "resume"; //$NON-NLS-1$
	private static final String RESUME_V2 = "resume*{0}"; //$NON-NLS-1$
	private static final String ABORT = "abort"; //$NON-NLS-1$
	private static final String START = "start"; //$NON-NLS-1$
	private static final String STEP_RETURN = "stepReturn"; //$NON-NLS-1$
	private static final String STEP_RETURN_V2 = "stepReturn*{0}"; //$NON-NLS-1$
	private static final String STEP_TO_FRAME = "stepToFrame"; //$NON-NLS-1$
	private static final String STEP = "step"; //$NON-NLS-1$
	private static final String FRAMES = "frames"; //$NON-NLS-1$
	private static final String FRAMES_V2 = "frames*{0}"; //$NON-NLS-1$
	private static final String SUBARGS_SPLIT = "\\|"; //$NON-NLS-1$
	private static final String STEP_TO_FRAME_0 = "stepToFrame*{1,number,integer}"; //$NON-NLS-1$
	private static final String STEP_TO_FRAME_0_V2 = "stepToFrame*{0}*{1,number,integer}"; //$NON-NLS-1$

	private enum State
	{
		STARTING, RUNNING, SUSPENDED, SUSPENDING, STEPPPING
	}

	private static final IStackFrame[] emptyStack = new IStackFrame[0];
	private static final IBreakpoint[] emptyBreakpoints = new IBreakpoint[0];

	private final String threadId;
	private final String label;
	private IStackFrame[] stackFrames = emptyStack;
	private IBreakpoint[] breakpoints = emptyBreakpoints;
	private State runningState = State.STARTING;
	private boolean validateFrames = false;

	/**
	 * JSDebugThread
	 * 
	 * @param target
	 */
	public JSDebugThread(IDebugTarget target, String threadId, String label)
	{
		super(target);
		this.threadId = threadId;
		this.label = label;
	}

	/*
	 * @see org.eclipse.debug.core.model.IThread#getStackFrames()
	 */
	public IStackFrame[] getStackFrames() throws DebugException
	{
		if (!isSuspended())
		{
			return emptyStack;
		}
		getStackFrames0();
		return isSuspended() && stackFrames != null ? stackFrames : emptyStack;
	}

	/*
	 * @see org.eclipse.debug.core.model.IThread#hasStackFrames()
	 */
	public boolean hasStackFrames() throws DebugException
	{
		if (!isSuspended())
		{
			return false;
		}
		getStackFrames0();
		return stackFrames != null && (stackFrames.length > 0);
	}

	/*
	 * @see org.eclipse.debug.core.model.IThread#getPriority()
	 */
	public int getPriority() throws DebugException
	{
		return 0;
	}

	/*
	 * @see org.eclipse.debug.core.model.IThread#getTopStackFrame()
	 */
	public IStackFrame getTopStackFrame() throws DebugException
	{
		if (!isSuspended())
		{
			return null;
		}
		getStackFrames0();
		return stackFrames != null && stackFrames.length > 0 ? stackFrames[0] : null;
	}

	/*
	 * @see org.eclipse.debug.core.model.IThread#getName()
	 */
	public String getName() throws DebugException
	{
		String name = (label != null) ? label
				: (threadId == JSDebugTarget.DEFAULT_THREAD_ID) ? Messages.JSDebugThread_main_label : threadId;
		return MessageFormat.format(Messages.JSDebugThread_Thread_Label, name,
				(runningState == State.SUSPENDING ? MessageFormat.format(" ({0})", Messages.JSDebugThread_Suspending) //$NON-NLS-1$
						: StringUtil.EMPTY));
	}

	/*
	 * @see org.eclipse.debug.core.model.IThread#getBreakpoints()
	 */
	public IBreakpoint[] getBreakpoints()
	{
		return isSuspended() && breakpoints != null ? breakpoints : emptyBreakpoints;
	}

	/*
	 * @see org.eclipse.debug.core.model.ISuspendResume#canResume()
	 */
	public boolean canResume()
	{
		return isSuspended();
	}

	/*
	 * @see org.eclipse.debug.core.model.ISuspendResume#canSuspend()
	 */
	public boolean canSuspend()
	{
		return !isSuspended() && runningState != State.STARTING;
	}

	/*
	 * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
	 */
	public boolean isSuspended()
	{
		return runningState == State.SUSPENDED;
	}

	/*
	 * @see org.eclipse.debug.core.model.ISuspendResume#resume()
	 */
	public void resume() throws DebugException
	{
		if (runningState != State.SUSPENDED)
		{
			return;
		}
		runningState = State.STEPPPING;
		fireChangeEvent(DebugEvent.STATE);
		JSDebugTarget target = getJSDebugTarget();
		String command = MessageFormat.format(target.getProtocolVersion() >= 2 ? RESUME_V2 : RESUME, threadId);
		target.getConnection().sendCommand(command);
	}

	/*
	 * @see org.eclipse.debug.core.model.ISuspendResume#suspend()
	 */
	public void suspend() throws DebugException
	{
		if (runningState != State.RUNNING)
		{
			return;
		}
		runningState = State.SUSPENDING;
		fireChangeEvent(DebugEvent.STATE);
		JSDebugTarget target = getJSDebugTarget();
		String command = MessageFormat.format(target.getProtocolVersion() >= 2 ? SUSPEND_V2 : SUSPEND, threadId);
		target.getConnection().sendCommand(command);
	}

	/*
	 * @see org.eclipse.debug.core.model.IStep#canStepInto()
	 */
	public boolean canStepInto()
	{
		return isSuspended() && !isStepping();
	}

	/*
	 * @see org.eclipse.debug.core.model.IStep#canStepOver()
	 */
	public boolean canStepOver()
	{
		return isSuspended() && !isStepping();
	}

	/*
	 * @see org.eclipse.debug.core.model.IStep#canStepReturn()
	 */
	public boolean canStepReturn()
	{
		return isSuspended() && !isStepping();
	}

	/*
	 * @see org.eclipse.debug.core.model.IStep#isStepping()
	 */
	public boolean isStepping()
	{
		return runningState == State.STEPPPING;
	}

	/*
	 * @see org.eclipse.debug.core.model.IStep#stepInto()
	 */
	public void stepInto() throws DebugException
	{
		if (runningState != State.SUSPENDED)
		{
			return;
		}
		runningState = State.STEPPPING;
		fireChangeEvent(DebugEvent.STATE);
		JSDebugTarget target = getJSDebugTarget();
		String command = MessageFormat.format(target.getProtocolVersion() >= 2 ? STEP_INTO_V2 : STEP_INTO, threadId);
		target.getConnection().sendCommand(command);
	}

	/*
	 * @see org.eclipse.debug.core.model.IStep#stepOver()
	 */
	public void stepOver() throws DebugException
	{
		if (runningState != State.SUSPENDED)
		{
			return;
		}
		runningState = State.STEPPPING;
		fireChangeEvent(DebugEvent.STATE);
		JSDebugTarget target = getJSDebugTarget();
		String command = MessageFormat.format(target.getProtocolVersion() >= 2 ? STEP_OVER_V2 : STEP_OVER, threadId);
		target.getConnection().sendCommand(command);
	}

	/*
	 * @see org.eclipse.debug.core.model.IStep#stepReturn()
	 */
	public void stepReturn() throws DebugException
	{
		if (runningState != State.SUSPENDED)
		{
			return;
		}
		runningState = State.STEPPPING;
		fireChangeEvent(DebugEvent.STATE);
		JSDebugTarget target = getJSDebugTarget();
		String command = MessageFormat
				.format(target.getProtocolVersion() >= 2 ? STEP_RETURN_V2 : STEP_RETURN, threadId);
		target.getConnection().sendCommand(command);
	}

	/*
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	public boolean canTerminate()
	{
		return getDebugTarget().canTerminate();
	}

	/*
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	public boolean isTerminated()
	{
		return getDebugTarget().isTerminated();
	}

	/*
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	public void terminate() throws DebugException
	{
		getDebugTarget().terminate();
	}

	/* package */void handleMessage(String[] args)
	{
		String action = args[0];
		int details = DebugEvent.UNSPECIFIED;
		if (SUSPENDED.equals(action))
		{
			invalidateStackFrames();
			runningState = State.SUSPENDED;
			breakpoints = null;
			String reason = args[1];
			if (BREAKPOINT.equals(reason) || KEYWORD.equals(reason) || FIRST_LINE.equals(reason)
					|| EXCEPTION.equals(reason) || WATCHPOINT.equals(reason))
			{
				details = DebugEvent.BREAKPOINT;
				stackFrames = null;
				/* find breakpoint(s) */
				URI sourceFile = getJSDebugTarget().resolveSourceFile(Util.decodeData(args[2]));
				try
				{
					if (BREAKPOINT.equals(reason))
					{
						breakpointHit(sourceFile, Integer.parseInt(args[3]));
					}
					else
					{
						JSDebugImplicitBreakpoint.Type type = null;
						if (KEYWORD.equals(reason))
						{
							type = JSDebugImplicitBreakpoint.Type.DEBUGGER_KEYWORD;
						}
						else if (FIRST_LINE.equals(reason))
						{
							type = JSDebugImplicitBreakpoint.Type.FIRST_LINE;
						}
						else if (EXCEPTION.equals(reason))
						{
							type = JSDebugImplicitBreakpoint.Type.EXCEPTION;
						}
						else if (WATCHPOINT.equals(reason))
						{
							type = JSDebugImplicitBreakpoint.Type.WATCHPOINT;
						}
						implicitBreakpointHit(sourceFile, Integer.parseInt(args[3]), type);
					}
				}
				catch (NumberFormatException e)
				{
				}
			}
			else if (reason.startsWith(STEP))
			{
				details = DebugEvent.STEP_END;
			}
			else
			{
				details = DebugEvent.CLIENT_REQUEST;
				stackFrames = null;
			}

			fireSuspendEvent(details);
			fireChangeEvent(DebugEvent.STATE);
		}
		else if (RESUMED.equals(action))
		{
			String reason = args[1];
			if (STEP_INTO.equals(reason))
			{
				details = DebugEvent.STEP_INTO;
			}
			else if (STEP_OVER.equals(reason))
			{
				details = DebugEvent.STEP_OVER;
			}
			else if (STEP_RETURN.equals(reason) || STEP_TO_FRAME.equals(reason))
			{
				details = DebugEvent.STEP_RETURN;
			}
			else if (RESUME.equals(reason))
			{
				details = DebugEvent.CLIENT_REQUEST;
				runningState = State.RUNNING;
			}
			else if (ABORT.equals(reason) || START.equals(reason))
			{
				details = DebugEvent.UNSPECIFIED;
				runningState = State.RUNNING;
			}
			else
			{
				runningState = State.RUNNING;
			}
			if (reason.startsWith(STEP))
			{
				/* top-level frame */
				if (stackFrames != null && stackFrames.length == 1)
				{
					details = DebugEvent.CLIENT_REQUEST;
				}
			}
			fireChangeEvent(DebugEvent.STATE);
			fireResumeEvent(details);
		}
	}

	/* package */void stepToFrame(IStackFrame frame) throws DebugException
	{
		if (runningState != State.SUSPENDED)
		{
			return;
		}
		int targetFrameId = ((JSDebugStackFrame) frame).getFrameId();
		runningState = State.STEPPPING;
		fireChangeEvent(DebugEvent.STATE);
		JSDebugTarget target = getJSDebugTarget();
		String command = MessageFormat.format(target.getProtocolVersion() >= 2 ? STEP_TO_FRAME_0_V2 : STEP_TO_FRAME_0,
				threadId, targetFrameId);
		target.getConnection().sendCommand(command);
	}

	/* package */boolean isInSuspendState()
	{
		return runningState == State.SUSPENDED || runningState == State.SUSPENDING;
	}

	/* package */String getThreadId()
	{
		return threadId;
	}

	private JSDebugTarget getJSDebugTarget()
	{
		return (JSDebugTarget) getDebugTarget();
	}

	private void breakpointHit(URI filename, int lineNumber)
	{
		IBreakpoint breakpoint = getJSDebugTarget().findBreakpointAt(filename, lineNumber);
		if (breakpoint != null && breakpoint instanceof IJSLineBreakpoint)
		{
			try
			{
				if (((IJSLineBreakpoint) breakpoint).getHitCount() > 0)
				{
					breakpoint.setEnabled(false);
				}
			}
			catch (CoreException e)
			{
				JSDebugPlugin.log(e);
			}
		}
		breakpoints = breakpoint != null ? new IBreakpoint[] { breakpoint } : null;
		// TODO: where to find runToLine breakpoint ?
	}

	private void implicitBreakpointHit(URI filename, int line, JSDebugImplicitBreakpoint.Type type)
	{
		/* TODO: use project-related path ? */
		breakpoints = new IBreakpoint[] { new JSDebugImplicitBreakpoint(filename, line, type) };
	}

	private synchronized void getStackFrames0() throws DebugException
	{
		if (!validateFrames && stackFrames != null)
		{
			return;
		}
		JSDebugTarget target = getJSDebugTarget();
		String command = MessageFormat.format(target.getProtocolVersion() >= 2 ? FRAMES_V2 : FRAMES, threadId);
		String[] args = target.getConnection().sendCommandAndWait(command);
		if (args != null)
		{
			Vector<IStackFrame> frames = new Vector<IStackFrame>();
			int frameIndex = (stackFrames != null) ? stackFrames.length - 1 : -1;
			for (int i = args.length - 1; i >= 1; --i)
			{
				int j = 0;
				String[] subargs = args[i].split(SUBARGS_SPLIT);
				int depth = Integer.parseInt(subargs[j++]);
				String function = Util.decodeData(subargs[j++]);
				String arguments = Util.decodeData(subargs[j++]);
				if (function.length() == 0)
				{
					function = MessageFormat.format("[{0}]", //$NON-NLS-1$
							i == args.length - 1 ? Messages.JSDebugTarget_TopLevelScript
									: Messages.JSDebugTarget_EvalScript);
				}
				else
				{
					function += MessageFormat.format("({0})", arguments); //$NON-NLS-1$
				}
				URI sourceFile = target.resolveSourceFile(Util.decodeData(subargs[j++]));
				int sourceLine = Integer.parseInt(subargs[j++]);
				IdeLog.logInfo(DebugCorePlugin.getDefault(), MessageFormat.format(
						"Stack traces requesting original mapped location for {0}:{1}", sourceFile, sourceLine),
						com.aptana.debug.core.IDebugScopes.DEBUG);
				ISourceMapResult sourceMapResult = target.getOriginalMappedLocation(sourceFile, sourceLine);
				if (sourceMapResult != null)
				{
					// We have mapping, so adjust the source location and line number to represent the original
					// location.
					try
					{
						sourceFile = new URI(sourceFile.getScheme(), null, sourceMapResult.getFile().toString(), null);
						sourceLine = sourceMapResult.getLineNumber();
					}
					catch (URISyntaxException e)
					{
						IdeLog.logError(JSDebugPlugin.getDefault(), e);
					}
				}
				++j; // skip native flag
				long pc = Long.parseLong(subargs[j++]);
				int scriptTag = Integer.parseInt(subargs[j++]);
				IStackFrame frame = null;
				if (frameIndex >= 0)
				{
					JSDebugStackFrame oldFrame = (JSDebugStackFrame) stackFrames[frameIndex];
					if ((oldFrame.scriptTag == scriptTag) && (oldFrame.pc == pc))
					{
						/* update depth */
						oldFrame.invalidate(depth, sourceLine, pc);
						frame = oldFrame;
						--frameIndex;
					}
					else if (oldFrame.scriptTag == scriptTag)
					{
						// update line/pc
						oldFrame.invalidate(depth, sourceLine, pc);
						frameIndex = -1;
					}
					else
					{
						frameIndex = -1;
					}
				}
				if (frame == null)
				{
					frame = new JSDebugStackFrame(target, this, depth, function, sourceFile, sourceLine, pc, scriptTag);
					IdeLog.logInfo(DebugCorePlugin.getDefault(),
							MessageFormat.format("Adding Debug stack frame for {0}:{1}", sourceFile, sourceLine),
							com.aptana.debug.core.IDebugScopes.DEBUG);
				}
				frames.add(0, frame);
			}
			stackFrames = (IStackFrame[]) frames.toArray(new IStackFrame[frames.size()]);
			validateFrames = false;
		}
	}

	private synchronized void invalidateStackFrames()
	{
		if (stackFrames != null)
		{
			for (IStackFrame stackFrame : stackFrames)
			{
				((JSDebugStackFrame) stackFrame).invalidate();
			}
		}
		validateFrames = true;
	}

}
