package com.aptana.parsing.ast;

import com.aptana.parsing.lexer.ILexeme;

public interface IParseNode extends ILexeme, ILanguageNode
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
}
