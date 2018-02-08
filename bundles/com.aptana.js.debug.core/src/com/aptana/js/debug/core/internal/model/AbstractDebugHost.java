/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.js.debug.core.internal.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.osgi.framework.Constants;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.js.debug.core.internal.ProtocolLogger;
import com.aptana.js.debug.core.internal.Util;

/**
 * @author Max Stepanov
 */
public abstract class AbstractDebugHost {

	protected static final String SUSPEND_ON_ERRORS = "suspendOnErrors"; //$NON-NLS-1$
	protected static final String SUSPEND_ON_EXCEPTIONS = "suspendOnExceptions"; //$NON-NLS-1$
	protected static final String SUSPEND_ON_KEYWORDS = "suspendOnKeywords"; //$NON-NLS-1$
	protected static final String SUSPEND_ON_FIRST_LINE = "suspendOnFirstLine"; //$NON-NLS-1$
	protected static final String UPDATE = "update"; //$NON-NLS-1$
	protected static final String VERSION = "version"; //$NON-NLS-1$
	protected static final String OPENED = "opened"; //$NON-NLS-1$
	protected static final String DETAILS = "details"; //$NON-NLS-1$
	protected static final String SET_VALUE = "setValue"; //$NON-NLS-1$
	protected static final String EVAL = "eval"; //$NON-NLS-1$
	protected static final String RESULT = "result"; //$NON-NLS-1$
	protected static final String THIS = "this"; //$NON-NLS-1$
	protected static final String THIS_DOT = THIS + "."; //$NON-NLS-1$
	protected static final String __PROTO__ = "__proto__"; //$NON-NLS-1$
	protected static final String VARIABLES = "variables"; //$NON-NLS-1$
	protected static final String OPTION = "option"; //$NON-NLS-1$
	protected static final String ENABLE = "enable"; //$NON-NLS-1$
	protected static final String STEP_FILTERS = "stepFilters"; //$NON-NLS-1$
	protected static final String DETAIL_FORMATTERS = "detailFormatters"; //$NON-NLS-1$
	protected static final String DISABLE = "disable"; //$NON-NLS-1$
	protected static final String CHANGE = "change"; //$NON-NLS-1$
	protected static final String CHANGED = "changed"; //$NON-NLS-1$
	protected static final String REMOVE = "remove"; //$NON-NLS-1$
	protected static final String REMOVED = "removed"; //$NON-NLS-1$
	protected static final String CREATE = "create"; //$NON-NLS-1$
	protected static final String UNDEFINED = "undefined"; //$NON-NLS-1$
	protected static final String SUSPENDED = "suspended"; //$NON-NLS-1$
	protected static final String RESUMED = "resumed"; //$NON-NLS-1$
	protected static final String ARGS_DELIMITER = "*"; //$NON-NLS-1$
	protected static final String SUBARGS_DELIMITER = "|"; //$NON-NLS-1$
	protected static final String ARGS_SPLIT = "\\*"; //$NON-NLS-1$
	protected static final String SUBARGS_SPLIT = "\\|"; //$NON-NLS-1$
	protected static final String VARIABLE_PARTS_SPLIT = "\\."; //$NON-NLS-1$
	protected static final String _0_1_2 = "{0}*{1}*{2}"; //$NON-NLS-1$
	protected static final String _0_1 = "{0}*{1}"; //$NON-NLS-1$
	protected static final String OBJECT_0 = "[object {0}]"; //$NON-NLS-1$
	protected static final String QUOTES_0 = "\"{0}\""; //$NON-NLS-1$
	protected static final String CREATED = "created"; //$NON-NLS-1$
	protected static final String TERMINATE = "terminate"; //$NON-NLS-1$
	protected static final String SUSPEND = "suspend"; //$NON-NLS-1$
	protected static final String FIRST_LINE = "firstLine"; //$NON-NLS-1$
	protected static final String EXCEPTION = "exception"; //$NON-NLS-1$
	protected static final String ERR = "err"; //$NON-NLS-1$
	protected static final String TRACE = "trace"; //$NON-NLS-1$
	protected static final String NULL = "null"; //$NON-NLS-1$
	protected static final String STRING = "String"; //$NON-NLS-1$
	protected static final String BOOLEAN = "Boolean"; //$NON-NLS-1$
	protected static final String NUMBER = "Number"; //$NON-NLS-1$
	protected static final String FUNCTION = "Function"; //$NON-NLS-1$
	protected static final String BREAKPOINT = "breakpoint"; //$NON-NLS-1$
	protected static final String WATCHPOINT = "watchpoint"; //$NON-NLS-1$
	protected static final String SCRIPTS = "scripts"; //$NON-NLS-1$
	protected static final String LOG = "log"; //$NON-NLS-1$
	protected static final String OUT = "out"; //$NON-NLS-1$
	protected static final String SUCCESS = "success"; //$NON-NLS-1$
	protected static final String FAILURE = "failure"; //$NON-NLS-1$
	protected static final String GET_SOURCE = "getSource"; //$NON-NLS-1$
	protected static final String STEP_INTO = "stepInto"; //$NON-NLS-1$
	protected static final String STEP_OVER = "stepOver"; //$NON-NLS-1$
	protected static final String RESUME = "resume"; //$NON-NLS-1$
	protected static final String ABORT = "abort"; //$NON-NLS-1$
	protected static final String START = "start"; //$NON-NLS-1$
	protected static final String STEP_RETURN = "stepReturn"; //$NON-NLS-1$
	protected static final String STEP_TO_FRAME = "stepToFrame"; //$NON-NLS-1$
	protected static final String FRAMES = "frames"; //$NON-NLS-1$
	protected static final String DBGSOURCE_SCHEME = "dbgsource://"; //$NON-NLS-1$
	protected static final String _0 = "0"; //$NON-NLS-1$
	protected static final String _1 = "1"; //$NON-NLS-1$
	protected static final String _R = "r"; //$NON-NLS-1$
	protected static final String _W = "w"; //$NON-NLS-1$
	protected static final String _RW = "rw"; //$NON-NLS-1$
	protected static final String ARG_FRAME_ID = "frameId"; //$NON-NLS-1$
	protected static final String ARG_VARIABLE_NAME = "variableName"; //$NON-NLS-1$
	protected static final String ARG_EVAL_ID = "evalId"; //$NON-NLS-1$	

