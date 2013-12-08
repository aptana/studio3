/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.js.debug.core.v8;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.chromium.sdk.Breakpoint;
import org.chromium.sdk.Breakpoint.Target;
import org.chromium.sdk.BrowserFactory;
import org.chromium.sdk.CallFrame;
import org.chromium.sdk.CallbackSemaphore;
import org.chromium.sdk.DebugContext;
import org.chromium.sdk.DebugContext.ContinueCallback;
import org.chromium.sdk.DebugContext.State;
import org.chromium.sdk.DebugContext.StepAction;
import org.chromium.sdk.DebugEventListener;
import org.chromium.sdk.ExceptionData;
import org.chromium.sdk.IgnoreCountBreakpointExtension;
import org.chromium.sdk.JavascriptVm.BreakpointCallback;
import org.chromium.sdk.JavascriptVm.ExceptionCatchMode;
import org.chromium.sdk.JavascriptVm.ScriptsCallback;
import org.chromium.sdk.JavascriptVm.SuspendCallback;
import org.chromium.sdk.JsEvaluateContext.EvaluateCallback;
import org.chromium.sdk.JsObject;
import org.chromium.sdk.JsScope;
import org.chromium.sdk.JsScope.Type;
import org.chromium.sdk.JsValue;
import org.chromium.sdk.JsVariable;
import org.chromium.sdk.JsVariable.SetValueCallback;
import org.chromium.sdk.Script;
import org.chromium.sdk.StandaloneVm;
import org.chromium.sdk.TextStreamPosition;
import org.chromium.sdk.util.GenericCallback;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;

import com.aptana.core.util.StringUtil;
import com.aptana.js.debug.core.internal.Util;
import com.aptana.js.debug.core.internal.model.AbstractDebugHost;

/**
 * @author Max Stepanov
 */
public class V8DebugHost extends AbstractDebugHost {
	
	private enum EventType {
		SUSPENDED, TERMINATE
	}

	private static final JsScope.Type[] SCOPE_ORDER = new JsScope.Type[] {
		JsScope.Type.CATCH,
		JsScope.Type.CLOSURE,
		JsScope.Type.WITH,
		JsScope.Type.LOCAL,
		JsScope.Type.GLOBAL
	};

	private static final int V8_CONNECT_TIMEOUT = 300000;
	private static final Pattern SCOPE_CHAIN_PATTERN = Pattern.compile("^<[A-Z]+>\\.(.*)$"); //$NON-NLS-1$
	private static final Pattern DETAIL_EXPRESSION_PATTERN = Pattern.compile("\\bthis\\b"); //$NON-NLS-1$
	private static final String THIS_SUBSTITUTE = "__this__"; //$NON-NLS-1$
	private static final String ANONYMOUS = "(anonymous function)"; //$NON-NLS-1$

	private boolean flatScopesMode = true;
	private final SocketAddress v8SocketAddress;

	private StandaloneVm vm;
	private DebugEventListener debugEventListener;
	private DebugContext currentContext;
	private List<? extends CallFrame> currentFrames;
	private StepAction lastStopAction;
	private BlockingQueue<EventType> eventQueue = new LinkedBlockingQueue<EventType>();
	private final Callback callback = new Callback();
	
	private List<Pattern> scriptFilters = new ArrayList<Pattern>();
	private List<Pattern> variableFilters = new ArrayList<Pattern>();
	private Map<String, String> detailFormatters = new HashMap<String, String>();
	private Map<String, Script> loadedScripts = new HashMap<String, Script>();
	private Map<String, Integer> scriptFunctionIds = new HashMap<String, Integer>();
	private int nextScriptFunctionId = 1;
	private boolean initialized = false;
	private boolean terminating = false;
	private Map<Integer, JsVariable> evalResults = new HashMap<Integer,JsVariable>();
	private int evalResultsLastId = 0;
	private Map<String, Map<Integer, Breakpoint>> breakpoints = new HashMap<String, Map<Integer,Breakpoint>>();
	private Map<Breakpoint, BreakpointProperties> breakpointProps = new HashMap<Breakpoint, AbstractDebugHost.BreakpointProperties>();
	private Map<String, Boolean> exceptions = new HashMap<String, Boolean>();

