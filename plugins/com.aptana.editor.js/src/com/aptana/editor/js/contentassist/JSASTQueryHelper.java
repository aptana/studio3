package com.aptana.editor.js.contentassist;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSNodeTypes;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.xpath.ParseNodeXPath;

public class JSASTQueryHelper
{
	public enum Classification
	{
		PROPERTY,
		FUNCTION
	}
	
	private static XPath SIBLING_VARS;
	private static XPath ANCESTOR_FUNCTION_VARS;
	private static XPath NAMED_FUNCTIONS;
	private static XPath VAR_FUNCTIONS;
	private static XPath IDENTIFIER_ASSIGNMENTS;
	private static XPath NON_FUNCTION_VARS;
	private static XPath ANCESTOR_FUNCTIONS;
	private static XPath ALL_VARS;
	
	static
	{
		try
		{
			ALL_VARS = new ParseNodeXPath("descendant::var/declaration/identifier[position() = 1]");
			
			ANCESTOR_FUNCTIONS = new ParseNodeXPath("ancestor::function");
			ANCESTOR_FUNCTION_VARS = new ParseNodeXPath("ancestor::function/statements/var/declaration/identifier[position() = 1]|ancestor::function/parameters/identifier");
			
			IDENTIFIER_ASSIGNMENTS = new ParseNodeXPath("//assign/identifier[position() = 1]");
			
			NON_FUNCTION_VARS = new ParseNodeXPath("./var/declaration/identifier[position() = 1 and count(following-sibling::function) = 0]");
			VAR_FUNCTIONS = new ParseNodeXPath("./var/declaration/identifier[count(following-sibling::function) > 0]");
			
			SIBLING_VARS = new ParseNodeXPath("../statements/var/declaration/identifier[position() = 1]");
			
			NAMED_FUNCTIONS = new ParseNodeXPath("function[string-length(@name) > 0]");
		}
		catch (JaxenException e)
		{
			if (Platform.inDevelopmentMode())
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * JSASTQueryHelper
	 */
	public JSASTQueryHelper()
	{
	}
	
	/**
	 * declaredInScope
	 * 
	 * @param ast
	 * @param name
	 * @return
	 */
	public boolean declaredInScope(IParseNode ast, String name)
	{
		Object list;
		boolean result = false;
		
		try
		{
			// look at any siblings
			list = SIBLING_VARS.evaluate(ast);
			
			if (list instanceof List<?>)
			{
				List<?> items = (List<?>) list;
				
				for (Object item : items)
				{
					if (name.equals(item.toString()))
					{
						result = true;
						break;
					}
				}
			}
			
			// look at any parent function's vars and args
			if (result == false)
			{
				list = ANCESTOR_FUNCTION_VARS.evaluate(ast);
				
				if (list instanceof List<?>)
				{
					List<?> items = (List<?>) list;
					
					for (Object item : items)
					{
						if (name.equals(item.toString()))
						{
							result = true;
							break;
						}
					}
				}
			}
		}
		catch (JaxenException e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * getSymbolsInScope
	 * 
	 * @param ast
	 * @return
	 */
	public Map<String,Classification> getSymbolsInScope(IParseNode ast)
	{
		final Map<String,Classification> result = new HashMap<String,Classification>();
		
		this.processXPath(
			ANCESTOR_FUNCTIONS,
			ast,
			new ItemProcessor() {
				public void process(Object item)
				{
					final JSFunctionNode function = (JSFunctionNode) item;
					IParseNode body = function.getBody();
					
					// add args
					for (IParseNode arg : function.getArgs())
					{
						result.put(arg.toString(), Classification.PROPERTY);
					}
					
					// add named functions
					for (String func : getNamedFunctions(body))
					{
						result.put(func, Classification.FUNCTION);
					}
					
					processXPath(
						ALL_VARS,
						body,
						new ItemProcessor() {
							public void process(Object item)
							{
								IParseNode node = (IParseNode) item;
								IParseNode parent = node.getParent();
								
								while (parent != null)
								{
									if (parent.getType() == JSNodeTypes.FUNCTION)
									{
										if (parent == function)
										{
											Classification c = (node.getType() == JSNodeTypes.FUNCTION) ? Classification.FUNCTION : Classification.PROPERTY;
											
											result.put(item.toString(), c);
										}
										break;
									}
									else
									{
										parent = parent.getParent();
									}
								}
							}
						}
					);
					
//					// add non-function vars
//					for (String var : getNonFunctionDeclarations(body))
//					{
//						result.put(var, Classification.PROPERTY);
//					}
//					
//					// vars that are functions
//					for (String var : getVarDeclaredFunctions(body))
//					{
//						result.put(var, Classification.FUNCTION);
//					}
				}
			}
		);
		
//		for (String global : this.getAccidentalGlobals(ast))
//		{
//			result.put(global, Classification.PROPERTY);
//		}
		
		return result;
	}
	
	/**
	 * getNonFunctionDeclarations
	 * 
	 * @param ast
	 * @return
	 */
	public List<String> getNonFunctionDeclarations(IParseNode ast)
	{
		final List<String> result = new LinkedList<String>();

		this.processXPath(
			NON_FUNCTION_VARS,
			ast,
			new ItemProcessor() {
				public void process(Object item)
				{
					result.add(item.toString());
				}
			}
		);

		return result;
	}

	/**
	 * getAccidentalGlobals
	 * 
	 * @param ast
	 * @return
	 */
	public List<String> getAccidentalGlobals(IParseNode ast)
	{
		final Set<String> globals = new HashSet<String>();
		
		this.processXPath(
			IDENTIFIER_ASSIGNMENTS,
			ast,
			new ItemProcessor() {
				public void process(Object item)
				{
					if (item instanceof IParseNode)
					{
						IParseNode node = (IParseNode) item;
						String name = node.toString();
						
						if (declaredInScope(node, name) == false)
						{
							globals.add(name);
						}
					}
				}
			}
		);
		
		List<String> result = new LinkedList<String>(globals);
		Collections.sort(result);
		
		return result;
	}

	/**
	 * getGlobalFunctions
	 * 
	 * @param ast
	 * @return
	 */
	public List<String> getGlobalFunctions(IParseNode ast)
	{
		List<String> result = this.getNamedFunctions(ast);

		result.addAll(this.getVarDeclaredFunctions(ast));

		return result;
	}

	/**
	 * getNamedFunctions
	 * 
	 * @param ast
	 * @return
	 */
	public List<String> getNamedFunctions(IParseNode ast)
	{
		final List<String> result = new LinkedList<String>();

		this.processXPath(
			NAMED_FUNCTIONS,
			ast,
			new ItemProcessor() {
				public void process(Object item)
				{
					JSFunctionNode function = (JSFunctionNode) item;
					
					result.add(function.getName());
				}
			}
		);
		
		return result;
	}
	
	/**
	 * getVarDeclaredFunctions
	 * 
	 * @param ast
	 * @return
	 */
	public List<String> getVarDeclaredFunctions(IParseNode ast)
	{
		final List<String> result = new LinkedList<String>();
		
		this.processXPath(
			VAR_FUNCTIONS,
			ast,
			new ItemProcessor() {
				public void process(Object item)
				{
					result.add(item.toString());
				}
			}
		);

		return result;
	}
	
	/**
	 * processXPath
	 * 
	 * @param expression
	 * @param node
	 * @param processor
	 */
	private void processXPath(XPath expression, IParseNode node, ItemProcessor processor)
	{
		if (expression != null && node != null && processor != null)
		{
			try
			{
				Object list = expression.evaluate(node);
	
				if (list instanceof List<?>)
				{
					List<?> items = (List<?>) list;
	
					for (Object item : items)
					{
						processor.process(item);
					}
				}
			}
			catch (JaxenException e)
			{
				e.printStackTrace();
			}
		}
	}
}
