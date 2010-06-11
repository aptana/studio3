package com.aptana.editor.js.contentassist;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Path;

import com.aptana.editor.js.parsing.JSParseState;
import com.aptana.editor.js.parsing.JSParser;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.tests.FileContentBasedTests;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.Scope;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.IRange;

public class ScopeTests extends FileContentBasedTests
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
		JSParser parser = new JSParser();
		ParseState parseState = new JSParseState();

		parseState.setEditState(source, source, 0, 0);
		parser.parse(parseState);

		return parseState.getParseResult();
	}
	
	/**
	 * getSymbols
	 * 
	 * @param resource
	 * @return
	 * @throws Exception 
	 */
	protected Scope<JSNode> getSymbols(String resource) throws Exception
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

		// parser
		JSParser parser = new JSParser();
		ParseState parseState = new ParseState();

		parseState.setEditState(source, source, 0, 0);
		parser.parse(parseState);
		
		return parser.getScope();
	}
	
	/**
	 * getTypes
	 * 
	 * @param symbols
	 * @param symbol
	 * @return
	 */
	protected List<String> getTypes(Scope<JSNode> symbols, String symbol)
	{
		List<JSNode> value = symbols.getLocalSymbol(symbol);
		Set<String> typeSet = new HashSet<String>();
		
		for (JSNode node : value)
		{
			typeSet.addAll(node.getReturnTypes());
		}
		
		return new LinkedList<String>(typeSet);
	}
	
	/**
	 * showSymbols
	 * 
	 * @param symbols
	 */
	protected void showSymbols(String title, Scope<JSNode> symbols)
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
	protected void showSymbols(Scope<JSNode> symbols, String indent)
	{
		for (String symbol : symbols.getLocalSymbolNames())
		{
			List<String> types = this.getTypes(symbols, symbol);
			
			System.out.print(indent);
			System.out.println(symbol + ": " + types);
		}
		
		for (Scope<JSNode> child : symbols.getChildren())
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
	public void testGlobalNamedFunction() throws Exception
	{
		Scope<JSNode> symbols = this.getSymbols("ast-queries/globalNamedFunction.js");
		
		assertNotNull(symbols);
		assertEquals(1, symbols.getLocalSymbolNames().size());
		
		List<Scope<JSNode>> children = symbols.getChildren();
		assertNotNull(children);
		assertEquals(1, children.size());
		
		Scope<JSNode> child = children.get(0);
		assertEquals(0, child.getLocalSymbolNames().size());
		
		//this.showSymbols("globalNamedFunction.js", symbols);
	}
	
	/**
	 * testGlobalVarFunction
	 * 
	 * @throws Exception
	 */
	public void testGlobalVarFunction() throws Exception
	{
		Scope<JSNode> symbols = this.getSymbols("ast-queries/globalVarFunction.js");
		
		assertNotNull(symbols);
		assertEquals(1, symbols.getLocalSymbolNames().size());
		
		List<Scope<JSNode>> children = symbols.getChildren();
		assertNotNull(children);
		assertEquals(1, children.size());
		
		Scope<JSNode> child = children.get(0);
		assertEquals(0, child.getLocalSymbolNames().size());
		
		//this.showSymbols("globalVarFunction.js", symbols);
	}
	
	/**
	 * testGlobalNamedVarFunction
	 * 
	 * @throws Exception
	 */
	public void testGlobalNamedVarFunction() throws Exception
	{
		Scope<JSNode> symbols = this.getSymbols("ast-queries/globalNamedVarFunction.js");
		
		assertNotNull(symbols);
		assertEquals(2, symbols.getLocalSymbolNames().size());
		
		List<Scope<JSNode>> children = symbols.getChildren();
		assertNotNull(children);
		assertEquals(1, children.size());
		
		Scope<JSNode> child = children.get(0);
		assertEquals(0, child.getLocalSymbolNames().size());
		
		//this.showSymbols("globalNamedVarFunction.js", symbols);
	}
	
	/**
	 * testGlobalVars
	 * 
	 * @throws Exception
	 */
	public void testGlobalVars() throws Exception
	{
		Scope<JSNode> symbols = this.getSymbols("ast-queries/globalVars.js");
		
		assertNotNull(symbols);
		assertEquals(6, symbols.getLocalSymbolNames().size());
		
		List<Scope<JSNode>> children = symbols.getChildren();
		assertNotNull(children);
		assertEquals(0, children.size());
		
		//this.showSymbols("globalVars.js", symbols);
	}
	
	/**
	 * testLocalVars
	 * 
	 * @throws Exception
	 */
	public void testLocalVars() throws Exception
	{
		Scope<JSNode> symbols = this.getSymbols("ast-queries/localVars.js");
		
		assertNotNull(symbols);
		assertEquals(1, symbols.getLocalSymbolNames().size());
		
		List<Scope<JSNode>> children = symbols.getChildren();
		assertNotNull(children);
		assertEquals(1, children.size());
		
		Scope<JSNode> child = children.get(0);
		assertEquals(6, child.getLocalSymbolNames().size());
		
		//this.showSymbols("localVars.js", symbols);
	}
	
	/**
	 * testParameters
	 * 
	 * @throws Exception
	 */
	public void testParameters() throws Exception
	{
		Scope<JSNode> symbols = this.getSymbols("ast-queries/parameters.js");
		
		assertNotNull(symbols);
		assertEquals(1, symbols.getLocalSymbolNames().size());
		
		List<Scope<JSNode>> children = symbols.getChildren();
		assertNotNull(children);
		assertEquals(1, children.size());
		
		Scope<JSNode> child = children.get(0);
		assertEquals(2, child.getLocalSymbolNames().size());
		
		this.showSymbols("parameters.js", symbols);
	}
	
	/**
	 * testNestedFunctions
	 * 
	 * @throws Exception
	 */
	public void testNestedFunctions() throws Exception
	{
		Scope<JSNode> symbols = this.getSymbols("ast-queries/nestedFunctions.js");
		
		assertNotNull(symbols);
		assertEquals(1, symbols.getLocalSymbolNames().size());
		
		List<Scope<JSNode>> children = symbols.getChildren();
		assertNotNull(children);
		assertEquals(1, children.size());
		
		Scope<JSNode> child = children.get(0);
		assertEquals(3, child.getLocalSymbolNames().size());
		
		children = child.getChildren();
		assertNotNull(children);
		assertEquals(1, children.size());
		
		child = children.get(0);
		assertEquals(2, child.getLocalSymbolNames().size());
		
		//this.showSymbols("nestedFunctions.js", symbols);
	}
	
	/**
	 * testNestedFunctions2
	 * 
	 * @throws Exception
	 */
	public void testNestedFunctions2() throws Exception
	{
		Scope<JSNode> symbols = this.getSymbols("ast-queries/nestedFunctions2.js");
		
		// global
		assertNotNull(symbols);
		assertEquals(3, symbols.getLocalSymbolNames().size());
		
		// functionA
		List<Scope<JSNode>> children = symbols.getChildren();
		assertNotNull(children);
		assertEquals(1, children.size());
		
		Scope<JSNode> child = children.get(0);
		assertEquals(5, child.getLocalSymbolNames().size());
		
		children = child.getChildren();
		assertNotNull(children);
		assertEquals(2, children.size());
		
		// functionB
		child = children.get(0);
		assertEquals(4, child.getLocalSymbolNames().size());
		
		// functionC
		List<Scope<JSNode>> grandchildren = child.getChildren();
		assertNotNull(grandchildren);
		assertEquals(1, grandchildren.size());
		
		Scope<JSNode> grandchild = grandchildren.get(0);
		assertEquals(3, grandchild.getLocalSymbolNames().size());
		
		// functoinB2
		child = children.get(1);
		assertEquals(2, child.getLocalSymbolNames().size());
		
		//this.showSymbols("nestedFunctions2.js", symbols);
	}
	
	/**
	 * testPrimitives
	 * 
	 * @throws Exception
	 */
	public void testPrimitives() throws Exception
	{
		Scope<JSNode> symbols = this.getSymbols("ast-queries/primitives.js");
		
		assertNotNull(symbols);
		assertEquals(8, symbols.getLocalSymbolNames().size());
		
		List<Scope<JSNode>> children = symbols.getChildren();
		assertNotNull(children);
		assertEquals(0, children.size());
		
		this.showSymbols("primitives.js", symbols);
	}
	
	/**
	 * testMultipleTypes
	 * 
	 * @throws Exception
	 */
	public void testMultipleTypes() throws Exception
	{
		Scope<JSNode> symbols = this.getSymbols("ast-queries/multipleTypes.js");
		
		assertNotNull(symbols);
		assertEquals(1, symbols.getLocalSymbolNames().size());
		
		List<Scope<JSNode>> children = symbols.getChildren();
		assertNotNull(children);
		assertEquals(0, children.size());
		
		this.showSymbols("multipleTypes.js", symbols);
	}
}
