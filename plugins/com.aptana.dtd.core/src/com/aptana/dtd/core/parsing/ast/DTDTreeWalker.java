/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.dtd.core.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class DTDTreeWalker
{
	public void visit(DTDAndExpressionNode node)
	{
		this.visitChildren(node);
	}

	public void visit(DTDAnyNode node)
	{
		this.visitChildren(node);
	}

	public void visit(DTDAttListDeclNode node)
	{
		this.visitChildren(node);
	}

	public void visit(DTDAttributeNode node)
	{
		this.visitChildren(node);
	}

	public void visit(DTDElementDeclNode node)
	{
		this.visitChildren(node);
	}

	public void visit(DTDElementNode node)
	{
		this.visitChildren(node);
	}

	public void visit(DTDEmptyNode node)
	{
		this.visitChildren(node);
	}

	public void visit(DTDEnumerationTypeNode node)
	{
		this.visitChildren(node);
	}

	public void visit(DTDGeneralEntityDeclNode node)
	{
		this.visitChildren(node);
	}

	public void visit(DTDIgnoreSectionNode node)
	{
		this.visitChildren(node);
	}

	public void visit(DTDIncludeSectionNode node)
	{
		this.visitChildren(node);
	}

	public void visit(DTDNDataDeclNode node)
	{
		this.visitChildren(node);
	}

	public void visit(DTDNotationDeclNode node)
	{
		this.visitChildren(node);
	}

	public void visit(DTDNotationTypeNode node)
	{
		this.visitChildren(node);
	}

	public void visit(DTDOneOrMoreNode node)
	{
		this.visitChildren(node);
	}

	public void visit(DTDOptionalNode node)
	{
		this.visitChildren(node);
	}

	public void visit(DTDOrExpressionNode node)
	{
		this.visitChildren(node);
	}

	public void visit(DTDParsedEntityDeclNode node)
	{
		this.visitChildren(node);
	}

	public void visit(DTDParseRootNode node)
	{
		for (IParseNode child : node)
		{
			if (child instanceof DTDNode)
			{
				((DTDNode) child).accept(this);
			}
		}
	}

	public void visit(DTDPCDataNode node)
	{
		this.visitChildren(node);
	}

	public void visit(DTDProcessingInstructionNode node)
	{
		this.visitChildren(node);
	}

	public void visit(DTDTypeNode node)
	{
		this.visitChildren(node);
	}

	public void visit(DTDZeroOrMoreNode node)
	{
		this.visitChildren(node);
	}

	/**
	 * visitChildren
	 * 
	 * @param node
	 */
	protected void visitChildren(DTDNode node)
	{
		for (IParseNode child : node)
		{
			if (child instanceof DTDNode)
			{
				((DTDNode) child).accept(this);
			}
		}
	}
}
