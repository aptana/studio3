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
	 * getFirstChild
	 * 
	 * @return
	 */
	public IParseNode getFirstChild();

	/**
	 * getFollowingNode
	 * 
	 * @return
	 */
	public IParseNode getFollowingNode();

	/**
	 * getFollowingSibling
	 * 
	 * @return
	 */
	public IParseNode getFollowingSibling();

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
	 * getPrecedingNode
	 * 
	 * @return
	 */
	public IParseNode getPrecedingNode();

	/**
	 * getPrecedingSibling
	 * 
	 * @return
	 */
	public IParseNode getPrecedingSibling();

	/**
	 * getType
	 * 
	 * @return
	 */
	public short getType();

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
	void setChildAt(int index, IParseNode child) throws IndexOutOfBoundsException;
}
