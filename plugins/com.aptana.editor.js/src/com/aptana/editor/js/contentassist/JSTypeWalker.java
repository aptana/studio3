package com.aptana.editor.js.contentassist;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.contentassist.UserAgentManager;
import com.aptana.editor.common.contentassist.UserAgentManager.UserAgent;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.index.JSIndexWriter;
import com.aptana.editor.js.contentassist.model.BaseElement;
import com.aptana.editor.js.contentassist.model.ContentSelector;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.ParameterElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.ReturnTypeElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.editor.js.contentassist.model.UserAgentElement;
import com.aptana.editor.js.parsing.ast.JSArrayNode;
import com.aptana.editor.js.parsing.ast.JSAssignmentNode;
import com.aptana.editor.js.parsing.ast.JSBinaryArithmeticOperatorNode;
import com.aptana.editor.js.parsing.ast.JSBinaryBooleanOperatorNode;
import com.aptana.editor.js.parsing.ast.JSConditionalNode;
import com.aptana.editor.js.parsing.ast.JSConstructNode;
import com.aptana.editor.js.parsing.ast.JSFalseNode;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSGetElementNode;
import com.aptana.editor.js.parsing.ast.JSGetPropertyNode;
import com.aptana.editor.js.parsing.ast.JSGroupNode;
import com.aptana.editor.js.parsing.ast.JSIdentifierNode;
import com.aptana.editor.js.parsing.ast.JSInvokeNode;
import com.aptana.editor.js.parsing.ast.JSNameValuePairNode;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSNodeTypes;
import com.aptana.editor.js.parsing.ast.JSNumberNode;
import com.aptana.editor.js.parsing.ast.JSObjectNode;
import com.aptana.editor.js.parsing.ast.JSPostUnaryOperatorNode;
import com.aptana.editor.js.parsing.ast.JSPreUnaryOperatorNode;
import com.aptana.editor.js.parsing.ast.JSRegexNode;
import com.aptana.editor.js.parsing.ast.JSReturnNode;
import com.aptana.editor.js.parsing.ast.JSStringNode;
import com.aptana.editor.js.parsing.ast.JSTreeWalker;
import com.aptana.editor.js.parsing.ast.JSTrueNode;
import com.aptana.editor.js.sdoc.model.DocumentationBlock;
import com.aptana.editor.js.sdoc.model.ExampleTag;
import com.aptana.editor.js.sdoc.model.ParamTag;
import com.aptana.editor.js.sdoc.model.ReturnTag;
import com.aptana.editor.js.sdoc.model.Tag;
import com.aptana.editor.js.sdoc.model.TagType;
import com.aptana.editor.js.sdoc.model.Type;
import com.aptana.editor.js.sdoc.model.TypeTag;
import com.aptana.index.core.Index;
import com.aptana.parsing.Scope;
import com.aptana.parsing.ast.IParseNode;

public class JSTypeWalker extends JSTreeWalker
{
	public static final String DYNAMIC_CLASS_PREFIX = "-dynamic-type-";

	private static Map<JSNode, String> NODE_TYPE_CACHE;

	private Scope<JSNode> _scope;
	private Index _index;
	private List<String> _types;
	private JSIndexQueryHelper _indexHelper;
	private List<TypeElement> _generatedTypes;

	/**
	 * applyDocumentation
	 * 
	 * @param function
	 * @param block
	 */
	protected static void applyDocumentation(FunctionElement function, DocumentationBlock block)
	{
		if (block != null)
		{
			// apply description
			function.setDescription(block.getText());

			// apply parameters
			for (Tag tag : block.getTags(TagType.PARAM))
			{
				ParamTag paramTag = (ParamTag) tag;
				ParameterElement parameter = new ParameterElement();

				parameter.setName(paramTag.getName());
				parameter.setDescription(paramTag.getText());
				parameter.setUsage(paramTag.getUsage().getName());

				for (Type type : paramTag.getTypes())
				{
					parameter.addType(type.toSource());
				}

				function.addParameter(parameter);
			}

			// apply return types
			for (Tag tag : block.getTags(TagType.RETURN))
			{
				ReturnTag returnTag = (ReturnTag) tag;

				for (Type type : returnTag.getTypes())
				{
					ReturnTypeElement returnType = new ReturnTypeElement();

					returnType.setType(type.toSource());
					returnType.setDescription(returnTag.getText());

					function.addReturnType(returnType);
				}
			}

			// apply examples
			for (Tag tag : block.getTags(TagType.EXAMPLE))
			{
				ExampleTag exampleTag = (ExampleTag) tag;

				function.addExample(exampleTag.getText());
			}
		}
	}

