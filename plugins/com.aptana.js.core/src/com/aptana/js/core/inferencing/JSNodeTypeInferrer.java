/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.inferencing;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.core.util.URIUtil;
import com.aptana.index.core.Index;
import com.aptana.js.core.JSLanguageConstants;
import com.aptana.js.core.JSTypeConstants;
import com.aptana.js.core.index.JSIndexQueryHelper;
import com.aptana.js.core.model.FunctionElement;
import com.aptana.js.core.model.PropertyElement;
import com.aptana.js.core.parsing.JSTokenType;
import com.aptana.js.core.parsing.ast.IJSNodeTypes;
import com.aptana.js.core.parsing.ast.JSArgumentsNode;
import com.aptana.js.core.parsing.ast.JSArrayNode;
import com.aptana.js.core.parsing.ast.JSAssignmentNode;
import com.aptana.js.core.parsing.ast.JSBinaryArithmeticOperatorNode;
import com.aptana.js.core.parsing.ast.JSBinaryBooleanOperatorNode;
import com.aptana.js.core.parsing.ast.JSConditionalNode;
import com.aptana.js.core.parsing.ast.JSConstructNode;
import com.aptana.js.core.parsing.ast.JSFalseNode;
import com.aptana.js.core.parsing.ast.JSFunctionNode;
import com.aptana.js.core.parsing.ast.JSGetElementNode;
import com.aptana.js.core.parsing.ast.JSGetPropertyNode;
import com.aptana.js.core.parsing.ast.JSGroupNode;
import com.aptana.js.core.parsing.ast.JSIdentifierNode;
import com.aptana.js.core.parsing.ast.JSInvokeNode;
import com.aptana.js.core.parsing.ast.JSNode;
import com.aptana.js.core.parsing.ast.JSNumberNode;
import com.aptana.js.core.parsing.ast.JSObjectNode;
import com.aptana.js.core.parsing.ast.JSPostUnaryOperatorNode;
import com.aptana.js.core.parsing.ast.JSPreUnaryOperatorNode;
import com.aptana.js.core.parsing.ast.JSRegexNode;
import com.aptana.js.core.parsing.ast.JSReturnNode;
import com.aptana.js.core.parsing.ast.JSStringNode;
import com.aptana.js.core.parsing.ast.JSTreeWalker;
import com.aptana.js.core.parsing.ast.JSTrueNode;
import com.aptana.js.internal.core.inferencing.JSPropertyCollector;
import com.aptana.js.internal.core.inferencing.JSSymbolTypeInferrer;
import com.aptana.js.internal.core.parsing.sdoc.model.DocumentationBlock;
import com.aptana.js.internal.core.parsing.sdoc.model.ParamTag;
import com.aptana.js.internal.core.parsing.sdoc.model.Tag;
import com.aptana.js.internal.core.parsing.sdoc.model.TagType;
import com.aptana.js.internal.core.parsing.sdoc.model.Type;
import com.aptana.parsing.ast.IParseNode;

public class JSNodeTypeInferrer extends JSTreeWalker
{
	private JSScope _scope;
	private Index _index;
	private URI _location;
	private List<String> _types;
	private JSIndexQueryHelper _queryHelper;
	/**
	 * A monitor we use mostly to monitor cancellation, but also to report progress (though it's on an unknown/number of
	 * units!)
	 */
	private SubMonitor _monitor;

	/**
	 * @param scope
	 * @param projectIndex
	 *            The current index we're dealing with.
	 * @param location
	 *            The current file/location we're dealing with.
	 * @param queryHelper
	 *            the query helper. The object we can ask for types/functions/etc. Contains knowledge of the build
	 *            paths.
	 */
	public JSNodeTypeInferrer(JSScope scope, Index projectIndex, URI location, JSIndexQueryHelper queryHelper)
	{
		this(scope, projectIndex, location, queryHelper, new NullProgressMonitor());
	}

	public JSNodeTypeInferrer(JSScope scope, Index projectIndex, URI location, JSIndexQueryHelper queryHelper,
			IProgressMonitor monitor)
	{
		this._scope = scope;
		this._index = projectIndex;
		this._location = location;
		this._queryHelper = queryHelper;
		this._monitor = SubMonitor.convert(monitor, IProgressMonitor.UNKNOWN);
	}

