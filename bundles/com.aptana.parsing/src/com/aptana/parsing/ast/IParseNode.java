/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
	 * @return true if this node should not appear in the outline, false otherwise
	 */
	public boolean isFilteredFromOutline();

	/**
	 * Set a child at a given index, replacing any existing child.
	 * 
	 * @param index
	 * @param child
	 * @throws IndexOutOfBoundsException
	 */
	void replaceChild(int index, IParseNode child) throws IndexOutOfBoundsException;
}
