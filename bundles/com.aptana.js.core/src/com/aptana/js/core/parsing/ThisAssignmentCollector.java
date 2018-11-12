/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing;

import java.util.ArrayList;
import java.util.List;

import com.aptana.js.core.parsing.ast.IJSNodeTypes;
import com.aptana.js.core.parsing.ast.JSAssignmentNode;
import com.aptana.js.core.parsing.ast.JSFunctionNode;
import com.aptana.js.core.parsing.ast.JSTreeWalker;
import com.aptana.parsing.ast.IParseNode;
import com.oracle.js.parser.TokenType;

/**
 * ThisAssignmentCollector
 */
public class ThisAssignmentCollector extends JSTreeWalker
{
	private List<JSAssignmentNode> assignments = new ArrayList<JSAssignmentNode>();

	/**
	 * getAssignments
	 * 
	 * @return
	 */
	public List<JSAssignmentNode> getAssignments()
	{
		return assignments;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSAssignmentNode)
	 */
	@Override
	public void visit(JSAssignmentNode node)
	{
		IParseNode lhs = node.getLeftHandSide();

		if (lhs.getNodeType() == IJSNodeTypes.GET_PROPERTY && (lhs.getFirstChild().getNodeType() == IJSNodeTypes.THIS || lhs.getFirstChild().getText().equals(TokenType.THIS.getName()))) //TODO: create JSThisNode directly while building AST
		{
			assignments.add(node);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSFunctionNode)
	 */
	@Override
	public void visit(JSFunctionNode node)
	{
		// don't descend into nested functions
	}
}