	/**
	 * applyDocumentation
	 * 
	 * @param property
	 * @param block
	 */
	protected static void applyDocumentation(PropertyElement property, DocumentationBlock block)
	{
		if (property instanceof FunctionElement)
		{
			applyDocumentation((FunctionElement) property, block);
		}
		else
		{
			if (block != null)
			{
				// apply description
				property.setDescription(block.getText());

				// apply types
				for (Tag tag : block.getTags(TagType.TYPE))
				{
					TypeTag typeTag = (TypeTag) tag;

					for (Type type : typeTag.getTypes())
					{
						ReturnTypeElement returnType = new ReturnTypeElement();

						returnType.setType(type.toSource());
						returnType.setDescription(typeTag.getText());

						property.addType(returnType);
					}
				}

				// apply examples
				for (Tag tag : block.getTags(TagType.EXAMPLE))
				{
					ExampleTag exampleTag = (ExampleTag) tag;

					property.addExample(exampleTag.getText());
				}
			}
		}
	}

	/**
	 * clearTypeCache
	 */
	public static void clearTypeCache()
	{
		if (NODE_TYPE_CACHE != null)
		{
			NODE_TYPE_CACHE.clear();
		}
	}

	/**
	 * getScopeProperties
	 * 
	 * @param scope
	 * @param index
	 * @param location
	 * @return
	 */
	public static List<PropertyElement> getScopeProperties(Scope<JSNode> scope, Index index, URI location)
	{
		clearTypeCache();

		List<PropertyElement> properties = new ArrayList<PropertyElement>();

		for (String symbol : scope.getLocalSymbolNames())
		{
			List<JSNode> nodes = scope.getSymbol(symbol);

			if (nodes != null && nodes.isEmpty() == false)
			{
				// TODO: We may want to process all nodes and potentially
				// create a new type that is the union of all types. For
				// now last definition wins.
				JSNode node = nodes.get(nodes.size() - 1);
				DocumentationBlock block = node.getDocumentation();
				PropertyElement property = (node instanceof JSFunctionNode) ? new FunctionElement() : new PropertyElement();

				property.setName(symbol);

				if (block != null)
				{
					applyDocumentation(property, block);
				}
				else
				{
					JSTypeWalker walker = new JSTypeWalker(scope, index);

					node.accept(walker);

					List<TypeElement> generatedTypes = walker.getGeneratedTypes();

					if (generatedTypes.isEmpty() == false)
					{
						JSIndexWriter writer = new JSIndexWriter();

						// write out any generated types
						for (TypeElement type : walker.getGeneratedTypes())
						{
							writer.writeType(index, type, location);
						}
					}

					// add property types
					for (String propertyType : walker.getTypes())
					{
						ReturnTypeElement returnType = new ReturnTypeElement();

						returnType.setType(propertyType);

						property.addType(returnType);
					}
				}

				properties.add(property);
			}
		}

		return properties;
	}

	/**
	 * JSTypeWalker
	 */
	public JSTypeWalker(Scope<JSNode> scope)
	{
		this(scope, null);
	}

	/**
	 * JSTypeWalker
	 * 
	 * @param scope
	 * @param projectIndex
	 */
	public JSTypeWalker(Scope<JSNode> scope, Index projectIndex)
	{
		this(scope, projectIndex, new ArrayList<TypeElement>());
	}