	public static V8DebugHost createDebugHost(SocketAddress sockAddress) throws CoreException {
		V8DebugHost debugHost = new V8DebugHost(sockAddress);
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
						continueVm(lastStopAction, 1);
						return;
					}
				}
				currentContext = context;
				eventQueue.offer(EventType.SUSPENDED);
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
				currentFrames = null;
				currentContext = null;
			}

			public VmStatusListener getVmStatusListener() {
				return null;
			}

			public void disconnected() {
			}
		};
	}

	public void setScriptFilters(Collection<Pattern> filters) {
		scriptFilters.addAll(filters);
	}

	public void setVariableFilters(Collection<Pattern> filters) {
		variableFilters.addAll(filters);
	}

	/* (non-Javadoc)
	 * @see com.aptana.js.debug.core.internal.model.AbstractDebugHost#getSyncObject()
	 */
	@Override
	protected Object getSyncObject() {
		return this;
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
			handleSuspend();
			return;
		}
		CallFrame frame = currentFrames.get(0);
		Script script = frame.getScript();
		// Filter internal API frames
		if (currentContext.getState() == State.NORMAL && isScriptFiltered(script)) {
			continueVm(SUSPEND.equals(suspendReason) ? StepAction.IN : (currentFrames.size() > 1 ? StepAction.OUT : StepAction.CONTINUE), 1);
			return;
		}
		List<CallFrame> filteredFrames = new ArrayList<CallFrame>();
		for (CallFrame f : currentFrames) {
			if (!isScriptFiltered(f.getScript())) {
				filteredFrames.add(f);
			}
		}
		currentFrames = filteredFrames;

		String fileName = makeAbsoluteURI(script.getName());
		TextStreamPosition position = frame.getStatementStartPosition();
		if (position == null) {
			logError("startDebugging(position=null)"); //$NON-NLS-1$
			continueVm(StepAction.CONTINUE, 0);
			return;
		}
		if (suspendReason == null) {
			suspendReason = UNDEFINED;
		}
		sendData(new String[] { SUSPENDED, suspendReason, Util.encodeData(fileName),
				Integer.toString(position.getLine()+1) }); // Line numbers are 0-based in V8, 1-based in Aptana Debugger Protocol/Eclipse.
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
		evalResults.clear();
		if (vm.isAttached()) {
			if (STEP_INTO.equals(resumeReason)) {
				continueVm(StepAction.IN, 1);
			} else if (STEP_OVER.equals(resumeReason)) {
				continueVm(StepAction.OVER, 1);
			} else if (STEP_RETURN.equals(resumeReason)) {
				int count = targetFrameCount;
				targetFrameCount = 1;
				continueVm(StepAction.OUT, count);
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

	private void processEvents() throws IOException, InterruptedException {
		EventType event;
		while ((event = eventQueue.poll(500, TimeUnit.MILLISECONDS)) != null) {
			switch (event) {
				case SUSPENDED:
					if (enabled) {
						handleSuspended();
					} else {
						eventQueue.offer(event);
						return;
					}
					break;
				case TERMINATE:
					handleTerminate();
					if (vm == null || !vm.isAttached()) {
						return;
					}
				default:
					break;
			}
		}
	}

	private void handleSuspended() throws IOException {
		checkInitialized();
		if (terminating) {
			handleTerminate();
			return;
		}
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
	
	protected void handleTerminate() {
		if (vm != null) {
			vm.detach();
		}
		closeLogger();
	}
	
	protected void evaluateInGlobalContext(String expression) {
		if (currentContext != null) {
			currentContext.getGlobalEvaluateContext().evaluateSync(expression, null, new Callback());
		}
	}

	private boolean suspendOnException(ExceptionData exceptionData) {
		JsValue exception = exceptionData.getExceptionValue();
		if (exception == null || !JsValue.Type.isObjectType(exception.getType())) {
			return false;
		}
		String exceptionClass = exception.asObject().getClassName();
		if (getBooleanOption(SUSPEND_ON_ERRORS) && exceptionClass.endsWith("Error")) { //$NON-NLS-1$
			return true;
		} else if (getBooleanOption(SUSPEND_ON_EXCEPTIONS) && exceptionClass.endsWith("Exception")) { //$NON-NLS-1$
			return true;
		} else if (exceptions.containsKey(exceptionClass)) {
			return true;
		} else {
			for (String t : getClassHierarchy(exception.asObject())) {
				if (exceptions.containsKey(t)) {
					return true;
				}
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.aptana.js.debug.core.internal.model.AbstractDebugHost#suspend(java.lang.String)
	 */
	@Override
	protected boolean suspend(String reason) {
		if (currentContext == null && vm != null) {
			suspendReason = reason;
			vm.suspend(null);
			return true;
		}
		return false;
	}
	
	protected void handleSuspend() {
		continueVm(StepAction.CONTINUE, 0);
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
				logError("listFrames(position=null)"); //$NON-NLS-1$
				continue;
			}
			String functionName = frame.getFunctionName();
			if (ANONYMOUS.equals(functionName) && i == currentFrames.size() - 1) {
				functionName = StringUtil.EMPTY;
			}
			framesData[i] = StringUtil.join(SUBARGS_DELIMITER, new String[] {
					Integer.toString(i), Util.encodeData(functionName), Util.encodeData(listArguments(frame)),
					Util.encodeData(fileName), Integer.toString(position.getLine()+1), // Line numbers are 0-based in V8, 1-based in Aptana Debugger Protocol/Eclipse.
					Boolean.toString(script.getType() == Script.Type.NATIVE), Long.toString(position.getOffset()), Integer.toString(getScriptFunctionId(script, functionName))
			});
		}
		return StringUtil.join(ARGS_DELIMITER, framesData);
	}

	private String listArguments(CallFrame frame) {
		String[] argsData = new String[0];
		return StringUtil.join(", ", argsData); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see com.aptana.js.debug.core.internal.model.AbstractDebugHost#listVariables(java.lang.String)
	 */
	@Override
	protected String listVariables(String variableName) {
		if (currentFrames == null) {
			return null;
		}
		CallFrame frame = null;
		JsVariable evalResult = null;
		Matcher matcher = VARIABLE_FRAME_PATTERN.matcher(variableName);
		if (matcher.matches()) {
			try {
				int frameId = Integer.parseInt(matcher.group(1));
				frame = currentFrames.get(frameId);
				variableName = matcher.group(2);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(ARG_FRAME_ID);
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new IllegalArgumentException(ARG_FRAME_ID);
			}
		} else {
			matcher = VARIABLE_EVAL_PATTERN.matcher(variableName);
			if (matcher.matches()) {
				try {
					int evalId = Integer.parseInt(matcher.group(1));
					evalResult = evalResults.get(Integer.valueOf(evalId));
					if (evalResult == null) {
						throw new IllegalArgumentException(ARG_EVAL_ID);
					}
					variableName = matcher.group(2);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(ARG_EVAL_ID);
				}		
			} else {
				return null;
			}
		}
		List<String> data = new ArrayList<String>();
		if (variableName.length() == 0) {
			if (frame != null) {
				generateVariablesData(data, Arrays.asList(frame.getReceiverVariable()));
				if (flatScopesMode) {
					flattenScopes(data, frame);
				} else {
					for (JsScope scope : frame.getVariableScopes()) {
						if (scope.getType() == Type.LOCAL) {
							continue;
						}
						data.add(generateVariableData(
								new VariableProperties(MessageFormat.format("<{0}>", scope.getType().name()), String.valueOf('o'), JsValue.Type.TYPE_OBJECT, StringUtil.EMPTY, StringUtil.EMPTY, null), //$NON-NLS-1$
								StringUtil.EMPTY));
					}
					generateVariablesData(data, getScopeVariables(frame, JsScope.Type.LOCAL), String.valueOf('l'), null);
				}
			} else if (evalResult != null) {
				generateValueData(data, evalResult);
			}
		} else {
			if (frame != null) {
				generateValueData(data, findVariable(frame, variableName));
			} else if (evalResult != null) {
				generateValueData(data, findVariable(evalResult, variableName));
			}
		}
		return StringUtil.join(ARGS_DELIMITER, data.toArray(new String[data.size()]));
	}

	private void flattenScopes(List<String> data, CallFrame frame) {
		Set<String> visitedNames = new HashSet<String>();
		for (JsScope.Type type : SCOPE_ORDER) {
			generateVariablesData(data, getScopeVariables(frame, type), type == Type.LOCAL ? String.valueOf('l') : null, visitedNames);
		}
	}

	private JsVariable findVariableInFlattenScope(CallFrame frame, String name) {
		for (JsScope.Type type : SCOPE_ORDER) {
			for (JsVariable v : getScopeVariables(frame, type)) {
				if (name.equals(v.getName())) {
					return v;
				}
			}
		}
		return null;
	}

	private static JsScope getScope(CallFrame frame, JsScope.Type type) {
		for (JsScope scope : frame.getVariableScopes()) {
			if (scope.getType() == type) {
				return scope;
			}
		}
		return null;
	}

	private static Collection<? extends JsVariable> getScopeVariables(CallFrame frame, JsScope.Type type) {
		JsScope scope = getScope(frame, type);
		if (scope != null) {
			return scope.getVariables();
		}
		return Collections.emptyList();
	}

	private boolean isScriptFiltered(Script script) {
		if (StringUtil.isEmpty(script.getName()))
		{
			return false;
		}
		String fileName = script.getName();
		for (Pattern pattern : scriptFilters) {
			if (pattern.matcher(fileName).matches()) {
				return true;
			}
		}
		return false;
	}

	private boolean isVariableFiltered(JsVariable variable) {
		String name = variable.getFullyQualifiedName();
		for (Pattern pattern : variableFilters) {
			if (pattern.matcher(name).matches()) {
				return true;
			}
		}
		return false;
	}

	private Object findVariable(CallFrame frame, String variableName) {
		if (THIS.equals(variableName)) {
			return frame.getReceiverVariable();
		} else if (variableName.startsWith(THIS_DOT)) {
			return findVariable(frame.getReceiverVariable(), variableName.substring(THIS_DOT.length()));
		} else if (flatScopesMode) {
			String name = variableName.split(VARIABLE_PARTS_SPLIT)[0];
			JsVariable v = findVariableInFlattenScope(frame, name);
			if (v != null) {
				return findVariable(v, variableName.length() == name.length() ? StringUtil.EMPTY : variableName.substring(name.length()+1));
			}
		} else if (variableName.startsWith("<")) { //$NON-NLS-1$
			int index = variableName.indexOf('>');
			if (index > 0) {
				String scopeName = variableName.substring(1, index);
				variableName = variableName.substring(index+1);
				try {
					if (variableName.length() == 0) {
						return getScope(frame, JsScope.Type.valueOf(scopeName));
					}
					if (variableName.charAt(0) == '.') {
						variableName = variableName.substring(1);
					}
					String name = variableName.split(VARIABLE_PARTS_SPLIT)[0];
					for (JsVariable v : getScopeVariables(frame, JsScope.Type.valueOf(scopeName))) {
						if (name.equals(v.getName())) {
							return findVariable(v, variableName.length() == name.length() ? StringUtil.EMPTY : variableName.substring(name.length()+1));
						}
					}
				} catch (IllegalArgumentException e) {
				}
			}
		} else {
			String name = variableName.split(VARIABLE_PARTS_SPLIT)[0];
			for (JsVariable v : getScopeVariables(frame, JsScope.Type.LOCAL)) {
				if (name.equals(v.getName())) {
					return findVariable(v, variableName.length() == name.length() ? StringUtil.EMPTY : variableName.substring(name.length()+1));
				}
			}
		}
		return null;
	}
	
	private JsVariable findVariable(JsVariable variable, String variableName) {
		if (variableName.length()  > 0) {
			String[] names = variableName.split(VARIABLE_PARTS_SPLIT);
			for (int i = 0; variable != null && i < names.length; ++i) {
				JsObject valueObject = variable.getValue().asObject();
				if (__PROTO__.equals(names[i])) {
					variable = findNamedProperty(valueObject.getInternalProperties(), names[i]);
				} else {
					variable = valueObject.getProperty(names[i]);					
				}
			}
		}
		return variable;
	}
	
	private static JsVariable findNamedProperty(Collection<? extends JsVariable> properties, String name) {
		for (JsVariable v : properties) {
			if (name.equals(v.getName())) {
				return v;
			}
		}
		return null;
	}

	private static String generateVariableData(VariableProperties props, String extraFlags) {
		return StringUtil.join(SUBARGS_DELIMITER, new String[] {
				Util.encodeData(props.name),
				Util.encodeData(props.displayType),
				combineFlags(props.flags, extraFlags),
				Util.encodeData(props.displayValue)
		});
	}

	private void generateVariablesData(List<String> data, Collection<? extends JsVariable> variables) {
		generateVariablesData(data, variables, StringUtil.EMPTY, null);
	}

	private void generateVariablesData(List<String> data, Collection<? extends JsVariable> variables, String extraFlags, Set<String> ignoreSet) {
		for (JsVariable variable : variables) {
			if (isVariableFiltered(variable) || (ignoreSet != null && ignoreSet.contains(variable.getName()))) {
				continue;
			}
			VariableProperties props = getVariableProperties(variable, false);
			if (props != null) {
				data.add(generateVariableData(props, extraFlags));
				if (ignoreSet != null) {
					ignoreSet.add(props.name);
				}
			}
		}		
	}
	
	private void generateValueData(List<String> data, Object object) {
		if (object instanceof JsVariable) {
			JsVariable variable = (JsVariable) object;
			JsValue value;
			if ((value = variable.getValue()) != null && JsValue.Type.isObjectType(value.getType())) {
				generateVariablesData(data, value.asObject().getProperties());
				generateVariablesData(data, value.asObject().getInternalProperties());
			}
		} else if (object instanceof JsScope) {
			JsScope scope = (JsScope) object;
			generateVariablesData(data, scope.getVariables(), "", null); //$NON-NLS-1$
		}
	}

	private VariableProperties getVariableProperties(JsVariable variable, boolean computeDetails) {
		if (variable == null) {
			return null;
		}
		String name = variable.getName();
		return getValueProperties(variable, variable.getValue(), name, computeDetails);
	}
	
	private VariableProperties getValueProperties(JsVariable variable, JsValue value, String name, boolean computeDetails) {
		String flags = StringUtil.EMPTY;
		JsValue.Type valueType = value.getType();
		String displayType;
		String displayValue;
		String detailValue = null;

		// TODO V8 protocol returns fixed 'false'
		//if (variable != null && variable.isMutable()) {
			flags = addFlag(flags, 'w');
		//}
		
		switch (valueType) {
		case TYPE_NUMBER:
			displayType = NUMBER;
			displayValue = value.getValueString();
			break;
		case TYPE_BOOLEAN:
			displayType = BOOLEAN;
			displayValue = value.getValueString();
			break;
		case TYPE_UNDEFINED:
			displayType = UNDEFINED;
			displayValue = value.getValueString();
			break;
		case TYPE_STRING:
			displayValue = MessageFormat.format(QUOTES_0, value.getValueString());
			displayType = STRING;
			break;
		case TYPE_NULL:
			displayValue = NULL;
			displayType = NULL;
			break;
		case TYPE_FUNCTION:
			flags = removeFlag(flags, 'w');
			displayValue = value.asObject().asFunction().getValueString();
			displayType = FUNCTION;
			break;
		case TYPE_OBJECT:
			if (THIS.equals(name) || __PROTO__.equals(name)) {
				flags = removeFlag(flags, 'w');
			}
			flags = addFlag(flags, 'o');
			displayValue = MessageFormat.format(OBJECT_0, value.asObject().getClassName());
			displayType = value.asObject().getClassName();
			if (computeDetails) {
				detailValue = getObjectDetail(value.asObject());
			}
			break;
		case TYPE_ARRAY:
			flags = addFlag(flags, 'o');
			displayValue = MessageFormat.format(OBJECT_0, value.asObject().getClassName());
			displayType = value.asObject().getClassName();
			if (computeDetails) {
				detailValue = getObjectDetail(value.asObject());
			}
			break;
		default:
			displayType = value.asObject().getClassName();
			displayType = MessageFormat.format("Unknown <{0}>", displayType); //$NON-NLS-1$
			displayValue = value.getValueString();
			break;
		}
		if (computeDetails && detailValue == null) {
			detailValue = displayValue;
		}
		return new VariableProperties(name, flags, valueType, displayType, displayValue, detailValue);
	}

	private VariableProperties getObjectProperties(Object object, boolean computeDetails) {
		if (object instanceof JsVariable) {
			return getVariableProperties((JsVariable) object, computeDetails);
		} else if (object instanceof JsValue) {
			return getValueProperties(null, (JsValue) object, null, computeDetails);
		} else if (object instanceof String) {
			return new VariableProperties(null, StringUtil.EMPTY, JsValue.Type.TYPE_STRING, STRING, (String) object,
					computeDetails ? (String) object : null);
		} else if (object instanceof Boolean) {
			return new VariableProperties(null, StringUtil.EMPTY, JsValue.Type.TYPE_BOOLEAN, BOOLEAN, Boolean.toString(((Boolean) object).booleanValue()),
					computeDetails ? Boolean.toString(((Boolean) object).booleanValue()) : null);			
		} else if (object instanceof Number) {
			return new VariableProperties(null, StringUtil.EMPTY, JsValue.Type.TYPE_NUMBER, NUMBER, String.valueOf(object),
					computeDetails ? String.valueOf(object) : null);
		} else if (object == null) {
			return new VariableProperties(null, StringUtil.EMPTY, JsValue.Type.TYPE_NULL, NULL, String.valueOf(object),
					computeDetails ? String.valueOf(object) : null);			
		}
		return null;
	}

	private String getObjectDetail(JsObject object) {
		String type = object.getClassName();
		if (detailFormatters.containsKey(type)) {
			return getValueDetail(type, object);
		} else {
			for (String t : getClassHierarchy(object)) {
				if (detailFormatters.containsKey(t)) {
					detailFormatters.put(type, detailFormatters.get(t));
					return getValueDetail(t, object);
				}
			}
		}
		detailFormatters.put(type, null);
		return null;
	}
	
	private String getValueDetail(String type, JsObject object) {
		String expression = detailFormatters.get(type);
		if (expression != null) {
			final Object[] result = new Object[1];
			Map<String, String> context = new HashMap<String, String>();
			context.put(THIS_SUBSTITUTE, object.getRefId());
			currentContext.getGlobalEvaluateContext().evaluateSync(expression, context, new Callback() {
				@Override
				public void success(JsVariable variable) {
					result[0] = variable;
				}

				@Override
				public void failure(String errorMessage) {
					result[0] = errorMessage;
				}
			});
			if (result[0] instanceof JsVariable) {
				VariableProperties props = getObjectProperties(result[0], false);
				if (props != null) {
					return props.displayValue;
				}
				return null;
			} else if (result[0] instanceof String){
				return (String) result[0];
			}
		}
		return null;
	}
	
	private static List<String> getClassHierarchy(JsObject object) {
		List<String> list = new ArrayList<String>();
		JsVariable proto = object.getProperty(__PROTO__);
		while (proto != null) {
			JsValue value;
			if (proto != null && (value = proto.getValue()) != null && JsValue.Type.isObjectType(value.getType())) {
				list.add(value.asObject().getClassName());
				proto = proto.getValue().asObject().getProperty(__PROTO__);
			}
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see com.aptana.js.debug.core.internal.model.AbstractDebugHost#processDetailFormatters(java.lang.String[])
	 */
	@Override
	protected String processDetailFormatters(String[] list) {
		detailFormatters.clear();
		for (int i = 0; i < list.length; ++i) {
			String[] df = list[i].split(SUBARGS_SPLIT);
			String type = Util.decodeData(df[0]);
			String expression = Util.decodeData(df[1]);
			Matcher matcher = DETAIL_EXPRESSION_PATTERN.matcher(expression);
			expression = matcher.replaceAll(THIS_SUBSTITUTE);
			detailFormatters.put(type, expression);
		}
		return null;
	}


	/* (non-Javadoc)
	 * @see com.aptana.js.debug.core.internal.model.AbstractDebugHost#setExceptionBreakpoint(java.lang.String)
	 */
	@Override
	protected void setExceptionBreakpoint(String exceptionType) {
		if (exceptions.size() == 1) {
			setCatchExceptions();
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.js.debug.core.internal.model.AbstractDebugHost#removeExceptionBreakpoint(java.lang.String)
	 */
	@Override
	protected void removeExceptionBreakpoint(String exceptionType) {
		if (exceptions.isEmpty()) {
			setCatchExceptions();
		}
	}
	
	private void setCatchExceptions() {
		CallbackSemaphore semaphore = new CallbackSemaphore();
		ExceptionCatchMode mode = getBooleanOption(SUSPEND_ON_ERRORS) || getBooleanOption(SUSPEND_ON_EXCEPTIONS) || !exceptions.isEmpty() ? ExceptionCatchMode.ALL : ExceptionCatchMode.NONE;
		semaphore.acquireDefault(vm.setBreakOnException(mode, callback, semaphore));

	}

	/* (non-Javadoc)
	 * @see com.aptana.js.debug.core.internal.model.AbstractDebugHost#processOptionChange(java.lang.String)
	 */
	@Override
	protected void processOptionChange(String option) {
		if (SUSPEND_ON_ERRORS.equals(option) || SUSPEND_ON_EXCEPTIONS.equals(option)) {
			setCatchExceptions();
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.js.debug.core.internal.model.AbstractDebugHost#doEval(java.lang.String, java.lang.String)
	 */
	@Override
	protected String doEval(String variableName, String expression) {
		if (currentFrames == null) {
			return null;
		}
		CallFrame frame = null;
		Matcher matcher = VARIABLE_FRAME_PATTERN.matcher(variableName);
		if (matcher.matches()) {
			try {
				int frameId = Integer.parseInt(matcher.group(1));
				frame = currentFrames.get(frameId);
				variableName = matcher.group(2);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(ARG_FRAME_ID);
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new IllegalArgumentException(ARG_FRAME_ID);
			}
		} else {
			return null;
		}
		try {
			matcher = SCOPE_CHAIN_PATTERN.matcher(expression);
			if (matcher.matches()) {
				expression = matcher.group(1);
			}
			JsVariable value = eval(frame, expression);
			if (value == null) {
				throw new Exception("evaluation failed"); //$NON-NLS-1$
			}
			Integer evalId = Integer.valueOf(evalResultsLastId++);
			evalResults.put(evalId, value);
			
			VariableProperties props = getObjectProperties(value, false);
			if (props == null) {
				return null;
			}
			String valueData = StringUtil.join(SUBARGS_DELIMITER, new String[] {
					Util.encodeData(props.displayType),
					props.flags,
					Util.encodeData(props.displayValue)
			});				
			return StringUtil.join(ARGS_DELIMITER, new String[] { RESULT, Integer.toString(evalId.intValue()), valueData });
		} catch (Exception e) {
			return StringUtil.join(ARGS_DELIMITER, new String[] { EXCEPTION, Util.encodeData(e.getMessage())});
		}
	}

	private JsVariable eval(CallFrame frame, String expressionString) throws Exception {
		final Exception[] exception = new Exception[1];
		final JsVariable[] result = new JsVariable[1];
		try {
			frame.getEvaluateContext().evaluateSync(expressionString, new HashMap<String, String>(), new Callback() {
				@Override
				public void success(JsVariable variable) {
					result[0] = variable;
				}

				@Override
				public void failure(String errorMessage) {
					exception[0] = new Exception(errorMessage);
				}
				
			});
		} catch (Exception e) {
			logError(e);
			throw new IllegalStateException("Invalid expression"); //$NON-NLS-1$
		}
		if (exception[0] != null) {
			throw exception[0];
		}
		return result[0];
	}

	/* (non-Javadoc)
	 * @see com.aptana.js.debug.core.internal.model.AbstractDebugHost#doGetDetails(java.lang.String)
	 */
	@Override
	protected String doGetDetails(String variableName) {
		if (currentFrames == null) {
			return null;
		}
		CallFrame frame = null;
		JsVariable evalResult = null;
		Matcher matcher = VARIABLE_FRAME_PATTERN.matcher(variableName);
		if (matcher.matches()) {
			try {
				int frameId = Integer.parseInt(matcher.group(1));
				frame = currentFrames.get(frameId);
				variableName = matcher.group(2);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(ARG_FRAME_ID);
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new IllegalArgumentException(ARG_FRAME_ID);
			}
		} else {
			matcher = VARIABLE_EVAL_PATTERN.matcher(variableName);
			if (matcher.matches()) {
				try {
					int evalId = Integer.parseInt(matcher.group(1));
					evalResult = evalResults.get(Integer.valueOf(evalId));
					if (evalResult == null) {
						throw new IllegalArgumentException(ARG_EVAL_ID);
					}
					variableName = matcher.group(2);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(ARG_EVAL_ID);
				}		
			} else {
				return null;
			}
		}
		VariableProperties props = null;
		if (variableName.length() == 0) {
			if (evalResult != null) {
				props = getObjectProperties(evalResult, true);
			}
		} else {
			if (frame != null) {
				props = getObjectProperties(findVariable(frame, variableName), true);
			} else if (evalResult != null) {
				props = getObjectProperties(findVariable(evalResult, variableName), true);
			}
		}
		if (props != null) {
			return StringUtil.join(ARGS_DELIMITER, new String[] { RESULT, Util.encodeData(props.detailValue)});
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.aptana.js.debug.core.internal.model.AbstractDebugHost#doSetValue(java.lang.String, java.lang.String)
	 */
	@Override
	protected String doSetValue(String variableName, String valueRef) {
		if (currentFrames == null) {
			return null;
		}
		CallFrame frame = null;
		JsVariable evalResult = null;
		Matcher matcher = VARIABLE_FRAME_PATTERN.matcher(variableName);
		if (matcher.matches()) {
			try {
				int frameId = Integer.parseInt(matcher.group(1));
				frame = currentFrames.get(frameId);
				variableName = matcher.group(2);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(ARG_FRAME_ID);
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new IllegalArgumentException(ARG_FRAME_ID);
			}
		} else {
			matcher = VARIABLE_EVAL_PATTERN.matcher(variableName);
			if (matcher.matches()) {
				try {
					int evalId = Integer.parseInt(matcher.group(1));
					evalResult = evalResults.get(Integer.valueOf(evalId));
					if (evalResult == null) {
						throw new IllegalArgumentException(ARG_EVAL_ID);
					}
					variableName = matcher.group(2);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(ARG_EVAL_ID);
				}		
			} else {
				return null;
			}
		}
		JsVariable variable = null;
		if (variableName.length() > 0) {
			if (frame != null) {
				Object object = findVariable(frame, variableName);
				if (object instanceof JsVariable) {
					variable = (JsVariable) object;
				}
			} else if (evalResult != null) {
				variable = findVariable(evalResult, variableName);
			}
		}
		if (variable == null) {
			throw new IllegalArgumentException(ARG_VARIABLE_NAME);
		}
		JsVariable value = null;
		matcher = VARIABLE_EVAL_PATTERN.matcher(valueRef);
		if (matcher.matches()) {
			try {
				int evalId = Integer.parseInt(matcher.group(1));
				JsVariable evalValue = evalResults.get(Integer.valueOf(evalId));
				if (evalValue != null && evalValue.getValue() != null) {
					value = evalValue;
				}
			} catch (NumberFormatException ignore) {
			}	
		}
		if (value == null) {
			throw new IllegalArgumentException("valueRef"); //$NON-NLS-1$
		}
		String failure = setVariableValue(frame, variable, value);
		if (failure != null) {
			return StringUtil.join(ARGS_DELIMITER, new String[] { EXCEPTION, Util.encodeData(failure) });
		}
		/* re-fetch variable again to get updated value */
		currentContext.getDefaultRemoteValueMapping().clearCaches();
		variable = null;
		if (frame != null) {
			Object object = findVariable(frame, variableName);
			if (object instanceof JsVariable) {
				variable = (JsVariable) object;
			}
		} else if (evalResult != null) {
			variable = findVariable(evalResult, variableName);
		}
		if (variable == null) {
			return null;
		}
		VariableProperties props = getVariableProperties(variable, false);
		String valueData = StringUtil.join(SUBARGS_DELIMITER, new String[] {
				Util.encodeData(props.displayType),
				props.flags,
				Util.encodeData(props.displayValue)
		});
		return StringUtil.join(ARGS_DELIMITER, new String[] { RESULT, valueData });
	}
	
	private String setVariableValue(CallFrame frame, JsVariable variable, JsVariable value) {
		final String[] failure = new String[1];
		String stringValue;
		boolean objectType = false;
		JsValue v = value.getValue();
		if (JsValue.Type.isObjectType(v.getType())) {
			stringValue = v.asObject().getRefId();
			objectType = true;
		} else {
			stringValue = v.getValueString();
			if (v.getType() == JsValue.Type.TYPE_STRING) {
				stringValue = StringUtil.quote(stringValue);
			}
		}
		if (variable.isMutable()) {
			variable.setValue(stringValue, new Callback() {
				@Override
				public void failure(String errorMessage) {
					failure[0] = errorMessage;
				}
			});
		} else {
			String valueName = "value"+System.currentTimeMillis(); //$NON-NLS-1$
			frame.getEvaluateContext().evaluateSync(MessageFormat.format("{0}={1}", variable.getFullyQualifiedName(), objectType ? valueName : stringValue), objectType ? Collections.singletonMap(valueName, stringValue) : null, new Callback() { //$NON-NLS-1$
				@Override
				public void failure(String errorMessage) {
					failure[0] = errorMessage;
				}
			});
		}
		return failure[0];
	}

	private void checkInitialized() {
		if (initialized) {
			return;
		}
		initialized = true;
		processLoadedScripts();
		setCatchExceptions();
	}
		
	private void processLoadedScripts() {
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
		if (StringUtil.isEmpty(script.getName()))
		{
			return;
		}
		String fileName = makeAbsoluteURI(script.getName());
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
		loadedScripts.remove(fileName);
		// TODO: send remove script
	}	

	private String[] listScripts(String fileName, Script script) {
		String[] functions = new String[] { "anonymous" }; //TODO //$NON-NLS-1$
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
	

	/* (non-Javadoc)
	 * @see com.aptana.js.debug.core.internal.model.AbstractDebugHost#frameCount()
	 */
	@Override
	protected int frameCount() {
		if (currentContext != null) {
			return currentFrames.size();
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.aptana.js.debug.core.internal.model.AbstractDebugHost#setBreakpoint(java.lang.String, int, com.aptana.js.debug.core.internal.model.AbstractDebugHost.BreakpointProperties)
	 */
	@Override
	protected boolean setBreakpoint(String uri, int lineNo, BreakpointProperties props) {
		lineNo -= 1; // Line numbers are 0-based in V8, 1-based in Aptana Debugger Protocol/Eclipse.
		final Breakpoint[] result = new Breakpoint[1];
		CallbackSemaphore semaphore = new CallbackSemaphore();
		Target target = new Breakpoint.Target.ScriptName(makeDebuggerURI(uri));
		Callback callback = new Callback() {
			@Override
			public void success(Breakpoint breakpoint) {
				result[0] = breakpoint;
			}
		};
		IgnoreCountBreakpointExtension extension;
		if (props.hitCount > 1 && (extension = vm.getIgnoreCountBreakpointExtension()) != null) {
			semaphore.acquireDefault(extension.setBreakpoint(vm, target, lineNo, 0, !props.disabled, props.conditionOnTrue ? props.condition : null, props.hitCount - 1, callback, semaphore));
		} else {
			semaphore.acquireDefault(vm.setBreakpoint(target, lineNo, 0, !props.disabled, props.conditionOnTrue ? props.condition : null, callback, semaphore));
		}
		if (result[0] != null) {
			Map<Integer, Breakpoint> scriptBreakpoints = breakpoints.get(uri);
			if (scriptBreakpoints == null) {
				breakpoints.put(uri, scriptBreakpoints = new HashMap<Integer, Breakpoint>());
			}
			scriptBreakpoints.put(lineNo, result[0]);
			breakpointProps.put(result[0], props);
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.aptana.js.debug.core.internal.model.AbstractDebugHost#removeBreakpoint(java.lang.String, int)
	 */
	@Override
	protected boolean removeBreakpoint(String uri, int lineNo) {
		lineNo -= 1; // Line numbers are 0-based in V8, 1-based in Aptana Debugger Protocol/Eclipse.
		Map<Integer, Breakpoint> scriptBreakpoints = breakpoints.get(uri);
		if (scriptBreakpoints != null) {
			Breakpoint bp = scriptBreakpoints.remove(Integer.valueOf(lineNo));
			if (bp != null) {
				CallbackSemaphore semaphore = new CallbackSemaphore();
				semaphore.acquireDefault(bp.clear(callback, semaphore));
				breakpointProps.remove(bp);
				return true;
			}
		}
		return false;
	}

	private void continueVm(StepAction stepAction, int stepCount) {
		currentContext.continueVm(lastStopAction = stepAction, stepCount, callback);
	}

	/* (non-Javadoc)
	 * @see com.aptana.js.debug.core.internal.model.AbstractDebugHost#initSession()
	 */
	@Override
	protected void initSession(ILaunch launch) throws CoreException {
		initLogger(V8DebugPlugin.getDefault(), "v8hostdebugger"); //$NON-NLS-1$
		try {
			long endTime = System.currentTimeMillis() + V8_CONNECT_TIMEOUT;
			boolean attached = false;
			while (System.currentTimeMillis() <= endTime && !launch.isTerminated()) {
				try {
					vm = BrowserFactory.getInstance().createStandalone(v8SocketAddress, Platform.inDevelopmentMode() ? new ConnectionLoggerImpl(System.out) : null);
					vm.attach(debugEventListener);
					attached = true;
					break;
				} catch (ConnectException e) {
				} catch (SocketTimeoutException e) {
				} catch (IOException e) {
				}
				Thread.sleep(500);
			}
			if (launch.isTerminated()) {
				closeLogger();
				terminate();
				return;
			}
			if (!attached) {
				vm = BrowserFactory.getInstance().createStandalone(v8SocketAddress, Platform.inDevelopmentMode() ? new ConnectionLoggerImpl(System.out) : null);
				vm.attach(debugEventListener); // last try, throws exception
			}
			if (vm.isAttached()) {
				new Thread("Aptana: V8 Debug Host") { //$NON-NLS-1$
					public void run() {
						try {
							while (vm != null && vm.isAttached()) {
								processEvents();
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
							/* just quit the loop */
						} catch (Exception e) {
							e.printStackTrace();
							logError(e);
						} finally {
							terminate();
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
		if (terminating) {
			return;
		}
		terminating = true;
		eventQueue.offer(EventType.TERMINATE);
	}

	private class Callback implements ContinueCallback, ScriptsCallback, EvaluateCallback, SetValueCallback, BreakpointCallback, SuspendCallback, GenericCallback<ExceptionCatchMode> {

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

		/* (non-Javadoc)
		 * @see org.chromium.sdk.JsEvaluateContext.EvaluateCallback#success(org.chromium.sdk.JsVariable)
		 */
		public void success(JsVariable variable) {
		}

		/* (non-Javadoc)
		 * @see org.chromium.sdk.JavascriptVm.BreakpointCallback#success(org.chromium.sdk.Breakpoint)
		 */
		public void success(Breakpoint breakpoint) {
		}

		/* (non-Javadoc)
		 * @see org.chromium.sdk.util.GenericCallback#success(java.lang.Object)
		 */
		public void success(ExceptionCatchMode value) {
		}

		/* (non-Javadoc)
		 * @see org.chromium.sdk.util.GenericCallback#failure(java.lang.Exception)
		 */
		public void failure(Exception exception) {
			logError(exception.getMessage());
		}

		/*
		 * (non-Javadoc)
		 * @see org.chromium.sdk.DebugContext.ContinueCallback#failure(java.lang.String)
		 */
		public void failure(String errorMessage) {
			logError(errorMessage);
		}

	}

	private class VariableProperties {
		String name;
		String flags;
		String displayType;
		String displayValue;
		String detailValue;
		
		VariableProperties(String name, String flags, JsValue.Type valueType, String displayType, String displayValue, String detailValue) {
			this.name = name;
			this.flags = flags;
			this.displayType = displayType;
			this.displayValue = displayValue;
			this.detailValue = detailValue;
		}
	}

}
