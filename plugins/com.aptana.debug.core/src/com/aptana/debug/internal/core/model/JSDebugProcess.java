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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;

import com.aptana.core.util.StringUtil;
import com.aptana.debug.core.JSDebugPlugin;
import com.aptana.debug.core.xhr.IXHRService;

/**
 * @author Max Stepanov
 */
public class JSDebugProcess extends PlatformObject implements IProcess, IDebugEventSetListener {

	private static final int MAX_WAIT_FOR_DEATH_ATTEMPTS = 10;
	private static final int TIME_TO_WAIT_FOR_THREAD_DEATH = 500; // ms

	private ILaunch launch;
	private String label;
	private Process process;
	private boolean killProcessOnTerminate;
	private volatile boolean processTerminated;
	private ProcessMonitorThread processMonitorThread;
	private IStreamsProxy streamsProxy;
	private PipedOutputStream out;
	private PipedOutputStream err;
	private IDebugTarget debugTarget;
	private IXHRService xhrService;
	private Map<String, String> fAttributes;

	/**
	 * JSDebugProcess
	 * 
	 * @param launch
	 * @param label
	 * @param attributes
	 */
	@SuppressWarnings("rawtypes")
	public JSDebugProcess(ILaunch launch, String label, Map attributes) {
		this(launch, null, false, label, attributes);
	}

	/**
	 * JSDebugProcess
	 * 
	 * @param launch
	 * @param process
	 * @param label
	 * @param attributes
	 */
	@SuppressWarnings("rawtypes")
	public JSDebugProcess(ILaunch launch, Process process, String label, Map attributes) {
		this(launch, process, true, label, attributes);
	}

	/**
	 * JSDebugProcess
	 * 
	 * @param launch
	 * @param process
	 * @param killProcessOnTerminate
	 * @param label
	 * @param attributes
	 */
	@SuppressWarnings("rawtypes")
	public JSDebugProcess(ILaunch launch, Process process, boolean killProcessOnTerminate, String label, Map attributes) {
		this.launch = launch;
		this.process = process;
		this.killProcessOnTerminate = killProcessOnTerminate;
		this.label = label;
		initializeAttributes(attributes);
		out = new PipedOutputStream();
		err = new PipedOutputStream();
		launch.addProcess(this);
		fireCreationEvent();
		DebugPlugin.getDefault().addDebugEventListener(this);
		if (process != null) {
			try {
				process.exitValue();
				processTerminated = true;
			} catch (IllegalThreadStateException e) {
			}
			if (processTerminated) {
				fireTerminateEvent();
			} else {
				processMonitorThread = new ProcessMonitorThread();
				processMonitorThread.start();
			}
		}
	}

	/**
	 * initializeAttributes
	 * 
	 * @param attributes
	 */
	@SuppressWarnings("rawtypes")
	private void initializeAttributes(Map attributes) {
		setAttribute(IProcess.ATTR_PROCESS_TYPE, "javascript"); //$NON-NLS-1$
		if (attributes != null) {
			for (Object key : attributes.keySet()) {
				setAttribute((String) key, (String) attributes.get(key));
			}
		}
	}

	/**
	 * @see org.eclipse.debug.core.model.IProcess#getLabel()
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @see org.eclipse.debug.core.model.IProcess#getLaunch()
	 */
	public ILaunch getLaunch() {
		return launch;
	}

	/**
	 * @see org.eclipse.debug.core.model.IProcess#getStreamsProxy()
	 */
	public IStreamsProxy getStreamsProxy() {
		if (streamsProxy == null) {
			try {
				streamsProxy = new StreamsProxy(new PipedInputStream(out), new PipedInputStream(err));
			} catch (IOException e) {
				JSDebugPlugin.log(e);
			}
		}
		return streamsProxy;
	}

	/**
	 * @see org.eclipse.debug.core.model.IProcess#setAttribute(java.lang.String,
	 *      java.lang.String)
	 */
	public void setAttribute(String key, String value) {
		if (fAttributes == null) {
			fAttributes = new HashMap<String, String>(5);
		}
		Object origVal = fAttributes.get(key);
		if (origVal != null && origVal.equals(value)) {
			return; // nothing changed.
		}

		fAttributes.put(key, value);
		fireChangeEvent();
	}

	/**
	 * @see org.eclipse.debug.core.model.IProcess#getAttribute(java.lang.String)
	 */
	public String getAttribute(String key) {
		if (fAttributes == null) {
			return null;
		}
		return (String) fAttributes.get(key);
	}

	/**
	 * @see org.eclipse.debug.core.model.IProcess#getExitValue()
	 */
	public int getExitValue() throws DebugException {
		if (!isTerminated()) {
			throwDebugException(null);
		}
		if (process != null) {
			return process.exitValue();
		}
		return -1;
	}

