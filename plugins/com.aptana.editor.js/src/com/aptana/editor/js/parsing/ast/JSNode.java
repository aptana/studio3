package com.aptana.editor.js.parsing.ast;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.aptana.editor.js.parsing.IJSParserConstants;
import com.aptana.editor.js.sdoc.model.DocumentationBlock;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseNode;
import com.aptana.parsing.ast.ParseRootNode;

public class JSNode extends ParseNode
{
	protected static final short DEFAULT_TYPE = JSNodeTypes.EMPTY;
	private static Map<Short, String> typeNameMap;

	private short fType;
	private boolean fSemicolonIncluded;

	private DocumentationBlock fDocumentation;

	/**
	 * static initializer
	 */
	static
	{
		typeNameMap = new HashMap<Short, String>();

		Class<?> klass = JSNodeTypes.class;

		for (Field field : klass.getFields())
		{
			String name = field.getName().toLowerCase();

			try
			{
				Short value = field.getShort(klass);

				typeNameMap.put(value, name);
			}
			catch (IllegalArgumentException e)
			{
			}
			catch (IllegalAccessException e)
			{
			}
		}
	}

	/**
	 * JSNode
	 */
	public JSNode()
	{
		this(DEFAULT_TYPE);
	}

	/**
	 * JSNode
	 * 
	 * @param type
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSNode(short type, JSNode... children)
	{
		super(IJSParserConstants.LANGUAGE);
		
		// set node type
		this.fType = type;
		
		// store children
		this.setChildren(children);
	}

	/**
	 * accept
	 * 
	 * @param walker
	 */
	public void accept(JSTreeWalker walker)
	{
		// sub-classes must override this method so their types will be
		// recognized properly
	}
	
	/**
	 * appendSemicolon
	 * 
	 * @param buffer
	 */
	protected void appendSemicolon(StringBuilder buffer)
	{
		if (getSemicolonIncluded())
		{
			buffer.append(";");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseBaseNode#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof JSNode))
		{
			return false;
		}
		JSNode other = (JSNode) obj;
		return getNodeType() == other.getNodeType() && getSemicolonIncluded() == other.getSemicolonIncluded()
			&& Arrays.equals(getChildren(), other.getChildren());
	}

	/**
	 * getContainingStatementNode
	 * 
	 * @return
	 */
	public IParseNode getContainingStatementNode()
	{
		// move up to nearest statement
		IParseNode result = this;
		IParseNode parent = result.getParent();

		while (parent != null)
		{
			if (parent instanceof ParseRootNode || parent.getNodeType() == JSNodeTypes.STATEMENTS)
			{
				break;
			}
			else
			{
				result = parent;
				parent = parent.getParent();
			}
		}

		return result;
	}

	/**
	 * getDocumentation
	 * 
	 * @return
	 */
	public DocumentationBlock getDocumentation()
	{
		return this.fDocumentation;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseBaseNode#getElementName()
	 */
	@Override
	public String getElementName()
	{
		String result = typeNameMap.get(this.getNodeType());

		return (result == null) ? super.getElementName() : result;
	}

	/**
	 * getSemicolonIncluded
	 * 
	 * @return
	 */
	public boolean getSemicolonIncluded()
	{
		return fSemicolonIncluded;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseBaseNode#getType()
	 */
	public short getNodeType()
	{
		return fType;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseBaseNode#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = getNodeType();
		hash = 31 * hash + (getSemicolonIncluded() ? 1 : 0);
		hash = 31 * hash + Arrays.hashCode(getChildren());
		return hash;
	}

	/**
	 * isEmpty
	 * 
	 * @return
	 */
	public boolean isEmpty()
	{
		return getNodeType() == JSNodeTypes.EMPTY;
	}

	/**
	 * setDocumentation
	 * 
	 * @param block
	 */
	public void setDocumentation(DocumentationBlock block)
	{
		fDocumentation = block;
	}

	/**
	 * setSemicolonIncluded
	 * 
	 * @param included
	 */
	public void setSemicolonIncluded(boolean included)
	{
		fSemicolonIncluded = included;
	}

	/**
	 * setType
	 * 
	 * @param type
	 */
	protected void setType(short type)
	{
		fType = type;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseBaseNode#toString()
	 */
	@Override
	public String toString()
	{
		if (this.getSemicolonIncluded())
		{
			return super.toString() + ";";
		}
		else
		{
			return super.toString();
		}
	}
}
