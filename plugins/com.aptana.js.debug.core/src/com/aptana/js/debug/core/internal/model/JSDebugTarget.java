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
package com.aptana.js.debug.core.internal.model;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManagerListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;
import org.osgi.framework.Constants;

import com.aptana.core.resources.IUniformResource;
import com.aptana.core.resources.IUniformResourceMarker;
import com.aptana.core.util.ResourceUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.debug.core.DetailFormatter;
import com.aptana.debug.core.IDetailFormattersChangeListener;
import com.aptana.debug.core.sourcelookup.IFileContentRetriever;
import com.aptana.debug.internal.core.DbgSourceURLStreamHandler;
import com.aptana.debug.internal.core.LocalResourceMapper;
import com.aptana.js.debug.core.IJSDebugConstants;
import com.aptana.js.debug.core.ILaunchConfigurationConstants;
import com.aptana.js.debug.core.JSDebugPlugin;
import com.aptana.js.debug.core.internal.Util;
import com.aptana.js.debug.core.internal.model.xhr.XHRService;
import com.aptana.js.debug.core.model.IJSDebugTarget;
import com.aptana.js.debug.core.model.IJSExceptionBreakpoint;
import com.aptana.js.debug.core.model.IJSLineBreakpoint;
import com.aptana.js.debug.core.model.IJSScriptElement;
import com.aptana.js.debug.core.model.JSDebugModel;
import com.aptana.js.debug.core.model.provisional.IJSWatchpoint;
import com.aptana.js.debug.core.model.xhr.IXHRService;

/**
 * @author Max Stepanov
 */
public class JSDebugTarget extends JSDebugElement implements IJSDebugTarget, IBreakpointManagerListener, IDetailFormattersChangeListener {

	private static final String UPDATE = "update"; //$NON-NLS-1$
	private static final String VERSION = "version"; //$NON-NLS-1$
	private static final String JAVASCRIPT = "javascript"; //$NON-NLS-1$
	private static final String JAVASCRIPT_SCHEME = "javascript:"; //$NON-NLS-1$
	private static final String OPENED = "opened"; //$NON-NLS-1$
	private static final String HTTP = "http"; //$NON-NLS-1$
	private static final String FILE = "file"; //$NON-NLS-1$
	private static final String EXCEPTION_0_1 = "exception*{0}*{1}"; //$NON-NLS-1$
	private static final String BREAKPOINT_0_1_2_3 = "breakpoint*{0}*{1}*{2}{3}"; //$NON-NLS-1$
	private static final String WATCHPOINT_0_1_2 = "watchpoint*{0}*{1}*{2}"; //$NON-NLS-1$
	private static final String DETAILS_0 = "details*{0}"; //$NON-NLS-1$
	private static final String SET_VALUE_0_1 = "setValue*{0}*{1}"; //$NON-NLS-1$
	private static final String EVAL_0 = "eval[{0}]"; //$NON-NLS-1$
	private static final String EVAL_0_1 = "eval*{0}*{1}"; //$NON-NLS-1$
	private static final String RESULT = "result"; //$NON-NLS-1$
	private static final String FRAME_0 = "frame[{0}]"; //$NON-NLS-1$
	private static final String VARIABLES_0 = "variables*{0}"; //$NON-NLS-1$
	private static final String OPEN_URL_0 = "openUrl*{0}"; //$NON-NLS-1$
	private static final String OPTION_0_1 = "option*{0}*{1}"; //$NON-NLS-1$
	private static final String ENABLE = "enable"; //$NON-NLS-1$
	private static final String STEP_FILTERS = "stepFilters"; //$NON-NLS-1$
	private static final String MONITOR_XHR = "monitorXHR"; //$NON-NLS-1$
	private static final String DETAIL_FORMATTERS = "detailFormatters"; //$NON-NLS-1$
	private static final String DISABLE = "disable"; //$NON-NLS-1$
	private static final String CHANGE = "change"; //$NON-NLS-1$
	private static final String REMOVE = "remove"; //$NON-NLS-1$
	private static final String CREATE = "create"; //$NON-NLS-1$
	private static final String RESOLVED = "resolved"; //$NON-NLS-1$
	private static final String DESTROYED = "destroyed"; //$NON-NLS-1$
	private static final String ARGS_SPLIT = "\\*"; //$NON-NLS-1$
	private static final String SUBARGS_SPLIT = "\\|"; //$NON-NLS-1$
	private static final String CREATED = "created"; //$NON-NLS-1$
	private static final String TERMINATE = "terminate"; //$NON-NLS-1$
	private static final String SUSPEND = "suspend"; //$NON-NLS-1$
	private static final String ERROR = "error"; //$NON-NLS-1$
	private static final String COMPLETED = "completed"; //$NON-NLS-1$
	private static final String LOADED = "loaded"; //$NON-NLS-1$
	private static final String SEND = "send"; //$NON-NLS-1$
	private static final String HEADERS = "headers"; //$NON-NLS-1$
	private static final String AUTH = "auth"; //$NON-NLS-1$
	private static final String OPEN = "open"; //$NON-NLS-1$
	private static final String START = "start"; //$NON-NLS-1$
	private static final String LOAD = "load"; //$NON-NLS-1$
	private static final String EXCEPTION = "exception"; //$NON-NLS-1$
	private static final String ERR = "err"; //$NON-NLS-1$
	private static final String TRACE = "trace"; //$NON-NLS-1$
	private static final String SRC = "src"; //$NON-NLS-1$
	private static final String BREAKPOINT = "breakpoint"; //$NON-NLS-1$
	private static final String SCRIPTS = "scripts"; //$NON-NLS-1$
	private static final String CLIENT = "client"; //$NON-NLS-1$
	private static final String XHR = "xhr"; //$NON-NLS-1$
	private static final String LOG = "log"; //$NON-NLS-1$
	private static final String SUCCESS = "success"; //$NON-NLS-1$
	private static final String GET_SOURCE_0 = "getSource*{0}"; //$NON-NLS-1$
	private static final String STEP_FILTERS_ENABLED2 = "stepFiltersEnabled";
	private static final String BYPASS_CONSTRUCTORS = "bypassConstructors";

	private static final int PROTOCOL_VERSION_MIN = 0;
	private static final int PROTOCOL_VERSION_MAX = 1;

	/**
	 * Step filter bit mask - indicates if step filters are enabled.
	 */
	private static final int FLAG_STEP_FILTERS_ENABLED = 0x001;
	
	/**
	 * Step filter bit mask - indicates if constructors are filtered.
	 */
	private static final int FLAG_FILTER_CONSTRUCTORS = 0x002;
	
	/**
	 * Mask used to flip individual bit masks via XOR
	 */
	private static final int XOR_MASK = 0xFFF;

	private static boolean checkUpdate = true;
	private DebugConnection connection;
	private int stepFilterMask = 0;
	private String[] stepFilters = null;

	private ILaunch launch;
	private String label;
	private IProcess process;
	private OutputStream out;
	private OutputStream err;
	private LocalResourceMapper resourceMapper;
	private JSDebugThread[] threads = new JSDebugThread[0];
	private IFileContentRetriever fileContentRetriever;
	private XHRService xhrService;
	private Map<String, IJSScriptElement> topScriptElements = new HashMap<String, IJSScriptElement>();
	private Map<Integer, IJSScriptElement> scripts = new HashMap<Integer, IJSScriptElement>();
	private List<IBreakpoint> runToLineBreakpoints = new ArrayList<IBreakpoint>();
	private Map<String, String> sourceResolveCache = new HashMap<String, String>(64);
	private String mainFile = null;
	private IBreakpoint skipOperationOnBreakpoint = null;
	private boolean ignoreBreakpointCreation = false;
	private boolean contentChanged = false;

