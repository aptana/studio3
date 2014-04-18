/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable variableDeclaredInLoop

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

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
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

import com.aptana.core.CorePlugin;
import com.aptana.core.IURIMapper;
import com.aptana.core.io.efs.EFSUtils;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.resources.IUniformResource;
import com.aptana.core.resources.IUniformResourceMarker;
import com.aptana.core.sourcemap.ISourceMap;
import com.aptana.core.sourcemap.ISourceMapRegistry;
import com.aptana.core.sourcemap.ISourceMapResult;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.debug.core.DebugCorePlugin;
import com.aptana.debug.core.DetailFormatter;
import com.aptana.debug.core.IDebugCoreConstants;
import com.aptana.debug.core.IDebugScopes;
import com.aptana.debug.core.IDetailFormattersChangeListener;
import com.aptana.debug.core.sourcelookup.IFileContentRetriever;
import com.aptana.js.debug.core.IJSDebugConstants;
import com.aptana.js.debug.core.ILaunchConfigurationConstants;
import com.aptana.js.debug.core.JSDebugPlugin;
import com.aptana.js.debug.core.internal.Util;
import com.aptana.js.debug.core.internal.model.xhr.XHRService;
import com.aptana.js.debug.core.model.IJSConnection;
import com.aptana.js.debug.core.model.IJSDebugConnectionHandler;
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
public class JSDebugTarget extends JSDebugElement implements IJSDebugTarget, IBreakpointManagerListener,
		IDetailFormattersChangeListener
{

	private static final String UPDATE = "update"; //$NON-NLS-1$
	private static final String VERSION = "version"; //$NON-NLS-1$
	private static final String JAVASCRIPT = "javascript"; //$NON-NLS-1$
	private static final String APP = "app"; //$NON-NLS-1$
	private static final String JAVASCRIPT_SCHEME = "javascript:"; //$NON-NLS-1$
	private static final String OPENED = "opened"; //$NON-NLS-1$
	private static final String HTTP = "http"; //$NON-NLS-1$
	private static final String FILE = "file"; //$NON-NLS-1$
	private static final String EXCEPTION_0_1 = "exception*{0}*{1}"; //$NON-NLS-1$
	private static final String BREAKPOINT_0_1_2_3 = "breakpoint*{0}*{1}*{2}{3}"; //$NON-NLS-1$
	private static final String WATCHPOINT_0_1_2 = "watchpoint*{0}*{1}*{2}"; //$NON-NLS-1$
	private static final String DETAILS_0 = "details*{1}"; //$NON-NLS-1$
	private static final String DETAILS_0_V2 = "details*{0}*{1}"; //$NON-NLS-1$
	private static final String SET_VALUE_0_1 = "setValue*{1}*{2}"; //$NON-NLS-1$
	private static final String SET_VALUE_0_1_V2 = "setValue*{0}*{1}*{2}"; //$NON-NLS-1$
	private static final String EVAL_0 = "eval[{0}]"; //$NON-NLS-1$
	private static final String EVAL_0_1 = "eval*{1}*{2}"; //$NON-NLS-1$
	private static final String EVAL_0_1_V2 = "eval*{0}*{1}*{2}"; //$NON-NLS-1$
	private static final String RESULT = "result"; //$NON-NLS-1$
	private static final String FRAME_0 = "frame[{0,number,integer}]"; //$NON-NLS-1$
	private static final String VARIABLES_0 = "variables*{1}"; //$NON-NLS-1$
	private static final String VARIABLES_0_V2 = "variables*{0}*{1}"; //$NON-NLS-1$
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
	private static final String WARN = "warn"; //$NON-NLS-1$
	private static final String TRACE = "trace"; //$NON-NLS-1$
	private static final String SRC = "src"; //$NON-NLS-1$
	private static final String BREAKPOINT = "breakpoint"; //$NON-NLS-1$
	private static final String SCRIPTS = "scripts"; //$NON-NLS-1$
	private static final String THREADS = "threads"; //$NON-NLS-1$
	private static final String CLIENT = "client"; //$NON-NLS-1$
	private static final String XHR = "xhr"; //$NON-NLS-1$
	private static final String LOG = "log"; //$NON-NLS-1$
	private static final String SUCCESS = "success"; //$NON-NLS-1$
	private static final String GET_SOURCE_0 = "getSource*{0}"; //$NON-NLS-1$
	private static final String STEP_FILTERS_ENABLED2 = "stepFiltersEnabled"; //$NON-NLS-1$
	private static final String BYPASS_CONSTRUCTORS = "bypassConstructors"; //$NON-NLS-1$
	private static final String SUSPEND_ON_KEYWORDS = "suspendOnKeywords"; //$NON-NLS-1$
	private static final String SUSPEND_ON_ERRORS = "suspendOnErrors"; //$NON-NLS-1$
	private static final String SUSPEND_ON_EXCEPTIONS = "suspendOnExceptions"; //$NON-NLS-1$
	private static final String SUSPEND_ON_FIRST_LINE = "suspendOnFirstLine"; //$NON-NLS-1$

	private static final char VARIABLE_FLAG_WRITABLE = 'w';
	private static final char VARIABLE_FLAG_CONST = 'c';
	private static final char VARIABLE_FLAG_LOCAL = 'l';
	private static final char VARIABLE_FLAG_ARG = 'a';
	private static final char VARIABLE_FLAG_EXCEPTION = 'e';

	private static final char WATCHPOINT_FLAG_READ = 'r';
	private static final char WATCHPOINT_FLAG_WRITE = 'w';

	private static final int PROTOCOL_VERSION_MIN = 0;
	private static final int PROTOCOL_VERSION_MAX = 2;

	protected static final String DEFAULT_THREAD_ID = "0"; //$NON-NLS-1$

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

	/**
	 * Hack used to retrieve the platform used for the debug session.
	 */
	private static final String DEPLOY_TARGET = "ATTR_TITANIUM_DEPLOY_TARGET"; //$NON-NLS-1$

	private static boolean checkUpdate = true;
	private IJSConnection connection;
	private int stepFilterMask = 0;
	private String[] stepFilters = null;

	private ILaunch launch;
	private String label;
	private IProcess process;
	private IURIMapper uriMapper;
	private Map<String, JSDebugThread> threads = new HashMap<String, JSDebugThread>();
	private IFileContentRetriever fileContentRetriever;
	private XHRService xhrService;
	private Map<URI, IJSScriptElement> topScriptElements = new HashMap<URI, IJSScriptElement>();
	private Map<Integer, IJSScriptElement> scripts = new HashMap<Integer, IJSScriptElement>();
	private List<IBreakpoint> runToLineBreakpoints = new ArrayList<IBreakpoint>();
	private Map<String, URI> sourceResolveCache = new HashMap<String, URI>(64);
	private URI mainFile = null;
	private IBreakpoint skipOperationOnBreakpoint = null;
	private boolean ignoreBreakpointCreation = false;
	private boolean contentChanged = false;
	private int protocolVersion;
	private ISourceMap sourceMap;

	private Job updateContentJob = new Job("Debugger Content Update") { //$NON-NLS-1$
		{
			setPriority(Job.INTERACTIVE);
			EclipseUtil.setSystemForJob(this);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor)
		{
			if (monitor.isCanceled())
			{
				return Status.CANCEL_STATUS;
			}
			if (connection == null || !connection.isConnected())
			{
				return Status.OK_STATUS;
			}
			try
			{
				boolean changed = false;
				synchronized (this)
				{
					if (contentChanged)
					{
						changed = true;
						contentChanged = false;
					}
				}
				if (changed)
				{
					fireChangeEvent(DebugEvent.CONTENT);
				}
				return Status.OK_STATUS;
			}
			finally
			{
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
	public JSDebugTarget(ILaunch launch, IProcess process, IURIMapper uriMapper, IJSConnection connection,
			boolean debugMode) throws CoreException
	{
		this(launch, null, process, uriMapper, connection, debugMode);
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
	public JSDebugTarget(ILaunch launch, String label, IProcess process, IURIMapper uriMapper,
			IJSConnection connection, boolean debugMode) throws CoreException
	{
		super(null);
		this.launch = launch;
		this.label = label;
		this.process = process;
		this.connection = connection;
		this.uriMapper = uriMapper;
		initSourceMapping();
		try
		{
			if (debugMode)
			{
				launch.addDebugTarget(this);
			}
			else
			{
				/* TODO: do some refactoring here */
				if (process instanceof JSDebugProcess)
				{
					((JSDebugProcess) process).setDebugTarget(this);
				}
			}
			init(debugMode);
		}
		catch (CoreException e)
		{
			shutdown();
			throw e;
		}
		catch (Exception e)
		{
			shutdown();
			throwDebugException(e);
		}
	}

	/**
	 * Initialize the source mapping. Load any contributed source-mapper by the project that is assigned to this launch.
	 * This loading may result in a <code>null</code> sourceMap instance in case there is no contributed mapper for the
	 * project.
	 */
	private void initSourceMapping()
	{
		ISourceMapRegistry sourceMapRegistry = CorePlugin.getDefault().getSourceMapRegistry();
		try
		{
			String projectName = launch.getLaunchConfiguration().getAttribute(
					ILaunchConfigurationConstants.ATTR_PROJECT_NAME, (String) null);
			if (projectName != null)
			{
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
				String platform = launch.getAttribute(DEPLOY_TARGET);
				sourceMap = sourceMapRegistry.getSourceMap(project, platform);
			}
		}
		catch (CoreException e)
		{
			IdeLog.logError(JSDebugPlugin.getDefault(), e);
		}
	}

	/**
	 * Computes and returns an {@link ISourceMapResult} for the given source URL and line.
	 * 
	 * @param generatedLocation
	 * @param sourceLine
	 * @return an {@link ISourceMapResult}. <code>null</code> if there is not mapping.
	 */
	public ISourceMapResult getOriginalMappedLocation(URI generatedLocation, int sourceLine)
	{
		if (sourceMap == null || generatedLocation == null || sourceLine < 0)
		{
			return null;
		}
		try
		{
			IResource resource = ResourcesPlugin.getWorkspace().getRoot()
					.findMember(Path.fromOSString(generatedLocation.getPath()));
			return sourceMap.getOriginalMapping(resource, sourceLine);
		}
		catch (Exception e)
		{
			IdeLog.logError(JSDebugPlugin.getDefault(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.js.debug.core.model.IJSDebugTarget#getConnection()
	 */
	public IJSConnection getConnection()
	{
		return connection;
	}

	/* package */int getProtocolVersion()
	{
		return protocolVersion;
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter)
	{
		if (adapter == IFileContentRetriever.class)
		{
			return getFileContentRetriever();
		}
		if (adapter == IXHRService.class)
		{
			return xhrService;
		}
		return super.getAdapter(adapter);
	}

	/**
	 * getFileContentRetriever
	 * 
	 * @return IFileContentRetriever
	 */
	private IFileContentRetriever getFileContentRetriever()
	{
		if (fileContentRetriever == null)
		{
			fileContentRetriever = new IFileContentRetriever()
			{
				public InputStream getContents(URI uri) throws CoreException
				{
					String[] args = connection.sendCommandAndWait(MessageFormat.format(GET_SOURCE_0,
							Util.encodeData(uri.toString())));
					if (args != null && SUCCESS.equals(args[1]))
					{
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
	private void handleLog(String[] args)
	{
		String log = args[1];
		String text = Util.decodeData(args[2]);
		if (args.length >= 4)
		{
			StringBuffer sb = new StringBuffer(text);
			String type = args[3];
			if (SRC.equals(type) && args.length >= 6)
			{
				URI fileName = resolveSourceFile(Util.decodeData(args[4]));
				sb.append(MessageFormat.format(" ({0}:{1})", fileName.getPath(), args[5])); //$NON-NLS-1$
			}
			else if (TRACE.equals(type))
			{
				sb.append('\n');
				for (int i = 4; i < args.length; ++i)
				{
					String[] subargs = args[i].split(SUBARGS_SPLIT);
					if (subargs[0].length() == 0)
					{
						subargs[0] = MessageFormat.format("[{0}]", //$NON-NLS-1$
								i == args.length - 1 ? Messages.JSDebugTarget_TopLevelScript
										: Messages.JSDebugTarget_EvalScript);
					}
					URI fileName = resolveSourceFile(Util.decodeData(subargs[2]));
					sb.append(MessageFormat.format("\tat {0}({1}) ({2}:{3})\n", //$NON-NLS-1$
							Util.decodeData(subargs[0]), Util.decodeData(subargs[1]), fileName.getPath(), subargs[3]));
				}
			}
			text = sb.toString();
		}
		if (!text.endsWith("\n")) { //$NON-NLS-1$
			text += "\n"; //$NON-NLS-1$
		}
		try
		{
			if (ERR.equals(log) || EXCEPTION.equals(log))
			{
				OutputStream err = ((JSDebugProcess) process).getStream(IDebugCoreConstants.ID_STANDARD_ERROR_STREAM);
				if (err != null)
				{
					err.write(text.getBytes());
				}
			}
			else
			{
				if (WARN.equals(log))
				{
					log = IJSDebugConstants.ID_WARNING_STREAM;
				}
				OutputStream out = ((JSDebugProcess) process).getStream(log);
				if (out == null)
				{
					out = ((JSDebugProcess) process).getStream(IDebugCoreConstants.ID_STANDARD_OUTPUT_STREAM);
				}
				if (out != null)
				{
					out.write(text.getBytes());
				}
			}
		}
		catch (IOException e)
		{
			JSDebugPlugin.log(e);
		}
	}

	/**
	 * handleXHR
	 * 
	 * @param args
	 */
	private void handleXHR(String[] args)
	{
		int j = 1;
		String rid = args[j++];
		String cmd = args[j++];
		if (START.equals(cmd))
		{
			String method = args[j++];
			String url = Util.decodeData(args[j++]);
			String[][] headers = getXHRHeaders(Util.decodeData(args[j++]));
			String body = Util.decodeData(args[j++]);

			xhrService.openRequest(rid, method, url, false);
			xhrService.setRequestHeaders(rid, headers);
			xhrService.setRequestBody(rid, body);
		}
		else if (LOAD.equals(cmd))
		{
			int statusCode = -1;
			try
			{
				statusCode = Integer.parseInt(args[j++]);
			}
			catch (NumberFormatException e)
			{
			}

			String statusText = Util.decodeData(args[j++]);
			String[][] headers = getXHRHeaders(Util.decodeData(args[j++]));
			String response = Util.decodeData(args[j++]);

			xhrService.setResponseStatus(rid, statusCode, statusText);
			xhrService.setResponseHeaders(rid, headers);
			xhrService.setResponseBody(rid, response);
		}
		/* XXX: obsoleted XHR commands below */
		else if (OPEN.equals(cmd))
		{
			String method = args[j++];
			String url = Util.decodeData(args[j++]);
			String auth = args[j++];
			xhrService.openRequest(rid, method, url, AUTH.equals(auth));
		}
		else if (HEADERS.equals(cmd))
		{
			String[][] headers = getXHRHeaders(Util.decodeData(args[j++]));
			xhrService.setRequestHeaders(rid, headers);
		}
		else if (SEND.equals(cmd))
		{
			String body = Util.decodeData(args[j++]);
			xhrService.setRequestBody(rid, body);
		}
		else if (LOADED.equals(cmd))
		{
			int statusCode = -1;
			try
			{
				statusCode = Integer.parseInt(args[j++]);
			}
			catch (NumberFormatException e)
			{
			}

			String statusText = Util.decodeData(args[j++]);
			String[][] headers = getXHRHeaders(Util.decodeData(args[j++]));
			xhrService.setResponseHeaders(rid, headers);
			xhrService.setResponseStatus(rid, statusCode, statusText);
		}
		else if (COMPLETED.equals(cmd))
		{
			String response = Util.decodeData(args[j++]);
			xhrService.setResponseBody(rid, response);
		}
		else if (ERROR.equals(cmd))
		{
			xhrService.setError(rid);
		}
	}

	/**
	 * getXHRHeaders
	 * 
	 * @param string
	 * @return String[][]
	 */
	private static String[][] getXHRHeaders(String string)
	{
		String[] headers = string.split("\\n"); //$NON-NLS-1$
		List<String[]> list = new ArrayList<String[]>(headers.length);
		for (String header : headers)
		{
			String value = StringUtil.EMPTY;
			int pos = header.indexOf(": "); //$NON-NLS-1$
			if (pos != -1)
			{
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
	private void handleClientAction(String[] args)
	{
		int j = 1;
		String action = args[j++];
		if (SUSPEND.equals(action))
		{
			try
			{
				if (canSuspend())
				{
					suspend();
				}
			}
			catch (DebugException ignore)
			{
			}
		}
		else if (TERMINATE.equals(action))
		{
			try
			{
				if (canTerminate())
				{
					terminate();
				}
			}
			catch (DebugException ignore)
			{
			}
		}
		else if (OPEN.equals(action))
		{
			URI fileName = resolveSourceFile(Util.decodeData(args[j++]));
			if (fileName != null)
			{
				Object sourceElement = null;
				ISourceLocator locator = launch.getSourceLocator();
				if (locator instanceof ISourceLookupDirector)
				{
					sourceElement = ((ISourceLookupDirector) locator).getSourceElement(fileName);
				}
				if (sourceElement != null)
				{
					DebugCorePlugin.openInEditor(sourceElement);
				}
			}
		}
	}

	/**
	 * handleScripts
	 * 
	 * @param args
	 */
	private void handleScripts(String[] args)
	{
		String action = args[1];
		if (CREATED.equals(action))
		{
			for (int i = 2; i < args.length; ++i)
			{
				int j = 0;
				String[] subargs = args[i].split(SUBARGS_SPLIT);
				if (subargs.length < 5)
				{
					JSDebugPlugin.log(MessageFormat.format("Missing fields in response: <{0}>", args[i])); //$NON-NLS-1$
					continue;
				}
				int scriptTag = -1;
				try
				{
					scriptTag = Integer.parseInt(subargs[j++]);
				}
				catch (NumberFormatException e)
				{
				}
				String source = Util.decodeData(subargs[j++]);
				if (source.length() == 0 || "[Eval-script]".equals(source) //$NON-NLS-1$
						|| source.startsWith("javascript:")) { //$NON-NLS-1$ 
					continue;
				}
				URI fileName = resolveSourceFile(source);
				String scriptName = Util.decodeData(subargs[j++]);
				int baseLine = -1;
				int lineExtent = -1;
				try
				{
					baseLine = Integer.parseInt(subargs[j++]);
					lineExtent = Integer.parseInt(subargs[j++]);
				}
				catch (NumberFormatException e)
				{
				}
				if (scriptName.length() == 0)
				{
					continue; // skip empty scripts
				}
				JSDebugScriptElement topScriptElement = (JSDebugScriptElement) topScriptElements.get(fileName);
				if (topScriptElement == null)
				{
					topScriptElement = new JSDebugTopScriptElement(this, fileName.getPath(), fileName);
					topScriptElements.put(fileName, topScriptElement);
				}
				JSDebugScriptElement scriptElement = new JSDebugScriptElement(this, scriptName, baseLine, lineExtent);
				topScriptElement.insertElement(scriptElement);
				if (scriptTag > 0)
				{
					scripts.put(Integer.valueOf(scriptTag), scriptElement);
				}
			}
			synchronized (updateContentJob)
			{
				contentChanged = true;
			}
			;
		}
		else if (DESTROYED.equals(action))
		{
			int j = 0;
			String[] subargs = args[2].split(SUBARGS_SPLIT);
			int scriptTag = -1;
			try
			{
				scriptTag = Integer.parseInt(subargs[j++]);
			}
			catch (NumberFormatException e)
			{
			}
			URI fileName = resolveSourceFile(Util.decodeData(subargs[j++]));
			JSDebugScriptElement topScriptElement = (JSDebugScriptElement) topScriptElements.get(fileName);
			if (scriptTag > 0)
			{
				JSDebugScriptElement scriptElement = (JSDebugScriptElement) scripts.remove(Integer.valueOf(scriptTag));
				if (topScriptElement != null && scriptElement != null)
				{
					topScriptElement.removeElement(scriptElement);
				}
			}
		}
		else if (RESOLVED.equals(action))
		{
			for (int i = 2; i < args.length; ++i)
			{
				int j = 0;
				String[] subargs = args[i].split(SUBARGS_SPLIT);
				int scriptTag = -1;
				try
				{
					scriptTag = Integer.parseInt(subargs[j++]);
				}
				catch (NumberFormatException e)
				{
				}
				String scriptName = Util.decodeData(subargs[j++]);
				if (scriptTag < 0 || scriptName.length() == 0)
				{
					continue;
				}
				JSDebugScriptElement scriptElement = (JSDebugScriptElement) scripts.get(Integer.valueOf(scriptTag));
				if (scriptElement != null)
				{
					scriptElement.setName(scriptName);
					scriptElement.fireChangeEvent(DebugEvent.STATE);
				}
			}

		}
	}

	/**
	 * handleThreads
	 * 
	 * @param args
	 */
	private void handleThreads(String[] args)
	{
		String action = args[1];
		String threadId = args[2];
		if (CREATED.equals(action))
		{
			if (threadId.length() > 0 && !threads.containsKey(threadId))
			{
				String label = args[3];
				JSDebugThread thread = new JSDebugThread(this, threadId, label.length() > 0 ? label : null);
				threads.put(threadId, thread);
				thread.fireCreationEvent();
				fireChangeEvent(DebugEvent.CONTENT);
			}
		}
		else if (DESTROYED.equals(action))
		{
			JSDebugThread thread = threads.get(threadId);
			if (thread != null)
			{
				threads.remove(threadId);
				thread.fireTerminateEvent();
				fireChangeEvent(DebugEvent.CONTENT);
			}
		}
	}

	/**
	 * @see org.eclipse.debug.core.model.IDebugElement#getLaunch()
	 */
	public ILaunch getLaunch()
	{
		return launch;
	}

	/**
	 * @see org.eclipse.debug.core.model.IDebugElement#getDebugTarget()
	 */
	public IDebugTarget getDebugTarget()
	{
		return this;
	}

	/**
	 * @see org.eclipse.debug.core.model.IDebugTarget#getProcess()
	 */
	public IProcess getProcess()
	{
		return process;
	}

	/**
	 * @see org.eclipse.debug.core.model.IDebugTarget#getThreads()
	 */
	public IThread[] getThreads() throws DebugException
	{
		return threads.values().toArray(new IThread[threads.size()]);
	}

	/**
	 * @see org.eclipse.debug.core.model.IDebugTarget#hasThreads()
	 */
	public boolean hasThreads() throws DebugException
	{
		return !threads.isEmpty();
	}

	/**
	 * @see org.eclipse.debug.core.model.IDebugTarget#getName()
	 */
	public String getName() throws DebugException
	{
		return label != null ? label : Messages.JSDebugTarget_JSDebugger;
	}

	/**
	 * @see org.eclipse.debug.core.model.IDebugTarget#supportsBreakpoint(org.eclipse.debug.core.model.IBreakpoint)
	 */
	public boolean supportsBreakpoint(IBreakpoint breakpoint)
	{
		if (breakpoint.getModelIdentifier().equals(getModelIdentifier()))
		{
			return true;
		}
		return false;
	}

	/**
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	public boolean canTerminate()
	{
		return !isTerminated();
	}

	/**
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	public boolean isTerminated()
	{
		return connection.isTerminated();
	}

	/**
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	public void terminate() throws DebugException
	{
		if (isTerminated())
		{
			return;
		}
		connection.sendCommand(TERMINATE);
	}

	/**
	 * @see org.eclipse.debug.core.model.ISuspendResume#canResume()
	 */
	public boolean canResume()
	{
		return isSuspended();
	}

	/**
	 * @see org.eclipse.debug.core.model.ISuspendResume#canSuspend()
	 */
	public boolean canSuspend()
	{
		return !isSuspended();
	}

	/**
	 * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
	 */
	public boolean isSuspended()
	{
		for (JSDebugThread thread : threads.values())
		{
			if (!thread.isInSuspendState())
			{
				return false;
			}
		}
		return !threads.isEmpty();
	}

	/**
	 * @see org.eclipse.debug.core.model.ISuspendResume#resume()
	 */
	public void resume() throws DebugException
	{
		for (IThread thread : threads.values())
		{
			thread.resume();
		}
		fireResumeEvent(DebugEvent.CLIENT_REQUEST);
	}

	/**
	 * @see org.eclipse.debug.core.model.ISuspendResume#suspend()
	 */
	public void suspend() throws DebugException
	{
		if (isDisconnected())
		{
			return;
		}
		for (IThread thread : threads.values())
		{
			thread.suspend();
		}
		fireSuspendEvent(DebugEvent.CLIENT_REQUEST);
	}

	/**
	 * @see org.eclipse.debug.core.IBreakpointListener#breakpointAdded(org.eclipse.debug.core.model.IBreakpoint)
	 */
	public void breakpointAdded(IBreakpoint breakpoint)
	{
		if (supportsBreakpoint(breakpoint) && breakpoint instanceof IJSLineBreakpoint)
		{
			try
			{
				if (((IJSLineBreakpoint) breakpoint).isRunToLine())
				{
					runToLineBreakpoints.add(breakpoint);
				}
			}
			catch (CoreException e)
			{
			}
		}
		IdeLog.logInfo(DebugCorePlugin.getDefault(),
				MessageFormat.format("Adding breakpoint for {0}", breakpoint.getMarker().getResource()),
				com.aptana.debug.core.IDebugScopes.DEBUG);
		handleBreakpoint(breakpoint, CREATE);
	}

	/**
	 * @see org.eclipse.debug.core.IBreakpointListener#breakpointRemoved(org.eclipse.debug.core.model.IBreakpoint,
	 *      org.eclipse.core.resources.IMarkerDelta)
	 */
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta)
	{
		if (supportsBreakpoint(breakpoint) && breakpoint instanceof IJSLineBreakpoint)
		{
			try
			{
				if (((IJSLineBreakpoint) breakpoint).isRunToLine())
				{
					runToLineBreakpoints.remove(breakpoint);
				}
			}
			catch (CoreException e)
			{
			}
		}
		handleBreakpoint(breakpoint, REMOVE);
	}

	/**
	 * @see org.eclipse.debug.core.IBreakpointListener#breakpointChanged(org.eclipse.debug.core.model.IBreakpoint,
	 *      org.eclipse.core.resources.IMarkerDelta)
	 */
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta)
	{
		/* TODO: check delta to use right operation (create/change) */
		handleBreakpoint(breakpoint, CHANGE);
	}

	/**
	 * @see org.eclipse.debug.core.model.IDisconnect#canDisconnect()
	 */
	public boolean canDisconnect()
	{
		return !isDisconnected();
	}

	/**
	 * @see org.eclipse.debug.core.model.IDisconnect#disconnect()
	 */
	public void disconnect() throws DebugException
	{
		connection.sendCommandAndWait(DISABLE);
		stopDebug();
	}

	/**
	 * @see org.eclipse.debug.core.model.IDisconnect#isDisconnected()
	 */
	public boolean isDisconnected()
	{
		return !connection.isConnected();
	}

	/**
	 * @see org.eclipse.debug.core.model.IMemoryBlockRetrieval#supportsStorageRetrieval()
	 */
	public boolean supportsStorageRetrieval()
	{
		return false;
	}

	/**
	 * @see org.eclipse.debug.core.model.IMemoryBlockRetrieval#getMemoryBlock(long, long)
	 */
	public IMemoryBlock getMemoryBlock(long startAddress, long length) throws DebugException
	{
		throwNotImplemented();
		return null;
	}

	/**
	 * @see com.aptana.js.debug.core.model.IJSDebugTarget#isFilterConstructors()
	 */
	public boolean isFilterConstructors()
	{
		return (stepFilterMask & FLAG_FILTER_CONSTRUCTORS) > 0;
	}

	/**
	 * @see com.aptana.js.debug.core.model.IJSDebugTarget#setFilterConstructors(boolean)
	 */
	public void setFilterConstructors(boolean filter)
	{
		if (filter)
		{
			stepFilterMask = stepFilterMask | FLAG_FILTER_CONSTRUCTORS;
		}
		else
		{
			stepFilterMask = stepFilterMask & (FLAG_FILTER_CONSTRUCTORS ^ XOR_MASK);
		}
	}

	/**
	 * @see org.eclipse.debug.core.model.IStepFilters#isStepFiltersEnabled()
	 */
	public boolean isStepFiltersEnabled()
	{
		return (stepFilterMask & FLAG_STEP_FILTERS_ENABLED) > 0;
	}

	/**
	 * @see org.eclipse.debug.core.model.IStepFilters#setStepFiltersEnabled(boolean)
	 */
	public void setStepFiltersEnabled(boolean enabled)
	{
		if (enabled)
		{
			stepFilterMask = stepFilterMask | FLAG_STEP_FILTERS_ENABLED;
		}
		else
		{
			stepFilterMask = stepFilterMask & (FLAG_STEP_FILTERS_ENABLED ^ XOR_MASK);
		}
		try
		{
			setOption(STEP_FILTERS_ENABLED2, Boolean.toString(isStepFiltersEnabled()));
		}
		catch (DebugException e)
		{
			JSDebugPlugin.log(e);
		}
	}

	/**
	 * @see org.eclipse.debug.core.model.IStepFilters#supportsStepFilters()
	 */
	public boolean supportsStepFilters()
	{
		return !isTerminated() && !isDisconnected();
	}

	/**
	 * @see com.aptana.js.debug.core.model.IJSDebugTarget#getStepFilters()
	 */
	public String[] getStepFilters()
	{
		return stepFilters;
	}

	/**
	 * @see com.aptana.js.debug.core.model.IJSDebugTarget#setStepFilters(java.lang.String[])
	 */
	public void setStepFilters(String[] list)
	{
		stepFilters = list;
	}

	/**
	 * @see com.aptana.js.debug.core.model.IJSDebugTarget#getAttribute(java.lang.String)
	 */
	public String getAttribute(String key)
	{
		return getLaunch().getAttribute(key);
	}

	/**
	 * @see com.aptana.js.debug.core.model.IJSDebugTarget#setAttribute(java.lang.String, java.lang.String)
	 */
	public void setAttribute(String key, String value)
	{
		getLaunch().setAttribute(key, value);
		try
		{
			handleAttribute(key);
		}
		catch (DebugException e)
		{
			JSDebugPlugin.log(e);
		}
	}

	/**
	 * handleAttribute
	 * 
	 * @param key
	 * @throws DebugException
	 */
	private void handleAttribute(String key) throws DebugException
	{
		String value = getAttribute(key);
		boolean boolValue = Boolean.valueOf(value).booleanValue();
		if (ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_FIRST_LINE.equals(key))
		{
			setOption(SUSPEND_ON_FIRST_LINE, Boolean.toString(boolValue));
		}
		else if (ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_EXCEPTIONS.equals(key))
		{
			setOption(SUSPEND_ON_EXCEPTIONS, Boolean.toString(boolValue));
		}
		else if (ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_ERRORS.equals(key))
		{
			setOption(SUSPEND_ON_ERRORS, Boolean.toString(boolValue));
		}
		else if (ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_DEBUGGER_KEYWORDS.equals(key))
		{
			setOption(SUSPEND_ON_KEYWORDS, Boolean.toString(boolValue));
		}
	}

	/**
	 * @see com.aptana.debug.core.debug.core.IDetailFormattersChangeListener#detailFormattersChanged()
	 */
	public void detailFormattersChanged()
	{
		try
		{
			handleDetailFormattersChange();
		}
		catch (DebugException e)
		{
			JSDebugPlugin.log(e);
		}
	}

	/**
	 * handleDetailFormattersChange
	 * 
	 * @throws DebugException
	 */
	private void handleDetailFormattersChange() throws DebugException
	{
		StringBuffer sb = new StringBuffer(DETAIL_FORMATTERS);
		for (DetailFormatter detailFormatter : JSDebugPlugin.getDefault().getDebugOptionsManager()
				.getDetailFormatters())
		{
			if (!detailFormatter.isEnabled())
			{
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
	private void init(boolean debugMode) throws CoreException
	{
		synchronized (this)
		{
			connection.start(new DebugConnectionHandler());
			updateContentJob.schedule();

			/* check debugger/protocol version */
			checkVersion();
		}

		if (true /* TODO: monitor XHR option */)
		{
			xhrService = new XHRService();
			setOption(MONITOR_XHR, Boolean.toString(true));
			if (process instanceof JSDebugProcess)
			{
				((JSDebugProcess) process).setXHRService(xhrService);
			}

		}

		if (debugMode)
		{
			fireCreationEvent();

			if (protocolVersion < 2)
			{
				JSDebugThread thread = new JSDebugThread(this, DEFAULT_THREAD_ID, null);
				threads.put(DEFAULT_THREAD_ID, thread);
				thread.fireCreationEvent();
				fireChangeEvent(DebugEvent.CONTENT);
			}

			handleAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_FIRST_LINE);
			handleAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_EXCEPTIONS);
			handleAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_ERRORS);
			handleAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_DEBUGGER_KEYWORDS);

			setOption(BYPASS_CONSTRUCTORS, Boolean.toString(isFilterConstructors()));
			setOption(STEP_FILTERS_ENABLED2, Boolean.toString(isStepFiltersEnabled()));
			if (stepFilters != null && stepFilters.length > 0)
			{
				StringBuffer sb = new StringBuffer(STEP_FILTERS);
				for (int i = 0; i < stepFilters.length; ++i)
				{
					sb.append(i != 0 ? '|' : '*').append(Util.encodeData(stepFilters[i]));
				}
				connection.sendCommandAndWait(sb.toString());
			}
			handleDetailFormattersChange();
			JSDebugPlugin.getDefault().getDebugOptionsManager().addChangeListener(this);

			/* restore breakpoints */
			for (IBreakpoint breakpoint : DebugPlugin.getDefault().getBreakpointManager()
					.getBreakpoints(getModelIdentifier()))
			{
				breakpointAdded(breakpoint);
			}

			// Register listeners
			DebugPlugin.getDefault().getBreakpointManager().addBreakpointManagerListener(this);
			DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this);

			connection.sendCommandAndWait(ENABLE);
		}
	}

	/**
	 * checkVersion
	 * 
	 * @throws DebugException
	 */
	private void checkVersion() throws DebugException
	{
		int protoVersion = 0;
		String version = null;
		String[] args = connection.sendCommandAndWait(VERSION);
		if (args != null && args.length >= 3 && args[1].charAt(0) != '!')
		{
			JSDebugPlugin.log(MessageFormat.format("Extension version: {0}; protocol v{1}", //$NON-NLS-1$
					args[2], args[1]));
			try
			{
				protoVersion = Integer.parseInt(args[1]);
			}
			catch (NumberFormatException e)
			{
			}
			version = args[2];
		}
		if ((protoVersion < PROTOCOL_VERSION_MIN) || (protoVersion > PROTOCOL_VERSION_MAX))
		{
			throwDebugException(MessageFormat.format(
					"Incompatible debugger extension protocol version {0} for [{1},{2}]", //$NON-NLS-1$
					Integer.toString(protoVersion), Integer.toString(PROTOCOL_VERSION_MIN),
					Integer.toString(PROTOCOL_VERSION_MAX)));
		}
		protocolVersion = protoVersion;
		if (checkUpdate)
		{
			boolean update = false;
			if (version != null)
			{
				String pluginVersion = (String) Platform.getBundle(JSDebugPlugin.PLUGIN_ID).getHeaders()
						.get(Constants.BUNDLE_VERSION);
				int index = pluginVersion.lastIndexOf('.');
				if (index != -1)
				{
					if (index >= version.length()
							|| !pluginVersion.substring(0, index).equals(version.substring(0, index)))
					{
						update = true;

					}
					else if (!pluginVersion.substring(index + 1).equals(version.substring(index + 1)))
					{
						try
						{
							if (Integer.parseInt(pluginVersion.substring(index + 1)) > Integer.parseInt(version
									.substring(index + 1)))
							{
								update = true;
							}
						}
						catch (NumberFormatException e)
						{
						}
					}
				}
				if (update)
				{
					args = connection.sendCommandAndWait(UPDATE);
					if (args != null && args.length >= 2)
					{
						JSDebugPlugin.log(MessageFormat.format("Extension update available: {0}", //$NON-NLS-1$
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
	private synchronized void setOption(String option, String value) throws DebugException
	{
		if (connection.isConnected())
		{
			connection.sendCommandAndWait(MessageFormat.format(OPTION_0_1, option, value));
		}
	}

	/**
	 * openURL
	 * 
	 * @param url
	 * @throws DebugException
	 */
	public void openURL(URL url) throws DebugException
	{
		if (connection.isConnected())
		{
			try
			{
				DebugEvent event = new DebugEvent(this, DebugEvent.MODEL_SPECIFIC,
						IJSDebugConstants.DEBUG_EVENT_URL_OPEN);
				URL fileUrl = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getPath());
				event.setData(resolveSourceFile(fileUrl.toExternalForm()));
				fireEvent(event);
			}
			catch (MalformedURLException e)
			{
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
	private void stopDebug() throws DebugException
	{
		if (connection == null || !connection.isConnected())
		{
			return;
		}
		connection.stop();
		updateContentJob.cancel();
		if (!threads.isEmpty())
		{
			try
			{
				for (JSDebugThread thread : threads.values())
				{
					thread.fireTerminateEvent();
				}
				threads.clear();
				topScriptElements.clear();
				scripts.clear();
				fireChangeEvent(DebugEvent.CONTENT);
			}
			finally
			{
				// Unregister listeners
				if (JSDebugPlugin.getDefault() != null)
				{
					JSDebugPlugin.getDefault().getDebugOptionsManager().removeChangeListener(this);
				}
				if (DebugPlugin.getDefault() != null)
				{
					DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(this);
					DebugPlugin.getDefault().getBreakpointManager().removeBreakpointManagerListener(this);
				}
			}

		}
	}

	/**
	 * shutdown
	 * 
	 * @throws DebugException
	 */
	private void shutdown() throws DebugException
	{
		try
		{
			stopDebug();
			if (connection != null)
			{
				connection.dispose();
			}
		}
		catch (IOException e)
		{
			throwDebugException(e);
		}
		finally
		{
			if (DebugPlugin.getDefault() != null)
			{
				fireTerminateEvent();
			}
		}
	}

	/**
	 * @see org.eclipse.debug.core.IBreakpointManagerListener#breakpointManagerEnablementChanged(boolean)
	 */
	public void breakpointManagerEnablementChanged(boolean enabled)
	{
		for (IBreakpoint breakpoint : DebugPlugin.getDefault().getBreakpointManager()
				.getBreakpoints(getModelIdentifier()))
		{
			if (enabled)
			{
				breakpointAdded(breakpoint);
			}
			else
			{
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
	protected IVariable[] loadVariables(String threadId, String qualifier) throws DebugException
	{
		if (!isThreadSuspended(threadId))
		{
			return new IVariable[0];
		}
		List<IVariable> list = new ArrayList<IVariable>();
		String[] args = connection.sendCommandAndWait(MessageFormat.format(protocolVersion >= 2 ? VARIABLES_0_V2
				: VARIABLES_0, threadId, Util.encodeData(qualifier)));
		if (args != null)
		{
			for (int i = 1; i < args.length; ++i)
			{
				String varData = args[i];
				int j = 0;
				if (varData.length() == 0)
				{
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
				ivalue = new JSDebugValue(this, threadId, q, type, complex, stringValue);
				list.add(new JSDebugVariable(this, threadId, q, name, ivalue, convertVariableFlags(flags)));
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
	protected Object evaluateExpression(String expression, IDebugElement context) throws DebugException
	{
		String threadId;
		String qualifier;
		Object result = null;
		// TODO: caching ?
		if (context instanceof JSDebugStackFrame)
		{
			qualifier = MessageFormat.format(FRAME_0, ((JSDebugStackFrame) context).getFrameId());
			threadId = ((JSDebugStackFrame) context).getThreadId();
		}
		else if (context instanceof JSDebugVariable)
		{
			qualifier = ((JSDebugVariable) context).getQualifier();
			threadId = ((JSDebugVariable) context).getThreadId();
		}
		else
		{
			return result;
		}
		if (!isThreadSuspended(threadId))
		{
			return null;
		}
		String command = MessageFormat.format(protocolVersion >= 2 ? EVAL_0_1_V2 : EVAL_0_1, threadId,
				Util.encodeData(qualifier), Util.encodeData(expression));
		String[] args = connection.sendCommandAndWait(command);
		String status = args != null && args.length > 1 ? args[1] : null;
		if (RESULT.equals(status))
		{
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
			result = new JSDebugValue(this, threadId, MessageFormat.format(EVAL_0, evalId), type, complex, stringValue);
		}
		else if (EXCEPTION.equals(status))
		{
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
	protected Object setValue(IVariable variable, IValue newValue) throws DebugException
	{
		String threadId;
		String qualifier;
		String vqualifier;
		Object result = null;
		if (variable instanceof JSDebugVariable)
		{
			qualifier = ((JSDebugVariable) variable).getQualifier();
			threadId = ((JSDebugVariable) variable).getThreadId();
		}
		else
		{
			return result;
		}
		if (newValue instanceof JSDebugValue)
		{
			vqualifier = ((JSDebugValue) newValue).getQualifier();
		}
		else
		{
			return result;
		}
		if (!isThreadSuspended(threadId))
		{
			return null;
		}
		String command = MessageFormat.format(protocolVersion >= 2 ? SET_VALUE_0_1_V2 : SET_VALUE_0_1, threadId,
				Util.encodeData(qualifier), vqualifier);
		String[] args = connection.sendCommandAndWait(command);
		if (args != null && args.length >= 3)
		{
			String status = args[1];
			if (RESULT.equals(status))
			{
				String[] subargs = args[2].split(SUBARGS_SPLIT);
				int j = 0;
				String type = subargs[j++];
				String flags = subargs[j++];
				String stringValue = Util.decodeData(subargs[j++]);
				boolean complex = flags.indexOf('o') != -1;
				result = new JSDebugValue(this, threadId, qualifier, type, complex, stringValue);
			}
			else if (EXCEPTION.equals(status))
			{
				result = new String[] { args[2] };
			}
		}
		return result;
	}

	/**
	 * @see com.aptana.js.debug.core.model.IJSDebugTarget#computeValueDetails(org.eclipse.debug.core.model.IValue)
	 */
	public String computeValueDetails(IValue value) throws DebugException
	{
		String threadId;
		String qualifier;
		String result = null;
		if (value instanceof JSDebugValue)
		{
			qualifier = ((JSDebugValue) value).getQualifier();
			threadId = ((JSDebugValue) value).getThreadId();
		}
		else
		{
			return value.getValueString();
		}
		if (!isThreadSuspended(threadId))
		{
			return StringUtil.EMPTY;
		}
		String command = MessageFormat.format(protocolVersion >= 2 ? DETAILS_0_V2 : DETAILS_0, threadId,
				Util.encodeData(qualifier));
		String[] args = connection.sendCommandAndWait(command);
		if (args != null && args.length >= 3)
		{
			String status = args[1];
			if (RESULT.equals(status))
			{
				result = Util.decodeData(args[2]);
			}
		}
		if (result == null)
		{
			result = value.getValueString();
		}
		if (result == null)
		{
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
	protected IVariable findVariable(String variableName, IDebugElement context) throws DebugException
	{
		if (Util.checkVariable(variableName))
		{
			Object result = evaluateExpression(variableName, context);
			if (result instanceof JSDebugValue)
			{
				return new JSDebugVariable(this, ((JSDebugValue) result).getThreadId(), null/* TODO? */, variableName,
						(IValue) result);
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
	private void handleBreakpoint(IBreakpoint breakpoint, String operation)
	{
		if (isDisconnected())
		{
			return;
		}
		if (breakpoint.equals(skipOperationOnBreakpoint))
		{
			skipOperationOnBreakpoint = null;
			return;
		}
		if (CREATE.equals(operation) && ignoreBreakpointCreation)
		{
			return;
		}
		if (supportsBreakpoint(breakpoint))
		{
			if (breakpoint instanceof IJSLineBreakpoint)
			{
				handleLineBreakpoint((IJSLineBreakpoint) breakpoint, operation);
			}
			else if (breakpoint instanceof IJSExceptionBreakpoint)
			{
				handleExceptionBreakpoint((IJSExceptionBreakpoint) breakpoint, operation);
			}
			else if (breakpoint instanceof IJSWatchpoint)
			{
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
	private void handleLineBreakpoint(IJSLineBreakpoint breakpoint, String operation)
	{
		IMarker marker = breakpoint.getMarker();
		// URL url = null;
		URI uri = null;
		int lineNumber = marker.getAttribute(IMarker.LINE_NUMBER, -1);
		String properties = StringUtil.EMPTY;
		try
		{
			URI fileName = null;
			if (marker instanceof IUniformResourceMarker)
			{
				fileName = ((IUniformResourceMarker) marker).getUniformResource().getURI();
			}
			else
			{
				IResource resource = marker.getResource();
				if (resource instanceof IWorkspaceRoot)
				{
					fileName = URI.create((String) marker.getAttribute(IJSDebugConstants.BREAKPOINT_LOCATION));
				}
				else
				{
					// Consult the sourcemap to see if there is a need to update the file path and line number before we
					// send this breakpoint.
					ISourceMapResult generatedMapping = getGeneratedMapping(resource, lineNumber);
					if (generatedMapping != null)
					{
						IdeLog.logInfo(DebugCorePlugin.getDefault(), MessageFormat.format(
								"Generated mapping while adding breakpoint for {0}:{1} is {2}:{3}", resource,
								lineNumber, generatedMapping.getFile(), generatedMapping.getLineNumber()),
								com.aptana.debug.core.IDebugScopes.DEBUG);
						IResource mappedResource = ResourcesPlugin.getWorkspace().getRoot()
								.findMember(resource.getProject().getFullPath().append(generatedMapping.getFile()));
						if (mappedResource != null)
						{
							resource = mappedResource;
							lineNumber = generatedMapping.getLineNumber();
						}
					}
					fileName = EFSUtils.getFileStore(resource).toURI();
				}
			}
			if (fileName != null && uriMapper != null)
			{
				uri = uriMapper.resolve(EFS.getStore(fileName));
			}
			if (uri == null && fileName != null)
			{
				try
				{
					if ("dbgsource".equals(fileName.getScheme())) { //$NON-NLS-1$
						uri = fileName;
						// url = new URL(null, fileName.toString(), DbgSourceURLStreamHandler.getDefault());
					}
					else
					{
						uri = fileName.toURL().toURI();
					}
				}
				catch (MalformedURLException e)
				{
				}
				catch (URISyntaxException e)
				{
				}
			}
		}
		catch (CoreException e)
		{
			JSDebugPlugin.log(e);
		}
		if (lineNumber == -1 || uri == null)
		{
			return;
		}

		boolean remove = REMOVE.equals(operation);
		if (!remove)
		{
			boolean enabled = false;
			try
			{
				enabled = breakpoint.isEnabled();
			}
			catch (CoreException ignore)
			{
			}
			int hitCount = marker.getAttribute(IJSDebugConstants.BREAKPOINT_HIT_COUNT, 0);
			boolean conditionEnabled = marker.getAttribute(IJSDebugConstants.BREAKPOINT_CONDITION_ENABLED, false);
			String condition = conditionEnabled ? marker.getAttribute(IJSDebugConstants.BREAKPOINT_CONDITION,
					StringUtil.EMPTY) : StringUtil.EMPTY;
			String suspendOnTrue = marker.getAttribute(IJSDebugConstants.BREAKPOINT_CONDITION_SUSPEND_ON_TRUE, true) ? "1" : "0"; //$NON-NLS-1$ //$NON-NLS-2$
			properties = MessageFormat.format("*{0}*{1}*{2}*{3}", //$NON-NLS-1$
					enabled ? "1" : "0", Integer.toString(hitCount), Util.encodeData(condition), suspendOnTrue); //$NON-NLS-1$ //$NON-NLS-2$
		}
		try
		{
			String[] args = connection.sendCommandAndWait(MessageFormat.format(BREAKPOINT_0_1_2_3, operation,
					Util.encodeData(uri.toString()), Integer.toString(lineNumber), properties));
			if (!remove && (args == null || args.length < 2 || !(operation + 'd').equals(args[1])))
			{
				breakpoint.setEnabled(false);
			}
		}
		catch (CoreException e)
		{
			JSDebugPlugin.log(e);
		}

	}

	/**
	 * Returns an {@link ISourceMapResult} that holds information about the generated file and line number.
	 * 
	 * @param resource
	 * @param lineNumber
	 * @return An {@link ISourceMapResult}. <code>null</code> if there is no mapping.
	 */
	private ISourceMapResult getGeneratedMapping(IResource resource, int lineNumber)
	{
		if (sourceMap == null)
		{
			return null;
		}
		try
		{
			return sourceMap.getGeneratedMapping(resource, lineNumber, 1);
		}
		catch (Exception e)
		{
			IdeLog.logError(JSDebugPlugin.getDefault(), "Error initializing the sourcemap", e); //$NON-NLS-1$
		}
		return null;
	}

	/**
	 * handleExceptionBreakpoint
	 * 
	 * @param breakpoint
	 * @param operation
	 */
	private void handleExceptionBreakpoint(IJSExceptionBreakpoint breakpoint, String operation)
	{
		IMarker marker = breakpoint.getMarker();
		String exceptionTypeName = marker.getAttribute(IJSDebugConstants.EXCEPTION_TYPE_NAME, StringUtil.EMPTY);
		if (exceptionTypeName == null || exceptionTypeName.length() == 0)
		{
			return;
		}
		IdeLog.logInfo(DebugCorePlugin.getDefault(), MessageFormat.format("Adding {0} exception breakpoint for {1}",
				exceptionTypeName, marker.getResource()), IDebugScopes.DEBUG);

		boolean enabled = false;
		try
		{
			enabled = breakpoint.isEnabled();
		}
		catch (CoreException ignore)
		{
		}
		if (!enabled)
		{
			operation = REMOVE;
		}

		enabled = !REMOVE.equals(operation);
		try
		{
			String[] args = connection.sendCommandAndWait(MessageFormat.format(EXCEPTION_0_1, operation,
					exceptionTypeName));
			if (enabled && (args == null || !(operation + 'd').equals(args[1])))
			{
				breakpoint.setEnabled(false);
			}
		}
		catch (CoreException e)
		{
			JSDebugPlugin.log(e);
		}

	}

	/**
	 * handleWatchpoint
	 * 
	 * @param watchpoint
	 * @param operation
	 */
	private void handleWatchpoint(IJSWatchpoint watchpoint, String operation)
	{
		IMarker marker = watchpoint.getMarker();
		String variableName = marker.getAttribute(IJSDebugConstants.WATCHPOINT_VARIABLE_ACCESSOR, StringUtil.EMPTY);
		boolean enabled = false;
		try
		{
			enabled = watchpoint.isEnabled();
		}
		catch (CoreException ignore)
		{
		}
		if (!enabled)
		{
			if (CREATE.equals(operation))
			{
				return;
			}
			operation = REMOVE;
		}
		enabled = !REMOVE.equals(operation);

		String kind = StringUtil.EMPTY;
		if (enabled)
		{
			try
			{
				if (watchpoint.isAccess())
				{
					kind += WATCHPOINT_FLAG_READ;
				}
				if (watchpoint.isModification())
				{
					kind += WATCHPOINT_FLAG_WRITE;
				}
			}
			catch (CoreException ignore)
			{
			}
		}
		try
		{
			String[] args = connection.sendCommandAndWait(MessageFormat.format(WATCHPOINT_0_1_2, operation,
					Util.encodeData(variableName), kind));
			if (enabled && (args == null || args.length < 2 || !(operation + 'd').equals(args[1])))
			{
				watchpoint.setEnabled(false);
			}
		}
		catch (CoreException e)
		{
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
	protected IBreakpoint findBreakpointAt(URI fileName, int lineNumber)
	{
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager()
				.getBreakpoints(getModelIdentifier());
		IBreakpoint breakpoint = findBreakpointIn(fileName, lineNumber, breakpoints);
		if (breakpoint != null)
		{
			return breakpoint;
		}
		if (!runToLineBreakpoints.isEmpty())
		{
			return findBreakpointIn(fileName, lineNumber,
					(IBreakpoint[]) runToLineBreakpoints.toArray(new IBreakpoint[runToLineBreakpoints.size()]));
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
	protected IBreakpoint findBreakpointIn(URI fileName, int lineNumber, IBreakpoint[] breakpoints)
	{
		for (IBreakpoint breakpoint : breakpoints)
		{
			if (getDebugTarget().supportsBreakpoint(breakpoint))
			{
				if (breakpoint instanceof ILineBreakpoint)
				{
					try
					{
						IMarker marker = breakpoint.getMarker();
						boolean fileMatched = false;
						if (marker instanceof IUniformResourceMarker)
						{
							URI breakpointURI = ((IUniformResourceMarker) marker).getUniformResource().getURI();
							fileMatched = /* new URI(Util.fixupURI( */fileName/* )) */.equals(breakpointURI);
						}
						else if (marker.getResource() instanceof IWorkspaceRoot)
						{
							URI breakpointURI = URI.create((String) marker
									.getAttribute(IJSDebugConstants.BREAKPOINT_LOCATION));
							fileMatched = /* new URI(Util.fixupURI( */fileName/* )) */.equals(breakpointURI);
						}
						else
						{
							IFileStore fileStore = EFS.getStore(fileName);
							IResource resource = (IResource) fileStore.getAdapter(IResource.class);
							if (resource != null)
							{
								fileMatched = resource.equals(marker.getResource());
							}
							else
							{
								File breakpointFile = marker.getResource().getLocation().toFile();
								fileMatched = breakpointFile.equals(fileStore.getAdapter(File.class));
							}
						}
						if (fileMatched && ((ILineBreakpoint) breakpoint).getLineNumber() == lineNumber)
						{
							return breakpoint;
						}
					}
					catch (CoreException e)
					{
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
	protected URI resolveSourceFile(String sourceFile)
	{
		URI resolved = sourceResolveCache.get(sourceFile);
		if (resolved != null)
		{
			return resolved;
		}
		try
		{
			URI uri = new URI(sourceFile);
			String scheme = uri.getScheme();
			if (FILE.equals(scheme))
			{
				File osFile = new File(uri.getSchemeSpecificPart());
				resolved = EFSUtils.getLocalFileStore(osFile).toURI();
			}
			else if (HTTP.equals(scheme) && uriMapper != null)
			{
				IFileStore fileStore = uriMapper.resolve(uri);
				if (fileStore != null)
				{
					resolved = fileStore.toURI();
				}
			}
			else if (APP.equals(scheme) && uriMapper != null)
			{
				IFileStore fileStore = uriMapper.resolve(uri);
				if (fileStore != null)
				{
					resolved = fileStore.toURI();
				}
			}
			else if (JAVASCRIPT.equals(scheme))
			{
				if (mainFile != null)
				{
					return mainFile;
				}
			}
			if (resolved != null)
			{
				sourceResolveCache.put(sourceFile, resolved);
				return resolved;
			}
		}
		catch (URISyntaxException e)
		{
			if (sourceFile.startsWith(JAVASCRIPT_SCHEME))
			{
				if (mainFile != null)
				{
					return mainFile;
				}
			}
			JSDebugPlugin.log(e);
		}
		try
		{
			return new URI(sourceFile);
		}
		catch (URISyntaxException e)
		{
			return null;
		}
	}

	/**
	 * findSourceResource
	 * 
	 * @param sourceFile
	 * @return Object
	 * @throws CoreException
	 */
	private Object findSourceResource(URI fileName) throws CoreException
	{
		ISourceLocator locator = launch.getSourceLocator();
		if (locator instanceof ISourceLookupDirector)
		{
			ISourceLookupDirector lookupDirector = (ISourceLookupDirector) locator;
			Object[] result = lookupDirector.findSourceElements(fileName);
			if (result != null && result.length > 0)
			{
				Object resource = result[0];
				if (resource instanceof IResource)
				{
					return resource;
				}
				if (!(resource instanceof IUniformResource) && resource instanceof IAdaptable)
				{
					Object adopted = ((IAdaptable) resource).getAdapter(IUniformResource.class);
					if (adopted != null)
					{
						resource = adopted;
					}
				}
				return resource;
			}
		}
		return null;
	}

	/*
	 * convertVariableFlags
	 * @param string
	 * @return int
	 */
	private static int convertVariableFlags(String string)
	{
		int flags = 0;
		for (char c : string.toCharArray())
		{
			switch (c)
			{
				case VARIABLE_FLAG_WRITABLE:
					flags |= JSDebugVariable.FLAGS_MODIFIABLE;
					break;
				case VARIABLE_FLAG_CONST:
					flags |= JSDebugVariable.FLAGS_CONST;
					break;
				case VARIABLE_FLAG_LOCAL:
					flags |= JSDebugVariable.FLAGS_LOCAL;
					break;
				case VARIABLE_FLAG_ARG:
					flags |= JSDebugVariable.FLAGS_ARGUMENT;
					break;
				case VARIABLE_FLAG_EXCEPTION:
					flags |= JSDebugVariable.FLAGS_EXCEPTION;
					break;
				default:
					break;
			}
		}
		return flags;
	}

	private boolean isThreadSuspended(String threadId)
	{
		JSDebugThread thread = threads.get(threadId);
		if (thread != null)
		{
			return thread.isSuspended();
		}
		return false;
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSDebugTarget#getTopScriptElements()
	 */
	public IJSScriptElement[] getTopScriptElements()
	{
		return (IJSScriptElement[]) topScriptElements.values().toArray(new IJSScriptElement[topScriptElements.size()]);
	}

	private class DebugConnectionHandler implements IJSDebugConnectionHandler
	{

		/*
		 * (non-Javadoc)
		 * @see com.aptana.js.debug.core.internal.model.DebugConnection.IHandler#handleMessage(java.lang.String)
		 */
		public void handleMessage(String message)
		{
			String[] args = message.split(ARGS_SPLIT);
			int j = 0;
			String action = args[j++];
			if (LOG.equals(action))
			{
				handleLog(args);
				return;
			}
			else if (XHR.equals(action))
			{
				handleXHR(args);
				return;
			}
			else if (CLIENT.equals(action))
			{
				handleClientAction(args);
				return;
			}
			else if (SCRIPTS.equals(action))
			{
				handleScripts(args);
				return;
			}
			else if (THREADS.equals(action))
			{
				handleThreads(args);
				return;
			}
			else if (BREAKPOINT.equals(action))
			{
				action = args[j++];
				/* find breakpoint(s) */
				URI sourceFile = resolveSourceFile(Util.decodeData(args[j++]));
				int lineNumber = -1;
				try
				{
					lineNumber = Integer.parseInt(args[j++]);
				}
				catch (NumberFormatException e)
				{
					JSDebugPlugin.log(e);
				}
				try
				{
					IBreakpoint breakpoint = findBreakpointAt(sourceFile, lineNumber);
					if (CREATE.equals(action) || CHANGE.equals(action))
					{

						boolean enabled = "1".equals(args[j++]); //$NON-NLS-1$
						int hitCount = -1;
						try
						{
							hitCount = Integer.parseInt(args[j++]);
						}
						catch (NumberFormatException e)
						{
							JSDebugPlugin.log(e);
						}
						String condition = Util.decodeData(args[j++]);
						boolean conditionOnTrue = "1".equals(args[j++]); //$NON-NLS-1$

						if (breakpoint == null)
						{
							Map<String, Object> attributes = new HashMap<String, Object>();
							attributes.put(IBreakpoint.ENABLED, Boolean.valueOf(enabled));
							if (hitCount != -1)
							{
								attributes.put(IJSDebugConstants.BREAKPOINT_HIT_COUNT, Integer.valueOf(hitCount));
							}
							if (condition.length() != 0)
							{
								attributes.put(IJSDebugConstants.BREAKPOINT_CONDITION, condition);
								attributes.put(IJSDebugConstants.BREAKPOINT_CONDITION_ENABLED, Boolean.TRUE);
								attributes.put(IJSDebugConstants.BREAKPOINT_CONDITION_SUSPEND_ON_TRUE,
										Boolean.valueOf(conditionOnTrue));
							}

							/* create breakpoint */
							Object resource = findSourceResource(sourceFile);
							if (resource instanceof IResource)
							{
								/*
								 * XXX: temporary solution to prevent breakpoint changes be sent back to host
								 */
								try
								{
									ignoreBreakpointCreation = true;
									JSDebugModel.createLineBreakpoint((IResource) resource, lineNumber, attributes,
											true);
								}
								finally
								{
									ignoreBreakpointCreation = false;
								}
							}
							else if (resource instanceof IUniformResource)
							{
								try
								{
									ignoreBreakpointCreation = true;
									JSDebugModel.createLineBreakpoint((IUniformResource) resource, lineNumber,
											attributes, true);
								}
								finally
								{
									ignoreBreakpointCreation = false;
								}
							}
						}
						else if (CHANGE.equals(action))
						{
							if (breakpoint.isEnabled() != enabled)
							{
								skipOperationOnBreakpoint = breakpoint;
								breakpoint.setEnabled(enabled);
							}
							if (breakpoint instanceof IJSLineBreakpoint)
							{
								IJSLineBreakpoint lineBreakpoint = (IJSLineBreakpoint) breakpoint;
								if (lineBreakpoint.getHitCount() != hitCount)
								{
									skipOperationOnBreakpoint = breakpoint;
									lineBreakpoint.setHitCount(hitCount);
								}
								if (!condition.equals(lineBreakpoint.getCondition()))
								{
									skipOperationOnBreakpoint = breakpoint;
									lineBreakpoint.setCondition(condition);
									lineBreakpoint.setConditionEnabled(condition.length() != 0);
								}
								if (lineBreakpoint.isConditionSuspendOnTrue() != conditionOnTrue)
								{
									skipOperationOnBreakpoint = breakpoint;
									lineBreakpoint.setConditionSuspendOnTrue(conditionOnTrue);
								}
							}

						}
					}
					else if (REMOVE.equals(action) && breakpoint != null)
					{
						skipOperationOnBreakpoint = breakpoint;
						breakpoint.delete();
					}
				}
				catch (CoreException e)
				{
					JSDebugPlugin.log(e);
				}
				return;
			}
			else if (OPENED.equals(action))
			{
				mainFile = resolveSourceFile(Util.decodeData(args[1]));
				DebugEvent event = new DebugEvent(this, DebugEvent.MODEL_SPECIFIC,
						IJSDebugConstants.DEBUG_EVENT_URL_OPENED);
				event.setData(mainFile);
				fireEvent(event);
				return;
			}
			else if (JSDebugThread.SUSPENDED.equals(action) || JSDebugThread.RESUMED.equals(action))
			{
				String threadId = DEFAULT_THREAD_ID;
				if (protocolVersion >= 2)
				{
					threadId = args[1];
					args = Util.removeArrayElement(args, 1);
				}
				JSDebugThread thread = threads.get(threadId);
				if (thread != null)
				{
					thread.handleMessage(args);
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.js.debug.core.internal.model.DebugConnection.IHandler#handleShutdown()
		 */
		public void handleShutdown()
		{
			try
			{
				shutdown();
			}
			catch (DebugException e)
			{
				JSDebugPlugin.log(e);
			}
		}
	}
}
