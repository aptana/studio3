/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core.build;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;

import beaver.Symbol;

import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParseResult;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.WorkingParseResult;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.parsing.ast.ParseError;
import com.aptana.parsing.ast.ParseRootNode;

/**
 * @author Fabio
 */
public class BuildContextTest extends TestCase
{

	public void testBuildContext() throws Exception
	{
		final int[] reparses = new int[] { 0 };
		final String[] content = new String[] { "" };
		final ParseRootNode parseRootNode = new ParseRootNode(new Symbol[0], 0, 0)
		{
			public String getLanguage()
			{
				return "test";
			}
		};
		BuildContext buildContext = new BuildContext(null)
		{
			@Override
			public String getContentType() throws CoreException
			{
				return "test";
			}

			@Override
			protected ParseResult parse(String contentType, IParseState parseState, WorkingParseResult working)
					throws Exception
			{
				reparses[0] += 1;
				working.addError(new ParseError("language", new Symbol(1), null));
				working.setParseResult(parseRootNode);
				return working.getImmutableResult();
			}

			@Override
			public synchronized String getContents()
			{
				return content[0];
			}
		};

		ParseState parseState = new ParseState(buildContext.getContents());
		ParseResult parseResult = buildContext.getAST(parseState);
		IParseRootNode ast = parseResult.getRootNode();
		assertEquals(parseRootNode, ast);
		assertEquals(1, parseResult.getErrors().size());
		assertEquals(1, reparses[0]);

		parseState = new ParseState(buildContext.getContents());
		ast = parseResult.getRootNode(); // This time it's cached.
		assertEquals(1, parseResult.getErrors().size()); // errors must be copied
		assertEquals(parseRootNode, ast);
		assertEquals(1, reparses[0]);

		content[0] = "new";
		ast = buildContext.getAST(); // Not cached again.
		assertEquals(parseRootNode, ast);
		assertEquals(2, reparses[0]);
	}
}