	/**
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	public boolean canTerminate() {
		return !isTerminated();
	}

	/**
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	public boolean isTerminated() {
		if (process != null) {
			return processTerminated;
		}
		IDebugTarget target = getDebugTarget();
		if (target != null) {
			return target.isTerminated();
		}
		return false;
	}

	/**
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	public void terminate() throws DebugException {
		if (!isTerminated()) {
			IDebugTarget target = getDebugTarget();
			if (target != null) {
				target.terminate();
			}
			terminateProcess();
		}
	}

	private void terminateProcess() throws DebugException {
		if (process != null && !processTerminated && killProcessOnTerminate) {
			process.destroy();
			for (int attempts = 0; attempts < MAX_WAIT_FOR_DEATH_ATTEMPTS; ++attempts) {
				try {
					process.exitValue(); // throws exception if process not
					// exited
					processTerminated = true;
					processMonitorThread.interrupt();
					return;
				} catch (IllegalThreadStateException ie) {
				}
				try {
					Thread.sleep(TIME_TO_WAIT_FOR_THREAD_DEATH);
				} catch (InterruptedException e) {
				}
			}
			throw new DebugException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, DebugException.TARGET_REQUEST_FAILED,
					Messages.JSDebugProcess_Terminate_Failed, null));
		}
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		if (adapter == IProcess.class) {
			return this;
		} else if (adapter == IDebugTarget.class) {
			ILaunch launch = getLaunch();
			IDebugTarget[] targets = launch.getDebugTargets();
			for (int i = 0; i < targets.length; i++) {
				if (this.equals(targets[i].getProcess())) {
					return targets[i];
				}
			}
			return null;
		} else if (adapter == IXHRService.class) {
			return xhrService;
		}
		return super.getAdapter(adapter);
	}

	/**
	 * fireCreationEvent
	 */
	protected void fireCreationEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.CREATE));
	}

	/**
	 * fireTerminateEvent
	 */
	protected void fireTerminateEvent() {
		if (DebugPlugin.getDefault() != null) {
			fireEvent(new DebugEvent(this, DebugEvent.TERMINATE));
		}
	}

	/**
	 * fireChangeEvent
	 */
	protected void fireChangeEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.CHANGE));
	}

	/**
	 * fireEvent
	 * 
	 * @param event
	 */
	protected void fireEvent(DebugEvent event) {
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { event });
	}

	/**
	 * throwDebugException
	 * 
	 * @param exception
	 * @throws DebugException
	 */
	protected void throwDebugException(Exception exception) throws DebugException {
		throw new DebugException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, DebugException.REQUEST_FAILED,
				StringUtil.EMPTY, exception));
	}

	/**
	 * closeStreams
	 */
	private void closeStreams() {
		try {
			out.close();
		} catch (IOException ignore) {
		}
		try {
			err.close();
		} catch (IOException ignore) {
		}
		out = null;
		err = null;
	}

	/**
	 * getOutputStream
	 * 
	 * @return OutputStream
	 */
	public OutputStream getOutputStream() {
		return out;
	}

	/**
	 * getErrorStream
	 * 
	 * @return OutputStream
	 */
	public OutputStream getErrorStream() {
		return err;
	}

	/**
	 * setDebugTarget
	 * 
	 * @param debugTarget
	 */
	protected void setDebugTarget(IDebugTarget debugTarget) {
		this.debugTarget = debugTarget;
	}

	/**
	 * getDebugTarget
	 * 
	 * @return IDebugTarget
	 */
	private IDebugTarget getDebugTarget() {
		if (debugTarget == null) {
			debugTarget = launch.getDebugTarget();
		}
		return debugTarget;
	}

	/**
	 * setXHRService
	 * 
	 * @param xhrService
	 */
	protected void setXHRService(IXHRService xhrService) {
		this.xhrService = xhrService;
	}

	/**
	 * @see org.eclipse.debug.core.IDebugEventSetListener#handleDebugEvents(org.eclipse.debug.core.DebugEvent[])
	 */
	public void handleDebugEvents(DebugEvent[] events) {
		for (int i = 0; i < events.length; i++) {
			DebugEvent event = events[i];
			switch (event.getKind()) {
			case DebugEvent.TERMINATE: {
				Object source = event.getSource();
				if (source.equals(getDebugTarget())) {
					closeStreams();
					fireTerminateEvent();
					DebugPlugin.getDefault().removeDebugEventListener(this);
					try {
						terminateProcess();
					} catch (DebugException e) {
						JSDebugPlugin.log(e);
					}
				}
				break;
			}
			default:
			}
		}
	}

	private class ProcessMonitorThread extends Thread {

		public ProcessMonitorThread() {
			super();
			setDaemon(true);
		}

		/**
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			while (!processTerminated) {
				try {
					process.waitFor();
				} catch (InterruptedException e) {
					Thread.interrupted(); // clear interrupted state
				} finally {
					processTerminated = true;
					fireTerminateEvent();
				}
			}
		}

	}
}
