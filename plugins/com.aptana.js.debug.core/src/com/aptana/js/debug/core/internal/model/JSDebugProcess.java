/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.internal.model;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
import com.aptana.js.debug.core.JSDebugPlugin;
import com.aptana.js.debug.core.internal.StreamsProxy;
import com.aptana.js.debug.core.model.xhr.IXHRService;

/**
 * @author Max Stepanov
 */
public final class JSDebugProcess extends PlatformObject implements IProcess {

	private static final int MAX_WAIT_FOR_DEATH_ATTEMPTS = 10;
	private static final int TIME_TO_WAIT_FOR_THREAD_DEATH = 500; // ms

	private final ILaunch launch;
	private final String label;
	private final Process process;
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
	public JSDebugProcess(ILaunch launch, String label, Map<String, Object> attributes) {
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
	public JSDebugProcess(ILaunch launch, Process process, String label, Map<String, Object> attributes) {
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
	public JSDebugProcess(ILaunch launch, Process process, boolean killProcessOnTerminate, String label, Map<String, Object> attributes) {
		this.launch = launch;
		this.process = process;
		this.killProcessOnTerminate = killProcessOnTerminate;
		this.label = label;
		initializeAttributes(attributes);
		out = new PipedOutputStream();
		err = new PipedOutputStream();
		launch.addProcess(this);
		fireCreationEvent();
		DebugPlugin.getDefault().addDebugEventListener(new DebugEventSetListener());
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

	/*
	 * @see org.eclipse.debug.core.model.IProcess#getLabel()
	 */
	public String getLabel() {
		return label;
	}

	/*
	 * @see org.eclipse.debug.core.model.IProcess#getLaunch()
	 */
	public ILaunch getLaunch() {
		return launch;
	}

	/*
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

	/*
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

	/*
	 * @see org.eclipse.debug.core.model.IProcess#getAttribute(java.lang.String)
	 */
	public String getAttribute(String key) {
		if (fAttributes == null) {
			return null;
		}
		return fAttributes.get(key);
	}

	/*
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

	/*
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	public boolean canTerminate() {
		return !isTerminated();
	}

	/*
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

	/*
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

	/*
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		if (adapter == IProcess.class) {
			return this;
		} else if (adapter == IDebugTarget.class) {
			ILaunch launch = getLaunch();
			for (IDebugTarget target : launch.getDebugTargets()) {
				if (this.equals(target.getProcess())) {
					return target;
				}
			}
			return null;
		} else if (adapter == IXHRService.class) {
			return xhrService;
		}
		return super.getAdapter(adapter);
	}

	/* package */ OutputStream getOutputStream() {
		return out;
	}

	/* package */ OutputStream getErrorStream() {
		return err;
	}

	/* package */ void setDebugTarget(IDebugTarget debugTarget) {
		this.debugTarget = debugTarget;
	}

	/* package */ void setXHRService(IXHRService xhrService) {
		this.xhrService = xhrService;
	}


	private void fireCreationEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.CREATE));
	}

	private void fireTerminateEvent() {
		if (DebugPlugin.getDefault() != null) {
			fireEvent(new DebugEvent(this, DebugEvent.TERMINATE));
		}
	}

	private void fireChangeEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.CHANGE));
	}

	private void fireEvent(DebugEvent event) {
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { event });
	}

	private void throwDebugException(Exception exception) throws DebugException {
		throw new DebugException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, DebugException.REQUEST_FAILED,
				StringUtil.EMPTY, exception));
	}

	private void initializeAttributes(Map<String, Object> attributes) {
		setAttribute(IProcess.ATTR_PROCESS_TYPE, "javascript"); //$NON-NLS-1$
		if (attributes != null) {
			for (Entry<String, Object> entry : attributes.entrySet()) {
				setAttribute(entry.getKey(), String.valueOf(entry.getValue()));
			}
		}
	}

	private void terminateProcess() throws DebugException {
		if (process != null && !processTerminated && killProcessOnTerminate) {
			process.destroy();
			for (int attempts = 0; attempts < MAX_WAIT_FOR_DEATH_ATTEMPTS; ++attempts) {
				try {
					process.exitValue(); // throws exception if process not exited
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

	private IDebugTarget getDebugTarget() {
		if (debugTarget == null) {
			debugTarget = launch.getDebugTarget();
		}
		return debugTarget;
	}

	
	private class DebugEventSetListener implements IDebugEventSetListener {

		public void handleDebugEvents(DebugEvent[] events) {
			for (DebugEvent event : events) {
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
		
	}
	
	private class ProcessMonitorThread extends Thread {

		public ProcessMonitorThread() {
			super();
			setDaemon(true);
		}

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
