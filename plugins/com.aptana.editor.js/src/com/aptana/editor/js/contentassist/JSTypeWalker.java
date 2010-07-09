package com.aptana.editor.js.contentassist;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.js.contentassist.model.FieldSelector;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.ReturnTypeElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
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
import com.aptana.index.core.Index;
import com.aptana.parsing.Scope;
import com.aptana.parsing.ast.IParseNode;

public class JSTypeWalker extends JSTreeWalker
{
	private static int TYPE_COUNT = 0;
	private static Map<JSNode, String> NODE_TYPE_CACHE;

	private static final String ARRAY_TYPE = "Array";
	private static final String BOOLEAN_TYPE = "Boolean";
	private static final String FUNCTION_TYPE = "Function";
	private static final String NUMBER_TYPE = "Number";
	private static final String OBJECT_TYPE = "Object";
	private static final String REG_EXP_TYPE = "RegExp";
	private static final String STRING_TYPE = "String";

	private static final String ARRAY_LITERAL = "[]";
	private static final String GENERIC_ARRAY_CLOSE = ">";
	private static final String GENERIC_ARRAY_OPEN = "Array<";

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

	private Scope<JSNode> _scope;
	private Index _index;
	private List<String> _types;
	private JSIndexQueryHelper _indexHelper;

