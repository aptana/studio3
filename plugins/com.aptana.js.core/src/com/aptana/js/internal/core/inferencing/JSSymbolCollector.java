// $codepro.audit.disable nonCaseLabelInSwitch
/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.inferencing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.js.core.inferencing.IInvocationProcessor;
import com.aptana.js.core.inferencing.JSPropertyCollection;
import com.aptana.js.core.inferencing.JSScope;
import com.aptana.js.core.inferencing.JSTypeUtil;
import com.aptana.js.core.parsing.ast.IJSNodeTypes;
import com.aptana.js.core.parsing.ast.JSArgumentsNode;
import com.aptana.js.core.parsing.ast.JSAssignmentNode;
import com.aptana.js.core.parsing.ast.JSCatchNode;
import com.aptana.js.core.parsing.ast.JSConstructNode;
import com.aptana.js.core.parsing.ast.JSDeclarationNode;
import com.aptana.js.core.parsing.ast.JSFunctionNode;
import com.aptana.js.core.parsing.ast.JSGetElementNode;
import com.aptana.js.core.parsing.ast.JSGetPropertyNode;
import com.aptana.js.core.parsing.ast.JSGroupNode;
import com.aptana.js.core.parsing.ast.JSInvokeNode;
import com.aptana.js.core.parsing.ast.JSLabelledNode;
import com.aptana.js.core.parsing.ast.JSNode;
import com.aptana.js.core.parsing.ast.JSObjectNode;
import com.aptana.js.core.parsing.ast.JSParseRootNode;
import com.aptana.js.core.parsing.ast.JSTreeWalker;
import com.aptana.js.core.parsing.ast.JSWithNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.IRange;

public class JSSymbolCollector extends JSTreeWalker
{
	private static List<IInvocationProcessor> PROCESSORS;
	private static Map<String, IInvocationProcessor> INVOCATION_PROCESSORS;

	private JSScope _scope;

	/**
	 * getInvocationProcessor
	 * 
	 * @return
	 */
	private static IInvocationProcessor getInvocationProcessor(String pattern)
	{
		if (INVOCATION_PROCESSORS == null)
		{
			INVOCATION_PROCESSORS = new HashMap<String, IInvocationProcessor>();

			for (IInvocationProcessor processor : getInvocationProcessors())
			{
				for (String invocationPattern : processor.getInvocationPatterns())
				{
					INVOCATION_PROCESSORS.put(invocationPattern, processor);
				}
			}
		}

		return INVOCATION_PROCESSORS.get(pattern);
	}

	/**
	 * getInvocationProcessors
	 * 
	 * @return
	 */
	private static List<IInvocationProcessor> getInvocationProcessors()
	{
		if (PROCESSORS == null)
		{
			PROCESSORS = new ArrayList<IInvocationProcessor>();

			// TODO: Eventually, this will be handled via an extension point.
			// We're targeting jQuery only right now, so we'll hard code this.
			PROCESSORS.add(new JQueryInvocationProcessor());
		}

		return PROCESSORS;
	}

	/**
	 * JSSymbolCollector
	 */
	public JSSymbolCollector()
	{
		this._scope = new JSScope();
	}

	/**
	 * accept
	 * 
	 * @param node
	 */
	protected void accept(IParseNode node)
	{
		if (node instanceof JSNode)
		{
			((JSNode) node).accept(this);
		}
	}

	/**
	 * addPropertyValue
	 * 
	 * @param name
	 * @param value
	 */
	public void addPropertyValue(String name, JSNode value)
	{
		if (!StringUtil.isEmpty(name) && value != null)
		{
			JSPropertyCollection object = this._scope.getObject();
			JSPropertyCollection property;

			if (object.hasProperty(name))
			{
				// use the currently existing property
				property = object.getProperty(name);
			}
			else
			{
				if (this._scope.hasSymbol(name))
				{
					property = this._scope.getSymbol(name);
				}
				else
				{
					// get global
					JSScope scope = this._scope;

					if (isPotentialGlobalValue(value))
					{
						while (scope.getParentScope() != null)
						{
							scope = scope.getParentScope();
						}
					}

					// create a new property
					property = new JSPropertyCollection();

					// add it to the current object
					scope.getObject().setProperty(name, property);
				}
			}

			if (value instanceof JSObjectNode)
			{
				JSPropertyCollector collector = new JSPropertyCollector(property);
				collector.visit((JSObjectNode) value);
			}

			property.addValue(value);
		}
	}