	protected static final Pattern VARIABLE_FRAME_PATTERN = Pattern.compile("^frame\\[(\\d+)\\]\\.?(.*)$"); //$NON-NLS-1$
	protected static final Pattern VARIABLE_EVAL_PATTERN = Pattern.compile("^eval\\[(\\d+)\\]\\.?(.*)$"); //$NON-NLS-1$

	protected static final String PROTOCOL_VERSION = "1"; //$NON-NLS-1$
	public static final int SOCKET_TIMEOUT = 1000;

	private Socket socket;
	private Reader reader;
	private Writer writer;

	private Plugin plugin;
	private File errorLogFile;
	private ProtocolLogger logger;

	protected boolean enabled;

	protected String suspendReason;
	protected String resumeReason;
	protected int targetFrameCount;

	protected String reqid;
	protected Map<String, Object> options = new HashMap<String, Object>();
	protected Set<String> exceptions = new HashSet<String>();

	/**
	 * 
	 */
	protected AbstractDebugHost() {
		// default options
		options.put(SUSPEND_ON_FIRST_LINE, false);
		options.put(SUSPEND_ON_KEYWORDS, true);
		options.put(SUSPEND_ON_EXCEPTIONS, false);
		options.put(SUSPEND_ON_ERRORS, false);
	}
	
	protected abstract Object getSyncObject();
	protected abstract boolean isConnected();
	protected abstract boolean isDebugging();
	protected abstract void initSession(ILaunch launch) throws CoreException;
	protected abstract void terminateSession();

	public final void start(final SocketAddress serverAddress, ILaunch launch) throws CoreException {
		new Thread("Aptana: Debugger Bridge") { //$NON-NLS-1$
			public void run() {
				Object syncObject = getSyncObject();
				synchronized (syncObject) {
					try {
						socket = new Socket();
						socket.connect(serverAddress, 5000);
						reader = new InputStreamReader(socket.getInputStream());
						writer = new OutputStreamWriter(socket.getOutputStream());
					} catch (IOException e) {
						logError(e);
					}
				}
				while (socket != null && !socket.isClosed()) {
					try {
						String message = readMessage();
						if (message == null) {
							break;
						}
						synchronized (syncObject) {
							handleMessage(message);							
						}
					} catch (IOException e) {
						break;
					} catch (Exception e) {
						logError(e);
					}
				}
				terminate();
			}
		}.start();
		initSession(launch);
	}

