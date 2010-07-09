package com.aptana.editor.js.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
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
import com.aptana.editor.js.parsing.ast.JSNodeTypes;
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
	private JSTypeWalker _typeWalker;

	private List<String> _types;
	private String _property;

	/**
	 * JSReferenceWalker
	 * 
	 * @param scope
	 * @param projectIndex
	 */
	public JSReferenceWalker(Scope<JSNode> scope, Index index)
	{
		this(scope, index, null);
	}
	
	/**
	 * JSReferenceWalker
	 * 
	 * @param scope
	 * @param index
	 * @param nodeTypeCache
	 */
	public JSReferenceWalker(Scope<JSNode> scope, Index index, JSTypeWalker typeWalker)
	{
		this._scope = scope;
		this._index = index;
		this._indexHelper = new JSIndexQueryHelper();
		this._typeWalker = typeWalker;
	}

	/**
	 * createReferenceWalker
	 * 
	 * @return
	 */
	protected JSReferenceWalker createReferenceWalker()
	{
		return new JSReferenceWalker(this._scope, this._index, this._typeWalker);
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
			result = this.createReferenceWalker();
			
			for (String typeName : this._types)
			{
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
		else if (this._typeWalker != null)
		{
			// we might have a user-generated type, so look for it directly
			if (this._typeWalker.hasGeneratedType(this._property))
			{
				result = this.createReferenceWalker();
				result.addType(this._property);
				result.setProperty(propertyName);
			}
		}
		
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.contentassist.IReference#getTypes()
	 */
	@Override
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
		if (typeName != null)
		{
			if (this._types == null)
			{
				this._types = new ArrayList<String>();
			}
			
			this._types.add(typeName); 
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
			JSReferenceWalker walker = this.createReferenceWalker();
			
			((JSNode) lhs).accept(walker);
			
			IReference reference = walker.getPropertyReference(propertyName);
			
			if (reference != null)
			{
				this._types = reference.getTypes();
				this._property = reference.getPropertyName();
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
		if (this._scope != null && this._scope.hasSymbol(name))
		{
			List<JSNode> symbolNodes = this._scope.getSymbol(name);

			for (JSNode symbolNode : symbolNodes)
			{
				if (symbolNode instanceof JSIdentifierNode)
				{
					if (symbolNode.getParent().getNodeType() == JSNodeTypes.PARAMETERS)
					{
						// TODO: look for docs to determine type
						// OR infer type from calls to the function
					}
					else if (symbolNode.getText().equals(name) == false)
					{
						((JSIdentifierNode) symbolNode).accept(this);
					}
				}
				else
				{
					symbolNode.accept(this);
				}
			}
		}
		else
		{
			PropertyElement property = this._indexHelper.getGlobal(this._index, name, EnumSet.noneOf(FieldSelector.class));
	
			if (property != null)
			{
				this.addType("Window");
				this.setProperty(name);
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
		IParseNode expression = node.getExpression();
		
		if (expression instanceof JSNode)
		{
			JSReferenceWalker walker = this.createReferenceWalker();
			
			((JSNode) expression).accept(walker);
			
			String methodName = walker.getPropertyName();
			
			if (methodName != null && methodName.length() > 0)
			{
				for (String typeName : walker.getTypes())
				{
					// try indexes first
					FunctionElement function = this._indexHelper.getTypeMethod(this._index, typeName, methodName, EnumSet.of(FieldSelector.RETURN_TYPES));
					
					// if it's not there, then try the generated types
					if (function == null && this._typeWalker != null)
					{
						TypeElement type = this._typeWalker.getGeneratedType(typeName);
						
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
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSObjectNode)
	 */
	@Override
	public void visit(JSObjectNode node)
	{
		String name = "Object";
		
		if (this._typeWalker != null)
		{
			String candidate = this._typeWalker.getNodeType(node);
			
			if (candidate != null)
			{
				name = candidate;
			}
		}
		
		this.setProperty(name);
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
