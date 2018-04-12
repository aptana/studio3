/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class CSSTreeWalker
{
	public void visit(CSSAtRuleNode node)
	{
		this.visitChildren(node);
	}

	public void visit(CSSAttributeSelectorNode node)
	{
		this.visitChildren(node);
	}

	public void visit(CSSCharSetNode node)
	{
		this.visitChildren(node);
	}

	public void visit(CSSCommentNode node)
	{
		this.visitChildren(node);
	}

	public void visit(CSSDeclarationNode node)
	{
		this.visitChildren(node);
	}

	public void visit(CSSErrorDeclarationNode node)
	{
		this.visitChildren(node);
	}

	public void visit(CSSExpressionNode node)
	{
		this.visitChildren(node);
	}

	public void visit(CSSFontFaceNode node)
	{
		this.visitChildren(node);
	}

	public void visit(CSSFunctionNode node)
	{
		this.visitChildren(node);
	}

	public void visit(CSSImportNode node)
	{
		this.visitChildren(node);
	}

	public void visit(CSSMediaNode node)
	{
		this.visitChildren(node);
	}

	public void visit(CSSNamespaceNode node)
	{
		this.visitChildren(node);
	}

	public void visit(CSSPageNode node)
	{
		this.visitChildren(node);
	}

	public void visit(CSSPageSelectorNode node)
	{
		this.visitChildren(node);
	}

	public void visit(CSSParseRootNode node)
	{
		for (IParseNode child : node)
		{
			if (child instanceof CSSNode)
			{
				((CSSNode) child).accept(this);
			}
		}
	}
	
	public void visit(CSSRuleNode node)
	{
		this.visitChildren(node);
	}

	public void visit(CSSSelectorNode node)
	{
		this.visitChildren(node);
	}

	public void visit(CSSSimpleSelectorNode node)
	{
		this.visitChildren(node);
	}

	public void visit(CSSTermListNode node)
	{
		this.visitChildren(node);
	}

	public void visit(CSSTermNode node)
	{
		this.visitChildren(node);
	}

	public void visit(CSSTextNode node)
	{
		this.visitChildren(node);
	}

	protected void visitChildren(CSSNode node)
	{
		for (IParseNode child : node)
		{
			if (child instanceof CSSNode)
			{
				((CSSNode) child).accept(this);
			}
		}
	}
}
