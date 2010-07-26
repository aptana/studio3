package com.aptana.editor.js.contentassist;

import com.aptana.editor.js.parsing.ast.JSAssignmentNode;
import com.aptana.editor.js.parsing.ast.JSCatchNode;
import com.aptana.editor.js.parsing.ast.JSDeclarationNode;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSGetPropertyNode;
import com.aptana.editor.js.parsing.ast.JSLabelledNode;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSNodeTypes;
import com.aptana.editor.js.parsing.ast.JSObjectNode;
import com.aptana.editor.js.parsing.ast.JSParseRootNode;
import com.aptana.editor.js.parsing.ast.JSTreeWalker;
import com.aptana.editor.js.parsing.ast.JSWithNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.IRange;

public class JSSymbolCollector extends JSTreeWalker
{
	private JSScope _scope;

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
		if (name != null && name.length() > 0 && value != null)
		{
			JSObject object = this._scope.getObject();
			JSObject property;
			
			if (object.hasProperty(name))
			{
				// use the currently existing property
				property = object.getProperty(name);
			}
			else
			{
				// create a new property
				property = new JSObject();
				
				// add it to the current object
				object.setProperty(name, property);
			}
			
			if (value instanceof JSObjectNode)
			{
				JSPropertyCollector collector = new JSPropertyCollector(property);
				collector.visit((JSObjectNode) value);
			}
			
			property.addValue(value);
		}
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
			case JSNodeTypes.IDENTIFIER:
				this.addPropertyValue(lhs.getText(), node);
				break;

			default:
				LOOP: while (lhs != null)
				{
					switch (lhs.getNodeType())
					{
						case JSNodeTypes.IDENTIFIER:
							String name = lhs.getText();

							if (this._scope.hasSymbol(name))
							{
								JSPropertyCollector collector = new JSPropertyCollector(this._scope.getObject());
								collector.visit(node);
							}
							// else secondary assignment without declared symbol
							break LOOP;

						case JSNodeTypes.THIS:
							// TODO: implement this once we're properly handling
							// [[proto]]
							System.out.println("unprocessed assignment: " + node);
							break LOOP;

						default:
							lhs = lhs.getFirstChild();
							break;
					}
				}

				if (lhs == null)
				{
					System.out.println("unprocessed assignment: " + node);
				}
				break;
		}
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSFunctionNode)
	 */
	@Override
	public void visit(JSFunctionNode node)
	{
		// add symbol if this has a name
		String name = node.getName().getText();

		if (name != null && name.length() > 0)
		{
			this.addPropertyValue(name, node);
		}

		// create a new scope and set its range
		IParseNode body = node.getBody();
		this.pushScope();
		this.setScopeRange(body);

		// add parameters
		for (IParseNode parameter : node.getParameters())
		{
			if (parameter instanceof JSNode)
			{
				this.addPropertyValue(parameter.getText(), (JSNode) parameter);
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
