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
package com.aptana.parsing.ast;

import com.aptana.parsing.lexer.ILexeme;

public interface IParseNode extends ILexeme, ILanguageNode, Iterable<IParseNode>
{
	/**
	 * addChild
	 * 
	 * @param child
	 */
	public void addChild(IParseNode child);

	/**
	 * getAttributes
	 * 
	 * @return
	 */
	public IParseNodeAttribute[] getAttributes();

	/**
	 * getChild
	 * 
	 * @param index
	 * @return
	 */
	public IParseNode getChild(int index);

	/**
	 * getChildCount
	 * 
	 * @return
	 */
	public int getChildCount();

	/**
	 * getChildIndex
	 * 
	 * @param child
	 * @return
	 */
	public int getChildIndex(IParseNode child);

	/**
	 * getChildren
	 * 
	 * @return
	 */
	public IParseNode[] getChildren();

	/**
	 * getElementName
	 * 
	 * @return
	 */
	public String getElementName();

	/**
	 * getFirstChild
	 * 
	 * @return
	 */
	public IParseNode getFirstChild();

	/**
	 * getIndex
	 * 
	 * @return
	 */
	public int getIndex();

	/**
	 * getLanguage
	 */
	public String getLanguage();

	/**
	 * getLastChild
	 * 
	 * @return
	 */
	public IParseNode getLastChild();

	/**
	 * getNameNode
	 * 
	 * @return
	 */
	public INameNode getNameNode();

	/**
	 * getNextNode
	 * 
	 * @return
	 */
	public IParseNode getNextNode();

	/**
	 * getNextSibling
	 * 
	 * @return
	 */
	public IParseNode getNextSibling();

	/**
	 * getNodeAt
	 * 
	 * @param offset
	 * @return
	 */
	public IParseNode getNodeAtOffset(int offset);

	/**
	 * getNodeType
	 * 
	 * @return
	 */
	public short getNodeType();

	/**
	 * getParent
	 * 
	 * @return
	 */
	public IParseNode getParent();

	/**
	 * getPreviousNode
	 * 
	 * @return
	 */
	public IParseNode getPreviousNode();

	/**
	 * getPreviousSibling
	 * 
	 * @return
	 */
	public IParseNode getPreviousSibling();

	/**
	 * getRootNode
	 * 
	 * @return
	 */
	public IParseNode getRootNode();

	/**
	 * hasChildren
	 * 
	 * @return
	 */
	public boolean hasChildren();

	/**
	 * Set a child at a given index, replacing any existing child.
	 * 
	 * @param index
	 * @param child
	 * @throws IndexOutOfBoundsException
	 */
	void replaceChild(int index, IParseNode child) throws IndexOutOfBoundsException;
}
