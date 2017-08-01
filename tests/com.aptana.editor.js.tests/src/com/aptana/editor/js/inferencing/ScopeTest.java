/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.inferencing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Path;
import org.junit.Test;

import com.aptana.index.core.Index;
import com.aptana.js.core.index.JSIndexQueryHelper;
import com.aptana.js.core.inferencing.JSNodeTypeInferrer;
import com.aptana.js.core.inferencing.JSPropertyCollection;
import com.aptana.js.core.inferencing.JSScope;
import com.aptana.js.core.parsing.GraalJSParser;
import com.aptana.js.core.parsing.ast.JSNode;
import com.aptana.js.core.parsing.ast.JSParseRootNode;
import com.aptana.parsing.IParser;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.IRange;

public class ScopeTest extends FileContentBasedTestCase
{
	private static final String CURSOR = "${cursor}";
	private static final int CURSOR_LENGTH = CURSOR.length();

	/**
	 * getAST
	 * 
	 * @param source
	 * @return
	 * @throws Exception
	 */
	protected IParseNode getAST(String source) throws Exception
	{
		IParser parser = new GraalJSParser();
		ParseState parseState = new ParseState(source);

		return parser.parse(parseState).getRootNode();
	}

	/**
	 * getSymbols
	 * 
	 * @param resource
	 * @return
	 * @throws Exception
	 */
	protected JSScope getSymbols(String resource) throws Exception
	{
		JSScope result = null;

		// get source from resource
		File file = this.getFile(new Path(resource));
		String source = this.getContent(file);

		// find all test points and clean up source along the way
		List<Integer> offsets = new LinkedList<Integer>();
		int offset = source.indexOf(CURSOR);

		while (offset != -1)
		{
			offsets.add(offset);
			source = source.substring(0, offset) + source.substring(offset + CURSOR_LENGTH);
			offset = source.indexOf(CURSOR);
		}

		// parser
		IParser parser = new GraalJSParser();
		ParseState parseState = new ParseState(source);

		IParseNode root = parser.parse(parseState).getRootNode();

		if (root instanceof JSParseRootNode)
		{
			result = ((JSParseRootNode) root).getGlobals();
		}

		return result;
	}

	/**
	 * getTypes
	 * 
	 * @param symbols
	 * @param symbol
	 * @return
	 */
	protected List<String> getTypes(JSScope symbols, String symbol)
	{
		JSPropertyCollection object = symbols.getLocalSymbol(symbol);
		List<JSNode> nodes = object.getValues();
		Set<String> typeSet = new HashSet<String>();

		for (JSNode node : nodes)
		{
			JSNodeTypeInferrer typeWalker = new JSNodeTypeInferrer(symbols, null, null,
					new JSIndexQueryHelper((Index) null));

			typeWalker.visit(node);

			typeSet.addAll(typeWalker.getTypes());
		}

		return new LinkedList<String>(typeSet);
	}

	/**
	 * showSymbols
	 * 
	 * @param symbols
	 */
	protected void showSymbols(String title, JSScope symbols)
	{
		IRange range = symbols.getRange();

		System.out.println(title);
		System.out.println("====");
		System.out.println("Globals(" + range.getStartingOffset() + "," + range.getEndingOffset() + ")");
		this.showSymbols(symbols, "");
		System.out.println();
	}

	/**
	 * showSymbols
	 * 
	 * @param symbols
	 * @param indent
	 */
	protected void showSymbols(JSScope symbols, String indent)
	{
		for (String symbol : symbols.getLocalSymbolNames())
		{
			List<String> types = this.getTypes(symbols, symbol);

			System.out.print(indent);
			System.out.println(symbol + ": " + types);
		}

		for (JSScope child : symbols.getChildren())
		{
			IRange range = child.getRange();

			System.out.print(indent);
			System.out.println("Child(" + range.getStartingOffset() + "," + range.getEndingOffset() + ")");

			this.showSymbols(child, indent + "  ");
		}
	}

