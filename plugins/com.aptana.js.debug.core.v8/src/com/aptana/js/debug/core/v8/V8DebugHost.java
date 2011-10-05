/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.js.debug.core.v8;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.chromium.sdk.BrowserFactory;
import org.chromium.sdk.CallFrame;
import org.chromium.sdk.DebugContext;
import org.chromium.sdk.DebugContext.ContinueCallback;
import org.chromium.sdk.DebugContext.State;
import org.chromium.sdk.DebugContext.StepAction;
import org.chromium.sdk.DebugEventListener;
import org.chromium.sdk.ExceptionData;
import org.chromium.sdk.JavascriptVm.ScriptsCallback;
import org.chromium.sdk.Script;
import org.chromium.sdk.Script.Type;
import org.chromium.sdk.StandaloneVm;
import org.chromium.sdk.TextStreamPosition;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;

import com.aptana.core.util.StringUtil;
import com.aptana.js.debug.core.internal.Util;
import com.aptana.js.debug.core.internal.model.AbstractDebugHost;

/**
 * @author Max Stepanov
 */
public class V8DebugHost extends AbstractDebugHost {

	private enum EventType {
		SUSPENDED,
	}

	private final SocketAddress v8SocketAddress;

	private StandaloneVm vm;
	private DebugEventListener debugEventListener;
	private DebugContext currentContext;
	private List<? extends CallFrame> currentFrames;

	private Queue<EventType> eventQueue = new LinkedList<EventType>();
	private final Callback callback = new Callback();
	
	private Map<String, String> detailFormatters = new HashMap<String, String>();
	private Map<String, Script> loadedScripts = new HashMap<String, Script>();
	private Map<String, Integer> scriptFunctionIds = new HashMap<String, Integer>();
	private int nextScriptFunctionId = 1;

	public static V8DebugHost createDebugHost(int port) throws CoreException {
		V8DebugHost debugHost = new V8DebugHost(new InetSocketAddress("127.0.0.1", port));
		return debugHost;
	}

	/**
	 * 
	 */
	protected V8DebugHost(SocketAddress v8SocketAddress) {
		super();
		this.v8SocketAddress = v8SocketAddress;
		debugEventListener = new DebugEventListener() {
			public void suspended(DebugContext context) {
				if (context.getState() == State.EXCEPTION) {
					if (!suspendOnException(context.getExceptionData())) {
						context.continueVm(StepAction.CONTINUE, 0, null);
						return;
					}
				}
				synchronized (eventQueue) {
					currentContext = context;
					eventQueue.add(EventType.SUSPENDED);
					eventQueue.notify();
				}
			}

			public void scriptLoaded(Script newScript) {
				synchronized (loadedScripts) {
					addScript(newScript);					
				}
			}

			public void scriptContentChanged(Script newScript) {
				synchronized (loadedScripts) {
					removeScript(newScript);
					addScript(newScript);					
				}
			}

			public void scriptCollected(Script script) {
				synchronized (loadedScripts) {
					removeScript(script);					
				}
			}

			public void resumed() {
				currentContext = null;
			}

			public VmStatusListener getVmStatusListener() {
				return null;
			}

			public void disconnected() {
				System.out.println("disconnected");
			}
		};
	}

	/* (non-Javadoc)
	 * @see com.aptana.js.debug.core.internal.model.AbstractDebugHost#getSyncObject()
	 */
	@Override
	protected Object getSyncObject() {
		return eventQueue;
	}

	/* (non-Javadoc)
	 * @see com.aptana.js.debug.core.internal.model.AbstractDebugHost#isConnected()
	 */
	@Override
	protected boolean isConnected() {
		return vm != null && vm.isAttached();
	}

	/* (non-Javadoc)
	 * @see com.aptana.js.debug.core.internal.model.AbstractDebugHost#isDebugging()
	 */
	@Override
	protected boolean isDebugging() {
		return currentContext != null;
	}

	/* (non-Javadoc)
	 * @see com.aptana.js.debug.core.internal.model.AbstractDebugHost#startDebugging()
	 */
	@Override
	protected void startDebugging() throws IOException {
		currentFrames = currentContext.getCallFrames();
		if (currentFrames == null || currentFrames.isEmpty()) {
			logError("startDebugging(frames is empty)");
			continueVm(StepAction.CONTINUE, 0);
			return;
		}
		if (suspendReason == null) {
			suspendReason = UNDEFINED;
		}
		CallFrame frame = currentFrames.get(0);
		Script script = frame.getScript();
		String fileName = makeAbsoluteURI(script.getName());
		TextStreamPosition position = frame.getStatementStartPosition();
		if (position == null) {
			logError("startDebugging(position=null)");
			continueVm(StepAction.CONTINUE, 0);
			return;
		}
		sendData(new String[] { SUSPENDED, suspendReason, Util.encodeData(fileName),
				Integer.toString(position.getLine()) });
		suspendReason = null;
	}

