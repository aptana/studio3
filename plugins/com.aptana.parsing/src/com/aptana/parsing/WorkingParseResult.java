/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing;

import java.util.ArrayList;
import java.util.List;

import com.aptana.parsing.ast.IParseError;
import com.aptana.parsing.ast.IParseRootNode;

/**
 * A working-copy of the parse result to be used during parsing. After done working with it,
 * {@link #getImmutableResult()} may be used to get an immutable version of the current parse results.
 * 
 * @author Fabio Zadrozny
 */
public class WorkingParseResult
{

	private final List<IParseError> fErrors;
	private IParseRootNode fParseResult;

	public WorkingParseResult()
	{
		fErrors = new ArrayList<IParseError>();
	}

	public void addError(IParseError error)
	{
		fErrors.add(error);
	}

	public void removeError(IParseError error)
	{
		fErrors.remove(error);
	}

	public void addAllErrors(List<IParseError> errors)
	{
		if (errors != null)
		{
			for (IParseError error : errors)
			{
				fErrors.add(error);
			}
		}
	}

	public void setParseResult(IParseRootNode root)
	{
		this.fParseResult = root;
	}

	public List<IParseError> getErrors()
	{
		return fErrors;
	}

	public ParseResult getImmutableResult()
	{
		return new ParseResult(fParseResult, fErrors);
	}

}