	/**
	 * addParameterTypes
	 * 
	 * @param identifierNode
	 */
	private void addParameterTypes(JSIdentifierNode identifierNode)
	{
		IParseNode parent = identifierNode.getParent();
		IParseNode grandparent = (parent != null) ? parent.getParent() : null;
		boolean foundType = false;

		if (grandparent != null && grandparent.getNodeType() == IJSNodeTypes.FUNCTION)
		{
			DocumentationBlock docs = ((JSNode) grandparent).getDocumentation();

			if (docs != null)
			{
				String name = identifierNode.getText();
				int index = identifierNode.getIndex();
				List<Tag> params = docs.getTags(TagType.PARAM);

				if (params != null && index < params.size())
				{
					ParamTag param = (ParamTag) params.get(index);

					if (name.equals(param.getName()))
					{
						for (Type parameterType : param.getTypes())
						{
							String type = parameterType.getName();

							// Fix up type names as might be necessary
							type = JSTypeMapper.getInstance().getMappedType(type);

							this.addType(type);
							foundType = true;
						}
					}
				}
			}
		}

		// Use "Object" as parameter type if we didn't find types by other
		// means
		if (!foundType)
		{
			this.addType(JSTypeConstants.DEFAULT_PARAMETER_TYPE);
		}
	}

	/**
	 * addType
	 * 
	 * @param type
	 */
	public void addType(String type)
	{
		if (type != null && type.length() > 0)
		{
			if (this._types == null)
			{
				this._types = new ArrayList<String>();
			}
			type = JSTypeUtil.validateTypeName(type);
			if (!this._types.contains(type))
			{
				this._types.add(type);
			}
		}
	}

	/**
	 * addTypes
	 * 
	 * @param node
	 */
	protected void addTypes(IParseNode node)
	{
		if (node instanceof JSNode)
		{
			((JSNode) node).accept(this);
		}
	}

	/**
	 * addTypes
	 * 
	 * @param types
	 */
	protected void addTypes(List<String> types)
	{
		if (types != null)
		{
			for (String type : types)
			{
				this.addType(type);
			}
		}
	}

	/**
	 * getActiveScope
	 * 
	 * @param offset
	 * @return
	 */
	protected JSScope getActiveScope(int offset)
	{
		if (this._scope == null)
		{
			return null;
		}
		// find the global scope
		JSScope root = this._scope;

		while (true)
		{
			JSScope candidate = root.getParentScope();

			if (candidate == null)
			{
				break;
			}
			root = candidate;
		}

		// find scope containing the specified offset
		return root.getScopeAtOffset(offset);
	}

	/**
	 * getTypes
	 * 
	 * @return
	 */
	public List<String> getTypes()
	{
		return CollectionsUtil.getListValue(_types);
	}

	/**
	 * getTypes
	 * 
	 * @param node
	 * @return
	 */
	public List<String> getTypes(IParseNode node)
	{
		return this.getTypes(node, this._scope);
	}

	/**
	 * getTypes
	 * 
	 * @param node
	 * @return
	 */
	public List<String> getTypes(IParseNode node, JSScope scope)
	{
		if (node instanceof JSNode)
		{
			// create new nested walker
			// FIXME How can we avoid total recursion here? can we re-use ourself somehow?
			JSNodeTypeInferrer walker = new JSNodeTypeInferrer(scope, this._index, this._location, this._queryHelper,
					_monitor.newChild(1));

			// collect types
			walker.visit((JSNode) node);

			// return collected types
			return walker.getTypes();
		}

		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSArrayNode)
	 */
	@Override
	public void visit(JSArrayNode node)
	{
		checkCancellation();

		if (!node.hasChildren())
		{
			this.addType(JSTypeConstants.ARRAY_TYPE);
		}
		else
		{
			// TODO: Add all element types?
			// TODO: Create equivalent of "structure" type if element types vary?
			for (String type : this.getTypes(node.getFirstChild()))
			{
				this.addType(JSTypeUtil.createGenericArrayType(type));
			}
		}

		_monitor.worked(1);
	}

