package com.aptana.editor.js.contentassist;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Path;

import com.aptana.editor.js.contentassist.JSASTQueryHelper.Classification;
import com.aptana.editor.js.parsing.JSParser;
import com.aptana.editor.js.tests.FileContentBasedTests;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;

public class ASTQueryTests extends FileContentBasedTests
{
	private static final String CURSOR = "${cursor}";
	private static final int CURSOR_LENGTH = CURSOR.length();
	private JSASTQueryHelper _queryHelper;

	/**
	 * getAST
	 * 
	 * @param source
	 * @return
	 * @throws Exception
	 */
	protected IParseNode getAST(String source) throws Exception
	{
		JSParser parser = new JSParser();
		ParseState parseState = new ParseState();

		parseState.setEditState(source, source, 0, 0);
		parser.parse(parseState);

		return parseState.getParseResult();
	}

	/**
	 * getTestContext
	 * 
	 * @param resource
	 * @param expectedTestPointCount
	 * @return
	 * @throws Exception
	 */
	protected List<IParseNode> getTestContext(String resource, int expectedTestPointCount) throws Exception
	{
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

		// parse and grab the AST
		IParseNode ast = this.getAST(source);

		// find target nodes
		List<IParseNode> targetNodes = new LinkedList<IParseNode>();

		for (int o : offsets)
		{
			IParseNode targetNode = (ast != null && ast.contains(o)) ? ast.getNodeAtOffset(o) : ast;

			targetNodes.add(targetNode);
		}

		assertNotNull(targetNodes);
		assertEquals(expectedTestPointCount, targetNodes.size());

		return targetNodes;
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		this._queryHelper = new JSASTQueryHelper();
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		this._queryHelper = null;
	}

	/**
	 * testGlobalNamedFunction
	 * 
	 * @throws Exception
	 */
	public void testGlobalNamedFunction() throws Exception
	{
		// make sure we have the expected number of test points
		List<IParseNode> targetNodes = this.getTestContext("ast-queries/globalNamedFunction.js", 1);

		List<String> functions = this._queryHelper.getChildFunctions(targetNodes.get(0));
		assertNotNull(functions);
		assertEquals(1, functions.size());
		assertEquals("globalFunction", functions.get(0));
	}

	/**
	 * testGlobalVarFunction
	 * 
	 * @throws Exception
	 */
	public void testGlobalVarFunction() throws Exception
	{
		List<IParseNode> targetNodes = this.getTestContext("ast-queries/globalVarFunction.js", 1);
		
		List<String> functions = this._queryHelper.getChildFunctions(targetNodes.get(0));
		assertNotNull(functions);
		assertEquals(1, functions.size());
		assertEquals("globalVarFunction", functions.get(0));
	}

	/**
	 * testGlobalNamedVarFunction
	 * 
	 * @throws Exception
	 */
	public void testGlobalNamedVarFunction() throws Exception
	{
		List<IParseNode> targetNodes = this.getTestContext("ast-queries/globalNamedVarFunction.js", 1);
		
		List<String> functions = this._queryHelper.getChildFunctions(targetNodes.get(0));
		assertNotNull(functions);
		assertEquals(2, functions.size());
		assertTrue(functions.contains("globalFunction"));
		assertTrue(functions.contains("globalVarFunction"));
	}

	/**
	 * testGlobalVars
	 * 
	 * @throws Exception
	 */
	public void testGlobalVars() throws Exception
	{
		List<IParseNode> targetNodes = this.getTestContext("ast-queries/globalVars.js", 1);
		IParseNode node = targetNodes.get(0);
		
		List<String> functions = this._queryHelper.getChildFunctions(node);
		assertNotNull(functions);
		assertEquals(0, functions.size());
		
		List<String> vars = this._queryHelper.getChildVarNonFunctions(node);
		assertNotNull(vars);
		assertEquals(6, vars.size());
		assertTrue(vars.contains("localVar1"));
		assertTrue(vars.contains("localVar2"));
		assertTrue(vars.contains("localVar3"));
		assertTrue(vars.contains("localVar4"));
		assertTrue(vars.contains("localVar5"));
		assertTrue(vars.contains("localVar6"));
	}

	/**
	 * testLocalVars
	 * 
	 * @throws Exception
	 */
	public void testLocalVars() throws Exception
	{
		List<IParseNode> targetNodes = this.getTestContext("ast-queries/localVars.js", 2);
		int index = 0;
		
		// position 1
		List<String> functions = this._queryHelper.getChildFunctions(targetNodes.get(index));
		assertNotNull(functions);
		assertEquals(0, functions.size());
		
		List<String> vars = this._queryHelper.getChildVarNonFunctions(targetNodes.get(index++));
		assertNotNull(vars);
		assertEquals(6, vars.size());
		assertTrue(vars.contains("localVar1"));
		assertTrue(vars.contains("localVar2"));
		assertTrue(vars.contains("localVar3"));
		assertTrue(vars.contains("localVar4"));
		assertTrue(vars.contains("localVar5"));
		assertTrue(vars.contains("localVar6"));
		
		// position 2
		functions = this._queryHelper.getChildFunctions(targetNodes.get(index++));
		assertNotNull(functions);
		assertEquals(1, functions.size());
		assertEquals("globalFunction", functions.get(0));
	}
	
	/**
	 * testParameters
	 * 
	 * @throws Exception
	 */
	public void testParameters() throws Exception
	{
		List<IParseNode> targetNodes = this.getTestContext("ast-queries/parameters.js", 2);
		int index = 0;
		
		// position 1
		Map<String,Classification> symbols = this._queryHelper.getSymbolsInScope(targetNodes.get(index++));
		assertNotNull(symbols);
		assertEquals(2, symbols.size());
		assertTrue(symbols.containsKey("parameter1"));
		assertTrue(symbols.containsKey("parameter2"));
		
		// position 2
		symbols = this._queryHelper.getSymbolsInScope(targetNodes.get(index++));
		assertNotNull(symbols);
		assertEquals(0, symbols.size());
	}

	/**
	 * testNestedFunctions
	 * 
	 * @throws Exception
	 */
	public void testNestedFunctions() throws Exception
	{
		List<IParseNode> targetNodes = this.getTestContext("ast-queries/nestedFunctions.js", 3);
		int index = 0;
		
		// position 1
		Map<String,Classification> symbols = this._queryHelper.getSymbolsInScope(targetNodes.get(index++));
		assertNotNull(symbols);
		assertEquals(4, symbols.size());
		assertTrue(symbols.containsKey("outerParam1"));
		assertTrue(symbols.containsKey("outerParam2"));
		assertTrue(symbols.containsKey("innerParam1"));
		assertTrue(symbols.containsKey("innerParam2"));
		
		// position 2
		symbols = this._queryHelper.getSymbolsInScope(targetNodes.get(index++));
		assertNotNull(symbols);
		assertEquals(2, symbols.size());
		assertTrue(symbols.containsKey("outerParam1"));
		assertTrue(symbols.containsKey("outerParam2"));
		
		// position 3
		symbols = this._queryHelper.getSymbolsInScope(targetNodes.get(index++));
		assertNotNull(symbols);
		assertEquals(0, symbols.size());
	}
}
