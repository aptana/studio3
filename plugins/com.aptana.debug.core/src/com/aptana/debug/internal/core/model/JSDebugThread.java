/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.debug.internal.core.model;

import java.text.MessageFormat;
import java.util.Vector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;

import com.aptana.core.util.StringUtil;
import com.aptana.debug.core.JSDebugPlugin;
import com.aptana.debug.internal.core.Util;
import com.aptana.js.debug.core.model.IJSLineBreakpoint;

/**
 * @author Max Stepanov
 */
public class JSDebugThread extends JSDebugElement implements IThread {

	private static final String SUSPENDED = "suspended"; //$NON-NLS-1$
	private static final String SUSPEND = "suspend"; //$NON-NLS-1$
	private static final String BREAKPOINT = "breakpoint"; //$NON-NLS-1$
	private static final String KEYWORD = "keyword"; //$NON-NLS-1$
	private static final String FIRST_LINE = "firstLine"; //$NON-NLS-1$
	private static final String EXCEPTION = "exception"; //$NON-NLS-1$
	private static final String WATCHPOINT = "watchpoint"; //$NON-NLS-1$
	private static final String RESUMED = "resumed"; //$NON-NLS-1$
	private static final String STEP_INTO = "stepInto"; //$NON-NLS-1$
	private static final String STEP_OVER = "stepOver"; //$NON-NLS-1$
	private static final String RESUME = "resume"; //$NON-NLS-1$
	private static final String ABORT = "abort"; //$NON-NLS-1$
	private static final String START = "start"; //$NON-NLS-1$
	private static final String STEP_RETURN = "stepReturn"; //$NON-NLS-1$
	private static final String STEP_TO_FRAME = "stepToFrame"; //$NON-NLS-1$
	private static final String STEP = "step"; //$NON-NLS-1$
	private static final String FRAMES = "frames"; //$NON-NLS-1$
	private static final String SUBARGS_SPLIT = "\\|"; //$NON-NLS-1$
	private static final String STEP_TO_FRAME_0 = "stepToFrame*{0}"; //$NON-NLS-1$

	private static final int STATE_STARTING = 0;
	private static final int STATE_RUNNING = 1;
	private static final int STATE_SUSPENDED = 2;
	private static final int STATE_SUSPENDING = 3;
	private static final int STATE_STEPPPING = 4;

	private final IStackFrame[] emptyStack = new IStackFrame[0];
	private IStackFrame[] stackFrames = emptyStack;
	private final IBreakpoint[] emptyBreakpoints = new IBreakpoint[0];
	private IBreakpoint[] breakpoints = emptyBreakpoints;
	private int runningState = STATE_STARTING;
	private boolean validateFrames = false;

	/**
	 * JSDebugThread
	 * 
	 * @param target
	 */
	public JSDebugThread(IDebugTarget target) {
		super(target);
	}

	/**
	 * @see org.eclipse.debug.core.model.IThread#getStackFrames()
	 */
	public IStackFrame[] getStackFrames() throws DebugException {
		if (!isSuspended()) {
			return emptyStack;
		}
		getStackFrames0();
		return isSuspended() && stackFrames != null ? stackFrames : emptyStack;
	}

	/**
	 * @see org.eclipse.debug.core.model.IThread#hasStackFrames()
	 */
	public boolean hasStackFrames() throws DebugException {
		if (!isSuspended()) {
			return false;
		}
		getStackFrames0();
		return stackFrames != null && (stackFrames.length > 0);
	}

	/**
	 * @see org.eclipse.debug.core.model.IThread#getPriority()
	 */
	public int getPriority() throws DebugException {
		return 0;
	}

	/**
	 * @see org.eclipse.debug.core.model.IThread#getTopStackFrame()
	 */
	public IStackFrame getTopStackFrame() throws DebugException {
		if (!isSuspended()) {
			return null;
		}
		getStackFrames0();
		return stackFrames != null && stackFrames.length > 0 ? stackFrames[0] : null;
	}