	/**
	 * testGlobalNamedFunction
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGlobalNamedFunction() throws Exception
	{
		JSScope symbols = this.getSymbols("ast-queries/globalNamedFunction.js");
		List<String> names;

		// global
		assertNotNull(symbols);
		names = symbols.getLocalSymbolNames();
		assertNotNull(names);
		assertEquals(1, names.size());
		assertEquals("globalFunction", names.get(0));

		// globalFunction
		List<JSScope> children = symbols.getChildren();
		assertNotNull(children);
		assertEquals(1, children.size());

		JSScope child = children.get(0);
		names = child.getLocalSymbolNames();
		assertNotNull(names);
		assertEquals(0, names.size());

		// this.showSymbols("globalNamedFunction.js", symbols);
	}

	/**
	 * testGlobalVarFunction
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGlobalVarFunction() throws Exception
	{
		JSScope symbols = this.getSymbols("ast-queries/globalVarFunction.js");
		List<String> names;

		// global
		assertNotNull(symbols);
		names = symbols.getLocalSymbolNames();
		assertNotNull(names);
		assertEquals(1, names.size());
		assertEquals("globalVarFunction", names.get(0));

		// globalVarFunction
		List<JSScope> children = symbols.getChildren();
		assertNotNull(children);
		assertEquals(1, children.size());

		JSScope child = children.get(0);
		names = child.getLocalSymbolNames();
		assertNotNull(names);
		assertEquals(0, names.size());

		// this.showSymbols("globalVarFunction.js", symbols);
	}

	/**
	 * testGlobalNamedVarFunction
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGlobalNamedVarFunction() throws Exception
	{
		JSScope symbols = this.getSymbols("ast-queries/globalNamedVarFunction.js");
		List<String> names;

		// global
		assertNotNull(symbols);
		names = symbols.getLocalSymbolNames();
		assertNotNull(names);
		assertEquals(2, names.size());
		assertTrue(names.contains("globalVarFunction"));
		assertTrue(names.contains("globalFunction"));

		// globalVarFunction/globalFunction
		List<JSScope> children = symbols.getChildren();
		assertNotNull(children);
		assertEquals(1, children.size());

		JSScope child = children.get(0);
		names = child.getLocalSymbolNames();
		assertNotNull(names);
		assertEquals(0, names.size());

		// this.showSymbols("globalNamedVarFunction.js", symbols);
	}

	/**
	 * testGlobalVars
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGlobalVars() throws Exception
	{
		JSScope symbols = this.getSymbols("ast-queries/globalVars.js");
		List<String> names;

		// global
		assertNotNull(symbols);
		names = symbols.getLocalSymbolNames();
		assertNotNull(names);
		assertEquals(6, names.size());
		assertTrue(names.contains("localVar1"));
		assertTrue(names.contains("localVar2"));
		assertTrue(names.contains("localVar3"));
		assertTrue(names.contains("localVar4"));
		assertTrue(names.contains("localVar5"));
		assertTrue(names.contains("localVar6"));

		List<JSScope> children = symbols.getChildren();
		assertNotNull(children);
		assertEquals(0, children.size());

		// this.showSymbols("globalVars.js", symbols);
	}

	/**
	 * testLocalVars
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLocalVars() throws Exception
	{
		JSScope symbols = this.getSymbols("ast-queries/localVars.js");
		List<String> names;

		// global
		assertNotNull(symbols);
		names = symbols.getLocalSymbolNames();
		assertNotNull(names);
		assertEquals(1, names.size());
		assertEquals("globalFunction", names.get(0));

		// globalFunction
		List<JSScope> children = symbols.getChildren();
		assertNotNull(children);
		assertEquals(1, children.size());

		JSScope child = children.get(0);
		names = child.getLocalSymbolNames();
		assertNotNull(names);
		assertEquals(6, names.size());
		assertTrue(names.contains("localVar1"));
		assertTrue(names.contains("localVar2"));
		assertTrue(names.contains("localVar3"));
		assertTrue(names.contains("localVar4"));
		assertTrue(names.contains("localVar5"));
		assertTrue(names.contains("localVar6"));

		// this.showSymbols("localVars.js", symbols);
	}

	/**
	 * testParameters
	 * 
	 * @throws Exception
	 */
	@Test
	public void testParameters() throws Exception
	{
		JSScope symbols = this.getSymbols("ast-queries/parameters.js");
		List<String> names;

		// global
		assertNotNull(symbols);
		names = symbols.getLocalSymbolNames();
		assertNotNull(names);
		assertEquals(1, names.size());
		assertEquals("globalFunction", names.get(0));

		// globalFunction
		List<JSScope> children = symbols.getChildren();
		assertNotNull(children);
		assertEquals(1, children.size());

		JSScope child = children.get(0);
		names = child.getLocalSymbolNames();
		assertEquals(2, names.size());
		assertTrue(names.contains("parameter1"));
		assertTrue(names.contains("parameter2"));

		// this.showSymbols("parameters.js", symbols);
	}

