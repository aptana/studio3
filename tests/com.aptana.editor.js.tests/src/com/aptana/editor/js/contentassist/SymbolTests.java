package com.aptana.editor.js.contentassist;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Path;

import com.aptana.editor.js.parsing.JSParser;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.tests.FileContentBasedTests;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.Scope;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.IRange;

public class SymbolTests extends FileContentBasedTests
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
		ParseState parseState = new ParseState();

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
		
		this.showSymbols("globalNamedFunction.js", symbols);
	}
	
	/**
	 * testGlobalVarFunction
	 * 
	 * @throws Exception
	 */
	public void testGlobalVarFunction() throws Exception
	{
		Scope<JSNode> symbols = this.getSymbols("ast-queries/globalVarFunction.js");
		
		this.showSymbols("globalVarFunction.js", symbols);
	}
	
	/**
	 * testGlobalNamedVarFunction
	 * 
	 * @throws Exception
	 */
	public void testGlobalNamedVarFunction() throws Exception
	{
		Scope<JSNode> symbols = this.getSymbols("ast-queries/globalNamedVarFunction.js");
		
		this.showSymbols("globalNamedVarFunction.js", symbols);
	}
	
	/**
	 * testGlobalVars
	 * 
	 * @throws Exception
	 */
	public void testGlobalVars() throws Exception
	{
		Scope<JSNode> symbols = this.getSymbols("ast-queries/globalVars.js");
		
		this.showSymbols("globalVars.js", symbols);
	}
	
	/**
	 * testLocalVars
	 * 
	 * @throws Exception
	 */
	public void testLocalVars() throws Exception
	{
		Scope<JSNode> symbols = this.getSymbols("ast-queries/localVars.js");
		
		this.showSymbols("localVars.js", symbols);
	}
	
	/**
	 * testParameters
	 * 
	 * @throws Exception
	 */
	public void testParameters() throws Exception
	{
		Scope<JSNode> symbols = this.getSymbols("ast-queries/parameters.js");
		
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
		
		this.showSymbols("nestedFunctions.js", symbols);
	}
	
	/**
	 * testNestedFunctions2
	 * 
	 * @throws Exception
	 */
	public void testNestedFunctions2() throws Exception
	{
		Scope<JSNode> symbols = this.getSymbols("ast-queries/nestedFunctions2.js");
		
		this.showSymbols("nestedFunctions2.js", symbols);
	}
	
	/**
	 * testPrimitives
	 * 
	 * @throws Exception
	 */
	public void testPrimitives() throws Exception
	{
		Scope<JSNode> symbols = this.getSymbols("ast-queries/primitives.js");
		
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
		
		this.showSymbols("multipleTypes.js", symbols);
	}
}
