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
	 * getChildIndex
	 * 
	 * @return
	 */
	public int getChildIndex();

	/**
	 * getChildren
	 * 
	 * @return
	 */
	public IParseNode[] getChildren();

	/**
	 * getChildrenCount
	 * 
	 * @return
	 */
	public int getChildrenCount();

	/**
	 * getElementName
	 * 
	 * @return
	 */
	public String getElementName();

	/**
	 * getIndex
	 * 
	 * @param child
	 * @return
	 */
	public int getIndex(IParseNode child);

	/**
	 * getLanguage
	 */
	public String getLanguage();

	/**
	 * getNameNode
	 * 
	 * @return
	 */
	public INameNode getNameNode();

	/**
	 * getNodeAt
	 * 
	 * @param offset
	 * @return
	 */
	public IParseNode getNodeAt(int offset);

	/**
	 * getParent
	 * 
	 * @return
	 */
	public IParseNode getParent();

	/**
	 * getType
	 * 
	 * @return
	 */
	public short getType();

	/**
	 * Set a child at a given index, replacing any existing child.
	 * 
	 * @param index
	 * @param child
	 * @throws IndexOutOfBoundsException
	 */
	void setChildAt(int index, IParseNode child) throws IndexOutOfBoundsException;
}
