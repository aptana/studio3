/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.validator;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
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
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.StreamUtil;
import com.aptana.editor.js.IJSConstants;
import com.aptana.editor.js.JSPlugin;
import com.aptana.index.core.build.BuildContext;

/**
 * Runs the code against JSLint inside Rhino, then parses out the reported errors/warnings.
 * 
 * @author cwilliams
 */
public class JSLintValidator extends AbstractBuildParticipant
{
	/**
	 * The unique ID of this validator/build participant.
	 */
	public static final String ID = "com.aptana.editor.js.validator.JSLintValidator"; //$NON-NLS-1$

	/**
	 * The bundle id containing JSLint script.
	 */
	private static final String ORG_MOZILLA_RHINO = "org.mozilla.rhino"; //$NON-NLS-1$

	private static final String JSLINT_FILENAME = "fulljslint.js"; //$NON-NLS-1$
	private static Script JS_LINT_SCRIPT;

	private static Map<String, Object> DEFAULT_OPTION = new HashMap<String, Object>();
	static
	{
		// Set default aptana options
		DEFAULT_OPTION.put("laxLineEnd", true); //$NON-NLS-1$
		DEFAULT_OPTION.put("undef", true); //$NON-NLS-1$
		DEFAULT_OPTION.put("browser", true); //$NON-NLS-1$
		DEFAULT_OPTION.put("jscript", true); //$NON-NLS-1$
		DEFAULT_OPTION.put("debug", true); //$NON-NLS-1$
		DEFAULT_OPTION.put("maxerr", 100000); //$NON-NLS-1$
		DEFAULT_OPTION.put("white", true); //$NON-NLS-1$
		DEFAULT_OPTION.put(
				"predef", new NativeArray(new String[] { "Ti", "Titanium", "alert", "require", "exports", "native", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
						"implements" })); //$NON-NLS-1$
	}

	private Map<String, Object> options;

	public JSLintValidator()
	{
		super();
	}