	private void checkCancellation()
	{
		if (_monitor.isCanceled())
		{
			throw new OperationCanceledException();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSAssignmentNode)
	 */
	@Override
	public void visit(JSAssignmentNode node)
	{
		checkCancellation();

		switch (node.getNodeType())
		{
			case IJSNodeTypes.ASSIGN:
				this.addTypes(node.getRightHandSide());
				break;

			case IJSNodeTypes.ADD_AND_ASSIGN:
				String type = JSTypeConstants.NUMBER_TYPE;
				List<String> lhsTypes = this.getTypes(node.getLeftHandSide());
				List<String> rhsTypes = this.getTypes(node.getRightHandSide());

				if (lhsTypes.contains(JSTypeConstants.STRING_TYPE) || rhsTypes.contains(JSTypeConstants.STRING_TYPE))
				{
					type = JSTypeConstants.STRING_TYPE;
				}

				this.addType(type);
				break;

			default:
				this.addType(JSTypeConstants.DEFAULT_ASSIGNMENT_TYPE);
				break;
		}

		_monitor.worked(1);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSArithmeticOperatorNode)
	 */
	@Override
	public void visit(JSBinaryArithmeticOperatorNode node)
	{
		checkCancellation();

		String type = JSTypeConstants.NUMBER_TYPE;
		if (node.getNodeType() == IJSNodeTypes.ADD)
		{
			IParseNode lhs = node.getLeftHandSide();
			IParseNode rhs = node.getRightHandSide();

			// NOTE: Iterate down the tree until we find the first non-addition node or the first string
			while (lhs.getNodeType() == IJSNodeTypes.ADD)
			{
				rhs = lhs.getLastChild();
				lhs = lhs.getFirstChild();

				if (rhs instanceof JSStringNode)
				{
					break;
				}
			}

			if (lhs instanceof JSStringNode || rhs instanceof JSStringNode)
			{
				type = JSTypeConstants.STRING_TYPE;
			}
			else
			{
				List<String> lhsTypes = this.getTypes(lhs);
				List<String> rhsTypes = this.getTypes(rhs);

				if (lhsTypes.contains(JSTypeConstants.STRING_TYPE) || rhsTypes.contains(JSTypeConstants.STRING_TYPE))
				{
					type = JSTypeConstants.STRING_TYPE;
				}
			}
		}
		this.addType(type);

		_monitor.worked(1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSBooleanOperatorNode)
	 */
	@Override
	public void visit(JSBinaryBooleanOperatorNode node)
	{
		checkCancellation();

		JSTokenType token = JSTokenType.get((String) node.getOperator().value);
		switch (token)
		{
			case AMPERSAND_AMPERSAND:
			case PIPE_PIPE:
				this.addTypes(node.getLeftHandSide());
				this.addTypes(node.getRightHandSide());
				break;

			default:
				this.addType(JSTypeConstants.BOOLEAN_TYPE);
		}

		_monitor.worked(1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSConditionalNode)
	 */
	@Override
	public void visit(JSConditionalNode node)
	{
		checkCancellation();

		this.addTypes(node.getTrueExpression());
		this.addTypes(node.getFalseExpression());

		_monitor.worked(1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSConstructNode)
	 */
	@Override
	public void visit(JSConstructNode node)
	{
		checkCancellation();

		IParseNode child = node.getExpression();
		if (child instanceof JSNode)
		{
			List<String> types = this.getTypes(child);
			List<String> returnTypes = new ArrayList<String>(types.size());

			for (String typeName : types)
			{
				if (typeName.startsWith(JSTypeConstants.GENERIC_CLASS_OPEN))
				{
					returnTypes.add(JSTypeUtil.getClassType(typeName));
				}
				else if (typeName.startsWith(JSTypeConstants.GENERIC_FUNCTION_OPEN))
				{
					returnTypes.addAll(JSTypeUtil.getFunctionSignatureReturnTypeNames(typeName));
				}
				else
				{
					returnTypes.add(typeName);
				}
			}

			for (String typeName : returnTypes)
			{
				this.addType(typeName);
			}
		}

		_monitor.worked(1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSFalseNode)
	 */
	@Override
	public void visit(JSFalseNode node)
	{
		checkCancellation();
		this.addType(JSTypeConstants.BOOLEAN_TYPE);
		_monitor.worked(1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSFunctionNode)
	 */
	@Override
	public void visit(JSFunctionNode node)
	{
		checkCancellation();

		List<String> types = new ArrayList<String>();
		JSScope scope = this.getActiveScope(node.getBody().getStartingOffset());
		boolean foundReturnExpression = false;

		// infer return types
		for (JSReturnNode returnValue : node.getReturnNodes())
		{
			IParseNode expression = returnValue.getExpression();

			if (!expression.isEmpty())
			{
				foundReturnExpression = true;

				types.addAll(this.getTypes(expression, scope));
			}
		}

		// If we couldn't infer a return type and we had a return
		// expression, then have it return Object
		if (foundReturnExpression && types.isEmpty())
		{
			types.add(JSTypeConstants.OBJECT_TYPE);
		}

		// build function type, including return values
		String type = JSTypeUtil.toFunctionType(types);
		this.addType(type);

		_monitor.worked(1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSGetElementNode)
	 */
	@Override
	public void visit(JSGetElementNode node)
	{
		checkCancellation();

		// TODO: Should check subscript to determine if the type is a Number or
		// a String. If it is a String, then this should behave like get-property
		// assuming we can retrieve a literal string.
		IParseNode lhs = node.getLeftHandSide();
		if (lhs instanceof JSNode)
		{
			for (String typeName : this.getTypes(lhs))
			{
				String typeString = JSTypeUtil.getArrayElementType(typeName);

				if (typeString != null)
				{
					this.addType(typeString);
				}
				else
				{
					this.addType(JSTypeConstants.OBJECT_TYPE);
				}
			}
		}

		_monitor.worked(1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSGetPropertyNode)
	 */
	@Override
	public void visit(JSGetPropertyNode node)
	{
		checkCancellation();

		IParseNode lhs = node.getLeftHandSide();
		if (lhs instanceof JSNode)
		{
			IParseNode rhs = node.getRightHandSide();
			String memberName = rhs.getText();

			for (String typeName : this.getTypes(lhs))
			{
				// Fix up type names as might be necessary
				typeName = JSTypeMapper.getInstance().getMappedType(typeName);
				// TODO Combine with similar code from ParseUtil.getParentObjectTypes
				if (JSTypeConstants.FUNCTION_JQUERY.equals(typeName)
						&& lhs instanceof JSIdentifierNode
						&& (JSTypeConstants.DOLLAR.equals(lhs.getText()) || JSTypeConstants.JQUERY
								.equals(lhs.getText())))
				{
					typeName = JSTypeConstants.CLASS_JQUERY;
				}

				// lookup up rhs name in type and add that value's type here
				Collection<PropertyElement> properties = this._queryHelper.getTypeMembers(typeName, memberName);

				if (properties != null)
				{
					for (PropertyElement property : properties)
					{
						if (property instanceof FunctionElement)
						{
							FunctionElement function = (FunctionElement) property;
							for (String type : function.getSignatureTypes())
							{
								this.addType(type);
							}
						}
						else
						{
							for (String type : property.getTypeNames())
							{
								this.addType(type);
							}
						}
					}
				}
			}
		}

		_monitor.worked(1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSGroupNode)
	 */
	@Override
	public void visit(JSGroupNode node)
	{
		checkCancellation();

		IParseNode expression = node.getExpression();
		if (expression instanceof JSNode)
		{
			((JSNode) expression).accept(this);
		}

		_monitor.worked(1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSIdentifierNode)
	 */
	@Override
	public void visit(JSIdentifierNode node)
	{
		checkCancellation();

		String name = node.getText();
		Collection<PropertyElement> properties = null;

		if (this._scope != null && this._scope.hasSymbol(name))
		{
			IParseNode parent = node.getParent();

			if (parent != null && parent.getNodeType() == IJSNodeTypes.PARAMETERS)
			{
				// special handling of parameters to potentially get the type
				// from documentation and to prevent an infinite loop since
				// parameters point to themselves in the symbol table
				this.addParameterTypes(node);
			}
			else
			{
				// Check the local scope for type first
				JSSymbolTypeInferrer symbolInferrer = new JSSymbolTypeInferrer(this._scope, this._index,
						this._location, this._queryHelper);
				PropertyElement property = symbolInferrer.getSymbolPropertyElement(name, _monitor.newChild(1));
				if (property != null)
				{
					// We found a match in the local scope
					properties = CollectionsUtil.newList(property);
				}
				else
				{
					// No match in the local scope, query the globals in index
					properties = this._queryHelper.getGlobals(getFileName(), name);
				}
			}
		}
		else
		{
			// Scope says it doesn't has a symbol with that name, so query the globals in index
			properties = this._queryHelper.getGlobals(getFileName(), name);
		}

		// Hopefully we found at least one match...
		if (properties != null)
		{
			for (PropertyElement property : properties)
			{
				if (property instanceof FunctionElement)
				{
					FunctionElement function = (FunctionElement) property;
					for (String type : function.getSignatureTypes())
					{
						this.addType(type);
					}
				}
				else
				{
					for (String type : property.getTypeNames())
					{
						this.addType(type);
					}
				}
			}
		}

		_monitor.worked(1);
	}

	protected String getFileName()
	{
		return URIUtil.getFileName(_location);
	}

	protected IProject getProject()
	{
		URI root = _index.getRoot();
		IPath containerPath = org.eclipse.core.filesystem.URIUtil.toPath(root);
		if (containerPath == null)
		{
			return null;
		}
		IContainer container = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(containerPath);
		if (container == null)
		{
			return null;
		}
		return container.getProject();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSInvokeNode)
	 */
	@Override
	public void visit(JSInvokeNode node)
	{
		checkCancellation();

		IParseNode child = node.getExpression();
		if (child instanceof JSNode)
		{
			if (child instanceof JSIdentifierNode && JSLanguageConstants.REQUIRE.equals(child.getNameNode().getName()))
			{
				// it's a requires!
				JSArgumentsNode args = (JSArgumentsNode) node.getArguments();
				IParseNode[] children = args.getChildren();
				for (IParseNode arg : children)
				{
					String moduleId = CommonJSResolver.getModuleId(arg);
					if (!StringUtil.isEmpty(moduleId))
					{
						IPath absolutePath = resolve(moduleId);
						String typeName = _queryHelper.getModuleType(absolutePath);
						if (typeName != null)
						{
							this.addType(typeName);
							return;
						}
					}
				}
			}

			List<String> types = this.getTypes(child);

			// NOTE: This is a special case for functions used as a RHS of assignments or as part of a property chain.
			// If the invocation returns undefined, we change that to Object.
			// TODO: As a refinement, we want to check that the function being called is not defined in the current
			// scope
			if (types.isEmpty())
			{
				IParseNode parent = node.getParent();

				if (parent != null)
				{
					switch (parent.getNodeType())
					{
						case IJSNodeTypes.ASSIGN:
							if (node.getIndex() == 1)
							{
								this.addType(JSTypeConstants.OBJECT_TYPE);
							}
							break;

						case IJSNodeTypes.GET_PROPERTY:
							this.addType(JSTypeConstants.OBJECT_TYPE);
							break;

						default:
							break;
					}
				}
			}

			for (String typeName : types)
			{
				if (JSTypeUtil.isFunctionPrefix(typeName))
				{
					List<String> returnTypes = JSTypeUtil.getFunctionSignatureReturnTypeNames(typeName);
					for (String returnTypeName : returnTypes)
					{
						this.addType(returnTypeName);
					}
				}
			}
		}

		_monitor.worked(1);
	}

	protected IPath resolve(String moduleId)
	{
		return RequireResolverFactory.resolve(moduleId, getProject(), Path.fromPortableString(_location.getPath())
				.removeLastSegments(1), Path.fromPortableString(_index.getRoot().getPath()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSNumberNode)
	 */
	@Override
	public void visit(JSNumberNode node)
	{
		checkCancellation();
		this.addType(JSTypeConstants.NUMBER_TYPE);
		_monitor.worked(1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSObjectNode)
	 */
	@Override
	public void visit(JSObjectNode node)
	{
		checkCancellation();

		if (node.hasChildren())
		{
			// collect all descendants into a property collection
			JSPropertyCollection symbol = new JSPropertyCollection();
			JSPropertyCollector collector = new JSPropertyCollector(symbol);

			collector.visit(node);
			symbol.addValue(node);

			JSSymbolTypeInferrer inferrer = new JSSymbolTypeInferrer(this._scope, this._index, this._location,
					this._queryHelper);
			Set<String> types = new LinkedHashSet<String>();

			inferrer.processProperties(symbol, types, _monitor.newChild(1));

			this.addTypes(new ArrayList<String>(types));
		}
		else
		{
			this.addType(JSTypeConstants.OBJECT_TYPE);
		}

		_monitor.worked(1);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSPostUnaryOperatorNode)
	 */
	@Override
	public void visit(JSPostUnaryOperatorNode node)
	{
		checkCancellation();
		this.addType(JSTypeConstants.NUMBER_TYPE);
		_monitor.worked(1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSPreUnaryOperatorNode)
	 */
	@Override
	public void visit(JSPreUnaryOperatorNode node)
	{
		checkCancellation();

		switch (node.getNodeType())
		{
			case IJSNodeTypes.DELETE:
			case IJSNodeTypes.LOGICAL_NOT:
				this.addType(JSTypeConstants.BOOLEAN_TYPE);
				break;

			case IJSNodeTypes.TYPEOF:
				this.addType(JSTypeConstants.STRING_TYPE);
				break;

			case IJSNodeTypes.VOID:
				// technically this returns 'undefined', but we return nothing
				// for both types
				break;

			default:
				this.addType(JSTypeConstants.NUMBER_TYPE);
				break;
		}

		_monitor.worked(1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSRegexNode)
	 */
	@Override
	public void visit(JSRegexNode node)
	{
		checkCancellation();
		this.addType(JSTypeConstants.REG_EXP_TYPE);
		_monitor.worked(1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSStringNode)
	 */
	@Override
	public void visit(JSStringNode node)
	{
		checkCancellation();
		this.addType(JSTypeConstants.STRING_TYPE);
		_monitor.worked(1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSTrueNode)
	 */
	@Override
	public void visit(JSTrueNode node)
	{
		checkCancellation();
		this.addType(JSTypeConstants.BOOLEAN_TYPE);
		_monitor.worked(1);
	}
}
