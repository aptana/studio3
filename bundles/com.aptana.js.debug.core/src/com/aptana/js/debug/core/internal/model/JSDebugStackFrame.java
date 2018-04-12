/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.internal.model;

import java.net.URI;
import java.text.MessageFormat;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;

import com.aptana.js.debug.core.model.IJSStackFrame;

/**
 * @author Max Stepanov
 */
public class JSDebugStackFrame extends JSDebugElement implements IJSStackFrame {

	private static final String FRAME_0 = "frame[{0,number,integer}]"; //$NON-NLS-1$

	private final JSDebugThread thread;
	private int frameId;
	private final String function;
	private final URI sourceFile;
	private int sourceLine;
	private IVariable[] variables;

	/* package */long pc;
	/* package */int scriptTag;

	/**
	 * JSDebugStackFrame
	 * 
	 * @param target
	 * @param thread
	 * @param frameId
	 * @param function
	 * @param sourceFile
	 * @param sourceLine
	 * @param pc
	 * @param scriptTag
	 */
	public JSDebugStackFrame(IDebugTarget target, JSDebugThread thread, int frameId, String function, URI sourceFile,
			int sourceLine, long pc, int scriptTag) {
		super(target);
		this.thread = thread;
		this.frameId = frameId;
		this.function = function;
		this.sourceFile = sourceFile;
		this.sourceLine = sourceLine;
		this.pc = pc;
		this.scriptTag = scriptTag;
	}

	/*
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		if (IJSStackFrame.class == adapter) {
			return this;
		}
		return super.getAdapter(adapter);
	}

	/*
	 * @see org.eclipse.debug.core.model.IStackFrame#getVariables()
	 */
	public IVariable[] getVariables() throws DebugException {
		getVariables0();
		return variables != null ? variables : new IVariable[0];
	}

	/*
	 * @see org.eclipse.debug.core.model.IStackFrame#hasVariables()
	 */
	public boolean hasVariables() throws DebugException {
		getVariables0();
		return variables != null && variables.length > 0;
	}

	/*
	 * @see org.eclipse.debug.core.model.IStackFrame#getThread()
	 */
	public IThread getThread() {
		return thread;
	}

	/*
	 * @see org.eclipse.debug.core.model.IStackFrame#getLineNumber()
	 */
	public int getLineNumber() throws DebugException {
		return sourceLine;
	}

	/*
	 * @see org.eclipse.debug.core.model.IStackFrame#getCharStart()
	 */
	public int getCharStart() throws DebugException {
		return -1;
	}

	/*
	 * @see org.eclipse.debug.core.model.IStackFrame#getCharEnd()
	 */
	public int getCharEnd() throws DebugException {
		return -1;
	}

	/*
	 * @see org.eclipse.debug.core.model.IStackFrame#getName()
	 */
	public String getName() throws DebugException {
		return function;
	}

	/*
	 * @see org.eclipse.debug.core.model.IStackFrame#getRegisterGroups()
	 */
	public IRegisterGroup[] getRegisterGroups() throws DebugException {
		return new IRegisterGroup[0];
	}

	/*
	 * @see org.eclipse.debug.core.model.IStackFrame#hasRegisterGroups()
	 */
	public boolean hasRegisterGroups() throws DebugException {
		return false;
	}

	/*
	 * @see org.eclipse.debug.core.model.IStep#canStepInto()
	 */
	public boolean canStepInto() {
		return thread.canStepInto() && isTopStackFrame();
	}

	/*
	 * @see org.eclipse.debug.core.model.IStep#canStepOver()
	 */
	public boolean canStepOver() {
		return thread.canStepOver();
	}

	/*
	 * @see org.eclipse.debug.core.model.IStep#canStepReturn()
	 */
	public boolean canStepReturn() {
		if (!thread.canStepReturn()) {
			return false;
		}
		IStackFrame bottomFrame = null;
		try {
			IStackFrame[] frames = thread.getStackFrames();
			if (frames.length > 0) {
				bottomFrame = frames[frames.length - 1];
			}
		} catch (DebugException e) {
		}
		return bottomFrame != null && !bottomFrame.equals(this);

	}