	private Job updateContentJob = new Job("Debugger Content Update") { //$NON-NLS-1$
		{
			setPriority(Job.INTERACTIVE);
			setSystem(true);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
			if (connection == null || !connection.isConnected()) {
				return Status.OK_STATUS;
			}
			try {
				boolean changed = false;
				synchronized (this) {
					if (contentChanged) {
						changed = true;
						contentChanged = false;
					}
				}
				if (changed) {
					fireChangeEvent(DebugEvent.CONTENT);
				}
				return Status.OK_STATUS;
			} finally {
				schedule(1000);
			}
		}

	};

	/**
	 * JSDebugTarget
	 * 
	 * @param launch
	 * @param process
	 * @param httpServer
	 * @param resourceMapper
	 * @param socket
	 * @param debugMode
	 * @throws CoreException
	 */
	public JSDebugTarget(ILaunch launch, IProcess process, HttpServerProcess httpServer,
			LocalResourceMapper resourceMapper, DebugConnection connection, boolean debugMode) throws CoreException {
		this(launch, null, process, httpServer, resourceMapper, connection, debugMode);
	}

	/**
	 * JSDebugTarget
	 * 
	 * @param launch
	 * @param label
	 * @param process
	 * @param httpServer
	 * @param resourceMapper
	 * @param socket
	 * @param debugMode
	 * @throws CoreException
	 */
	public JSDebugTarget(ILaunch launch, String label, IProcess process, HttpServerProcess httpServer,
			LocalResourceMapper resourceMapper, DebugConnection connection, boolean debugMode) throws CoreException {
		super(null);
		this.launch = launch;
		this.label = label;
		this.process = process;
		this.resourceMapper = resourceMapper;
		this.connection = connection;

		try {
			if (debugMode) {
				launch.addDebugTarget(this);
			} else {
				/* TODO: do some refactoring here */
				if (process instanceof JSDebugProcess) {
					((JSDebugProcess) process).setDebugTarget(this);
				}
				if (httpServer != null) {
					httpServer.setDebugTarget(this);
				}
			}
			init(debugMode);
		} catch (CoreException e) {
			shutdown();
			throw e;
		} catch (Exception e) {
			shutdown();
			throwDebugException(e);
		}
	}

	/* package */ DebugConnection getConnection() {
		return connection;
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		if (adapter == IFileContentRetriever.class) {
			return getFileContentRetriever();
		}
		if (adapter == IXHRService.class) {
			return xhrService;
		}
		return super.getAdapter(adapter);
	}

	/**
	 * getFileContentRetriever
	 * 
	 * @return IFileContentRetriever
	 */
	private IFileContentRetriever getFileContentRetriever() {
		if (fileContentRetriever == null) {
			fileContentRetriever = new IFileContentRetriever() {
				public InputStream getContents(URI uri) throws CoreException {
					String[] args = connection.sendCommandAndWait(
							MessageFormat.format(GET_SOURCE_0, Util.encodeData(uri.toString())));
					if (args != null && SUCCESS.equals(args[1])) {
						return new ByteArrayInputStream(Util.decodeData(args[2]).getBytes());
					}
					return null;
				}
			};
		}
		return fileContentRetriever;
	}

	/**
	 * handleLog
	 * 
	 * @param args
	 */
	private void handleLog(String[] args) {
		String log = args[1];
		String text = Util.decodeData(args[2]);
		if (args.length >= 4) {
			StringBuffer sb = new StringBuffer(text);
			String type = args[3];
			if (SRC.equals(type) && args.length >= 6) {
				String fileName = resolveSourceFile(Util.decodeData(args[4]));
				IFile file = ResourceUtil.findWorkspaceFile(Path.fromOSString(fileName));
				if (file != null) {
					fileName = file.getFullPath().makeRelative().toString();
				}
				sb.append(MessageFormat.format(" ({0}:{1})", fileName, args[5])); //$NON-NLS-1$
			} else if (TRACE.equals(type)) {
				sb.append('\n');
				for (int i = 4; i < args.length; ++i) {
					String[] subargs = args[i].split(SUBARGS_SPLIT);
					if (subargs[0].length() == 0) {
						subargs[0] = MessageFormat.format("[{0}]", //$NON-NLS-1$
								i == args.length - 1 ? Messages.JSDebugTarget_TopLevelScript
										: Messages.JSDebugTarget_EvalScript);
					}
					String fileName = resolveSourceFile(Util.decodeData(subargs[2]));
					IFile file = ResourceUtil.findWorkspaceFile(Path.fromOSString(fileName));
					if (file != null) {
						fileName = file.getFullPath().makeRelative().toString();
					}
					sb.append(MessageFormat.format("\tat {0}({1}) ({2}:{3})\n", //$NON-NLS-1$
							Util.decodeData(subargs[0]), Util.decodeData(subargs[1]), fileName,
									subargs[3]));
				}
			}
			text = sb.toString();
		}
		if (!text.endsWith("\n")) { //$NON-NLS-1$
			text += "\n"; //$NON-NLS-1$
		}
		try {
			if (ERR.equals(log) || EXCEPTION.equals(log)) {
				if (err != null) {
					err.write(text.getBytes());
				}
			} else
			/* out */{
				if (out != null) {
					out.write(text.getBytes());
				}
			}
		} catch (IOException e) {
			JSDebugPlugin.log(e);
		}
	}

	/**
	 * handleXHR
	 * 
	 * @param args
	 */
	private void handleXHR(String[] args) {
		int j = 1;
		String rid = args[j++];
		String cmd = args[j++];
		if (START.equals(cmd)) {
			String method = args[j++];
			String url = Util.decodeData(args[j++]);
			String[][] headers = getXHRHeaders(Util.decodeData(args[j++]));
			String body = Util.decodeData(args[j++]);

			xhrService.openRequest(rid, method, url, false);
			xhrService.setRequestHeaders(rid, headers);
			xhrService.setRequestBody(rid, body);
		} else if (LOAD.equals(cmd)) {
			int statusCode = -1;
			try {
				statusCode = Integer.parseInt(args[j++]);
			} catch (NumberFormatException e) {
			}

			String statusText = Util.decodeData(args[j++]);
			String[][] headers = getXHRHeaders(Util.decodeData(args[j++]));
			String response = Util.decodeData(args[j++]);

			xhrService.setResponseStatus(rid, statusCode, statusText);
			xhrService.setResponseHeaders(rid, headers);
			xhrService.setResponseBody(rid, response);
		}
		/* XXX: obsoleted XHR commands below */
		else if (OPEN.equals(cmd)) {
			String method = args[j++];
			String url = Util.decodeData(args[j++]);
			String auth = args[j++];
			xhrService.openRequest(rid, method, url, AUTH.equals(auth));
		} else if (HEADERS.equals(cmd)) {
			String[][] headers = getXHRHeaders(Util.decodeData(args[j++]));
			xhrService.setRequestHeaders(rid, headers);
		} else if (SEND.equals(cmd)) {
			String body = Util.decodeData(args[j++]);
			xhrService.setRequestBody(rid, body);
		} else if (LOADED.equals(cmd)) {
			int statusCode = -1;
			try {
				statusCode = Integer.parseInt(args[j++]);
			} catch (NumberFormatException e) {
			}

			String statusText = Util.decodeData(args[j++]);
			String[][] headers = getXHRHeaders(Util.decodeData(args[j++]));
			xhrService.setResponseHeaders(rid, headers);
			xhrService.setResponseStatus(rid, statusCode, statusText);
		} else if (COMPLETED.equals(cmd)) {
			String response = Util.decodeData(args[j++]);
			xhrService.setResponseBody(rid, response);
		} else if (ERROR.equals(cmd)) {
			xhrService.setError(rid);
		}
	}