	/**
	 * JSTypeWalker
	 * 
	 * @param scope
	 * @param projectIndex
	 * @param generatedTypes
	 */
	protected JSTypeWalker(Scope<JSNode> scope, Index projectIndex, List<TypeElement> generatedTypes)
	{
		this._scope = scope;
		this._index = projectIndex;
		this._indexHelper = new JSIndexQueryHelper();
		this._generatedTypes = generatedTypes;
	}

	/**
	 * addGeneratedType
	 * 
	 * @param type
	 */
	protected void addGeneratedType(TypeElement type)
	{
		this._generatedTypes.add(type);
	}

	/**
	 * addIdentifierTypes
	 * 
	 * @param name
	 * @param identifierNode
	 */
	private void addIdentifierTypes(String name, JSIdentifierNode identifierNode)
	{
		IParseNode parent = identifierNode.getParent();

		if (parent.getNodeType() == JSNodeTypes.PARAMETERS)
		{
			IParseNode grandparent = parent.getParent();
			int typeCount = this.getTypes().size();

			if (grandparent.getNodeType() == JSNodeTypes.FUNCTION)
			{
				DocumentationBlock docs = ((JSNode) grandparent).getDocumentation();

				if (docs != null)
				{
					int index = identifierNode.getIndex();
					List<Tag> params = docs.getTags(TagType.PARAM);

					if (params != null && index < params.size())
					{
						ParamTag param = (ParamTag) params.get(index);

						if (name.equals(param.getName()))
						{
							for (Type parameterType : param.getTypes())
							{
								this.addType(parameterType.getName());
							}
						}
					}
				}
			}

			// Use "Object" as parameter type if we didn't find types by other
			// means
			if (this.getTypes().size() == typeCount)
			{
				this.addType(JSTypeConstants.OBJECT);
			}
		}
		else if (identifierNode.getText().equals(name) == false) // prevent recursion
		{
			identifierNode.accept(this);
		}
	}

	/**
	 * addNonIdentifierTypes
	 * 
	 * @param node
	 */
	private void addNonIdentifierTypes(JSNode node)
	{
		DocumentationBlock block = node.getDocumentation();

		if (block != null)
		{
			if (node instanceof JSFunctionNode)
			{
				FunctionElement function = new FunctionElement();

				applyDocumentation(function, block);

				this.addType(function.getSignature());
			}
			else
			{
				PropertyElement property = new PropertyElement();

				applyDocumentation(property, block);

				this.addTypes(property.getTypeNames());
			}
		}
		else
		{
			node.accept(this);
		}
	}

