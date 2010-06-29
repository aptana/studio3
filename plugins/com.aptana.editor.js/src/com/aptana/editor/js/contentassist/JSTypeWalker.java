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
import com.aptana.editor.js.parsing.ast.JSArrayNode;
import com.aptana.editor.js.parsing.ast.JSConstructNode;
import com.aptana.editor.js.parsing.ast.JSFalseNode;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSGetPropertyNode;
import com.aptana.editor.js.parsing.ast.JSIdentifierNode;
import com.aptana.editor.js.parsing.ast.JSInvokeNode;
import com.aptana.editor.js.parsing.ast.JSNode;
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
		
		// be sure we return some type of list
		if (result == null)
		{
			result = Collections.emptyList();
		}
		
		return walker.getTypes();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSArrayNode)
	 */
	@Override
	public void visit(JSArrayNode node)
	{
		this.addType("Array");
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
		this.addType("Boolean");
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSFunctionNode)
	 */
	@Override
	public void visit(JSFunctionNode node)
	{
		this.addType("Function");
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
						this.addType("Function");
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
				this.addType("Function");
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
		this.addType("Number");
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSObjectNode)
	 */
	@Override
	public void visit(JSObjectNode node)
	{
		this.addType("Object");
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSRegexNode)
	 */
	@Override
	public void visit(JSRegexNode node)
	{
		this.addType("RegExp");
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSStringNode)
	 */
	@Override
	public void visit(JSStringNode node)
	{
		this.addType("String");
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSTrueNode)
	 */
	@Override
	public void visit(JSTrueNode node)
	{
		this.addType("Boolean");
	}
}
