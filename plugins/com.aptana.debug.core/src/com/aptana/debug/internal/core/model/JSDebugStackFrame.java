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

	private IThread thread;
	private int frameId;
	private String function;
	private String sourceFile;
	private int sourceLine = -1;
	private IVariable[] variables;

	/**
	 * pc
	 */
	protected long pc;

	/**
	 * scriptTag
	 */
	protected int scriptTag;

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
	public JSDebugStackFrame(IDebugTarget target, IThread thread, int frameId, String function, String sourceFile,
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

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		if (IJSStackFrame.class == adapter) {
			return this;
		}
		return super.getAdapter(adapter);
	}

	/**
	 * @see org.eclipse.debug.core.model.IStackFrame#getVariables()
	 */
	public IVariable[] getVariables() throws DebugException {
		getVariables0();
		return variables != null ? variables : new IVariable[0];
	}

	/**
	 * @see org.eclipse.debug.core.model.IStackFrame#hasVariables()
	 */
	public boolean hasVariables() throws DebugException {
		getVariables0();
		return variables != null && variables.length > 0;
	}

	/**
	 * @see org.eclipse.debug.core.model.IStackFrame#getThread()
	 */
	public IThread getThread() {
		return thread;
	}

	/**
	 * @see org.eclipse.debug.core.model.IStackFrame#getLineNumber()
	 */
	public int getLineNumber() throws DebugException {
		return sourceLine;
	}

	/**
	 * @see org.eclipse.debug.core.model.IStackFrame#getCharStart()
	 */
	public int getCharStart() throws DebugException {
		return -1;
	}

	/**
	 * @see org.eclipse.debug.core.model.IStackFrame#getCharEnd()
	 */
	public int getCharEnd() throws DebugException {
		return -1;
	}

	/**
	 * @see org.eclipse.debug.core.model.IStackFrame#getName()
	 */
	public String getName() throws DebugException {
		return function;
	}

	/**
	 * @see org.eclipse.debug.core.model.IStackFrame#getRegisterGroups()
	 */
	public IRegisterGroup[] getRegisterGroups() throws DebugException {
		return new IRegisterGroup[0];
	}

	/**
	 * @see org.eclipse.debug.core.model.IStackFrame#hasRegisterGroups()
	 */
	public boolean hasRegisterGroups() throws DebugException {
		return false;
	}

	/**
	 * @see org.eclipse.debug.core.model.IStep#canStepInto()
	 */
	public boolean canStepInto() {
		return thread.canStepInto() && isTopStackFrame();
	}

	/**
	 * @see org.eclipse.debug.core.model.IStep#canStepOver()
	 */
	public boolean canStepOver() {
		return thread.canStepOver();
	}

	/**
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

	/**
	 * @see org.eclipse.debug.core.model.IStep#isStepping()
	 */
	public boolean isStepping() {
		return thread.isStepping();
	}

	/**
	 * @see org.eclipse.debug.core.model.IStep#stepInto()
	 */
	public void stepInto() throws DebugException {
		if (!canStepInto()) {
			return;
		}
		thread.stepInto();
	}

	/**
	 * @see org.eclipse.debug.core.model.IStep#stepOver()
	 */
	public void stepOver() throws DebugException {
		if (!canStepOver() || !isValid()) {
			return;
		}
		if (isTopStackFrame()) {
			thread.stepOver();
		} else {
			((JSDebugThread) thread).stepToFrame(this);
		}
	}

	/**
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
					((JSDebugThread) thread).stepToFrame(frames[i + 1]);
				}
			}
		}
		/** TODO: disable steps for top-level frames ? */
	}

	/**
	 * @see org.eclipse.debug.core.model.ISuspendResume#canResume()
	 */
	public boolean canResume() {
		return thread.canResume();
	}

	/**
	 * @see org.eclipse.debug.core.model.ISuspendResume#canSuspend()
	 */
	public boolean canSuspend() {
		return thread.canSuspend();
	}

	/**
	 * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
	 */
	public boolean isSuspended() {
		return thread.isSuspended();
	}

	/**
	 * @see org.eclipse.debug.core.model.ISuspendResume#resume()
	 */
	public void resume() throws DebugException {
		thread.resume();
	}

	/**
	 * @see org.eclipse.debug.core.model.ISuspendResume#suspend()
	 */
	public void suspend() throws DebugException {
		thread.suspend();
	}

	/**
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	public boolean canTerminate() {
		return thread.canTerminate();
	}

	/**
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	public boolean isTerminated() {
		return thread.isTerminated();
	}

	/**
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	public void terminate() throws DebugException {
		thread.terminate();
	}

	/**
	 * @see com.aptana.js.debug.core.model.IJSStackFrame#getSourceFileName()
	 */
	public String getSourceFileName() {
		return sourceFile;
	}

	/**
	 * getVariables0
	 * 
	 * @throws DebugException
	 */
	private void getVariables0() throws DebugException {
		if (variables != null || !isValid()) {
			return;
		}
		variables = ((JSDebugTarget) getDebugTarget()).loadVariables(MessageFormat.format("frame[{0}]", frameId)); //$NON-NLS-1$
		for (int i = 0; i < variables.length; ++i) {
			((JSDebugVariable) variables[i]).flags |= JSDebugVariable.FLAGS_TOPLEVEL;
		}
	}

	/**
	 * getFrameId
	 * 
	 * @return int
	 */
	protected int getFrameId() {
		return frameId;
	}

	/**
	 * isTopStackFrame
	 * 
	 * @return boolean
	 */
	private boolean isTopStackFrame() {
		IStackFrame tos = null;
		try {
			tos = thread.getTopStackFrame();
		} catch (DebugException e) {
		}
		return tos != null && tos.equals(this);
	}

	/**
	 * @see com.aptana.js.debug.core.model.IJSStackFrame#findVariable(java.lang.String)
	 */
	public IVariable findVariable(String variableName) throws DebugException {
		IVariable[] vars = getVariables();
		for (int i = 0; i < vars.length; ++i) {
			if (vars[i].getName().equals(variableName)) {
				return vars[i];
			}
		}
		return ((JSDebugTarget) getDebugTarget()).findVariable(variableName, this);
	}

	/**
	 * isSameAs
	 * 
	 * @param other
	 * @return boolean
	 */
	protected boolean isSameAs(JSDebugStackFrame other) {
		return (scriptTag == other.scriptTag) && (pc == other.pc) && (sourceLine == other.sourceLine);
	}

	/**
	 * isValid
	 * 
	 * @return boolean
	 */
	private boolean isValid() {
		return frameId >= 0;
	}

	/**
	 * invalidate
	 */
	protected void invalidate() {
		frameId = -1;
	}

	/**
	 * invalidate
	 * 
	 * @param frameId
	 * @param sourceLine
	 * @param pc
	 */
	protected void invalidate(int frameId, int sourceLine, long pc) {
		this.frameId = frameId;
		this.sourceLine = sourceLine;
		this.pc = pc;
		variables = null;
	}
}
