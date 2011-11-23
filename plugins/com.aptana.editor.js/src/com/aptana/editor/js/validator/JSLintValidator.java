/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.validator;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptOrFnNode;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.optimizer.Codegen;

import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.core.build.Problem;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StreamUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.editor.js.IJSConstants;
import com.aptana.editor.js.JSPlugin;
import com.aptana.index.core.build.BuildContext;
import com.aptana.parsing.ast.IParseError;
import com.aptana.parsing.ast.IParseError.Severity;

public class JSLintValidator extends AbstractBuildParticipant
{

	private static final String JSLINT_FILENAME = "fulljslint.js"; //$NON-NLS-1$
	private static Script JS_LINT_SCRIPT;

	public void buildFile(BuildContext context, IProgressMonitor monitor)
	{
		boolean enableParseErrors = enableJSParseErrors();
		boolean enableJSLint = enableJSLint();

		List<IProblem> problems = new ArrayList<IProblem>();
		if (enableParseErrors || enableJSLint)
		{
			try
			{

				String source = context.getContents();
				URI uri = context.getURI();
				String sourcePath = uri.toString();

				if (enableParseErrors)
				{
					context.getAST(); // Ensure a parse happened

					// Add parse errors... FIXME Move this out of here!
					for (IParseError parseError : context.getParseErrors())
					{
						int severity = (parseError.getSeverity() == Severity.ERROR) ? IMarker.SEVERITY_ERROR
								: IMarker.SEVERITY_WARNING;
						int line = -1;
						if (source != null)
						{
							line = getLineNumber(parseError.getOffset(), source);
						}
						problems.add(new Problem(severity, parseError.getMessage(), parseError.getOffset(), parseError
								.getLength(), line, sourcePath));
					}
				}
				if (enableJSLint)
				{
					// FIXME Create sub-marker type of JS Problem marker just for JSLint!
					Context cContext = Context.enter();
					try
					{

						DefaultErrorReporter reporter = new DefaultErrorReporter();
						cContext.setErrorReporter(reporter);
						parseWithLint(cContext, source, sourcePath, problems);
					}
					finally
					{
						Context.exit();
					}
				}
			}
			catch (Exception e)
			{
				IdeLog.logError(JSPlugin.getDefault(), "Failed to parse for JSLint", e); //$NON-NLS-1$
			}
		}
		context.putProblems(IJSConstants.JS_PROBLEM_MARKER_TYPE, problems);
	}

	private boolean enableJSLint()
	{
		// FIXME We shouldn't be storing translatable names in prefs like this. Use ids, store under sub-nodes per
		// langauge or something?
		IEclipsePreferences prefs = EclipseUtil.instanceScope().getNode(CommonEditorPlugin.PLUGIN_ID);
		String result = prefs
				.get(MessageFormat.format(
						"{0}:{1}", IJSConstants.CONTENT_TYPE_JS, IPreferenceConstants.SELECTED_VALIDATORS), //$NON-NLS-1$
						"JSLint JavaScript Validator"); //$NON-NLS-1$
		return result.indexOf("JSLint JavaScript Validator") != -1; //$NON-NLS-1$
	}

	private boolean enableJSParseErrors()
	{
		IEclipsePreferences store = EclipseUtil.instanceScope().getNode(CommonEditorPlugin.PLUGIN_ID);
		return store.getBoolean(getEnableParseErrorPrefKey(IJSConstants.CONTENT_TYPE_JS), true);
	}

	private String getEnableParseErrorPrefKey(String language)
	{
		return MessageFormat.format("{0}:{1}", language, IPreferenceConstants.PARSE_ERROR_ENABLED); //$NON-NLS-1$
	}

	public void deleteFile(BuildContext context, IProgressMonitor monitor)
	{
		context.removeProblems(IJSConstants.JS_PROBLEM_MARKER_TYPE);
	}