	/**
	 * readMessage
	 * 
	 * @return String
	 * @throws IOException
	 */
	private String readMessage() throws IOException {
		StringBuffer sb = new StringBuffer();
		int messageSize = 0;
		int i;
		char ch;
		while ((i = (reader != null ? reader.read() : -1)) != -1) {
			ch = (char) i;
			if (ch == '*' && sb.length() > 0) {
				try {
					messageSize = Integer.parseInt(sb.toString());
					break;
				} catch (NumberFormatException ignore) {
				}
				sb.setLength(0);
			} else if (ch >= '0' && ch <= '9') {
				sb.append(ch);
			} else if (sb.length() > 0) {
				sb.setLength(0);
			}
		}
		if (i == -1) {
			return null;
		}

		char[] buffer = new char[1024];
		sb.setLength(0); // clear the buffer
		while (messageSize > sb.length()) {
			int n = reader.read(buffer, 0, Math.min(messageSize - sb.length(), buffer.length));
			if (n == -1) {
				return null;
			}
			sb.append(buffer, 0, n);
		}
		return sb.toString();
	}

	private void handleMessage(String message) throws IOException {
		logger.log(true, message);
		if (message.endsWith(ARGS_DELIMITER)) {
			message += ARGS_DELIMITER + ' ';
		}
		String[] args = message.split(ARGS_SPLIT);
		reqid = args[0];
		if (args.length < 2) {
			sendResponse("!no command specified"); //$NON-NLS-1$
		}
		if (!isConnected()) {
			return;
		}
		String command = args[1];
		try {
			if (FRAMES.equals(command)) {
				sendResponse(listFrames());
			} else if (VARIABLES.equals(command)) {
				sendResponse(listVariables(Util.decodeData(args[2])));
			} else if (DETAILS.equals(command)) {
				sendResponse(doGetDetails(Util.decodeData(args[2])));
			} else if (EVAL.equals(command)) {
				sendResponse(doEval(Util.decodeData(args[2]), Util.decodeData(args[3])));
			} else if (STEP_INTO.equals(command)
					|| STEP_OVER.equals(command)
					|| STEP_RETURN.equals(command)
					|| STEP_TO_FRAME.equals(command)) {
				if (isDebugging()) {
					suspendReason = command;
					if (STEP_TO_FRAME.equals(command)) {
						int frameId;
						try {
							frameId = Integer.parseInt(args[2]);
						} catch (NumberFormatException ignore) {
							throw new IllegalArgumentException(ARG_FRAME_ID);
						}
						if (frameId == 0 || frameId >= frameCount()) {
							stopDebugging(STEP_OVER);
						} else {
							targetFrameCount = frameId;
							stopDebugging(STEP_RETURN);
						}
					} else {
						stopDebugging(command);
					}
				}
			} else if (ENABLE.equals(command)) {
				enable();
				sendResponse(null);
			} else if (DISABLE.equals(command)) {
				disable();
				sendResponse(null);
			} else if (OPTION.equals(command)) {
				String option = args[2];
				if (options.containsKey(option)) {
					if (options.get(option) instanceof Boolean) {
						options.put(option, Boolean.valueOf(args[3]).booleanValue());						
					} else {
						options.put(option, args[3]);
					}
					processOptionChange(option);
					sendResponse(null);
				} else {
					sendResponse("!unknown option <"+option+">"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			} else if (SUSPEND.equals(command)) {
				suspend(command);
			} else if (RESUME.equals(command)) {
				stopDebugging(RESUME);
			} else if (BREAKPOINT.equals(command)) {
				String action = args[2];
				int lineNo;
				try {
					lineNo = Integer.parseInt(args[4]);
				} catch (NumberFormatException ignore) {
					throw new IllegalArgumentException("lineNo"); //$NON-NLS-1$
				}
				BreakpointProperties props = null;
				if (CREATE.equals(action) || CHANGE.equals(action)) {
					int hitCount;
					try {
						hitCount = Integer.parseInt(args[6]);
					} catch (NumberFormatException ignore) {
						throw new IllegalArgumentException("hitCount"); //$NON-NLS-1$
					}
					props = new BreakpointProperties(_0.equals(args[5]), hitCount,
							Util.decodeData(args[7]), _1.equals(args[8]));
				}
				sendResponse(processBreakpoint(
						action,
						Util.decodeData(args[3]),
						lineNo,
						props));
			} else if (EXCEPTION.equals(command)) {
				sendResponse(processExceptionBreakpoint(args[2], Util.decodeData(args[3])));
			} else if (STEP_FILTERS.equals(command)) {
				sendResponse(null/* TODO */);
			} else if (DETAIL_FORMATTERS.equals(command)) {
				String[] df = new String[args.length-2];
				System.arraycopy(args, 2, df, 0, df.length);
				sendResponse(processDetailFormatters(df));
			} else if (SET_VALUE.equals(command)) {
				sendResponse(doSetValue(Util.decodeData(args[2]), Util.decodeData(args[3])));
			} else if (GET_SOURCE.equals(command)) {
				sendResponse(getSource(Util.decodeData(args[2])));
			} else if (TERMINATE.equals(command)) {
				terminate();
			} else if (VERSION.equals(command)) {
				sendResponse(MessageFormat.format(_0_1, PROTOCOL_VERSION, getVersion()));
			} else if (UPDATE.equals(command)) {
				sendResponse(null);
			} else {
				sendResponse("!unsupported command <" + command + ">"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			sendResponse("!not enough arguments for <" + command + ">"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (IllegalArgumentException e) {
			sendResponse("!invalid argument value for <" + e.getMessage() + ">"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (Exception e) {
			logError(e);
		}
	}

	protected final void sendResponse(String data) throws IOException {
		if (reqid == null) {
			throw new IllegalStateException("reqid is null"); //$NON-NLS-1$
		}
		sendData(reqid, data);
		reqid = null;
	}

	protected final void sendData(String[] data) throws IOException {
		sendData(null, StringUtil.join(ARGS_DELIMITER, data));
	}

	protected final void sendData(String reqid, String data) throws IOException {
		if (writer == null) {
			return;
		}
		String message = null;
		if (reqid != null && data != null) {
			message = MessageFormat.format(_0_1_2, Integer.toString(data.length() + reqid.length() + 1), reqid, data);
		} else if (reqid != null) {
			message = MessageFormat.format(_0_1, Integer.toString(reqid.length()), reqid);
		} else {
			message = MessageFormat.format(_0_1, Integer.toString(data.length()), data);
		}
		writer.write(message);
		try {
			writer.flush();
		} catch (SocketException ignore) {
		}
		logger.log(false, message);
	}

	private String processBreakpoint(String action, String uri, int lineNo, BreakpointProperties props) {
		if (CREATE.equals(action)) {
			if (setBreakpoint(uri, lineNo, props)) {
				return CREATED;
			}
		} else if (CHANGE.equals(action) && removeBreakpoint(uri, lineNo)) {
			if (setBreakpoint(uri, lineNo, props)) {
				return CHANGED;
			}
		} else if (REMOVE.equals(action)) {
			if (removeBreakpoint(uri, lineNo)) {
				return REMOVED;
			}
		}
		return null;
	}
	
	private String processExceptionBreakpoint(String action, String exceptionType) {
		if (CREATE.equals(action)) {
			if (!exceptions.contains(exceptionType)) {
				exceptions.add(exceptionType);
				setExceptionBreakpoint(exceptionType);
				return CREATED;
			}
		} else if (CHANGE.equals(action)) {
			exceptions.add(exceptionType);
			setExceptionBreakpoint(exceptionType);
			return CHANGED;
		} else if (REMOVE.equals(action)) {
			if (exceptions.remove(exceptionType)) {
				removeExceptionBreakpoint(exceptionType);
				return REMOVED;
			}
		}
		return null;
	}

	protected final void terminate() {
		checkCloseStreams();
		terminateSession();
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException ignore) {
			}
			socket = null;
			checkCloseStreams();
		}
		logger.close();
	}
	
	private void checkCloseStreams() {
		if (socket == null || socket.isClosed()) {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ignore) {
				} finally {
					reader = null;
				}
			}
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException ignore) {
				} finally {
					writer = null;
				}
			}
		}		
	}

	protected abstract void startDebugging() throws IOException;
	protected abstract void stopDebugging(String reason) throws IOException;
	protected abstract String listFrames();
	protected abstract int frameCount();
	protected abstract String listVariables(String variableName);
	protected abstract String doGetDetails(String variableName);
	protected abstract String doEval(String variableName, String expression);
	protected abstract String doSetValue(String variableName, String valueRef);
	protected abstract boolean suspend(String reason);
	protected abstract String processDetailFormatters(String[] list);
	protected abstract String getSource(String uri);
	protected abstract boolean setBreakpoint(String uri, int lineNo, BreakpointProperties props);
	protected abstract boolean removeBreakpoint(String uri, int lineNo);
	protected abstract void setExceptionBreakpoint(String exceptionType);
	protected abstract void removeExceptionBreakpoint(String exceptionType);
	protected abstract void processOptionChange(String option);

	protected void enable() throws IOException {
		enabled = true;
		sendData(new String[] { RESUMED, START });		
	}
	
	private void disable() throws IOException {
		enabled = false;
		stopDebugging(null);			
	}

	protected final boolean getBooleanOption(String option) {
		Object value = options.get(option);
		if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue();
		}
		return false;
	}

	protected String makeAbsoluteURI(String string) {
		URI uri = null;
		try {
			uri = new URI(string);
		} catch (URISyntaxException e) {
		}
		if (uri == null || uri.getScheme() == null) {
			return "app:///"+string; //$NON-NLS-1$
		}
		return string;
	}

	protected String makeDebuggerURI(String string) {
		URI uri = null;
		try {
			uri = new URI(string);
			if ("app".equals(uri.getScheme())) { //$NON-NLS-1$
				String path = uri.getPath();
				if (path.startsWith("/")) { //$NON-NLS-1$
					path = path.substring(1);
				}
				return path;
			}
		} catch (URISyntaxException e) {
		}
		return string;
	}

	protected static String addFlag(String flags, char flag) {
		if (flags.indexOf(flag) == -1) {
			flags += flag;
		}
		return flags;
	}

	protected static String combineFlags(String flags, String additions) {
		if (additions == null) {
			return flags;
		} else if (additions.length() == 1) {
			return addFlag(flags, additions.charAt(0));
		} else if (additions.length() > 0) {
			for (int i = 0; i < additions.length(); ++i) {
				char ch = additions.charAt(i);
				if (ch == '-' && (i + 1) < additions.length()) {
					++i;
					flags = removeFlag(flags, additions.charAt(i));
				} else {
					flags = addFlag(flags, additions.charAt(i));
				}
			}
		}
		return flags;
	}

	protected static String removeFlag(String flags, char flag) {
		if (flags.indexOf(flag) != -1) {
			flags = flags.replace(String.valueOf(flag), StringUtil.EMPTY);
		}
		return flags;
	}

	protected final void initLogger(Plugin plugin, String basename) throws DebugException {
		this.plugin = plugin;
		IPath base = plugin.getStateLocation().append("logs"); //$NON-NLS-1$
		base.toFile().mkdirs();
		errorLogFile = base.append(basename+".err").toFile(); //$NON-NLS-1$
		logger = new ProtocolLogger(basename, plugin.getBundle().getSymbolicName());
	}
	
	protected final void closeLogger() {
		logger.close();
	}

	protected final synchronized void logError(String message) {
		System.out.println(message);
		try {
			PrintWriter logWriter = new PrintWriter(new FileOutputStream(errorLogFile, true), true);
			logWriter.format("[%1$tc] %2$s\n", Calendar.getInstance(), message); //$NON-NLS-1$
			logWriter.close();
		} catch (FileNotFoundException e) {
			IdeLog.logError(plugin, e);
		}
	}

	protected final void logError(Exception e) {
		IdeLog.logError(plugin, e);
		StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));
		logError(writer.toString());
	}

	private String getVersion() {
		return (String) plugin.getBundle().getHeaders().get(Constants.BUNDLE_VERSION);
	}

	protected class BreakpointProperties {
		public boolean disabled;
		public int hitCount;
		public String condition;
		public boolean conditionOnTrue;
				
		BreakpointProperties(boolean disabled, int hitCount, String condition, boolean conditionOnTrue) {
			this.disabled = disabled;
			this.hitCount = hitCount;
			this.condition = condition;
			this.conditionOnTrue = conditionOnTrue;
		}
	}

}
