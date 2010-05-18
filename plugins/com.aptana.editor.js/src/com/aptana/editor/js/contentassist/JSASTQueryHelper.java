package com.aptana.editor.js.contentassist;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jaxen.JaxenException;
import org.jaxen.XPath;

import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSPrimitiveNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.xpath.ParseNodeXPath;

public class JSASTQueryHelper
{
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
		boolean result = false;
		
		try
		{
			XPath xpath = new ParseNodeXPath("ancestor::function/statements/var/declaration/identifier[position() = 1]");
			Object list = xpath.evaluate(ast);
			
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
	public List<String> getSymbolsInScope(IParseNode ast)
	{
		final Set<String> result = new HashSet<String>();
		
		this.processXPath(
			"ancestor::function",
			ast,
			new ItemProcessor() {
				public void process(Object item)
				{
					if (item instanceof JSFunctionNode)
					{
						JSFunctionNode function = (JSFunctionNode) item;
						
						// add args
						for (IParseNode arg : function.getArgs())
						{
							result.add(arg.toString());
						}
						
						// add vars
						result.addAll(getGlobalDeclarations(function.getBody()));
					}
				}
			}
		);
		
		result.addAll(this.getAccidentalGlobals(ast));
		
		return new ArrayList<String>(result);
	}
	
	/**
	 * getGlobalDeclarations
	 * 
	 * @param ast
	 * @return
	 */
	public List<String> getGlobalDeclarations(IParseNode ast)
	{
		final List<String> result = new LinkedList<String>();

		// NOTE: we're using a relative path here so we can use this expression
		// at the top-level of a file and for function bodies
		this.processXPath(
			"var/declaration/identifier[position() = 1 and count(following-sibling::function) = 0]",
			ast,
			new ItemProcessor() {
				public void process(Object item)
				{
					if (item instanceof JSPrimitiveNode)
					{
						result.add(item.toString());
					}
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
		final List<String> result = new LinkedList<String>();
		
		this.processXPath(
			"//assign/identifier[position() = 1]",
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
							result.add(name);
						}
					}
				}
			}
		);
		
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
		final List<String> result = new LinkedList<String>();

		this.processXPath(
			"/function[string-length(@name) > 0]",
			ast,
			new ItemProcessor() {
				public void process(Object item)
				{
					if (item instanceof JSFunctionNode)
					{
						JSFunctionNode function = (JSFunctionNode) item;
						
						result.add(function.getName());
					}
				}
			}
		);

		this.processXPath(
			"/var/declaration/identifier[count(following-sibling::function) > 0]",
			ast,
			new ItemProcessor() {
				public void process(Object item)
				{
					if (item instanceof JSPrimitiveNode)
					{
						result.add(item.toString());
					}
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
	private void processXPath(String expression, IParseNode node, ItemProcessor processor)
	{
		if (expression != null && expression.length() > 0 && node != null && processor != null)
		{
			try
			{
				XPath xpath = new ParseNodeXPath(expression);
				Object list = xpath.evaluate(node);
	
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