	private List<TypeElement> _generatedTypes;

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
	 * createReferenceWalker
	 * 
	 * @return
	 */
	protected JSReferenceWalker createReferenceWalker()
	{
		return new JSReferenceWalker(this._scope, this._index, this);
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
			if (type.endsWith(ARRAY_LITERAL))
			{
				result = type.substring(0, type.length() - 2);
			}
			else if (type.startsWith(GENERIC_ARRAY_OPEN) && type.endsWith(GENERIC_ARRAY_CLOSE))
			{
				result = type.substring(GENERIC_ARRAY_OPEN.length() + 1, type.length() - 1);

				// TODO: handle generalized nesting like Array<Array<String>>. Just return
				// ARRAY type for now
				if (result.startsWith(ARRAY_TYPE))
				{
					result = ARRAY_TYPE;
				}
			}
			else if (type.equals(ARRAY_TYPE))
			{
				result = OBJECT_TYPE;
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
		List<String> result;

		if (this._types != null)
		{
			result = this._types;
		}
		else
		{
			// be sure we return some type of list so we don't have to check for
			// null return values
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
		return MessageFormat.format("UserType{0}", TYPE_COUNT++);
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
		this.addType(ARRAY_TYPE);
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
				String type = NUMBER_TYPE;
				List<String> lhsTypes = this.getTypes(node.getLeftHandSide());
				List<String> rhsTypes = this.getTypes(node.getRightHandSide());

				if (lhsTypes.contains(STRING_TYPE) || rhsTypes.contains(STRING_TYPE))
				{
					type = STRING_TYPE;
				}

				this.addType(type);
				break;

			default:
				this.addType(NUMBER_TYPE);
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
		String type = NUMBER_TYPE;

		if (node.getNodeType() == JSNodeTypes.ADD)
		{
			List<String> lhsTypes = this.getTypes(node.getLeftHandSide());
			List<String> rhsTypes = this.getTypes(node.getRightHandSide());

			if (lhsTypes.contains(STRING_TYPE) || rhsTypes.contains(STRING_TYPE))
			{
				type = STRING_TYPE;
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
		this.addType(BOOLEAN_TYPE);
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
		IParseNode child = node.getIdentifier();

		if (child instanceof JSNode)
		{
			JSReferenceWalker walker = this.createReferenceWalker();

			((JSNode) child).accept(walker);

			String methodName = walker.getPropertyName();

			for (String typeName : walker.getTypes())
			{
				FunctionElement function = this._indexHelper.getTypeMethod(this._index, typeName, methodName, EnumSet.of(FieldSelector.RETURN_TYPES));

				if (function != null)
				{
					for (String returnType : function.getTypeNames())
					{
						this.addType(returnType);
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
		this.addType(BOOLEAN_TYPE);
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
			List<String> types = new ArrayList<String>();
			Scope<JSNode> scope = this._scope.getScopeAtOffset(node.getBody().getStartingOffset());
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
				type = StringUtil.join(",", types);
			}
			else if (foundReturnExpression)
			{
				// If we couldn't infer a return type and we had a return
				// expression, then at least return Object from this function
				type = OBJECT_TYPE;
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
			JSReferenceWalker walker = this.createReferenceWalker();

			((JSNode) lhs).accept(walker);

			String memberName = walker.getPropertyName();

			for (String typeName : walker.getTypes())
			{
				PropertyElement property = this._indexHelper.getTypeMember(this._index, typeName, memberName, EnumSet.of(FieldSelector.TYPES,
					FieldSelector.RETURN_TYPES));

				if (property != null)
				{
					ReturnTypeElement[] returnTypes = property.getTypes();

					if (returnTypes != null && returnTypes.length > 0)
					{
						for (ReturnTypeElement returnType : property.getTypes())
						{
							String typeString = this.getElementType(returnType.getType());

							if (typeString != null)
							{
								this.addType(typeString);
							}
							else
							{
								this.addType(OBJECT_TYPE);
							}
						}
					}
					else
					{
						this.addType(OBJECT_TYPE);
					}
				}
				else
				{
					this.addType(OBJECT_TYPE);
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
			String name = rhs.getText();

			for (String type : this.getTypes(lhs))
			{
				// lookup up rhs name in type and add that value's type here
				PropertyElement property = this._indexHelper.getTypeMember(this._index, type, name, EnumSet.of(FieldSelector.TYPES));

				if (property != null)
				{
					if (property instanceof FunctionElement)
					{
						this.addType(FUNCTION_TYPE);
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

		// lookup in local scope
		if (this._scope != null && this._scope.hasSymbol(name))
		{
			List<JSNode> symbolNodes = this._scope.getSymbol(name);

			for (JSNode symbolNode : symbolNodes)
			{
				symbolNode.accept(this);
			}
		}
		else
		{
			PropertyElement property = this._indexHelper.getGlobal(this._index, name, EnumSet.of(FieldSelector.TYPES, FieldSelector.RETURN_TYPES));

			if (property != null)
			{
				if (property instanceof FunctionElement)
				{
					this.addType(FUNCTION_TYPE);
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
			JSReferenceWalker walker = this.createReferenceWalker();

			((JSNode) child).accept(walker);

			String methodName = walker.getPropertyName();

			for (String typeName : walker.getTypes())
			{
				FunctionElement function = this._indexHelper.getTypeMethod(this._index, typeName, methodName, EnumSet.of(FieldSelector.RETURN_TYPES));
				
				if (function == null)
				{
					TypeElement type = this.getGeneratedType(typeName);
					
					if (type != null)
					{
						PropertyElement property = type.getProperty(methodName);
						
						if (property instanceof FunctionElement)
						{
							function = (FunctionElement) property;
						}
					}
				}

				if (function != null)
				{
					for (String returnType : function.getTypeNames())
					{
						this.addType(returnType);
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
		this.addType(NUMBER_TYPE);
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
				type = OBJECT_TYPE;
			}
			else
			{
				// TODO: Ideally we should hash the properties and their types to
				// see if a type like this one already exists
				TypeElement newType = new TypeElement();

				newType.setName(this.getUniqueTypeName());
				newType.addParentType(OBJECT_TYPE);

				// temporary container to collect properties and their value
				// sub-trees so we can infer property types after we have all
				// of the object's properties
				Map<PropertyElement,IParseNode> propertyNodeMap = new LinkedHashMap<PropertyElement,IParseNode>();

				for (IParseNode child : node)
				{
					if (child instanceof JSNameValuePairNode)
					{
						JSNameValuePairNode nameValue = (JSNameValuePairNode) child;
						IParseNode nameNode = nameValue.getName();
						IParseNode valueNode = nameValue.getValue();
						PropertyElement property = (valueNode instanceof JSFunctionNode) ? new FunctionElement() : new PropertyElement();
						String name = nameNode.getText();

						// trim off leading and trailing quotes, if necessary
						property.setName((nameNode instanceof JSStringNode) ? name.substring(1, name.length() - 1) : name);

						newType.addProperty(property);
						
						// save property value for inferencing after all
						// properties have been collected
						propertyNodeMap.put(property, valueNode);
					}
				}
				
				// save reference to type before inferring property types to
				// avoid potential infinite recursion
				this.putNodeType(node, newType.getName());
				
				// add to generated types so references can have access to the
				// type before we finish processing
				this.addGeneratedType(newType);
				
				// now infer the property types
				for (Map.Entry<PropertyElement, IParseNode> entry : propertyNodeMap.entrySet())
				{
					PropertyElement property = entry.getKey();
					IParseNode valueNode = entry.getValue();
					
					for (String valueType : this.getTypes(valueNode))
					{
						ReturnTypeElement returnType = new ReturnTypeElement();

						returnType.setType(valueType);

						property.addType(returnType);
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
		this.addType(NUMBER_TYPE);
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
				this.addType(BOOLEAN_TYPE);
				break;

			case JSNodeTypes.TYPEOF:
				this.addType(STRING_TYPE);
				break;

			case JSNodeTypes.VOID:
				// technically this returns 'undefined', but we return nothing
				break;

			default:
				this.addType(NUMBER_TYPE);
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
		this.addType(REG_EXP_TYPE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSStringNode)
	 */
	@Override
	public void visit(JSStringNode node)
	{
		this.addType(STRING_TYPE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSTrueNode)
	 */
	@Override
	public void visit(JSTrueNode node)
	{
		this.addType(BOOLEAN_TYPE);
	}
}
