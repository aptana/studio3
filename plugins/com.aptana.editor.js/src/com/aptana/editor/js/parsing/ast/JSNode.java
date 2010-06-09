package com.aptana.editor.js.parsing.ast;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.aptana.editor.js.parsing.IJSParserConstants;
import com.aptana.parsing.ast.ParseBaseNode;

public class JSNode extends ParseBaseNode
{
	protected static final short DEFAULT_TYPE = JSNodeTypes.EMPTY;

	private static Map<Short, String> typeNameMap;

	private short fType;
	private boolean fSemicolonIncluded;

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
		this(DEFAULT_TYPE, 0, 0);
	}

	/**
	 * JSNode
	 * 
	 * @param type
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSNode(short type, int start, int end, JSNode... children)
	{
		super(IJSParserConstants.LANGUAGE);
		fType = type;
		this.start = start;
		this.end = end;
		setChildren(children);
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
		return getType() == other.getType() && getSemicolonIncluded() == other.getSemicolonIncluded() && Arrays.equals(getChildren(), other.getChildren());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseBaseNode#getElementName()
	 */
	@Override
	public String getElementName()
	{
		String result = typeNameMap.get(this.getType());

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
	public short getType()
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
		int hash = getType();
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
		return getType() == JSNodeTypes.EMPTY;
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