	/* (non-Javadoc)
	 * @see com.aptana.js.debug.core.internal.model.AbstractDebugHost#stopDebugging(java.lang.String)
	 */
	@Override
	protected void stopDebugging(String reason) throws IOException {
		if (currentContext == null) {
			return;
		}
		if (reason != null) {
			resumeReason = reason;
		}
		if (vm.isAttached()) {
			if (STEP_INTO.equals(resumeReason)) {
				continueVm(StepAction.IN, 1);
			} else if (STEP_OVER.equals(resumeReason)) {
				continueVm(StepAction.OVER, 1);
			} else if (STEP_RETURN.equals(resumeReason)) {
				continueVm(StepAction.OUT, 1);
			} else if (!ABORT.equals(reason)) {
				continueVm(StepAction.CONTINUE, 0);
			}
		}
		currentFrames = null;
		currentContext = null;
		if (resumeReason == null) {
			resumeReason = UNDEFINED;
			suspendReason = null;
		}
		sendData(new String[] { RESUMED, resumeReason });
		resumeReason = null;
	}

	private void processEvents() throws IOException {
		while (!eventQueue.isEmpty()) {
			switch (eventQueue.poll()) {
				case SUSPENDED:
					handleSuspended();
					break;
				default:
					break;
			}
		}
	}

	private void handleSuspended() throws IOException {
		switch (currentContext.getState()) {
			case NORMAL:
				if (!currentContext.getBreakpointsHit().isEmpty()) {
					suspendReason = BREAKPOINT;
				}
				break;
			case EXCEPTION:
				suspendReason = EXCEPTION;
				break;
		}
		startDebugging();
	}

