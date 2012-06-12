/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.aptana.parsing.ast.IParseError;
import com.aptana.parsing.ast.IParseRootNode;

public class ParseResult
{

	private final IParseRootNode fParseResult;
	private final List<IParseError> fErrors;
	public final static ParseResult EMPTY = new ParseResult();

	public ParseResult(IParseRootNode parseResult, List<IParseError> errors)
	{
		fParseResult = parseResult;
		fErrors = Collections.unmodifiableList(Arrays.asList(errors.toArray(new IParseError[errors.size()])));
	}

	@SuppressWarnings("unchecked")
	private ParseResult() // Use ParseResult.EMPTY instead
	{
		this(null, Collections.EMPTY_LIST);
	}

	/**
	 * @return the ast with the result of a parse.
	 */
	public IParseRootNode getRootNode()
	{
		return fParseResult;
	}

	/**
	 * @return a list with the errors (which may not be modified).
	 */
	public List<IParseError> getErrors()
	{
		return fErrors;
	}

}
