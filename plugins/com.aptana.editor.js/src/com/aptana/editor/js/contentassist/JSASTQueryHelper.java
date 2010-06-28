package com.aptana.editor.js.contentassist;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	
	private static XPath ALL_VARS;
	private static XPath ANCESTOR_FUNCTIONS;
	private static XPath NAMED_FUNCTIONS;
	private static XPath NON_FUNCTION_VARS;
	private static XPath VAR_FUNCTIONS;

	/**
	 * static initializer
	 */
	static
	{
		try
		{
			// symbol queries
			ALL_VARS = new ParseNodeXPath("descendant::var/declaration/identifier[position() = 1]"); //$NON-NLS-1$
			ANCESTOR_FUNCTIONS = new ParseNodeXPath("ancestor::function"); //$NON-NLS-1$
			
			// child queries
			NAMED_FUNCTIONS = new ParseNodeXPath("./function[string-length(@name) > 0]"); //$NON-NLS-1$
			NON_FUNCTION_VARS = new ParseNodeXPath("./var/declaration/identifier[position() = 1 and count(following-sibling::function) = 0]"); //$NON-NLS-1$
			VAR_FUNCTIONS = new ParseNodeXPath("./var/declaration/identifier[count(following-sibling::function) > 0]"); //$NON-NLS-1$
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
					
					// add args
					for (IParseNode arg : function.getArgs())
					{
						result.put(arg.toString(), Classification.PROPERTY);
					}
					
					// add all descendant vars not contained by descendant functions
					processXPath(
						ALL_VARS,
						function.getBody(),
						new ItemProcessor() {
							public void process(Object item)
							{
								IParseNode node = (IParseNode) item;
								IParseNode nextSibling = node.getParent().getChild(1);
								IParseNode parent = node.getParent();
								
								while (parent != null)
								{
									if (parent.getNodeType() == JSNodeTypes.FUNCTION)
									{
										if (parent == function)
										{
											Classification c = (nextSibling.getNodeType() == JSNodeTypes.FUNCTION) ? Classification.FUNCTION : Classification.PROPERTY;
											
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
				}
			}
		);
		
		return result;
	}
	
	/**
	 * getChildVarNonFunctions
	 * 
	 * @param ast
	 * @return
	 */
	public List<String> getChildVarNonFunctions(IParseNode ast)
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
	 * getChildFunctions
	 * 
	 * @param ast
	 * @return
	 */
	public List<String> getChildFunctions(IParseNode ast)
	{
		List<String> result = this.getChildNamedFunctions(ast);

		result.addAll(this.getChildVarFunctions(ast));

		return result;
	}

	/**
	 * getChildNamedFunctions
	 * 
	 * @param ast
	 * @return
	 */
	public List<String> getChildNamedFunctions(IParseNode ast)
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
	 * getChildVarFunctions
	 * 
	 * @param ast
	 * @return
	 */
	public List<String> getChildVarFunctions(IParseNode ast)
	{
		final List<String> result = new LinkedList<String>();
		
		this.processXPath(
			VAR_FUNCTIONS,
			ast,
			new ItemProcessor() {
				public void process(Object item)
				{
					result.add(item.toString());
					
					IParseNode identifier = (IParseNode) item;
					JSFunctionNode function = (JSFunctionNode) identifier.getParent().getChild(1);
					String name = function.getName();
					
					if (name != null && name.length() > 0)
					{
						result.add(name);
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
