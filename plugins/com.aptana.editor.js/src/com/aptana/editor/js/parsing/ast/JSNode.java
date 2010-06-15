package com.aptana.editor.js.parsing.ast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aptana.editor.js.parsing.IJSParserConstants;
import com.aptana.editor.js.sdoc.model.DocumentationBlock;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseBaseNode;
import com.aptana.parsing.ast.ParseRootNode;

public class JSNode extends ParseBaseNode
{
	protected static final short DEFAULT_TYPE = JSNodeTypes.EMPTY;
	private static Map<Short, String> typeNameMap;

	private short fType;
	private boolean fSemicolonIncluded;
	
	private DocumentationBlock fDocumentationBlock;
	private List<String> fReturnTypes;

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
	 * addReturnTypes
	 * 
	 * @param types
	 */
	protected void addReturnTypes(List<String> types)
	{
		// do nothing, sub-classes should override
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
			if (parent instanceof ParseRootNode || parent.getType() == JSNodeTypes.STATEMENTS)
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
	 * getDocumentationBlock
	 * 
	 * @return
	 */
	public DocumentationBlock getDocumentationBlock()
	{
		if (this.fDocumentationBlock == null)
		{
			// look up block
		}
		
		return this.fDocumentationBlock;
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
	 * getReturnTypes
	 * 
	 * @return
	 */
	public List<String> getReturnTypes()
	{
		if (fReturnTypes == null)
		{
			fReturnTypes = new ArrayList<String>();

			addReturnTypes(fReturnTypes);
		}

		return fReturnTypes;
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