	private void parseWithLint(Context context, String source, String path, List<IProblem> items)
	{
		Scriptable scope = context.initStandardObjects();
		Script script = getJSLintScript();
		if (script == null)
		{
			return;
		}
		script.exec(context, scope);

		IDocument doc = null;

		Object functionObj = scope.get("JSLINT", scope); //$NON-NLS-1$
		if (functionObj instanceof Function)
		{
			Function function = (Function) functionObj;

			Object[] args = { source, scope.get("aptanaOptions", scope) }; //$NON-NLS-1$
			// PC: we ignore the result, because i have found that with some versions, there might
			// be errors but this function returned true (false == errors)
			function.call(context, scope, scope, args);

			Object errorObject = function.get("errors", scope); //$NON-NLS-1$
			if (errorObject instanceof NativeArray)
			{
				NativeArray errorArray = (NativeArray) errorObject;
				Object[] ids = errorArray.getIds();
				if (ids.length == 0)
				{
					return;
				}

				boolean lastIsError = false;
				NativeObject last = (NativeObject) errorArray.get(Integer.parseInt(ids[ids.length - 1].toString()),
						scope);
				if (last == null)
				{
					lastIsError = true;
				}

				NativeObject object;
				int line;
				String reason;
				int character;
				for (int i = 0; i < ids.length; ++i)
				{

					object = (NativeObject) errorArray.get(Integer.parseInt(ids[i].toString()), scope);
					if (object != null)
					{
						line = (int) Double.parseDouble(object.get("line", scope).toString()); //$NON-NLS-1$
						reason = object.get("reason", scope).toString().trim(); //$NON-NLS-1$
						character = (int) Double.parseDouble(object.get("character", scope).toString()); //$NON-NLS-1$

						// Don't attempt to add errors or warnings if there are already errors on this line
						if (hasErrorOrWarningOnLine(items, line))
						{
							continue;
						}

						if (!isIgnored(reason, IJSConstants.CONTENT_TYPE_JS))
						{
							if (doc == null)
							{
								doc = new Document(source);
							}
							try
							{
								character += doc.getLineOffset(line - 1);
							}
							catch (BadLocationException e)
							{
								// ignore
							}

							if (i == ids.length - 2 && lastIsError)
							{
								items.add(createError(reason, line, character, 0, path));
							}
							else
							{
								items.add(createWarning(reason, line, character, 0, path));
							}
						}
					}
				}
			}
		}
	}

	private String getFilterExpressionsPrefKey(String language)
	{
		return language + ":" + IPreferenceConstants.FILTER_EXPRESSIONS; //$NON-NLS-1$
	}

	private boolean isIgnored(String message, String language)
	{
		String list = CommonEditorPlugin.getDefault().getPreferenceStore()
				.getString(getFilterExpressionsPrefKey(language));
		if (!StringUtil.isEmpty(list))
		{
			String[] expressions = list.split("####"); //$NON-NLS-1$
			for (String expression : expressions)
			{
				if (message.matches(expression))
				{
					return true;
				}
			}
		}
		return false;
	}

	private static synchronized Script getJSLintScript()
	{
		if (JS_LINT_SCRIPT == null)
		{
			URL url = Platform.getBundle("org.mozilla.rhino").getEntry("/" + JSLINT_FILENAME); //$NON-NLS-1$ //$NON-NLS-2$
			if (url != null)
			{
				String source = null;
				try
				{
					source = StreamUtil.readContent(url.openStream());
				}
				catch (IOException e)
				{
					IdeLog.logError(JSPlugin.getDefault(), Messages.JSLintValidator_ERR_FailToGetJSLint, e);
				}
				if (source != null)
				{
					JS_LINT_SCRIPT = getJSLintScript(source);
				}
			}
		}
		return JS_LINT_SCRIPT;
	}

	private static Script getJSLintScript(String source)
	{
		Context context = Context.enter();
		try
		{
			CompilerEnvirons compilerEnv = new CompilerEnvirons();
			compilerEnv.initFromContext(context);
			Parser p = new Parser(compilerEnv, context.getErrorReporter());

			ScriptOrFnNode tree = p.parse(source, JSLINT_FILENAME, 1);
			String encodedSource = p.getEncodedSource();

			Codegen compiler = new Codegen();
			Object bytecode = compiler.compile(compilerEnv, tree, encodedSource, false);

			return compiler.createScriptObject(bytecode, null);
		}
		catch (Exception e)
		{
			IdeLog.logError(JSPlugin.getDefault(), Messages.JSLintValidator_ERR_FailToGetJSLint, e);
		}
		finally
		{
			Context.exit();
		}

		return null;
	}
}