	/*
	 * @see org.eclipse.debug.core.model.IStep#isStepping()
	 */
	public boolean isStepping() {
		return thread.isStepping();
	}

	/*
	 * @see org.eclipse.debug.core.model.IStep#stepInto()
	 */
	public void stepInto() throws DebugException {
		if (!canStepInto()) {
			return;
		}
		thread.stepInto();
	}

	/*
	 * @see org.eclipse.debug.core.model.IStep#stepOver()
	 */
	public void stepOver() throws DebugException {
		if (!canStepOver() || !isValid()) {
			return;
		}
		if (isTopStackFrame()) {
			thread.stepOver();
		} else {
			thread.stepToFrame(this);
		}
	}

	/*
	 * @see org.eclipse.debug.core.model.IStep#stepReturn()
	 */
	public void stepReturn() throws DebugException {
		if (!canStepReturn() || !isValid()) {
			return;
		}
		if (isTopStackFrame()) {
			thread.stepReturn();
		} else {
			IStackFrame[] frames = thread.getStackFrames();
			for (int i = frames.length - 2; i > 0; --i) {
				if (frames[i].equals(this)) {
					thread.stepToFrame(frames[i + 1]);
				}
			}
		}
		// TODO: disable steps for top-level frames ?
	}

	/*
	 * @see org.eclipse.debug.core.model.ISuspendResume#canResume()
	 */
	public boolean canResume() {
		return thread.canResume();
	}

	/*
	 * @see org.eclipse.debug.core.model.ISuspendResume#canSuspend()
	 */
	public boolean canSuspend() {
		return thread.canSuspend();
	}

	/*
	 * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
	 */
	public boolean isSuspended() {
		return thread.isSuspended();
	}

	/*
	 * @see org.eclipse.debug.core.model.ISuspendResume#resume()
	 */
	public void resume() throws DebugException {
		thread.resume();
	}

	/*
	 * @see org.eclipse.debug.core.model.ISuspendResume#suspend()
	 */
	public void suspend() throws DebugException {
		thread.suspend();
	}

	/*
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	public boolean canTerminate() {
		return thread.canTerminate();
	}

	/*
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	public boolean isTerminated() {
		return thread.isTerminated();
	}

	/*
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	public void terminate() throws DebugException {
		thread.terminate();
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSStackFrame#getSourceFileName()
	 */
	public URI getSourceFileName() {
		return sourceFile;
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSStackFrame#findVariable(java.lang.String)
	 */
	public IVariable findVariable(String variableName) throws DebugException {
		for (IVariable var : getVariables()) {
			if (var.getName().equals(variableName)) {
				return var;
			}
		}
		return getJSDebugTarget().findVariable(variableName, this);
	}

	/* package */int getFrameId() {
		return frameId;
	}

	/* package */boolean isSameAs(JSDebugStackFrame other) {
		return (scriptTag == other.scriptTag) && (pc == other.pc) && (sourceLine == other.sourceLine);
	}

	/* package */void invalidate() {
		frameId = -1;
	}

	/* package */void invalidate(int frameId, int sourceLine, long pc) {
		this.frameId = frameId;
		this.sourceLine = sourceLine;
		this.pc = pc;
		variables = null;
	}

	/* package */String getThreadId() {
		return thread.getThreadId();
	}

	private JSDebugTarget getJSDebugTarget() {
		return (JSDebugTarget) getDebugTarget();
	}

	private boolean isTopStackFrame() {
		IStackFrame tos = null;
		try {
			tos = thread.getTopStackFrame();
		} catch (DebugException e) {
		}
		return tos != null && tos.equals(this);
	}

	private void getVariables0() throws DebugException {
		if (variables != null || !isValid()) {
			return;
		}
		JSDebugTarget target = getJSDebugTarget();
		String command = MessageFormat.format(FRAME_0, frameId);
		variables = target.loadVariables(thread.getThreadId(), command);
		for (IVariable var : variables) {
			((JSDebugVariable) var).flags |= JSDebugVariable.FLAGS_TOPLEVEL;
		}
	}

	private boolean isValid() {
		return frameId >= 0;
	}

}