	/**
	 * @see org.eclipse.debug.core.model.IThread#getName()
	 */
	public String getName() throws DebugException {
		return MessageFormat.format(Messages.JSDebugThread_ThreadMain_0, (runningState == STATE_SUSPENDING ? MessageFormat
				.format(" ({0})", Messages.JSDebugThread_Suspending) //$NON-NLS-1$
				: StringUtil.EMPTY));
	}

	/**
	 * @see org.eclipse.debug.core.model.IThread#getBreakpoints()
	 */
	public IBreakpoint[] getBreakpoints() {
		return isSuspended() && breakpoints != null ? breakpoints : emptyBreakpoints;
	}

	/**
	 * @see org.eclipse.debug.core.model.ISuspendResume#canResume()
	 */
	public boolean canResume() {
		return isSuspended();
	}

	/**
	 * @see org.eclipse.debug.core.model.ISuspendResume#canSuspend()
	 */
	public boolean canSuspend() {
		return !isSuspended() && runningState != STATE_STARTING;
	}

	/**
	 * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
	 */
	public boolean isSuspended() {
		return runningState == STATE_SUSPENDED;
	}

	/**
	 * @see org.eclipse.debug.core.model.ISuspendResume#resume()
	 */
	public void resume() throws DebugException {
		if (runningState != STATE_SUSPENDED) {
			return;
		}
		runningState = STATE_STEPPPING;
		fireChangeEvent(DebugEvent.STATE);
		((JSDebugTarget) getDebugTarget()).getConnection().sendCommand(RESUME);
	}

	/**
	 * @see org.eclipse.debug.core.model.ISuspendResume#suspend()
	 */
	public void suspend() throws DebugException {
		if (runningState != STATE_RUNNING) {
			return;
		}
		runningState = STATE_SUSPENDING;
		fireChangeEvent(DebugEvent.STATE);
		((JSDebugTarget) getDebugTarget()).getConnection().sendCommand(SUSPEND);
	}

	/**
	 * @see org.eclipse.debug.core.model.IStep#canStepInto()
	 */
	public boolean canStepInto() {
		return isSuspended() && !isStepping();
	}

	/**
	 * @see org.eclipse.debug.core.model.IStep#canStepOver()
	 */
	public boolean canStepOver() {
		return isSuspended() && !isStepping();
	}

	/**
	 * @see org.eclipse.debug.core.model.IStep#canStepReturn()
	 */
	public boolean canStepReturn() {
		return isSuspended() && !isStepping();
	}

	/**
	 * @see org.eclipse.debug.core.model.IStep#isStepping()
	 */
	public boolean isStepping() {
		return runningState == STATE_STEPPPING;
	}

	/**
	 * @see org.eclipse.debug.core.model.IStep#stepInto()
	 */
	public void stepInto() throws DebugException {
		if (runningState != STATE_SUSPENDED) {
			return;
		}
		runningState = STATE_STEPPPING;
		fireChangeEvent(DebugEvent.STATE);
		((JSDebugTarget) getDebugTarget()).getConnection().sendCommand(STEP_INTO);
	}

	/**
	 * @see org.eclipse.debug.core.model.IStep#stepOver()
	 */
	public void stepOver() throws DebugException {
		if (runningState != STATE_SUSPENDED) {
			return;
		}
		runningState = STATE_STEPPPING;
		fireChangeEvent(DebugEvent.STATE);
		((JSDebugTarget) getDebugTarget()).getConnection().sendCommand(STEP_OVER);
	}

	/**
	 * @see org.eclipse.debug.core.model.IStep#stepReturn()
	 */
	public void stepReturn() throws DebugException {
		if (runningState != STATE_SUSPENDED) {
			return;
		}
		runningState = STATE_STEPPPING;
		fireChangeEvent(DebugEvent.STATE);
		((JSDebugTarget) getDebugTarget()).getConnection().sendCommand(STEP_RETURN);
	}