	private boolean isPotentialGlobalValue(JSNode node)
	{
		boolean result = true;

		if (node != null)
		{
			if (node.getNodeType() == IJSNodeTypes.FUNCTION)
			{
				result = false;
			}
			else
			{
				// check parent type
				IParseNode parent = node.getParent();
				int nodeType = (parent != null) ? parent.getNodeType() : -1;

				switch (nodeType)
				{
					case IJSNodeTypes.DECLARATION:
					case IJSNodeTypes.PARAMETERS:
						result = false;
						break;
				}
			}
		}

		return result;
	}

	/**
	 * getScope
	 * 
	 * @return Scope<JSNode>
	 */
	public JSScope getScope()
	{
		return this._scope;
	}

	/**
	 * popScope
	 */
	protected void popScope()
	{
		if (this._scope != null)
		{
			this._scope = this._scope.getParentScope();
		}
	}

	/**
	 * pushScope
	 */
	protected void pushScope()
	{
		JSScope childScope = new JSScope();

		if (this._scope != null)
		{
			this._scope.addScope(childScope);
		}

		this._scope = childScope;
	}

	/**
	 * setScopeRange
	 * 
	 * @param range
	 */
	protected void setScopeRange(IRange range)
	{
		if (this._scope != null)
		{
			this._scope.setRange(range);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSAssignmentNode)
	 */
	@Override
	public void visit(JSAssignmentNode node)
	{
		IParseNode lhs = node.getLeftHandSide();

		switch (lhs.getNodeType())
		{
			case IJSNodeTypes.IDENTIFIER:
				this.addPropertyValue(lhs.getText(), node);
				break;

			default:
				LOOP: while (lhs != null)
				{
					switch (lhs.getNodeType())
					{
						case IJSNodeTypes.IDENTIFIER:
							JSPropertyCollector collector = new JSPropertyCollector(this._scope.getObject());
							collector.visit(node);
							break LOOP;

						case IJSNodeTypes.THIS:
							// Get the surrounding function's "property" to hang it's own properties off of
							JSPropertyCollection parent = getSurroundingProperty(node);
							if (parent != null)
							{
								// in cases where we're handling an assign whose lhs is a GetElement, it's adding the
								// name of the root property after "this" to itself, causing a bad hierarchy.
								// i.e. this.apis['customerevent'] ended up adding 'apis' as a property on 'apis', then
								// adding 'customerevent' on the grandchild 'apis' property.
								if (node.getFirstChild() instanceof JSGetElementNode)
								{
									parent = parent.getParentProperty();
								}
								if (parent != null)
								{
									collector = new JSPropertyCollector(parent);
									collector.visit(node);
								}
							}
							break LOOP;

						default:
							lhs = lhs.getFirstChild();
							break;
					}
				}

				// TODO: unhandled assignment
				break;
		}

		// process rhs
		IParseNode rhs = node.getRightHandSide();

		if (rhs != null)
		{
			this.accept(rhs);
		}
	}

	private JSPropertyCollection getSurroundingProperty(JSNode node)
	{
		// Here we'll cheat to get the fully qualified name of the property we want to add
		String fullyQualifiedName = JSTypeUtil.getName(node);
		if (StringUtil.isEmpty(fullyQualifiedName))
		{
			return null;
		}
		// Now let's split it into it's parts
		List<String> names = StringUtil.split(fullyQualifiedName, '.');
		names.remove(names.size() - 1); // drop the last segment, which is the property we plan to add and get the path
										// to it's parent
		if (CollectionsUtil.isEmpty(names))
		{
			return null;
		}

		// Ok traverse the property chain get what "this" refers to so we can hang properties off of it!
		String firstName = names.remove(0);
		JSPropertyCollection collection = getScope().getSymbol(firstName);
		if (!names.isEmpty())
		{
			for (String name : names)
			{
				if (collection == null)
				{
					return null;
				}
				collection = collection.getProperty(name);
			}
		}
		return collection;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSCatchNode)
	 */
	@Override
	public void visit(JSCatchNode node)
	{
		IParseNode body = node.getBody();

		this.accept(body);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSDeclarationNode)
	 */
	@Override
	public void visit(JSDeclarationNode node)
	{
		String name = node.getIdentifier().getText();
		IParseNode value = node.getValue();

		if (value instanceof JSNode)
		{
			this.addPropertyValue(name, (JSNode) value);
		}

		// process any complex data structures from this assignment
		this.accept(value);
	}

	private String getFunctionName(JSFunctionNode funcNode)
	{
		// normal named function?
		String name = funcNode.getName().getText();
		if (!StringUtil.isEmpty(name))
		{
			return name;
		}

		// The name may be empty, thus it's an anonymous function being invoked (suing new or self-invoking) and
		// assigned to some variable/identifier/property. We need _that_ name
		if (funcNode.getParent() instanceof JSConstructNode || funcNode.getParent() instanceof JSInvokeNode)
		{
			IParseNode possibleDecl = funcNode.getParent().getParent();
			// being assigned to a var
			if (possibleDecl instanceof JSDeclarationNode)
			{
				JSDeclarationNode declNode = (JSDeclarationNode) possibleDecl;
				return declNode.getIdentifier().getText();
			}
			// This is being assigned to some property of an enclosing object. Get the name of
			// the property we're assigning it to
			else if (possibleDecl instanceof JSAssignmentNode)
			{
				JSAssignmentNode assign = (JSAssignmentNode) possibleDecl;
				IParseNode left = assign.getChild(0);
				if (left instanceof JSGetPropertyNode)
				{
					// FIXME What if we're using this sort of accessor: this['propName'] ?
					JSGetPropertyNode getProp = (JSGetPropertyNode) left;
					return getProp.getChild(1).getText();
				}
			}
		}

		// We weren't able to figure out the name. Hopefully this is an entirely anonymous function
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSFunctionNode)
	 */
	@Override
	public void visit(JSFunctionNode node)
	{
		// add symbol if this has a name
		String name = node.getName().getText();
		if (!StringUtil.isEmpty(name))
		{
			this.addPropertyValue(name, node);
		}

		// create a new scope and set its range
		IParseNode body = node.getBody();
		this.pushScope();
		this.setScopeRange(body);

		// add parameters
		boolean processed = false;
		IParseNode params = node.getParameters();

		if (node.getParent() instanceof JSGroupNode)
		{
			IParseNode args = node.getParent().getParent().getLastChild();

			if (args instanceof JSArgumentsNode && params.getChildCount() == args.getChildCount())
			{
				for (int i = 0; i < params.getChildCount(); i++)
				{
					IParseNode identifier = params.getChild(i);
					IParseNode value = args.getChild(i);

					if (value instanceof JSNode)
					{
						this.addPropertyValue(identifier.getText(), (JSNode) value);
					}
				}

				processed = true;
			}
		}

		// default behavior
		if (!processed)
		{
			for (IParseNode parameter : node.getParameters())
			{
				if (parameter instanceof JSNode)
				{
					this.addPropertyValue(parameter.getText(), (JSNode) parameter);
				}
			}
		}

		// process body
		this.accept(body);

		// restore original scope
		this.popScope();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSGetPropertyNode)
	 */
	@Override
	public void visit(JSGetPropertyNode node)
	{
		// No need to process the rhs since it's always an identifier
		this.accept(node.getLeftHandSide());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSInvokeNode)
	 */
	@Override
	public void visit(JSInvokeNode node)
	{
		IParseNode expression = node.getExpression();
		boolean processed = false;

		// NOTE: limiting to dotted names for efficiency
		if (expression instanceof JSGetPropertyNode)
		{
			IInvocationProcessor processor = getInvocationProcessor(expression.toString());

			if (processor != null)
			{
				processed = processor.processInvocation(this._scope, node);
			}
		}

		if (!processed)
		{
			super.visit(node);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSLabelledNode)
	 */
	@Override
	public void visit(JSLabelledNode node)
	{
		// No need to process the label since it's always an identifier
		this.accept(node.getBlock());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSParseRootNode)
	 */
	@Override
	public void visit(JSParseRootNode node)
	{
		// set scope range
		this.setScopeRange(node);

		// process children
		for (IParseNode child : node)
		{
			this.accept(child);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSWithNode)
	 */
	@Override
	public void visit(JSWithNode node)
	{
		// TODO: This does "interesting" things to the current scope. We need to make sure we understand all cases
		// before implementing this
	}
}