	public void buildFile(BuildContext context, IProgressMonitor monitor)
	{
		if (context == null)
		{
			return;
		}

		intializeOptions();
		List<IProblem> problems = Collections.emptyList();
		String sourcePath = context.getURI().toString();
		try
		{

			Context cContext = Context.enter();
			try
			{
				DefaultErrorReporter reporter = new DefaultErrorReporter();
				cContext.setErrorReporter(reporter);
				problems = parseWithLint(cContext, context.getContents(), sourcePath);
			}
			finally
			{
				Context.exit();
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(JSPlugin.getDefault(),
					MessageFormat.format("Failed to parse {0} with JSLint", sourcePath), e); //$NON-NLS-1$
		}

		this.options = null;
		context.putProblems(IJSConstants.JSLINT_PROBLEM_MARKER_TYPE, problems);
	}

	public void deleteFile(BuildContext context, IProgressMonitor monitor)
	{
		if (context == null)
		{
			return;
		}

		context.removeProblems(IJSConstants.JSLINT_PROBLEM_MARKER_TYPE);
	}

	private List<IProblem> parseWithLint(Context context, String source, String path)
	{
		Scriptable scope = context.initStandardObjects();
		Script script = getJSLintScript();
		if (script == null)
		{
			return Collections.emptyList();
		}

		script.exec(context, scope);

		Object functionObj = scope.get("JSLINT", scope); //$NON-NLS-1$
		if (!(functionObj instanceof Function))
		{
			return Collections.emptyList();
		}

		Function function = (Function) functionObj;
		NativeObject optionsObj = new NativeObject();
		// Set our overriding options.
		if (this.options != null)
		{
			for (Map.Entry<String, Object> entry : this.options.entrySet())
			{
				scope.put(entry.getKey(), optionsObj, entry.getValue());
			}
		}

		Object[] args = { source, optionsObj };
		// PC: we ignore the result, because i have found that with some versions, there might
		// be errors but this function returned true (false == errors)
		function.call(context, scope, scope, args);

		Object errorObject = function.get("errors", scope); //$NON-NLS-1$
		if (!(errorObject instanceof NativeArray))
		{
			return Collections.emptyList();
		}

		NativeArray errorArray = (NativeArray) errorObject;
		Object[] ids = errorArray.getIds();
		if (ArrayUtil.isEmpty(ids))
		{
			return Collections.emptyList();
		}

		boolean lastIsError = false;
		NativeObject last = (NativeObject) errorArray.get(Integer.parseInt(ids[ids.length - 1].toString()), scope);
		if (last == null)
		{
			lastIsError = true;
		}

		List<String> filters = getFilters();
		IDocument doc = null; // Lazily init document object to query about lines/offsets
		List<IProblem> items = new ArrayList<IProblem>(ids.length);
		for (int i = 0; i < ids.length; ++i)
		{
			// Grab the warning/error
			NativeObject object = (NativeObject) errorArray.get(Integer.parseInt(ids[i].toString()), scope);
			if (object == null)
			{
				continue;
			}

			// Grab the line of the error. Skip if we already recorded an error on this line (why?)
			int line = (int) Double.parseDouble(object.get("line", scope).toString()); //$NON-NLS-1$

			// Grab the details of the error. If user has set up filters to ignore it, move on
			String reason = object.get("reason", scope).toString().trim(); //$NON-NLS-1$
			if (isIgnored(reason, filters))
			{
				continue;
			}

			// lazy init of document to query for offsets/line info
			if (doc == null)
			{
				doc = new Document(source);
			}

			// Translate the column reported into the absolute offset from start of doc
			int character = (int) Double.parseDouble(object.get("character", scope).toString()); //$NON-NLS-1$
			try
			{
				// JSLint reports the offset as column on the given line, and counts tab characters as 4 columns
				// We account for that by adding the offset of the line start, and reducing the column count on
				// tabs
				IRegion lineInfo = doc.getLineInformation(line - 1);
				int realOffset = lineInfo.getOffset();
				String rawLine = doc.get(realOffset, lineInfo.getLength());
				int actual = character - 1;
				for (int x = 0; x < actual; x++)
				{
					char c = rawLine.charAt(x);
					if (c == '\t')
					{
						actual -= 3;
					}
					realOffset++;
				}
				character = realOffset;
			}
			catch (BadLocationException e)
			{
				// ignore
			}

			// Now record the error
			if (i == ids.length - 2 && lastIsError)
			{
				// If this starts with "Stopping", convert the last warning to an error and skip this.
				if (reason.startsWith("Stopping")) //$NON-NLS-1$
				{
					IProblem lastWarning = items.remove(items.size() - 1);
					items.add(createError(lastWarning.getMessage(), lastWarning.getLineNumber(),
							lastWarning.getOffset(), 1, path));
				}
				else
				{
					items.add(createError(reason, line, character, 1, path));
				}
			}
			else
			{
				items.add(createWarning(reason, line, character, 1, path));
			}
		}
		return items;
	}

	/**
	 * Lazily grab the JSLint script.
	 * 
	 * @return
	 */
	private static synchronized Script getJSLintScript()
	{
		if (JS_LINT_SCRIPT == null)
		{
			URL url = Platform.getBundle(ORG_MOZILLA_RHINO).getEntry("/" + JSLINT_FILENAME); //$NON-NLS-1$
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

	/**
	 * Compile JSLint file into {@link Script} object.
	 * 
	 * @param source
	 * @return
	 */
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

	public void setOption(String propertyName, Object value)
	{
		intializeOptions();
		this.options.put(propertyName, value);
	}

	private void intializeOptions()
	{
		if (this.options == null)
		{
			this.options = new HashMap<String, Object>();
			this.options.putAll(DEFAULT_OPTION);
		}
	}

	class DefaultErrorReporter implements ErrorReporter
	{

		private List<IProblem> items = new ArrayList<IProblem>();

		public DefaultErrorReporter()
		{
			items = new ArrayList<IProblem>();
		}

		public void error(String message, String sourceURI, int line, String lineText, int lineOffset)
		{
			items.add(createError(message, line, lineOffset, 1, sourceURI));
		}

		public void warning(String message, String sourceURI, int line, String lineText, int lineOffset)
		{
			items.add(createWarning(message, line, lineOffset, 1, sourceURI));
		}

		public EvaluatorException runtimeError(String message, String sourceURI, int line, String lineText,
				int lineOffset)
		{
			return new EvaluatorException(message, sourceURI, line, lineText, lineOffset);
		}

		public List<IProblem> getItems()
		{
			return Collections.unmodifiableList(items);
		}
	}
}