	/**
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	public boolean canTerminate() {
		return getDebugTarget().canTerminate();
	}

	/**
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	public boolean isTerminated() {
		return getDebugTarget().isTerminated();
	}

	/**
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	public void terminate() throws DebugException {
		getDebugTarget().terminate();
	}

	/**
	 * handleMessage
	 * 
	 * @param args
	 */
	protected void handleMessage(String[] args) {
		String action = args[0];
		int details = DebugEvent.UNSPECIFIED;
		if (SUSPENDED.equals(action)) {
			invalidateStackFrames();
			runningState = STATE_SUSPENDED;
			breakpoints = null;
			String reason = args[1];
			if (BREAKPOINT.equals(reason) || KEYWORD.equals(reason) || FIRST_LINE.equals(reason)
					|| EXCEPTION.equals(reason) || WATCHPOINT.equals(reason)) {
				details = DebugEvent.BREAKPOINT;
				stackFrames = null;
				/* find breakpoint(s) */
				String sourceFile = ((JSDebugTarget) getDebugTarget()).resolveSourceFile(Util.decodeData(args[2]));
				try {
					if (BREAKPOINT.equals(reason)) {
						breakpointHit(sourceFile, Integer.parseInt(args[3]));
					} else {
						int type = 0;
						if (KEYWORD.equals(reason)) {
							type = JSDebugImplicitBreakpoint.TYPE_DEBUGGER_KEYWORD;
						} else if (FIRST_LINE.equals(reason)) {
							type = JSDebugImplicitBreakpoint.TYPE_FIRST_LINE;
						} else if (EXCEPTION.equals(reason)) {
							type = JSDebugImplicitBreakpoint.TYPE_EXCEPTION;
						} else if (WATCHPOINT.equals(reason)) {
							type = JSDebugImplicitBreakpoint.TYPE_WATCHPOINT;
						}
						implicitBreakpointHit(sourceFile, Integer.parseInt(args[3]), type);
					}
				} catch (NumberFormatException e) {
				}
			} else if (reason.startsWith(STEP)) {
				details = DebugEvent.STEP_END;
			} else {
				details = DebugEvent.CLIENT_REQUEST;
				stackFrames = null;
			}

			fireSuspendEvent(details);
			fireChangeEvent(DebugEvent.STATE);
		} else if (RESUMED.equals(action)) {
			String reason = args[1];
			if (STEP_INTO.equals(reason)) {
				details = DebugEvent.STEP_INTO;
			} else if (STEP_OVER.equals(reason)) {
				details = DebugEvent.STEP_OVER;
			} else if (STEP_RETURN.equals(reason) || STEP_TO_FRAME.equals(reason)) {
				details = DebugEvent.STEP_RETURN;
			} else if (RESUME.equals(reason)) {
				details = DebugEvent.CLIENT_REQUEST;
				runningState = STATE_RUNNING;
			} else if (ABORT.equals(reason) || START.equals(reason)) {
				runningState = STATE_RUNNING;
			} else {
				runningState = STATE_RUNNING;
			}
			if (reason.startsWith(STEP)) {
				/* top-level frame */
				if (stackFrames != null && stackFrames.length == 1) {
					details = DebugEvent.CLIENT_REQUEST;
				}
			}
			fireChangeEvent(DebugEvent.STATE);
			fireResumeEvent(details);
		}
	}

	/**
	 * breakpointHit
	 * 
	 * @param filename
	 * @param lineNumber
	 */
	private void breakpointHit(String filename, int lineNumber) {
		IBreakpoint breakpoint = ((JSDebugTarget) getDebugTarget()).findBreakpointAt(filename, lineNumber);
		if (breakpoint != null && breakpoint instanceof IJSLineBreakpoint) {
			try {
				if (((IJSLineBreakpoint) breakpoint).getHitCount() > 0) {
					breakpoint.setEnabled(false);
				}
			} catch (CoreException e) {
				JSDebugPlugin.log(e);
			}
		}
		breakpoints = breakpoint != null ? new IBreakpoint[] { breakpoint } : null;
		// TODO: where to find runToLine breakpoint ?
	}