	/**
	 * testNestedFunctions
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNestedFunctions() throws Exception
	{
		JSScope symbols = this.getSymbols("ast-queries/nestedFunctions.js");
		List<String> names;

		// global
		assertNotNull(symbols);
		names = symbols.getLocalSymbolNames();
		assertNotNull(names);
		assertEquals(1, names.size());
		assertEquals("outerFunction", names.get(0));

		// outerFunction
		List<JSScope> children = symbols.getChildren();
		assertNotNull(children);
		assertEquals(1, children.size());

		JSScope child = children.get(0);
		names = child.getLocalSymbolNames();
		assertNotNull(names);
		assertEquals(3, names.size());
		assertTrue(names.contains("innerFunction"));
		assertTrue(names.contains("outerParam1"));
		assertTrue(names.contains("outerParam2"));

		// innerFunction
		children = child.getChildren();
		assertNotNull(children);
		assertEquals(1, children.size());

		child = children.get(0);
		names = child.getLocalSymbolNames();
		assertNotNull(names);
		assertEquals(2, names.size());
		assertTrue(names.contains("innerParam1"));
		assertTrue(names.contains("innerParam2"));

		// this.showSymbols("nestedFunctions.js", symbols);
	}

	/**
	 * testNestedFunctions2
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNestedFunctions2() throws Exception
	{
		JSScope symbols = this.getSymbols("ast-queries/nestedFunctions2.js");
		List<String> names;

		// global
		assertNotNull(symbols);
		names = symbols.getLocalSymbolNames();
		assertNotNull(names);
		assertEquals(3, names.size());
		assertTrue(names.contains("global1"));
		assertTrue(names.contains("global2"));
		assertTrue(names.contains("functionA"));

		// functionA
		List<JSScope> children = symbols.getChildren();
		assertNotNull(children);
		assertEquals(1, children.size());

		JSScope child = children.get(0);
		names = child.getLocalSymbolNames();
		assertNotNull(names);
		assertEquals(5, names.size());
		assertTrue(names.contains("functionAParam1"));
		assertTrue(names.contains("functionAParam2"));
		assertTrue(names.contains("functionALocal"));
		assertTrue(names.contains("functionB"));
		assertTrue(names.contains("functionB2"));

		children = child.getChildren();
		assertNotNull(children);
		assertEquals(2, children.size());

		// functionB
		child = children.get(0);
		names = child.getLocalSymbolNames();
		assertNotNull(names);
		assertEquals(4, names.size());
		assertTrue(names.contains("functionBParam1"));
		assertTrue(names.contains("functionBParam2"));
		assertTrue(names.contains("functionBLocal"));
		assertTrue(names.contains("functionC"));

		// functionC
		List<JSScope> grandchildren = child.getChildren();
		assertNotNull(grandchildren);
		assertEquals(1, grandchildren.size());

		JSScope grandchild = grandchildren.get(0);
		names = grandchild.getLocalSymbolNames();
		assertNotNull(names);
		assertEquals(3, names.size());
		assertTrue(names.contains("functionCParam1"));
		assertTrue(names.contains("functionCParam2"));
		assertTrue(names.contains("functionCLocal"));

		// functoinB2
		child = children.get(1);
		names = child.getLocalSymbolNames();
		assertNotNull(names);
		assertEquals(2, names.size());
		assertTrue(names.contains("functionB2Param"));
		assertTrue(names.contains("functionB2Local"));

		// this.showSymbols("nestedFunctions2.js", symbols);
	}

	/**
	 * testPrimitives
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPrimitives() throws Exception
	{
		JSScope symbols = this.getSymbols("ast-queries/primitives.js");
		List<String> names;

		assertNotNull(symbols);
		names = symbols.getLocalSymbolNames();
		assertNotNull(names);
		assertEquals(8, names.size());
		assertTrue(names.contains("booleanTrue"));
		assertTrue(names.contains("booleanFalse"));
		assertTrue(names.contains("doubleQuotedString"));
		assertTrue(names.contains("singleQuotedString"));
		assertTrue(names.contains("array"));
		assertTrue(names.contains("object"));
		assertTrue(names.contains("number"));
		assertTrue(names.contains("regex"));

		List<JSScope> children = symbols.getChildren();
		assertNotNull(children);
		assertEquals(0, children.size());

		// this.showSymbols("primitives.js", symbols);
	}

	/**
	 * testMultipleTypes
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMultipleTypes() throws Exception
	{
		JSScope symbols = this.getSymbols("ast-queries/multipleTypes.js");
		List<String> names;

		assertNotNull(symbols);
		names = symbols.getLocalSymbolNames();
		assertNotNull(names);
		assertEquals(1, names.size());
		assertEquals("stringAndNumber", names.get(0));

		List<JSScope> children = symbols.getChildren();
		assertNotNull(children);
		assertEquals(0, children.size());

		// this.showSymbols("multipleTypes.js", symbols);
	}

	@Test
	public void testImpliedGlobal() throws Exception
	{
		JSScope symbols = this.getSymbols("ast-queries/impliedGlobal.js");
		Set<String> names;

		assertNotNull(symbols);
		names = new HashSet<String>(symbols.getLocalSymbolNames());
		assertNotNull(names);
		assertEquals(2, names.size());
		assertTrue(names.contains("abc"));
		assertTrue(names.contains("ghi"));

		List<JSScope> children = symbols.getChildren();
		assertNotNull(children);
		assertEquals(1, children.size());

		JSScope child = children.get(0);
		names = new HashSet<String>(child.getLocalSymbolNames());
		assertNotNull(names);
		assertEquals(1, names.size());
		assertTrue(names.contains("def"));
	}

	@Test
	public void testImpliedGlobal2() throws Exception
	{
		JSScope symbols = this.getSymbols("ast-queries/impliedGlobal2.js");
		Set<String> names;

		assertNotNull(symbols);
		names = new HashSet<String>(symbols.getLocalSymbolNames());
		assertNotNull(names);
		assertEquals(3, names.size());
		assertTrue(names.contains("abc"));
		assertTrue(names.contains("ghi"));
		assertTrue(names.contains("pqr"));

		List<JSScope> children = symbols.getChildren();
		assertNotNull(children);
		assertEquals(1, children.size());

		JSScope child = children.get(0);
		names = new HashSet<String>(child.getLocalSymbolNames());
		assertNotNull(names);
		assertEquals(2, names.size());
		assertTrue(names.contains("def"));
		assertTrue(names.contains("jkl"));

		List<JSScope> grandchildren = child.getChildren();
		assertNotNull(grandchildren);
		assertEquals(1, grandchildren.size());

		JSScope grandchild = grandchildren.get(0);
		names = new HashSet<String>(grandchild.getLocalSymbolNames());
		assertNotNull(names);
		assertEquals(1, names.size());
		assertTrue(names.contains("mno"));
	}
}
