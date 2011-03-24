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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
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

import com.aptana.core.util.StreamUtil;
import com.aptana.editor.common.validator.IValidationItem;
import com.aptana.editor.common.validator.IValidationManager;
import com.aptana.editor.common.validator.IValidator;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.parsing.IJSParserConstants;

public class JSLintValidator implements IValidator
{

	private static final String JSLINT_FILENAME = "fulljslint.js"; //$NON-NLS-1$
	private static Script jsLintScript;

	public JSLintValidator()
	{
	}

	public List<IValidationItem> validate(String source, URI path, IValidationManager manager)
	{
		List<IValidationItem> items = new ArrayList<IValidationItem>();
		Context context = Context.enter();
		DefaultErrorReporter reporter = new DefaultErrorReporter();
		try
		{
			context.setErrorReporter(reporter);
			parseWithLint(context, source, path, manager, items);
		}
		finally
		{
			Context.exit();
		}
		return items;
	}

	private void parseWithLint(Context context, String source, URI path, IValidationManager manager,
			List<IValidationItem> items)
	{
		Scriptable scope = context.initStandardObjects();
		getJSLintScript().exec(context, scope);

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
				for (int i = 0; i < ids.length; ++i)
				{
					object = (NativeObject) errorArray.get(Integer.parseInt(ids[i].toString()), scope);
					if (object != null)
					{
						int line = (int) Double.parseDouble(object.get("line", scope).toString()); //$NON-NLS-1$
						String reason = object.get("reason", scope).toString().trim(); //$NON-NLS-1$
						int character = (int) Double.parseDouble(object.get("character", scope).toString()); //$NON-NLS-1$

						if (!manager.isIgnored(reason, IJSParserConstants.LANGUAGE))
						{
							if (i == ids.length - 2 && lastIsError)
							{
								items.add(manager.addError(reason, line, character, 0, path));
							}
							else
							{
								items.add(manager.addWarning(reason, line, character, 0, path));
							}
						}
					}
				}
			}
		}
	}

	private static synchronized Script getJSLintScript()
	{
		if (jsLintScript == null)
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
				}
				if (source != null)
				{
					jsLintScript = getJSLintScript(source);
				}
			}
		}
		return jsLintScript;
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
			JSPlugin.logError(Messages.JSLintValidator_ERR_FailToGetJSLint, e);
		}
		finally
		{
			Context.exit();
		}

		return null;
	}
}
