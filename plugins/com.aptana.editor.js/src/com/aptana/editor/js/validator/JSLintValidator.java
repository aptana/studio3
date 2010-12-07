/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
package com.aptana.editor.js.validator;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;

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
import com.aptana.editor.js.Activator;
import com.aptana.editor.js.parsing.IJSParserConstants;
import com.aptana.libraries.LibrariesPlugin;

public class JSLintValidator implements IValidator
{

	private static final String JSLINT_FILENAME = "fulljslint.js"; //$NON-NLS-1$
	private static Script jsLintScript;
	static
	{
		URL url = LibrariesPlugin.getDefault().getBundle().getEntry("/" + JSLINT_FILENAME); //$NON-NLS-1$
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

	public JSLintValidator()
	{
	}

	public List<IValidationItem> validate(String source, URI path, IValidationManager manager)
	{
		Context context = Context.enter();
		DefaultErrorReporter reporter = new DefaultErrorReporter();
		context.setErrorReporter(reporter);
		parseWithLint(context, source, path, manager);

		return manager.getItems();
	}

	private void parseWithLint(Context context, String source, URI path, IValidationManager manager)
	{
		Scriptable scope = context.initStandardObjects();
		jsLintScript.exec(context, scope);

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
								manager.addError(reason, line, character, 0, path);
							}
							else
							{
								manager.addWarning(reason, line, character, 0, path);
							}
						}
					}
				}

			}
		}
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
			Activator.logError(Messages.JSLintValidator_ERR_FailToGetJSLint, e);
		}
		finally
		{
			Context.exit();
		}

		return null;
	}
}
