package com.aptana.editor.js.inferencing;

import java.util.LinkedList;
import java.util.List;

import com.aptana.editor.js.parsing.JSParser;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSParseRootNode;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;

import junit.framework.TestCase;

public class InferencingTestsBase extends TestCase
{
	/**
	 * getParseRootNode
	 * 
	 * @param source
	 * @return
	 */
	protected IParseNode getParseRootNode(String source)
	{
		JSParser parser = new JSParser();
		ParseState parseState = new ParseState();

		parseState.setEditState(source, source, 0, 0);

		try
		{
			parser.parse(parseState);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}

		return parseState.getParseResult();
	}

	/**
	 * getGlobals
	 * 
	 * @param source
	 * @return
	 */
	protected JSScope getGlobals(String source)
	{
		IParseNode root = this.getParseRootNode(source);
		assertTrue(root instanceof JSParseRootNode);

		JSSymbolCollector s = new JSSymbolCollector();
		((JSParseRootNode) root).accept(s);

		return s.getScope();
	}

	/**
	 * getTypes
	 * 
	 * @param node
	 * @return
	 */
	protected List<String> getTypes(JSScope globals, JSNode node)
	{
		JSNodeTypeInferrer walker = new JSNodeTypeInferrer(globals, null, null);

		node.accept(walker);

		return walker.getTypes();
	}

	/**
	 * getTypes
	 * 
	 * @param nodes
	 * @return
	 */
	protected List<String> getTypes(JSScope globals, List<JSNode> nodes)
	{
		List<String> result = new LinkedList<String>();

		for (IParseNode node : nodes)
		{
			JSNodeTypeInferrer walker = new JSNodeTypeInferrer(globals, null, null);

			assertTrue(node instanceof JSNode);

			((JSNode) node).accept(walker);

			result.addAll(walker.getTypes());
		}

		return result;
	}

	/**
	 * varTypeTests
	 * 
	 * @param source
	 * @param symbol
	 * @param types
	 */
	public void varTypeTests(String source, String symbol, String... types)
	{
		JSScope globals = this.getGlobals(source);

		assertTrue(globals.hasLocalSymbol(symbol));
		JSPropertyCollection object = globals.getSymbol(symbol);
		List<JSNode> values = object.getValues();
		assertNotNull(values);
		assertEquals(1, values.size());

		List<String> symbolTypes = this.getTypes(globals, values);
		assertNotNull(types);
		assertEquals(types.length, symbolTypes.size());

		for (String type : types)
		{
			assertTrue(symbolTypes.contains(type));
		}
	}
}
