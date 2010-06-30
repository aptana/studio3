package com.aptana.editor.js.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import com.aptana.editor.js.contentassist.model.FieldSelector;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.ReturnTypeElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.editor.js.parsing.ast.JSBinaryArithmeticOperatorNode;
import com.aptana.editor.js.parsing.ast.JSArrayNode;
import com.aptana.editor.js.parsing.ast.JSBinaryBooleanOperatorNode;
import com.aptana.editor.js.parsing.ast.JSConstructNode;
import com.aptana.editor.js.parsing.ast.JSFalseNode;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSGetElementNode;
import com.aptana.editor.js.parsing.ast.JSGetPropertyNode;
import com.aptana.editor.js.parsing.ast.JSGroupNode;
import com.aptana.editor.js.parsing.ast.JSIdentifierNode;
import com.aptana.editor.js.parsing.ast.JSInvokeNode;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSNodeTypes;
import com.aptana.editor.js.parsing.ast.JSNumberNode;
import com.aptana.editor.js.parsing.ast.JSObjectNode;
import com.aptana.editor.js.parsing.ast.JSRegexNode;
import com.aptana.editor.js.parsing.ast.JSStringNode;
import com.aptana.editor.js.parsing.ast.JSTreeWalker;
import com.aptana.editor.js.parsing.ast.JSTrueNode;
import com.aptana.index.core.Index;
import com.aptana.parsing.Scope;
import com.aptana.parsing.ast.IParseNode;

public class JSTypeWalker extends JSTreeWalker
{
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
	
	private Scope<JSNode> _scope;
	private Index _index;
	private List<String> _types;
	private JSIndexQueryHelper _indexHelper;

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
		this._scope = scope;
		this._index = projectIndex;
		this._indexHelper = new JSIndexQueryHelper();
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

			this._types.add(type);
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
		return new JSReferenceWalker(this._scope, this._index);
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
	 * getTypes
	 * 
	 * @return
	 */
	public List<String> getTypes()
	{
		return this._types;
	}
	
	/**
	 * getTypes
	 * 
	 * @param node
	 * @return
	 */
	public List<String> getTypes(JSNode node)
	{
		List<String> result;
		
		// create new nested walker
		JSTypeWalker walker = new JSTypeWalker(this._scope, this._index);
		
		// collect types
		walker.visit(node);
		
		// grab result
		result = walker.getTypes();
		
		// be sure we return some type of list so we don't have to check for
		// null return values
		if (result == null)
		{
			result = Collections.emptyList();
		}
		
		return walker.getTypes();
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSArithmeticOperatorNode)
	 */
	@Override
	public void visit(JSBinaryArithmeticOperatorNode node)
	{
		String type = NUMBER_TYPE;
		
		if (node.getNodeType() == JSNodeTypes.ADD)
		{
			IParseNode lhs = node.getLeftHandSide();
			IParseNode rhs = node.getRightHandSide();
			
			if (lhs instanceof JSNode && rhs instanceof JSNode)
			{
				List<String> lhsTypes = this.getTypes((JSNode) lhs);
				List<String> rhsTypes = this.getTypes((JSNode) rhs);
				
				if (lhsTypes.contains(STRING_TYPE) || rhsTypes.contains(STRING_TYPE))
				{
					type = STRING_TYPE;
				}
			}
		}
		
		this.addType(type);
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

	/* (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSBooleanOperatorNode)
	 */
	@Override
	public void visit(JSBinaryBooleanOperatorNode node)
	{
		this.addType(BOOLEAN_TYPE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSConstructNode)
	 */
	@Override
	public void visit(JSConstructNode node)
	{
		IParseNode child = node.getChild(0);

		// TEMP: for debugging
		String name = child.getText();
		List<JSNode> symbolNodes = this._scope.getSymbol(name);

		for (JSNode symbolNode : symbolNodes)
		{
			if (symbolNode instanceof JSFunctionNode)
			{
				List<String> returnTypes = ((JSFunctionNode) symbolNode).getReturnTypes();

				this.addTypes(returnTypes);
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
		this.addType(FUNCTION_TYPE);
	}

	
	/* (non-Javadoc)
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
			
			for (TypeElement type : walker.getTypes())
			{
				String typeName = type.getName();
				PropertyElement property = this._indexHelper.getTypeMember(this._index, typeName, memberName, EnumSet.of(FieldSelector.TYPES, FieldSelector.RETURN_TYPES));
				
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

	/* (non-Javadoc)
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
			List<String> types = this.getTypes((JSNode) lhs);
			
			for (String type : types)
			{
				// lookup up rhs name in type and add that value's type here
				PropertyElement property = this._indexHelper.getCoreTypeMember(type, name, EnumSet.of(FieldSelector.TYPES));
				
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

	/* (non-Javadoc)
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
//		if (this._scope != null && this._scope.hasSymbol(name))
//		{
//			List<JSNode> symbolNodes = this._scope.getSymbol(name);
//	
//			for (JSNode symbolNode : symbolNodes)
//			{
//				symbolNode.accept(this);
//			}
//		}
		
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSInvokeNode)
	 */
	@Override
	public void visit(JSInvokeNode node)
	{
		IParseNode child = node.getExpression();
		
		if (child instanceof JSIdentifierNode)
		{
			String name = child.getText();
			
			PropertyElement function = this._indexHelper.getGlobalFunction(this._index, name, EnumSet.of(FieldSelector.RETURN_TYPES));
			
			if (function != null)
			{
				for (String type : function.getTypeNames())
				{
					this.addType(type);
				}
			}
			
//			// lookup in local scope
//			if (this._scope != null && this._scope.hasSymbol(name))
//			{
//				List<JSNode> symbolNodes = this._scope.getSymbol(name);
//		
//				for (JSNode symbolNode : symbolNodes)
//				{
//					if (symbolNode instanceof JSFunctionNode)
//					{
//						this.addTypes(((JSFunctionNode) symbolNode).getReturnTypes());
//					}
//				}
//			}
		}
		else if (child instanceof JSNode)
		{
			JSReferenceWalker walker = this.createReferenceWalker();
			
			((JSNode) child).accept(walker);
			
			String methodName = walker.getPropertyName();
			
			for (TypeElement type : walker.getTypes())
			{
				String typeName = type.getName();
				
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
		this.addType(OBJECT_TYPE);
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