	/**
	 * addType
	 * 
	 * @param type
	 */
	protected void addType(String type)
	{
		if (type != null && type.length() > 0)
		{
			if (this._types == null)
			{
				this._types = new ArrayList<String>();
			}

			if (this._types.contains(type) == false)
			{
				this._types.add(type);
			}
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
	 * addUserAgents
	 * 
	 * @param element
	 */
	protected void addUserAgents(BaseElement element)
	{
		// make valid in all user agents
		for (UserAgent userAgent : UserAgentManager.getInstance().getAllUserAgents())
		{
			UserAgentElement ua = new UserAgentElement();

			ua.setPlatform(userAgent.ID);

			element.addUserAgent(ua);
		}
	}

	/**
	 * getActiveScope
	 * 
	 * @param offset
	 * @return
	 */
	protected Scope<JSNode> getActiveScope(int offset)
	{
		Scope<JSNode> result = null;

		if (this._scope != null)
		{
			Scope<JSNode> root = this._scope;

			while (true)
			{
				Scope<JSNode> candidate = root.getParentScope();

				if (candidate == null)
				{
					break;
				}
				else
				{
					root = candidate;
				}
			}

			result = root.getScopeAtOffset(offset);
		}

		return result;
	}

	/**
	 * getElementType
	 * 
	 * @param type
	 * @return
	 */
	protected String getElementType(String type)
	{
		String result = null;

		if (type != null && type.length() > 0)
		{
			if (type.endsWith(JSTypeConstants.ARRAY_LITERAL))
			{
				result = type.substring(0, type.length() - 2);
			}
			else if (type.startsWith(JSTypeConstants.GENERIC_ARRAY_OPEN) && type.endsWith(JSTypeConstants.GENERIC_ARRAY_CLOSE))
			{
				result = type.substring(JSTypeConstants.GENERIC_ARRAY_OPEN.length(), type.length() - 1);
			}
			else if (type.equals(JSTypeConstants.ARRAY))
			{
				result = JSTypeConstants.OBJECT;
			}
		}

		return result;
	}

	/**
	 * getGeneratedType
	 * 
	 * @param name
	 * @return
	 */
	public TypeElement getGeneratedType(String name)
	{
		TypeElement result = null;

		for (TypeElement type : this._generatedTypes)
		{
			if (type.getName().equals(name))
			{
				result = type;
				break;
			}
		}

		return result;
	}

	/**
	 * getGeneratedTypes
	 * 
	 * @return
	 */
	public List<TypeElement> getGeneratedTypes()
	{
		return this._generatedTypes;
	}

	/**
	 * getTypeElement
	 * 
	 * @param node
	 * @return
	 */
	protected String getNodeType(JSNode node)
	{
		String result = null;

		if (NODE_TYPE_CACHE != null)
		{
			result = NODE_TYPE_CACHE.get(node);
		}

		return result;
	}

	/**
	 * getReturnNodes
	 * 
	 * @param node
	 * @return
	 */
	protected List<JSReturnNode> getReturnNodes(JSFunctionNode node)
	{
		List<JSReturnNode> result = new ArrayList<JSReturnNode>();

		// create and prime queue
		List<IParseNode> queue = new ArrayList<IParseNode>();
		queue.add(node.getBody());

		while (queue.size() > 0)
		{
			IParseNode current = queue.remove(0);

			if (current instanceof JSReturnNode)
			{
				result.add((JSReturnNode) current);
			}
			else if (current instanceof JSFunctionNode == false)
			{
				for (IParseNode child : current)
				{
					queue.add(child);
				}
			}
		}

		return result;
	}

	/**
	 * getTypes
	 * 
	 * @return
	 */
	public List<String> getTypes()
	{
		List<String> result = this._types;

		if (result == null)
		{
			result = Collections.emptyList();
		}

		return result;
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
	public List<String> getTypes(IParseNode node, Scope<JSNode> scope)
	{
		List<String> result;

		if (node instanceof JSNode)
		{
			// create new nested walker
			JSTypeWalker walker = new JSTypeWalker(scope, this._index, this._generatedTypes);

			// collect types
			walker.visit((JSNode) node);

			// return collected types
			result = walker.getTypes();
		}
		else
		{
			result = Collections.emptyList();
		}

		return result;
	}

	/**
	 * @return
	 */
	private String getUniqueTypeName()
	{
		UUID uuid = UUID.randomUUID();

		return MessageFormat.format("{0}{1}", DYNAMIC_CLASS_PREFIX, uuid); //$NON-NLS-1$
	}

	/**
	 * hasGeneratedType
	 * 
	 * @param type
	 * @return
	 */
	public boolean hasGeneratedType(String type)
	{
		boolean result = false;

		if (type != null && type.length() > 0 && NODE_TYPE_CACHE != null)
		{
			// we might have a user-generated type, so look for it directly
			for (String typeName : NODE_TYPE_CACHE.values())
			{
				if (typeName.equals(type))
				{
					result = true;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * putTypeElement
	 * 
	 * @param node
	 * @param type
	 */
	protected void putNodeType(JSNode node, String type)
	{
		if (NODE_TYPE_CACHE == null)
		{
			NODE_TYPE_CACHE = new HashMap<JSNode, String>();
		}

		NODE_TYPE_CACHE.put(node, type);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSArrayNode)
	 */
	@Override
	public void visit(JSArrayNode node)
	{
		if (node.hasChildren() == false)
		{
			this.addType(JSTypeConstants.ARRAY);
		}
		else
		{
			for (String type : this.getTypes(node.getFirstChild()))
			{
				this.addType(JSTypeConstants.GENERIC_ARRAY_OPEN + type + JSTypeConstants.GENERIC_ARRAY_CLOSE);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSAssignmentNode)
	 */
	@Override
	public void visit(JSAssignmentNode node)
	{
		switch (node.getNodeType())
		{
			case JSNodeTypes.EQUAL:
				this.addTypes(this.getTypes(node.getRightHandSide()));
				break;

			case JSNodeTypes.ADD_AND_ASSIGN:
				String type = JSTypeConstants.NUMBER;
				List<String> lhsTypes = this.getTypes(node.getLeftHandSide());
				List<String> rhsTypes = this.getTypes(node.getRightHandSide());

				if (lhsTypes.contains(JSTypeConstants.STRING) || rhsTypes.contains(JSTypeConstants.STRING))
				{
					type = JSTypeConstants.STRING;
				}

				this.addType(type);
				break;

			default:
				this.addType(JSTypeConstants.NUMBER);
				break;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSArithmeticOperatorNode)
	 */
	@Override
	public void visit(JSBinaryArithmeticOperatorNode node)
	{
		String type = JSTypeConstants.NUMBER;

		if (node.getNodeType() == JSNodeTypes.ADD)
		{
			List<String> lhsTypes = this.getTypes(node.getLeftHandSide());
			List<String> rhsTypes = this.getTypes(node.getRightHandSide());

			if (lhsTypes.contains(JSTypeConstants.STRING) || rhsTypes.contains(JSTypeConstants.STRING))
			{
				type = JSTypeConstants.STRING;
			}
		}

		this.addType(type);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSBooleanOperatorNode)
	 */
	@Override
	public void visit(JSBinaryBooleanOperatorNode node)
	{
		this.addType(JSTypeConstants.BOOLEAN);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSConditionalNode)
	 */
	@Override
	public void visit(JSConditionalNode node)
	{
		this.addTypes(this.getTypes(node.getTrueExpression()));
		this.addTypes(this.getTypes(node.getFalseExpression()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSConstructNode)
	 */
	@Override
	public void visit(JSConstructNode node)
	{
		// TODO: Need to handle any property assignments off of "this"
		IParseNode child = node.getExpression();

		if (child instanceof JSNode)
		{
			List<String> types = this.getTypes(child);

			for (String typeName : types)
			{
				int index = typeName.indexOf(':');

				if (index != -1)
				{
					for (String returnTypeName : typeName.substring(index + 1).split(",")) //$NON-NLS-1$
					{
						this.addType(returnTypeName);
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSFalseNode)
	 */
	@Override
	public void visit(JSFalseNode node)
	{
		this.addType(JSTypeConstants.BOOLEAN);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSFunctionNode)
	 */
	@Override
	public void visit(JSFunctionNode node)
	{
		String type = this.getNodeType(node);

		if (type == null)
		{
			// We temporarily store the default function type to prevent
			// infinite recursion in potential invoke cycles
			this.putNodeType(node, JSTypeConstants.FUNCTION);

			List<String> types = new ArrayList<String>();
			Scope<JSNode> scope = this.getActiveScope(node.getBody().getStartingOffset());
			boolean foundReturnExpression = false;

			// infer return types
			for (JSReturnNode returnValue : this.getReturnNodes(node))
			{
				IParseNode expression = returnValue.getExpression();

				if (expression.isEmpty() == false)
				{
					foundReturnExpression = true;

					types.addAll(this.getTypes(expression, scope));
				}
			}

			// build function type, including return values
			if (types.isEmpty() == false)
			{
				type = JSTypeConstants.FUNCTION + ":" + StringUtil.join(",", types); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else if (foundReturnExpression)
			{
				// If we couldn't infer a return type and we had a return
				// expression, then at least return Object from this function
				type = JSTypeConstants.FUNCTION + ":" + JSTypeConstants.OBJECT; //$NON-NLS-1$
			}
			else
			{
				type = JSTypeConstants.FUNCTION;
			}

			this.putNodeType(node, type);
		}

		this.addType(type);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSGetElementNode)
	 */
	@Override
	public void visit(JSGetElementNode node)
	{
		// TODO: Should check subscript to determine if the type is a Number or
		// a String. If it is a String, then this should behave like get-property
		// assuming we can retrieve a literal string.
		IParseNode lhs = node.getLeftHandSide();

		if (lhs instanceof JSNode)
		{
			for (String typeName : this.getTypes(lhs))
			{
				String typeString = this.getElementType(typeName);

				if (typeString != null)
				{
					this.addType(typeString);
				}
				else
				{
					this.addType(JSTypeConstants.OBJECT);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSGetPropertyNode)
	 */
	@Override
	public void visit(JSGetPropertyNode node)
	{
		IParseNode lhs = node.getLeftHandSide();

		if (lhs instanceof JSNode)
		{
			IParseNode rhs = node.getRightHandSide();
			String memberName = rhs.getText();

			for (String typeName : this.getTypes(lhs))
			{
				// lookup up rhs name in type and add that value's type here
				PropertyElement property = this._indexHelper.getTypeMember(this._index, typeName, memberName, EnumSet.of(ContentSelector.RETURN_TYPES,
					ContentSelector.TYPES));

				if (property == null)
				{
					TypeElement type = this.getGeneratedType(typeName);

					if (type != null)
					{
						property = type.getProperty(memberName);
					}
				}

				if (property != null)
				{
					if (property instanceof FunctionElement)
					{
						FunctionElement function = (FunctionElement) property;

						this.addType(function.getSignature());
					}
					else
					{
						for (ReturnTypeElement typeElement : property.getTypes())
						{
							this.addType(typeElement.getType());
						}
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSGroupNode)
	 */
	@Override
	public void visit(JSGroupNode node)
	{
		IParseNode expression = node.getExpression();

		if (expression instanceof JSNode)
		{
			((JSNode) expression).accept(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSIdentifierNode)
	 */
	@Override
	public void visit(JSIdentifierNode node)
	{
		String name = node.getText();

		if (this._scope != null && this._scope.hasSymbol(name))
		{
			List<JSNode> symbolNodes = this._scope.getSymbol(name);

			for (JSNode symbolNode : symbolNodes)
			{
				if (symbolNode instanceof JSIdentifierNode)
				{
					this.addIdentifierTypes(name, (JSIdentifierNode) symbolNode);
				}
				else
				{
					this.addNonIdentifierTypes(symbolNode);
				}
			}
		}
		else
		{
			PropertyElement property = this._indexHelper.getGlobal(this._index, name, EnumSet.of(ContentSelector.TYPES, ContentSelector.RETURN_TYPES));

			if (property != null)
			{
				if (property instanceof FunctionElement)
				{
					FunctionElement function = (FunctionElement) property;

					this.addType(function.getSignature());
				}
				else
				{
					for (ReturnTypeElement typeElement : property.getTypes())
					{
						this.addType(typeElement.getType());
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSInvokeNode)
	 */
	@Override
	public void visit(JSInvokeNode node)
	{
		IParseNode child = node.getExpression();

		if (child instanceof JSNode)
		{
			List<String> types = this.getTypes(child);

			for (String typeName : types)
			{
				int index = typeName.indexOf(':');

				if (index != -1)
				{
					for (String returnTypeName : typeName.substring(index + 1).split(",")) //$NON-NLS-1$
					{
						this.addType(returnTypeName);
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSNumberNode)
	 */
	@Override
	public void visit(JSNumberNode node)
	{
		this.addType(JSTypeConstants.NUMBER);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSObjectNode)
	 */
	@Override
	public void visit(JSObjectNode node)
	{
		String type = this.getNodeType(node);

		if (type == null)
		{
			if (node.hasChildren() == false)
			{
				type = JSTypeConstants.OBJECT;
			}
			else
			{
				// TODO: Ideally we should hash the properties and their types to
				// see if a type like this one already exists
				TypeElement newType = new TypeElement();

				newType.setName(this.getUniqueTypeName());
				newType.addParentType(JSTypeConstants.OBJECT);
				this.addUserAgents(newType);

				// temporary container to collect properties and their value
				// sub-trees so we can infer property types after we have all
				// of the object's properties. We use a LinkedHashMap to
				// preserve order.
				Map<PropertyElement, JSNode> propertyNodeMap = new LinkedHashMap<PropertyElement, JSNode>();

				for (IParseNode child : node)
				{
					if (child instanceof JSNameValuePairNode)
					{
						JSNameValuePairNode nameValue = (JSNameValuePairNode) child;
						IParseNode nameNode = nameValue.getName();
						IParseNode valueNode = nameValue.getValue();

						if (valueNode instanceof JSNode)
						{
							PropertyElement property = (valueNode instanceof JSFunctionNode) ? new FunctionElement() : new PropertyElement();
							String name = nameNode.getText();

							// trim off leading and trailing quotes, if necessary
							property.setName((nameNode instanceof JSStringNode) ? name.substring(1, name.length() - 1) : name);

							// make valid in all user agents
							this.addUserAgents(property);

							newType.addProperty(property);

							// save property value for inferencing after all
							// properties have been collected
							propertyNodeMap.put(property, (JSNode) valueNode);
						}
					}
				}

				// save reference to type before inferring property types to
				// avoid potential infinite recursion
				this.putNodeType(node, newType.getName());

				// add to generated types so we have easy access to the type
				// when performing property lookups during inferencing
				this.addGeneratedType(newType);

				// now infer the property types
				for (Map.Entry<PropertyElement, JSNode> entry : propertyNodeMap.entrySet())
				{
					PropertyElement property = entry.getKey();
					JSNode valueNode = entry.getValue();
					DocumentationBlock docs = valueNode.getDocumentation();

					if (docs != null)
					{
						// get type from the docs
						applyDocumentation(property, docs);
					}
					else
					{
						// infer the type
						for (String valueType : this.getTypes(valueNode))
						{
							// process potential function signatures
							int index = valueType.indexOf(':');

							if (index != -1)
							{
								for (String returnTypeName : valueType.substring(index + 1).split(",")) //$NON-NLS-1$
								{
									ReturnTypeElement returnType = new ReturnTypeElement();

									returnType.setType(returnTypeName);

									property.addType(returnType);
								}
							}
							else
							{
								ReturnTypeElement returnType = new ReturnTypeElement();

								returnType.setType(valueType);

								property.addType(returnType);
							}
						}
					}
				}

				type = newType.getName();
			}
		}

		this.addType(type);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSPostUnaryOperatorNode)
	 */
	@Override
	public void visit(JSPostUnaryOperatorNode node)
	{
		this.addType(JSTypeConstants.NUMBER);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSPreUnaryOperatorNode)
	 */
	@Override
	public void visit(JSPreUnaryOperatorNode node)
	{
		switch (node.getNodeType())
		{
			case JSNodeTypes.DELETE:
			case JSNodeTypes.LOGICAL_NOT:
				this.addType(JSTypeConstants.BOOLEAN);
				break;

			case JSNodeTypes.TYPEOF:
				this.addType(JSTypeConstants.STRING);
				break;

			case JSNodeTypes.VOID:
				// technically this returns 'undefined', but we return nothing
				break;

			default:
				this.addType(JSTypeConstants.NUMBER);
				break;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSRegexNode)
	 */
	@Override
	public void visit(JSRegexNode node)
	{
		this.addType(JSTypeConstants.REG_EXP);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSStringNode)
	 */
	@Override
	public void visit(JSStringNode node)
	{
		this.addType(JSTypeConstants.STRING);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSTrueNode)
	 */
	@Override
	public void visit(JSTrueNode node)
	{
		this.addType(JSTypeConstants.BOOLEAN);
	}
}
