/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

import com.aptana.parsing.ast.IParseNode;

public class JSSwitchNode extends JSNode
{
	private Symbol _leftParenthesis;
	private Symbol _rightParenthesis;
	private Symbol _leftBrace;
	private Symbol _rightBrace;

	/**
	 * JSSwitchNode
	 * 
	 * @param leftParenthesis
	 * @param expression
	 * @param rightParenthesis
	 * @param leftBrace
	 * @param rightBrace
	 * @param children
	 */
	public JSSwitchNode(Symbol leftParenthesis, JSNode expression, Symbol rightParenthesis, Symbol leftBrace, Symbol rightBrace, JSNode... children)
	{
		super(JSNodeTypes.SWITCH, expression);
		
		for (JSNode child : children)
		{
			this.addChild(child);
		}

		this._leftParenthesis = leftParenthesis;
		this._rightParenthesis = rightParenthesis;
		this._leftBrace = leftBrace;
		this._rightBrace = rightBrace;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#accept(com.aptana.editor.js.parsing.ast.JSTreeWalker)
	 */
	@Override
	public void accept(JSTreeWalker walker)
	{
		walker.visit(this);
	}

	/**
	 * getExpression
	 * 
	 * @return
	 */
	public IParseNode getExpression()
	{
		return this.getChild(0);
	}

	/**
	 * getLeftBrace
	 * 
	 * @return
	 */
	public Symbol getLeftBrace()
	{
		return this._leftBrace;
	}

	/**
	 * getLeftParenthesis
	 * 
	 * @return
	 */
	public Symbol getLeftParenthesis()
	{
		return this._leftParenthesis;
	}

	/**
	 * getRightBrace
	 * 
	 * @return
	 */
	public Symbol getRightBrace()
	{
		return this._rightBrace;
	}

	/**
	 * getRightParenthesis
	 * 
	 * @return
	 */
	public Symbol getRightParenthesis()
	{
		return this._rightParenthesis;
	}
}