	private boolean suspendOnException(ExceptionData exception) {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.aptana.js.debug.core.internal.model.AbstractDebugHost#suspend(java.lang.String)
	 */
	@Override
	protected void suspend(String reason) {
		if (currentContext == null) {
			suspendReason = reason;
			vm.suspend(null);
		}			
	}

	/* (non-Javadoc)
	 * @see com.aptana.js.debug.core.internal.model.AbstractDebugHost#listFrames()
	 */
	@Override
	protected String listFrames() {
		if (currentFrames == null) {
			return null;
		}
		String[] framesData = new String[currentFrames.size()];
		for (int i = 0; i < currentFrames.size(); ++i) {
			CallFrame frame = currentFrames.get(i);
			Script script = frame.getScript();
			String fileName = makeAbsoluteURI(script.getName());
			TextStreamPosition position = frame.getStatementStartPosition();
			if (position == null) {
				logError("listFrames(position=null)");
				continue;
			}
			String functionName = frame.getFunctionName();
			if (ANONYMOUS.equals(functionName)) {
				functionName = StringUtil.EMPTY;
			}
			framesData[i] = StringUtil.join(SUBARGS_DELIMITER, new String[] {
					Integer.toString(i), Util.encodeData(functionName), Util.encodeData(listArguments(frame)),
					Util.encodeData(fileName), Integer.toString(position.getLine()),
					Boolean.toString(script.getType() == Type.NATIVE), Long.toString(position.getOffset()), Integer.toString(getScriptFunctionId(script, functionName))
			});
		}
		return StringUtil.join(ARGS_DELIMITER, framesData);
	}

	private String listArguments(CallFrame frame) {
		String[] argsData = new String[0];
//		for (int i = 0; i < args.length; ++i) {
//			argsData[i] = args[i].getName();
//		}
		return StringUtil.join(", ", argsData); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see com.aptana.js.debug.core.internal.model.AbstractDebugHost#processDetailFormatters(java.lang.String[])
	 */
	@Override
	protected String processDetailFormatters(String[] list) {
		detailFormatters.clear();
		for (int i = 0; i < list.length; ++i) {
			String[] df = list[i].split(SUBARGS_SPLIT);
			detailFormatters.put(Util.decodeData(df[0]), Util.decodeData(df[1]));
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.aptana.js.debug.core.internal.model.AbstractDebugHost#processLoadedScripts()
	 */
	@Override
	protected void processLoadedScripts() {
		vm.getScripts(new Callback() {
			public void success(Collection<Script> scripts) {
				synchronized (loadedScripts) {
					for (Script script : scripts) {
						addScript(script);
					}					
				}
			}
		});		
	}
	
	private void addScript(Script script) {
		String fileName = makeAbsoluteURI(script.getName());
		System.out.println("added "+fileName);
		loadedScripts.put(fileName, script);
		String[] scriptsData = listScripts(fileName, script);
		if (scriptsData != null) {
			try {
				sendData(new String[] { SCRIPTS, CREATED, StringUtil.join(ARGS_DELIMITER, scriptsData) });
			} catch (IOException e) {
				logError(e);
			}
		}
	}
	
	private void removeScript(Script script) {
		String fileName = makeAbsoluteURI(script.getName());
		System.out.println("removed "+fileName);
		loadedScripts.remove(fileName);		
	}	

	private String[] listScripts(String fileName, Script script) {
		String[] functions = new String[] { "function" };
		if (functions == null || functions.length == 0) {
			return null;
		}
		String[] scriptsData = new String[functions.length];
		for (int i = 0; i < functions.length; ++i) {
			String functionName = functions[i];
			int lineNo = 1;
			int nlines = 1;
			scriptsData[i] = StringUtil.join(SUBARGS_DELIMITER, new String[] {
					Integer.toString(getScriptFunctionId(script, functionName)), Util.encodeData(fileName), Util.encodeData(functionName),
					Integer.toString(lineNo), Integer.toString(nlines)
			});
		}
		return scriptsData;
	}
	
	private int getScriptFunctionId(Script script, String functionName) {
		String key = Integer.toString(script.getId().hashCode())+':'+functionName;
		Integer id = scriptFunctionIds.get(key);
		if (id == null) {
			id = nextScriptFunctionId++;
			scriptFunctionIds.put(key, id);
		}
		return id;
	}

	/* (non-Javadoc)
	 * @see com.aptana.js.debug.core.internal.model.AbstractDebugHost#getSource(java.lang.String)
	 */
	@Override
	protected String getSource(String uri) {
		Script script = loadedScripts.get(uri);
		if (script != null) {
			String sources = script.getSource();
			return StringUtil.join(ARGS_DELIMITER, new String[] { SUCCESS, Util.encodeData(sources) });
		}
		return FAILURE;
	}
	

	private void continueVm(StepAction stepAction, int stepCount) {
		currentContext.continueVm(stepAction, stepCount, callback);
	}

	/* (non-Javadoc)
	 * @see com.aptana.js.debug.core.internal.model.AbstractDebugHost#initSession()
	 */
	@Override
	protected void initSession() throws CoreException {
		initLogger(V8DebugPlugin.getDefault(), "v8hostdebugger");
		try {
			vm = BrowserFactory.getInstance().createStandalone(v8SocketAddress, null);
			vm.attach(debugEventListener);
			if (vm.isAttached()) {
				new Thread("Aptana: V8 Debug Host") { //$NON-NLS-1$
					public void run() {
						try {
							synchronized (eventQueue) {
								while (vm != null && vm.isAttached()) {
									processEvents();
									eventQueue.wait(500);
								}
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
							/* just quit the loop */
						} catch (Exception e) {
							e.printStackTrace();
							logError(e);
						} finally {
							handleTerminate();
						}
					}
				}.start();
			}
			return;
		} catch (Exception e) {
			logError(e);
			closeLogger();
			throw new CoreException(new Status(Status.ERROR, V8DebugPlugin.PLUGIN_ID, 0,
					"Session initialization failed", e)); //$NON-NLS-1$
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.js.debug.core.internal.model.AbstractDebugHost#terminateSession()
	 */
	@Override
	protected void terminateSession() {
		synchronized(eventQueue) {
			if (currentContext != null) {
				try {
					stopDebugging(ABORT);
				} catch (IOException e) {
					logError(e);
				}
			}
		}
		if (vm != null) {
			vm.detach();
			System.out.println("term reason="+vm.getDisconnectReason());
		}
		closeLogger();
	}

	private class Callback implements ContinueCallback, ScriptsCallback {

		/*
		 * (non-Javadoc)
		 * @see org.chromium.sdk.DebugContext.ContinueCallback#success()
		 */
		public void success() {
		}

		/* (non-Javadoc)
		 * @see org.chromium.sdk.JavascriptVm.ScriptsCallback#success(java.util.Collection)
		 */
		public void success(Collection<Script> scripts) {
		}

		/*
		 * (non-Javadoc)
		 * @see org.chromium.sdk.DebugContext.ContinueCallback#failure(java.lang.String)
		 */
		public void failure(String errorMessage) {
			logError(errorMessage);
		}

	}

}
