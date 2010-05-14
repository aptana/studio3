package com.aptana.editor.js.contentassist;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
	 * getSymbolsInScope
	 * 
	 * @param ast
	 * @return
	 */
	public List<String> getSymbolsInScope(IParseNode ast)
	{
		final List<String> result = new LinkedList<String>();
		
		this.processXPath(
			"ancestor::function",
			ast,
			new ItemProcessor() {
				public void process(Object item)
				{
					if (item instanceof JSFunctionNode)
					{
						JSFunctionNode function = (JSFunctionNode) item;
						
						result.addAll(Arrays.asList(function.getArgNames()));
					}
				}
			}
		);
		
		return result;
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

		this.processXPath(
			"/var/declaration/identifier[position() = 1 and count(following-sibling::function) = 0]",
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
