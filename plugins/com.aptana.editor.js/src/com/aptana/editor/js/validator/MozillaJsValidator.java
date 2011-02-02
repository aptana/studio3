/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.validator;

import java.net.URI;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Parser;

import com.aptana.editor.common.validator.IValidationItem;
import com.aptana.editor.common.validator.IValidationManager;
import com.aptana.editor.common.validator.IValidator;
import com.aptana.editor.js.parsing.IJSParserConstants;

public class MozillaJsValidator implements IValidator
{

	public List<IValidationItem> validate(String source, URI path, IValidationManager manager)
	{
		Context cx = Context.enter();
		DefaultErrorReporter reporter = new DefaultErrorReporter();
		try
		{
			cx.setErrorReporter(reporter);

			CompilerEnvirons compilerEnv = new CompilerEnvirons();
			compilerEnv.initFromContext(cx);

			Parser p = new Parser(compilerEnv, reporter);
			try
			{
				p.parse(source, path.toString(), 1);
			}
			catch (EvaluatorException e)
			{
			}
		}
		finally
		{
			Context.exit();
		}

		// converts the items from mozilla's error reporter to the ones stored in validation manager
		List<ErrorItem> errors = reporter.getItems();
		String message;
		int severity;
		for (ErrorItem error : errors)
		{
			message = error.getMessage();
			if (!manager.isIgnored(message, IJSParserConstants.LANGUAGE))
			{
				severity = error.getSeverity();
				if (severity == IMarker.SEVERITY_ERROR)
				{
					manager.addError(message, error.getLine(), error.getLineOffset(), 0, path);
				}
				else if (severity == IMarker.SEVERITY_WARNING)
				{
					manager.addWarning(message, error.getLine(), error.getLineOffset(), 0, path);
				}
			}
		}
		return manager.getItems();
	}
}