	/**
	 * getXHRHeaders
	 * 
	 * @param string
	 * @return String[][]
	 */
	private static String[][] getXHRHeaders(String string) {
		String[] headers = string.split("\\n"); //$NON-NLS-1$
		List<String[]> list = new ArrayList<String[]>(headers.length);
		for (int i = 0; i < headers.length; ++i) {
			String header = headers[i];
			String value = StringUtil.EMPTY;
			int pos = header.indexOf(": "); //$NON-NLS-1$
			if (pos != -1) {
				value = header.substring(pos + 2);
				header = header.substring(0, pos);
			}
			list.add(new String[] { header, value });
		}

		return (String[][]) list.toArray(new String[list.size()][]);
	}

	/**
	 * handleClientAction
	 * 
	 * @param args
	 */
	private void handleClientAction(String[] args) {
		int j = 1;
		String action = args[j++];
		if (SUSPEND.equals(action)) {
			try {
				if (canSuspend()) {
					suspend();
				}
			} catch (DebugException ignore) {
			}
		} else if (TERMINATE.equals(action)) {
			try {
				if (canTerminate()) {
					terminate();
				}
			} catch (DebugException ignore) {
			}
		} else if (OPEN.equals(action)) {
			String fileName = resolveSourceFile(Util.decodeData(args[j++]));
			if (fileName != null) {
				Object sourceElement = null;
				ISourceLocator locator = launch.getSourceLocator();
				if (locator instanceof ISourceLookupDirector) {
					sourceElement = ((ISourceLookupDirector) locator).getSourceElement(fileName);
				}
				if (sourceElement != null) {
					JSDebugPlugin.openInEditor(sourceElement);
				}
			}
		}
	}

	/**
	 * handleScripts
	 * 
	 * @param args
	 */
	private void handleScripts(String[] args) {
		String action = args[1];
		if (CREATED.equals(action)) {
			for (int i = 2; i < args.length; ++i) {
				int j = 0;
				String[] subargs = args[i].split(SUBARGS_SPLIT);
				if (subargs.length < 5) {
					JSDebugPlugin.log(MessageFormat.format(
							"Missing fields in response: <{0}>", args[i])); //$NON-NLS-1$
					continue;
				}
				int scriptTag = -1;
				try {
					scriptTag = Integer.parseInt(subargs[j++]);
				} catch (NumberFormatException e) {
				}
				String fileName = Util.decodeData(subargs[j++]);
				if (fileName.length() == 0 || "[Eval-script]".equals(fileName) //$NON-NLS-1$
						|| fileName.startsWith("javascript:")) { //$NON-NLS-1$ 
					continue;
				}
				fileName = resolveSourceFile(fileName);
				String scriptName = Util.decodeData(subargs[j++]);
				int baseLine = -1;
				int lineExtent = -1;
				try {
					baseLine = Integer.parseInt(subargs[j++]);
					lineExtent = Integer.parseInt(subargs[j++]);
				} catch (NumberFormatException e) {
				}
				if (scriptName.length() == 0) {
					continue; // skip empty scripts
				}
				JSDebugScriptElement topScriptElement = (JSDebugScriptElement) topScriptElements.get(fileName);
				if (topScriptElement == null) {
					String name = fileName;
					IFile file = ResourceUtil.findWorkspaceFile(Path.fromOSString(fileName));
					if (file != null) {
						name = file.getFullPath().toString();
					}
					topScriptElement = new JSDebugTopScriptElement(this, name, fileName);
					topScriptElements.put(fileName, topScriptElement);
				}
				JSDebugScriptElement scriptElement = new JSDebugScriptElement(this, scriptName, baseLine, lineExtent);
				topScriptElement.insertElement(scriptElement);
				if (scriptTag > 0) {
					scripts.put(new Integer(scriptTag), scriptElement);
				}
			}
			synchronized (updateContentJob) {
				contentChanged = true;
			}
			;
		} else if (DESTROYED.equals(action)) {
			int j = 0;
			String[] subargs = args[2].split(SUBARGS_SPLIT);
			int scriptTag = -1;
			try {
				scriptTag = Integer.parseInt(subargs[j++]);
			} catch (NumberFormatException e) {
			}
			String fileName = resolveSourceFile(Util.decodeData(subargs[j++]));
			JSDebugScriptElement topScriptElement = (JSDebugScriptElement) topScriptElements.get(fileName);
			if (scriptTag > 0) {
				JSDebugScriptElement scriptElement = (JSDebugScriptElement) scripts.remove(new Integer(scriptTag));
				if (topScriptElement != null && scriptElement != null) {
					topScriptElement.removeElement(scriptElement);
				}
			}
		} else if (RESOLVED.equals(action)) {
			for (int i = 2; i < args.length; ++i) {
				int j = 0;
				String[] subargs = args[i].split(SUBARGS_SPLIT);
				int scriptTag = -1;
				try {
					scriptTag = Integer.parseInt(subargs[j++]);
				} catch (NumberFormatException e) {
				}
				String scriptName = Util.decodeData(subargs[j++]);
				if (scriptTag < 0 || scriptName.length() == 0) {
					continue;
				}
				JSDebugScriptElement scriptElement = (JSDebugScriptElement) scripts.get(new Integer(scriptTag));
				if (scriptElement != null) {
					scriptElement.setName(scriptName);
					scriptElement.fireChangeEvent(DebugEvent.STATE);
				}
			}

		}
	}

	/**
	 * @see org.eclipse.debug.core.model.IDebugElement#getLaunch()
	 */
	public ILaunch getLaunch() {
		return launch;
	}

	/**
	 * @see org.eclipse.debug.core.model.IDebugElement#getDebugTarget()
	 */
	public IDebugTarget getDebugTarget() {
		return this;
	}

	/**
	 * @see org.eclipse.debug.core.model.IDebugTarget#getProcess()
	 */
	public IProcess getProcess() {
		return process;
	}

	/**
	 * @see org.eclipse.debug.core.model.IDebugTarget#getThreads()
	 */
	public IThread[] getThreads() throws DebugException {
		return threads;
	}

	/**
	 * @see org.eclipse.debug.core.model.IDebugTarget#hasThreads()
	 */
	public boolean hasThreads() throws DebugException {
		return threads.length > 0;
	}

	/**
	 * @see org.eclipse.debug.core.model.IDebugTarget#getName()
	 */
	public String getName() throws DebugException {
		return label != null ? label : Messages.JSDebugTarget_JSDebugger;
	}

