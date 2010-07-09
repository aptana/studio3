package com.aptana.editor.js.contentassist;

import com.aptana.editor.js.parsing.ast.JSAssignmentNode;
import com.aptana.editor.js.parsing.ast.JSCatchNode;
import com.aptana.editor.js.parsing.ast.JSDeclarationNode;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSGetPropertyNode;
import com.aptana.editor.js.parsing.ast.JSLabelledNode;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSParseRootNode;
import com.aptana.editor.js.parsing.ast.JSTreeWalker;
import com.aptana.editor.js.parsing.ast.JSWithNode;
import com.aptana.parsing.Scope;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.IRange;

public class JSSymbolCollector extends JSTreeWalker
{
	private Scope<JSNode> fScope;

	/**
	 * JSSymbolCollector
	 */
	public JSSymbolCollector()
	{
		fScope = new Scope<JSNode>();
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
	 * addAssignment
	 * 
	 * @param assignment
	 */
	protected void addAssignment(JSAssignmentNode assignment)
	{
		if (fScope != null)
		{
			fScope.addAssignment(assignment);
		}
	}
	
	/**
	 * addSymbol
	 * 
	 * @param name
	 * @param value
	 */
	protected void addSymbol(String name, JSNode value)
	{
		if (fScope != null)
		{
			fScope.addSymbol(name, value);
		}
	}

	/**
	 * getScope
	 * 
	 * @return Scope<JSNode>
	 */
	public Scope<JSNode> getScope()
	{
		return fScope;
	}

	/**
	 * popScope
	 */
	protected void popScope()
	{
		if (fScope != null)
		{
			fScope = fScope.getParentScope();
		}
	}

	/**
	 * pushScope
	 */
	protected void pushScope()
	{
		Scope<JSNode> childScope = new Scope<JSNode>();

		if (fScope != null)
		{
			fScope.addScope(childScope);
		}

		fScope = childScope;
	}
	
	/**
	 * setScopeRange
	 *
	 * @param range
	 */
	protected void setScopeRange(IRange range)
	{
		if (fScope != null)
		{
			fScope.setRange(range);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSAssignmentNode)
	 */
	@Override
	public void visit(JSAssignmentNode node)
	{
		this.addAssignment(node);
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
			this.addSymbol(name, (JSNode) value);
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
			this.addSymbol(name, node);
		}
		
		// create a new scope
		this.pushScope();
		
		// add parameters
		for (IParseNode parameter : node.getParameters())
		{
			if (parameter instanceof JSNode)
			{
				this.addSymbol(parameter.getText(), (JSNode) parameter);
			}
		}
		
		// process body
		IParseNode body = node.getBody();
		
		this.accept(body);
		
		// set scope range
		this.setScopeRange(body);
		
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
		for (IParseNode child : node)
		{
			this.accept(child);
		}
		
		this.setScopeRange(node);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSWithNode)
	 */
	@Override
	public void visit(JSWithNode node)
	{
		// TODO: This does "interesting" things to the current scope. We need to make sure we understand all cases before implementing this
	}
}