	/**
	 * implicitBreakpointHit
	 * 
	 * @param filename
	 * @param line
	 * @param type
	 */
	private void implicitBreakpointHit(String filename, int line, int type) {
		/* TODO: use project-related path ? */
		breakpoints = new IBreakpoint[] { new JSDebugImplicitBreakpoint(filename, line, type) };
	}

	/**
	 * getStackFrames0
	 * 
	 * @throws DebugException
	 */
	private synchronized void getStackFrames0() throws DebugException {
		if (!validateFrames && stackFrames != null) {
			return;
		}
		String[] args = ((JSDebugTarget) getDebugTarget()).getConnection().sendCommandAndWait(FRAMES);
		if (args != null) {
			Vector<IStackFrame> frames = new Vector<IStackFrame>();
			JSDebugTarget target = (JSDebugTarget) getDebugTarget();
			int frameIndex = (stackFrames != null) ? stackFrames.length - 1 : -1;
			for (int i = args.length - 1; i >= 1; --i) {
				int j = 0;
				String[] subargs = args[i].split(SUBARGS_SPLIT);
				int depth = Integer.parseInt(subargs[j++]);
				String function = Util.decodeData(subargs[j++]);
				String arguments = Util.decodeData(subargs[j++]);
				if (function.length() == 0) {
					function = MessageFormat.format("[{0}]", //$NON-NLS-1$
							i == args.length - 1 ? Messages.JSDebugTarget_TopLevelScript
									: Messages.JSDebugTarget_EvalScript);
				} else {
					function += MessageFormat.format("({0})", arguments); //$NON-NLS-1$
				}
				String sourceFile = target.resolveSourceFile(Util.decodeData(subargs[j++]));
				int sourceLine = Integer.parseInt(subargs[j++]);
				++j; /* skip native flag */
				long pc = Long.parseLong(subargs[j++]);
				int scriptTag = Integer.parseInt(subargs[j++]);
				IStackFrame frame = null;
				if (frameIndex >= 0) {
					JSDebugStackFrame oldFrame = (JSDebugStackFrame) stackFrames[frameIndex];
					if ((oldFrame.scriptTag == scriptTag) && (oldFrame.pc == pc)) {
						/* update depth */
						oldFrame.invalidate(depth, sourceLine, pc);
						frame = oldFrame;
						--frameIndex;
					} else if (oldFrame.scriptTag == scriptTag) {
						/* update line/pc */
						oldFrame.invalidate(depth, sourceLine, pc);
						frameIndex = -1;
					} else {
						frameIndex = -1;
					}
				}
				if (frame == null) {
					frame = new JSDebugStackFrame(target, this, depth, function, sourceFile, sourceLine, pc, scriptTag);
				}
				frames.add(0, frame);
			}
			stackFrames = (IStackFrame[]) frames.toArray(new IStackFrame[frames.size()]);
			validateFrames = false;
		}
	}

	/**
	 * invalidateStackFrames
	 */
	private void invalidateStackFrames() {
		if (stackFrames != null) {
			for (int i = 0; i < stackFrames.length; ++i) {
				((JSDebugStackFrame) stackFrames[i]).invalidate();
			}
		}
		validateFrames = true;
	}

	/**
	 * stepToFrame
	 * 
	 * @param frame
	 * @throws DebugException
	 */
	protected void stepToFrame(IStackFrame frame) throws DebugException {
		if (runningState != STATE_SUSPENDED) {
			return;
		}
		int targetFrameId = ((JSDebugStackFrame) frame).getFrameId();
		runningState = STATE_STEPPPING;
		fireChangeEvent(DebugEvent.STATE);
		((JSDebugTarget) getDebugTarget()).getConnection().sendCommand(
				MessageFormat.format(STEP_TO_FRAME_0, targetFrameId));
	}
}