	/**
	 * @see org.eclipse.debug.core.model.IDebugTarget#supportsBreakpoint(org.eclipse.debug.core.model.IBreakpoint)
	 */
	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		if (breakpoint.getModelIdentifier().equals(getModelIdentifier())) {
			return true;
		}
		return false;
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
		return connection.isTerminated();
	}

	/**
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	public void terminate() throws DebugException {
		if (isTerminated()) {
			return;
		}
		connection.sendCommand(TERMINATE);
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
		return !isSuspended();
	}

	/**
	 * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
	 */
	public boolean isSuspended() {
		return threads.length > 0 ? threads[0].isSuspended() : false;
	}

	/**
	 * @see org.eclipse.debug.core.model.ISuspendResume#resume()
	 */
	public void resume() throws DebugException {
		threads[0].resume();
	}

	/**
	 * @see org.eclipse.debug.core.model.ISuspendResume#suspend()
	 */
	public void suspend() throws DebugException {
		if (isDisconnected()) {
			return;
		}
		threads[0].suspend();
	}

	/**
	 * @see org.eclipse.debug.core.IBreakpointListener#breakpointAdded(org.eclipse.debug.core.model.IBreakpoint)
	 */
	public void breakpointAdded(IBreakpoint breakpoint) {
		if (supportsBreakpoint(breakpoint) && breakpoint instanceof IJSLineBreakpoint) {
			try {
				if (((IJSLineBreakpoint) breakpoint).isRunToLine()) {
					runToLineBreakpoints.add(breakpoint);
				}
			} catch (CoreException e) {
			}
		}
		handleBreakpoint(breakpoint, CREATE);
	}

	/**
	 * @see org.eclipse.debug.core.IBreakpointListener#breakpointRemoved(org.eclipse.debug.core.model.IBreakpoint,
	 *      org.eclipse.core.resources.IMarkerDelta)
	 */
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		if (supportsBreakpoint(breakpoint) && breakpoint instanceof IJSLineBreakpoint) {
			try {
				if (((IJSLineBreakpoint) breakpoint).isRunToLine()) {
					runToLineBreakpoints.remove(breakpoint);
				}
			} catch (CoreException e) {
			}
		}
		handleBreakpoint(breakpoint, REMOVE);
	}

	/**
	 * @see org.eclipse.debug.core.IBreakpointListener#breakpointChanged(org.eclipse.debug.core.model.IBreakpoint,
	 *      org.eclipse.core.resources.IMarkerDelta)
	 */
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		/* TODO: check delta to use right operation (create/change) */
		handleBreakpoint(breakpoint, CHANGE);
	}

	/**
	 * @see org.eclipse.debug.core.model.IDisconnect#canDisconnect()
	 */
	public boolean canDisconnect() {
		return !isDisconnected();
	}

	/**
	 * @see org.eclipse.debug.core.model.IDisconnect#disconnect()
	 */
	public void disconnect() throws DebugException {
		connection.sendCommandAndWait(DISABLE);
		stopDebug();
	}

	/**
	 * @see org.eclipse.debug.core.model.IDisconnect#isDisconnected()
	 */
	public boolean isDisconnected() {
		return !connection.isConnected();
	}

	/**
	 * @see org.eclipse.debug.core.model.IMemoryBlockRetrieval#supportsStorageRetrieval()
	 */
	public boolean supportsStorageRetrieval() {
		return false;
	}

	/**
	 * @see org.eclipse.debug.core.model.IMemoryBlockRetrieval#getMemoryBlock(long,
	 *      long)
	 */
	public IMemoryBlock getMemoryBlock(long startAddress, long length) throws DebugException {
		throwNotImplemented();
		return null;
	}

	/**
	 * @see com.aptana.js.debug.core.model.IJSDebugTarget#isFilterConstructors()
	 */
	public boolean isFilterConstructors() {
		return (stepFilterMask & FLAG_FILTER_CONSTRUCTORS) > 0;
	}

	/**
	 * @see com.aptana.js.debug.core.model.IJSDebugTarget#setFilterConstructors(boolean)
	 */
	public void setFilterConstructors(boolean filter) {
		if (filter) {
			stepFilterMask = stepFilterMask | FLAG_FILTER_CONSTRUCTORS;
		} else {
			stepFilterMask = stepFilterMask & (FLAG_FILTER_CONSTRUCTORS ^ XOR_MASK);
		}
	}

	/**
	 * @see org.eclipse.debug.core.model.IStepFilters#isStepFiltersEnabled()
	 */
	public boolean isStepFiltersEnabled() {
		return (stepFilterMask & FLAG_STEP_FILTERS_ENABLED) > 0;
	}

	/**
	 * @see org.eclipse.debug.core.model.IStepFilters#setStepFiltersEnabled(boolean)
	 */
	public void setStepFiltersEnabled(boolean enabled) {
		if (enabled) {
			stepFilterMask = stepFilterMask | FLAG_STEP_FILTERS_ENABLED;
		} else {
			stepFilterMask = stepFilterMask & (FLAG_STEP_FILTERS_ENABLED ^ XOR_MASK);
		}
		try {
			setOption(STEP_FILTERS_ENABLED2, Boolean.toString(isStepFiltersEnabled())); //$NON-NLS-1$
		} catch (DebugException e) {
			JSDebugPlugin.log(e);
		}
	}

	/**
	 * @see org.eclipse.debug.core.model.IStepFilters#supportsStepFilters()
	 */
	public boolean supportsStepFilters() {
		return !isTerminated() && !isDisconnected();
	}

	/**
	 * @see com.aptana.js.debug.core.model.IJSDebugTarget#getStepFilters()
	 */
	public String[] getStepFilters() {
		return stepFilters;
	}

	/**
	 * @see com.aptana.js.debug.core.model.IJSDebugTarget#setStepFilters(java.lang.String[])
	 */
	public void setStepFilters(String[] list) {
		stepFilters = list;
	}

	/**
	 * @see com.aptana.js.debug.core.model.IJSDebugTarget#getAttribute(java.lang.String)
	 */
	public String getAttribute(String key) {
		return getLaunch().getAttribute(key);
	}

	/**
	 * @see com.aptana.js.debug.core.model.IJSDebugTarget#setAttribute(java.lang.String,
	 *      java.lang.String)
	 */
	public void setAttribute(String key, String value) {
		getLaunch().setAttribute(key, value);
		try {
			handleAttribute(key);
		} catch (DebugException e) {
			JSDebugPlugin.log(e);
		}
	}

	/**
	 * handleAttribute
	 * 
	 * @param key
	 * @throws DebugException
	 */
	private void handleAttribute(String key) throws DebugException {
		String value = getAttribute(key);
		boolean boolValue = Boolean.valueOf(value).booleanValue();
		if (ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_FIRST_LINE.equals(key)) {
			setOption("suspendOnFirstLine", Boolean.toString(boolValue)); //$NON-NLS-1$
		} else if (ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_EXCEPTIONS.equals(key)) {
			setOption("suspendOnExceptions", Boolean.toString(boolValue)); //$NON-NLS-1$
		} else if (ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_ERRORS.equals(key)) {
			setOption("suspendOnErrors", Boolean.toString(boolValue)); //$NON-NLS-1$
		} else if (ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_DEBUGGER_KEYWORDS.equals(key)) {
			setOption("suspendOnKeywords", Boolean.toString(boolValue)); //$NON-NLS-1$
		}
	}

	/**
	 * @see com.aptana.debug.core.debug.core.IDetailFormattersChangeListener#detailFormattersChanged()
	 */
	public void detailFormattersChanged() {
		try {
			handleDetailFormattersChange();
		} catch (DebugException e) {
			JSDebugPlugin.log(e);
		}
	}

	/**
	 * handleDetailFormattersChange
	 * 
	 * @throws DebugException
	 */
	private void handleDetailFormattersChange() throws DebugException {
		StringBuffer sb = new StringBuffer(DETAIL_FORMATTERS);
		for (DetailFormatter detailFormatter : JSDebugPlugin.getDefault().getDebugOptionsManager().getDetailFormatters()) {
			if (!detailFormatter.isEnabled()) {
				continue;
			}
			sb.append(MessageFormat.format("*{0}|{1}", //$NON-NLS-1$
					detailFormatter.getTypeName(), Util.encodeData(detailFormatter.getSnippet())));
		}
		connection.sendCommandAndWait(sb.toString());
	}

	/**
	 * init
	 * 
	 * @param debugMode
	 * @throws CoreException
	 */
	private void init(boolean debugMode) throws CoreException {
		synchronized (this) {
			connection.start(new DebugConnectionHandler());
			updateContentJob.schedule();

			/* check debugger/protocol version */
			checkVersion();
		}

		if (true /* TODO: monitor XHR option */) {
			xhrService = new XHRService();
			setOption(MONITOR_XHR, Boolean.toString(true));
			if (process instanceof JSDebugProcess) {
				((JSDebugProcess) process).setXHRService(xhrService);
			}

		}

		if (debugMode) {
			fireCreationEvent();

			JSDebugThread thread = new JSDebugThread(this);
			threads = new JSDebugThread[] { thread };
			for (int i = 0; i < threads.length; ++i) {
				threads[i].fireCreationEvent();
			}
			fireChangeEvent(DebugEvent.CONTENT);

			handleAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_FIRST_LINE);
			handleAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_EXCEPTIONS);
			handleAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_ERRORS);
			handleAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_DEBUGGER_KEYWORDS);

			setOption(BYPASS_CONSTRUCTORS, Boolean.toString(isFilterConstructors())); //$NON-NLS-1$
			setOption(STEP_FILTERS_ENABLED2, Boolean.toString(isStepFiltersEnabled())); //$NON-NLS-1$
			if (stepFilters != null && stepFilters.length > 0) {
				StringBuffer sb = new StringBuffer(STEP_FILTERS);
				for (int i = 0; i < stepFilters.length; ++i) {
					sb.append(i != 0 ? '|' : '*').append(Util.encodeData(stepFilters[i]));
				}
				connection.sendCommandAndWait(sb.toString());
			}
			handleDetailFormattersChange();
			JSDebugPlugin.getDefault().getDebugOptionsManager().addChangeListener(this);

			/* restore breakpoints */
			IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(
					getModelIdentifier());
			for (int i = 0; i < breakpoints.length; ++i) {
				breakpointAdded(breakpoints[i]);
			}

			// Register listeners
			DebugPlugin.getDefault().getBreakpointManager().addBreakpointManagerListener(this);
			DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this);

			connection.sendCommandAndWait(ENABLE);
		}

		if (process instanceof JSDebugProcess) {
			out = ((JSDebugProcess) process).getOutputStream();
			err = ((JSDebugProcess) process).getErrorStream();
		}
	}

	/**
	 * checkVersion
	 * 
	 * @throws DebugException
	 */
	private void checkVersion() throws DebugException {
		int protoVersion = 0;
		String version = null;
		String[] args = connection.sendCommandAndWait(VERSION);
		if (args != null && args.length >= 3 && args[1].charAt(0) != '!') {
			JSDebugPlugin.log(MessageFormat.format("Extension version: {0}; protocol v{1}", //$NON-NLS-1$
					args[2], args[1]));
			try {
				protoVersion = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
			}
			version = args[2];
		}
		if ((protoVersion < PROTOCOL_VERSION_MIN) || (protoVersion > PROTOCOL_VERSION_MAX)) {
			throwDebugException(MessageFormat.format(
					"Incompatible debugger extension protocol version {0} for [{1},{2}]", //$NON-NLS-1$
					Integer.toString(protoVersion), Integer.toString(PROTOCOL_VERSION_MIN),
							Integer.toString(PROTOCOL_VERSION_MAX)));
		}
		if (checkUpdate) {
			boolean update = false;
			if (version != null) {
				String pluginVersion = (String) Platform.getBundle(JSDebugPlugin.PLUGIN_ID).getHeaders().get(
						Constants.BUNDLE_VERSION);
				int index = pluginVersion.lastIndexOf('.');
				if (index != -1) {
					if (index >= version.length()
							|| !pluginVersion.substring(0, index).equals(version.substring(0, index))) {
						update = true;

					} else if (!pluginVersion.substring(index + 1).equals(version.substring(index + 1))) {
						try {
							if (Integer.parseInt(pluginVersion.substring(index + 1)) > Integer.parseInt(version
									.substring(index + 1))) {
								update = true;
							}
						} catch (NumberFormatException e) {
						}
					}
				}
				if (update) {
					args = connection.sendCommandAndWait(UPDATE);
					if (args != null && args.length >= 2) {
						JSDebugPlugin.log(MessageFormat.format(
								"Extension update available: {0}", //$NON-NLS-1$
								args[1]));

					}
				}
			}
			checkUpdate = false;
		}

	}

	/**
	 * setOption
	 * 
	 * @param option
	 * @param value
	 * @throws DebugException
	 */
	private synchronized void setOption(String option, String value) throws DebugException {
		if (connection.isConnected()) {
			connection.sendCommandAndWait(MessageFormat.format(OPTION_0_1, option, value));
		}
	}

	/**
	 * openURL
	 * 
	 * @param url
	 * @throws DebugException
	 */
	public void openURL(URL url) throws DebugException {
		if (connection.isConnected()) {
			try {
				DebugEvent event = new DebugEvent(this, DebugEvent.MODEL_SPECIFIC, IJSDebugConstants.DEBUG_EVENT_URL_OPEN);
				URL fileUrl = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getPath());
				event.setData(resolveSourceFile(fileUrl.toExternalForm()));
				fireEvent(event);
			} catch (MalformedURLException e) {
				JSDebugPlugin.log(e);
			}

			mainFile = null;
			connection.sendCommandAndWait(MessageFormat.format(OPEN_URL_0, Util.encodeData(url.toString())));
		}
	}

	/**
	 * stopDebug
	 * 
	 * @throws DebugException
	 */
	private void stopDebug() throws DebugException {
		if (connection == null || !connection.isConnected()) {
			return;
		}
		connection.stop();
		updateContentJob.cancel();
		if (threads.length > 0) {
			for (int i = 0; i < threads.length; ++i) {
				threads[i].fireTerminateEvent();
			}
			threads = new JSDebugThread[0];
			topScriptElements.clear();
			scripts.clear();
			fireChangeEvent(DebugEvent.CONTENT);

			// Unregister listeners
			JSDebugPlugin.getDefault().getDebugOptionsManager().removeChangeListener(this);
			DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(this);
			DebugPlugin.getDefault().getBreakpointManager().removeBreakpointManagerListener(this);
		}
	}

	/**
	 * shutdown
	 * 
	 * @throws DebugException
	 */
	private void shutdown() throws DebugException {
		try {
			stopDebug();
			if (connection != null) {
				connection.dispose();
			}
		} catch (IOException e) {
			throwDebugException(e);
		} finally {
			if (DebugPlugin.getDefault() != null) {
				fireTerminateEvent();
			}
		}
	}

	/**
	 * @see org.eclipse.debug.core.IBreakpointManagerListener#breakpointManagerEnablementChanged(boolean)
	 */
	public void breakpointManagerEnablementChanged(boolean enabled) {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager()
				.getBreakpoints(getModelIdentifier());
		for (int i = 0; i < breakpoints.length; i++) {
			IBreakpoint breakpoint = breakpoints[i];
			if (enabled) {
				breakpointAdded(breakpoint);
			} else {
				breakpointRemoved(breakpoint, null);
			}
		}
	}

	/**
	 * loadVariables
	 * 
	 * @param qualifier
	 * @return IVariable[]
	 * @throws DebugException
	 */
	protected IVariable[] loadVariables(String qualifier) throws DebugException {
		if (!isSuspended()) {
			return new IVariable[0];
		}
		List<IVariable> list = new ArrayList<IVariable>();
		String[] args = connection.sendCommandAndWait(MessageFormat.format(VARIABLES_0, Util.encodeData(qualifier)));
		if (args != null) {
			for (int i = 1; i < args.length; ++i) {
				int j = 0;
				String varData = args[i];
				if (varData.length() == 0) {
					break;
				}
				if (varData.endsWith("|")) //$NON-NLS-1$
				{
					varData += "| "; //$NON-NLS-1$
				}
				String[] subargs = varData.split(SUBARGS_SPLIT);
				String name = Util.decodeData(subargs[j++]);
				String type = Util.decodeData(subargs[j++]);
				String flags = subargs[j++];
				String stringValue = Util.decodeData(subargs[j++]);
				boolean complex = flags.indexOf('o') != -1;
				IValue ivalue;
				String q = MessageFormat.format("{0}.{1}", //$NON-NLS-1$
						qualifier, name);
				ivalue = new JSDebugValue(this, q, type, complex, stringValue);
				list.add(new JSDebugVariable(this, q, name, ivalue, convertVariableFlags(flags)));
			}
		}

		return (IVariable[]) list.toArray(new IVariable[list.size()]);
	}

	/**
	 * evaluateExpression
	 * 
	 * @param expression
	 * @param context
	 * @return Object
	 * @throws DebugException
	 */
	protected Object evaluateExpression(String expression, IDebugElement context) throws DebugException {
		if (!isSuspended()) {
			return null;
		}

		String qualifier;
		Object result = null;
		// TODO: caching ?
		if (context instanceof JSDebugStackFrame) {
			qualifier = MessageFormat.format(FRAME_0, ((JSDebugStackFrame) context).getFrameId());
		} else if (context instanceof JSDebugVariable) {
			qualifier = ((JSDebugVariable) context).getQualifier();
		} else {
			return result;
		}
		String[] args = connection.sendCommandAndWait(MessageFormat.format(EVAL_0_1,
				Util.encodeData(qualifier), Util.encodeData(expression)));
		String status = args != null && args.length > 1 ? args[1] : null;
		if (RESULT.equals(status)) {
			String evalId = args[2];
			String varData = args[3];
			if (varData.endsWith("|")) //$NON-NLS-1$
			{
				varData += "| "; //$NON-NLS-1$
			}
			String[] subargs = varData.split(SUBARGS_SPLIT);
			int j = 0;
			String type = subargs[j++];
			String flags = subargs[j++];
			String stringValue = Util.decodeData(subargs[j++]);
			boolean complex = flags.indexOf('o') != -1;
			result = new JSDebugValue(this, MessageFormat.format(EVAL_0, evalId), type, complex, stringValue);
		} else if (EXCEPTION.equals(status)) {
			result = new String[] { args[2] };
		}
		return result;
	}

	/**
	 * setValue
	 * 
	 * @param variable
	 * @param newValue
	 * @return Object
	 * @throws DebugException
	 */
	protected Object setValue(IVariable variable, IValue newValue) throws DebugException {
		if (!isSuspended()) {
			return null;
		}

		String qualifier;
		String vqualifier;
		Object result = null;
		if (variable instanceof JSDebugVariable) {
			qualifier = ((JSDebugVariable) variable).getQualifier();
		} else {
			return result;
		}
		if (newValue instanceof JSDebugValue) {
			vqualifier = ((JSDebugValue) newValue).getQualifier();
		} else {
			return result;
		}
		String[] args = connection.sendCommandAndWait(MessageFormat.format(SET_VALUE_0_1,
				Util.encodeData(qualifier), vqualifier));
		if (args != null && args.length >= 3) {
			String status = args[1];
			if (RESULT.equals(status)) {
				String[] subargs = args[2].split(SUBARGS_SPLIT);
				int j = 0;
				String type = subargs[j++];
				String flags = subargs[j++];
				String stringValue = Util.decodeData(subargs[j++]);
				boolean complex = flags.indexOf('o') != -1;
				result = new JSDebugValue(this, qualifier, type, complex, stringValue);
			} else if (EXCEPTION.equals(status)) {
				result = new String[] { args[2] };
			}
		}
		return result;
	}

	/**
	 * @see com.aptana.js.debug.core.model.IJSDebugTarget#computeValueDetails(org.eclipse.debug.core.model.IValue)
	 */
	public String computeValueDetails(IValue value) throws DebugException {
		if (!isSuspended()) {
			return StringUtil.EMPTY;
		}

		String qualifier;
		String result = null;
		if (value instanceof JSDebugValue) {
			qualifier = ((JSDebugValue) value).getQualifier();
		} else {
			return value.getValueString();
		}
		String[] args = connection.sendCommandAndWait(MessageFormat.format(DETAILS_0, Util.encodeData(qualifier)));
		if (args != null && args.length >= 3) {
			String status = args[1];
			if (RESULT.equals(status)) {
				result = Util.decodeData(args[2]);
			}
		}
		if (result == null) {
			result = value.getValueString();
		}
		if (result == null) {
			return StringUtil.EMPTY;
		}
		return result;
	}

	/**
	 * findVariable
	 * 
	 * @param variableName
	 * @param context
	 * @return IVariable
	 * @throws DebugException
	 */
	protected IVariable findVariable(String variableName, IDebugElement context) throws DebugException {
		if (Util.checkVariable(variableName)) {
			Object result = evaluateExpression(variableName, context);
			if (result instanceof IValue) {
				return new JSDebugVariable(this, null/* TODO? */, variableName, (IValue) result);
			}
		}
		return null;
	}

	/**
	 * handleBreakpoint
	 * 
	 * @param breakpoint
	 * @param operation
	 */
	private void handleBreakpoint(IBreakpoint breakpoint, String operation) {
		if (isDisconnected()) {
			return;
		}
		if (breakpoint.equals(skipOperationOnBreakpoint)) {
			skipOperationOnBreakpoint = null;
			return;
		}
		if (CREATE.equals(operation) && ignoreBreakpointCreation) {
			return;
		}
		if (supportsBreakpoint(breakpoint)) {
			if (breakpoint instanceof IJSLineBreakpoint) {
				handleLineBreakpoint((IJSLineBreakpoint) breakpoint, operation);
			} else if (breakpoint instanceof IJSExceptionBreakpoint) {
				handleExceptionBreakpoint((IJSExceptionBreakpoint) breakpoint, operation);
			} else if (breakpoint instanceof IJSWatchpoint) {
				handleWatchpoint((IJSWatchpoint) breakpoint, operation);
			}
		}
	}

	/**
	 * handleLineBreakpoint
	 * 
	 * @param breakpoint
	 * @param operation
	 */
	private void handleLineBreakpoint(IJSLineBreakpoint breakpoint, String operation) {
		IMarker marker = breakpoint.getMarker();
		URL url = null;
		String properties = StringUtil.EMPTY;
		try {
			URI uri = null;
			if (marker instanceof IUniformResourceMarker) {
				uri = ((IUniformResourceMarker) marker).getUniformResource().getURI();
			} else {
				IResource resource = marker.getResource();
				if (resource instanceof IWorkspaceRoot) {
					uri = URI.create((String) marker.getAttribute(IJSDebugConstants.BREAKPOINT_LOCATION));
				} else {
					uri = resource.getLocation().makeAbsolute().toFile().toURI();
				}
			}
			if (uri != null && resourceMapper != null) {
				uri = resourceMapper.resolveLocalURI(uri);
			}
			if (uri != null) {
				if ("dbgsource".equals(uri.getScheme())) //$NON-NLS-1$
				{
					url = new URL(null, uri.toString(), DbgSourceURLStreamHandler.getDefault());
				} else {
					url = uri.toURL();
				}
			}
		} catch (MalformedURLException e) {
			JSDebugPlugin.log(e);
		} catch (CoreException e) {
			JSDebugPlugin.log(e);
		}
		int lineNumber = marker.getAttribute(IMarker.LINE_NUMBER, -1);
		if (lineNumber == -1) {
			return;
		}

		boolean remove = REMOVE.equals(operation);
		if (!remove) {
			boolean enabled = false;
			try {
				enabled = breakpoint.isEnabled();
			} catch (CoreException ignore) {
			}
			int hitCount = marker.getAttribute(IJSDebugConstants.BREAKPOINT_HIT_COUNT, 0);
			boolean conditionEnabled = marker.getAttribute(IJSDebugConstants.BREAKPOINT_CONDITION_ENABLED, false);
			String condition = conditionEnabled ? marker.getAttribute(IJSDebugConstants.BREAKPOINT_CONDITION,
					StringUtil.EMPTY) : StringUtil.EMPTY;
			String suspendOnTrue = marker.getAttribute(IJSDebugConstants.BREAKPOINT_CONDITION_SUSPEND_ON_TRUE, true) ? "1" : "0"; //$NON-NLS-1$ //$NON-NLS-2$
			properties = MessageFormat
					.format(
							"*{0}*{1}*{2}*{3}", //$NON-NLS-1$
							enabled ? "1" : "0", Integer.toString(hitCount), Util.encodeData(condition), suspendOnTrue); //$NON-NLS-1$ //$NON-NLS-2$
		}
		try {
			String[] args = connection.sendCommandAndWait(MessageFormat.format(BREAKPOINT_0_1_2_3,
					operation, Util.encodeData(url.toString()), Integer.toString(lineNumber), properties));
			if (!remove && (args == null || args.length < 2 || !(operation + 'd').equals(args[1]))) {
				breakpoint.setEnabled(false);
			}
		} catch (CoreException e) {
			JSDebugPlugin.log(e);
		}

	}

	/**
	 * handleExceptionBreakpoint
	 * 
	 * @param breakpoint
	 * @param operation
	 */
	private void handleExceptionBreakpoint(IJSExceptionBreakpoint breakpoint, String operation) {
		IMarker marker = breakpoint.getMarker();
		String exceptionTypeName = marker.getAttribute(IJSDebugConstants.EXCEPTION_TYPE_NAME, StringUtil.EMPTY);
		if (exceptionTypeName == null || exceptionTypeName.length() == 0) {
			return;
		}

		boolean enabled = false;
		try {
			enabled = breakpoint.isEnabled();
		} catch (CoreException ignore) {
		}
		if (!enabled) {
			operation = REMOVE;
		}

		enabled = !REMOVE.equals(operation);
		try {
			String[] args = connection.sendCommandAndWait(MessageFormat.format(EXCEPTION_0_1, operation,
					exceptionTypeName));
			if (enabled && (args == null || !(operation + 'd').equals(args[1]))) {
				breakpoint.setEnabled(false);
			}
		} catch (CoreException e) {
			JSDebugPlugin.log(e);
		}

	}

	/**
	 * handleWatchpoint
	 * 
	 * @param watchpoint
	 * @param operation
	 */
	private void handleWatchpoint(IJSWatchpoint watchpoint, String operation) {
		IMarker marker = watchpoint.getMarker();
		String variableName = marker.getAttribute(IJSDebugConstants.WATCHPOINT_VARIABLE_ACCESSOR, StringUtil.EMPTY);
		boolean enabled = false;
		try {
			enabled = watchpoint.isEnabled();
		} catch (CoreException ignore) {
		}
		if (!enabled) {
			if (CREATE.equals(operation)) {
				return;
			}
			operation = REMOVE;
		}
		enabled = !REMOVE.equals(operation);

		String kind = StringUtil.EMPTY;
		if (enabled) {
			try {
				if (watchpoint.isAccess()) {
					kind += 'r';
				}
				if (watchpoint.isModification()) {
					kind += 'w';
				}
			} catch (CoreException ignore) {
			}
		}
		try {
			String[] args = connection.sendCommandAndWait(MessageFormat.format(WATCHPOINT_0_1_2,
					operation, Util.encodeData(variableName), kind));
			if (enabled && (args == null || args.length < 2 || !(operation + 'd').equals(args[1]))) {
				watchpoint.setEnabled(false);
			}
		} catch (CoreException e) {
			JSDebugPlugin.log(e);
		}

	}

	/**
	 * findBreakpointAt
	 * 
	 * @param filename
	 * @param lineNumber
	 * @return IBreakpoint
	 */
	protected IBreakpoint findBreakpointAt(String filename, int lineNumber) {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager()
				.getBreakpoints(getModelIdentifier());
		IBreakpoint breakpoint = findBreakpointIn(filename, lineNumber, breakpoints);
		if (breakpoint != null) {
			return breakpoint;
		}
		if (!runToLineBreakpoints.isEmpty()) {
			return findBreakpointIn(filename, lineNumber, (IBreakpoint[]) runToLineBreakpoints
					.toArray(new IBreakpoint[runToLineBreakpoints.size()]));
		}
		return null;
	}

	/**
	 * findBreakpointIn
	 * 
	 * @param filename
	 * @param lineNumber
	 * @param breakpoints
	 * @return IBreakpoint
	 */
	protected IBreakpoint findBreakpointIn(String fileName, int lineNumber, IBreakpoint[] breakpoints) {
		for (int i = 0; i < breakpoints.length; ++i) {
			IBreakpoint breakpoint = breakpoints[i];
			if (getDebugTarget().supportsBreakpoint(breakpoint)) {
				if (breakpoint instanceof ILineBreakpoint) {
					try {
						IMarker marker = breakpoint.getMarker();
						boolean fileMatched = false;
						if (marker instanceof IUniformResourceMarker) {
							URI breakpointURI = ((IUniformResourceMarker) marker).getUniformResource().getURI();
							fileMatched = new URI(Util.fixupURI(fileName)).equals(breakpointURI);
						} else if (marker.getResource() instanceof IWorkspaceRoot) {
							URI breakpointURI = URI.create((String) marker
									.getAttribute(IJSDebugConstants.BREAKPOINT_LOCATION));
							fileMatched = new URI(Util.fixupURI(fileName)).equals(breakpointURI);
						} else {
							IFile file = ResourceUtil.findWorkspaceFile(Path.fromOSString(fileName));
							if (file != null) {
								fileMatched = file.equals(marker.getResource());
							} else {
								File breakpointFile = marker.getResource().getLocation().toFile();
								fileMatched = new File(fileName).equals(breakpointFile);
							}
						}
						if (fileMatched && ((ILineBreakpoint) breakpoint).getLineNumber() == lineNumber) {
							return breakpoint;
						}
					} catch (CoreException e) {
						JSDebugPlugin.log(e);
					} catch (URISyntaxException e) {
						JSDebugPlugin.log(e);
					}
				}
			}
		}
		return null;
	}

	/**
	 * Returns absolute path for local files or leaves path(URL) unchanged
	 * 
	 * @param sourceFile
	 * @return String
	 */
	protected String resolveSourceFile(String sourceFile) {
		String resolved = (String) sourceResolveCache.get(sourceFile);
		if (resolved != null) {
			return resolved;
		}
		try {
			URI uri = new URI(sourceFile);
			String scheme = uri.getScheme();
			if (FILE.equals(scheme)) {
				try {
					File osFile = new File(uri.getSchemeSpecificPart());
					IPath canonicalPath = new Path(osFile.getCanonicalPath());
					IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(canonicalPath);
					if (file != null) {
						resolved = file.getLocation().toString();
					} else {
						resolved = osFile.getAbsolutePath();
					}
				} catch (IOException e) {
				}
			} else if (HTTP.equals(scheme) && resourceMapper != null) {
				File osFile = resourceMapper.resolveServerURL(uri.toURL());
				if (osFile != null) {
					resolved = osFile.getAbsolutePath();
				}
			} else if (JAVASCRIPT.equals(scheme)) {
				if (mainFile != null) {
					return mainFile;
				}
			}
			if (resolved != null) {
				sourceResolveCache.put(sourceFile, resolved);
				return resolved;
			}
		} catch (URISyntaxException e) {
			if (sourceFile.startsWith(JAVASCRIPT_SCHEME)) {
				if (mainFile != null) {
					return mainFile;
				}
			}
			JSDebugPlugin.log(e);
		} catch (MalformedURLException e) {
			JSDebugPlugin.log(e);
		}
		return sourceFile;
	}

	/**
	 * findSourceResource
	 * 
	 * @param sourceFile
	 * @return Object
	 * @throws CoreException
	 */
	private Object findSourceResource(String sourceFile) throws CoreException {
		ISourceLocator locator = launch.getSourceLocator();
		if (locator instanceof ISourceLookupDirector) {
			ISourceLookupDirector lookupDirector = (ISourceLookupDirector) locator;
			Object[] result = lookupDirector.findSourceElements(sourceFile);
			if (result != null && result.length > 0) {
				Object resource = result[0];
				if (resource instanceof IResource) {
					return resource;
				}
				if (!(resource instanceof IUniformResource) && resource instanceof IAdaptable) {
					Object adopted = ((IAdaptable) resource).getAdapter(IUniformResource.class);
					if (adopted != null) {
						resource = adopted;
					}
				}
				return resource;
			}
		}
		return null;
	}

	/**
	 * convertVariableFlags
	 * 
	 * @param string
	 * @return int
	 */
	private static int convertVariableFlags(String string) {
		int flags = 0;
		char[] chars = string.toCharArray();
		for (int i = 0; i < chars.length; ++i) {
			switch (chars[i]) {
			case 'w':
				flags |= JSDebugVariable.FLAGS_MODIFIABLE;
				break;
			case 'c':
				flags |= JSDebugVariable.FLAGS_CONST;
				break;
			case 'l':
				flags |= JSDebugVariable.FLAGS_LOCAL;
				break;
			case 'a':
				flags |= JSDebugVariable.FLAGS_ARGUMENT;
				break;
			case 'e':
				flags |= JSDebugVariable.FLAGS_EXCEPTION;
				break;
			default:
				break;
			}
		}
		return flags;
	}

	/**
	 * @see com.aptana.js.debug.core.model.IJSDebugTarget#getTopScriptElements()
	 */
	public IJSScriptElement[] getTopScriptElements() {
		return (IJSScriptElement[]) topScriptElements.values().toArray(new IJSScriptElement[topScriptElements.size()]);
	}
	
	private class DebugConnectionHandler implements DebugConnection.IHandler {

		/* (non-Javadoc)
		 * @see com.aptana.js.debug.core.internal.model.DebugConnection.IHandler#handleMessage(java.lang.String)
		 */
		public void handleMessage(String message) {
			String[] args = message.split(ARGS_SPLIT);
			int j = 0;
			String action = args[j++];
			if (LOG.equals(action)) {
				handleLog(args);
				return;
			} else if (XHR.equals(action)) {
				handleXHR(args);
				return;
			} else if (CLIENT.equals(action)) {
				handleClientAction(args);
				return;
			} else if (SCRIPTS.equals(action)) {
				handleScripts(args);
				return;
			} else if (BREAKPOINT.equals(action)) {
				action = args[j++];
				/* find breakpoint(s) */
				String sourceFile = resolveSourceFile(Util.decodeData(args[j++]));
				int lineNumber = -1;
				try {
					lineNumber = Integer.parseInt(args[j++]);
				} catch (NumberFormatException e) {
					JSDebugPlugin.log(e);
				}
				try {
					IBreakpoint breakpoint = findBreakpointAt(sourceFile, lineNumber);
					if (CREATE.equals(action) || CHANGE.equals(action)) {

						boolean enabled = "1".equals(args[j++]); //$NON-NLS-1$
						int hitCount = -1;
						try {
							hitCount = Integer.parseInt(args[j++]);
						} catch (NumberFormatException e) {
							JSDebugPlugin.log(e);
						}
						String condition = Util.decodeData(args[j++]);
						boolean conditionOnTrue = "1".equals(args[j++]); //$NON-NLS-1$

						if (breakpoint == null) {
							Map<String, Object> attributes = new HashMap<String, Object>();
							attributes.put(IBreakpoint.ENABLED, Boolean.valueOf(enabled));
							if (hitCount != -1) {
								attributes.put(IJSDebugConstants.BREAKPOINT_HIT_COUNT, new Integer(hitCount));
							}
							if (condition.length() != 0) {
								attributes.put(IJSDebugConstants.BREAKPOINT_CONDITION, condition);
								attributes.put(IJSDebugConstants.BREAKPOINT_CONDITION_ENABLED, Boolean.TRUE);
								attributes.put(IJSDebugConstants.BREAKPOINT_CONDITION_SUSPEND_ON_TRUE, Boolean
										.valueOf(conditionOnTrue));
							}

							/* create breakpoint */
							Object resource = findSourceResource(sourceFile);
							if (resource instanceof IResource) {
								/*
								 * XXX: temporary solution to prevent breakpoint
								 * changes be sent back to host
								 */
								try {
									ignoreBreakpointCreation = true;
									breakpoint = JSDebugModel.createLineBreakpoint((IResource) resource, lineNumber,
											attributes, true);
								} finally {
									ignoreBreakpointCreation = false;
								}
							} else if (resource instanceof IUniformResource) {
								try {
									ignoreBreakpointCreation = true;
									breakpoint = JSDebugModel.createLineBreakpoint((IUniformResource) resource, lineNumber,
											attributes, true);
								} finally {
									ignoreBreakpointCreation = false;
								}
							}
						} else if (CHANGE.equals(action)) {
							if (breakpoint.isEnabled() != enabled) {
								skipOperationOnBreakpoint = breakpoint;
								breakpoint.setEnabled(enabled);
							}
							if (breakpoint instanceof IJSLineBreakpoint) {
								IJSLineBreakpoint lineBreakpoint = (IJSLineBreakpoint) breakpoint;
								if (lineBreakpoint.getHitCount() != hitCount) {
									skipOperationOnBreakpoint = breakpoint;
									lineBreakpoint.setHitCount(hitCount);
								}
								if (!condition.equals(lineBreakpoint.getCondition())) {
									skipOperationOnBreakpoint = breakpoint;
									lineBreakpoint.setCondition(condition);
									lineBreakpoint.setConditionEnabled(condition.length() != 0);
								}
								if (lineBreakpoint.isConditionSuspendOnTrue() != conditionOnTrue) {
									skipOperationOnBreakpoint = breakpoint;
									lineBreakpoint.setConditionSuspendOnTrue(conditionOnTrue);
								}
							}

						}
					} else if (REMOVE.equals(action) && breakpoint != null) {
						skipOperationOnBreakpoint = breakpoint;
						breakpoint.delete();
					}
				} catch (CoreException e) {
					JSDebugPlugin.log(e);
				}
				return;
			} else if (OPENED.equals(action)) {
				mainFile = resolveSourceFile(Util.decodeData(args[1]));
				DebugEvent event = new DebugEvent(this, DebugEvent.MODEL_SPECIFIC, IJSDebugConstants.DEBUG_EVENT_URL_OPENED);
				event.setData(mainFile);
				fireEvent(event);
				return;
			}
			if (threads.length > 0) {
				threads[0].handleMessage(args);
			}
		}

		/* (non-Javadoc)
		 * @see com.aptana.js.debug.core.internal.model.DebugConnection.IHandler#handleShutdown()
		 */
		public void handleShutdown() {
			try {
				shutdown();
			} catch (DebugException e) {
				JSDebugPlugin.log(e);
			}
		}
		
	}
}
