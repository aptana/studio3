package com.aptana.editor.js.contentassist;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import com.aptana.editor.js.contentassist.model.FieldSelector;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.editor.js.parsing.ast.JSArrayNode;
import com.aptana.editor.js.parsing.ast.JSFalseNode;
import com.aptana.editor.js.parsing.ast.JSGetPropertyNode;
import com.aptana.editor.js.parsing.ast.JSIdentifierNode;
import com.aptana.editor.js.parsing.ast.JSInvokeNode;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSObjectNode;
import com.aptana.editor.js.parsing.ast.JSRegexNode;
import com.aptana.editor.js.parsing.ast.JSStringNode;
import com.aptana.editor.js.parsing.ast.JSTreeWalker;
import com.aptana.editor.js.parsing.ast.JSTrueNode;
import com.aptana.index.core.Index;
import com.aptana.parsing.Scope;
import com.aptana.parsing.ast.IParseNode;

public class JSReferenceWalker extends JSTreeWalker implements IReference
{
	private Scope<JSNode> _scope;
	private Index _index;
	private JSIndexQueryHelper _indexHelper;

	private List<TypeElement> _types;
	private String _property;

	/**
	 * JSReferenceWalker
	 * 
	 * @param scope
	 * @param projectIndex
	 */
	public JSReferenceWalker(Scope<JSNode> scope, Index index)
	{
		this._scope = scope;
		this._index = index;
		this._indexHelper = new JSIndexQueryHelper();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.contentassist.IReference#getPropertyName()
	 */
	@Override
	public String getPropertyName()
	{
		return this._property;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.contentassist.IReference#getPropertyReference(java.lang.String)
	 */
	@Override
	public IReference getPropertyReference(String propertyName)
	{
		JSReferenceWalker result = null;

		if (this._property == null)
		{
			this._property = propertyName;
			result = this;
		}
		else if (this._types != null)
		{
			result = new JSReferenceWalker(this._scope, this._index);
			
			for (TypeElement type : this._types)
			{
				String typeName = type.getName();
				PropertyElement property = this._indexHelper.getTypeProperty(this._index, typeName, this._property, EnumSet.of(FieldSelector.TYPES));
				
				if (property != null)
				{
					if (property instanceof FunctionElement)
					{
						result.addType("Function");
					}
					else
					{
						for (String propertyType : property.getTypeNames())
						{
							result.addType(propertyType);
						}
					}
					
					result.setProperty(propertyName);
				}
			}
		}
		
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.contentassist.IReference#getTypes()
	 */
	@Override
	public List<TypeElement> getTypes()
	{
		return this._types;
	}

	/**
	 * setProperty
	 * 
	 * @param name
	 */
	protected void setProperty(String name)
	{
		this._property = name;
	}

	/**
	 * setType
	 * 
	 * @param typeName
	 */
	protected void addType(String typeName)
	{
		TypeElement type = this._indexHelper.getType(this._index, typeName, EnumSet.of(FieldSelector.NAME));
		
		if (type != null)
		{
			if (this._types == null)
			{
				this._types = new LinkedList<TypeElement>();
			}
			
			this._types.add(type); 
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSArrayNode)
	 */
	@Override
	public void visit(JSArrayNode node)
	{
		this.setProperty("Array");
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSFalseNode)
	 */
	@Override
	public void visit(JSFalseNode node)
	{
		this.setProperty("Boolean");
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
			String propertyName = rhs.getText();
			JSReferenceWalker walker = new JSReferenceWalker(this._scope, this._index);
			
			((JSNode) lhs).accept(walker);
			
			IReference reference = walker.getPropertyReference(propertyName);
			
			this._types = reference.getTypes();
			this._property = reference.getPropertyName();
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

//		// lookup in local scope
//		if (this._scope != null && this._scope.hasSymbol(name))
//		{
//			List<JSNode> symbolNodes = this._scope.getSymbol(name);
//
//			for (JSNode symbolNode : symbolNodes)
//			{
//				symbolNode.accept(this);
//			}
//		}
//		else
//		{
		
		PropertyElement property = this._indexHelper.getGlobal(this._index, name, EnumSet.noneOf(FieldSelector.class));

		if (property != null)
		{
			this.addType("Window");
			this.setProperty(name);
		}
		
//		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSInvokeNode)
	 */
	@Override
	public void visit(JSInvokeNode node)
	{
		IParseNode expression = node.getExpression();
		
		if (expression instanceof JSNode)
		{
			JSReferenceWalker walker = new JSReferenceWalker(this._scope, this._index);
			
			((JSNode) expression).accept(walker);
			
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
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSObjectNode)
	 */
	@Override
	public void visit(JSObjectNode node)
	{
		this.setProperty("Object");
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSRegexNode)
	 */
	@Override
	public void visit(JSRegexNode node)
	{
		this.setProperty("RegExp");
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSStringNode)
	 */
	@Override
	public void visit(JSStringNode node)
	{
		this.setProperty("String");
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSTrueNode)
	 */
	@Override
	public void visit(JSTrueNode node)
	{
		this.setProperty("Boolean");
	}
}
